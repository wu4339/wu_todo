# wu_todo

一个 Android 待办应用，直接读取 **Obsidian 的 Kanban 插件（list 模式）** 生成的 Markdown 看板文件，并以卡片看板的形式展示、勾选。

- 读取 `## 列标题` 与 `- [ ] / - [x] 任务`
- 点击任务即可勾选，修改会**直接写回**原 `.md` 文件（Obsidian 里实时可见）
- 通过系统文件选择器打开单个 `.md` 文件，或选择一个文件夹批量挑选看板
- 支持从「文件管理器 → 用其他应用打开」直接加载 `.md`
- 适配 Android 11+ 的分区存储（Storage Access Framework，带持久权限）
- 自动跳过 frontmatter、Obsidian 的 `%%` 注释块、代码围栏等内容

## 界面

设计参考 Obsidian Kanban 看板：浅灰背景、白色圆角卡片、列标题前的红点、灰色任务文字与圆圈勾选框，双列网格布局。

## 构建

### 本地构建

前置：JDK 17、Android SDK（compileSdk 34 / build-tools 34.0.0 / platform-tools）。

```bash
# 设置 Android SDK 路径
export ANDROID_HOME=/path/to/android-sdk

./gradlew assembleDebug      # 生成 app/build/outputs/apk/debug/app-debug.apk
./gradlew assembleRelease    # 生成 app/build/outputs/apk/release/app-release-unsigned.apk
```

> 项目已把 Maven 仓库指向阿里云镜像（maven.aliyun.com），国内网络可直接拉取依赖；Gradle 发行版也走腾讯镜像，无需翻墙。

### 通过 GitHub 自动编译与发布

仓库已内置 `.github/workflows/android-build.yml`：

- 推送到 `main`/`master` 分支时，自动构建并上传 **debug APK** 作为 Actions 产物（可直接下载安装）。
- 打 `v*` 标签（如 `v1.0.0`）并 push 时，自动构建并创建一个 GitHub Release，附带 debug 与 release 两个 APK。

```bash
git tag v1.0.0
git push origin v1.0.0
```

## 使用

1. 安装后首次进入，点击「打开看板 .md 文件」或「从文件夹选择」。
2. 选中 Obsidian 保险库里的看板文件（如 `任务看板.md`），并授予「永久访问」权限。
3. 在卡片里点任务即可勾选；回到 Obsidian 即可看到 ` [x]` 已更新。

示例看板文件见 `sample/任务看板.md`。

## 项目结构

```
app/src/main/java/com/wu/todo/
├── MainActivity.kt                 # 入口、文件/文件夹选择器、外部打开
├── data/
│   ├── KanbanParser.kt            # Markdown 解析与勾选原地切换
│   └── KanbanRepository.kt        # SAF 读写 .md
└── ui/
    ├── BoardScreen.kt             # 看板界面（卡片网格、勾选、菜单）
    ├── BoardViewModel.kt          # 状态与业务逻辑
    └── theme/Theme.kt             # 配色主题
```
