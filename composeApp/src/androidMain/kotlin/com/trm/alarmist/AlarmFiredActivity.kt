package com.trm.alarmist

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.alarm
import alarmist.composeapp.generated.resources.dismiss
import alarmist.composeapp.generated.resources.snooze
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.trm.alarmist.core.common.util.formattedTime
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.common.util.turnScreenOffAndKeyguardOn
import com.trm.alarmist.core.common.util.turnScreenOnAndKeyguardOff
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnDismissUseCase
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnSnoozeUseCase
import com.trm.alarmist.core.system.AlarmFireSettings
import com.trm.alarmist.core.system.AndroidAlarmService
import com.trm.alarmist.core.system.getAlarmFireSettings
import com.trm.alarmist.core.ui.theme.AppTheme
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.koin.android.ext.android.inject

class AlarmFiredActivity : ComponentActivity() {
  private val updateAlarmOnDismissUseCase: UpdateAlarmOnDismissUseCase by inject()
  private val updateAlarmOnSnoozeUseCase: UpdateAlarmOnSnoozeUseCase by inject()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    requestWindowFeature(Window.FEATURE_NO_TITLE)
    turnScreenOnAndKeyguardOff()

    setContent {
      AppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
          val settings = getAlarmFireSettings(intent)
          AlarmFiredView(
            settings = settings,
            modifier = Modifier.fillMaxSize().padding(16.dp),
            onSnoozeClick = {
              lifecycleScope
                .launch { updateAlarmOnSnoozeUseCase(settings.id) }
                .invokeOnCompletion {
                  stopService(Intent(this@AlarmFiredActivity, AndroidAlarmService::class.java))
                  finish()
                }
            },
            onDismissClick = {
              lifecycleScope
                .launch { updateAlarmOnDismissUseCase(settings.id, settings.fireOnDateTime) }
                .invokeOnCompletion {
                  stopService(Intent(this@AlarmFiredActivity, AndroidAlarmService::class.java))
                  finish()
                }
            },
          )
        }
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    turnScreenOffAndKeyguardOn()
  }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun AlarmFiredView(
  settings: AlarmFireSettings,
  modifier: Modifier = Modifier,
  onSnoozeClick: () -> Unit = {},
  onDismissClick: () -> Unit = {},
) {
  Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
    Spacer(modifier = Modifier.weight(1f))

    Text(
      text =
        buildAnnotatedString {
          withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 96.sp)) {
            append(
              settings.fireOnDateTime.formattedTime(
                context = LocalContext.current,
                showDayOfWeek = false,
                showAmPmIf12HourFormat = false,
              )
            )
          }
          if (!DateFormat.is24HourFormat(LocalContext.current)) {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 48.sp)) {
              append(' ')
              append(
                settings.fireOnDateTime.time.format(LocalTime.Format { amPmMarker("AM", "PM") })
              )
            }
          }
        }
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = settings.name ?: stringResource(Res.string.alarm),
      style = MaterialTheme.typography.headlineMedium,
    )

    Spacer(modifier = Modifier.weight(.5f))

    Row(modifier = Modifier.fillMaxWidth()) {
      OutlinedButton(onClick = onSnoozeClick, modifier = Modifier.weight(1f)) {
        Text(
          text = stringResource(Res.string.snooze),
          style = MaterialTheme.typography.headlineLarge,
          modifier = Modifier.padding(vertical = 16.dp),
        )
      }

      Spacer(modifier = Modifier.width(16.dp))

      Button(onClick = onDismissClick, modifier = Modifier.weight(1f)) {
        Text(
          text = stringResource(Res.string.dismiss),
          style = MaterialTheme.typography.headlineLarge,
          modifier = Modifier.padding(vertical = 16.dp),
        )
      }
    }

    Spacer(modifier = Modifier.weight(.5f))
  }
}

@Composable
@Preview
private fun AlarmFiredPreview() {
  AlarmFiredView(
    settings =
      AlarmFireSettings(
        id = 1L,
        name = "Get up",
        fireOnDateTime = LocalDateTime.now(),
        snoozeAvailable = true,
        alarmDurationMinutes = 3L,
        soundEnabled = true,
        soundId = null,
        vibrationEnabled = true,
      ),
    modifier = Modifier.fillMaxSize().padding(16.dp),
  )
}
