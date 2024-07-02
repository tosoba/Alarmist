package com.trm.alarmist.widget.group

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

class AlarmGroupWidgetConfigActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setResult(RESULT_CANCELED)

    val appWidgetId =
      intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
    if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) finish()

    setContent {
      //      AppTheme {
      //        Surface(color = MaterialTheme.colorScheme.background) {
      //          val component =
      //            DefaultGroupWidgetConfigComponent(componentContext = defaultComponentContext())
      //          val state by component.feature.state.collectAsState()
      //          GroupWidgetConfigContent(
      //            state = state,
      //            modifier = Modifier.fillMaxSize(),
      //            onExpandGroup = component.feature::onExpandGroup,
      //            onCollapseGroup = component.feature::onCollapseGroup,
      //            onChooseGroup = component.feature::onChooseGroup,
      //          )
      //        }
      //      }

      Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Alarm group config activity")

        Row {
          Button(onClick = { finish() }) { Text(text = "Cancel") }
          Button(
            onClick = {
              // TODO: update widget like in daylighter

              setResult(
                RESULT_OK,
                Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId),
              )
              finish()
            }
          ) {
            Text(text = "Ok")
          }
        }
      }
    }
  }

  companion object {
    fun pendingIntent(context: Context): PendingIntent =
      PendingIntent.getActivity(
        context,
        0,
        Intent(context, AlarmGroupWidgetConfigActivity::class.java),
        // must have FLAG_MUTABLE - otherwise EXTRA_APPWIDGET_ID will not be set
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
      )
  }
}
