package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceTheme
import androidx.glance.text.FontWeight
import androidx.glance.text.TextStyle

object WidgetTextStyles {
  val leadingText: TextStyle
    @Composable
    get() =
      TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize =
          if (WidgetLayoutSize.fromLocalSize() == WidgetLayoutSize.Small) {
            18.sp
          } else {
            22.sp // M3 Title Large
          },
        color = GlanceTheme.colors.onSurface,
      )

  val titleText: TextStyle
    @Composable
    get() =
      TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize =
          if (WidgetLayoutSize.fromLocalSize() == WidgetLayoutSize.Small) {
            14.sp // M3 Title Small
          } else {
            16.sp // M3 Title Medium
          },
        color = GlanceTheme.colors.onSurface,
      )

  val supportingText: TextStyle
    @Composable
    get() =
      TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp, // M3 Label Medium
        color = GlanceTheme.colors.secondary,
      )
}
