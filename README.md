# QuailSpotter

QuailSpotter is a cross-platform mobile application built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform** integrating both an offline ML model using TensorFlowLite and LLM Integration.  

The project aims to provide backyard quail farmers with a tool to identify quail sex using feather patterns when possible, specifically focusing on **Coturnix quails**.

Using feather patterns to sex quails at approximately four weeks of age provides advantages to quail farmers over waiting for sexual maturity. 
However, other genes can mask the traits, making some breeds difficult or impossible. 

More information on quail genetics and how various breeds are classified at 
https://www.pipsnchicks.com/quail-genetics

<img width="320" height="615" alt="Screenshot_20260424_133307" src="https://github.com/user-attachments/assets/8b7cd3f4-4f17-4e92-bef9-47c68f4e83b8" /><img width="320" height="615" alt="Screenshot_20260424_133143" src="https://github.com/user-attachments/assets/a9f70da8-3172-494e-a3e3-097c091b6fcf" />

## 🎯 Goals

- **Automated Identification**: Use on-device Machine Learning (TensorFlow Lite) to identify the sex of quails from images.
- **AI Deep Scan**: Leveraging cloud-based LLMs (OpenAI) for advanced genetic analysis and reasoning when feather patterns are complex. The Deep Scan button is available whenever an image is captured, enabling detailed insights even if local detection returns no results.
- **Cross-Platform Consistency**: Provide a unified experience across Android and iOS using a shared codebase for both UI and business logic.
- **Hybrid Processing**: Balancing local TFLite detection for speed with optional cloud analysis for higher accuracy and detailed insights.

## 🛠️ Implementation

### Technology Stack
- **UI Framework**: [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform) for a shared UI across Android and iOS.
- **Language**: Kotlin 2.0+ with Kotlin Multiplatform.
- **Machine Learning**: 
    - **TensorFlow Lite (TFLite)**: Local engine for real-time quail detection.
    - **OpenAI API**: Used for the "Deep Scan" feature to provide genetic reasoning and breed identification.
- **Testing**:
    - **Compose UI Test**: Cross-platform UI testing framework for verifying UI interactions and application logic.
    - **Robolectric**: Enables running Android UI tests on the JVM for fast and reliable local verification.
- **Image Handling**: [Peekaboo](https://github.com/onseok/peekaboo) for cross-platform image picking and camera access.
- **Dependency Management**: CocoaPods for iOS native dependencies (TFLite) and Gradle for the shared Kotlin code.

### Project Structure
- **`:composeApp`**: Contains the shared Compose UI code and platform-specific entry points.
    - `commonMain`: Shared UI components, ViewModels, and App state management.
    - `androidUnitTest`: JUnit 4 and Robolectric tests for UI verification, including button logic and AI scan timeouts.
    - `androidMain` / `iosMain`: Platform-specific integrations.
- **`:shared`**: Contains the core business logic and domain models.
    - `domain/QuailDetector`: Bridge to the TFLite interpreter.
    - `domain/QuailAiAnalyzer`: Integration with OpenAI for advanced analysis.
    - `domain/ImageProcessor`: Handles image resizing and normalization.
- **`iosApp`**: The native iOS wrapper that launches the Compose Multiplatform framework.

## 🚀 Getting Started

### Prerequisites
- Android Studio (latest version)
- Xcode (for iOS development)
- CocoaPods (`brew install cocoapods`)
- **OpenAI API Key**: Add `openai.api.key=your_key_here` to your `local.properties` file.

### Build and Run

#### Android
```shell
./gradlew :composeApp:assembleDebug
```

#### Running Tests
Verify the UI and logic using the automated test suite:
```shell
./gradlew :composeApp:testDebugUnitTest
```

#### iOS
1. Open the `iosApp` directory in Xcode.
2. Run `pod install` in the `iosApp` directory.
3. Build and run the `iosApp` target.

---
Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html) and [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform).
