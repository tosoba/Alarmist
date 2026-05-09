package com.trm.alarmist.feature.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class AndroidWidgetScreenProvider : WidgetScreenProvider {
  @Composable
  override fun Content(modifier: Modifier) {
    AndroidWidgetsContent(modifier = modifier)
  }
}
