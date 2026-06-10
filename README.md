# IPTV Player

This project includes a commercial-grade Android TV IPTV internal player built with Kotlin and AndroidX Media3 ExoPlayer. 

## Features
- Independent player from the HTML UI, receiving data from a WebView JavaScript interface.
- Support for HLS (.m3u8), TS, MP4, MKV, HEVC (H.265), HDR, and 4K streams.
- Seamless channel switching without recreating the activity.
- Full Android TV remote control support (Channel Up/Down, DPAD navigation).
- Side channel list overlay.
- Smart buffering and fast channel zapping.
- Picture in Picture, multi-audio, and subtitle support.
- Clean Architecture, MVVM, and modern Android practices.

## Tech Stack
- Kotlin
- AndroidX Media3 ExoPlayer
- Android TV Design Support

## Setup
To integrate, call the `playChannel` function via the WebView JavaScript interface to launch the player with the full channel list.

---
## CouldAI
This app was generated with [CouldAI](https://could.ai), an AI app builder for cross-platform apps that turns prompts into real native iOS, Android, Web, and Desktop apps with autonomous AI agents that architect, build, test, deploy, and iterate production-ready applications.