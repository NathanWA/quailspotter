---
name: android
description: Android development patterns with Jetpack Compose. Use when implementing Android UI, ViewModels, or platform-specific features.
---
# QuailSpotter Project Skills & Rules

This file defines the technical context and coding standards for the QuailSpotter project. Gemini should refer to these rules when generating or refactoring code.

## 🛠 Tech Stack
- **Backend:** Ktor (Server-side Kotlin)
- **Engine:** Netty
- **Shared Logic:** Kotlin Multiplatform (KMP)
- **Architecture:** Modular Routing

## 📋 Coding Standards
1. **Routing:** Keep `Application.kt` clean. Define new features in extension functions on `Route` in separate files (e.g., `src/main/kotlin/com/nathan/quailspotter/routes/`).
2. **Response Types:** Use Kotlinx Serialization for JSON responses rather than raw strings.
3. **Naming:** Use PascalCase for Classes and camelCase for functions/variables.
4. **Ports:** Always use the `SERVER_PORT` constant defined in the project configuration.

## 🎯 Domain Logic (QuailSpotter)
- **Sightings:** Users should be able to log quail sightings (location, timestamp, species).
- **Greeting:** The `Greeting().greet()` function from the common module is the standard way to verify cross-platform connectivity.

## 🚀 How to add a new "Skill" (Feature)
To add a new feature to this project:
1. Create a new Kotlin file in the `routes` package.
2. Define a `fun Route.featureNameRoutes() { ... }` function.
3. Register the function in `Application.kt` inside the `routing {}` block.
4. Update this `SKILL.MD` if new architectural patterns are introduced.


## Best Practices

| Area | Recommendation |
|------|----------------|
| **State** | Use StateFlow for UI state, Channel for events |
| **Lifecycle** | Use `collectAsStateWithLifecycle()` |
| **Preview** | Add @Preview for all composables |
| **Separation** | Screen = state collection, Content = pure UI |
| **Testing** | Extract logic to ViewModel, test without UI |
| **Modifiers** | Accept Modifier parameter, apply last |
| **Navigation** | Use type-safe routes |
