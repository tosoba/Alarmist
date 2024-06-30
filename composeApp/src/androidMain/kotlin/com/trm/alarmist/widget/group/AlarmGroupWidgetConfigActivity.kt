package com.trm.alarmist.widget.group

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

class AlarmGroupWidgetConfigActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // TODO: when setting widget config refer always to the widget that was most recently pinned
    setContent {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Alarm group config activity")
      }
    }
  }

  companion object {
    fun pendingIntent(context: Context): PendingIntent =
      PendingIntent.getActivity(
        context,
        0,
        Intent(context, AlarmGroupWidgetConfigActivity::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
      )
  }
}
