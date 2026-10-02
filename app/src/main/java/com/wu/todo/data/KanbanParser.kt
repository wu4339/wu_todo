package com.wu.todo.data

/**
 * Obsidian 的 Kanban 插件（list 模式）会生成如下结构的 Markdown：
 *
 * ---
 * kanban-plugin: list
 * ---
 * ## 今日待办
 * - [ ] 任务 A
 * - [x] 任务 B
 * ## 明日待办
 * - [ ] 任务 C
 * %% kanban:settings
 * {"kanban-plugin":"list",...}
 * %%
 *
 * 本解析器只关心「## 看板列」与「- [ ] / - [x] 任务」，其余（frontmatter、%% 注释块、
 * 代码围栏等）全部跳过，并在写回时原样保留。
 */

data class KanbanTask(
    /** 列表内唯一 id，仅用于 Compose 的 key */
    val id: String,
    /** 在原始文本按行拆分后的行号，用于原地切换勾选状态 */
    val lineIndex: Int,
    val text: String,
    val done: Boolean,
    /** 缩进层级（一个 tab 记为 4 级），用于子任务缩进显示 */
    val indent: Int,
    /**
     * 任务备注：任务行下方的缩进行（Obsidian Kanban 卡片 note）。
     * 支持多条备注——文件里每条写作独立的缩进无序列表项（`- 内容`），读取时已剥掉 `- ` 前缀，
     * 故这里存的是纯文本列表，每个元素对应一条备注。
     */
    val notes: List<String> = emptyList()
)

data class KanbanSection(
    val title: String,
    val headerLineIndex: Int,
    val tasks: List<KanbanTask>
) {
    val total get() = tasks.size
    val doneCount get() = tasks.count { it.done }
}

data class ParsedBoard(
    val fileName: String = "",
    val sections: List<KanbanSection>,
    val lines: List<String>,
    val lineSeparator: String
)

private val SECTION_RE = Regex("""^#{1,6}\s+(.*)$""")
private val TASK_RE = Regex("""^(\s*)([-*+]|\d+[.)])\s+\[([ xX])\]\s*(.*)$""")

/** 无序列表项前缀（`- ` / `* ` / `+ `）：备注行的写入格式，读取时按它剥掉前缀 */
private val NOTE_BULLET_RE = Regex("""^[-*+]\s+""")

object KanbanParser {

    fun parse(raw: String, fileName: String = ""): ParsedBoard {
        val separator = if (raw.contains("\r\n")) "\r\n" else "\n"
        val lines = raw.split("\n").map { it.removeSuffix("\r") }

        val sections = mutableListOf<MutableSection>()
        var current: MutableSection? = null

        var frontmatter = false
        var frontmatterStarted = false
        var fence = false
        var comment = false
        var seq = 0
        // 当前备注归属的任务 id（任务行下方连续的缩进普通文本行视为该任务的备注）
        var noteTargetId: String? = null

        lines.forEachIndexed { index, line ->
            val t = line.trim()

            // YAML frontmatter：--- ... ---
            if (!frontmatterStarted && index == 0 && t == "---") {
                frontmatter = true
                return@forEachIndexed
            }
            if (frontmatter) {
                if (t == "---") {
                    frontmatter = false
                    frontmatterStarted = true
                }
                return@forEachIndexed
            }

            // 代码围栏 ``` 或 ~~~ 内的内容保持原样
            if (t.startsWith("```") || t.startsWith("~~~")) {
                fence = !fence
                return@forEachIndexed
            }
            if (fence) return@forEachIndexed

            // Obsidian 注释块 %% ... %%
            if (t.startsWith("%%") && !(t.length > 2 && t.endsWith("%%"))) {
                comment = true
                return@forEachIndexed
            }
            if (comment) {
                if (t.endsWith("%%")) comment = false
                return@forEachIndexed
            }
            if (t.startsWith("%%") && t.endsWith("%%") && t.length > 2) {
                return@forEachIndexed
            }

            // 看板列标题：## 标题
            val sec = SECTION_RE.find(t)
            if (sec != null) {
                val title = sec.groupValues[1].trim()
                val newSection = MutableSection(title, index)
                current = newSection
                sections += newSection
                noteTargetId = null
                return@forEachIndexed
            }

            // 任务：- [ ] / - [x] / 1. [ ] 等
            val tk = TASK_RE.find(line)
            if (tk != null) {
                val indentStr = tk.groupValues[1]
                val indent = if ('\t' in indentStr) indentStr.length * 4 else indentStr.length
                val done = tk.groupValues[3].equals("x", ignoreCase = true)
                val text = tk.groupValues[4].trim()
                if (text.isEmpty()) return@forEachIndexed // 忽略空任务

                var section = current
                if (section == null) {
                    section = MutableSection("未分组", -1)
                    current = section
                    sections += section
                }
                val newTask = KanbanTask(
                    id = "t${++seq}",
                    lineIndex = index,
                    text = text,
                    done = done,
                    indent = indent
                )
                section.tasks += newTask
                noteTargetId = newTask.id
                return@forEachIndexed
            }

            // 任务行下方的缩进普通文本行 → 该任务的备注（遇到空行/顶格行结束）。
            // 每条缩进行作为独立的一条备注，支持一个任务挂多条 note。
            if (current != null && noteTargetId != null) {
                val lastTask = current!!.tasks.lastOrNull { it.id == noteTargetId }
                if (lastTask != null && line.isNotBlank() &&
                    (line.startsWith(" ") || line.startsWith("\t"))
                ) {
                    val i = current!!.tasks.indexOf(lastTask)
                    // 备注行两种写法都接受：`- 内容`（无序列表项，写入用的格式）与缩进纯文本（旧格式）。
                    // 读取时统一去掉列表符号，保证 note 文本与写入格式一一对应、往返不叠加前缀
                    val content = t.replace(NOTE_BULLET_RE, "")
                    current!!.tasks[i] = lastTask.copy(notes = lastTask.notes + content)
                    return@forEachIndexed
                }
                noteTargetId = null
            }
        }

        return ParsedBoard(
            fileName = fileName,
            sections = sections.map { it.toSection() },
            lines = lines,
            lineSeparator = separator
        )
    }

