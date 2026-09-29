package com.wu.todo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wu.todo.data.KanbanSection
import com.wu.todo.data.KanbanTask
import com.wu.todo.ui.theme.WuAccent
import com.wu.todo.ui.theme.WuBackground
import com.wu.todo.ui.theme.WuCard
import com.wu.todo.ui.theme.WuCircleStroke
import com.wu.todo.ui.theme.WuSubtle
import com.wu.todo.ui.theme.WuTaskText
import com.wu.todo.ui.theme.WuTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardScreen(
    viewModel: BoardViewModel,
    onOpenFile: () -> Unit,
    onOpenFolder: () -> Unit
) {
    val state by viewModel.state
    val snackbarHostState = remember { SnackbarHostState() }
    var menuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    Scaffold(
        containerColor = WuBackground,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WuBackground,
                    titleContentColor = WuTitle,
                    actionIconContentColor = WuTitle
                ),
                title = {
                    Column {
                        Text(
                            text = "wu_todo",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = WuAccent
                        )
                        if (state.fileName != null) {
                            val sub = buildString {
                                append(state.fileName)
                                if (state.totalTasks > 0) append(" · 已完成 ${state.doneTasks}/${state.totalTasks}")
                                if (state.readOnly) append(" · 只读")
                            }
                            Text(
                                text = sub,
                                fontSize = 12.sp,
                                color = WuSubtle,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.reload() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "刷新")
                    }
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "更多")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("打开 .md 文件") },
                            onClick = { menuExpanded = false; onOpenFile() }
                        )
                        DropdownMenuItem(
                            text = { Text("选择看板文件夹") },
                            onClick = { menuExpanded = false; onOpenFolder() }
                        )
                        DropdownMenuItem(
                            text = { Text("刷新") },
                            onClick = { menuExpanded = false; viewModel.reload() }
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->

        when {
            state.loading && state.sections.isEmpty() -> {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = WuAccent)
                }
            }
            state.fileName == null -> {
                EmptyState(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    onOpenFile = onOpenFile,
                    onOpenFolder = onOpenFolder
                )
            }
            state.sections.isEmpty() -> {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Text(
                        text = "这个看板还没有任务",
                        color = WuSubtle,
                        fontSize = 15.sp
                    )
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = innerPadding,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    items(state.sections, key = { it.title + it.headerLineIndex }) { section ->
                        SectionCard(section = section, onToggle = viewModel::toggle)
                    }
                }
            }
        }
    }

    // 文件夹内多个 .md 文件时的选择对话框
    if (state.folderChoices != null) {
        FolderPickerDialog(
            files = state.folderChoices!!,
            onPick = { viewModel.chooseFolderFile(it) },
            onDismiss = { viewModel.dismissFolderDialog() }
        )
    }
}

@Composable
private fun SectionCard(
    section: KanbanSection,
    onToggle: (KanbanTask) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = WuCard)
    ) {
        Column(Modifier.padding(horizontal = 18.dp, vertical = 18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(13.dp)
                        .clip(CircleShape)
                        .background(WuAccent)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = section.title,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = WuTitle
                )
            }
            Spacer(Modifier.height(14.dp))
            if (section.tasks.isEmpty()) {
                Text("（无任务）", color = WuSubtle, fontSize = 13.sp)
            } else {
                section.tasks.forEach { task ->
                    TaskRow(task = task, onToggle = onToggle)
                }
            }
        }
    }
}

@Composable
private fun TaskRow(
    task: KanbanTask,
    onToggle: (KanbanTask) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onToggle(task) }
            .padding(start = (task.indent * 10).dp, top = 9.dp, bottom = 9.dp)
    ) {
        CheckCircle(done = task.done)
        Spacer(Modifier.width(14.dp))
        Text(
            text = task.text,
            fontSize = 17.sp,
            color = if (task.done) WuTaskText.copy(alpha = 0.7f) else WuTaskText,
            textDecoration = if (task.done) TextDecoration.LineThrough else null,
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun CheckCircle(done: Boolean) {
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .then(
                if (done) Modifier.background(WuAccent)
                else Modifier.border(2.dp, WuCircleStroke, CircleShape)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (done) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
    onOpenFile: () -> Unit,
    onOpenFolder: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("wu_todo", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = WuAccent)
        Spacer(Modifier.height(10.dp))
        Text("读取 Obsidian 看板，随手打勾", fontSize = 15.sp, color = WuSubtle)
        Spacer(Modifier.height(28.dp))
        Button(onClick = onOpenFile) {
            Icon(Icons.Filled.FolderOpen, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("打开看板 .md 文件")
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onOpenFolder) {
            Icon(Icons.Filled.FolderOpen, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("从文件夹选择")
        }
    }
}

@Composable
private fun FolderPickerDialog(
    files: List<FolderFile>,
    onPick: (android.net.Uri) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择看板文件") },
        text = {
            Column {
                files.forEach { file ->
                    TextButton(
                        onClick = { onPick(file.uri) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            file.name,
                            modifier = Modifier.fillMaxWidth(),
                            color = WuTitle,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
