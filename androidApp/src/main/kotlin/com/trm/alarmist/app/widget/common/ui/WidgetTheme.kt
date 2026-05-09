package com.trm.alarmist.app.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.glance.GlanceTheme
import androidx.glance.material3.ColorProviders
import com.trm.alarmist.core.ui.theme.darkScheme
import com.trm.alarmist.core.ui.theme.isDynamicColorSchemeAvailable
import com.trm.alarmist.core.ui.theme.lightScheme

@Composable
fun WidgetTheme(content: @Composable () -> Unit) {
  GlanceTheme(
    colors =
      if (isDynamicColorSchemeAvailable()) GlanceTheme.colors
      else ColorProviders(light = lightScheme, dark = darkScheme),
    content = content,
  )
}
