plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.roborazzi)
}

android {

    namespace = "com.example"

    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {

        applicationId = "com.aistudio.earnago.app"

        minSdk = 24
        targetSdk = 36

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val geminiKey =
            (System.getenv("GEMINI_API_KEY") ?: "")
                .replace("\"", "")
                .trim()

        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"$geminiKey\""
        )
    }

    signingConfigs {

        create("debugConfig") {

            storeFile = file("${rootDir}/debug.keystore")
            storePassword = "android"

            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }

        create("release") {

            val keystorePath =
                System.getenv("KEYSTORE_PATH")
                    ?: "${rootDir}/my-upload-key.jks"

            val keyStoreFile = file(keystorePath)

            if (
                keyStoreFile.exists() &&
                !System.getenv("STORE_PASSWORD").isNullOrBlank()
            ) {

                storeFile = keyStoreFile
                storePassword = System.getenv("STORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS") ?: "upload"
                keyPassword = System.getenv("KEY_PASSWORD")

            } else {

                storeFile = file("${rootDir}/debug.keystore")
                storePassword = "android"

                keyAlias = "androiddebugkey"
                keyPassword = "android"
            }
        }
    }

    buildTypes {

        debug {

            signingConfig =
                signingConfigs.getByName("debugConfig")
        }

        release {

            signingConfig =
                signingConfigs.getByName("release")

            isMinifyEnabled = false
            isCrunchPngs = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {

        sourceCompatibility =
            JavaVersion.VERSION_11

        targetCompatibility =
            JavaVersion.VERSION_11
    }

    buildFeatures {

        compose = true
        buildConfig = true
    }

    testOptions {

        unitTests {

            isIncludeAndroidResources = true
        }
    }

    dependenciesInfo {

        includeInApk = false
        includeInBundle = true
    }
}

dependencies {

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.coil.compose)

    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    implementation(libs.re
