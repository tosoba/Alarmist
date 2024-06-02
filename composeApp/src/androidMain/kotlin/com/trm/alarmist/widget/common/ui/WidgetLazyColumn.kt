package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.LazyListScope
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height

@Composable
fun WidgetLazyColumn(
  modifier: GlanceModifier = GlanceModifier,
  horizontalAlignment: Alignment.Horizontal = Alignment.Start,
  content: LazyListScope.() -> Unit,
) {
  Box(modifier = modifier.fillMaxSize().cornerRadius(16.dp)) {
    LazyColumn(horizontalAlignment = horizontalAlignment, content = content)
  }
}

@Composable
fun <T> WidgetLazyColumn(
  items: List<T>,
  modifier: GlanceModifier = GlanceModifier,
  horizontalAlignment: Alignment.Horizontal = Alignment.Start,
  verticalItemsSpacing: Dp = 4.dp,
  itemContent: @Composable (item: T) -> Unit,
) {
  WidgetLazyColumn(modifier, horizontalAlignment) {
    itemsIndexed(items) { index, item ->
      Column(modifier = GlanceModifier.fillMaxWidth()) {
        itemContent(item)
        if (index != items.lastIndex) {
          Spacer(modifier = GlanceModifier.height(verticalItemsSpacing))
        }
      }
    }
  }
}
