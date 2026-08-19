# KOTLIN ANDROID MOBILE APP -> FOOD DELIVERY PLATFORM

[![Kotlin](https://img.shields.io/badge/Kotlin-Dev-blue?logo=kotlin)](https://kotlinlang.org) [![Android Studio](https://img.shields.io/badge/Android%20Studio-IDE-green?style=for-the-badge&logo=androidstudio&logoColor=white)](https://developer.android.com/studio) 
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI%20Toolkit-purple?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)


#### Framework: Jetpack Compose, IDE: Android Studio

A mobile application written in Kotlin using Android Studio and Jetpack Compose, designed as the frontend of a complete food‑delivery platform.
> Beforehand, I created a cross-platform version of the project using the [React Native Framework](https://github.com/ValeBotti/React-Native-Uni-Project), then I created an [ASP.NET Core Web API web application](https://github.com/ValeBotti/ASP_NET_Core_Web_API) because the previous APIs had been discontinued.

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

## Architecture Overview

1. Presentation Layer — Jetpack Compose UI
- Composable functions
- State-driven rendering
- Navigation Compose

2. ViewModel Layer — MVVM
-  StateFlow / MutableState
-  Event handling
-  Business logic

3. Data Layer
- Room Database
- PreferenceStorage

4. Networking — Retrofit + OkHttp
- Retrofit (API interface)
- OkHttp (client, logging)

5. Location & Mapbox Integration
- Request permission
- Mapbox map rendering + dynamic markers

6. Lifecycle
- Compose lifecycle
- Handling app closure, background, resume

## Project structure:
> When talking about the project's structure in Jetpack Compose, we refer to packages, not directories. Packages are designed to provide a clear structure to the project that directories cannot provide.

```text
    └── main
        └── java
            └── com.example.valentinabotti_kotlin
                └── data/
                └── model/
                └── ui/
                └── viewmodel/
                ├── Navigation.kt
                └── MainActivity.kt
            └── res
            └── drawable/
            └── AndroidManifest.xml
```

- com.< author >.<app_name>: the main package -> that's the application ID that the Play Store uses to uniquely identify the app.
- data: dealing with DBController, PreferenceStorage, and ApiCalls
- model: navigation and data classes (all those dear old structures you need when dealing with OOP)
- ui: presentation layer
- viewmodel: handling state, lifecycle, business logic (I chose to keep it in here), and so on.
- MainActivity.kt -> in modern Android programming, you use a single activity.

## Presentation Layer - User Interface

In the project, I've created the subdirectory "ui"; it contains all the themes, screens, and components I've created for the presentation layer.
Back when I created the project, the main goal was to learn Kotlin and pass the exam. Meanwhile, I was working on my thesis where I developed an Android application using Java.
As you may know, the programming paradigm has changed a lot: Java for Android relies on imperative programming and XML layouts, while the modern Android standard uses exclusively declarative programming with Kotlin.
Now I want to get deeper into learning UI basics that apply to most modern frontend frameworks.

[Jetpack Compose UI doc](https://developer.android.com/develop/ui?hl=it)

```text
ui/
├── theme/
    ├── Color.kt -> a semantic map of colors and meanings
    ├── Shapes.kt -> predefined shapes
    ├── Theme.kt -> light, dark, accessibility...
    └── Type.kt -> typography
├── screens/     -> components that contain the entire screen (these are switched with navigation)
    ├── HomeListaMenu.kt -> home page where you can see the list of menus you can buy
    ├── DettagliMenu.kt -> the page where you can purchase food
    ├── StatoConsegna.kt -> shows you the map
    └── ProfiloUtente.kt -> user's profile
└── components/  -> reusable pieces
```

> Unidirectional dependency flow: screens use components; components use themes; never the other way around.
