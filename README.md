# Release 监控助手 (MIUIX 风格 Android 原生客户端)

专为 Android 移动端打造的独立 GitHub Release 版本监控应用。UI 基于小米澎湃OS / MIUI 设计语言，完全采用 [compose-miuix-ui/miuix](https://github.com/compose-miuix-ui/miuix) 官方核心组件库构建。

> **免本地配置**：本项目已内置 **GitHub Actions 云端自动构建工作流**，您无需在电脑上安装任何 Java、Gradle 或 Android Studio 环境，只需将本工程推送到 GitHub，云端服务器便会自动为您编译打包出可直接安装的 `.apk` 文件！

---

## 🌟 核心功能与 MIUIX 风格体验

1. **纯正的小米澎湃OS (HyperOS / MIUI) 视觉**：
   - 采用 `compose-miuix-ui` 的 `MiuixTheme` 统一色彩体系与圆角规范；
   - 沉浸式毛玻璃大标题顶栏 (`TopAppBar` + `Scaffold`)；
   - 典型的大圆角分组卡片 (`Card`)；
   - 小米经典橙色徽标（`#FF6900`）高亮提示待更新版本。
2. **开关与检测机制**：
   - **移除后台定时轮询**：优化移动端耗电与系统后台杀进程问题；
   - **打开软件自动检测开关**：使用 MIUI 标志性的 **`SuperSwitch`** 控件，开启后每次启动应用自动在后台检查更新；
   - **手动检测按钮**：顶部工具栏提供【一键全检】按钮，每个项目卡片提供【单独刷新】按钮。
3. **项目手动录入与管理**：
   - 随时录入监控项目：支持设置【项目自定义标题】、【GitHub 仓库 (支持 owner/repo 或 URL)】、【当前跟踪版本】以及【多行备注】；
   - 支持编辑与删除，所有数据在手机本地安全持久化（SharedPreferences），无任何隐私泄露风险。
4. **Release 更新日志与下载**：
   - 点击项目卡片弹窗查看最新 Release 更新日志、发布时间；
   - 提供直达浏览器下载按钮与“将最新版本标记为当前版本”快捷操作。

---

## 🚀 零环境上传 GitHub 自动获取 APK 教程

您不需要在电脑上安装任何编译工具，按以下步骤即可在 GitHub 免费获得编译好的 APK：

### 第一步：新建 GitHub 仓库并上传代码
1. 打开 [GitHub](https://github.com/) 并登录，点击右上角 **New repository** 新建一个仓库（例如命名为 `ReleaseMonitor`）；
2. 打开终端（或使用 Git 客户端），将本文件夹 (`github项目`) 里的全部文件推送到该仓库：
   ```bash
   cd "e:\python program\GITHUB NEW\github项目"
   git init
   git add .
   git commit -m "feat: init MIUIX Release Monitor Android app"
   git branch -M main
   git remote add origin https://github.com/<你的GitHub用户名>/ReleaseMonitor.git
   git push -u origin main
   ```

### 第二步：GitHub Actions 自动云端编译
1. 打开您刚才创建的 GitHub 仓库页面，点击上方的 **Actions** 选项卡；
2. 您会看到名为 **`Build Android APK`** 的自动化任务正在运行（黄圈表示正在编译中，通常 2~3 分钟完成）；
3. 编译完成后状态会变为绿色勾勾（`Success`）。

### 第三步：下载 APK 安装到手机
1. 点击进入该次成功的构建记录；
2. 页面往下拉到底部的 **Artifacts**（构建产物）区域；
3. 点击 **`ReleaseMonitor-MIUIX-debug-apk`** 即可直接下载压缩包；
4. 解压下载的压缩包，里面就是编译好的 `.apk` 安装包，发送到手机直接安装即可测试！

---

## 💻 本地 Android Studio 开发（可选）

如果您以后在电脑上安装了 Android Studio，也可直接在 Android Studio 中点击 **Open** 选择本目录，连接手机即可直接点击绿色三角符号进行调试与打包。
