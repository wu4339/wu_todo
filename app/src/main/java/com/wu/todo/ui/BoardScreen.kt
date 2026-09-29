package com.wu.todo.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wu.todo.data.KanbanSection
import com.wu.todo.data.KanbanTask
import com.wu.todo.ui.theme.WuAccent
import com.wu.todo.ui.theme.WuBackground
import com.wu.todo.ui.theme.WuCard
import com.wu.todo.ui.theme.WuCircleStroke
import com.wu.todo.ui.theme.WuDivider
import com.wu.todo.ui.theme.WuDoneGrey
import com.wu.todo.ui.theme.WuFab
import com.wu.todo.ui.theme.WuSubtle
import com.wu.todo.ui.theme.WuTaskText
import com.wu.todo.ui.theme.WuTitle
import kotlinx.coroutines.launch

/** 用「列头行号」作为一列的唯一 key，重命名标题后 key 不变，详情页不会跳回总览 */
private fun KanbanSection.uniqueKey() = "col@${headerLineIndex}"

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
    // 当前打开的看板列（null 表示在看板总览）
    var openedSectionKey by remember { mutableStateOf<String?>(null) }
    // 左侧栏（文件抽屉）
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    val openedSection = openedSectionKey?.let { key ->
        state.sections.firstOrNull { it.uniqueKey() == key }
    }

    // 点击卡片后进入该列的详情页
    if (openedSection != null) {
        SectionDetailScreen(
            section = openedSection,
            pinned = openedSection.title in state.pinnedTitles,
            snackbarHostState = snackbarHostState,
            onBack = { openedSectionKey = null },
            onToggle = viewModel::toggle,
            onDelete = viewModel::delete,
            onTogglePin = { viewModel.togglePin(openedSection) },
            onRename = { newTitle -> viewModel.renameSection(openedSection, newTitle) },
            onAdd = { text -> viewModel.addTask(openedSection, text) },
            onRefresh = viewModel::reload
        )
        return
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            BoardDrawerContent(
                files = state.drawerFiles,
                currentUri = state.fileUri,
                onPick = { uri ->
                    openedSectionKey = null
                    viewModel.chooseFolderFile(uri)
                    scope.launch { drawerState.close() }
                },
                onOpenFolder = {
                    scope.launch { drawerState.close() }
                    onOpenFolder()
                }
            )
        }
    ) {
    Scaffold(
        containerColor = WuBackground,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WuBackground,
                    titleContentColor = WuTitle,
                    navigationIconContentColor = WuTitle,
                    actionIconContentColor = WuTitle
                ),
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Filled.Menu, contentDescription = "打开文件列表")
                    }
                },
                title = {
                    Column {
                        Text(
                            text = "wu_todo",
                            fontSize = 16.sp,
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
                                fontSize = 11.sp,
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
                        fontSize = 13.sp
                    )
                }
            }
            else -> {
                val pinnedSections = state.sections.filter { it.title in state.pinnedTitles }
                val normalSections = state.sections.filter { it.title !in state.pinnedTitles }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = innerPadding,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    if (pinnedSections.isNotEmpty()) {
                        item(key = "pinned_header", span = { GridItemSpan(2) }) {
                            PinnedHeader()
                        }
                        items(pinnedSections, key = { "p_${it.uniqueKey()}" }) { section ->
                            SectionCard(
                                section = section,
                                onToggle = viewModel::toggle,
                                onOpen = { openedSectionKey = section.uniqueKey() }
                            )
                        }
                        if (normalSections.isNotEmpty()) {
                            item(key = "pinned_divider", span = { GridItemSpan(2) }) {
                                HorizontalDivider(
                                    color = WuDivider,
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                )
                            }
                        }
                    }
                    items(normalSections, key = { it.uniqueKey() }) { section ->
                        SectionCard(
                            section = section,
                            onToggle = viewModel::toggle,
                            onOpen = { openedSectionKey = section.uniqueKey() }
                        )
                    }
                }
            }
        }
    }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SectionDetailScreen(
    section: KanbanSection,
    pinned: Boolean,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onToggle: (KanbanTask) -> Unit,
    onDelete: (KanbanTask) -> Unit,
    onTogglePin: () -> Unit,
    onRename: (String) -> Unit,
    onAdd: (String) -> Unit,
    onRefresh: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var completedExpanded by remember { mutableStateOf(true) }
    // 底部弹出的添加任务输入
    var showAddSheet by remember { mutableStateOf(false) }
    var newTaskText by remember { mutableStateOf("") }
    // 标题编辑状态
    var editingTitle by remember(section.uniqueKey()) { mutableStateOf(false) }
    var titleDraft by remember(section.uniqueKey()) { mutableStateOf(section.title) }

    // 系统返回键/手势：回到看板主界面（编辑标题时先退出编辑）
    BackHandler {
        if (editingTitle) {
            editingTitle = false
            titleDraft = section.title
        } else {
            onBack()
        }
    }

    val activeTasks = section.tasks.filter { !it.done }
    val doneTasks = section.tasks.filter { it.done }

    Scaffold(
        containerColor = WuBackground,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WuBackground,
                    navigationIconContentColor = WuTitle,
                    actionIconContentColor = WuTitle
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "返回看板")
                    }
                },
                title = {},
                actions = {
                    IconButton(onClick = onTogglePin) {
                        if (pinned) {
                            Icon(
                                Icons.Filled.PushPin,
                                contentDescription = "取消置顶",
                                tint = WuAccent,
                                modifier = Modifier.rotate(35f)
                            )
                        } else {
                            Icon(
                                Icons.Outlined.PushPin,
                                contentDescription = "置顶",
                                tint = WuTitle,
                                modifier = Modifier.rotate(35f)
                            )
                        }
                    }
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "更多")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("刷新") },
                            onClick = { menuExpanded = false; onRefresh() }
                        )
                        DropdownMenuItem(
                            text = { Text("返回看板") },
                            onClick = { menuExpanded = false; onBack() }
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = WuFab,
                contentColor = WuTitle
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "添加任务",
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            // 列标题：红色圆点 + 大号标题（点击进入编辑）
            if (editingTitle) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = titleDraft,
                        onValueChange = { titleDraft = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = WuTitle
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        editingTitle = false
                        titleDraft = section.title
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = "取消编辑", tint = WuSubtle)
                    }
                    IconButton(onClick = {
                        onRename(titleDraft)
                        editingTitle = false
                    }) {
                        Icon(Icons.Filled.Check, contentDescription = "保存标题", tint = WuAccent)
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            titleDraft = section.title
                            editingTitle = true
                        }
                        .padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(WuAccent)
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = section.title,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = WuTitle,
                        lineHeight = 32.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "重命名列标题",
                        tint = WuSubtle,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(Modifier.height(22.dp))

            // 未完成任务
            if (activeTasks.isEmpty() && doneTasks.isEmpty()) {
                Text("（无任务）", color = WuSubtle, fontSize = 14.sp)
            } else {
                activeTasks.forEach { task ->
                    DetailTaskRow(task = task, onToggle = onToggle)
                }
            }

            // 已完成：折叠区
            if (doneTasks.isNotEmpty()) {
                Spacer(Modifier.height(18.dp))
                HorizontalDivider(color = WuDivider, thickness = 1.dp)
                Spacer(Modifier.height(10.dp))

                val chevronAngle by animateFloatAsState(
                    targetValue = if (completedExpanded) 0f else -90f,
                    label = "chevron"
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { completedExpanded = !completedExpanded }
                        .padding(vertical = 6.dp)
                ) {
                    Text(
                        text = "Completed",
                        fontSize = 15.sp,
                        color = WuSubtle,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (completedExpanded) "折叠已完成" else "展开已完成",
                        tint = WuSubtle,
                        modifier = Modifier
                            .size(22.dp)
                            .rotate(chevronAngle)
                    )
                }

                AnimatedVisibility(visible = completedExpanded) {
                    Column {
                        Spacer(Modifier.height(4.dp))
                        doneTasks.forEach { task ->
                            CompletedTaskRow(
                                task = task,
                                onToggle = onToggle,
                                onDelete = onDelete
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }

    // 底部弹出：新建任务输入条（输入 + 右侧加号提交）
    if (showAddSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddSheet = false },
            containerColor = WuCard
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                TextField(
                    value = newTaskText,
                    onValueChange = { newTaskText = it },
                    placeholder = { Text("new task", color = WuSubtle, fontSize = 16.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = WuAccent
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        onAdd(newTaskText)
                        newTaskText = ""
                        showAddSheet = false
                    }),
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        onAdd(newTaskText)
                        newTaskText = ""
                        showAddSheet = false
                    }
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "确认添加",
                        tint = WuTitle,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Spacer(Modifier.height(18.dp))
        }
    }
}

@Composable
private fun DetailTaskRow(
    task: KanbanTask,
    onToggle: (KanbanTask) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onToggle(task) }
            .padding(start = (task.indent * 10).dp, top = 11.dp, bottom = 11.dp)
    ) {
        BigCheckCircle(done = false)
        Spacer(Modifier.width(16.dp))
        Text(
            text = task.text,
            fontSize = 18.sp,
            color = WuTaskText,
            lineHeight = 24.sp
        )
    }
}

