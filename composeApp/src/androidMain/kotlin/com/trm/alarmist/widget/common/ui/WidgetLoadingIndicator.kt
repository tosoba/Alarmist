package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box

@Composable
internal fun WidgetLoadingIndicator(modifier: GlanceModifier = GlanceModifier) {
  Box(modifier = modifier, contentAlignment = Alignment.Center) { CircularProgressIndicator() }
}
