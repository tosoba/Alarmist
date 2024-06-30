package com.trm.alarmist.widget.common

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.trm.alarmist.R

class WidgetPinnedReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    Toast.makeText(
        context,
        context.getString(R.string.widget_pinned_success_info),
        Toast.LENGTH_SHORT,
      )
      .show()
  }

  companion object {
    fun pendingIntent(context: Context): PendingIntent =
      PendingIntent.getBroadcast(
        context,
        0,
        Intent(context, WidgetPinnedReceiver::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
      )
  }
}
