import java.io.FileInputStream
import java.util.*

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
}


val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = if (keystorePropertiesFile.isFile && keystorePropertiesFile.canRead()) {
    Properties().apply {
        load(FileInputStream(keystorePropertiesFile))
    }
} else null

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
    signingConfigs {
        if (keystoreProperties != null)
            create("release") {
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
            }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (keystoreProperties != null) {
                signingConfig = signingConfigs["release"]
            }
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
    implementation(libs.legacy.support.v4)
    implementation(libs.recyclerview)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.androidX.ktx)
    implementation(libs.androidX.appcompact)
    implementation(libs.androidX.constraintLayout)
    implementation(libs.bundles.androidX.lifecycle)
    implementation(libs.bundles.androidX.navigation)
    implementation(libs.bundles.androidX.camera)
    implementation(libs.bundles.androidX.preference)

    implementation(libs.bundles.rikkax)
    implementation(libs.vanniktech.imagecropper)
    implementation(libs.google.material)
    implementation(libs.google.zxing)
}

configurations.all {
    exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk7")
    exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
}
