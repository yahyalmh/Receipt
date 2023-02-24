import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.version
import org.gradle.plugin.use.PluginDependenciesSpec

object Plugins {
    const val ANDROID_APPLICATION = "com.android.application"
    const val ANDROID_LIBRARAY = "com.android.library"
    const val KOTLIN_ANDROID = "org.jetbrains.kotlin.android"
    const val KOTLIN_JVM = "org.jetbrains.kotlin.jvm"
    const val HILT_PLUGIN = "com.google.dagger.hilt.android"
    const val JUNIT5_GRADLE_PLUGIN = "de.mannodermaus.android-junit5"
    const val GOOGLE_SERVICES = "com.google.gms.google-services"
    const val CRASHLYTIS = "com.google.firebase.crashlytics"
    const val GRADLE_VERSIONS = "com.github.ben-manes.versions"
}

fun PluginDependenciesSpec.androidApplication() {
    id(Plugins.ANDROID_APPLICATION) version "7.4.0" apply false
}

fun PluginDependenciesSpec.androidLibrary() {
    id(Plugins.ANDROID_LIBRARAY) version "7.4.0" apply false
}

fun PluginDependenciesSpec.kotlinAndroid() {
    id(Plugins.KOTLIN_ANDROID) version "1.7.21" apply false
}

fun PluginDependenciesSpec.kotilnJWM() {
    id(Plugins.KOTLIN_JVM) version "1.7.21" apply false
}

fun PluginDependenciesSpec.hiltPlugin() {
    id(Plugins.HILT_PLUGIN) version Version.HILT apply false
}

fun PluginDependenciesSpec.junit5GradlePlugin() {
    id(Plugins.JUNIT5_GRADLE_PLUGIN) version "1.8.2.1" apply false
}

fun PluginDependenciesSpec.googleServices() {
    id(Plugins.GOOGLE_SERVICES) version Version.Firebase.GMS apply false
}

fun PluginDependenciesSpec.crashlyticsPlugin() {
    id(Plugins.CRASHLYTIS) version Version.Firebase.CRASHLYTICS_GRADLE apply false
}

fun PluginDependenciesSpec.gradleVersions() {
    id(Plugins.GRADLE_VERSIONS) version "0.45.0"
}