    /** 原地切换某个任务的勾选状态，返回新的行集合（其余内容不变） */
    fun toggle(lines: List<String>, task: KanbanTask): List<String> {
        val line = lines.getOrNull(task.lineIndex) ?: return lines
        val m = TASK_RE.find(line) ?: return lines
        val range = m.groups[3]!!.range
        val newChar = if (task.done) " " else "x"
        val newLine = line.substring(0, range.first) + newChar + line.substring(range.last + 1)
        val result = ArrayList(lines)
        result[task.lineIndex] = newLine
        return result
    }

    /** 重命名看板列：替换列头行的标题文本（保留原 # 级别），返回新的行集合 */
    fun renameSection(lines: List<String>, headerLineIndex: Int, newTitle: String): List<String> {
        val idx = headerLineIndex
        if (idx !in lines.indices) return lines
        val t = lines[idx].trim()
        val m = SECTION_RE.find(t) ?: return lines
        if (m.groupValues[1].trim() == newTitle.trim()) return lines
        val hashes = t.takeWhile { it == '#' }
        val result = ArrayList(lines)
        result[idx] = "$hashes $newTitle"
        return result
    }

    /** 在指定列末尾追加一个未完成任务行 `- [ ] 文本`，返回新的行集合 */
    fun addTask(lines: List<String>, headerLineIndex: Int, text: String): List<String> {
        if (text.isBlank()) return lines
        var insertAt = if (headerLineIndex in lines.indices) headerLineIndex + 1 else 0
        var lastTaskEnd = -1
        var i = insertAt
        while (i < lines.size) {
            val t = lines[i].trim()
            // 碰到下一列标题或 Obsidian 设置注释块即认为本列结束
            if (SECTION_RE.find(t) != null || t.startsWith("%%")) break
            if (TASK_RE.find(lines[i]) != null) lastTaskEnd = i
            i++
        }
        val at = if (lastTaskEnd >= 0) lastTaskEnd + 1 else insertAt
        val result = ArrayList(lines)
        result.add(at, "- [ ] ${text.trim()}")
        return result
    }

