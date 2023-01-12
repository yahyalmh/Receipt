buildscript {
    dependencies {
        classpath("com.google.firebase:firebase-crashlytics-gradle:${Version.Firebase.CRASHLYTICS_GRADLE}")
        classpath("com.android.tools.build:gradle:7.3.1")
    }
}

plugins {
    id("com.android.application") version "7.3.1" apply false
    id("com.android.library") version "7.3.1" apply false
    id("org.jetbrains.kotlin.android") version "1.6.10" apply false
    id("org.jetbrains.kotlin.jvm") version "1.7.20" apply false
    id("com.google.dagger.hilt.android") version Version.HILT apply false
    id("de.mannodermaus.android-junit5") version "1.8.2.1" apply false
    id("com.google.gms.google-services") version Version.Firebase.GMS apply false
    id("com.google.firebase.crashlytics") version Version.Firebase.CRASHLYTICS_GRADLE apply false
}