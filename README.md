# KOTLIN ANDROID MOBILE APP -> FOOD DELIVERY PLATFORM

### Framework: Jetpack Compose, IDE: Android Studio

A mobile application written in Kotlin using Android Studio and Jetpack Compose, designed as the frontend of a complete food‑delivery platform.

This project presented me with a few challenges, such as:

- Retrofit integration for API communication
- Connecting emulator and physical device to the backend
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
-  Buisiness logic

4. Data Layer
- Room Database
- PreferenceStorage

5. Networking — Retrofit + OkHttp
- Retrofit (API interface)
- OkHttp (client, logging)

6. Location & Mapbox Integration
- Requiest permission
- Mapbox map rendering + dynamic markers

7. LifeCylce
- Compose lifecycle
- Handling app closure, background, resume
