package com.trm.alarmist.widget.common.ui

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
import com.trm.alarmist.widget.common.util.clickableIfNotNull

@Composable
fun WidgetListItem(
  headlineContent: @Composable () -> Unit,
  modifier: GlanceModifier = GlanceModifier,
  contentSpacing: Dp = 16.dp,
  supportingContent: @Composable (() -> Unit)? = null,
  leadingContent: @Composable (() -> Unit)? = null,
  trailingContent: @Composable (() -> Unit)? = null,
  onClick: Action? = null,
  itemContentDescription: String? = null,
) {
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
      Spacer(modifier = GlanceModifier.width(contentSpacing))
    }

    Column(
      modifier = GlanceModifier.defaultWeight(),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      headlineContent()
      supportingContent?.let { it() }
    }

    trailingContent?.let {
      Spacer(modifier = GlanceModifier.width(contentSpacing))
      it()
    }
  }
}