    /**
     * 复制任务：把该任务块（任务行 + 缩进的备注/子任务行）整块复制一份，插到原块之后。
     * 完成状态、note、子任务一并复制；块尾空行不复制。
     */
    fun duplicateTask(lines: List<String>, task: KanbanTask): List<String> {
        val idx = task.lineIndex
        if (idx !in lines.indices) return lines
        if (TASK_RE.find(lines[idx]) == null) return lines
        val taskIndent = lines[idx].takeWhile { it == ' ' || it == '\t' }
        // 块尾：任务行下方连续的「缩进行 / 空行 / 缩进更深的任务行（子任务）」都属于本块
        var end = idx + 1
        while (end < lines.size) {
            val ln = lines[end]
            if (TASK_RE.find(ln) != null) {
                val ind = ln.takeWhile { it == ' ' || it == '\t' }
                if (ind.length > taskIndent.length) { end++; continue } else break
            }
            if (ln.isBlank() || ln.startsWith(" ") || ln.startsWith("\t")) { end++; continue }
            break
        }
        // 块尾的空行不复制
        var blockEnd = end
        while (blockEnd > idx + 1 && lines[blockEnd - 1].isBlank()) blockEnd--
        val result = ArrayList(lines)
        result.addAll(blockEnd, lines.subList(idx, blockEnd))
        return result
    }

    /** 重命名任务：仅替换任务行中的文本部分（保留勾选状态、缩进与列表标记） */
    fun renameTask(lines: List<String>, task: KanbanTask, newText: String): List<String> {
        val idx = task.lineIndex
        if (idx !in lines.indices) return lines
        if (newText.isBlank()) return lines
        val m = TASK_RE.find(lines[idx]) ?: return lines
        val g = m.groups[4] ?: return lines
        if (m.groupValues[4].trim() == newText.trim()) return lines
        val result = ArrayList(lines)
        result[idx] = lines[idx].substring(0, g.range.first) + newText.trim() +
            lines[idx].substring(g.range.last + 1)
        return result
    }

    /** 把任务移动到目标列末尾（目标列用列头行号标识，-1 表示未分组），返回新的行集合 */
    fun moveTask(lines: List<String>, task: KanbanTask, targetHeaderLineIndex: Int): List<String> {
        val idx = task.lineIndex
        if (idx !in lines.indices) return lines
        if (TASK_RE.find(lines[idx]) == null) return lines
        val movedLine = lines[idx].trimStart() // 移到目标列顶层，去掉原缩进

        val without = ArrayList(lines)
        without.removeAt(idx)
        // 删掉一行后，目标列头的行号需要相应前移
        var header = targetHeaderLineIndex
        if (header > idx) header -= 1
        if (header < 0 || header >= without.size) return without

        var lastTask = -1
        var i = header + 1
        while (i < without.size) {
            val t = without[i].trim()
            if (SECTION_RE.find(t) != null || t.startsWith("%%")) break
            if (TASK_RE.find(without[i]) != null) lastTask = i
            i++
        }
        val at = if (lastTask >= 0) lastTask + 1 else header + 1
        val result = ArrayList(without)
        result.add(at, movedLine)
        return result
    }

    /** 在任务行下方插入一个缩进的子任务行，返回新的行集合 */
    fun addSubtask(lines: List<String>, task: KanbanTask, text: String): List<String> {
        if (text.isBlank()) return lines
        val idx = task.lineIndex
        if (idx !in lines.indices) return lines
        if (TASK_RE.find(lines[idx]) == null) return lines
        val indent = lines[idx].takeWhile { it == ' ' || it == '\t' } + "    "
        val result = ArrayList(lines)
        result.add(idx + 1, "${indent}- [ ] ${text.trim()}")
        return result
    }

    /**
     * 整体替换某任务的子任务列表（含顺序调整）：先删掉父任务下方紧邻的缩进子任务行，
     * 再按 [subs] 的顺序依次写入（第二项为是否已完成）。备注行不受影响。
     */
    fun replaceSubtasks(
        lines: List<String>,
        task: KanbanTask,
        subs: List<Pair<String, Boolean>>
    ): List<String> {
        val idx = task.lineIndex
        if (idx !in lines.indices) return lines
        if (TASK_RE.find(lines[idx]) == null) return lines

        val baseIndent = lines[idx].takeWhile { it == ' ' || it == '\t' }
        val result = ArrayList(lines)
        // 备注行紧贴任务行、排在子任务之前：先跳过它们，子任务始终写在备注之后
        var anchor = idx + 1
        while (anchor < result.size && isNoteLine(result[anchor])) anchor++
        // 删除父任务下方、缩进更深的已有子任务行（备注行保留，且不影响其顺序）
        var i = anchor
        while (i < result.size) {
            val ln = result[i]
            val ind = ln.takeWhile { it == ' ' || it == '\t' }
            // 回到同级/更浅层级 → 本任务的子任务区结束
            if (ind.length <= baseIndent.length) break
            if (TASK_RE.find(ln) != null) result.removeAt(i) else i++
        }
        // 按新顺序写回
        val subIndent = baseIndent + "    "
        val inserts = subs.mapNotNull { (t, done) ->
            t.trim().takeIf { it.isNotEmpty() }
                ?.let { "$subIndent- [${if (done) "x" else " "}] $it" }
        }
        result.addAll(anchor, inserts)
        return result
    }

