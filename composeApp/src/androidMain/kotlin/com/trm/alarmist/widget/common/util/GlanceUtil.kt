package com.trm.alarmist.widget.common.util

import android.content.res.Resources
import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding

@Composable
internal fun widgetBackgroundModifier() =
  GlanceModifier.fillMaxSize()
    .padding(16.dp)
    .appWidgetBackground()
    .background(GlanceTheme.colors.primaryContainer)
    .widgetBackgroundCornerRadius()

internal fun GlanceModifier.widgetBackgroundCornerRadius(): GlanceModifier {
  if (Build.VERSION.SDK_INT >= 31) {
    cornerRadius(android.R.dimen.system_app_widget_background_radius)
  } else {
    cornerRadius(16.dp)
  }
  return this
}

internal fun GlanceModifier.widgetInnerCornerRadius(): GlanceModifier {
  if (Build.VERSION.SDK_INT >= 31) {
    cornerRadius(android.R.dimen.system_app_widget_inner_radius)
  } else {
    cornerRadius(8.dp)
  }
  return this
}

@Composable
internal fun stringResource(@StringRes id: Int, args: List<Any> = emptyList()): String =
  LocalContext.current.getString(id, *args.toTypedArray())

internal val Float.toPx: Float
  get() = this * Resources.getSystem().displayMetrics.density
internal val Float.toDp: Float
  get() = this / Resources.getSystem().displayMetrics.density

internal val smallFontSize: Float
  @Composable
  get() =
    with(LocalSize.current) {
      when {
        width > 200.dp && height > 250.dp -> 16f
        width > 175.dp && height > 200.dp -> 14f
        else -> 12f
      }
    }

internal val mediumFontSize: Float
  @Composable
  get() =
    with(LocalSize.current) {
      when {
        width > 200.dp && height > 250.dp -> 22f
        width > 175.dp && height > 200.dp -> 20f
        else -> 18f
      }
    }

internal val largeFontSize: Float
  @Composable
  get() =
    with(LocalSize.current) {
      when {
        width > 200.dp && height > 250.dp -> 30f
        width > 175.dp && height > 200.dp -> 26f
        else -> 22f
      }
    }

internal val LocalIsPreviewProvider = staticCompositionLocalOf { false }

internal fun GlanceModifier.clickableIfNotNull(action: Action?): GlanceModifier =
  if (action != null) this.clickable(action) else this
