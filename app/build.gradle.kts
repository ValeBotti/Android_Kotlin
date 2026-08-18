plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlinx-serialization")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.valentinabotti_kotlin"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.valentinabotti_kotlin"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Mapbox
    implementation("com.mapbox.maps:android:11.9.1")
    implementation("com.mapbox.extension:maps-compose:11.9.1")


    // Lifecycle & Navigation
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.navigation:navigation-compose:2.6.0")
    implementation(libs.androidx.lifecycle.process)

    //Room
    implementation("androidx.room:room-ktx:2.7.0")
    ksp("androidx.room:room-compiler:2.7.0")


    // Networking
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("androidx.datastore:datastore-preferences:1.1.2")

    // Compose UI
    implementation("androidx.compose.runtime:runtime:1.0.0")
    implementation("androidx.compose.material3:material3:1.4.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.0.0")
    implementation("androidx.compose.material:material-icons-extended")

    // Ktor
    implementation(libs.ktor.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.logging)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.io.ktor.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // AndroidX Libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}