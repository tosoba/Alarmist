package com.trm.alarmist.core.system

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.dismiss
import alarmist.composeapp.generated.resources.snooze
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.trm.alarmist.MainActivity
import com.trm.alarmist.R
import com.trm.alarmist.core.common.util.getSerializable
import com.trm.alarmist.core.common.util.getStringBlocking
import com.trm.alarmist.core.common.util.launch
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnDismissUseCase
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnSnoozeUseCase
import io.github.aakira.napier.Napier
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AndroidAlarmService : Service(), KoinComponent {
  private val updateAlarmOnDismissUseCase: UpdateAlarmOnDismissUseCase by inject()
  private val updateAlarmOnSnoozeUseCase: UpdateAlarmOnSnoozeUseCase by inject()

  private var mediaPlayer: MediaPlayer? = null

  private val firedAlarmNotificationReceiver: BroadcastReceiver =
    object : BroadcastReceiver() {
      override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != ACTION_FIRED_ALARM_NOTIFICATION) return

        stopSelf()

        when (
          intent.getSerializable<NotificationInteraction>(EXTRA_ALARM_NOTIFICATION_INTERACTION)
        ) {
          NotificationInteraction.DISMISS -> {
            launch {
              updateAlarmOnDismissUseCase(getAlarmId(intent), getAlarmFireOnDateTime(intent))
            }
          }
          NotificationInteraction.SNOOZE -> {
            launch { updateAlarmOnSnoozeUseCase(getAlarmId(intent)) }
          }
          null -> {
            throw IllegalArgumentException()
          }
        }
      }
    }

  override fun onCreate() {
    super.onCreate()
    ContextCompat.registerReceiver(
      this,
      firedAlarmNotificationReceiver,
      IntentFilter(ACTION_FIRED_ALARM_NOTIFICATION),
      ContextCompat.RECEIVER_EXPORTED,
    )
  }

  @OptIn(ExperimentalResourceApi::class)
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    if (intent == null) return START_NOT_STICKY

    ServiceCompat.startForeground(
      this,
      getAlarmId(intent).toInt(),
      NotificationCompat.Builder(this, ALARM_FIRED_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Alarm was fired")
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
        .setOngoing(true)
        .setFullScreenIntent(
          PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java) // TODO: replace with AlarmActivity
              .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
          ),
          true,
        )
        .setDeleteIntent(getAlarmBroadcastPendingIntent(intent, NotificationInteraction.DISMISS))
        .addAction(
          R.drawable.ic_launcher_foreground,
          getStringBlocking(Res.string.dismiss),
          getAlarmBroadcastPendingIntent(intent, NotificationInteraction.DISMISS),
        )
        // TODO: make snooze action conditional based on alarm snooze settings
        .addAction(
          R.drawable.ic_launcher_foreground,
          getStringBlocking(Res.string.snooze),
          getAlarmBroadcastPendingIntent(intent, NotificationInteraction.SNOOZE),
        )
        .build(),
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED
      } else {
        0
      },
    )

    startPlaying()

    // TODO: auto snooze timer?

    return START_REDELIVER_INTENT
  }

  private fun getAlarmBroadcastPendingIntent(
    intent: Intent,
    action: NotificationInteraction,
  ): PendingIntent =
    PendingIntent.getBroadcast(
      this,
      action.requestCode,
      Intent(ACTION_FIRED_ALARM_NOTIFICATION)
        .putExtra(EXTRA_ALARM_ID, getAlarmId(intent))
        .putExtra(EXTRA_FIRE_ON_DATE_TIME, getAlarmFireOnDateTime(intent).toString())
        .putExtra(EXTRA_ALARM_NOTIFICATION_INTERACTION, action),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

  private fun startPlaying() {
    stopPlaying()

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

        isLooping = true
        setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        )
        prepare()
        start()
      }
    // TODO: start vibration
  }

  private fun stopPlaying() {
    mediaPlayer?.stopAndRelease()

    // TODO: cancel vibration
  }

  override fun onDestroy() {
    super.onDestroy()
    stopPlaying()
    unregisterReceiver(firedAlarmNotificationReceiver)
  }

  override fun onBind(intent: Intent?): IBinder? = null

  private fun MediaPlayer.stopAndRelease() {
    stop()
    release()
    mediaPlayer = null
  }

  private enum class NotificationInteraction {
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

    private const val EXTRA_ALARM_NOTIFICATION_INTERACTION = "ALARM_NOTIFICATION_INTERACTION"
  }
}
