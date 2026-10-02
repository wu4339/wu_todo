package com.wu.todo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.wu.todo.ui.BoardScreen
import com.wu.todo.ui.BoardViewModel

class MainActivity : ComponentActivity() {

    private val viewModel by lazy { BoardViewModel(application) }

    private val openFileLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let { viewModel.openFile(it) } }

    private val openFolderLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri -> uri?.let { viewModel.addBoardPath(it) } }

    private val addFileLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let { viewModel.addMdFile(it) } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 从“用其他应用打开”入口直接加载 .md
        if (intent?.action == Intent.ACTION_VIEW) {
            intent.data?.let { viewModel.openFile(it) }
        }

        setContent {
            com.wu.todo.ui.theme.WuTodoTheme {
                BoardScreen(
                    viewModel = viewModel,
                    onOpenFile = {
                        openFileLauncher.launch(
                            arrayOf("text/markdown", "text/plain", "application/octet-stream", "*/*")
                        )
                    },
                    onAddFile = {
                        addFileLauncher.launch(
                            arrayOf("text/markdown", "text/plain", "application/octet-stream", "*/*")
                        )
                    },
                    onOpenFolder = { openFolderLauncher.launch(null) }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // 回到前台时重新加载，确保外部（如 Obsidian）对看板的修改可见
        viewModel.reload()
    }
}