    /**
     * 重排某一列内的任务顺序（长按拖动排序）。
     *
     * [orderedTaskLineIndexes] 为「按原始行号标识的当前顺序」——即拖动前每个顶层任务所在的行号，
     * 按期望的新顺序排列。每个任务连同其下方的缩进子任务/备注行作为一个整体一起移动；
     * 列头之后、首个任务之前的行与列尾空行保持原位不动。
     */
    fun reorderTasks(
        lines: List<String>,
        headerLineIndex: Int,
        orderedTaskLineIndexes: List<Int>
    ): List<String> {
        if (headerLineIndex !in lines.indices) return lines
        // 找出本列内容区间 [headerLineIndex + 1, end)
        var end = headerLineIndex + 1
        while (end < lines.size) {
            val t = lines[end].trim()
            if (SECTION_RE.find(t) != null || t.startsWith("%%")) break
            end++
        }
        // 列尾空行留在原位，不参与重排
        var regionEnd = end
        while (regionEnd > headerLineIndex + 1 && lines[regionEnd - 1].isBlank()) regionEnd--

        val head = mutableListOf<String>()          // 首个任务行之前的自由行
        val blocks = LinkedHashMap<Int, MutableList<String>>() // 任务行号 -> 该任务块的全部行
        var curTaskLine = -1
        for (i in (headerLineIndex + 1) until regionEnd) {
            if (TASK_RE.find(lines[i]) != null) {
                curTaskLine = i
                blocks[i] = mutableListOf(lines[i])
            } else if (curTaskLine >= 0) {
                blocks[curTaskLine]!!.add(lines[i])  // 子任务/备注/空行归入上一个任务块
            } else {
                head.add(lines[i])
            }
        }
        if (blocks.isEmpty()) return lines
        val order = orderedTaskLineIndexes.filter { blocks.containsKey(it) }
        if (order.size != blocks.size) return lines // 顺序信息不完整，保守不动

        val rebuilt = ArrayList<String>(regionEnd - headerLineIndex - 1)
        rebuilt.addAll(head)
        order.forEach { rebuilt.addAll(blocks[it]!!) }

        val result = ArrayList(lines)
        result.subList(headerLineIndex + 1, regionEnd).clear()
        result.addAll(headerLineIndex + 1, rebuilt)
        return result
    }

    /** 任务行下方的连续备注行判定：有缩进、且不是任务行（`- 内容` 与旧的缩进纯文本都算） */
    private fun isNoteLine(l: String): Boolean =
        l.isNotBlank() && (l.startsWith(" ") || l.startsWith("\t")) && TASK_RE.find(l) == null

    /**
     * 设置任务备注：先清除任务行正下方已有备注行；texts 非空时逐条插入新备注行（每条独立一行）。
     *
     * 写入格式为**缩进的无序列表项**（与子任务同级缩进、排在子任务之前）：
     * ```
     * - [ ] 任务1
     *     - note 一
     *     - note 二
     *     - [ ] 子任务
     * ```
     * 读取时（NOTE_BULLET_RE）会把 `- ` 前缀剥掉，保证多次保存不叠加前缀。
     */
    fun setNotes(lines: List<String>, task: KanbanTask, texts: List<String>): List<String> {
        val idx = task.lineIndex
        if (idx !in lines.indices) return lines
        if (TASK_RE.find(lines[idx]) == null) return lines
        val result = ArrayList(lines)
        var i = idx + 1
        while (i < result.size && isNoteLine(result[i])) {
            result.removeAt(i)
        }
        // 仅保留非空项并 trim，与读取后存储的 notes 对齐
        val kept = texts.map { it.trim() }.filter { it.isNotEmpty() }
        if (kept.isNotEmpty()) {
            // 与子任务同级缩进（任务行缩进 + 4 空格），紧跟任务行、位于子任务之前
            val indent = lines[idx].takeWhile { it == ' ' || it == '\t' } + "    "
            // 每条备注独立写成一个无序列表项
            val noteLines = kept.map { "$indent- $it" }
            result.addAll(idx + 1, noteLines)
        }
        return result
    }

