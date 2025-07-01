plugins {
    // Apply the Android application plugin using version catalog alias
    alias(libs.plugins.android.application)
    // Apply Google Services plugin for Firebase integration
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.resource.noteit"  // Unique namespace for your app
    compileSdk = 35                    // SDK version to compile against

    defaultConfig {
        applicationId = "com.resource.noteit"  // Unique app ID
        minSdk = 24                            // Minimum Android version supported
        targetSdk = 34                         // Targeted Android SDK version
        versionCode = 1                        // Internal version number
        versionName = "1.0"                    // Version shown to users

        // Instrumentation test runner for UI testing
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false  // Disable code shrinking for release build (enable for production)
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), // Default Proguard settings
                "proguard-rules.pro"  // Custom Proguard rules if any
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11  // Use Java 11 features in source code
        targetCompatibility = JavaVersion.VERSION_11  // Target JVM bytecode version
    }

    buildFeatures {
        viewBinding = true  // Enable view binding to easily access views without findViewById
    }
}

dependencies {
    // Core AndroidX AppCompat library for backward compatibility of UI components
    implementation(libs.appcompat)

    // Material Design components for UI styling and widgets
    implementation(libs.material)

    // Activity KTX for easier activity lifecycle and other Kotlin extensions
    implementation(libs.activity)

    // Room runtime for SQLite database operations
    implementation("androidx.room:room-runtime:2.6.1")

    // Gson library for JSON serialization/deserialization (keep only one version)
    implementation("com.google.code.gson:gson:2.10.1")

    // Navigation component fragment support for in-app navigation
    implementation(libs.navigation.fragment)

    // GridLayout for creating grid-based UI layouts
    implementation("androidx.gridlayout:gridlayout:1.0.0")

    // Navigation UI helpers for navigation components (e.g., toolbar & drawer)
    implementation(libs.navigation.ui)

    // Room annotation processor to generate database code
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // ConstraintLayout for flexible and performant UI layout
    implementation(libs.constraintlayout)

    // Firebase Realtime Database for cloud-hosted NoSQL database features
    implementation(libs.firebase.database)

    // Firebase Storage to upload and download files to cloud storage
    implementation(libs.firebase.storage)

    // JUnit for unit testing
    testImplementation(libs.junit)

    // AndroidX JUnit extension for instrumented Android tests
    androidTestImplementation(libs.ext.junit)

    // Espresso for UI testing automation
    androidTestImplementation(libs.espresso.core)
}
