package com.trm.alarmist.app.widget.common.system

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.trm.alarmist.core.system.AlarmScheduler
import com.trm.alarmist.core.system.WidgetManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WidgetUpdateAlarmReceiver : BroadcastReceiver(), KoinComponent {
  private val widgetManager: WidgetManager by inject()
  private val alarmScheduler: AlarmScheduler by inject()

  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action != ACTION_ALARM_UPDATE) return

    widgetManager.updateDateSensitiveWidgets()
    alarmScheduler.scheduleNextWidgetUpdate()
  }

  companion object {
    private const val ACTION_ALARM_UPDATE = "ALARM_UPDATE"
    private const val REQUEST_CODE = -100

    fun pendingIntent(context: Context): PendingIntent =
      PendingIntent.getBroadcast(
        context,
        REQUEST_CODE,
        Intent(context, WidgetUpdateAlarmReceiver::class.java).setAction(ACTION_ALARM_UPDATE),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
      )
  }
}
