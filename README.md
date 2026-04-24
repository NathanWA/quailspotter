# QuailSpotter

QuailSpotter is a cross-platform mobile application built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**. The project aims to provide backyard quail farmers with a tool to identify quail sex using feather patterns when possible, specifically focusing on **Coturnix quails**.

More information on quail genetics and how various breeds are classified at 
https://www.pipsnchicks.com/quail-genetics
<img width="320" height="615" alt="Screenshot_20260424_133307" src="https://github.com/user-attachments/assets/8b7cd3f4-4f17-4e92-bef9-47c68f4e83b8" /><img width="320" height="615" alt="Screenshot_20260424_133143" src="https://github.com/user-attachments/assets/a9f70da8-3172-494e-a3e3-097c091b6fcf" />

## 🎯 Goals

- **Automated Identification**: Use on-device Machine Learning (TensorFlow Lite) to identify the sex of quails from images.
- **Cross-Platform Consistency**: Provide a unified experience across Android and iOS using a shared codebase for both UI and business logic.
- **On-Device Processing**: Ensure all image classification happens locally on the device for speed and offline capability.

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
