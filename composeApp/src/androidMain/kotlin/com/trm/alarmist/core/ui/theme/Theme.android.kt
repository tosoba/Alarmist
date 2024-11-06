package com.trm.alarmist.core.ui.theme

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

fun isDynamicColorSchemeAvailable(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@Composable
private fun selectSchemeForContrast(darkTheme: Boolean): ColorScheme {
  val context = LocalContext.current
  return when {
    isDynamicColorSchemeAvailable() -> {
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> {
      darkScheme
    }
    else -> {
      lightScheme
    }
  }
}

@Composable
actual fun AppTheme(darkTheme: Boolean, dynamicColor: Boolean, content: @Composable () -> Unit) {
  MaterialTheme(colorScheme = selectSchemeForContrast(darkTheme = darkTheme), content = content)
}
