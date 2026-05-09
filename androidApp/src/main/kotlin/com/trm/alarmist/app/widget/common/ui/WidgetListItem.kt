package com.trm.alarmist.app.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.action.Action
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.width
import androidx.glance.semantics.contentDescription
import androidx.glance.semantics.semantics
import com.trm.alarmist.app.widget.common.util.clickableIfNotNull

@Composable
fun WidgetListItem(
  modifier: GlanceModifier = GlanceModifier,
  contentSpacing: Dp = 8.dp,
  headlineContent: @Composable (() -> Unit)? = null,
  supportingContent: @Composable (() -> Unit)? = null,
  leadingContent: @Composable (() -> Unit)? = null,
  trailingContent: @Composable (() -> Unit)? = null,
  onClick: Action? = null,
  itemContentDescription: String? = null,
) {
  require(leadingContent != null || headlineContent != null)

  val listItemModifier =
    if (itemContentDescription != null) {
      modifier.semantics { contentDescription = itemContentDescription }
    } else {
      modifier
    }

  Row(
    modifier = listItemModifier.clickableIfNotNull(onClick),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    leadingContent?.let {
      it()
      if (headlineContent != null) {
        Spacer(modifier = GlanceModifier.width(contentSpacing))
      }
    }

    if (headlineContent != null) {
      Column(
        modifier = GlanceModifier.defaultWeight(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        headlineContent()
        supportingContent?.invoke()
      }
    } else {
      Spacer(modifier = GlanceModifier.defaultWeight())
    }

    trailingContent?.let {
      if (headlineContent != null) {
        Spacer(modifier = GlanceModifier.width(contentSpacing))
      }
      it()
    }
  }
}
