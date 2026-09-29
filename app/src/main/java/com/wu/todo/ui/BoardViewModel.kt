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

    init {
        prefs.getString("last_uri", null)?.let { saved ->
            runCatching { openFile(Uri.parse(saved)) }
        }
    }

    fun openFile(uri: Uri) {
        takePersistable(uri)
        loadFrom(uri)
    }

    fun openFolder(uri: Uri) {
        takePersistable(uri)
        val files = runCatching { repo.listMarkdownInTree(uri) }.getOrDefault(emptyList())
        if (files.isEmpty()) {
            update { copy(message = "该文件夹下没有找到 .md 文件") }
            return
        }
        // 文件夹内所有 md 文件放入左侧栏；当前文件不在其中时自动打开第一个
        val uris = files.map { it.second }
        update { copy(drawerFiles = files.map { FolderFile(it.first, it.second) }) }
        if (state.value.fileUri !in uris) {
            openFile(files.first().second)
        }
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
