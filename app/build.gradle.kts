@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "io.github.a13e300.scanner"
    compileSdk = 33

    defaultConfig {
        applicationId = "io.github.a13e300.scanner"
        minSdk = 22
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.2")
    implementation(libs.androidX.ktx)
    implementation(libs.androidX.appcompact)
    implementation(libs.androidX.constraintLayout)
    implementation(libs.bundles.androidX.lifecycle)
    implementation(libs.bundles.androidX.navigation)
    implementation(libs.bundles.androidX.camera)

    implementation(libs.bundles.rikkax)
    implementation(libs.vanniktech.imagecropper)
    implementation(libs.google.material)
    implementation(libs.google.zxing)
}

configurations.all {
    exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk7")
    exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
}
