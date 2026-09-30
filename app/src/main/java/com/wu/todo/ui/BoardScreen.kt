package com.wu.todo.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wu.todo.R
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
import kotlinx.coroutines.delay
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
    // 新建任务列表的底部编辑页
    var showListSheet by remember { mutableStateOf(false) }

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
            sections = state.sections,
            pinned = openedSection.title in state.pinnedTitles,
            dotColor = Color(state.sectionColors[openedSection.title] ?: WuAccent.toArgb()),
            snackbarHostState = snackbarHostState,
            onBack = { openedSectionKey = null },
            onToggle = viewModel::toggle,
            onDelete = viewModel::delete,
            onTogglePin = { viewModel.togglePin(openedSection) },
            onRename = { newTitle -> viewModel.renameSection(openedSection, newTitle) },
            onAdd = { text -> viewModel.addTask(openedSection, text) },
            onRenameTask = viewModel::renameTask,
            onMoveTask = { task, target -> viewModel.moveTask(task, target, openedSection) },
            onAddSubtask = viewModel::addSubtask,
            onDeleteList = { viewModel.deleteSection(openedSection) },
            onSetAllDone = { done -> viewModel.setAllTasks(openedSection, done) },
            onDeleteCompleted = { viewModel.deleteCompletedTasks(openedSection) },
            onRefresh = viewModel::reload
        )
        return
    }

    // 抽屉打开时：系统返回（侧滑返回）先关闭抽屉，而不是退出应用
    if (drawerState.isOpen) {
        BackHandler { scope.launch { drawerState.close() } }
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
                    // 不再显示应用名 wu_todo，只显示当前文件与完成进度
                    if (state.fileName != null) {
                        val sub = buildString {
                            append(state.fileName)
                            if (state.totalTasks > 0) append(" · 已完成 ${state.doneTasks}/${state.totalTasks}")
                            if (state.readOnly) append(" · 只读")
                        }
                        Text(
                            text = sub,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = WuTitle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
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
            // 顶栏与看板内容（Pinned 区）之间的分割线
            HorizontalDivider(color = WuDivider, thickness = 1.dp)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showListSheet = true },
                containerColor = WuFab,
                contentColor = WuTitle
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "新建任务列表",
                    modifier = Modifier.size(30.dp)
                )
            }
        }
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
                // 瀑布流（StaggeredGrid）：卡片按自身高度紧密堆叠，
                // 不再像普通 Grid 那样按行对齐而在矮卡片下方留出空白
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalItemSpacing = 12.dp,
                        contentPadding = PaddingValues(
                            start = 12.dp,
                            end = 12.dp,
                            top = 12.dp,
                            bottom = 96.dp
                        ),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (pinnedSections.isNotEmpty()) {
                            item(key = "pinned_header", span = StaggeredGridItemSpan.FullLine) {
                                PinnedHeader()
                            }
                            items(pinnedSections, key = { "p_${it.uniqueKey()}" }) { section ->
                                SectionCard(
                                    section = section,
                                    dotColor = Color(state.sectionColors[section.title] ?: WuAccent.toArgb()),
                                    onToggle = viewModel::toggle,
                                    onOpen = { openedSectionKey = section.uniqueKey() }
                                )
                            }
                            if (normalSections.isNotEmpty()) {
                                item(key = "pinned_divider", span = StaggeredGridItemSpan.FullLine) {
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
                                dotColor = Color(state.sectionColors[section.title] ?: WuAccent.toArgb()),
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

    // 底部弹出：新建任务列表
    if (showListSheet) {
        AddListSheet(
            onDismiss = { showListSheet = false },
            onCreate = { name, colorArgb -> viewModel.addSection(name, colorArgb) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun SectionDetailScreen(
    section: KanbanSection,
    sections: List<KanbanSection>,
    pinned: Boolean,
    dotColor: Color,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onToggle: (KanbanTask) -> Unit,
    onDelete: (KanbanTask) -> Unit,
    onTogglePin: () -> Unit,
    onRename: (String) -> Unit,
    onAdd: (String) -> Unit,
    onRenameTask: (KanbanTask, String) -> Unit,
    onMoveTask: (KanbanTask, KanbanSection) -> Unit,
    onAddSubtask: (KanbanTask, String) -> Unit,
    onDeleteList: () -> Unit,
    onSetAllDone: (Boolean) -> Unit,
    onDeleteCompleted: () -> Unit,
    onRefresh: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var completedExpanded by remember { mutableStateOf(true) }
    // 底部弹出的添加任务输入
    var showAddSheet by remember { mutableStateOf(false) }
    var newTaskText by remember { mutableStateOf("") }
    // 当前正在编辑的任务（null 表示未打开编辑页）
    var editingTask by remember { mutableStateOf<KanbanTask?>(null) }
    // 标题编辑状态
    var editingTitle by remember(section.uniqueKey()) { mutableStateOf(false) }
    var titleDraft by remember(section.uniqueKey()) { mutableStateOf(section.title) }

    // 系统返回键/手势：回到看板主界面（编辑标题时先退出编辑）
    // 添加任务/编辑任务窗口打开时禁用此回调，由 KeyboardSheet 自己的 BackHandler 处理返回（只关窗口）
    BackHandler(enabled = !showAddSheet && editingTask == null) {
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
                        // List 分组
                        Text(
                            "List",
                            fontSize = 12.sp,
                            color = WuSubtle,
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
                        )
                        DropdownMenuItem(
                            text = { Text("Edit list", color = WuTitle) },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Edit,
                                    contentDescription = null,
                                    tint = WuTitle,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                titleDraft = section.title
                                editingTitle = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete List", color = WuTitle) },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = null,
                                    tint = WuTitle,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onDeleteList()
                            }
                        )
                        // Task 分组
                        Text(
                            "Task",
                            fontSize = 12.sp,
                            color = WuSubtle,
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
                        )
                        DropdownMenuItem(
                            text = { Text("Incomplete all tasks", color = WuTitle) },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = WuTitle,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onSetAllDone(false)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Complete all tasks", color = WuTitle) },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = WuTitle,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onSetAllDone(true)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete all completed tasks", color = WuTitle) },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Cancel,
                                    contentDescription = null,
                                    tint = WuTitle,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onDeleteCompleted()
                            }
                        )
                    }
                }
            )
            // 顶栏与列表标题之间的分割线
            HorizontalDivider(color = WuDivider, thickness = 1.dp)
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
                            fontSize = 15.sp,
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
                            .background(dotColor)
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = section.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = WuTitle,
                        lineHeight = 26.sp,
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
                // 空列表：卡通插画 + 提示文字
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp, bottom = 24.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.empty_illustration),
                        contentDescription = null,
                        modifier = Modifier.size(230.dp)
                    )
                    Spacer(Modifier.height(20.dp))
                    Text(
                        "There is no task.",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = WuTitle
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("Press + to add the task", fontSize = 15.sp, color = WuSubtle)
                }
            } else {
                activeTasks.forEach { task ->
                    DetailTaskRow(
                        task = task,
                        onToggle = onToggle,
                        onEdit = { editingTask = it }
                    )
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
                                onDelete = onDelete,
                                onEdit = { editingTask = it }
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
        val addFocus = remember { FocusRequester() }
        KeyboardSheet(onDismiss = { showAddSheet = false }, focus = addFocus) {
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
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(addFocus)
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

    // 底部弹出：任务编辑页（改文本 / 移动到列 / 加子项 / 删除 / 保存）
    editingTask?.let { t ->
        TaskEditSheet(
            task = t,
            sections = sections,
            currentSection = section,
            onDismiss = { editingTask = null },
            onRenameTask = onRenameTask,
            onMoveTask = onMoveTask,
            onAddSubtask = onAddSubtask,
            onDelete = onDelete
        )
    }
}

/** 键盘一体化底部面板：无滑入动画，出现即拉起键盘；imePadding 让面板骑在键盘上逐帧同步升起 */
@Composable
private fun KeyboardSheet(
    focus: FocusRequester,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val density = LocalDensity.current
    val imeInsets = WindowInsets.ime
    val scope = rememberCoroutineScope()
    var closing by remember { mutableStateOf(false) }
    // 平滑关闭：先收键盘，面板骑在键盘上同步降到底部，键盘收完再移除窗口（避免跳动）
    val close = {
        if (!closing) {
            closing = true
            keyboard?.hide()
            scope.launch {
                val start = System.currentTimeMillis()
                do {
                    delay(16)
                    // 持续压制键盘：对抗尚未完成的 show 请求（弹出途中返回时，避免键盘先升完再降的闪跳）
                    if (imeInsets.getBottom(density) > 0) keyboard?.hide()
                } while (System.currentTimeMillis() - start < 260 ||
                    (imeInsets.getBottom(density) > 0 && System.currentTimeMillis() - start < 1200)
                )
                onDismiss()
            }
        }
    }
    // 面板从组合移除时（Done/保存等直接关闭路径）兜底收起键盘
    DisposableEffect(Unit) {
        onDispose { keyboard?.hide() }
    }
    BackHandler(onBack = close)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.32f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { close() }
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .imePadding()
                .background(WuCard, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* 吃掉面板内点击，不关闭 */ }
        ) {
            // 出现即聚焦并拉起键盘（与面板同时出现，逐帧重试保证成功）；
            // close 之后立即停止重试，否则键盘会被重新拉起造成闪跳
            LaunchedEffect(Unit) {
                repeat(40) {
                    if (closing) return@LaunchedEffect
                    focus.requestFocus()
                    keyboard?.show()
                    delay(16)
                }
            }
            content()
        }
    }
}

