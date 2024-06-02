package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.LocalSize

/**
 * Reference breakpoints for deciding on widget style to display e.g. list / grid etc.
 *
 * In this layout, only width breakpoints are used to scale the layout.
 */
enum class WidgetLayoutSize(val maxWidth: Dp) {
  // Single column vertical list without images or trailing button in this size.
  Small(maxWidth = 260.dp),

  // Single column horizontal list with images and optional trailing button if exists.
  Medium(maxWidth = 479.dp),

  // 2 Column Grid of horizontal list items. Images are always shown; trailing button is shown if
  // it fits.
  Large(maxWidth = 644.dp);

  companion object {
    /**
     * Returns the corresponding [WidgetLayoutSize] to be considered for the current widget size.
     */
    @Composable
    fun fromLocalSize(): WidgetLayoutSize {
      val width = LocalSize.current.width
      return when {
        width >= Medium.maxWidth -> Large
        width >= Small.maxWidth -> Medium
        else -> Small
      }
    }

    @Composable fun showTitleBar(): Boolean = LocalSize.current.height >= 180.dp
  }
}
