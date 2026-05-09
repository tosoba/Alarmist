plugins {
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.jetbrainsCompose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlinx.serialization)
  alias(libs.plugins.kotlinx.parcelize)
}

dependencies {
  implementation(project(":composeApp"))
  
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.lifecycle.lifecycleRuntimeCompose)
  
  // Compose Multiplatform libraries
  implementation(libs.compose.ui)
  implementation(libs.compose.foundation)
  implementation(libs.compose.material3)
  implementation(libs.compose.runtime)
  implementation(libs.compose.ui.tooling.preview)
  implementation(libs.compose.components.resources)
  implementation(libs.material3.windowSizeClass)
  
  // Android-specific Compose libraries
  implementation("androidx.compose.material:material-icons-extended:1.7.5")
  
  implementation(libs.kotlinx.datetime)
  
  implementation(libs.decompose)
  implementation(libs.decompose.extensions.android)
  implementation(libs.decompose.extensions.compose)
  
  implementation(libs.koin.android)
  
  implementation(libs.androidx.work.ktx)
  implementation(libs.glance.appwidget)
  implementation(libs.glance.material3)
  
  implementation(libs.napier)
}

android {
  namespace = "com.trm.alarmist.app"
  compileSdk = libs.versions.android.compileSdk.get().toInt()

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
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
}
