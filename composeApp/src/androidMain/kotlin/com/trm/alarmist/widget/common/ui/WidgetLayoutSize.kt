package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.LocalSize

sealed interface WidgetLayoutSize {
  val showTitleBar: Boolean

  data class Small(override val showTitleBar: Boolean) : WidgetLayoutSize

  data class Medium(override val showTitleBar: Boolean) : WidgetLayoutSize

  data class Large(override val showTitleBar: Boolean) : WidgetLayoutSize

  companion object {
    @Composable
    fun fromLocalSize(): WidgetLayoutSize {
      val width = LocalSize.current.width
      val showTitleBar = LocalSize.current.height >= 180.dp
      return when {
        width >= 480.dp -> Large(showTitleBar)
        width >= 260.dp -> Medium(showTitleBar)
        else -> Small(showTitleBar)
      }
    }
  }
}
