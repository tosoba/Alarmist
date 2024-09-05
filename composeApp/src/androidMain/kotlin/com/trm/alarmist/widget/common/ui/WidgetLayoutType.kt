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
      val showTitleBar = size.height >= WidgetLayoutCutOff.SHOW_TITLE_BAR
      return when {
        width >= WidgetLayoutCutOff.LARGE -> Large(showTitleBar)
        width >= WidgetLayoutCutOff.MEDIUM -> Medium(showTitleBar)
        else -> Small(showTitleBar)
      }
    }
  }
}

internal object WidgetLayoutCutOff {
  val LARGE = 480.dp
  val MEDIUM = 260.dp

  val SHOW_TITLE_BAR = 180.dp
}
