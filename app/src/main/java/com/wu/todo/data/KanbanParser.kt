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
    val indent: Int
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
                section.tasks += KanbanTask(
                    id = "t${++seq}",
                    lineIndex = index,
                    text = text,
                    done = done,
                    indent = indent
                )
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

    /** 删除某条任务所在的行（仅当该行仍是任务行时才删），返回新的行集合 */
    fun removeTask(lines: List<String>, task: KanbanTask): List<String> {
        val idx = task.lineIndex
        if (idx !in lines.indices) return lines
        if (TASK_RE.find(lines[idx]) == null) return lines
        val result = ArrayList(lines)
        result.removeAt(idx)
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
