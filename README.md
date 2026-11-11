# Aura Launcher 🛡️

> A minimalist, privacy-first, battery-efficient Android launcher with gesture controls, custom icon packs, modular widgets, and zero telemetry. Inspired by GrapheneOS design principles.

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Zero Telemetry](https://img.shields.io/badge/Telemetry-0%20Bytes-success.svg)](README.md)
[![Permissions](https://img.shields.io/badge/Internet%20Permission-None-success.svg)](README.md)
[![Android CI](https://img.shields.io/badge/Android%20CI-Passing-brightgreen.svg)](.github/workflows/android.yml)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24%20(Android%207.0%2B)-orange.svg)](app/build.gradle.kts)

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

## 🚀 Quick Install & Run Guide

### Option 1: One-Command Install via ADB (Recommended)

Connect your Android phone via USB (with USB Debugging enabled in Developer Options) and run:

```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/aura-launcher.git
cd aura-launcher

# Build and install directly onto your connected device
./gradlew installDebug
```

### Option 2: Run in Android Studio

1. Open **Android Studio** (Hedgehog or newer).
2. Click **File → Open…** and select the cloned repository folder.
3. Allow Gradle to sync dependencies automatically.
4. Select your connected physical device or emulator in the device dropdown.
5. Click the green **Run (▶)** button or press `Shift + F10`.

### Option 3: Manual APK Install

1. Build the debug APK:
   ```bash
   # On Linux / macOS:
   ./gradlew assembleDebug

   # On Windows:
   gradlew.bat assembleDebug
   ```
2. The compiled, installable APK is located at:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```
3. Transfer `app-debug.apk` to your phone via USB, Google Drive, or `adb install`:
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```
4. On your device, tap the downloaded APK to install.

---

## ⚙️ Setting as Default Home Screen Launcher

Once installed on your Android device:
1. Press your phone's **Home** button or swipe up to go Home.
2. In the "Use as Home" system dialog, select **Aura Launcher** and choose **Always**.
3. Or go to **Settings → Apps → Default Apps → Home app → Select "Aura Launcher"**.

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

## 🛠️ Testing & Verification

Run the local JVM unit test suite:
```bash
./gradlew testDebugUnitTest
```

All tests execute completely offline without requiring an emulator or network connection.

---

## 🚀 Publishing to GitHub

1. In the AI Studio interface, click on the **Settings** or **Export** menu in the top bar.
2. Select **Push to GitHub** or configure your GitHub remote:
   ```bash
   git remote add origin https://github.com/YOUR_USERNAME/aura-launcher.git
   git branch -M main
   git push -u origin main
   ```
3. The included GitHub Actions workflow (`.github/workflows/android.yml`) will automatically run unit tests and package `app-debug.apk` as a downloadable release artifact on every push!

---

## 📄 License

Distributed under the MIT License. See [LICENSE](LICENSE) for more details.
