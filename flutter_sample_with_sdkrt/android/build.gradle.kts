buildscript {
    extra.apply {
        set("privacy_sandbox_ui_version","1.0.0-alpha09")
        set("privacy_sandbox_tools_version","1.0.0-alpha09")
        set("privacy_sandbox_sdk_runtime_version", "1.0.0-alpha14")
        set("privacy_sandbox_activity_version","1.0.0-alpha01")
        set("compose_version", "1.5.3")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

val newBuildDir: Directory = rootProject.layout.buildDirectory.dir("../../build").get()
rootProject.layout.buildDirectory.value(newBuildDir)

subprojects {
    val newSubprojectBuildDir: Directory = newBuildDir.dir(project.name)
    project.layout.buildDirectory.value(newSubprojectBuildDir)
}
subprojects {
    project.evaluationDependsOn(":app")
}


plugins {
    id("com.android.application") version "8.8.0" apply false
    id("com.android.library") version "8.8.0" apply false
    id("androidx.privacysandbox.library") version "1.0.0-alpha02" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}