package com.wu.todo.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
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
    onAddFile: () -> Unit,
    onOpenFolder: () -> Unit
) {
    val state by viewModel.state
    val snackbarHostState = remember { SnackbarHostState() }
    var menuExpanded by remember { mutableStateOf(false) }
    // 当前打开的看板列（null 表示在看板总览）
    var openedSectionKey by remember { mutableStateOf<String?>(null) }
    // 左侧栏（文件抽屉）：用受控布尔 + 自绘抽屉，避免 Material3 ModalNavigationDrawer 在 1.2.x
    // 关闭后主界面无法响应触摸/按键的已知问题（关闭后残留 scrim 拦截事件）
    var drawerOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    // 新建任务列表的底部编辑页
    var showListSheet by remember { mutableStateOf(false) }

    // ===== 列表模式（参考图）：通栏的列区块，列头可折叠 =====
    // 默认用列表模式；顶栏可切回"瀑布流网格"总览
    var listMode by rememberSaveable { mutableStateOf(false) }
    // 已折叠的列（按列名记，列名改了会重新展开，可接受）
    var collapsedTitles by rememberSaveable { mutableStateOf(listOf<String>()) }
    // 列表模式下打开的列（列头 ⋮ 菜单里的入口：添加卡片 / 编辑列表）
    var listAddKey by remember { mutableStateOf<String?>(null) }
    var listEditKey by remember { mutableStateOf<String?>(null) }
    // 列表模式下打开的任务编辑页：列 key + 任务行号（用行号而非对象，避免数据刷新后引用过期）
    var listTaskRef by remember { mutableStateOf<Pair<String, Int>?>(null) }
    // 列表模式默认折叠所有列：每次"进入列表模式"都重新默认折叠一次；
    // 列表模式内的数据刷新不会重置用户已手动展开/折叠的状态。
    var collapsedSeededForList by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(listMode, state.sections) {
        if (listMode && state.sections.isNotEmpty()) {
            if (!collapsedSeededForList) {
                collapsedTitles = state.sections.map { it.title }
                collapsedSeededForList = true
            }
        } else if (!listMode) {
            // 切回网格后，下次再进入列表模式重新默认折叠
            collapsedSeededForList = false
        }
    }

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
            onSetSubtasks = viewModel::replaceSubtasks,
            onSetNote = viewModel::setNotes,
            onDeleteList = { viewModel.deleteSection(openedSection) },
            onSetAllDone = { done -> viewModel.setAllTasks(openedSection, done) },
            onDeleteCompleted = { viewModel.deleteCompletedTasks(openedSection) },
            onRefresh = viewModel::reload,
            onSetDotColor = { argb -> viewModel.setSectionColor(openedSection, argb) }
        )
        return
    }

    // 自定义抽屉：受控 Box + 仅在打开时组合的 scrim，关闭后没有任何拦截层残留，
    // 从而根治「打开再关闭侧栏后主界面不响应触摸/按键」的问题。
    if (drawerOpen) {
        BackHandler { drawerOpen = false }
    }
    Box(
        Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // 主界面任意位置向右滑动 → 打开左侧栏（自绘抽屉）
                val openPx = 40.dp.toPx()
                var acc = 0f
                detectHorizontalDragGestures(
                    onDragStart = { acc = 0f },
                    onHorizontalDrag = { change, amount ->
                        acc += amount
                        if (!drawerOpen && acc > openPx) {
                            drawerOpen = true
                        }
                        change.consume()
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
                    IconButton(onClick = { drawerOpen = true }) {
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
                    // 列表模式 / 网格总览 互切
                    IconButton(onClick = { listMode = !listMode }) {
                        Icon(
                            imageVector = if (listMode) Icons.Filled.GridView else Icons.Filled.List,
                            contentDescription = if (listMode) "切换到网格总览" else "切换到列表模式"
                        )
                    }
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "更多")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (listMode) "切换到网格总览" else "切换到列表模式") },
                            onClick = { menuExpanded = false; listMode = !listMode }
                        )
                        // 列表模式：一键折叠/展开所有列表（全部已折叠时变为"展开"）
                        if (listMode && state.sections.isNotEmpty()) {
                            val allCollapsed = state.sections.all { it.title in collapsedTitles }
                            DropdownMenuItem(
                                text = { Text(if (allCollapsed) "展开所有列表" else "折叠所有列表") },
                                onClick = {
                                    menuExpanded = false
                                    collapsedTitles =
                                        if (allCollapsed) emptyList()
                                        else state.sections.map { it.title }
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("添加 md 文件") },
                            onClick = { menuExpanded = false; onAddFile() }
                        )
                        DropdownMenuItem(
                            text = { Text("添加看板路径") },
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
                // 两种视图：列表模式（通栏的列区块、列头可折叠）/ 瀑布流网格总览
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (listMode) {
                        ListBoard(
                            pinnedSections = pinnedSections,
                            normalSections = normalSections,
                            collapsedTitles = collapsedTitles,
                            onToggleCollapse = { title ->
                                collapsedTitles =
                                    if (title in collapsedTitles) collapsedTitles - title
                                    else collapsedTitles + title
                            },
                            onToggleTask = viewModel::toggle,
                            onOpenTask = { section, task ->
                                listTaskRef = section.uniqueKey() to task.lineIndex
                            },
                            onOpenSection = { section -> openedSectionKey = section.uniqueKey() },
                            onAddCard = { section -> listAddKey = section.uniqueKey() },
                            onEditList = { section -> listEditKey = section.uniqueKey() },
                            onSetAllDone = { section, done -> viewModel.setAllTasks(section, done) },
                            onDeleteCompleted = { section -> viewModel.deleteCompletedTasks(section) },
                            onDeleteTask = { _, task -> viewModel.delete(task) },
                            onMoveTaskToTop = { section, task ->
                                // 必须按"整块"（任务行 + 其下方子任务/备注行）给出完整行号顺序：
                                // 目标任务块在最前，其余顶层任务块保持原相对顺序。
                                // 若只传顶层行号，本列一旦含子任务，reorderTasks 会把每个子任务行
                                // 也当成一个独立块，与"仅顶层行号"的入参数量不符 → blocks.size 校验失败 → 整列不动。
                                val tops = section.topLevelTasks()
                                val ordered = listOf(task) +
                                    tops.filter { it.lineIndex != task.lineIndex }
                                val newOrder = ordered.flatMap { section.blockLineIndexes(it) }
                                viewModel.reorderTasks(section, newOrder)
                            },
                            onDuplicateTask = { _, task -> viewModel.duplicateTask(task) },
                            onReorderSections = { newOrder -> viewModel.reorderSections(newOrder) }
                        )
                    } else {
                        // 瀑布流（StaggeredGrid）：卡片按自身高度紧密堆叠，
                        // 不再像普通 Grid 那样按行对齐而在矮卡片下方留出空白
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
    // 抽屉遮罩：仅在打开时组合，关闭后立即移除，绝不残留拦截层
    if (drawerOpen) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.32f))
                .clickable { drawerOpen = false }
                .zIndex(20f)
        )
    }
    // 抽屉面板：打开时滑入（offset 0），关闭时滑出屏幕外；动画结束（offset 回到 -360dp）即移出组合
    val sheetOffset by animateDpAsState(
        targetValue = if (drawerOpen) 0.dp else (-360).dp,
        animationSpec = tween(durationMillis = 280),
        label = "drawerOffset"
    )
    if (drawerOpen || sheetOffset != (-360).dp) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(320.dp)
                .offset(x = sheetOffset)
                .background(WuCard)
                .zIndex(21f)
                .clickable { }
        ) {
            BoardDrawerContent(
                files = state.drawerFiles,
                currentUri = state.fileUri,
                onPick = { uri ->
                    openedSectionKey = null
                    viewModel.chooseFolderFile(uri)
                    drawerOpen = false
                },
                onOpenFolder = {
                    drawerOpen = false
                    onOpenFolder()
                }
            )
        }
    }
    }

    // ===== 列表模式下的弹层 =====

    // 添加卡片（底部紧凑输入页，贴键盘）
    listAddKey?.let { key ->
        val sec = state.sections.firstOrNull { it.uniqueKey() == key }
        if (sec != null) {
            AddCardSheet(
                onDismiss = { listAddKey = null },
                onAdd = { text -> viewModel.addTask(sec, text) }
            )
        }
    }

    // 编辑列表（改名 / 换色 / 删除）
    listEditKey?.let { key ->
        val sec = state.sections.firstOrNull { it.uniqueKey() == key }
        if (sec != null) {
            EditSectionSheet(
                section = sec,
                dotColor = Color(state.sectionColors[sec.title] ?: WuAccent.toArgb()),
                onDismiss = { listEditKey = null },
                onRename = { newTitle -> viewModel.renameSection(sec, newTitle) },
                onDelete = {
                    viewModel.deleteSection(sec)
                    listEditKey = null
                },
                onSetDotColor = { argb -> viewModel.setSectionColor(sec, argb) }
            )
        }
    }

    // 任务编辑页（点列表模式里的任务卡片）
    listTaskRef?.let { ref ->
        val sec = state.sections.firstOrNull { it.uniqueKey() == ref.first }
        val t = sec?.tasks?.firstOrNull { it.lineIndex == ref.second }
        if (sec != null && t != null) {
            TaskEditSheet(
                task = t,
                sections = state.sections,
                currentSection = sec,
                sectionColors = state.sectionColors,
                dotColor = Color(state.sectionColors[sec.title] ?: WuAccent.toArgb()),
                onDismiss = { listTaskRef = null },
                onRenameTask = viewModel::renameTask,
                onMoveTask = { task, target -> viewModel.moveTask(task, target, sec) },
                onSetSubtasks = viewModel::replaceSubtasks,
                onSetNote = viewModel::setNotes,
                onDelete = { task ->
                    viewModel.delete(task)
                    listTaskRef = null
                },
                onSetDotColor = { argb -> viewModel.setSectionColor(sec, argb) }
            )
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
    onSetSubtasks: (KanbanTask, List<Pair<String, Boolean>>) -> Unit,
    onSetNote: (KanbanTask, List<String>) -> Unit,
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
            onSetSubtasks = onSetSubtasks,
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
    /** 面板出现时是否自动聚焦并拉起键盘。false = 打开面板不弹键盘（如任务编辑页） */
    autoFocus: Boolean = true,
    /** 大面板顶部距离屏幕上沿的间距（越小面板越高、越靠上） */
    topGap: Dp = 60.dp,
    /** 键盘弹起时，内容区底部再留出的安全余量（防止被输入法键盘上方那行工具栏/按键压住） */
    imeExtraBottom: Dp = 0.dp,
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
                    else Modifier.height(fullScreenH - topGap)
                )
                .offset(y = panelOffsetY)
                .background(WuCard, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* 吃掉面板内点击，不关闭 */ }
        ) {
            // 出现即聚焦并拉起键盘（逐帧重试保证成功；一旦进入关闭流程立即停止，避免 show/hide 打架闪跳）
            // autoFocus=false 时完全不碰焦点与键盘：打开面板保持键盘收起，用户点输入框才弹
            if (autoFocus) {
                LaunchedEffect(Unit) {
                    repeat(40) {
                        if (closing) return@LaunchedEffect
                        runCatching { focus.requestFocus() }
                        keyboard?.show()
                        delay(16)
                    }
                }
            }
            // 键盘避让：imePadding 加在内部内容区 → 键盘升起时内容上移、面板盒子高度恒定不变；
            // 紧凑模式下内层改为随内容高度（用 fillMaxSize 会撑满整屏，面板又变回大面板）
            Column(
                modifier = Modifier
                    .then(if (compact) Modifier.fillMaxWidth() else Modifier.fillMaxSize())
                    .imePadding()
                    // 键盘弹起时再留一段安全距离：部分输入法键盘上方还有一行工具栏，
                    // ime insets 未必全部覆盖，留出余量避免最后一行内容被它压住
                    .padding(bottom = if (imeInsets.getBottom(density) > 0) imeExtraBottom else 0.dp)
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
    onSetSubtasks: (KanbanTask, List<Pair<String, Boolean>>) -> Unit,
    onDelete: (KanbanTask) -> Unit,
    onSetNote: (KanbanTask, List<String>) -> Unit,
    onSetDotColor: (Int) -> Unit
) {
    var text by remember(task.id) { mutableStateOf(task.text) }
    // "移动到其他列"选择面板：必须用应用内自绘浮层（系统 Popup 会抢焦点使键盘收起 → 面板被误关闭）
    var moveSheetOpen by remember(task.id) { mutableStateOf(false) }
    // Add items 交互状态：0=收起 1=选项菜单(Note/Subtask) 2=备注输入 3=子任务输入
    var addItemState by remember(task.id) { mutableStateOf(0) }
    // 子任务行列表：已有子任务（只读，key 为负）+ 本次新增的草稿行（可编辑，key 为正），
    // 统一按显示顺序排列、整体可拖动排序
    val subRows = remember(task.id) {
        val initial = mutableStateListOf<SubRow>()
        currentSection.subtasksOf(task).forEachIndexed { i, sub ->
            initial.add(SubRow(-(i + 1).toLong(), sub, sub.text, committed = true, done = sub.done))
        }
        initial
    }
    // 草稿行 key 用正数（1,2,3…），已有子任务用负数（-1,-2…），两类 key 永不冲突。
    // autoFocusDraftKey 的初值必须是"不可能与任何行相等"的值：用 0（key 里没有 0），
    // 否则会误判成"需要自动聚焦某行"，对没有输入框的只读行调用未绑定的 FocusRequester → 崩溃。
    var subKeySeq by remember(task.id) { mutableStateOf(0L) }
    var autoFocusDraftKey by remember(task.id) { mutableStateOf(0L) }
    val textFocus = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    // 多条备注：每条独立成行，可增删改；key 与子任务同套规则（已有负数 / 新增正数），永不与子任务撞 key
    val noteItems = remember(task.id) {
        mutableStateListOf<NoteItem>().apply {
            task.notes.forEachIndexed { i, n -> add(NoteItem(-(i + 1).toLong(), n, committed = true)) }
        }
    }
    var noteSeq by remember(task.id) { mutableStateOf(0L) }
    var autoFocusNoteKey by remember(task.id) { mutableStateOf(0L) }
    val addNote: () -> Unit = {
        noteSeq += 1
        val item = NoteItem(noteSeq, "", committed = false)
        noteItems.add(item)
        autoFocusNoteKey = item.key
    }
    // 删除子任务/备注时，被删除的输入框会从组合中移除 → 输入法自动收起 →
    // KeyboardSheet 的"键盘收起即关闭面板"联动会误把整个编辑面板关掉。
    // 删除后短暂抑制该联动，保证「点删除不退出编辑窗口」。
    var suppressImeClose by remember(task.id) { mutableStateOf(false) }
    // 每次需要"抑制"时让令牌自增：LaunchedEffect 依令牌重启计时。
    // 若只用一个 Boolean，重复置 true 不会重启旧计时，连续两次删除/交接时第二次就失去保护。
    var imeSuppressToken by remember(task.id) { mutableStateOf(0) }
    LaunchedEffect(imeSuppressToken) {
        if (imeSuppressToken > 0) {
            suppressImeClose = true
            delay(900)
            suppressImeClose = false
        }
    }
    // 删除某行、或回车把编辑焦点交接给下一条 note 时，当前输入框会离开组合/失焦 → 输入法短暂收起。
    // KeyboardSheet 有"键盘收起即关面板"的联动，所以这些时刻都要临时抑制它，保证面板不被误关。
    val holdPanelOpen: () -> Unit = { imeSuppressToken += 1 }
    val removeNote: (NoteItem) -> Unit = { item ->
        holdPanelOpen()
        noteItems.remove(item)
    }
    val flushNotes: () -> Unit = { onSetNote(task, noteItems.map { it.value.trim() }) }

    // 追加一行空草稿并自动聚焦
    val addDraft: () -> Unit = {
        subKeySeq += 1
        val row = SubRow(subKeySeq, null, "")
        subRows.add(row)
        autoFocusDraftKey = row.key
    }
    // 把当前子任务列表（含顺序与勾选状态）整体写回 board
    val flushSubs: () -> Unit = {
        onSetSubtasks(task, subRows.map { it.value.trim() to it.done })
    }

    // "Add items" 入口（紧凑型）：放在列名那一行的最右侧，点击展开/收起选项菜单。
    // 菜单项本身在列名行下方内联展开（见下方布局）
    val addItemsEntry: @Composable () -> Unit = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .clickable { addItemState = if (addItemState == 1) 0 else 1 }
                .padding(horizontal = 8.dp, vertical = 5.dp)
        ) {
            Icon(
                Icons.Outlined.AddCircle,
                contentDescription = "Add items",
                tint = WuSubtle,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text("Add items", fontSize = 13.sp, color = WuSubtle)
        }
    }

    // 备注重排：按拖拽得到的 key 顺序重排 noteItems（flushNotes 写回时即按此顺序落盘）。
    val reorderNotes: (List<Long>) -> Unit = { order ->
        val map = noteItems.associateBy { it.key }
        noteItems.clear()
        noteItems.addAll(order.mapNotNull { map[it] })
    }

    // 打开选择面板时收起键盘（面板落到底部）；从面板回来后恢复输入焦点（首次打开面板不弹键盘）
    var movePanelOpened by remember(task.id) { mutableStateOf(false) }
    LaunchedEffect(moveSheetOpen) {
        if (moveSheetOpen) {
            movePanelOpened = true
            repeat(24) { keyboard?.hide(); delay(16) }
        } else if (movePanelOpened) {
            repeat(6) { runCatching { textFocus.requestFocus() }; keyboard?.show(); delay(16) }
        }
    }

    // 新增备注后：光标自动聚焦到对应输入框（持续抢焦点，覆盖面板初始拉焦循环）
    LaunchedEffect(autoFocusNoteKey) {
        if (autoFocusNoteKey != 0L) {
            repeat(45) {
                noteItems.firstOrNull { it.key == autoFocusNoteKey }?.fr?.let { runCatching { it.requestFocus() } }
                delay(16)
            }
        }
    }

    // 键盘一体化面板：出现即拉起键盘，面板随键盘同步升起
    // contentPadding = 0：分割线需要通栏（左边缘到右边缘），水平内边距由各行自己控制
    KeyboardSheet(
        // 关闭面板前先把子任务列表落盘，避免输入内容丢失
        onDismiss = { flushNotes(); flushSubs(); onDismiss() },
        focus = textFocus,
        // 删除行时 suppressImeClose 短暂为 true，避免键盘收起连带关闭面板
        imeAutoClose = !moveSheetOpen && !suppressImeClose,
        contentPadding = PaddingValues(0.dp),
        // 打开编辑面板不弹键盘（点输入框才弹）；面板整体再上移 10dp（顶部间距 40 → 30）
        autoFocus = false,
        topGap = 30.dp,
        // 键盘弹起时内容底部再留 16dp，避免最后一行被输入法键盘上方那行按键压住
        imeExtraBottom = 16.dp,
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
            // 移动到其他列 + 右侧「Add items」入口
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
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
                // 把「Add items」挪到列名这一行的最右边：点击展开/收起 Note / Subtask 选项菜单
                Spacer(Modifier.weight(1f))
                addItemsEntry()
            }
            // 选项菜单：Note（备注）与 Subtask（子任务），紧贴在列名行下方展开
            if (addItemState == 1) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(WuBackground)
                            .clickable {
                                addNote()
                                addItemState = 0
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
                            .clickable {
                                addItemState = 3
                                addDraft()
                            }
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
            HorizontalDivider(color = WuDivider, thickness = 1.dp)
            // 中部：可滚动内容 + 悬浮保存按钮。
            // 保存按钮改成真正悬浮在内容之上（不再独占一行高度），内容区多出约 68dp
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                // 备注区：md 中写在任务行下方（`- 内容`），位置在子任务之前，与看板文件结构一致
                if (noteItems.isNotEmpty()) {
                    NoteSection(
                        items = noteItems,
                        onAdd = addNote,
                        onRemove = removeNote,
                        onReorder = reorderNotes,
                        onHandoff = holdPanelOpen
                    )
                    HorizontalDivider(color = WuDivider, thickness = 1.dp)
                }
                // 子任务区：已有子任务 + 新增草稿行（统一列表，可拖动排序）+「Add subtasks」按钮
                if (subRows.isNotEmpty() || addItemState == 3) {
                    SubtaskSection(
                        rows = subRows,
                        autoFocusKey = autoFocusDraftKey,
                        onAdd = addDraft,
                        onRemove = { row ->
                            holdPanelOpen()
                            subRows.remove(row)
                        },
                        onReorder = { finalOrder ->
                            // 松手后才按最终顺序一次性重排真实数据（拖拽过程中不碰真实列表，避免重组崩溃）
                            val map = subRows.associateBy { it.key }
                            val reordered = finalOrder.mapNotNull { map[it] }
                            if (reordered.size == subRows.size) {
                                subRows.clear()
                                subRows.addAll(reordered)
                            }
                        },
                        onHandoff = holdPanelOpen
                    )
                    HorizontalDivider(color = WuDivider, thickness = 1.dp)
                }
                // 「Add items」入口与选项菜单已上移到列名那一行的最右侧（见上方布局）
                    // 悬浮保存按钮的让位空间：内容滚到底时，最后一项不会被按钮盖住
                    Spacer(Modifier.height(76.dp))
                }
                // 右下角黄色对勾：真正悬浮在内容之上（不占布局高度）
                FloatingActionButton(
                    onClick = {
                        onRenameTask(task, text.toSingleLineTaskText())
                        // 保存前先把备注与子任务列表落盘
                        flushNotes()
                        flushSubs()
                        onDismiss()
                    },
                    containerColor = WuFab,
                    contentColor = WuTitle,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 20.dp, bottom = 12.dp)
                ) {
                    Icon(Icons.Filled.Check, contentDescription = "保存")
                }
            }
        }
    }
}

