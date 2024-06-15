plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.jetbrainsCompose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlinx.serialization)
  alias(libs.plugins.kotlinx.parcelize)
  alias(libs.plugins.sqlDelight)
  alias(libs.plugins.mokkery)
}

kotlin {
  androidTarget { compilations.all { kotlinOptions { jvmTarget = "1.8" } } }

  listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
    iosTarget.binaries.framework {
      baseName = "ComposeApp"
      isStatic = true

      export(libs.decompose)

      export(libs.essenty.lifecycle)
      export(libs.essenty.backHandler)
      export(libs.essenty.statekeeper)
    }
  }

  sourceSets {
    androidMain.dependencies {
      implementation(libs.androidx.activity.compose)
      implementation(libs.androidx.savedstate.savedstate)
      implementation(libs.androidx.lifecycle.lifecycleRuntime)
      implementation(libs.androidx.lifecycle.lifecycleService)

      implementation(libs.compose.ui.tooling.preview)
      implementation(libs.compose.ui.tooling)

      implementation(libs.decompose.extensions.android)
      implementation(libs.essenty.statekeeper.android)

      implementation(libs.glance.appwidget)
      implementation(libs.glance.material3)
      implementation(libs.glance.preview)

      implementation(libs.koin.android)

      implementation(libs.material)

      implementation(libs.permission.flow.compose)

      implementation(libs.sqldelight.android.driver)
    }

    getByName("androidUnitTest").dependencies {
      implementation(libs.kotlin.test)
      implementation(libs.kotlinx.coroutines.test)
      implementation(libs.sqldelight.sqlite.driver)
    }

    commonMain.dependencies {
      implementation(compose.runtime)
      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(compose.materialIconsExtended)
      implementation(compose.ui)
      implementation(compose.components.resources)
      implementation(libs.epicCalendar)

      implementation(libs.decompose)
      implementation(libs.decompose.extensions.compose)
      implementation(libs.essenty.lifecycle)
      implementation(libs.essenty.statekeeper)

      api(libs.koin.core)

      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.kotlinx.datetime)
      implementation(libs.kotlinx.serialization.json)

      implementation(libs.material3.windowSizeClass)

      implementation(libs.napier)

      implementation(libs.sqldelight.coroutines.extensions)
    }

    commonTest.dependencies {
      implementation(libs.kotlin.test)
      implementation(libs.kotlinx.coroutines.test)
    }

    iosMain.dependencies {
      implementation(libs.sqldelight.native.driver)
      implementation(libs.stately.common)

      api(libs.decompose)

      api(libs.essenty.lifecycle)
      api(libs.essenty.backHandler)
      api(libs.essenty.statekeeper)
    }
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

  buildFeatures { compose = true }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  composeOptions { kotlinCompilerExtensionVersion = "1.5.13" }
}

sqldelight {
  databases { create("AlarmistDb") { packageName.set("com.trm.alarmist.db") } }
  linkSqlite = true
}
