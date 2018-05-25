# Agora-Custom-Media-Device-Android
Read this in other languages: [English](https://github.com/AgoraIO/Agora-Custom-Media-Device-Android/blob/master/README.en.md)

## 简介

本示例代码基于 Agora Native SDK 中新增的 MediaIO 接口开发，能帮助开发者实现以下功能：

1. 手机屏幕共享或者投射特定的 View 到远端。
2. 本地视频和 Camera 作为外部数据源，本地显示并推送视频或 Camera 数据到远端；基于 MediaIO 接口，实现频道内视频源动态切换。

在运行本 Demo 前，你可以：

* 查看 Agora Native SDK 的入门示例代码：[Agora-Android-Tutorial-1to1](https://github.com/AgoraIO/Agora-Android-Tutorial-1to1)
* 查看 Android 平台使用旧接口实现屏幕共享的示例代码：[Agora-Screen-Sharing-Android](https://github.com/AgoraIO-Community/Agora-Screen-Sharing-Android)
* 查看 iOS 平台实现屏幕共享的示例代码：[Agora-Screen-Sharing-iOS](https://github.com/AgoraIO-Community/Agora-Screen-Sharing-iOS)

## 准备开发环境

* 请保证你的 Android Studio 版本在 Android Studio 2.0 以上
* Android 真机一台（Nexus 5X 或其他设备）
* 由于部分模拟机有功能缺失或性能问题，Agora 推荐使用真机

## 运行示例程序
1. 在 Agora.io 注册账号，并创建自己的测试项目，获取 App ID。
2. 下载本页示例程序。
3. 在示例程序的 *app/src/main/res/values/strings.xml* 路径下，填入获取到的 App ID:

	```
	<string name="agora_app_id"><#YOUR APP ID#></string>
	```
4. 点击 [Agora.io SDK](https://www.agora.io/cn/download/) 下载 **视频通话 + 直播 SDK**，然后解压。
5. 将 SDK 中 **libs** 文件夹下的 ***.jar** 文件复制到本示例程序的 *app/libs* 路径下。
6. 将 SDK 中 **libs** 文件夹下的 **arm64-v8a/x86/armeabi-v7a** 文件复制到本示例程序的 *app/src/main/jniLibs* 路径下。
7. 用 Android Studio 打开该示例项目，连接设备，编译并运行。你也可以使用 `gradle` 直接编译运行。

## 联系我们
* 完整的 API 文档见 [文档中心](https://docs.agora.io/cn/2.2.1)
* 如果在集成中遇到问题，你可以到 [开发者社区](https://dev.agora.io/cn/) 提问
* 如果有售前咨询问题，你可以拨打 400 632 6626，或加入官方 QQ 群 12742516 提问
* 如果需要售后技术支持，你可以在 [Agora Dashboard](https://dashboard.agora.io/signin?next=%2F) 提交工单
* 如果发现了示例代码的 Bug，欢迎提交到 [issue](https://github.com/AgoraIO-Community/Agora-Screen-Sharing-Android/issues)

## 代码许可
The MIT License (MIT).
