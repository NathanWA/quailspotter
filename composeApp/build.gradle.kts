import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    kotlin("native.cocoapods")
}

kotlin {
    androidTarget()
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    )

    cocoapods {
        summary = "Compose App Module"
        homepage = "https://github.com/JetBrains/compose-multiplatform"
        version = "1.0"
        ios.deploymentTarget = "15.0"
        framework {
            baseName = "ComposeApp"
            isStatic = true
        }
        pod("TensorFlowLiteObjC") {
            version = "~> 2.14.0"
            moduleName = "TFLTensorFlowLite"
            packageName = "cocoapods.TFLTensorFlowLite"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        pod("TensorFlowLiteObjC/CoreML") {
            version = "~> 2.14.0"
            moduleName = "TFLTensorFlowLite"
            packageName = "cocoapods.TFLTensorFlowLiteCoreML"
        }
        pod("TensorFlowLiteObjC/Metal") {
            version = "~> 2.14.0"
            moduleName = "TFLTensorFlowLite"
            packageName = "cocoapods.TFLTensorFlowLiteMetal"
        }
        pod("TensorFlowLiteC") {
            version = "~> 2.14.0"
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(projects.shared)
            implementation(libs.peekaboo.ui)
            implementation(libs.peekaboo.image.picker)
        }

        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.startup)
        }

        iosMain.dependencies {
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

compose {
    resources {
        packageOfResClass = "com.nathan.quailspotter"
        generateResClass = always
    }
}

android {
    namespace = "com.nathan.quailspotter"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.nathan.quailspotter"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(11)
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}
