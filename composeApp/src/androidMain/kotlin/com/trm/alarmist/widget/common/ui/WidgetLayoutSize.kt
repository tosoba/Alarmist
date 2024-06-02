package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.LocalSize

enum class WidgetLayoutSize(val maxWidth: Dp) {
  Small(maxWidth = 260.dp),
  Medium(maxWidth = 479.dp),
  Large(maxWidth = 644.dp);

  companion object {
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
