package com.trm.alarmist.core.system

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.dismiss
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
import com.trm.alarmist.core.common.util.launch
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnNotificationUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AndroidAlarmService : Service(), KoinComponent {
  private val updateAlarmOnNotificationUseCase: UpdateAlarmOnNotificationUseCase by inject()

  private var mediaPlayer: MediaPlayer? = null
  private var notificationId: Int = 0

  private val firedAlarmActionReceiver: BroadcastReceiver =
    object : BroadcastReceiver() {
      override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != ACTION_FIRED_ALARM) return

        // TODO: differentiate between snooze/dismiss - currently just dismissing
        stopSelf()
        launch {
          updateAlarmOnNotificationUseCase(getAlarmId(intent), getAlarmFireOnDateTime(intent))
        }
      }
    }

  override fun onCreate() {
    super.onCreate()
    ContextCompat.registerReceiver(
      this,
      firedAlarmActionReceiver,
      IntentFilter(ACTION_FIRED_ALARM),
      ContextCompat.RECEIVER_EXPORTED,
    )
  }

  @OptIn(ExperimentalResourceApi::class)
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    if (intent == null) return START_NOT_STICKY

    val alarmId = getAlarmId(intent)
    ServiceCompat.startForeground(
      this,
      alarmId.toInt(),
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
        .setDeleteIntent(
          PendingIntent.getBroadcast(
            this,
            100,
            Intent(
                ACTION_FIRED_ALARM
              ) // TODO: likely should use snooze action here (instead of dismiss)
              .putExtra(EXTRA_ALARM_ID, alarmId)
              .putExtra(EXTRA_FIRE_ON_DATE_TIME, getAlarmFireOnDateTime(intent).toString()),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
          )
        )
        .addAction(
          R.drawable.ic_launcher_foreground,
          runBlocking { org.jetbrains.compose.resources.getString(Res.string.dismiss) },
          PendingIntent.getBroadcast(
            this,
            100,
            Intent(ACTION_FIRED_ALARM)
              .putExtra(EXTRA_ALARM_ID, alarmId)
              .putExtra(EXTRA_FIRE_ON_DATE_TIME, getAlarmFireOnDateTime(intent).toString()),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
          ),
        )
        // TODO: snooze action
        .build(),
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED
      } else {
        0
      },
    )
    notificationId = alarmId.toInt()

    startPlaying()

    // TODO: auto snooze timer?

    return START_REDELIVER_INTENT
  }

  private fun startPlaying() {
    stopPlaying()

    mediaPlayer =
      MediaPlayer().apply {
        setOnErrorListener { mp, _, _ ->
          Napier.e("Error occurred while playing audio.")
          mp.stop()
          mp.release()
          mediaPlayer = null
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
    mediaPlayer?.apply {
      stop()
      release()
      mediaPlayer = null
    }

    // TODO: cancel vibration
    cancelNotification(notificationId)
  }

  override fun onDestroy() {
    stopPlaying()
    unregisterReceiver(firedAlarmActionReceiver)
    ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    super.onDestroy()
  }

  override fun onBind(intent: Intent?): IBinder? = null

  companion object {
    private const val ACTION_FIRED_ALARM = "ACTION_FIRED_ALARM"
  }
}