/** 已完成任务：灰色对勾 + 灰字 + 右侧 ✕ 删除（对勾或文字可取消完成） */
@Composable
private fun CompletedTaskRow(
    task: KanbanTask,
    onToggle: (KanbanTask) -> Unit,
    onDelete: (KanbanTask) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .padding(start = (task.indent * 10).dp, top = 8.dp, bottom = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = "取消完成",
            tint = WuDoneGrey,
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .clickable { onToggle(task) }
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = task.text,
            fontSize = 18.sp,
            color = WuDoneGrey,
            lineHeight = 24.sp,
            modifier = Modifier
                .weight(1f)
                .clickable { onToggle(task) }
        )
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "删除任务",
            tint = WuDoneGrey,
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .clickable { onDelete(task) }
        )
    }
}

@Composable
private fun BigCheckCircle(done: Boolean) {
    Box(
        modifier = Modifier
            .size(24.dp)
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
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/** 总览顶部的「PINNED」分组标题：斜置图钉 + 字距拉开的灰字 */
@Composable
private fun PinnedHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 2.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.PushPin,
            contentDescription = null,
            tint = WuSubtle,
            modifier = Modifier
                .size(16.dp)
                .rotate(-35f)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = "PINNED",
            fontSize = 13.sp,
            color = WuSubtle,
            letterSpacing = 1.5.sp
        )
    }
}

