buildscript {
    dependencies {
        classpath("com.google.firebase:firebase-crashlytics-gradle:${Version.Firebase.CRASHLYTICS_GRADLE}")
        classpath("com.android.tools.build:gradle:7.4.0")
    }
    repositories {
        mavenCentral()
    }
}

plugins {
    androidApplication()
    androidLibrary()
    kotlinAndroid()
    kotilnJWM()
    hiltPlugin()
    junit5GradlePlugin()
    googleServices()
    crashlyticsPlugin()
    gradleVersions()
}