plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.ui.scan"
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk

        testInstrumentationRunner = AppConfig.androidTestInstrumentation
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Version.KOTLIN_COMPILER_EXTENSION_VERSION
    }
}

dependencies {
    compose()
    composeNavigation()
    composeViewModel()
    composeMaterial()

    coroutines()

    junit5()
    junit4()
    androidXTest()
    espresso()
    mockito()
    composeTest()

    permissions()

    camerax()
    mlCamera()

    gson()
    textRecognition()
    odml()
    mlCommonVision()
    mlLanguageId()
    mlTranslate()

    composeConstraintLayout()
    hilt()
    hiltTest()
    coilCompose()
    placeholder()

    moduleDependency(":ui:common")
    moduleDependency(":data:common")
    moduleDependency(":data:mlkit")
    moduleDependency(":data:firebase")
}