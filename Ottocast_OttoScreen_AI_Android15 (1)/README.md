# Ottocast OttoScreen AI - Native Android 15 Launcher

A native, production-grade custom Android Car Launcher application in Kotlin using Jetpack Compose, specifically optimized for the 11.4-inch ultra-wide display (1920x720 resolution) on Android 15 (targetSdk 35, minSdk 29).

## Highlights & Core Requirements

1. **Android Home Launcher Manifest Declaration:**
   - Intent filter with action `android.intent.action.MAIN`
   - Categories `android.intent.category.HOME`, `android.intent.category.DEFAULT`, and `android.intent.category.LAUNCHER`
   - `launchMode="singleTask"`, `stateNotNeeded="true"`, `sensorLandscape`
2. **Landscape-Only Ultra-wide Layout:**
   - Designed for 11.4-inch 1920x720 in-car display.
   - Zero phone-portrait assumptions; fluid responsive grid.
   - Strict adherence to >= 64dp touch target sizes for driver safety.
3. **Home Screen App Grid:**
   - Queries standard `PackageManager.queryIntentActivities(Intent(ACTION_MAIN).addCategory(CATEGORY_LAUNCHER))`.
   - Filters out apps without launch intents.
   - Hides this launcher itself (`packageName != context.packageName`).
   - Tapping an icon launches the application with `FLAG_ACTIVITY_NEW_TASK`.
4. **Bottom Dock:**
   - 4 configurable shortcut slots (default: Navigation, Media, Phone, Settings) with minimum 64dp touch size.
5. **Settings Screen:**
   - High-contrast toggle switches to control which apps appear on the home grid.
   - Dock slot assignment.
6. **100% Local-Only:**
   - Zero network connection required.
   - No login, telemetry, or Firebase.
   - State persisted using standard Android `SharedPreferences`.

## Building the APK

```bash
# 1. Unzip the project or clone into Android Studio
# 2. Compile debug APK
./gradlew assembleDebug

# Output APK path:
# app/build/outputs/apk/debug/app-debug.apk
```

## Installing on Ottocast OttoScreen via ADB

```bash
# Connect via USB or Wi-Fi ADB
adb connect <device-ip>:5555

# Install APK replacing any older version
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Set as default home launcher
# Go to: Settings -> Apps -> Default apps -> Home app -> Select "Ottocast OttoScreen AI"
```
