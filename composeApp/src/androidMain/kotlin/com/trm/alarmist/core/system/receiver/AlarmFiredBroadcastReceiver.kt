package com.trm.alarmist.core.system.receiver

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.alarm_not_fired_due_to_permission
import alarmist.composeapp.generated.resources.named_alarm_not_fired_due_to_permission
import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.trm.alarmist.core.common.domain.model.AlarmFireSettings
import com.trm.alarmist.core.common.util.EXTRA_ALARM_FIRE_SETTINGS
import com.trm.alarmist.core.common.util.getStringBlocking
import com.trm.alarmist.core.common.util.launch
import com.trm.alarmist.core.common.util.requireAlarmFireSettings
import com.trm.alarmist.core.domain.usecase.IsAlarmScheduledToFireAtDateTimeUseCase
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnDismissUseCase
import com.trm.alarmist.core.system.AndroidAlarmService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmFiredBroadcastReceiver : BroadcastReceiver(), KoinComponent {
  private val isAlarmScheduledToFireAtDateTimeUseCase: IsAlarmScheduledToFireAtDateTimeUseCase by
    inject()
  private val updateAlarmOnDismissUseCase: UpdateAlarmOnDismissUseCase by inject()

  override fun onReceive(context: Context, intent: Intent?) {
    if (intent?.action != ACTION_ALARM_FIRED) return

    val settings = intent.requireAlarmFireSettings()
    launch {
      if (!isAlarmScheduledToFireAtDateTimeUseCase(settings.id, settings.fireOnDateTime))
        return@launch

      if (
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
          PackageManager.PERMISSION_GRANTED
      ) {
        updateAlarmOnDismissUseCase(settings.id, settings.fireOnDateTime)

        withContext(Dispatchers.Main) {
          Toast.makeText(
              context,
              settings.name?.let {
                getStringBlocking(Res.string.named_alarm_not_fired_due_to_permission, it)
              } ?: getStringBlocking(Res.string.alarm_not_fired_due_to_permission),
              Toast.LENGTH_LONG,
            )
            .show()
        }
      } else {
        ContextCompat.startForegroundService(
          context,
          Intent(context, AndroidAlarmService::class.java).putExtras(intent),
        )
      }
    }
  }

  companion object {
    private const val ACTION_ALARM_FIRED = "ALARM_FIRED"

    fun intent(context: Context): Intent =
      Intent(context, AlarmFiredBroadcastReceiver::class.java).setAction(ACTION_ALARM_FIRED)

    fun intent(context: Context, settings: AlarmFireSettings): Intent =
      intent(context).putExtra(EXTRA_ALARM_FIRE_SETTINGS, settings)
  }
}
