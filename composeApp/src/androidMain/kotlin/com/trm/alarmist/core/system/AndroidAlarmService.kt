package com.trm.alarmist.core.system

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.dismiss
import alarmist.composeapp.generated.resources.snooze
import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.trm.alarmist.AlarmFiredActivity
import com.trm.alarmist.R
import com.trm.alarmist.core.common.util.getSerializable
import com.trm.alarmist.core.common.util.getStringBlocking
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnDismissUseCase
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnSnoozeUseCase
import io.github.aakira.napier.Napier
import java.util.Timer
import java.util.TimerTask
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AndroidAlarmService : LifecycleService(), KoinComponent {
  private val updateAlarmOnDismissUseCase: UpdateAlarmOnDismissUseCase by inject()
  private val updateAlarmOnSnoozeUseCase: UpdateAlarmOnSnoozeUseCase by inject()

  private var mediaPlayer: MediaPlayer? = null
  private var vibrator: Vibrator? = null

  private var isPlaying = false
  private val missedAlarms = mutableListOf<AlarmFireSettings>()

  private val alarmFiredNotificationReceiver: BroadcastReceiver =
    object : BroadcastReceiver() {
      override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ACTION_ALARM_FIRED_NOTIFICATION) {
          performAlarmActionAndStopSelf(
            actionType =
              requireNotNull(intent.getSerializable<AlarmActionType>(EXTRA_ALARM_ACTION_TYPE)) {
                "Missing AlarmActionType."
              },
            settings = getAlarmFireSettings(intent),
          )
        }
      }
    }

  private val alarmDurationTimer = Timer()

  override fun onCreate() {
    super.onCreate()
    ContextCompat.registerReceiver(
      this,
      alarmFiredNotificationReceiver,
      IntentFilter(ACTION_ALARM_FIRED_NOTIFICATION),
      ContextCompat.RECEIVER_EXPORTED,
    )
  }

  override fun onDestroy() {
    super.onDestroy()

    stopPlaying()
    alarmDurationTimer.cancel()

    unregisterReceiver(alarmFiredNotificationReceiver)

    missedAlarms.forEach(::notifyAlarmMissed)
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    super.onStartCommand(intent, flags, startId)
    if (intent == null) return START_NOT_STICKY

    val settings = getAlarmFireSettings(intent)
    cancelNotification(settings.id.toInt())

    if (isPlaying) {
      onAlreadyPlaying(settings)
      return START_NOT_STICKY
    }

    isPlaying = true

    ServiceCompat.startForeground(
      this,
      settings.id.toInt(),
      buildNotification(settings),
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED
      } else {
        0
      },
    )
    startPlaying(settings)
    startAlarmDurationTimer(settings)

    return START_REDELIVER_INTENT
  }

  private fun onAlreadyPlaying(settings: AlarmFireSettings) {
    lifecycleScope.launch { updateAlarmOnDismissUseCase(settings.id, settings.fireOnDateTime) }
    missedAlarms.add(settings)
  }

  @OptIn(ExperimentalResourceApi::class)
  private fun buildNotification(settings: AlarmFireSettings): Notification =
    NotificationCompat.Builder(this, ALARM_FIRED_NOTIFICATION_CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setContentTitle("Alarm was fired")
      .setCategory(NotificationCompat.CATEGORY_ALARM)
      .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
      .setOngoing(true)
      .setFullScreenIntent(
        PendingIntent.getActivity(
          this,
          ALARM_FIRED_ACTIVITY_REQUEST_CODE,
          Intent(this, AlarmFiredActivity::class.java)
            .putExtra(EXTRA_ALARM_FIRE_SETTINGS, settings)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION),
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        ),
        true,
      )
      .setDeleteIntent(getAlarmFiredBroadcastPendingIntent(settings, AlarmActionType.DISMISS))
      .addAction(
        R.drawable.ic_launcher_foreground,
        getStringBlocking(Res.string.dismiss),
        getAlarmFiredBroadcastPendingIntent(settings, AlarmActionType.DISMISS),
      )
      .run {
        if (settings.snoozeAvailable) {
          addAction(
            R.drawable.ic_launcher_foreground,
            getStringBlocking(Res.string.snooze),
            getAlarmFiredBroadcastPendingIntent(settings, AlarmActionType.SNOOZE),
          )
        } else {
          this
        }
      }
      .build()

  private fun getAlarmFiredBroadcastPendingIntent(
    settings: AlarmFireSettings,
    action: AlarmActionType,
  ): PendingIntent =
    PendingIntent.getBroadcast(
      this,
      action.requestCode,
      Intent(ACTION_ALARM_FIRED_NOTIFICATION)
        .putExtra(EXTRA_ALARM_FIRE_SETTINGS, settings)
        .putExtra(EXTRA_ALARM_ACTION_TYPE, action),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

  private fun startPlaying(settings: AlarmFireSettings) {
    if (settings.soundEnabled) {
      mediaPlayer =
        MediaPlayer().apply {
          setOnErrorListener { player, what, extra ->
            Napier.e("Error occurred while playing audio - $what:$extra")
            player.stopAndRelease()
            true
          }
          setDataSource(
            this@AndroidAlarmService,
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), // TODO: custom alarm sounds
          )
          setAudioAttributes(
            AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_ALARM)
              .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
              .build()
          )
          isLooping = true

          prepare()
          start()
        }
    }

    if (settings.vibrationEnabled) {
      vibrator =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSystemService(VibratorManager::class.java).defaultVibrator
          } else {
            getSystemService(Vibrator::class.java)
          }
          .apply { vibrate(VibrationEffect.createWaveform(longArrayOf(0L, 1000L, 1000L), 0)) }
    }
  }

  private fun stopPlaying() {
    mediaPlayer?.stopAndRelease()
    vibrator?.cancel()
  }

  private fun MediaPlayer.stopAndRelease() {
    stop()
    release()
    mediaPlayer = null
  }

  private fun startAlarmDurationTimer(settings: AlarmFireSettings) {
    alarmDurationTimer.schedule(
      object : TimerTask() {
        override fun run() {
          performAlarmActionAndStopSelf(
            actionType =
              if (settings.snoozeAvailable) AlarmActionType.SNOOZE else AlarmActionType.DISMISS,
            settings = settings,
          )
        }
      },
      60_000 * settings.alarmDurationMinutes,
    )
  }

  private fun performAlarmActionAndStopSelf(
    actionType: AlarmActionType,
    settings: AlarmFireSettings,
  ) {
    when (actionType) {
      AlarmActionType.DISMISS -> {
        lifecycleScope.launch { updateAlarmOnDismissUseCase(settings.id, settings.fireOnDateTime) }
      }
      AlarmActionType.SNOOZE -> {
        lifecycleScope.launch { updateAlarmOnSnoozeUseCase(settings.id) }
      }
    }.invokeOnCompletion { stopSelf() }
  }

  private enum class AlarmActionType {
    DISMISS,
    SNOOZE;

    val requestCode: Int
      get() =
        when (this) {
          DISMISS -> 100
          SNOOZE -> 200
        }
  }

  companion object {
    private const val ACTION_ALARM_FIRED_NOTIFICATION = "ACTION_FIRED_ALARM_NOTIFICATION"

    private const val EXTRA_ALARM_ACTION_TYPE = "ALARM_NOTIFICATION_INTERACTION_TYPE"

    private const val ALARM_FIRED_ACTIVITY_REQUEST_CODE = 300
  }
}
