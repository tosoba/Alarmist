package com.trm.alarmist.core.common.util

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.trm.alarmist.R

internal fun Context.addAlarmDeeplinkPattern(): String =
  "${getString(R.string.deeplink_scheme)}://${getString(R.string.deeplink_host)}/${getString(R.string.deeplink_path_add_alarm)}"

internal fun Context.addAlarmDeeplinkUri(): Uri = addAlarmDeeplinkPattern().toUri()

internal fun Context.editAlarmDeeplinkPattern(id: Long): String =
  "${getString(R.string.deeplink_scheme)}://${getString(R.string.deeplink_host)}/${getString(R.string.deeplink_path_edit_alarm)}/$id"

internal fun Context.editAlarmDeeplinkUri(id: Long): Uri = editAlarmDeeplinkPattern(id).toUri()

fun Context.stopwatchDeeplinkUri(): Uri =
  "${getString(R.string.deeplink_scheme)}://${getString(R.string.deeplink_host)}/${getString(R.string.deeplink_path_stopwatch)}"
    .toUri()
