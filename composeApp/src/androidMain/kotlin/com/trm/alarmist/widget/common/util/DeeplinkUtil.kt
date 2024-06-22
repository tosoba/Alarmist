package com.trm.alarmist.widget.common.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import androidx.glance.LocalContext
import androidx.glance.action.Action
import androidx.glance.appwidget.action.actionStartActivity
import com.trm.alarmist.MainActivity
import com.trm.alarmist.R

internal fun Context.editAlarmDeeplinkPattern(id: Long): String =
  "${getString(R.string.deeplink_scheme)}://${getString(R.string.deeplink_host)}/${getString(R.string.deeplink_path_edit_alarm)}/$id"

internal fun Context.editAlarmDeeplinkUri(id: Long): Uri = editAlarmDeeplinkPattern(id).toUri()

@Composable
internal fun deepLinkAction(uri: Uri): Action =
  actionStartActivity(
    Intent(Intent.ACTION_VIEW, uri, LocalContext.current, MainActivity::class.java)
  )
