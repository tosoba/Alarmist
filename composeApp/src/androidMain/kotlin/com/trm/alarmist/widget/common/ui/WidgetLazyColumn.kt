package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.LazyListScope
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height

@Composable
fun <T> WidgetLazyColumn(
  items: List<T>,
  modifier: GlanceModifier = GlanceModifier,
  horizontalAlignment: Alignment.Horizontal = Alignment.Start,
  verticalItemsSpacing: Dp = 4.dp,
  itemId: (item: T) -> Long = { LazyListScope.UnspecifiedItemId },
  itemContent: @Composable (item: T) -> Unit,
) {
  WidgetLazyColumn(modifier = modifier, horizontalAlignment = horizontalAlignment) {
    itemsIndexed(items = items, itemId = { _, item -> itemId(item) }) { index, item ->
      Column(modifier = GlanceModifier.fillMaxWidth()) {
        itemContent(item)
        if (index != items.lastIndex) {
          Spacer(modifier = GlanceModifier.height(verticalItemsSpacing))
        }
      }
    }
  }
}

@Composable
private fun WidgetLazyColumn(
  modifier: GlanceModifier = GlanceModifier,
  horizontalAlignment: Alignment.Horizontal = Alignment.Start,
  content: LazyListScope.() -> Unit,
) {
  Box(modifier = modifier) {
    LazyColumn(horizontalAlignment = horizontalAlignment, content = content)
  }
}
