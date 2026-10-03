package com.wu.todo.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.wu.todo.data.KanbanParser
import com.wu.todo.data.KanbanRepository
import com.wu.todo.data.KanbanSection
import com.wu.todo.data.KanbanTask

data class FolderFile(val name: String, val uri: Uri)

data class BoardUiState(
    val loading: Boolean = false,
    val fileName: String? = null,
    val fileUri: Uri? = null,
    val sections: List<KanbanSection> = emptyList(),
    val lines: List<String> = emptyList(),
    val lineSeparator: String = "\n",
    val error: String? = null,
    val message: String? = null,
    val readOnly: Boolean = false,
    /** 已置顶（pin）的看板列标题集合，按文件 uri 持久化在本地 */
    val pinnedTitles: Set<String> = emptySet(),
    /** 列标题 → 圆点颜色（ARGB Int），仅存本地 */
    val sectionColors: Map<String, Int> = emptyMap(),
    /** 左侧栏展示的文件夹内所有 .md 文件 */
    val drawerFiles: List<FolderFile> = emptyList()
) {
    val totalTasks get() = sections.sumOf { it.total }
    val doneTasks get() = sections.sumOf { it.doneCount }
}

class BoardViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = KanbanRepository(app)
    private val prefs: SharedPreferences = app.getSharedPreferences("wu_todo", Context.MODE_PRIVATE)

    /** 对外暴露为 MutableState，Compose 里用 `by` 委托即可观察 */
    val state: MutableState<BoardUiState> = mutableStateOf(BoardUiState())

    private val FOLDER_PREFS_KEY = "board_paths"   // 已添加的「看板路径」（文件夹树）集合
    private val FILE_PREFS_KEY = "extra_files"      // 单独添加的 .md 文件集合（name|uri）

    // 已添加的看板路径（文件夹树 URI）与单独添加的 md 文件，二者共同决定左侧栏文件列表
    private var savedFolders: MutableSet<String> =
        (prefs.getStringSet(FOLDER_PREFS_KEY, emptySet()) ?: emptySet()).toMutableSet()
    private var savedFiles: MutableList<FolderFile> = run {
        (prefs.getStringSet(FILE_PREFS_KEY, emptySet()) ?: emptySet())
            .mapNotNull { entry ->
                val i = entry.indexOf('|')
                if (i <= 0) null else FolderFile(entry.substring(0, i), Uri.parse(entry.substring(i + 1)))
            }.toMutableList()
    }

    init {
        // 恢复已添加的看板路径与独立 md 文件，重填左侧抽屉文件列表；再恢复上次打开的具体文件
        rebuildDrawer()
        val fileSaved = prefs.getString("last_uri", null)
        if (fileSaved != null) {
            runCatching { openFile(Uri.parse(fileSaved)) }
        }
    }

    /** 根据「看板路径 + 独立 md 文件」重算左侧栏文件列表（按 uri 去重、按名称排序） */
    private fun rebuildDrawer() {
        val files = mutableListOf<FolderFile>()
        for (folder in savedFolders) {
            runCatching { repo.listMarkdownInTree(Uri.parse(folder)) }
                .getOrDefault(emptyList())
                .forEach { (name, uri) -> files.add(FolderFile(name, uri)) }
        }
        files.addAll(savedFiles)
        val deduped = files.distinctBy { it.uri.toString() }.sortedBy { it.name.lowercase() }
        update { copy(drawerFiles = deduped) }
    }

    /** 打开单个 .md 文件（设为当前看板），并把它登记进左侧栏 */
    fun openFile(uri: Uri) {
        takePersistable(uri)
        addExtraFile(uri)   // 打开过的文件也出现在左侧栏
        loadFrom(uri)
    }

    /** 添加「看板路径」：扫描该文件夹树下的全部 .md 文件并加入左侧栏（不改当前打开的看板） */
    fun addBoardPath(uri: Uri) {
        takePersistable(uri)
        savedFolders.add(uri.toString())
        prefs.edit().putStringSet(FOLDER_PREFS_KEY, savedFolders.toSet()).apply()
        rebuildDrawer()
        // 若当前还没有打开任何看板，则自动打开该路径下的第一个文件
        if (state.value.fileUri == null) {
            runCatching { repo.listMarkdownInTree(uri) }
                .getOrDefault(emptyList()).firstOrNull()?.second?.let { openFile(it) }
        }
    }

    /** 添加单个 .md 文件到左侧栏（不切换当前看板） */
    fun addMdFile(uri: Uri) {
        takePersistable(uri)
        addExtraFile(uri)
        rebuildDrawer()
    }

    /** 把某个 md 文件登记进「独立文件」集合并持久化（去重、置顶） */
    private fun addExtraFile(uri: Uri) {
        val name = repo.displayName(uri) ?: uri.lastPathSegment ?: "看板"
        savedFiles.removeAll { it.uri == uri }
        savedFiles.add(0, FolderFile(name, uri))
        prefs.edit().putStringSet(
            FILE_PREFS_KEY,
            savedFiles.map { "${it.name}|${it.uri}" }.toSet()
        ).apply()
    }

    fun chooseFolderFile(uri: Uri) {
        openFile(uri)
    }

    fun reload() {
        state.value.fileUri?.let { loadFrom(it) }
    }

    fun toggle(task: KanbanTask) {
        val cur = state.value
        val uri = cur.fileUri ?: return
        val newLines = KanbanParser.toggle(cur.lines, task)
        val board = KanbanParser.parse(newLines.joinToString(cur.lineSeparator), cur.fileName ?: "")
        update { copy(lines = newLines, sections = board.sections) }

        if (cur.readOnly) return
        persist(uri, newLines.joinToString(cur.lineSeparator))
    }

    /** 删除某条任务（从 .md 中移除对应行） */
    fun delete(task: KanbanTask) {
        val cur = state.value
        val uri = cur.fileUri ?: return
        val newLines = KanbanParser.removeTask(cur.lines, task)
        val board = KanbanParser.parse(newLines.joinToString(cur.lineSeparator), cur.fileName ?: "")
        update { copy(lines = newLines, sections = board.sections) }

        if (cur.readOnly) return
        persist(uri, newLines.joinToString(cur.lineSeparator))
    }

    /** 切换某个看板列的置顶状态（仅存本地，不写回 .md） */
    fun togglePin(section: KanbanSection) {
        val cur = state.value
        val uri = cur.fileUri ?: return
        val key = pinnedPrefsKey(uri)
        val newSet = (prefs.getStringSet(key, emptySet()) ?: emptySet()).toMutableSet()
        if (section.title in newSet) newSet.remove(section.title) else newSet.add(section.title)
        prefs.edit().putStringSet(key, HashSet(newSet)).apply()
        update { copy(pinnedTitles = newSet.toSet()) }
    }

    /** 重命名看板列：修改列头行并写回 .md，同时同步本地置顶记录 */
    fun renameSection(section: KanbanSection, newTitle: String) {
        val cur = state.value
        val uri = cur.fileUri ?: return
        val trimmed = newTitle.trim()
        if (trimmed.isEmpty() || trimmed == section.title) return

        val newLines = KanbanParser.renameSection(cur.lines, section.headerLineIndex, trimmed)
        val board = KanbanParser.parse(newLines.joinToString(cur.lineSeparator), cur.fileName ?: "")
        update { copy(lines = newLines, sections = board.sections) }

        // 同步置顶记录里的旧标题
        val key = pinnedPrefsKey(uri)
        val saved = prefs.getStringSet(key, null)
        if (saved != null && section.title in saved) {
            val newSet = saved.toMutableSet()
            newSet.remove(section.title)
            newSet.add(trimmed)
            prefs.edit().putStringSet(key, HashSet(newSet)).apply()
            update { copy(pinnedTitles = newSet.toSet()) }
        }

        if (cur.readOnly) return
        persist(uri, newLines.joinToString(cur.lineSeparator))
    }

    private fun pinnedPrefsKey(uri: Uri) = "pinned_$uri"

    private fun persist(uri: Uri, text: String) {
        runCatching { repo.writeText(uri, text) }
            .onFailure { update { copy(readOnly = true, message = "保存失败，已切换为只读模式") } }
    }

    /** 在指定列末尾添加一个未完成任务并写回 .md */
    fun addTask(section: KanbanSection, text: String) {
        val cur = state.value
        val uri = cur.fileUri ?: return
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        val newLines = KanbanParser.addTask(cur.lines, section.headerLineIndex, trimmed)
        val board = KanbanParser.parse(newLines.joinToString(cur.lineSeparator), cur.fileName ?: "")
        update { copy(lines = newLines, sections = board.sections) }

        if (cur.readOnly) return
        persist(uri, newLines.joinToString(cur.lineSeparator))
    }

    /** 重命名任务文本并写回 .md */
    fun renameTask(task: KanbanTask, newText: String) {
        val cur = state.value
        val uri = cur.fileUri ?: return
        if (newText.isBlank() || newText.trim() == task.text) return

        val newLines = KanbanParser.renameTask(cur.lines, task, newText)
        val board = KanbanParser.parse(newLines.joinToString(cur.lineSeparator), cur.fileName ?: "")
        update { copy(lines = newLines, sections = board.sections) }

        if (cur.readOnly) return
        persist(uri, newLines.joinToString(cur.lineSeparator))
    }

    /** 把任务移动到目标列末尾并写回 .md（目标列与当前列相同则忽略） */
    fun moveTask(task: KanbanTask, target: KanbanSection, from: KanbanSection) {
        val cur = state.value
        val uri = cur.fileUri ?: return
        if (target.headerLineIndex == from.headerLineIndex) return

        val newLines = KanbanParser.moveTask(cur.lines, task, target.headerLineIndex)
        val board = KanbanParser.parse(newLines.joinToString(cur.lineSeparator), cur.fileName ?: "")
        update { copy(lines = newLines, sections = board.sections) }

        if (cur.readOnly) return
        persist(uri, newLines.joinToString(cur.lineSeparator))
    }

    /** 拖动排序：按新的任务行号顺序重排某一列并写回 .md */
    fun reorderTasks(section: KanbanSection, orderedTaskLineIndexes: List<Int>) {
        val cur = state.value
        val uri = cur.fileUri ?: return
        if (orderedTaskLineIndexes.isEmpty()) return

        val newLines = KanbanParser.reorderTasks(cur.lines, section.headerLineIndex, orderedTaskLineIndexes)
        if (newLines == cur.lines) return
        val board = KanbanParser.parse(newLines.joinToString(cur.lineSeparator), cur.fileName ?: "")
        update { copy(lines = newLines, sections = board.sections) }

        if (cur.readOnly) return
        persist(uri, newLines.joinToString(cur.lineSeparator))
    }

    /**
     * 拖动排序（列表模式）：按新的列头行号顺序重排整个看板列并写回 .md。
     * 每列连同其任务/子任务/备注行作为整体移动；首个列头之前的行与设置注释块保持原位。
     */
    fun reorderSections(orderedHeaderLineIndexes: List<Int>) {
        val cur = state.value
        val uri = cur.fileUri ?: return
        if (orderedHeaderLineIndexes.size < 2) return

        val newLines = KanbanParser.reorderSections(cur.lines, orderedHeaderLineIndexes)
        if (newLines == cur.lines) return
        val board = KanbanParser.parse(newLines.joinToString(cur.lineSeparator), cur.fileName ?: "")
        update { copy(lines = newLines, sections = board.sections) }

        if (cur.readOnly) return
        persist(uri, newLines.joinToString(cur.lineSeparator))
    }

    /** 给任务添加一个缩进子任务并写回 .md */
    fun addSubtask(task: KanbanTask, text: String) {
        val cur = state.value
        val uri = cur.fileUri ?: return
        if (text.isBlank()) return

        val newLines = KanbanParser.addSubtask(cur.lines, task, text)
        val board = KanbanParser.parse(newLines.joinToString(cur.lineSeparator), cur.fileName ?: "")
        update { copy(lines = newLines, sections = board.sections) }

        if (cur.readOnly) return
        persist(uri, newLines.joinToString(cur.lineSeparator))
    }

    /** 整体替换某任务的子任务列表（含拖动排序后的新顺序）并写回 .md */
    fun replaceSubtasks(task: KanbanTask, subs: List<Pair<String, Boolean>>) {
        val cur = state.value
        val uri = cur.fileUri ?: return

        val newLines = KanbanParser.replaceSubtasks(cur.lines, task, subs)
        if (newLines == cur.lines) return
        val board = KanbanParser.parse(newLines.joinToString(cur.lineSeparator), cur.fileName ?: "")
        update { copy(lines = newLines, sections = board.sections) }

        if (cur.readOnly) return
        persist(uri, newLines.joinToString(cur.lineSeparator))
    }

    /** 设置任务备注（任务行下方的缩进普通文本行）并写回 .md，texts 为空则清除备注 */
    fun setNotes(task: KanbanTask, texts: List<String>) {
        val cur = state.value
        val uri = cur.fileUri ?: return
        // 仅保留非空项并 trim，与解析后存储的 notes 对齐后再比较，避免无意义的写回
        val cleaned = texts.map { it.trim() }.filter { it.isNotEmpty() }
        if (cleaned == task.notes) return

        val newLines = KanbanParser.setNotes(cur.lines, task, cleaned)
        val board = KanbanParser.parse(newLines.joinToString(cur.lineSeparator), cur.fileName ?: "")
        update { copy(lines = newLines, sections = board.sections) }

        if (cur.readOnly) return
        persist(uri, newLines.joinToString(cur.lineSeparator))
    }

    /** 新建看板列：写回 .md，并把圆点颜色记在本地 */
    fun addSection(title: String, colorArgb: Int) {
        val cur = state.value
        val uri = cur.fileUri ?: return
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return

        val newLines = KanbanParser.addSection(cur.lines, trimmed)
        val board = KanbanParser.parse(newLines.joinToString(cur.lineSeparator), cur.fileName ?: "")
        prefs.edit().putInt(sectionColorPrefsKey(uri, trimmed), colorArgb).apply()
        update {
            copy(
                lines = newLines,
                sections = board.sections,
                sectionColors = sectionColors + (trimmed to colorArgb)
            )
        }

        if (cur.readOnly) return
        persist(uri, newLines.joinToString(cur.lineSeparator))
    }

    /** 设置列的圆点颜色（仅记录在本地偏好，不改动 .md 内容） */
    fun setSectionColor(section: KanbanSection, colorArgb: Int) {
        val cur = state.value
        val uri = cur.fileUri ?: return
        prefs.edit().putInt(sectionColorPrefsKey(uri, section.title), colorArgb).apply()
        update {
            copy(sectionColors = sectionColors + (section.title to colorArgb))
        }
    }

    private fun sectionColorPrefsKey(uri: Uri, title: String) = "sec_color_${uri}_$title"

    /** 删除整个看板列并写回 .md，同时清理该列的置顶与颜色记录 */
    fun deleteSection(section: KanbanSection) {
        val cur = state.value
        val uri = cur.fileUri ?: return

        val newLines = KanbanParser.deleteSection(cur.lines, section.headerLineIndex)
        val board = KanbanParser.parse(newLines.joinToString(cur.lineSeparator), cur.fileName ?: "")

        val key = pinnedPrefsKey(uri)
        val saved = prefs.getStringSet(key, null)
        var pinned = cur.pinnedTitles
        if (saved != null && section.title in saved) {
            val newSet = saved.toMutableSet()
            newSet.remove(section.title)
            prefs.edit().putStringSet(key, HashSet(newSet)).apply()
            pinned = newSet.toSet()
        }
        prefs.edit().remove(sectionColorPrefsKey(uri, section.title)).apply()
        update {
            copy(
                lines = newLines,
                sections = board.sections,
                pinnedTitles = pinned,
                sectionColors = sectionColors - section.title
            )
        }

        if (cur.readOnly) return
        persist(uri, newLines.joinToString(cur.lineSeparator))
    }

    /** 列内全部任务标记为完成/未完成并写回 .md */
    fun setAllTasks(section: KanbanSection, done: Boolean) {
        val cur = state.value
        val uri = cur.fileUri ?: return

        val newLines = KanbanParser.setAllTasksDone(cur.lines, section.headerLineIndex, done)
        val board = KanbanParser.parse(newLines.joinToString(cur.lineSeparator), cur.fileName ?: "")
        update { copy(lines = newLines, sections = board.sections) }

        if (cur.readOnly) return
        persist(uri, newLines.joinToString(cur.lineSeparator))
    }

    /** 删除列内全部已完成任务并写回 .md */
    fun deleteCompletedTasks(section: KanbanSection) {
        val cur = state.value
        val uri = cur.fileUri ?: return

        val newLines = KanbanParser.deleteCompletedTasks(cur.lines, section.headerLineIndex)
        val board = KanbanParser.parse(newLines.joinToString(cur.lineSeparator), cur.fileName ?: "")
        update { copy(lines = newLines, sections = board.sections) }

        if (cur.readOnly) return
        persist(uri, newLines.joinToString(cur.lineSeparator))
    }

    private fun loadSectionColors(uri: Uri): Map<String, Int> {
        val prefix = "sec_color_${uri}_"
        return prefs.all.mapNotNull { (k, v) ->
            if (k.startsWith(prefix) && v is Int) k.removePrefix(prefix) to v else null
        }.toMap()
    }

    fun consumeMessage() = update { copy(message = null) }

    private fun loadFrom(uri: Uri) {
        update { copy(loading = true, error = null) }
        runCatching {
            val raw = repo.readText(uri)
            val board = KanbanParser.parse(raw, repo.displayName(uri) ?: "看板")
            board to uri
        }.onSuccess { (board, u) ->
            prefs.edit().putString("last_uri", u.toString()).apply()
            val pinned = prefs.getStringSet(pinnedPrefsKey(u), emptySet())?.toSet() ?: emptySet()
            val colors = loadSectionColors(u)
            update {
                copy(
                    loading = false,
                    fileName = board.fileName.ifBlank { "看板" },
                    fileUri = u,
                    sections = board.sections,
                    lines = board.lines,
                    lineSeparator = board.lineSeparator,
                    pinnedTitles = pinned,
                    sectionColors = colors,
                    error = null
                )
            }
        }.onFailure { e ->
            update { copy(loading = false, error = "读取失败：${e.message}") }
        }
    }

    private fun takePersistable(uri: Uri) {
        runCatching {
            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            getApplication<Application>().contentResolver
                .takePersistableUriPermission(uri, flags)
        }
    }

    private inline fun update(block: BoardUiState.() -> BoardUiState) {
        state.value = state.value.block()
    }
}
