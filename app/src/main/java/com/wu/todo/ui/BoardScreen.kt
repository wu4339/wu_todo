package com.wu.todo.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DriveFileMove
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.SubdirectoryArrowRight
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
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
import androidx.compose.ui.zIndex
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

/** 输入框允许多行（自动换行展示），但 Obsidian Kanban 一条任务只占一行 markdown，
 *  因此写入前把换行/制表符折叠为空格，避免破坏 board 结构 */
private fun String.toSingleLineTaskText(): String =
    trim().replace(Regex("[\\r\\n\\t]+"), " ")

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
            sectionColors = state.sectionColors,
            onReorderTasks = viewModel::reorderTasks,
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
            onSetNote = viewModel::setNote,
            onDeleteList = { viewModel.deleteSection(openedSection) },
            onSetAllDone = { done -> viewModel.setAllTasks(openedSection, done) },
            onDeleteCompleted = { viewModel.deleteCompletedTasks(openedSection) },
            onRefresh = viewModel::reload,
            onSetDotColor = { argb -> viewModel.setSectionColor(openedSection, argb) }
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
            Column {
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
                    // 只显示当前文件名，不显示已完成进度
                    if (state.fileName != null) {
                        val sub = buildString {
                            append(state.fileName)
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
            // 顶栏与看板内容（Pinned 区）之间的分割线（上移 4dp、颜色更淡）
            HorizontalDivider(
                color = Color(0xFFE6E6E6),
                thickness = 1.dp,
                modifier = Modifier.offset(y = (-4).dp)
            )
            }
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalItemSpacing = 8.dp,
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
    sectionColors: Map<String, Int>,
    onReorderTasks: (KanbanSection, List<Int>) -> Unit,
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
    onSetNote: (KanbanTask, String) -> Unit,
    onDeleteList: () -> Unit,
    onSetAllDone: (Boolean) -> Unit,
    onDeleteCompleted: () -> Unit,
    onRefresh: () -> Unit,
    onSetDotColor: (Int) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var completedExpanded by remember { mutableStateOf(true) }
    // 底部弹出的添加任务输入
    var showAddSheet by remember { mutableStateOf(false) }
    var newTaskText by remember { mutableStateOf("") }
    // 当前正在编辑的任务（null 表示未打开编辑页）
    var editingTask by remember { mutableStateOf<KanbanTask?>(null) }
    // 点击列标题：弹出列表编辑页（改名称 + 调色板换色 + 删除列表）
    var showEditSheet by remember(section.uniqueKey()) { mutableStateOf(false) }
    // 点击圆点：在调色板中循环切换本列颜色
    val cycleDotColor = {
        val idx = ListPalette.indexOf(dotColor.toArgb().toLong())
        onSetDotColor(ListPalette[(idx + 1) % ListPalette.size].toInt())
    }

    // 系统返回键/手势：回到看板主界面
    // 添加任务/编辑任务/编辑列表窗口打开时禁用此回调，由 KeyboardSheet 自己的 BackHandler 处理返回（只关窗口）
    BackHandler(enabled = !showAddSheet && editingTask == null && !showEditSheet) {
        onBack()
    }

    // 子任务不展开：详情页同样只列出顶层任务，子任务以「已完成/总数」计数显示
    val topTasks = section.topLevelTasks()
    val activeTasks = topTasks.filter { !it.done }
    val doneTasks = topTasks.filter { it.done }

    // ---- 长按拖动排序 ----
    val sortKey = section.uniqueKey()
    // 拖动中的显示顺序（任务 id）；null = 未拖动
    var dragOrder by remember(sortKey) { mutableStateOf<List<String>?>(null) }
    var dragTaskId by remember(sortKey) { mutableStateOf<String?>(null) }
    var dragOffset by remember(sortKey) { mutableFloatStateOf(0f) }
    // 各行高度（拖动时用于推算槽位位置）
    val rowHeights = remember(sortKey) { mutableStateMapOf<String, Float>() }
    // pointerInput 的 lambda 只在首次创建时捕获变量，用 State 才能读到最新值
    val dragOrderState = rememberUpdatedState(dragOrder)
    val activeTasksState = rememberUpdatedState(activeTasks)
    val reorderState = rememberUpdatedState(onReorderTasks)
    val sectionState = rememberUpdatedState(section)
    // 拖动中按本地顺序渲染，实现"其它行让位"；松手后一次性提交排序
    val displayedTasks = dragOrder?.let { order ->
        order.mapNotNull { id -> activeTasks.firstOrNull { it.id == id } }
    } ?: activeTasks

    Scaffold(
        containerColor = WuBackground,
        topBar = {
            Column {
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
                                showEditSheet = true
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
            // 顶栏与列表标题之间的分割线（上移 4dp、颜色更淡）
            HorizontalDivider(
                color = Color(0xFFE6E6E6),
                thickness = 1.dp,
                modifier = Modifier.offset(y = (-4).dp)
            )
            }
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
            // 列标题：彩色圆点 + 大号标题（点击标题弹出列表编辑页）
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { showEditSheet = true }
                    .padding(vertical = 4.dp)
            ) {
                // 点击圆点：在调色板中循环切换本列颜色
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                        .clickable { cycleDotColor() }
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
            }
            Spacer(Modifier.height(15.dp))

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
                displayedTasks.forEach { task -> key(task.id) {
                    val dragging = dragTaskId == task.id
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .zIndex(if (dragging) 1f else 0f)
                            .graphicsLayer { translationY = if (dragging) dragOffset else 0f }
                            .shadow(
                                elevation = if (dragging) 8.dp else 0.dp,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .background(
                                color = if (dragging) WuCard else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .onGloballyPositioned { coords ->
                                // 只记高度：拖动时用"顺序 + 高度累加"推算各行位置，
                                // 不依赖 onGloballyPositioned 回写的实时 y（换位后要下一帧才更新）
                                rowHeights[task.id] = coords.size.height.toFloat()
                            }
                            .pointerInput(task.id) {
                                // 拖动期局部几何模型：手势开始时快照顺序与各行高度，
                                // 之后每次换位都由"高度累加"同步算出槽位 y，
                                // 彻底避免读到过期坐标导致的反复横跳
                                var ids: MutableList<String> = mutableListOf()
                                var hs: Map<String, Float> = emptyMap()

                                // 按当前顺序累加高度得到每个槽位的顶部 y（基准 0，比较时同基准即可）
                                fun topsOf(): FloatArray {
                                    val arr = FloatArray(ids.size)
                                    var acc = 0f
                                    ids.forEachIndexed { i, id ->
                                        arr[i] = acc
                                        acc += hs[id] ?: 0f
                                    }
                                    return arr
                                }

                                detectDragGesturesAfterLongPress(
                                    onDragStart = {
                                        val snapshot = activeTasksState.value
                                        ids = snapshot.map { it.id }.toMutableList()
                                        // 快照各行高度；个别未测到的用中位高度兜底，避免模型塌陷
                                        val measured = snapshot.mapNotNull { rowHeights[it.id] }
                                            .filter { it > 0f }
                                            .sorted()
                                        val fallback = measured.getOrNull(measured.size / 2) ?: 0f
                                        hs = snapshot.associate {
                                            it.id to ((rowHeights[it.id] ?: 0f).takeIf { h -> h > 0f } ?: fallback)
                                        }
                                        dragTaskId = task.id
                                        dragOffset = 0f
                                        dragOrder = ids.toList()
                                    },
                                    onDrag = { change, amount ->
                                        change.consume()
                                        if (dragOrderState.value == null || ids.isEmpty()) return@detectDragGesturesAfterLongPress
                                        dragOffset += amount.y
                                        val h = hs[task.id] ?: 0f
                                        // 越过相邻行中位线即交换；每次交换做位移补偿保持视觉连续
                                        var guard = 0
                                        var swapped = true
                                        while (swapped && guard++ < 20) {
                                            swapped = false
                                            val tops = topsOf()
                                            val i = ids.indexOf(task.id)
                                            if (i < 0) break
                                            val center = tops[i] + h / 2 + dragOffset
                                            // 向下越过下一行中线 → 与下一行交换
                                            if (i < ids.size - 1) {
                                                val nid = ids[i + 1]
                                                val nh = hs[nid] ?: 0f
                                                if (center > tops[i + 1] + nh / 2) {
                                                    ids[i] = nid
                                                    ids[i + 1] = task.id
                                                    // 换位后被拖行坐到下一槽位（其顶部 = 原顶部 + 邻行高）
                                                    dragOffset -= nh
                                                    dragOrder = ids.toList()
                                                    swapped = true
                                                    continue
                                                }
                                            }
                                            // 向上越过上一行中线 → 与上一行交换
                                            if (i > 0) {
                                                val pid = ids[i - 1]
                                                val ph = hs[pid] ?: 0f
                                                if (center < tops[i - 1] + ph / 2) {
                                                    ids[i] = pid
                                                    ids[i - 1] = task.id
                                                    // 换位后被拖行上移到上一槽位
                                                    dragOffset += ph
                                                    dragOrder = ids.toList()
                                                    swapped = true
                                                    continue
                                                }
                                            }
                                        }
                                    },
                                    onDragEnd = {
                                        val order = dragOrderState.value
                                        dragTaskId = null
                                        dragOffset = 0f
                                        dragOrder = null
                                        if (order != null) {
                                            val sec = sectionState.value
                                            val all = sec.tasks
                                            val active = activeTasksState.value
                                            // 拖动只作用于顶层任务；落盘时把「顶层任务 + 其子任务行」
                                            // 整块按新顺序写回，其它行（已完成任务 / 备注）保持原槽位
                                            val movedFlat = order.mapNotNull { id ->
                                                active.firstOrNull { it.id == id }
                                            }.flatMap { sec.blockLineIndexes(it) }
                                            val movedSet = movedFlat.toHashSet()
                                            val newOrder = ArrayList<Int>(all.size)
                                            var k = 0
                                            all.forEach { t ->
                                                if (t.lineIndex in movedSet) {
                                                    if (k < movedFlat.size) newOrder.add(movedFlat[k++])
                                                } else {
                                                    newOrder.add(t.lineIndex)
                                                }
                                            }
                                            if (newOrder.size == all.size &&
                                                newOrder != all.map { it.lineIndex }
                                            ) {
                                                reorderState.value(sec, newOrder)
                                            }
                                        }
                                    },
                                    onDragCancel = {
                                        dragTaskId = null
                                        dragOffset = 0f
                                        dragOrder = null
                                    }
                                )
                            }
                    ) {
                        val (subDone, subTotal) = section.subtaskProgress(task)
                        DetailTaskRow(
                            task = task,
                            subtaskDone = subDone,
                            subtaskTotal = subTotal,
                            onToggle = onToggle,
                            onEdit = { editingTask = it }
                        )
                    }
                } }
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
                            val (subDone, subTotal) = section.subtaskProgress(task)
                            CompletedTaskRow(
                                task = task,
                                subtaskDone = subDone,
                                subtaskTotal = subTotal,
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
        KeyboardSheet(onDismiss = { showAddSheet = false }, focus = addFocus, compact = true) {
            // 紧凑面板：只有一条输入框，贴在键盘正上方（水平 padding 由 KeyboardSheet 统一提供）
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = newTaskText,
                    onValueChange = { newTaskText = it },
                    placeholder = { Text("new task", color = WuSubtle, fontSize = 16.sp) },
                    // 多行输入：长文本自动换行、输入框随内容增高（最多 4 行，超出后框内滚动）
                    singleLine = false,
                    minLines = 1,
                    maxLines = 4,
                    textStyle = TextStyle(fontSize = 16.sp, color = WuTitle),
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
                        onAdd(newTaskText.toSingleLineTaskText())
                        newTaskText = ""
                        showAddSheet = false
                    }),
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(addFocus)
                )
                IconButton(
                    onClick = {
                        onAdd(newTaskText.toSingleLineTaskText())
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
            Spacer(Modifier.height(14.dp))
        }
    }

    // 底部弹出：任务编辑页（改文本 / 移动到列 / 加子项 / 删除 / 保存）
    editingTask?.let { t ->
        TaskEditSheet(
            task = t,
            sections = sections,
            currentSection = section,
            sectionColors = sectionColors,
            dotColor = dotColor,
            onDismiss = { editingTask = null },
            onRenameTask = onRenameTask,
            onMoveTask = onMoveTask,
            onAddSubtask = onAddSubtask,
            onSetNote = onSetNote,
            onDelete = onDelete,
            onSetDotColor = onSetDotColor
        )
    }

    // 底部弹出：列表编辑页（改名称 + 调色板换色 + 删除列表）
    if (showEditSheet) {
        EditSectionSheet(
            section = section,
            dotColor = dotColor,
            onDismiss = { showEditSheet = false },
            onRename = onRename,
            onDelete = onDeleteList,
            onSetDotColor = onSetDotColor
        )
    }
}

/** 底部弹出：列表编辑页（拖动条 + 删除列表 + 改名称 + 调色板换色 + 保存） */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditSectionSheet(
    section: KanbanSection,
    dotColor: Color,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit,
    onDelete: () -> Unit,
    onSetDotColor: (Int) -> Unit
) {
    var title by remember { mutableStateOf(section.title) }
    var paletteOpen by remember { mutableStateOf(false) }
    val titleFocus = remember { FocusRequester() }

    KeyboardSheet(onDismiss = onDismiss, focus = titleFocus) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 顶部拖动条装饰
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp, bottom = 4.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(WuDivider)
            )
            // 右上角：删除列表
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { onDelete(); onDismiss() }) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "删除列表",
                        tint = WuSubtle,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            // 列表名称输入（大号粗体）
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
            // 调色板行：点击调色板图标或圆点展开/收起颜色网格
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Icon(
                    Icons.Outlined.Palette,
                    contentDescription = "调色板",
                    tint = WuSubtle,
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .clickable { paletteOpen = !paletteOpen }
                )
                Spacer(Modifier.width(18.dp))
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                        .clickable { paletteOpen = !paletteOpen }
                )
            }
            HorizontalDivider(color = WuDivider, thickness = 1.dp)
            if (paletteOpen) {
                // 颜色网格：在窗口内部向下展开（窗口大小不变），缩进与圆点对齐
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.padding(start = 40.dp, top = 14.dp, bottom = 6.dp)
                ) {
                    ListPalette.chunked(8).forEach { rowColors ->
                        Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                            rowColors.forEach { c ->
                                val selected = c == dotColor.toArgb().toLong()
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(Color(c.toInt()))
                                        .clickable {
                                            onSetDotColor(c.toInt())
                                            paletteOpen = false
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (selected) {
                                        Icon(
                                            Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            // 右下角黄色对勾：保存标题修改
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                FloatingActionButton(
                    onClick = {
                        if (title.isNotBlank() && title.trim() != section.title) {
                            onRename(title.trim())
                        }
                        onDismiss()
                    },
                    containerColor = WuFab,
                    contentColor = WuTitle
                ) {
                    Icon(Icons.Filled.Check, contentDescription = "保存")
                }
            }
        }
    }
}

/** 键盘一体化底部面板：出现即拉起键盘，从底部滑入；关闭统一走 requestClose（单一下滑动画）。
 *  compact=false（默认）：大面板，顶部固定离屏顶 60dp；compact=true：高度随内容，贴在键盘正上方 */
@Composable
private fun KeyboardSheet(
    focus: FocusRequester,
    onDismiss: () -> Unit,
    /** 面板内部叠加的自绘浮层（如"移动到其他列"选择面板）。必须画在面板内容之上，
     *  且不能使用系统 Popup/DropdownMenu——那会抢占窗口焦点使键盘收起，触发误关闭 */
    overlay: (@Composable BoxScope.() -> Unit)? = null,
    /** 键盘被系统收起时是否联动关闭面板（打开内部浮层时需临时关掉） */
    imeAutoClose: Boolean = true,
    /** 紧凑模式：面板高度随内容自适应，整体贴在键盘上方（如"新建任务"输入条）；
     *  默认 false = 大面板，顶部固定离屏顶 60dp */
    compact: Boolean = false,
    /** 内容区水平内边距：默认 20dp。需要"分割线通栏"的面板（如任务编辑页）传 0.dp，自行控制各行的内边距 */
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val keyboard = LocalSoftwareKeyboardController.current
    // 优雅关闭流程：closing=true 后 → 停止拉键盘循环 + 收起键盘 + 面板下滑出屏 + 遮罩淡出，
    // 三者同步进行（约 320ms），动画结束后才真正卸载面板——彻底消除"瞬间消失"闪跳
    var closing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    // pAnim：0=完全展开贴底，1=完全下滑出屏。只由 requestClose / 进入动画驱动，
    // 不再悬挂任何手势进度，保证"一次关闭 = 一条动画"，不会停在半路
    val pAnim = remember { Animatable(1f) }
    // 唯一关闭入口：返回键 / 侧滑 / 点击遮罩 / 键盘收起，全部走这里，幂等（closing 守卫）
    val requestClose: () -> Unit = {
        if (!closing) {
            closing = true
            keyboard?.hide()
            scope.launch {
                pAnim.animateTo(1f, tween(320))
                onDismiss()
            }
        }
    }
    // 面板从组合移除时（Done/保存等直接关闭路径）兜底收起键盘
    DisposableEffect(Unit) {
        onDispose { keyboard?.hide() }
    }
    // 进入动画：面板首次出现时从屏幕底部滑入（1→0），遮罩同步淡入
    LaunchedEffect(Unit) {
        pAnim.snapTo(1f)
        pAnim.animateTo(0f, tween(320))
    }
    // 键盘/密度：组合期取值（@Composable 属性不能在协程内读取），提前声明以便返回手势逻辑引用
    val imeInsets = WindowInsets.ime
    val density = LocalDensity.current
    var imeSeen by remember { mutableStateOf(false) }
    // 用 rememberUpdatedState 读取最新开关，避免 imeAutoClose 变化时重启该协程
    val autoClose by rememberUpdatedState(imeAutoClose)
    // 返回键 / 侧滑返回：直接走统一关闭动画。
    // 不再使用 PredictiveBackHandler 做"跟手预览"——那条路径用手势进度直接写 pAnim，
    // 一旦手势被输入法吞掉、或结束后判定落空，面板就会卡在半路关不掉（故整个移除）
    BackHandler { requestClose() }
    // 键盘可见时，侧滑返回会先被输入法消费（app 收不到 back 回调），所以监听
    // "键盘从可见变为隐藏"：键盘一收起就同步关闭面板，用户一次侧滑即可退出
    LaunchedEffect(Unit) {
        snapshotFlow { imeInsets.getBottom(density) > 0 }
            .collect { visible ->
                if (visible) {
                    imeSeen = true
                } else if (imeSeen && autoClose) {
                    requestClose()
                }
            }
    }
    val fullScreenH = LocalConfiguration.current.screenHeightDp.dp
    // 遮罩透明度随面板下潜进度联动（与面板同一条动画源，天然同步，无独立动画源）
    val scrimAlpha = 0.32f * (1f - pAnim.value.coerceIn(0f, 1f))
    // 面板整体偏移：进入/关闭/侧滑都走 pAnim，单一动画源
    val panelOffsetY = fullScreenH * pAnim.value
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = scrimAlpha))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { requestClose() }
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                // 大面板：总高恒定 = 屏高 - 60dp（顶部恒离屏顶 60dp，不随键盘升降重排）；
                // 紧凑模式：高度随内容，配合内层 imePadding 使面板整体贴在键盘正上方
                .then(
                    if (compact) Modifier.wrapContentHeight()
                    else Modifier.height(fullScreenH - 60.dp)
                )
                .offset(y = panelOffsetY)
                .background(WuCard, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* 吃掉面板内点击，不关闭 */ }
        ) {
            // 出现即聚焦并拉起键盘（逐帧重试保证成功；一旦进入关闭流程立即停止，避免 show/hide 打架闪跳）
            LaunchedEffect(Unit) {
                repeat(40) {
                    if (closing) return@LaunchedEffect
                    focus.requestFocus()
                    keyboard?.show()
                    delay(16)
                }
            }
            // 键盘避让：imePadding 加在内部内容区 → 键盘升起时内容上移、面板盒子高度恒定不变；
            // 紧凑模式下内层改为随内容高度（用 fillMaxSize 会撑满整屏，面板又变回大面板）
            Column(
                modifier = Modifier
                    .then(if (compact) Modifier.fillMaxWidth() else Modifier.fillMaxSize())
                    .imePadding()
                    .padding(contentPadding)
            ) {
                content()
            }
        }
        // 内部浮层画在面板之上（不用系统 Popup，避免抢焦点导致键盘收起、误触发关闭）
        overlay?.invoke(this)
    }
}

