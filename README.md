# KOTLIN ANDROID MOBILE APP -> FOOD DELIVERY PLATFORM

### Framework: Jetpack Compose, IDE: Android Studio

A mobile application written in Kotlin using Android Studio and Jetpack Compose, designed as the frontend of a complete food‑delivery platform.

This project presented me with a few challenges, such as:

- Retrofit integration for API communication
- Connecting an emulator or a physical device
- Lifecycle management in a modern Compose environment
- MVVM architecture using ViewModel and state‑driven UI
- Location handling and permissions
- Mapbox integration for real‑time drone tracking
- PreferenceStorage for session persistence
- Room Database for local caching and offline support

[Project video](https://1drv.ms/v/c/e3188549c7d8abfc/IQBei9-F1AD5Qph_f5faK733AecOH8uAjvyG88z45eNunNk?e=XN8hr3)

### Architecture Overview

1. Presentation Layer — Jetpack Compose UI
- Composable functions
- State-driven rendering
- Navigation Compose

2. ViewModel Layer — MVVM
-  StateFlow / MutableState
-  Event handling
-  Business logic

4. Data Layer
- Room Database
- PreferenceStorage

5. Networking — Retrofit + OkHttp
- Retrofit (API interface)
- OkHttp (client, logging)

6. Location & Mapbox Integration
- Request permission
- Mapbox map rendering + dynamic markers

7. Lifecycle
- Compose lifecycle
- Handling app closure, background, resume

### In the project, I've created the subdirectory "ui"; it contains all the themes, screens, and components I've created for the presentation layer.

Back when I created the project, the main goal was to learn Kotlin and pass the exam; now I want to get deeper into learning UI basics that apply to most modern frontend frameworks.

[Jetpack Compose UI doc](https://developer.android.com/develop/ui?hl=it)

> Directory structure:

ui/
├── theme/       -> colors, typography, spacing, shape
├── screens/     -> screens
└── components/  -> reusable pieces

