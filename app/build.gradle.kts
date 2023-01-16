plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.firebase.crashlytics")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.receipt"
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        applicationId = "com.example.receipt"
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName

        testInstrumentationRunner = AppConfig.androidTestInstrumentation
        vectorDrawables {
            useSupportLibrary = true
        }
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

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,LICENSE}"
            pickFirsts += "/META-INF/{AL2.0,LGPL2.1,LICENSE*}"
        }
    }
}


dependencies {
    androidxCore()
    compose()
    composeMaterial()
    composeNavigation()
    camerax()
    mlCamera()
    gson()
    textRecognition()
    permissions()
    coroutines()
    odml()
    mlCommonVision()
    mlLanguageId()
    mlTranslate()
    hilt()
    crashlytics()
    junit4()

    moduleDependency(":ui:main")
//    implementation ("com.google.guava:guava:27.1-android")
}
