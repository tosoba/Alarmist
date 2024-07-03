package com.trm.alarmist.widget.group

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.backhandler.BackHandler
import com.arkivanov.essenty.lifecycle.asEssentyLifecycle
import com.trm.alarmist.core.ui.theme.AppTheme
import com.trm.alarmist.feature.widget.config.group.DefaultGroupWidgetConfigComponent
import com.trm.alarmist.feature.widget.config.group.GroupWidgetConfigContent

class AlarmGroupWidgetConfigActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setResult(RESULT_CANCELED)

    val appWidgetId =
      intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
    if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) finish()

    val component =
      DefaultGroupWidgetConfigComponent(
        componentContext =
          DefaultComponentContext(
            lifecycle = lifecycle.asEssentyLifecycle(),
            backHandler =
              BackHandler(onBackPressedDispatcher = onBackPressedDispatcher, lifecycleOwner = this),
          )
      )

    setContent {
      AppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
          val state by component.feature.state.collectAsState()

          Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
              GroupWidgetConfigContent(
                state = state,
                modifier = Modifier.fillMaxSize(),
                onExpandGroup = component.feature::onExpandGroup,
                onCollapseGroup = component.feature::onCollapseGroup,
                onChooseGroup = component.feature::onChooseGroup,
              )

              // TODO: new group fab
            }

            Row(modifier = Modifier.fillMaxWidth()) {
              // TODO: different button weights for different screen sizes

              OutlinedButton(onClick = { finish() }, modifier = Modifier.weight(1f)) {
                Text(text = "Cancel")
              }

              Spacer(modifier = Modifier.width(16.dp))

              Button(
                onClick = {
                  // TODO: update widget like in daylighter

                  setResult(
                    RESULT_OK,
                    Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId),
                  )
                  finish()
                },
                modifier = Modifier.weight(1f),
              ) {
                Text(text = "Ok")
              }
            }
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
