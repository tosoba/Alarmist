package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.appwidget.components.CircleIconButton
import com.trm.alarmist.R
import com.trm.alarmist.widget.common.util.stringResource

@Composable
internal fun WidgetRefreshButton(onClick: Action) {
  CircleIconButton(
    imageProvider = ImageProvider(R.drawable.refresh),
    contentDescription = stringResource(R.string.refresh),
    contentColor = GlanceTheme.colors.secondary,
    backgroundColor = null,
    onClick = onClick,
  )
}
