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
    /** 当从文件夹里选中了多个 .md 文件时，弹出选择列表 */
    val folderChoices: List<FolderFile>? = null
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
        when {
            files.isEmpty() -> update { copy(message = "该文件夹下没有找到 .md 文件") }
            files.size == 1 -> openFile(files.first().second)
            else -> update {
                copy(
                    loading = false,
                    fileName = repo.displayName(uri) ?: "文件夹",
                    fileUri = uri,
                    folderChoices = files.map { FolderFile(it.first, it.second) }
                )
            }
        }
    }

    fun chooseFolderFile(uri: Uri) {
        update { copy(folderChoices = null) }
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
        runCatching { repo.writeText(uri, newLines.joinToString(cur.lineSeparator)) }
            .onFailure { update { copy(readOnly = true, message = "保存失败，已切换为只读模式") } }
    }

    fun dismissFolderDialog() = update { copy(folderChoices = null) }
    fun consumeMessage() = update { copy(message = null) }

    private fun loadFrom(uri: Uri) {
        update { copy(loading = true, error = null, folderChoices = null) }
        runCatching {
            val raw = repo.readText(uri)
            val board = KanbanParser.parse(raw, repo.displayName(uri) ?: "看板")
            board to uri
        }.onSuccess { (board, u) ->
            prefs.edit().putString("last_uri", u.toString()).apply()
            update {
                copy(
                    loading = false,
                    fileName = board.fileName.ifBlank { "看板" },
                    fileUri = u,
                    sections = board.sections,
                    lines = board.lines,
                    lineSeparator = board.lineSeparator,
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
