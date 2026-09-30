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
    /** 任务备注：任务行下方的缩进普通文本行（Obsidian Kanban 卡片 note），多行以空格拼接 */
    val note: String = ""
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

            // 任务行下方的缩进普通文本行 → 该任务的备注（遇到空行/顶格行结束）
            if (current != null && noteTargetId != null) {
                val lastTask = current!!.tasks.lastOrNull { it.id == noteTargetId }
                if (lastTask != null && line.isNotBlank() &&
                    (line.startsWith(" ") || line.startsWith("\t"))
                ) {
                    val i = current!!.tasks.indexOf(lastTask)
                    current!!.tasks[i] = lastTask.copy(note = (lastTask.note + " " + t).trim())
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

    /** 任务行下方的连续备注行（缩进非任务非空行）的结束判断 */
    private fun isNoteLine(l: String): Boolean =
        l.isNotBlank() && (l.startsWith(" ") || l.startsWith("\t")) && TASK_RE.find(l) == null

    /** 设置任务备注：先清除任务行下方已有备注行；text 非空时插入新备注行（缩进与任务行一致） */
    fun setNote(lines: List<String>, task: KanbanTask, text: String): List<String> {
        val idx = task.lineIndex
        if (idx !in lines.indices) return lines
        if (TASK_RE.find(lines[idx]) == null) return lines
        val result = ArrayList(lines)
        var i = idx + 1
        while (i < result.size && isNoteLine(result[i])) {
            result.removeAt(i)
        }
        if (text.isNotBlank()) {
            val indent = lines[idx].takeWhile { it == ' ' || it == '\t' }
            result.add(idx + 1, "$indent${text.trim()}")
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
