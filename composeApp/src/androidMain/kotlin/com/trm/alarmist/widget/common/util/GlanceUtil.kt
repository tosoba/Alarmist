package com.trm.alarmist.widget.common.util

import android.util.TypedValue
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.Action
import androidx.glance.action.clickable
import com.trm.alarmist.widget.common.ui.WidgetLayoutSize

@Composable
internal fun stringResource(@StringRes id: Int, args: List<Any> = emptyList()): String =
  LocalContext.current.getString(id, *args.toTypedArray())

@Composable
internal fun integerResource(@IntegerRes id: Int): Int =
  LocalContext.current.resources.getInteger(id)

internal val LocalIsPreview = staticCompositionLocalOf { false }

internal val LocalWidgetLayoutSize = staticCompositionLocalOf { WidgetLayoutSize.Small }

internal fun GlanceModifier.clickableIfNotNull(action: Action?): GlanceModifier =
  if (action != null) this.clickable(action) else this

@Composable
internal fun composableIfOrNull(
  condition: Boolean,
  content: @Composable () -> Unit,
): (@Composable () -> Unit)? = if (condition) content else null

@Composable
fun Float.spToDp(): Dp {
  val context = LocalContext.current
  return Dp(
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, context.resources.displayMetrics) /
      context.resources.displayMetrics.density
  )
}

internal fun interface AppWidgetIdProvider {
  fun getAppWidgetId(glanceId: GlanceId): Int
}

internal val LocalAppWidgetIdProvider = staticCompositionLocalOf { AppWidgetIdProvider { 0 } }
