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
  private val missedAlarms = mutableListOf<Intent>()

  private val firedAlarmNotificationReceiver: BroadcastReceiver =
    object : BroadcastReceiver() {
      override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ACTION_FIRED_ALARM_NOTIFICATION) {
          performAlarmActionAndStopSelf(
            actionType =
              requireNotNull(intent.getSerializable<AlarmActionType>(EXTRA_ALARM_ACTION_TYPE)) {
                "Missing AlarmActionType."
              },
            intent = intent,
          )
        }
      }
    }

  private val alarmDurationTimer = Timer()

  override fun onCreate() {
    super.onCreate()
    ContextCompat.registerReceiver(
      this,
      firedAlarmNotificationReceiver,
      IntentFilter(ACTION_FIRED_ALARM_NOTIFICATION),
      ContextCompat.RECEIVER_EXPORTED,
    )
  }

  override fun onDestroy() {
    super.onDestroy()

    stopPlaying()
    alarmDurationTimer.cancel()

    unregisterReceiver(firedAlarmNotificationReceiver)

    missedAlarms.forEach {
      notifyAlarmMissed(id = getAlarmId(it), fireOnDateTime = getAlarmFireOnDateTime(it))
    }
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    super.onStartCommand(intent, flags, startId)
    if (intent == null) return START_NOT_STICKY

    if (isPlaying) {
      onAlreadyPlaying(intent)
      return START_NOT_STICKY
    }

    isPlaying = true

    ServiceCompat.startForeground(
      this,
      getAlarmId(intent).toInt(),
      buildNotification(intent),
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED
      } else {
        0
      },
    )
    startPlaying(intent)
    startAlarmDurationTimer(intent)

    return START_REDELIVER_INTENT
  }

  private fun onAlreadyPlaying(intent: Intent) {
    lifecycleScope.launch {
      updateAlarmOnDismissUseCase(
        id = getAlarmId(intent),
        notificationDateTime = getAlarmFireOnDateTime(intent),
      )
    }
    missedAlarms.add(intent)
  }

  @OptIn(ExperimentalResourceApi::class)
  private fun buildNotification(intent: Intent): Notification =
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
            .putExtras(intent)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION),
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        ),
        true,
      )
      .setDeleteIntent(getAlarmBroadcastPendingIntent(intent, AlarmActionType.DISMISS))
      .addAction(
        R.drawable.ic_launcher_foreground,
        getStringBlocking(Res.string.dismiss),
        getAlarmBroadcastPendingIntent(intent, AlarmActionType.DISMISS),
      )
      .run {
        if (isSnoozeAvailable(intent)) {
          addAction(
            R.drawable.ic_launcher_foreground,
            getStringBlocking(Res.string.snooze),
            getAlarmBroadcastPendingIntent(intent, AlarmActionType.SNOOZE),
          )
        } else {
          this
        }
      }
      .build()

  private fun getAlarmBroadcastPendingIntent(
    intent: Intent,
    action: AlarmActionType,
  ): PendingIntent =
    PendingIntent.getBroadcast(
      this,
      action.requestCode,
      Intent(ACTION_FIRED_ALARM_NOTIFICATION)
        .putExtra(EXTRA_ALARM_ID, getAlarmId(intent))
        .putExtra(EXTRA_FIRE_ON_DATE_TIME, getAlarmFireOnDateTime(intent).toString())
        .putExtra(EXTRA_ALARM_ACTION_TYPE, action),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

  private fun startPlaying(intent: Intent) {
    if (isSoundEnabled(intent)) {
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

    if (isVibrationEnabled(intent)) {
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

  private fun startAlarmDurationTimer(intent: Intent) {
    alarmDurationTimer.schedule(
      object : TimerTask() {
        override fun run() {
          performAlarmActionAndStopSelf(
            actionType =
              if (isSnoozeAvailable(intent)) AlarmActionType.SNOOZE else AlarmActionType.DISMISS,
            intent = intent,
          )
        }
      },
      60_000 * getRingDurationMinutes(intent),
    )
  }

  private fun performAlarmActionAndStopSelf(actionType: AlarmActionType, intent: Intent) {
    when (actionType) {
      AlarmActionType.DISMISS -> {
        lifecycleScope.launch {
          updateAlarmOnDismissUseCase(getAlarmId(intent), getAlarmFireOnDateTime(intent))
        }
      }
      AlarmActionType.SNOOZE -> {
        lifecycleScope.launch { updateAlarmOnSnoozeUseCase(getAlarmId(intent)) }
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
    private const val ACTION_FIRED_ALARM_NOTIFICATION = "ACTION_FIRED_ALARM_NOTIFICATION"

    private const val EXTRA_ALARM_ACTION_TYPE = "ALARM_NOTIFICATION_INTERACTION_TYPE"

    private const val ALARM_FIRED_ACTIVITY_REQUEST_CODE = 300
  }
}