@Composable
private fun SectionCard(
    section: KanbanSection,
    onToggle: (KanbanTask) -> Unit,
    onOpen: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() },
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
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = WuTitle
                )
            }
            Spacer(Modifier.height(14.dp))
            if (section.tasks.isEmpty()) {
                Text("（无任务）", color = WuSubtle, fontSize = 12.sp)
            } else {
                section.tasks.forEach { task ->
                    TaskRow(task = task, onToggle = onToggle, onOpen = onOpen)
                }
            }
        }
    }
}

/** 主界面卡片里的任务行：点击复选框=切换完成并写回 .md；点击其余区域=打开该列详情 */
@Composable
private fun TaskRow(
    task: KanbanTask,
    onToggle: (KanbanTask) -> Unit,
    onOpen: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onOpen() }
            .padding(start = (task.indent * 10).dp, top = 5.dp, bottom = 5.dp)
    ) {
        CheckCircle(done = task.done, size = 18.dp, onClick = { onToggle(task) })
        Spacer(Modifier.width(12.dp))
        Text(
            text = task.text,
            fontSize = 15.sp,
            color = if (task.done) WuTaskText.copy(alpha = 0.7f) else WuTaskText,
            textDecoration = if (task.done) TextDecoration.LineThrough else null,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun CheckCircle(
    done: Boolean,
    size: Dp = 22.dp,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
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
                modifier = Modifier.size(size * 0.62f)
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
        Text("wu_todo", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = WuAccent)
        Spacer(Modifier.height(10.dp))
        Text("读取 Obsidian 看板，随手打勾", fontSize = 13.sp, color = WuSubtle)
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

/** 主界面左侧栏：列出所选文件夹内的全部 .md 文件，点击切换；底部提供选择文件夹入口 */
@Composable
private fun BoardDrawerContent(
    files: List<FolderFile>,
    currentUri: android.net.Uri?,
    onPick: (android.net.Uri) -> Unit,
    onOpenFolder: () -> Unit
) {
    ModalDrawerSheet(drawerContainerColor = WuCard) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 20.dp)
        ) {
            Text("wu_todo", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WuAccent)
            Spacer(Modifier.height(4.dp))
            Text("看板文件", fontSize = 12.sp, color = WuSubtle)
            Spacer(Modifier.height(14.dp))
            if (files.isEmpty()) {
                Text(
                    "尚未选择文件夹\n\n点下方按钮选择看板文件夹，\n其中所有 .md 文件会列在这里",
                    fontSize = 13.sp,
                    color = WuSubtle
                )
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    files.forEach { f ->
                        val selected = f.uri == currentUri
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onPick(f.uri) }
                                .padding(horizontal = 8.dp, vertical = 10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (selected) WuAccent else Color.Transparent)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                f.name,
                                fontSize = 14.sp,
                                color = if (selected) WuTitle else WuTaskText,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = WuDivider, thickness = 1.dp)
            Spacer(Modifier.height(10.dp))
            TextButton(onClick = onOpenFolder) {
                Icon(
                    Icons.Filled.FolderOpen,
                    contentDescription = null,
                    tint = WuSubtle,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("选择看板文件夹", fontSize = 13.sp, color = WuSubtle)
            }
        }
    }
}
