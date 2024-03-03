package com.trm.alarmist.core.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootCompletedBroadcastReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {
    if (intent?.action != Intent.ACTION_BOOT_COMPLETED && intent?.action != Intent.ACTION_REBOOT) {
      return
    }
    // TODO: check any missed alarms
  }
}
