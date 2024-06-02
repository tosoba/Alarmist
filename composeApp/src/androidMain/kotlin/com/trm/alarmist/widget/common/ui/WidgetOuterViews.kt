package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.ColumnScope
import com.trm.alarmist.widget.common.util.widgetBackgroundModifier

@Composable
internal fun WidgetOuterColumn(
  modifier: GlanceModifier = GlanceModifier,
  verticalAlignment: Alignment.Vertical = Alignment.Top,
  horizontalAlignment: Alignment.Horizontal = Alignment.Start,
  content: @Composable ColumnScope.() -> Unit,
) {
  Column(
    modifier = widgetBackgroundModifier().then(modifier),
    verticalAlignment = verticalAlignment,
    horizontalAlignment = horizontalAlignment,
    content = content,
  )
}