/** 底部弹出的任务编辑页 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskEditSheet(
    task: KanbanTask,
    sections: List<KanbanSection>,
    currentSection: KanbanSection,
    onDismiss: () -> Unit,
    onRenameTask: (KanbanTask, String) -> Unit,
    onMoveTask: (KanbanTask, KanbanSection) -> Unit,
    onAddSubtask: (KanbanTask, String) -> Unit,
    onDelete: (KanbanTask) -> Unit
) {
    var text by remember(task.id) { mutableStateOf(task.text) }
    var moveMenu by remember { mutableStateOf(false) }
    var showSubInput by remember { mutableStateOf(false) }
    var subText by remember { mutableStateOf("") }
    val textFocus = remember { FocusRequester() }

    // 键盘一体化面板：出现即拉起键盘，面板随键盘同步升起
    KeyboardSheet(onDismiss = onDismiss, focus = textFocus) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height((LocalConfiguration.current.screenHeightDp * 0.72f).dp)
                .padding(horizontal = 20.dp)
        ) {
            // 顶部：右上角删除按钮
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { onDelete(task); onDismiss() }) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "删除任务",
                        tint = WuSubtle,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            // 任务文本编辑（大号粗体）
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("任务内容", color = WuSubtle, fontSize = 20.sp) },
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = WuTitle
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = WuAccent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(textFocus)
            )
            HorizontalDivider(color = WuDivider, thickness = 1.dp)
            // 移动到其他列
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                Icon(
                    Icons.Filled.List,
                    contentDescription = null,
                    tint = WuSubtle,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(14.dp))
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(WuAccent.copy(alpha = 0.12f))
                            .clickable { moveMenu = true }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(currentSection.title, fontSize = 13.sp, color = WuAccent)
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            contentDescription = "选择列",
                            tint = WuAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = moveMenu,
                        onDismissRequest = { moveMenu = false }
                    ) {
                        sections.forEach { s ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        s.title,
                                        color = if (s.headerLineIndex == currentSection.headerLineIndex) WuSubtle else WuTitle
                                    )
                                },
                                onClick = {
                                    moveMenu = false
                                    onMoveTask(task, s)
                                    onDismiss()
                                }
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = WuDivider, thickness = 1.dp)
            // Add items：添加子任务
            if (!showSubInput) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showSubInput = true }
                        .padding(vertical = 10.dp)
                ) {
                    Icon(
                        Icons.Outlined.AddCircle,
                        contentDescription = null,
                        tint = WuSubtle,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(14.dp))
                    Text("Add items", fontSize = 15.sp, color = WuSubtle)
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    TextField(
                        value = subText,
                        onValueChange = { subText = it },
                        placeholder = { Text("子任务内容", color = WuSubtle, fontSize = 14.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = WuBackground,
                            unfocusedContainerColor = WuBackground,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = WuAccent
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        onAddSubtask(task, subText)
                        subText = ""
                        showSubInput = false
                    }) {
                        Icon(Icons.Filled.Add, contentDescription = "添加子任务", tint = WuTitle)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Spacer(Modifier.weight(1f))
            // 右下角黄色对勾：保存文本修改（贴底）
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                FloatingActionButton(
                    onClick = {
                        onRenameTask(task, text)
                        onDismiss()
                    },
                    containerColor = WuFab,
                    contentColor = WuTitle
                ) {
                    Icon(Icons.Filled.Check, contentDescription = "保存")
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

/** 详情页任务行：点击行=打开底部编辑页；点击圆圈=勾选完成 */
@Composable
private fun DetailTaskRow(
    task: KanbanTask,
    onToggle: (KanbanTask) -> Unit,
    onEdit: (KanbanTask) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onEdit(task) }
            .padding(start = (task.indent * 10).dp, top = 11.dp, bottom = 11.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .clickable { onToggle(task) }
                .padding(2.dp)
        ) {
            BigCheckCircle(done = false)
        }
        Spacer(Modifier.width(14.dp))
        Text(
            text = task.text,
            fontSize = 14.sp,
            color = WuTitle,
            lineHeight = 19.sp
        )
    }
}

