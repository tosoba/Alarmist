package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.LocalSize

sealed interface WidgetLayoutType {
  val showTitleBar: Boolean

  data class Small(override val showTitleBar: Boolean) : WidgetLayoutType

  data class Medium(override val showTitleBar: Boolean) : WidgetLayoutType

  data class Large(override val showTitleBar: Boolean) : WidgetLayoutType

  companion object {
    @Composable
    fun fromWidgetSize(size: DpSize = LocalSize.current): WidgetLayoutType {
      val width = size.width
      val showTitleBar = size.height >= WidgetLayoutCutOffPoint.MIN_SHOW_TITLE_BAR_HEIGHT.dp
      return when {
        width >= WidgetLayoutCutOffPoint.MIN_LARGE_WIDTH.dp -> Large(showTitleBar)
        width >= WidgetLayoutCutOffPoint.MIN_MEDIUM_WIDTH.dp -> Medium(showTitleBar)
        else -> Small(showTitleBar)
      }
    }
  }
}

internal object WidgetLayoutCutOffPoint {
  const val MIN_LARGE_WIDTH = 480
  const val MIN_MEDIUM_WIDTH = 260

  const val MIN_SHOW_TITLE_BAR_HEIGHT = 180
}
