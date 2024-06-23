package com.trm.alarmist.widget.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class WidgetPinnedReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    Toast.makeText(context, "Widget pinned. Check it on your homescreen.", Toast.LENGTH_SHORT)
      .show()
  }
}
