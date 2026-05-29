import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidKotlinMultiplatform)
  alias(libs.plugins.jetbrainsCompose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlinx.serialization)
  alias(libs.plugins.kotlinx.parcelize)
  alias(libs.plugins.sqlDelight)
  alias(libs.plugins.mokkery)
}

kotlin {
  android {
    namespace = "com.trm.alarmist"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    minSdk = libs.versions.android.minSdk.get().toInt()

    withJava()
    androidResources { enable = true }
    withHostTestBuilder {}

    compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
  }

  compilerOptions {
    freeCompilerArgs.addAll(
      "-P",
      "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=com.trm.alarmist.core.common.model.CommonParcelize",
      "-Xexpect-actual-classes",
    )
  }

  listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
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
      implementation(libs.androidx.lifecycle.lifecycleRuntimeCompose)
      implementation(libs.androidx.lifecycle.lifecycleService)
      implementation(libs.androidx.work.ktx)

      implementation(libs.compose.ui.tooling.preview)
      implementation(libs.compose.ui.tooling)

      implementation(libs.decompose.extensions.android)
      implementation(libs.essenty.statekeeper.android)

      implementation(libs.koin.android)

      implementation(libs.permission.flow.compose)

      implementation(libs.sqldelight.android.driver)
    }

    getByName("androidHostTest").dependencies {
      implementation(libs.kotlin.test)
      implementation(libs.kotlinx.coroutines.test)
      implementation(libs.sqldelight.sqlite.driver)
      implementation(libs.koin.test)
    }

    commonMain.dependencies {
      implementation(libs.compose.runtime)
      implementation(libs.compose.foundation)
      implementation(libs.compose.material3)
      implementation(libs.compose.material.iconsExtended)
      implementation(libs.compose.ui)
      implementation(libs.compose.components.resources)

      implementation(libs.decompose)
      implementation(libs.decompose.extensions.compose)
      implementation(libs.essenty.lifecycle)
      implementation(libs.essenty.statekeeper)

      api(libs.koin.core)
      api(libs.koin.compose)

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

sqldelight {
  databases { create("AlarmistDb") { packageName.set("com.trm.alarmist.db") } }
  linkSqlite = true
}

compose.resources { publicResClass = true }