/** 已完成任务：灰色对勾（点=取消完成）+ 灰字（点=编辑）+ 右侧 ✕ 删除 */
@Composable
private fun CompletedTaskRow(
    task: KanbanTask,
    onToggle: (KanbanTask) -> Unit,
    onDelete: (KanbanTask) -> Unit,
    onEdit: (KanbanTask) -> Unit
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
            fontSize = 14.sp,
            color = WuDoneGrey,
            lineHeight = 19.sp,
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onEdit(task) }
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
            .size(20.dp)
            .clip(CircleShape)
            .then(
                if (done) Modifier.background(WuAccent)
                else Modifier.border(1.5.dp, WuCircleStroke, CircleShape)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (done) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(13.dp)
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
    dotColor: Color,
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
                        .background(dotColor)
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
            .padding(start = (task.indent * 10).dp, top = 3.dp, bottom = 3.dp)
    ) {
        CheckCircle(done = task.done, size = 14.dp, onClick = { onToggle(task) })
        Spacer(Modifier.width(10.dp))
        Text(
            text = task.text,
            fontSize = 13.sp,
            color = if (task.done) WuTaskText.copy(alpha = 0.7f) else WuTaskText,
            textDecoration = if (task.done) TextDecoration.LineThrough else null,
            lineHeight = 18.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CheckCircle(
    done: Boolean,
    size: Dp = 20.dp,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .then(
                if (done) Modifier.background(WuAccent)
                else Modifier.border(1.5.dp, WuCircleStroke, CircleShape)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (done) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(size * 0.6f)
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

/** 可选的列圆点颜色（调色板循环切换） */
private val ListPalette = listOf(
    0xFFF2645A, 0xFFF59E42, 0xFFF6C344, 0xFF4CAF50,
    0xFF42A5F5, 0xFF9C6ADE, 0xFF8A8A8A
)

/** 底部弹出：新建任务列表（输入名称 + 调色板选圆点颜色） */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddListSheet(
    onDismiss: () -> Unit,
    onCreate: (String, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var colorIdx by remember { mutableStateOf(0) }

    val titleFocus = remember { FocusRequester() }
    KeyboardSheet(onDismiss = onDismiss, focus = titleFocus) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height((LocalConfiguration.current.screenHeightDp * 0.72f).dp)
                .padding(horizontal = 20.dp)
        ) {
            // 列表名称输入
            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("List name", color = WuSubtle, fontSize = 22.sp) },
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = WuTitle
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = WuAccent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(titleFocus)
            )
            HorizontalDivider(color = WuDivider, thickness = 1.dp)
            // 调色板：点击换色，圆点显示当前颜色
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Icon(
                    Icons.Outlined.Palette,
                    contentDescription = "切换颜色",
                    tint = WuSubtle,
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .clickable { colorIdx = (colorIdx + 1) % ListPalette.size }
                )
                Spacer(Modifier.width(18.dp))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(ListPalette[colorIdx].toInt()))
                        .clickable { colorIdx = (colorIdx + 1) % ListPalette.size }
                )
                Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.weight(1f))
            // 右下角黄色对勾：保存
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                FloatingActionButton(
                    onClick = {
                        if (title.isNotBlank()) {
                            onCreate(title.trim(), ListPalette[colorIdx].toInt())
                        }
                        onDismiss()
                    },
                    containerColor = WuFab,
                    contentColor = WuTitle
                ) {
                    Icon(Icons.Filled.Check, contentDescription = "创建列表")
                }
            }
            Spacer(Modifier.height(14.dp))
        }
    }
}
