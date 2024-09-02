package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceTheme
import androidx.glance.text.FontWeight
import androidx.glance.text.TextStyle
import com.trm.alarmist.widget.common.util.LocalWidgetLayoutSize

object WidgetTextStyles {
  @Composable
  fun leadingText(fontWeight: FontWeight = FontWeight.Medium): TextStyle =
    TextStyle(
      fontWeight = fontWeight,
      fontSize =
        if (LocalWidgetLayoutSize.current is WidgetLayoutSize.Small) {
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
        fontWeight = FontWeight.Normal,
        fontSize =
          if (LocalWidgetLayoutSize.current is WidgetLayoutSize.Small) {
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

  val largeHeaderText: TextStyle
    @Composable
    get() =
      TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        color = GlanceTheme.colors.onSurface,
      )
}
