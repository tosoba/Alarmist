package com.trm.alarmist.widget.common.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.Action
import androidx.glance.action.clickable

@Composable
internal fun stringResource(@StringRes id: Int, args: List<Any> = emptyList()): String =
  LocalContext.current.getString(id, *args.toTypedArray())

internal val LocalIsPreviewProvider = staticCompositionLocalOf { false }

internal fun GlanceModifier.clickableIfNotNull(action: Action?): GlanceModifier =
  if (action != null) this.clickable(action) else this

@Composable
internal fun composableIfOrNull(
  condition: Boolean,
  content: @Composable () -> Unit,
): (@Composable () -> Unit)? = if (condition) content else null