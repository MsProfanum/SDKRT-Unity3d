plugins {
    id("com.android.application")
    id("kotlin-android")
    // The Flutter Gradle Plugin must be applied after the Android and Kotlin Gradle plugins.
    id("dev.flutter.flutter-gradle-plugin")
}

android {
    privacySandbox {
        enable = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3" // Update to the desired Compose Compiler version
    }

    namespace = "com.example.flutter_sample_with_sdkrt"


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    defaultConfig {
        // TODO: Specify your own unique Application ID (https://developer.android.com/studio/build/application-id.html).
        applicationId = "com.example.flutter_sample_with_sdkrt"
        // You can update the following values to match your application needs.
        // For more information, see: https://flutter.dev/to/review-gradle-config.
        minSdk = 24
        compileSdk = 35
        targetSdk = 34
        versionCode = flutter.versionCode
        versionName = flutter.versionName
    }

    buildTypes {
        release {
            // TODO: Add your own signing config for the release build.
            // Signing with the debug keys for now, so `flutter run --release` works.
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

val privacy_sandbox_ui_version = rootProject.extra["privacy_sandbox_ui_version"]
val privacy_sandbox_sdk_runtime_version = rootProject.extra["privacy_sandbox_sdk_runtime_version"]
val privacy_sandbox_activity_version = rootProject.extra["privacy_sandbox_activity_version"]
val compose_version = rootProject.extra["compose_version"]

dependencies {
    implementation(project(":ra-sdk"))
//    implementation(project(":re-sdk-bundle"))
    implementation("androidx.privacysandbox.sdkruntime:sdkruntime-client:$privacy_sandbox_sdk_runtime_version")
    // This is required to display banner ads using the SandboxedUiAdapter interface.
    implementation("androidx.privacysandbox.ui:ui-core:$privacy_sandbox_ui_version")
    implementation("androidx.privacysandbox.ui:ui-client:$privacy_sandbox_ui_version")

    // This is required to use SDK ActivityLaunchers.
    implementation("androidx.privacysandbox.activity:activity-core:$privacy_sandbox_activity_version")
    implementation("androidx.privacysandbox.activity:activity-client:$privacy_sandbox_activity_version")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.compose.ui:ui:$compose_version")
    implementation("androidx.compose.material:material:$compose_version")
    implementation("androidx.core:core-ktx:1.2.0")
//    implementation(project(":re-sdk"))
}

flutter {
    source = "../.."
}
