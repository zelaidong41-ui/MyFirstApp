# 🚀 WanAndroid-Radar (玩安卓雷达资讯客户端)

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)]()
[![License](https://img.shields.io/badge/License-MIT-blue.svg)]()

### 🌟 项目简介
本项目是一款基于 **Retrofit2 + Glide + RecyclerView** 构建的极客资讯 App。它不仅实现了基础的 WanAndroid 平台数据展示，更创新性地加入了一套 **“关键词语义分拣系统”**（雷达分拣机），能够自动识别资讯内容并动态匹配 UI 视觉元素。

### 🛠️ 核心技术栈
- **网络层**：使用 `Retrofit2` + `OkHttp3` 实现高效的 RESTful API 请求。
- **解析层**：利用 `Gson` 进行 JSON 数据的自动化模型转换。
- **图片处理**：集成 `Glide` 实现图片的异步加载与智能缓存。
- **列表方案**：采用 `RecyclerView` 配合自定义 `Adapter` 实现复杂逻辑下的高性能渲染。
- **分拣算法**：逻辑层实现了一套关键词权重匹配机制（Android/Java/Flutter/面试），动态分发 UI 资源。

### ✨ 功能亮点
1. **智能分拣系统**：
   - 核心**：自动捕捉标题关键词，匹配安卓原生视觉图标。
   - 混合开发**：识别 Flutter 动态，提升技术视野。
   - 面试突击**：高亮面试相关文章，助力开发者进阶。
   - 保底机制**：针对长尾内容进行优雅的兜底显示。
2. **异步非阻塞体验**：全链路采用异步回调逻辑，确保在复杂网络环境下依然拥有丝滑的滑动体验。
3. **架构解耦**：严格遵循面向对象原则，View 层与 Data 层清晰分离。

### 📸 运行预览

### 🏗️ 如何运行
1. `git clone https://github.com/zelaidong41-ui/MyFirstApp.git`
2. 使用 Android Studio 打开项目。
3. 连接您的手机（已开启开发者模式）。
4. 点击 `Run` 即可见证！

---
**Author**: 董泽来 (Shanghai University of Engineering and Technology)
**Goal**: 致力于成为一名优秀的 Android 架构师。
