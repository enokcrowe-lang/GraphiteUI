# Aura Launcher 🛡️

> A minimalist, privacy-first, battery-efficient Android launcher with gesture controls, custom icon packs, and zero telemetry. Inspired by GrapheneOS design principles.

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Zero Telemetry](https://img.shields.io/badge/Telemetry-0%20Bytes-success.svg)](README.md)
[![Permissions](https://img.shields.io/badge/Internet%20Permission-None-success.svg)](README.md)

---

## 🌟 Key Highlights

- **🔒 Zero Telemetry & 100% Offline**: The application declares **NO** `android.permission.INTERNET` in `AndroidManifest.xml`. The Android OS physically isolates the launcher sandbox from all network sockets. Zero analytics SDKs, zero crash reporters, zero advertising libraries.
- **⚡ Battery & Resource Efficiency**: Built with a lightweight Kotlin and Jetpack Compose architecture. Uses strictly event-driven updates (sticky broadcasts, UI lifecycle). No long-running background polling daemons, zero wakelocks, and a pure `#000000` AMOLED dark theme to maximize OLED battery longevity.
- **🎨 Custom Icon Packs & GrapheneOS Minimalism**:
  - Auto-detects third-party installed icon packs (ADW, Nova, Go Launcher standards).
  - Built-in **GrapheneOS Monochrome** high-contrast desaturated theme for minimal distraction.
  - Built-in **Pure Minimalist Text** mode (Niagara style) with zero visual icon noise.
  - Adaptive shape masking: Squircle, Circle, Rounded Square, Hexagon, or System Default.
  - Adjustable icon scale (70% - 130%) and toggleable app labels.
- **👆 Responsive Gesture Controls**:
  - **Swipe Up**: Instantly open App Drawer or Quick Search.
  - **Swipe Down**: Expand notification shade / status bar.
  - **Double Tap**: Screen sleep / standby blackout overlay.
  - **Pinch In**: Instant fuzzy search across all applications.
  - **Long Press**: Open Launcher Settings & Widget Customization.
- **🧩 Modular Minimalist Widget System**:
  - **Minimalist Clock & Date**: Clean typography, stealth battery badge, and one-tap clock/alarm shortcut.
  - **Power & Battery Monitor**: Live hardware battery percentage, temperature (°C), health condition, charging speed, and zero-wakeups audit.
  - **Privacy Weather**: Location-privacy weather widget with zero continuous background GPS tracking. Supports local climate heuristics, custom city labels, condition cycling, and °C/°F toggles.
  - **Privacy & Security Shield**: Live verification of 0 bytes network transmitted, sandbox integrity, and zero telemetry SDKs.
  - **Private Scratchpad**: On-device encrypted memo card directly on the home screen for instant notes without third-party apps.
  - **Quick Action Pills**: Fast one-touch toggles for Search, Sleep, App Drawer, and Settings.
  - Reorder, toggle, and switch between compact/expanded card layouts.
- **📱 Clean App Drawer & Privacy Vault**:
  - Instant fuzzy search with keyboard auto-open support.
  - Fast A-Z alphabet scrubber bar.
  - Category filtering: All, Favorites, Chat & Call, Productivity, Tools & Media, System.
  - App Context Menu: Pin to Dock, Pin to Home, Custom App Renaming, App Info, Uninstall, and App Hiding (Privacy Vault).

---

## 🏗️ Architecture

```
app/src/main/java/com/example/
├── MainActivity.kt                      # Edge-to-edge Launcher activity entry point
├── data/
│   ├── model/
│   │   ├── AppInfo.kt                   # AppItem, AppCategory, and context menu models
│   │   ├── IconPackModel.kt             # Icon pack themes, shapes, and installed packs
│   │   ├── WidgetModel.kt               # Widget types, battery telemetry, and weather models
│   │   └── GestureModel.kt              # Gesture settings and launcher preferences
│   └── repository/
│       ├── AppRepository.kt             # Scans installed apps with in-memory caching
│       ├── IconPackManager.kt           # Scans installed icon packs and applies monochrome filters
│       ├── BatteryMonitor.kt            # Sticky broadcast receiver for battery telemetry
│       ├── PrivacyWeatherHelper.kt      # Offline zero-tracking climate estimation
│       └── LauncherPreferencesRepository.kt # Fast, zero-overhead reactive persistence
└── ui/
    ├── components/
    │   ├── AppIconView.kt               # Dynamic icon renderer with shapes and monochrome filters
    │   ├── ClockWidgetView.kt           # Minimalist clock & date widget
    │   ├── BatteryWidgetView.kt         # Live battery telemetry card
    │   ├── PrivacyWeatherWidgetView.kt  # Location-privacy weather widget
    │   ├── PrivacyAuditWidgetView.kt    # Real-time zero-telemetry monitor
    │   ├── ScratchpadWidgetView.kt      # Home screen quick scratchpad
    │   ├── QuickActionsWidgetView.kt    # Shortcut pill buttons
    │   ├── DockView.kt                  # Pinned favorites dock
    │   ├── GestureDetectorBox.kt        # Multi-touch gesture engine
    │   └── WidgetContainer.kt           # Base modular widget card
    ├── drawer/
    │   ├── AppDrawer.kt                 # Full-screen drawer with grid/list and alphabet scrubber
    │   ├── AppContextMenuDialog.kt      # Long-press app management menu
    │   └── AppRenameDialog.kt           # Custom app renaming dialog
    ├── settings/
    │   └── SettingsBottomSheet.kt       # Comprehensive launcher configuration
    ├── home/
    │   └── HomeScreen.kt                # Root home screen coordinating gestures and widgets
    └── theme/
        ├── Color.kt                     # GrapheneOS AMOLED palette
        ├── Theme.kt                     # Dynamic and true black dark theme
        └── Type.kt                      # Inter and JetBrains Mono typography
```

---

## 🛠️ Building & Running

### Prerequisites
- Android SDK 36 (minSdk 24)
- Java 11+
- Gradle 8.x

### Build Commands
```bash
# Compile debug APK
gradle :app:assembleDebug

# Run Robolectric unit tests
gradle :app:testDebugUnitTest
```

---

## 🚀 Publishing to GitHub

1. In the AI Studio interface, click on the **Settings** or **Export** menu in the top bar.
2. Select **Push to GitHub** or configure your GitHub remote:
   ```bash
   git remote add origin https://github.com/YOUR_USERNAME/aura-launcher.git
   git branch -M main
   git push -u origin main
   ```

---

## 📄 License

Distributed under the MIT License. See [LICENSE](LICENSE) for more details.
