package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.RowScope
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.unit.ColorProvider

@Composable
fun ContentTitleBar(
  startIcon: ImageProvider,
  iconColor: ColorProvider? = GlanceTheme.colors.onSurface,
  modifier: GlanceModifier = GlanceModifier,
  actions: @Composable RowScope.() -> Unit = {},
  content: @Composable RowScope.() -> Unit,
) {
  @Composable
  fun StartIcon() {
    Box(GlanceModifier.size(48.dp).padding(start = 2.dp), contentAlignment = Alignment.Center) {
      Image(
        modifier = GlanceModifier.size(24.dp),
        provider = startIcon,
        contentDescription = "",
        colorFilter = iconColor?.let { ColorFilter.tint(iconColor) },
      )
    }
  }

  Row(
    modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
    verticalAlignment = Alignment.Vertical.CenterVertically,
  ) {
    StartIcon()
    content()
    actions()
  }
}
