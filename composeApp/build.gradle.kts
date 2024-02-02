import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.jetbrainsCompose)
  alias(libs.plugins.kotlinx.serialization)
  alias(libs.plugins.sqlDelight)
}

kotlin {
  androidTarget { compilations.all { kotlinOptions { jvmTarget = "1.8" } } }

  listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
    iosTarget.binaries.framework {
      baseName = "ComposeApp"
      isStatic = true
    }
  }

  sourceSets {
    androidMain.dependencies {
      implementation(libs.androidx.activity.compose)
      implementation(libs.androidx.savedstate.savedstate)
      implementation(libs.androidx.lifecycle.lifecycleRuntime)
      
      implementation(libs.compose.ui.tooling.preview)

      implementation(libs.decompose.extensions.android)
      implementation(libs.decompose.extensions.compose)

      implementation(libs.essenty.statekeeper.android)

      implementation(libs.koin.android)
      implementation(libs.koin.core)

      implementation(libs.sqldelight.android.driver)
    }

    commonMain.dependencies {
      implementation(compose.runtime)
      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(compose.ui)
      @OptIn(ExperimentalComposeLibrary::class) implementation(compose.components.resources)

      implementation(libs.decompose)
      implementation(libs.essenty.lifecycle)
      implementation(libs.essenty.statekeeper)

      implementation(libs.koin.core)

      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.kotlinx.datetime)
      implementation(libs.kotlinx.serialization.json)

      implementation(libs.sqldelight.coroutines.extensions)
    }

    iosMain.dependencies { implementation(libs.sqldelight.native.driver) }
  }
}

android {
  namespace = "com.trm.alarmist"
  compileSdk = libs.versions.android.compileSdk.get().toInt()

  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  sourceSets["main"].res.srcDirs("src/androidMain/res")
  sourceSets["main"].resources.srcDirs("src/commonMain/resources")

  defaultConfig {
    applicationId = "com.trm.alarmist"
    minSdk = libs.versions.android.minSdk.get().toInt()
    targetSdk = libs.versions.android.targetSdk.get().toInt()
    versionCode = 1
    versionName = "1.0"
  }

  packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }

  buildTypes { getByName("release") { isMinifyEnabled = false } }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  dependencies { debugImplementation(libs.compose.ui.tooling) }
}

sqldelight { databases { create("AlarmistDb") { packageName.set("com.trm.alarmist.db") } } }
