package com.trm.alarmist.core.system.stopwatch

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.trm.alarmist.MainActivity
import com.trm.alarmist.core.domain.model.StopwatchState

object ServiceHelper {

  fun clickPendingIntent(context: Context): PendingIntent {
    // TODO: likely should work with a deeplink
    val clickIntent =
      Intent(context, MainActivity::class.java).apply {
        putExtra(Constants.STOPWATCH_STATE, StopwatchState.Started.name)
      }
    return PendingIntent.getActivity(
      context,
      Constants.CLICK_REQUEST_CODE,
      clickIntent,
      PendingIntent.FLAG_IMMUTABLE,
    )
  }

  fun stopPendingIntent(context: Context): PendingIntent {
    val stopIntent =
      Intent(context, StopwatchService::class.java).apply {
        putExtra(Constants.STOPWATCH_STATE, StopwatchState.Stopped.name)
      }
    return PendingIntent.getService(
      context,
      Constants.STOP_REQUEST_CODE,
      stopIntent,
      PendingIntent.FLAG_IMMUTABLE,
    )
  }

  fun resumePendingIntent(context: Context): PendingIntent {
    val resumeIntent =
      Intent(context, StopwatchService::class.java).apply {
        putExtra(Constants.STOPWATCH_STATE, StopwatchState.Started.name)
      }
    return PendingIntent.getService(
      context,
      Constants.RESUME_REQUEST_CODE,
      resumeIntent,
      PendingIntent.FLAG_IMMUTABLE,
    )
  }

  fun cancelPendingIntent(context: Context): PendingIntent {
    val cancelIntent =
      Intent(context, StopwatchService::class.java).apply {
        putExtra(Constants.STOPWATCH_STATE, StopwatchState.Canceled.name)
      }
    return PendingIntent.getService(
      context,
      Constants.CANCEL_REQUEST_CODE,
      cancelIntent,
      PendingIntent.FLAG_IMMUTABLE,
    )
  }

  fun triggerForegroundService(context: Context, action: String) {
    Intent(context, StopwatchService::class.java).apply {
      this.action = action
      context.startService(this)
    }
  }
}