/**
 * 任务编辑面板里的一行子任务。
 * [existing] != null 表示来自 board 的已有子任务（只读文本）；为 null 表示本次新增的草稿行（可编辑、可删除）。
 */
private class SubRow(
    val key: Long,
    val existing: KanbanTask?,
    value: String,
    committed: Boolean = false,
    done: Boolean = false
) {
    var value by mutableStateOf(value)
    // 是否已确认（回车提交后 / 来自 board 的已有子任务）：确认后不再显示输入框，改为只读文本
    var committed by mutableStateOf(committed)
    // 子任务勾选状态：已有子任务取 board 中的值；新增草稿默认未完成。
    // 改动只在面板内即时生效，点保存/关闭时才随 flushSubs 整块写回 .md。
    var done by mutableStateOf(done)
}

/** 一条备注：与子任务同套 key 规则（已有负数 / 新增正数），各自持有 FocusRequester 便于新增后自动聚焦 */
private class NoteItem(
    val key: Long,
    value: String,
    committed: Boolean = false
) {
    var value by mutableStateOf(value)
    // 已有备注初始为"只读"（committed=true），点击文本才进入编辑态；
    // 新增的草稿备注 committed=false，天然处于编辑态。与子任务同一套模型。
    var committed by mutableStateOf(committed)
    val fr = FocusRequester()
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
 * 子任务区：统一列出「已有子任务（只读）+ 新增草稿行（可编辑）」，
 * 每行右侧是拖动排序手柄（≡），按住上下拖动即可调整顺序；
 * 底部是「+」按钮（每点一次追加一行草稿）。
 * 点击只读文本即进入编辑（光标落在文字末尾）；回车完成本条并把焦点交给下一条（草稿则续加一行）。
 */
@Composable
private fun SubtaskSection(
    rows: List<SubRow>,
    autoFocusKey: Long,
    onAdd: () -> Unit,
    onRemove: (SubRow) -> Unit,
    onReorder: (List<Long>) -> Unit,
    /** 编辑焦点在行间交接（回车跳到下一条）时调用：临时抑制键盘收起关面板的联动 */
    onHandoff: () -> Unit = {}
) {
    // 各行高度（拖动时按"越过相邻行中线即交换"的模型计算）。
    // 普通 Map（非 Compose 状态）：onGloballyPositioned 回调里只写普通 Map，不再触发重组，
    // 避免"写入→重组→重新布局→再次写入"的无限重组循环导致崩溃（子任务编辑面板一交互就闪退的根因）。
    val rowHeights = remember { mutableMapOf<Long, Float>() }
    var dragKey by remember { mutableStateOf<Long?>(null) }
    var dragDy by remember { mutableFloatStateOf(0f) }
    var dragFrom by remember { mutableStateOf(-1) }
    var dragTo by remember { mutableStateOf(-1) }
    // 松手后正在回落到目标槽位的行：保留最后一帧偏移再用动画收敛到 0，避免"落位一跳"
    var settleKey by remember { mutableStateOf<Long?>(null) }
    // 正在编辑的子任务行 key：点击只读文本即进入编辑态；编辑态下该行显示输入框 + 右侧 ✕。
    // 草稿行（新增且未确认）天然处于编辑态
    var editingKey by remember { mutableStateOf<Long?>(null) }
    // 上一次回车的时间戳：软键盘"完成"与硬件回车可能各回调一次，300ms 内只认第一次，避免连跳两行
    var lastEnterAt by remember { mutableStateOf(0L) }
    val keyboard = LocalSoftwareKeyboardController.current
    // 固定顺序（拖拽中不变）与最新回调：手势在 pointerInput 协程里执行，用 rememberUpdatedState 取值避免陈旧
    val order = rows.map { it.key }
    val orderState = rememberUpdatedState(order)
    val reorderState = rememberUpdatedState(onReorder)
    // 正在移动的行（拖动中或松手回落中）的纵向偏移：拖动中 snap 跟随手指保证跟手；
    // 松手后 tween 收敛回 0（与真实顺序重排发生在同一帧 → 视觉位置无缝衔接）
    val movingKey = dragKey ?: settleKey
    val movingDy by animateFloatAsState(
        targetValue = if (dragKey != null) dragDy else 0f,
        animationSpec = if (dragKey != null) snap() else tween(durationMillis = 160),
        label = "movingDy"
    )

    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 6.dp)
    ) {
        // 左侧列表图标：整体下移，使其竖向中心与该行文字齐平（行内容 36dp 居中）
        Icon(
            Icons.Outlined.FormatListBulleted,
            contentDescription = null,
            tint = WuSubtle,
            modifier = Modifier
                .padding(top = 11.dp)
                .size(20.dp)
        )
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            val rowByKey = rows.associateBy { it.key }
            // 整个拖拽过程中顺序保持不变（松手才重排）；被拖行"越过"的行靠 shift 位移让位
            val dragHeight = rowHeights[dragKey] ?: 0f
            order.forEachIndexed { index, key ->
                val row = rowByKey[key] ?: return@forEachIndexed
                // 回车：完成本条子任务的编辑。
                // 草稿行（新增未确认）：确认并续加一行；已有子任务：焦点交给下一条（末条则退出编辑）
                val onEnter: () -> Unit = enter@{
                    // 软键盘"完成"与硬件回车可能各回调一次 → 300ms 内只认第一次
                    val now = System.currentTimeMillis()
                    if (now - lastEnterAt < 300L) return@enter
                    lastEnterAt = now
                    val i = order.indexOf(row.key)
                    val nextKey = if (i in 0 until order.size - 1) order[i + 1] else null
                    if (row.existing == null) {
                        // 草稿行：回车确认并追加下一行（保持连续录入）；空草稿则退出编辑
                        if (row.value.trim().isNotEmpty()) {
                            row.value = row.value.trim()
                            row.committed = true
                            onAdd()
                        } else {
                            editingKey = null
                        }
                    } else if (nextKey != null) {
                        onHandoff()
                        editingKey = nextKey
                    } else {
                        // 已经是最后一条子任务：回车先完成本条编辑（trim 落盘），再在末尾新增一条子任务继续录入
                        row.value = row.value.trim()
                        onHandoff()
                        onAdd()
                    }
                }
                key(row.key) {
                    val dragging = dragKey == row.key
                    // 让位位移：向下拖动时 (dragFrom, dragTo] 的行整体上移一个行高；
                    // 向上拖动时 [dragTo, dragFrom) 的行整体下移。用动画过渡 → 不再整块瞬移。
                    val shiftTarget = when {
                        dragKey == null || dragging -> 0f
                        dragFrom < dragTo && index > dragFrom && index <= dragTo -> -dragHeight
                        dragFrom > dragTo && index in dragTo until dragFrom -> dragHeight
                        else -> 0f
                    }
                    val shift by animateFloatAsState(
                        targetValue = shiftTarget,
                        // 拖拽中平滑让位；松手瞬间 snap 归零，与真实重排同帧 → 视觉位置不跳
                        animationSpec = if (dragKey == null) snap() else tween(durationMillis = 150),
                        label = "shift"
                    )
                    // 编辑态：用户点击进入的行，或新增且未确认的草稿行（天然可编辑）
                    val isEditing = editingKey == row.key || (row.existing == null && !row.committed)
                    val fr = remember { FocusRequester() }
                    // 进入编辑态即聚焦：fr 只在编辑态（BasicTextField 存在）时才被绑定，
                    // 不会在只读行上调用未绑定 FocusRequester → 避免历史闪退
                    LaunchedEffect(isEditing, row.key) {
                        if (isEditing) {
                            runCatching { fr.requestFocus() }
                            // 回车切到下一条子任务时，焦点在两行间交接：主动拉起键盘一次，
                            // 避免中间"无输入框聚焦"空档导致键盘收起（进而关面板）
                            keyboard?.show()
                        }
                    }
                    // 纵向偏移：被拖行 / 松手回落中的行用 movingDy（拖动中跟手、松手后收敛）；
                    // 其余行用 shift 平滑让位
                    val translation = if (dragging || settleKey == row.key) movingDy else shift
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .zIndex(if (dragging) 1f else 0f)
                            .graphicsLayer { translationY = translation }
                            .onGloballyPositioned { c ->
                                rowHeights[row.key] = c.size.height.toFloat()
                            }
                            // 拖动时：不要卡片框/阴影，仅整体轻微半透明，保持"跟手"的轻量手感
                            .alpha(if (dragging) 0.92f else 1f)
                            .padding(vertical = 3.dp)
                    ) {
                        // 子任务勾选框：点击切换"已完成/未完成"，改动随保存/关闭写回 .md
                        // 未完成：空心方块（可点选完成）；已完成：不要方块，只显示一个灰色对勾。
                        Icon(
                            imageVector = if (row.done) Icons.Outlined.Check else Icons.Outlined.CheckBoxOutlineBlank,
                            contentDescription = if (row.done) "标记为未完成" else "标记为已完成",
                            tint = WuSubtle,
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .clickable { row.done = !row.done }
                        )
                        // 复选框与文字间距收窄，文字与复选框挨得更近（之前 12dp 偏宽）
                        Spacer(Modifier.width(8.dp))
                        if (isEditing) {
                            // 编辑态：可编辑输入框；回车即确认/退出（草稿续行，已有子任务退出编辑）
                            // 用 BasicTextField 而非 TextField：后者最小高度固定 56dp，会让行间距过大
                            // 显式持有 TextFieldValue：初始 selection 落在文本末尾 —— 点击（或回车接力）
                            // 进入编辑态时光标就在文字最后。state 随"编辑态分支"进出自动重置，
                            // 每次重新进入编辑都从当前文本末尾开始。
                            var field by remember(row.key) {
                                mutableStateOf(
                                    TextFieldValue(row.value, TextRange(row.value.length, row.value.length))
                                )
                            }
                            BasicTextField(
                                value = field,
                                onValueChange = { field = it; row.value = it.text },
                                textStyle = TextStyle(
                                    fontSize = 15.sp,
                                    color = if (row.done) WuSubtle else WuTitle,
                                    lineHeight = 22.sp,
                                    textDecoration = if (row.done) TextDecoration.LineThrough else null
                                ),
                                // 多行：自动换行、随内容增高（最多 5 行，超出后框内滚动）
                                singleLine = false,
                                maxLines = 5,
                                cursorBrush = SolidColor(WuAccent),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { onEnter() }),
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(fr)
                                    .onPreviewKeyEvent { e ->
                                        // 回车：草稿确认并续行；已有子任务则把焦点交给下一条（软/硬键盘换行键都吃掉）
                                        if (e.type == KeyEventType.KeyDown &&
                                            (e.key == Key.Enter || e.key == Key.NumPadEnter)
                                        ) {
                                            onEnter()
                                            true
                                        } else {
                                            false
                                        }
                                    },
                                decorationBox = { inner ->
                                    Box {
                                        if (row.value.isEmpty()) {
                                            Text(
                                                "Subtask",
                                                color = WuSubtle,
                                                fontSize = 15.sp,
                                                lineHeight = 22.sp
                                            )
                                        }
                                        inner()
                                    }
                                }
                            )
                        } else {
                            // 只读文本：点击即进入编辑态，方便直接修改已有子任务
                            Text(
                                text = row.value,
                                fontSize = 15.sp,
                                color = if (row.done) WuSubtle else WuTitle,
                                textDecoration = if (row.done) TextDecoration.LineThrough else null,
                                lineHeight = 22.sp,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { editingKey = row.key }
                            )
                        }
                        // 编辑态：右侧的排序手柄（≡）变成删除符号（✕），点 ✕ 删除该行；
                        // 非编辑态：显示 ≡ 拖动排序。两者都占 36dp，宽度一致 → 文字区宽度不变、符号对齐。
                        if (isEditing) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .clickable { onRemove(row) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.Close,
                                    contentDescription = "删除该子任务行",
                                    tint = WuSubtle,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        } else {
                            // 拖动排序手柄：按住上下拖动调整子任务顺序。
                            // 用 36dp 的 Box 包住 22dp 图标，扩大可点区域，更容易抓住。
                            Box(
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .size(36.dp)
                                    .pointerInput(row.key) {
                                    detectDragGestures(
                                        onDragStart = {
                                            val ord = orderState.value
                                            val from = ord.indexOf(row.key)
                                            settleKey = null
                                            dragDy = 0f
                                            dragFrom = from
                                            dragTo = from
                                            dragKey = row.key
                                        },
                                        onDrag = { change, amount ->
                                            change.consume()
                                            dragDy += amount.y
                                            val ord = orderState.value
                                            // 按行高累计推算目标槽位：越过相邻行中线才算换位
                                            var acc = 0f
                                            var t = dragFrom
                                            if (dragDy > 0f) {
                                                while (t < ord.size - 1) {
                                                    val h = rowHeights[ord[t + 1]] ?: 0f
                                                    if (h > 0f && dragDy > acc + h / 2f) {
                                                        acc += h
                                                        t++
                                                    } else break
                                                }
                                            } else if (dragDy < 0f) {
                                                while (t > 0) {
                                                    val h = rowHeights[ord[t - 1]] ?: 0f
                                                    if (h > 0f && -dragDy > acc + h / 2f) {
                                                        acc += h
                                                        t--
                                                    } else break
                                                }
                                            }
                                            dragTo = t
                                        },
                                        onDragEnd = {
                                            val ord = orderState.value
                                            val from = dragFrom
                                            val to = dragTo
                                            dragKey = null
                                            dragDy = 0f
                                            dragFrom = -1
                                            dragTo = -1
                                            if (from in ord.indices && to in ord.indices && from != to) {
                                                // 松手才把最终顺序一次性写回真实列表；被拖行标记为"回落中"，
                                                // 用最后一帧偏移平滑收进目标槽位
                                                val newOrder = ord.toMutableList().also { it.add(to, it.removeAt(from)) }
                                                settleKey = row.key
                                                reorderState.value(newOrder)
                                            } else {
                                                settleKey = null
                                            }
                                        },
                                        onDragCancel = {
                                            dragKey = null
                                            dragDy = 0f
                                            dragFrom = -1
                                            dragTo = -1
                                            settleKey = null
                                        }
                                    )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.DragHandle,
                                    contentDescription = "拖动排序",
                                    tint = WuSubtle,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
            // 「+」按钮：每点一次追加一行子任务（取代原 "Add subtasks" 文字按钮）
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 10.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(WuBackground)
                        .clickable { onAdd() }
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "添加子任务",
                        tint = WuTitle,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * 备注区：与子任务同套拖动排序模型。
 * 每条 note 一行：整区一个笔记图标 + 可编辑文本（左端与子任务复选框左端对齐）
 * + ✕/≡（编辑态显示删除 ✕，非编辑态显示拖动 ≡）。
 * 点击文本进入编辑态（光标落在文字末尾）；按住 ≡ 上下拖动调整顺序；
 * 回车完成本条并把编辑焦点交给下一条（末条则新增一条，保持连续录入）。
 * 写回 md 时每条是独立的缩进无序列表项（`- 内容`），排在子任务之前，与文件结构一致。
 */
@Composable
private fun NoteSection(
    items: List<NoteItem>,
    onAdd: () -> Unit,
    onRemove: (NoteItem) -> Unit,
    onReorder: (List<Long>) -> Unit,
    /** 编辑焦点在行间交接（回车跳到下一条）时调用：临时抑制键盘收起关面板的联动 */
    onHandoff: () -> Unit = {}
) {
    // 各行高度（拖动时按"越过相邻行中线即换位"的模型计算），普通 Map 避免重组循环
    val rowHeights = remember { mutableMapOf<Long, Float>() }
    // 正在被手指拖动的行；dragFrom/dragTo 是它在"固定顺序"里的起始槽位与当前目标槽位。
    // 拖拽过程中不改真实列表顺序，只让其他行用动画位移"让位"，松手才一次性写回 ——
    // 其他行是平滑移动而不是整块瞬移，"拖动时跳动"的问题由此消除。
    var dragKey by remember { mutableStateOf<Long?>(null) }
    var dragDy by remember { mutableFloatStateOf(0f) }
    var dragFrom by remember { mutableStateOf(-1) }
    var dragTo by remember { mutableStateOf(-1) }
    // 松手后正在回落到目标槽位的行：保留最后一帧偏移再用动画收敛到 0，避免"落位一跳"
    var settleKey by remember { mutableStateOf<Long?>(null) }
    // 正在编辑（已聚焦）的 note：编辑态下右侧的 ≡ 拖动手柄变成 ✕ 删除符号，与子任务行为一致
    var editingKey by remember { mutableStateOf<Long?>(null) }
    // 上一次回车的时间戳：软键盘"完成"与硬件回车可能各触发一次回调，
    // 300ms 内只认第一次，避免一次回车连跳两条 note
    var lastEnterAt by remember { mutableStateOf(0L) }
    val keyboard = LocalSoftwareKeyboardController.current
    // 固定顺序（拖拽中不变）与最新回调：手势在 pointerInput 协程里执行，用 rememberUpdatedState 取值避免陈旧
    val order = items.map { it.key }
    val orderState = rememberUpdatedState(order)
    val reorderState = rememberUpdatedState(onReorder)
    // 正在移动的行（拖动中或松手回落中）的纵向偏移：拖动中 snap 跟随手指保证跟手；
    // 松手后 tween 收敛回 0（与真实顺序重排发生在同一帧 → 视觉位置无缝衔接）
    val movingKey = dragKey ?: settleKey
    val movingDy by animateFloatAsState(
        targetValue = if (dragKey != null) dragDy else 0f,
        animationSpec = if (dragKey != null) snap() else tween(durationMillis = 160),
        label = "movingDy"
    )

    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 6.dp)
    ) {
        Icon(
            Icons.Outlined.Notes,
            contentDescription = null,
            tint = WuSubtle,
            // 图标整体下移，使其竖向中心与该行文字（行高 22sp，行内容 36dp 居中）齐平
            modifier = Modifier
                .padding(top = 11.dp)
                .size(20.dp)
        )
        // 与子任务区的间距保持一致（14dp），保证两个区的图标起点一致
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            val itemByKey = items.associateBy { it.key }
            // 整个拖拽过程中顺序保持不变（松手才重排）；被拖行"越过"的行靠 shift 位移让位
            val dragHeight = rowHeights[dragKey] ?: 0f
            order.forEachIndexed { index, key ->
                val item = itemByKey[key] ?: return@forEachIndexed
                key(item.key) {
                    val dragging = dragKey == item.key
                    // 让位位移：向下拖动时 (dragFrom, dragTo] 的行整体上移一个行高；
                    // 向上拖动时 [dragTo, dragFrom) 的行整体下移。用动画过渡 → 不再整块瞬移。
                    val shiftTarget = when {
                        dragKey == null || dragging -> 0f
                        dragFrom < dragTo && index > dragFrom && index <= dragTo -> -dragHeight
                        dragFrom > dragTo && index in dragTo until dragFrom -> dragHeight
                        else -> 0f
                    }
                    val shift by animateFloatAsState(
                        targetValue = shiftTarget,
                        // 拖拽中平滑让位；松手瞬间 snap 归零，与真实重排同帧 → 视觉位置不跳
                        animationSpec = if (dragKey == null) snap() else tween(durationMillis = 150),
                        label = "shift"
                    )
                    // 编辑态：用户点击进入的备注行，或新增且未确认的草稿备注（天然可编辑）
                    val isEditing = editingKey == item.key || !item.committed
                    // 进入编辑态即聚焦：fr 只在编辑态（BasicTextField 存在）时才绑定，避免崩溃
                    LaunchedEffect(isEditing, item.key) {
                        if (isEditing) {
                            runCatching { item.fr.requestFocus() }
                            // 回车切到下一条 note 时，输入焦点在两行之间交接：
                            // 主动把键盘再拉起一次，避免中间出现"无输入框聚焦"的空档导致键盘收起（进而关面板）
                            keyboard?.show()
                        }
                    }
                    // 纵向偏移：被拖行 / 松手回落中的行用 movingDy（拖动中跟手、松手后收敛）；
                    // 其余行用 shift 平滑让位
                    val translation = if (dragging || settleKey == item.key) movingDy else shift
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .zIndex(if (dragging) 1f else 0f)
                            .graphicsLayer { translationY = translation }
                            .onGloballyPositioned { c ->
                                rowHeights[item.key] = c.size.height.toFloat()
                            }
                            // 拖动时：不要卡片框/阴影，仅整体轻微半透明，保持"跟手"的轻量手感
                            .alpha(if (dragging) 0.92f else 1f)
                            .padding(vertical = 3.dp)
                    ) {
                        // 备注不加左侧占位：文字左端与子任务的复选框左端对齐（整体比子任务文字更靠左）
                        // 回车：完成本条 note 的编辑，并把编辑焦点交给列表里的下一条；
                        // 若本条已是最后一条，则在末尾新增一条（保持连续录入的习惯）
                        val onEnter: () -> Unit = enter@{
                            // 软键盘"完成"与硬件回车可能各回调一次 → 300ms 内只认第一次
                            val now = System.currentTimeMillis()
                            if (now - lastEnterAt < 300L) return@enter
                            lastEnterAt = now
                            if (!item.committed && item.value.trim().isNotEmpty()) {
                                item.value = item.value.trim()
                                item.committed = true
                            }
                            val i = order.indexOf(item.key)
                            val nextKey = if (i in 0 until order.size - 1) order[i + 1] else null
                            if (nextKey != null) {
                                onHandoff()
                                editingKey = nextKey
                            } else if (item.value.trim().isNotEmpty()) {
                                onAdd()
                            } else {
                                editingKey = null
                            }
                        }
                        if (isEditing) {
                            // 显式持有 TextFieldValue：初始 selection 落在文本末尾 —— 点击（或回车接力）
                            // 进入编辑态时光标就在文字最后。该 state 随"编辑态分支"进出而自动重置：
                            // 每次重新进入编辑都从当前文本末尾开始，不会被上一次的位置残留影响。
                            var field by remember(item.key) {
                                mutableStateOf(
                                    TextFieldValue(item.value, TextRange(item.value.length, item.value.length))
                                )
                            }
                            BasicTextField(
                                value = field,
                                onValueChange = { field = it; item.value = it.text },
                                textStyle = TextStyle(fontSize = 15.sp, color = WuTitle, lineHeight = 22.sp),
                                // 多行：备注可换行，最多 5 行后框内滚动
                                singleLine = false,
                                maxLines = 5,
                                cursorBrush = SolidColor(WuAccent),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { onEnter() }),
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(item.fr)
                                    .onPreviewKeyEvent { e ->
                                        // 回车：完成本条 note 并进入下一条编辑（内容为空则不新增，避免连出空 note）
                                        if (e.type == KeyEventType.KeyDown &&
                                            (e.key == Key.Enter || e.key == Key.NumPadEnter)
                                        ) {
                                            onEnter()
                                            true
                                        } else {
                                            false
                                        }
                                    },
                                decorationBox = { inner ->
                                    Box {
                                        if (item.value.isEmpty()) {
                                            Text("Note", color = WuSubtle, fontSize = 15.sp, lineHeight = 22.sp)
                                        }
                                        inner()
                                    }
                                }
                            )
                        } else {
                            // 只读文本：点击即进入编辑态（右侧 ≡ 同步变成 ✕ 删除符号）
                            Text(
                                text = item.value,
                                fontSize = 15.sp,
                                color = WuTitle,
                                lineHeight = 22.sp,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { editingKey = item.key }
                            )
                        }
                        // 编辑态（正在编辑该条 note）：右侧的 ≡ 拖动手柄变成 ✕ 删除符号；
                        // 非编辑态：显示 ≡ 拖动排序。两者都占 36dp，宽度一致 → 文字区宽度不变、符号对齐。
                        if (isEditing) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .clickable { onRemove(item) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.Close,
                                    contentDescription = "删除该备注",
                                    tint = WuSubtle,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        } else {
                            // 拖动排序手柄（≡）：按住上下拖动调整备注顺序。
                            // 用 36dp 的 Box 包住 22dp 图标，扩大可点区域，更容易抓住。
                            Box(
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .size(36.dp)
                                    .pointerInput(item.key) {
                                    detectDragGestures(
                                        onDragStart = {
                                            val ord = orderState.value
                                            val from = ord.indexOf(item.key)
                                            settleKey = null
                                            dragDy = 0f
                                            dragFrom = from
                                            dragTo = from
                                            dragKey = item.key
                                        },
                                        onDrag = { change, amount ->
                                            change.consume()
                                            dragDy += amount.y
                                            val ord = orderState.value
                                            // 按行高累计推算目标槽位：越过相邻行中线才算换位
                                            var acc = 0f
                                            var t = dragFrom
                                            if (dragDy > 0f) {
                                                while (t < ord.size - 1) {
                                                    val h = rowHeights[ord[t + 1]] ?: 0f
                                                    if (h > 0f && dragDy > acc + h / 2f) {
                                                        acc += h
                                                        t++
                                                    } else break
                                                }
                                            } else if (dragDy < 0f) {
                                                while (t > 0) {
                                                    val h = rowHeights[ord[t - 1]] ?: 0f
                                                    if (h > 0f && -dragDy > acc + h / 2f) {
                                                        acc += h
                                                        t--
                                                    } else break
                                                }
                                            }
                                            dragTo = t
                                        },
                                        onDragEnd = {
                                            val ord = orderState.value
                                            val from = dragFrom
                                            val to = dragTo
                                            dragKey = null
                                            dragDy = 0f
                                            dragFrom = -1
                                            dragTo = -1
                                            if (from in ord.indices && to in ord.indices && from != to) {
                                                // 松手才把最终顺序一次性写回真实列表；被拖行标记为"回落中"，
                                                // 用最后一帧偏移平滑收进目标槽位
                                                val newOrder = ord.toMutableList().also { it.add(to, it.removeAt(from)) }
                                                settleKey = item.key
                                                reorderState.value(newOrder)
                                            } else {
                                                settleKey = null
                                            }
                                        },
                                        onDragCancel = {
                                            dragKey = null
                                            dragDy = 0f
                                            dragFrom = -1
                                            dragTo = -1
                                            settleKey = null
                                        }
                                    )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.DragHandle,
                                    contentDescription = "拖动排序",
                                    tint = WuSubtle,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
            // 备注区底部「+」按钮：整区只在最后一条 note 下方有一个（点一次在末尾追加一条空备注并进入编辑）
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 10.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(WuBackground)
                        .clickable { onAdd() }
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "添加备注",
                        tint = WuTitle,
                        modifier = Modifier.size(20.dp)
                    )
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
        val noteStr = task.notes.joinToString("\n")
        if (noteStr.isNotBlank()) {
            Text(
                text = noteStr,
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
        val noteStr = task.notes.joinToString("\n")
        if (noteStr.isNotBlank()) {
            Text(
                text = noteStr,
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

// ===== 列表模式（参考图）：通栏的列区块、列头可折叠 =====

/** 列表模式配色（复刻参考图）：列区块比页面背景略深的浅灰，任务卡片纯白并带细边框 */
private val ListSectionBg = Color(0xFFEAEAEB)
private val ListCardBorder = Color(0xFFDDDDDE)
private val ListTaskCardBg = Color(0xFFFFFFFF)

/**
 * 列表模式总览：每列一个通栏区块，列头点击可折叠/展开。
 * 折叠状态按列名记忆（rememberSaveable：旋转屏幕后仍在；不写回 .md）。
 */
@Composable
private fun ListBoard(
    pinnedSections: List<KanbanSection>,
    normalSections: List<KanbanSection>,
    collapsedTitles: List<String>,
    onToggleCollapse: (String) -> Unit,
    onToggleTask: (KanbanTask) -> Unit,
    onOpenTask: (KanbanSection, KanbanTask) -> Unit,
    onOpenSection: (KanbanSection) -> Unit,
    onAddCard: (KanbanSection) -> Unit,
    onEditList: (KanbanSection) -> Unit,
    onSetAllDone: (KanbanSection, Boolean) -> Unit,
    onDeleteCompleted: (KanbanSection) -> Unit,
    onDeleteTask: (KanbanSection, KanbanTask) -> Unit,
    onMoveTaskToTop: (KanbanSection, KanbanTask) -> Unit,
    onDuplicateTask: (KanbanSection, KanbanTask) -> Unit,
    /** 拖动列头排序：回调新的列头行号顺序（按原始行号标识） */
    onReorderSections: (List<Int>) -> Unit
) {
    // ===== 拖动排序模型（与备注/子任务同套）=====
    // 每个列区块的高度：按"越过相邻区块中线即换位"计算目标槽位
    val rowHeights = remember { mutableMapOf<Int, Float>() }
    var dragHead by remember { mutableStateOf<Int?>(null) }
    var dragDy by remember { mutableFloatStateOf(0f) }
    var dragFrom by remember { mutableStateOf(-1) }
    var dragTo by remember { mutableStateOf(-1) }
    // 松手后正在回落到目标槽位的区块：按列名（重排后行号会变，列名稳定）标记，
    // 保留最后一帧偏移再用动画收敛到 0，避免落位时"一跳"
    var settleTitle by remember { mutableStateOf<String?>(null) }

    // 参与排序的列：有真实列头行号（≥0）的才可拖动；「未分组」这类伪列排在最后
    val draggable = normalSections.filter { it.headerLineIndex >= 0 }
    val trailing = normalSections.filter { it.headerLineIndex < 0 }
    val order = draggable.map { it.headerLineIndex }
    val orderState = rememberUpdatedState(order)
    val reorderState = rememberUpdatedState(onReorderSections)
    val byHead = draggable.associateBy { it.headerLineIndex }

    val movingDy by animateFloatAsState(
        targetValue = if (dragHead != null) dragDy else 0f,
        animationSpec = if (dragHead != null) snap() else tween(durationMillis = 160),
        label = "sectionMovingDy"
    )
    val dragHeight = dragHead?.let { rowHeights[it] } ?: 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (pinnedSections.isNotEmpty()) {
            PinnedHeader()
            pinnedSections.forEach { section ->
                key("pin_${section.uniqueKey()}") {
                    ListSectionBlock(
                        section = section,
                        collapsed = section.title in collapsedTitles,
                        // 置顶列不参与拖动排序（顺序保存在本地、不写回 .md），用图钉代替拖动手柄
                        dragHandle = {
                            Box(Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.PushPin,
                                    contentDescription = null,
                                    tint = WuSubtle,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .rotate(-35f)
                                )
                            }
                        },
                        onToggleCollapse = { onToggleCollapse(section.title) },
                        onToggleTask = onToggleTask,
                        onOpenTask = { task -> onOpenTask(section, task) },
                        onOpenSection = { onOpenSection(section) },
                        onAddCard = { onAddCard(section) },
                        onEditList = { onEditList(section) },
                        onSetAllDone = { done -> onSetAllDone(section, done) },
                        onDeleteCompleted = { onDeleteCompleted(section) },
                        onDeleteTask = { task -> onDeleteTask(section, task) },
                        onMoveTaskToTop = { task -> onMoveTaskToTop(section, task) },
                        onDuplicateTask = { task -> onDuplicateTask(section, task) }
                    )
                }
            }
            if (normalSections.isNotEmpty()) {
                HorizontalDivider(
                    color = WuDivider,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }

        order.forEachIndexed { index, head ->
            val section = byHead[head] ?: return@forEachIndexed
            key(head) {
                val dragging = dragHead == head
                // 让位位移：向下拖时 (from, to] 整体上移一个区块高；向上拖时 [to, from) 整体下移
                val shiftTarget = when {
                    dragHead == null || dragging -> 0f
                    dragFrom < dragTo && index > dragFrom && index <= dragTo -> -dragHeight
                    dragFrom > dragTo && index in dragTo until dragFrom -> dragHeight
                    else -> 0f
                }
                val shift by animateFloatAsState(
                    targetValue = shiftTarget,
                    animationSpec = if (dragHead == null) snap() else tween(durationMillis = 150),
                    label = "sectionShift"
                )
                val translation = if (dragging || settleTitle == section.title) movingDy else shift
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(if (dragging) 1f else 0f)
                        .graphicsLayer { translationY = translation }
                        .onGloballyPositioned { c -> rowHeights[head] = c.size.height.toFloat() }
                ) {
                    ListSectionBlock(
                        section = section,
                        collapsed = section.title in collapsedTitles,
                        dragHandle = {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .pointerInput(head) {
                                        detectDragGestures(
                                            onDragStart = {
                                                val ord = orderState.value
                                                val f = ord.indexOf(head)
                                                if (f >= 0) {
                                                    settleTitle = null
                                                    dragDy = 0f
                                                    dragFrom = f
                                                    dragTo = f
                                                    dragHead = head
                                                }
                                            },
                                            onDrag = { change, amount ->
                                                change.consume()
                                                dragDy += amount.y
                                                val ord = orderState.value
                                                // 按区块高度累计推算目标槽位：越过相邻区块中线才算换位
                                                var acc = 0f
                                                var t = dragFrom
                                                if (dragDy > 0f) {
                                                    while (t < ord.size - 1) {
                                                        val h = rowHeights[ord[t + 1]] ?: 0f
                                                        if (h > 0f && dragDy > acc + h / 2f) {
                                                            acc += h
                                                            t++
                                                        } else break
                                                    }
                                                } else if (dragDy < 0f) {
                                                    while (t > 0) {
                                                        val h = rowHeights[ord[t - 1]] ?: 0f
                                                        if (h > 0f && -dragDy > acc + h / 2f) {
                                                            acc += h
                                                            t--
                                                        } else break
                                                    }
                                                }
                                                dragTo = t
                                            },
                                            onDragEnd = {
                                                val ord = orderState.value
                                                val from = dragFrom
                                                val to = dragTo
                                                dragHead = null
                                                dragDy = 0f
                                                dragFrom = -1
                                                dragTo = -1
                                                if (from in ord.indices && to in ord.indices && from != to) {
                                                    // 松手才一次性写回真实顺序；被拖区块标记"回落中"平滑收位
                                                    val newOrder = ord.toMutableList()
                                                        .also { it.add(to, it.removeAt(from)) }
                                                    settleTitle = section.title
                                                    reorderState.value(newOrder)
                                                } else {
                                                    settleTitle = null
                                                }
                                            },
                                            onDragCancel = {
                                                dragHead = null
                                                dragDy = 0f
                                                dragFrom = -1
                                                dragTo = -1
                                                settleTitle = null
                                            }
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.DragHandle,
                                    contentDescription = "拖动排序列",
                                    tint = WuSubtle,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        onToggleCollapse = { onToggleCollapse(section.title) },
                        onToggleTask = onToggleTask,
                        onOpenTask = { task -> onOpenTask(section, task) },
                        onOpenSection = { onOpenSection(section) },
                        onAddCard = { onAddCard(section) },
                        onEditList = { onEditList(section) },
                        onSetAllDone = { done -> onSetAllDone(section, done) },
                        onDeleteCompleted = { onDeleteCompleted(section) },
                        onDeleteTask = { task -> onDeleteTask(section, task) },
                        onMoveTaskToTop = { task -> onMoveTaskToTop(section, task) },
                        onDuplicateTask = { task -> onDuplicateTask(section, task) }
                    )
                }
            }
        }

        // 「未分组」等无列头的伪列：不参与排序，排在最后
        trailing.forEach { section ->
            key("tail_${section.uniqueKey()}") {
                ListSectionBlock(
                    section = section,
                    collapsed = section.title in collapsedTitles,
                    dragHandle = {},
                    onToggleCollapse = { onToggleCollapse(section.title) },
                    onToggleTask = onToggleTask,
                    onOpenTask = { task -> onOpenTask(section, task) },
                    onOpenSection = { onOpenSection(section) },
                    onAddCard = { onAddCard(section) },
                    onEditList = { onEditList(section) },
                    onSetAllDone = { done -> onSetAllDone(section, done) },
                    onDeleteCompleted = { onDeleteCompleted(section) },
                    onDeleteTask = { task -> onDeleteTask(section, task) },
                    onMoveTaskToTop = { task -> onMoveTaskToTop(section, task) },
                    onDuplicateTask = { task -> onDuplicateTask(section, task) }
                )
            }
        }
    }
}

/**
 * 列表模式里的一个列区块（复刻参考图）：列头（拖动手柄 + 箭头 + 列名 + 任务数 + ⋮）
 * + 任务卡片竖排 +「+添加卡片」。整体紧凑、圆角小、字号小。
 */
@Composable
private fun ListSectionBlock(
    section: KanbanSection,
    collapsed: Boolean,
    /** 列头最左侧的控件：普通列是拖动排序手柄，置顶列是图钉，未分组列留空 */
    dragHandle: @Composable () -> Unit,
    onToggleCollapse: () -> Unit,
    onToggleTask: (KanbanTask) -> Unit,
    onOpenTask: (KanbanTask) -> Unit,
    onOpenSection: () -> Unit,
    onAddCard: () -> Unit,
    onEditList: () -> Unit,
    onSetAllDone: (Boolean) -> Unit,
    onDeleteCompleted: () -> Unit,
    onDeleteTask: (KanbanTask) -> Unit,
    onMoveTaskToTop: (KanbanTask) -> Unit,
    onDuplicateTask: (KanbanTask) -> Unit
) {
    // 只列顶层任务（与主界面一致：子任务不展开、不计数），未完成在前
    val tasks = section.topLevelTasks().sortedBy { it.done }
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = ListSectionBg),
        border = BorderStroke(1.dp, ListCardBorder)
    ) {
        Column(Modifier.padding(bottom = if (collapsed) 0.dp else 4.dp)) {
            // 列头：拖动手柄独立在左（不参与折叠点击）；其余区域点击折叠/展开（⋮ 自己消费点击）
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 2.dp, end = 2.dp, top = 3.dp, bottom = 3.dp)
            ) {
                dragHandle()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onToggleCollapse() }
                        .padding(start = 1.dp, end = 2.dp)
                ) {
                    Icon(
                        imageVector = if (collapsed) Icons.Filled.KeyboardArrowRight else Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (collapsed) "展开列表" else "折叠列表",
                        tint = WuSubtle,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = section.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = WuTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    // 任务数（折叠时也显示）
                    Text(
                        text = "${tasks.size}",
                        fontSize = 12.sp,
                        color = WuSubtle
                    )
                    Spacer(Modifier.width(2.dp))
                    Box {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "列表操作",
                            tint = WuSubtle,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { menuExpanded = true }
                                .padding(5.dp)
                                .size(16.dp)
                        )
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("编辑列表") },
                                onClick = { menuExpanded = false; onEditList() }
                            )
                            DropdownMenuItem(
                                text = { Text("打开列详情") },
                                onClick = { menuExpanded = false; onOpenSection() }
                            )
                            DropdownMenuItem(
                                text = { Text("添加卡片") },
                                onClick = { menuExpanded = false; onAddCard() }
                            )
                            HorizontalDivider(color = WuDivider, thickness = 1.dp)
                            DropdownMenuItem(
                                text = { Text("全部标记完成") },
                                onClick = { menuExpanded = false; onSetAllDone(true) }
                            )
                            DropdownMenuItem(
                                text = { Text("清除已完成") },
                                onClick = { menuExpanded = false; onDeleteCompleted() }
                            )
                        }
                    }
                }
            }
            // 展开：任务卡片竖排 +「+添加卡片」
            if (!collapsed) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tasks.forEach { task ->
                        val (subDone, subTotal) = section.subtaskProgress(task)
                        ListTaskCard(
                            task = task,
                            subtaskDone = subDone,
                            subtaskTotal = subTotal,
                            onToggle = { onToggleTask(task) },
                            onOpen = { onOpenTask(task) },
                            onDelete = { onDeleteTask(task) },
                            onMoveToTop = { onMoveTaskToTop(task) },
                            onDuplicate = { onDuplicateTask(task) }
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onAddCard() }
                        .padding(vertical = if (tasks.isEmpty()) 9.dp else 11.dp)
                ) {
                    Text("+添加卡片", fontSize = 13.sp, color = WuSubtle)
                }
            }
        }
    }
}

/** 列表模式里的任务卡片（复刻参考图）：纯白小圆角卡片，左侧圆圈切换完成，点卡片=编辑；⋮ 菜单：删除/移到顶部/编辑/复制；有子任务/备注时底部显示计数 */
@Composable
private fun ListTaskCard(
    task: KanbanTask,
    subtaskDone: Int,
    subtaskTotal: Int,
    onToggle: () -> Unit,
    onOpen: () -> Unit,
    onDelete: () -> Unit,
    onMoveToTop: () -> Unit,
    onDuplicate: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val noteCount = task.notes.size
    val hasMeta = subtaskTotal > 0 || noteCount > 0
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = ListTaskCardBg),
        border = BorderStroke(1.dp, ListCardBorder)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 10.dp,
                        end = 2.dp,
                        top = 7.dp,
                        // 有计数行时收紧底部间距，让计数贴着任务文字
                        bottom = if (hasMeta) 1.dp else 7.dp
                    )
            ) {
                if (task.done) {
                    // 已完成：灰色勾，仍可点回未完成
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "标记未完成",
                        tint = WuDoneGrey,
                        modifier = Modifier
                            .size(15.dp)
                            .clickable { onToggle() }
                    )
                } else {
                    CheckCircle(done = false, size = 15.dp, onClick = onToggle)
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = task.text,
                    fontSize = 13.sp,
                    color = if (task.done) WuTaskText.copy(alpha = 0.7f) else WuTitle,
                    textDecoration = if (task.done) TextDecoration.LineThrough else null,
                    lineHeight = 18.sp,
                    modifier = Modifier.weight(1f)
                )
                Box {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "更多操作",
                        tint = WuSubtle,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { menuExpanded = true }
                            .padding(5.dp)
                            .size(16.dp)
                    )
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("删除", color = Color(0xFFD9483B)) },
                            onClick = { menuExpanded = false; onDelete() }
                        )
                        DropdownMenuItem(
                            text = { Text("移到顶部") },
                            onClick = { menuExpanded = false; onMoveToTop() }
                        )
                        DropdownMenuItem(
                            text = { Text("编辑") },
                            onClick = { menuExpanded = false; onOpen() }
                        )
                        DropdownMenuItem(
                            text = { Text("复制") },
                            onClick = { menuExpanded = false; onDuplicate() }
                        )
                    }
                }
            }
            // 卡片底部计数：子任务「已完成/总数」；其后若有 note 则追加 note 个数
            if (hasMeta) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 35.dp, end = 12.dp)
                        .padding(bottom = 7.dp)
                ) {
                    if (subtaskTotal > 0) {
                        Icon(
                            imageVector = Icons.Outlined.FormatListBulleted,
                            contentDescription = null,
                            tint = WuSubtle,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = "$subtaskDone/$subtaskTotal",
                            fontSize = 11.sp,
                            color = WuSubtle,
                            lineHeight = 13.sp
                        )
                        if (noteCount > 0) Spacer(Modifier.width(12.dp))
                    }
                    if (noteCount > 0) {
                        Icon(
                            imageVector = Icons.Outlined.Notes,
                            contentDescription = null,
                            tint = WuSubtle,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = "$noteCount",
                            fontSize = 11.sp,
                            color = WuSubtle,
                            lineHeight = 13.sp
                        )
                    }
                }
            }
        }
    }
}

/** 底部紧凑输入页：给某一列添加一张卡片（贴键盘，回车或 + 确认） */
@Composable
private fun AddCardSheet(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val focus = remember { FocusRequester() }
    KeyboardSheet(onDismiss = onDismiss, focus = focus, compact = true) {
        Spacer(Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("new task", color = WuSubtle, fontSize = 16.sp) },
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
                    val t = text.toSingleLineTaskText()
                    if (t.isNotBlank()) onAdd(t)
                    onDismiss()
                }),
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focus)
            )
            IconButton(onClick = {
                val t = text.toSingleLineTaskText()
                if (t.isNotBlank()) onAdd(t)
                onDismiss()
            }) {
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
        Column(Modifier.padding(horizontal = 18.dp, vertical = if (section.tasks.isEmpty()) 16.dp else 18.dp)) {
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
                // 子任务不展开：只列出顶层任务（主界面不显示子任务计数）
                section.topLevelTasks()
                    .sortedBy { it.done }
                    .forEach { task ->
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
                    "尚未选择文件夹\n\n点下方按钮添加看板路径，\n其中所有 .md 文件会列在这里",
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
                Text("添加看板路径", fontSize = 13.sp, color = WuSubtle)
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