    /** 在文件末尾追加一个新看板列（Obsidian 设置注释块之前），返回新的行集合 */
    fun addSection(lines: List<String>, title: String): List<String> {
        if (title.isBlank()) return lines
        var insertAt = lines.size
        var i = lines.size - 1
        while (i >= 0 && lines[i].trim().isEmpty()) i-- // 跳过末尾空行
        if (i >= 0 && lines[i].trim() == "%%") {
            i-- // 先跳过设置块的结束 %% 标记，再往前找起始 %% 行
            while (i >= 0 && !lines[i].trim().startsWith("%%")) i--
            insertAt = if (i >= 0) i else lines.size
        }
        val result = ArrayList(lines)
        result.addAll(insertAt, listOf("", "## ${title.trim()}"))
        return result
    }

    /** 删除整列（列头行与该列全部内容），返回新的行集合 */
    fun deleteSection(lines: List<String>, headerLineIndex: Int): List<String> {
        if (headerLineIndex !in lines.indices) return lines
        var contentEnd = headerLineIndex
        var i = headerLineIndex + 1
        while (i < lines.size) {
            val t = lines[i].trim()
            if (SECTION_RE.find(t) != null || t.startsWith("%%")) break
            if (t.isNotEmpty()) contentEnd = i
            i++
        }
        val result = ArrayList(lines)
        if (contentEnd + 1 > headerLineIndex) result.subList(headerLineIndex, contentEnd + 1).clear()
        return result
    }

    /** 把列内全部任务标记为完成/未完成，返回新的行集合 */
    fun setAllTasksDone(lines: List<String>, headerLineIndex: Int, done: Boolean): List<String> {
        val target = if (done) "x" else " "
        val result = ArrayList(lines)
        var i = headerLineIndex + 1
        while (i < lines.size) {
            val t = lines[i].trim()
            if (SECTION_RE.find(t) != null || t.startsWith("%%")) break
            val m = TASK_RE.find(lines[i])
            if (m != null) {
                val g = m.groups[3]!!
                val cur = if (g.value.equals("x", ignoreCase = true)) "x" else " "
                if (cur != target) {
                    result[i] = lines[i].substring(0, g.range.first) + target +
                        lines[i].substring(g.range.last + 1)
                }
            }
            i++
        }
        return result
    }

    /** 删除列内全部已完成任务行，返回新的行集合 */
    fun deleteCompletedTasks(lines: List<String>, headerLineIndex: Int): List<String> {
        val toRemove = mutableListOf<Int>()
        var i = headerLineIndex + 1
        while (i < lines.size) {
            val t = lines[i].trim()
            if (SECTION_RE.find(t) != null || t.startsWith("%%")) break
            val m = TASK_RE.find(lines[i])
            if (m != null && m.groups[3]!!.value.equals("x", ignoreCase = true)) toRemove.add(i)
            i++
        }
        if (toRemove.isEmpty()) return lines
        val result = ArrayList(lines)
        for (idx in toRemove.asReversed()) result.removeAt(idx)
        return result
    }

    /** 删除某条任务所在的行（仅当该行仍是任务行时才删），返回新的行集合 */
    fun removeTask(lines: List<String>, task: KanbanTask): List<String> {
        val idx = task.lineIndex
        if (idx !in lines.indices) return lines
        if (TASK_RE.find(lines[idx]) == null) return lines
        val result = ArrayList(lines)
        result.removeAt(idx)
        // 连带删除任务下方的备注行，避免残留
        while (idx < result.size && isNoteLine(result[idx])) {
            result.removeAt(idx)
        }
        return result
    }

    private class MutableSection(
        var title: String,
        var headerLineIndex: Int
    ) {
        val tasks = mutableListOf<KanbanTask>()
        fun toSection() = KanbanSection(title, headerLineIndex, tasks.toList())
    }
}
