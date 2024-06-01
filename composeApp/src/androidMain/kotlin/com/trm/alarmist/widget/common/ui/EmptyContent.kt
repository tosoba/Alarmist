package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.appwidget.components.FilledButton
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

@Composable
fun EmptyContent(
  noDataIconRes: Int,
  noDataText: String,
  actionButtonText: String,
  actionButtonIcon: Int,
  actionButtonOnClick: Action,
) {
  @Composable fun showIcon() = LocalSize.current.height >= 180.dp

  Column(
    verticalAlignment = Alignment.CenterVertically,
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = GlanceModifier.fillMaxSize(),
  ) {
    if (showIcon()) {
      Image(
        provider = ImageProvider(noDataIconRes),
        colorFilter = ColorFilter.tint(GlanceTheme.colors.secondary),
        contentDescription = null,
      )

      Spacer(modifier = GlanceModifier.height(8.dp))
    }

    Text(
      text = noDataText,
      style =
        TextStyle(
          fontWeight = FontWeight.Medium,
          color = GlanceTheme.colors.onSurface,
          fontSize = 16.sp, // M3 - title/medium
        ),
    )

    Spacer(modifier = GlanceModifier.height(8.dp))

    FilledButton(
      text = actionButtonText,
      icon = ImageProvider(actionButtonIcon),
      onClick = actionButtonOnClick,
    )
  }
}
