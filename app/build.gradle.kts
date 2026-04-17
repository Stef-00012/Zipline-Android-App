plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.stefdp.zipline"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.stefdp.zipline"
        minSdk = 26
        targetSdk = 36
        versionCode = 28
        versionName = "2.0.2" // TODO: reminder, when updating here, update the APP_VERSION constant in MainActivity too
        ndkVersion = "29.0.14206865"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            manifestPlaceholders["appName"] = "Zipline"

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            isDebuggable = false

            ndk {
                debugSymbolLevel = "FULL"
            }

//            signingConfig = signingConfigs.getByName("debug")
        }

        debug {
            isMinifyEnabled = false
            isShrinkResources = false

            isDebuggable = true

            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            manifestPlaceholders["appName"] = "Zipline (Debug)"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.core.splashscreen)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // HTTP Requests
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.scalars)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.logging.interceptor)

    // Charts
//    implementation(libs.vico.compose)
//    implementation(libs.vico.compose.m3)
    implementation(libs.compose.charts)

    // Skeleton Loading
    implementation(libs.compose.shimmer)

    // Date to Human Readable
    implementation(libs.human.readable)

    // Widgets
    implementation(libs.glance.appwidget)
    implementation(libs.glance.material3)
    implementation(libs.glance.preview)
    implementation(libs.glance.appwidget.preview)
    debugImplementation(libs.glance.preview)
    debugImplementation(libs.glance.appwidget.preview)

    // Network Image Display
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.coil.gif)
    implementation(libs.coil.svg)
    implementation(libs.coil.video)

    // Network Video Display
    //// Core
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.common.ktx)

    //// UI
    implementation(libs.media3.ui)
    implementation(libs.media3.ui.compose)
    implementation(libs.media3.ui.compose.material3)

    //// Extensions
    implementation(libs.media3.exoplayer.dash)
    implementation(libs.media3.exoplayer.hls)
    implementation(libs.media3.extractor)
    implementation(libs.media3.session)
    implementation(libs.media3.datasource.okhttp)

    // QR Code
    implementation(libs.zxing.core)

    // SemVer
    implementation(libs.semver)
}