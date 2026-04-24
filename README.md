# QuailSpotter

QuailSpotter is a cross-platform mobile application built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**. The project aims to provide bird enthusiasts and researchers with a tool to identify and log quail sightings, specifically focusing on **Coturnix quails**.

## 🎯 Goals

- **Automated Identification**: Use on-device Machine Learning (TensorFlow Lite) to identify the sex and species of quails from images.
- **Sightings Logger**: Enable users to log sightings with timestamps, locations, and categorical notes.
- **Cross-Platform Consistency**: Provide a unified experience across Android and iOS using a shared codebase for both UI and business logic.
- **On-Device Processing**: Ensure all image classification happens locally on the device for privacy and offline capability.

## 🛠️ Implementation

### Technology Stack
- **UI Framework**: [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform) for a shared UI across Android and iOS.
- **Language**: Kotlin 2.0+ with Kotlin Multiplatform.
- **Machine Learning**: 
    - **TensorFlow Lite (TFLite)**: The core engine for quail detection.
    - **Native Interop**: Uses the `TensorFlowLiteObjC` Pod for iOS and the `kflite` library for Android to run a shared `.tflite` model.
- **Image Handling**: [Peekaboo](https://github.com/onseok/peekaboo) for cross-platform image picking and camera access.
- **Dependency Management**: CocoaPods for iOS native dependencies (TFLite) and Gradle for the shared Kotlin code.

### Project Structure
- **`:composeApp`**: Contains the shared Compose UI code and platform-specific entry points.
    - `commonMain`: Shared UI components and ViewModels.
    - `androidMain` / `iosMain`: Platform-specific integrations (e.g., AppContext initialization).
- **`:shared`**: Contains the core business logic and domain models.
    - `domain/QuailDetector`: The bridge between the raw image data and the TFLite interpreter.
    - `domain/ImageProcessor`: Handles image resizing and normalization for the ML model.
- **`iosApp`**: The native iOS wrapper that launches the Compose Multiplatform framework.

## 🎨 App Icon
The app features a custom-designed silhouette of a **Coturnix quail** (plump, no head plume, side profile) centered within a "spotter" viewfinder, reflecting the core purpose of the application.

- **Background**: Forest Green (`#2E7D32`)
- **Foreground**: Light Brown (`#D7CCC8`)

---

## 🚀 Getting Started

### Prerequisites
- Android Studio (latest version)
- Xcode (for iOS development)
- CocoaPods (`brew install cocoapods`)

### Build and Run

#### Android
```shell
./gradlew :composeApp:assembleDebug
```

#### iOS
1. Open the `iosApp` directory in Xcode.
2. Run `pod install` in the `iosApp` directory.
3. Build and run the `iosApp` target.

---
Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html) and [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform).
