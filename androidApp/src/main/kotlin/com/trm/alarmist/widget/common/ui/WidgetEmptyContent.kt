package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.appwidget.components.FilledButton
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.text.Text
import com.trm.alarmist.widget.common.util.emptyActionIfPreviewOrElse

@Composable
fun WidgetEmptyContent(
  emptyText: String,
  actionButtonText: String,
  actionButtonIcon: Int?,
  actionButtonOnClick: Action,
) {
  Column(
    verticalAlignment = Alignment.CenterVertically,
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = GlanceModifier.fillMaxSize(),
  ) {
    Text(text = emptyText, style = WidgetTextStyles.titleText)

    Spacer(modifier = GlanceModifier.height(8.dp))

    FilledButton(
      text = actionButtonText,
      icon = actionButtonIcon?.let(::ImageProvider),
      onClick = emptyActionIfPreviewOrElse { actionButtonOnClick },
    )
  }
}