/** 底部弹出的任务编辑页 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskEditSheet(
    task: KanbanTask,
    sections: List<KanbanSection>,
    currentSection: KanbanSection,
    /** 各列圆点颜色（列名 -> ARGB），用于"移动到其他列"面板里的圆点 */
    sectionColors: Map<String, Int>,
    dotColor: Color,
    onDismiss: () -> Unit,
    onRenameTask: (KanbanTask, String) -> Unit,
    onMoveTask: (KanbanTask, KanbanSection) -> Unit,
    onAddSubtask: (KanbanTask, String) -> Unit,
    onDelete: (KanbanTask) -> Unit,
    onSetNote: (KanbanTask, String) -> Unit,
    onSetDotColor: (Int) -> Unit
) {
    var text by remember(task.id) { mutableStateOf(task.text) }
    // "移动到其他列"选择面板：必须用应用内自绘浮层（系统 Popup 会抢焦点使键盘收起 → 面板被误关闭）
    var moveSheetOpen by remember(task.id) { mutableStateOf(false) }
    // Add items 交互状态：0=收起 1=选项菜单(Note/Subtask) 2=备注输入 3=子任务输入
    var addItemState by remember(task.id) { mutableStateOf(0) }
    // 子任务草稿行：每点一次「Add subtasks」追加一行，各行独立输入、可单独删除
    val draftSubs = remember(task.id) { mutableStateListOf<DraftSub>() }
    var draftSeq by remember(task.id) { mutableStateOf(0L) }
    var autoFocusDraftId by remember(task.id) { mutableStateOf(-1L) }
    var noteText by remember(task.id) { mutableStateOf(task.note) }
    val textFocus = remember { FocusRequester() }
    val noteFocus = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    // 该任务已有的子任务（board 里缩进在父任务之下的任务行）
    val existingSubs = currentSection.subtasksOf(task)

    // 追加一行空草稿并自动聚焦
    val addDraft: () -> Unit = {
        val d = DraftSub(draftSeq++, "")
        draftSubs.add(d)
        autoFocusDraftId = d.id
    }
    // 把所有非空草稿写入 board（逆序提交：addSubtask 每次插到父任务行下方，逆序可保持输入顺序）
    val flushDrafts: () -> Unit = {
        draftSubs.map { it.value.trim() }.filter { it.isNotEmpty() }.asReversed()
            .forEach { onAddSubtask(task, it) }
        draftSubs.clear()
    }

    // "Add items" 入口行（未展开时、以及备注行下方共用）
    val addItemsEntry: @Composable () -> Unit = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { addItemState = 1 }
                .padding(horizontal = 20.dp)
                .padding(vertical = 14.dp)
        ) {
            Icon(
                Icons.Outlined.AddCircle,
                contentDescription = null,
                tint = WuSubtle,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(14.dp))
            Text("Add items", fontSize = 15.sp, color = WuTitle)
        }
    }

    // 打开选择面板时收起键盘（面板落到底部）；从面板返回时恢复输入焦点与键盘
    LaunchedEffect(moveSheetOpen) {
        if (moveSheetOpen) {
            repeat(24) { keyboard?.hide(); delay(16) }
        } else {
            repeat(6) { textFocus.requestFocus(); keyboard?.show(); delay(16) }
        }
    }

    // 选 Note 后：光标自动聚焦到备注输入框（持续抢焦点，覆盖面板初始拉焦循环）
    LaunchedEffect(addItemState) {
        when (addItemState) {
            2 -> repeat(45) { noteFocus.requestFocus(); delay(16) }
        }
    }

    // 键盘一体化面板：出现即拉起键盘，面板随键盘同步升起
    // contentPadding = 0：分割线需要通栏（左边缘到右边缘），水平内边距由各行自己控制
    KeyboardSheet(
        // 关闭面板前先把草稿子任务落盘，避免输入内容丢失
        onDismiss = { flushDrafts(); onDismiss() },
        focus = textFocus,
        imeAutoClose = !moveSheetOpen,
        contentPadding = PaddingValues(0.dp),
        overlay = {
            if (moveSheetOpen) {
                MoveToListPanel(
                    sections = sections,
                    currentSection = currentSection,
                    sectionColors = sectionColors,
                    onPick = { target ->
                        moveSheetOpen = false
                        onMoveTask(task, target)
                        onDismiss()
                    },
                    onCancel = { moveSheetOpen = false }
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 顶部行：居中拖拽手柄 + 右上角圆形删除按钮（面板顶部第一行，离屏顶 60dp）
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(WuDivider)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(WuBackground)
                        .clickable { onDelete(task); onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "删除任务",
                        tint = WuTitle,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            // 任务标题：独占一行、自动换行（最多 3 行，超出后框内滚动）
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("任务内容", color = WuSubtle, fontSize = 20.sp) },
                // 多行输入：长任务自动换行、输入框随内容增高（最多 3 行，超出后框内滚动）
                singleLine = false,
                minLines = 1,
                maxLines = 3,
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
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = 10.dp)
                    .focusRequester(textFocus)
            )
            HorizontalDivider(color = WuDivider, thickness = 1.dp)
            // 移动到其他列
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(vertical = 10.dp)
            ) {
                Icon(
                    Icons.Outlined.DriveFileMove,
                    contentDescription = null,
                    tint = WuSubtle,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(14.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(WuAccent.copy(alpha = 0.12f))
                        .clickable { moveSheetOpen = true }
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
            }
            HorizontalDivider(color = WuDivider, thickness = 1.dp)
            // 中部内容可滚动：子任务行可能较多
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
            // 子任务区：已有子任务 + 正在输入的新子任务行 +「Add subtasks」按钮（每点一次多一行）
            if (existingSubs.isNotEmpty() || draftSubs.isNotEmpty() || addItemState == 3) {
                SubtaskSection(
                    existing = existingSubs,
                    drafts = draftSubs,
                    autoFocusId = autoFocusDraftId,
                    onAdd = addDraft,
                    onRemove = { d -> draftSubs.remove(d) }
                )
                HorizontalDivider(color = WuDivider, thickness = 1.dp)
            }
            // Add items：先弹出选项菜单（Note / Subtask），再进入对应输入行
            when (addItemState) {
                0 -> {
                    addItemsEntry()
                }
                1 -> {
                    // 选项菜单：Note（备注）与 Subtask（子任务）
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(WuBackground)
                                .clickable {
                                    noteText = task.note
                                    addItemState = 2
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Notes,
                                contentDescription = null,
                                tint = WuSubtle,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(14.dp))
                            Text("Note", fontSize = 15.sp, color = WuTitle)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(WuBackground)
                                .clickable { addItemState = 3; addDraft() }
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                        ) {
                            Icon(
                                Icons.Outlined.SubdirectoryArrowRight,
                                contentDescription = null,
                                tint = WuSubtle,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(14.dp))
                            Text("Subtask", fontSize = 15.sp, color = WuTitle)
                        }
                    }
                }
                2 -> {
                    // 备注（note）行：左侧笔记图标 + 多行输入框 + 右侧 ✕（保存并收起），
                    // 行下方保留 Add items 入口（与参考图一致）
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Notes,
                            contentDescription = null,
                            tint = WuSubtle,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(14.dp))
                        TextField(
                            value = noteText,
                            onValueChange = { noteText = it },
                            placeholder = { Text("Note", color = WuSubtle, fontSize = 15.sp) },
                            // 多行：自动换行，最多 5 行，超出后框内滚动
                            singleLine = false,
                            minLines = 1,
                            maxLines = 5,
                            textStyle = TextStyle(
                                fontSize = 15.sp,
                                color = WuTitle,
                                lineHeight = 22.sp
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = WuAccent
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(noteFocus)
                        )
                        Spacer(Modifier.width(8.dp))
                        IconButton(onClick = {
                            onSetNote(task, noteText)
                            addItemState = 0
                        }) {
                            Icon(
                                Icons.Outlined.Close,
                                contentDescription = "保存备注并收起",
                                tint = WuSubtle,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    HorizontalDivider(color = WuDivider, thickness = 1.dp)
                    addItemsEntry()
                }
                else -> {
                    // 0 = 收起；3 = 子任务编辑中（子任务区已在上方渲染）
                    addItemsEntry()
                }
            }
            }
            // 右下角黄色对勾：保存文本修改（沉底）
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.End
            ) {
                FloatingActionButton(
                    onClick = {
                        onRenameTask(task, text.toSingleLineTaskText())
                        // 保存前先把草稿子任务落盘
                        flushDrafts()
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

/** 任务编辑面板里正在输入的子任务草稿行（独立输入，可单独删除） */
private class DraftSub(val id: Long, value: String) {
    var value by mutableStateOf(value)
}

/** 取某任务正下方紧邻的缩进子任务行（board 中 indent 大于父任务的任务即其子任务） */
private fun KanbanSection.subtasksOf(task: KanbanTask): List<KanbanTask> {
    val i = tasks.indexOfFirst { it.lineIndex == task.lineIndex }
    if (i < 0) return emptyList()
    val base = task.indent
    val out = ArrayList<KanbanTask>()
    var j = i + 1
    while (j < tasks.size && tasks[j].indent > base) {
        out.add(tasks[j])
        j++
    }
    return out
}

/**
 * 列内「顶层任务」：跳过挂在其它任务下面的缩进子任务。
 * 子任务不再展开成多行，只在父任务下方以「已完成/总数」的计数形式展示。
 */
private fun KanbanSection.topLevelTasks(): List<KanbanTask> {
    val out = ArrayList<KanbanTask>()
    var i = 0
    while (i < tasks.size) {
        val t = tasks[i]
        out.add(t)
        var j = i + 1
        while (j < tasks.size && tasks[j].indent > t.indent) j++
        i = j
    }
    return out
}

/** 该顶层任务连同其子任务行的原始行号（用于拖动排序时整块移动） */
private fun KanbanSection.blockLineIndexes(task: KanbanTask): List<Int> {
    val i = tasks.indexOfFirst { it.lineIndex == task.lineIndex }
    if (i < 0) return listOf(task.lineIndex)
    val out = ArrayList<Int>()
    out.add(task.lineIndex)
    var j = i + 1
    while (j < tasks.size && tasks[j].indent > task.indent) {
        out.add(tasks[j].lineIndex)
        j++
    }
    return out
}

/** 顶层任务的子任务统计：已完成数 to 总数 */
private fun KanbanSection.subtaskProgress(task: KanbanTask): Pair<Int, Int> {
    val subs = subtasksOf(task)
    return subs.count { it.done } to subs.size
}

/**
 * 子任务区：列出该任务已有的子任务 + 正在输入的草稿行 +「Add subtasks」按钮。
 * 每点一次按钮就追加一行草稿（各行独立输入、右侧 ✕ 可删除该行）。
 */
@Composable
private fun SubtaskSection(
    existing: List<KanbanTask>,
    drafts: List<DraftSub>,
    autoFocusId: Long,
    onAdd: () -> Unit,
    onRemove: (DraftSub) -> Unit
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 10.dp)
    ) {
        // 左侧列表图标：与第一行子任务对齐
        Icon(
            Icons.Outlined.FormatListBulleted,
            contentDescription = null,
            tint = WuSubtle,
            modifier = Modifier
                .padding(top = 10.dp)
                .size(20.dp)
        )
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            // 已有子任务：只读展示（来自 board，缩进在父任务之下）
            existing.forEach { sub ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) {
                    Icon(
                        Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = WuSubtle,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        sub.text,
                        fontSize = 15.sp,
                        color = WuTitle,
                        lineHeight = 22.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            // 新子任务草稿行：多行输入 + 右侧 ✕ 删除该行
            drafts.forEach { d ->
                key(d.id) {
                    val fr = remember { FocusRequester() }
                    LaunchedEffect(autoFocusId, d.id) {
                        if (autoFocusId == d.id) fr.requestFocus()
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Outlined.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = WuSubtle,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        TextField(
                            value = d.value,
                            onValueChange = { d.value = it },
                            placeholder = { Text("Subtask", color = WuSubtle, fontSize = 15.sp) },
                            // 多行：自动换行、随内容增高（最多 5 行，超出后框内滚动）
                            singleLine = false,
                            minLines = 1,
                            maxLines = 5,
                            textStyle = TextStyle(
                                fontSize = 15.sp,
                                color = WuTitle,
                                lineHeight = 22.sp
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = WuAccent
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(fr)
                        )
                        Spacer(Modifier.width(8.dp))
                        IconButton(onClick = { onRemove(d) }) {
                            Icon(
                                Icons.Outlined.Close,
                                contentDescription = "删除该子任务行",
                                tint = WuSubtle,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
            // 「Add subtasks」按钮：每点一次追加一行
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(WuBackground)
                        .clickable { onAdd() }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = null,
                        tint = WuTitle,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Add subtasks", fontSize = 15.sp, color = WuTitle)
                }
            }
        }
    }
}

/**
 * "Select a list to move to" 底部选择面板（画在编辑面板之上）。
 *
 * 刻意不用 DropdownMenu / Popup：系统弹窗会夺走窗口焦点，使输入法收起，
 * 从而触发 KeyboardSheet 的"键盘收起即关闭"逻辑，编辑面板被误关闭。
 */
@Composable
private fun BoxScope.MoveToListPanel(
    sections: List<KanbanSection>,
    currentSection: KanbanSection,
    sectionColors: Map<String, Int>,
    onPick: (KanbanSection) -> Unit,
    onCancel: () -> Unit
) {
    // 遮罩：点击空白处返回编辑面板
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.32f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCancel() }
    )
    // 紧凑浮层卡片：四周留边、限宽限高；列表项多了在卡片内部上下滚动
    val maxPanelHeight = LocalConfiguration.current.screenHeightDp.dp * 0.46f
    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(horizontal = 26.dp, vertical = 30.dp)
            .fillMaxWidth()
            .heightIn(max = maxPanelHeight)
            .background(WuCard, RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { /* 吃掉面板内点击 */ }
    ) {
        // 顶部拖动条装饰
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 10.dp, bottom = 8.dp)
                .width(30.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(WuDivider)
        )
        Text(
            "Select a list to move to",
            fontSize = 14.sp,
            color = WuSubtle,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )
        HorizontalDivider(color = WuDivider, thickness = 1.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                // fill=false：内容少时卡片自然收缩，内容多时占满限高并在内部滚动
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 6.dp)
        ) {
            sections.forEach { s ->
                val isCurrent = s.headerLineIndex == currentSection.headerLineIndex
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(enabled = !isCurrent) { onPick(s) }
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color(sectionColors[s.title] ?: WuAccent.toArgb()))
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        s.title,
                        fontSize = 15.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        color = if (isCurrent) WuSubtle else WuTitle
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

/** 详情页任务行：点击行=打开底部编辑页；点击圆圈=勾选完成；任务下方显示备注 */
@Composable
private fun DetailTaskRow(
    task: KanbanTask,
    subtaskDone: Int,
    subtaskTotal: Int,
    onToggle: (KanbanTask) -> Unit,
    onEdit: (KanbanTask) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onEdit(task) }
            .padding(start = (task.indent * 10).dp, top = 8.dp, bottom = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onToggle(task) }
                    .padding(2.dp)
            ) {
                BigCheckCircle(done = false, size = 18.dp, strokeColor = WuTitle)
            }
            Spacer(Modifier.width(14.dp))
            Text(
                text = task.text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = WuTitle,
                lineHeight = 19.sp
            )
        }
        if (task.note.isNotBlank()) {
            Text(
                text = task.note,
                fontSize = 12.sp,
                color = WuSubtle,
                lineHeight = 16.sp,
                modifier = Modifier.padding(start = 36.dp, top = 2.dp)
            )
        }
        // 子任务不展开，只显示「图标 + 已完成数/总数」
        if (subtaskTotal > 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 36.dp, top = 3.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.FormatListBulleted,
                    contentDescription = null,
                    tint = WuSubtle,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(Modifier.width(7.dp))
                Text(
                    text = "$subtaskDone/$subtaskTotal",
                    fontSize = 12.sp,
                    color = WuSubtle,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

/** 已完成任务：灰色对勾（点=取消完成）+ 灰字（点=编辑）+ 右侧 ✕ 删除；任务下方显示备注 */
@Composable
private fun CompletedTaskRow(
    task: KanbanTask,
    subtaskDone: Int,
    subtaskTotal: Int,
    onToggle: (KanbanTask) -> Unit,
    onDelete: (KanbanTask) -> Unit,
    onEdit: (KanbanTask) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .padding(start = (task.indent * 10).dp, top = 6.dp, bottom = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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
                fontWeight = FontWeight.Medium,
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
        // 子任务不展开，只显示「图标 + 已完成数/总数」
        if (subtaskTotal > 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 40.dp, top = 3.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.FormatListBulleted,
                    contentDescription = null,
                    tint = WuDoneGrey.copy(alpha = 0.75f),
                    modifier = Modifier.size(15.dp)
                )
                Spacer(Modifier.width(7.dp))
                Text(
                    text = "$subtaskDone/$subtaskTotal",
                    fontSize = 12.sp,
                    color = WuDoneGrey.copy(alpha = 0.75f),
                    lineHeight = 15.sp
                )
            }
        }
        if (task.note.isNotBlank()) {
            Text(
                text = task.note,
                fontSize = 12.sp,
                color = WuDoneGrey.copy(alpha = 0.75f),
                lineHeight = 16.sp,
                modifier = Modifier.padding(start = 40.dp, top = 2.dp)
            )
        }
    }
}

@Composable
private fun BigCheckCircle(
    done: Boolean,
    size: Dp = 20.dp,
    strokeColor: Color = WuCircleStroke
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .then(
                if (done) Modifier.background(WuAccent)
                else Modifier.border(1.dp, strokeColor, CircleShape)
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
            // 立体感：小圆角 + 淡边框 + 底部更深的投影（"底部线更黑"=更明显的暗投影）
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = Color(0x1A000000),
                spotColor = Color(0x4D000000)
            )
            .clickable { onOpen() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = WuCard),
        border = BorderStroke(1.dp, Color(0xFFE6E6E6))
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
            // 空任务时不再留标题下方的空隙，卡片高度随之收紧
            if (section.tasks.isNotEmpty()) {
                Spacer(Modifier.height(14.dp))
                // 未完成在前、完成在底部（稳定排序保持原有相对顺序）
                // 子任务不展开：只列出顶层任务，子任务数以计数形式挂在任务下方
                section.topLevelTasks()
                    .sortedBy { it.done }
                    .forEach { task ->
                        val (subDone, subTotal) = section.subtaskProgress(task)
                        TaskRow(
                            task = task,
                            subtaskDone = subDone,
                            subtaskTotal = subTotal,
                            onToggle = onToggle,
                            onOpen = onOpen
                        )
                    }
            }
        }
    }
}

/** 主界面卡片里的任务行：点击复选框=切换完成并写回 .md；点击其余区域=打开该列详情 */
@Composable
private fun TaskRow(
    task: KanbanTask,
    subtaskDone: Int,
    subtaskTotal: Int,
    onToggle: (KanbanTask) -> Unit,
    onOpen: () -> Unit
) {
    Row(
        // 顶部对齐：任务文字可能换行，且下方还会跟一行子任务计数
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onOpen() }
            .padding(start = (task.indent * 10).dp, top = 3.dp, bottom = 3.dp)
    ) {
        // 圆圈/对勾与第一行文字大致居中对齐（文字行高 18sp ≈ 22dp，圆圈 14dp）
        Box(modifier = Modifier.padding(top = 4.dp)) {
            if (task.done) {
                // 已完成：只显示勾（灰色），不带复选框圆圈（仍可点击切换回未完成）
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = WuDoneGrey,
                    modifier = Modifier
                        .size(15.dp)
                        .clickable { onToggle(task) }
                )
            } else {
                CheckCircle(done = false, size = 14.dp, onClick = { onToggle(task) })
            }
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.text,
                fontSize = 13.sp,
                color = if (task.done) WuTaskText.copy(alpha = 0.7f) else WuTaskText,
                textDecoration = if (task.done) TextDecoration.LineThrough else null,
                lineHeight = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            // 子任务不展开，只显示「图标 + 已完成数/总数」
            if (subtaskTotal > 0) {
                Spacer(Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.FormatListBulleted,
                        contentDescription = null,
                        tint = WuSubtle,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "$subtaskDone/$subtaskTotal",
                        fontSize = 12.sp,
                        color = WuSubtle,
                        lineHeight = 14.sp
                    )
                }
            }
        }
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

/** 可选的列圆点颜色（编辑页调色板点选；其余位置点击循环切换） */
private val ListPalette = listOf(
    0xFFF2645A, 0xFFE91E63, 0xFFAB47BC, 0xFF7E57C2,
    0xFF5C6BC0, 0xFF1E88E5, 0xFF29B6F6, 0xFF26C6DA,
    0xFF00897B, 0xFF43A047, 0xFF7CB342, 0xFFC0CA33,
    0xFFFDD835, 0xFFFB8C00, 0xFFF4511E, 0xFFBDBDBD
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
    var paletteOpen by remember { mutableStateOf(false) }

    val titleFocus = remember { FocusRequester() }
    KeyboardSheet(onDismiss = onDismiss, focus = titleFocus) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
            // 调色板行：点击调色板图标或圆点展开/收起颜色网格
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
                        .clickable { paletteOpen = !paletteOpen }
                )
                Spacer(Modifier.width(18.dp))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(ListPalette[colorIdx].toInt()))
                        .clickable { paletteOpen = !paletteOpen }
                )
            }
            HorizontalDivider(color = WuDivider, thickness = 1.dp)
            if (paletteOpen) {
                // 颜色网格：在窗口内部向下展开（窗口大小不变），缩进与圆点对齐
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.padding(start = 40.dp, top = 14.dp, bottom = 6.dp)
                ) {
                    ListPalette.chunked(8).forEachIndexed { rowIdx, rowColors ->
                        Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                            rowColors.forEachIndexed { colIdx, c ->
                                val absIdx = rowIdx * 8 + colIdx
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(Color(c.toInt()))
                                        .clickable {
                                            colorIdx = absIdx
                                            paletteOpen = false
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (absIdx == colorIdx) {
                                        Icon(
                                            Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
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
