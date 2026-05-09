package com.trm.alarmist.feature.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface WidgetScreenProvider {
  @Composable fun Content(modifier: Modifier)
}
