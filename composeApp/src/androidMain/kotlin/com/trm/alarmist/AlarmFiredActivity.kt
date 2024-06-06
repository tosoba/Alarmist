package com.trm.alarmist

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.alarm
import alarmist.composeapp.generated.resources.dismiss
import alarmist.composeapp.generated.resources.snooze
import android.content.Intent
import android.os.Bundle
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
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.trm.alarmist.core.common.util.amPmString
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.common.util.toFormattedString
import com.trm.alarmist.core.common.util.turnScreenOffAndKeyguardOn
import com.trm.alarmist.core.common.util.turnScreenOnAndKeyguardOff
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnDismissUseCase
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnSnoozeUseCase
import com.trm.alarmist.core.system.AlarmFireSettings
import com.trm.alarmist.core.system.AndroidAlarmService
import com.trm.alarmist.core.system.getAlarmFireSettings
import com.trm.alarmist.core.ui.AutoSizeText
import com.trm.alarmist.core.ui.theme.AppTheme
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.StringResource
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

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun AlarmFiredView(
  settings: AlarmFireSettings,
  modifier: Modifier = Modifier,
  onSnoozeClick: () -> Unit = {},
  onDismissClick: () -> Unit = {},
  stringResource: @Composable (StringResource) -> String = { stringResource(resource = it) },
) {
  val windowSizeClass = calculateWindowSizeClass()
  if (
    windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact ||
      windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
  ) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
      Column(modifier = Modifier.weight(.5f), horizontalAlignment = Alignment.CenterHorizontally) {
        AlarmFireAtTimeText(settings)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = settings.name ?: stringResource(Res.string.alarm),
          style = MaterialTheme.typography.headlineMedium,
        )
      }

      Spacer(modifier = Modifier.width(32.dp))

      Column(modifier = Modifier.weight(.5f), horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedButton(onClick = onSnoozeClick, modifier = Modifier.fillMaxWidth()) {
          AutoSizeText(
            text = stringResource(Res.string.snooze),
            style = MaterialTheme.typography.headlineLarge,
            maxLines = 1,
            maxTextSize = MaterialTheme.typography.headlineLarge.fontSize,
            modifier = Modifier.padding(vertical = 8.dp),
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onDismissClick, modifier = Modifier.fillMaxWidth()) {
          AutoSizeText(
            text = stringResource(Res.string.dismiss),
            style = MaterialTheme.typography.headlineLarge,
            maxLines = 1,
            maxTextSize = MaterialTheme.typography.headlineLarge.fontSize,
            modifier = Modifier.padding(vertical = 8.dp),
          )
        }
      }
    }
  } else {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
      Spacer(modifier = Modifier.weight(1f))

      AlarmFireAtTimeText(settings)

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = settings.name ?: stringResource(Res.string.alarm),
        style = MaterialTheme.typography.headlineMedium,
      )

      Spacer(modifier = Modifier.weight(.5f))

      Row(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(onClick = onSnoozeClick, modifier = Modifier.weight(1f)) {
          AutoSizeText(
            text = stringResource(Res.string.snooze),
            style = MaterialTheme.typography.headlineLarge,
            maxLines = 1,
            maxTextSize = MaterialTheme.typography.headlineLarge.fontSize,
            modifier = Modifier.padding(vertical = 8.dp),
          )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(onClick = onDismissClick, modifier = Modifier.weight(1f)) {
          AutoSizeText(
            text = stringResource(Res.string.dismiss),
            style = MaterialTheme.typography.headlineLarge,
            maxLines = 1,
            maxTextSize = MaterialTheme.typography.headlineLarge.fontSize,
            modifier = Modifier.padding(vertical = 8.dp),
          )
        }
      }

      Spacer(modifier = Modifier.weight(.5f))
    }
  }
}

@Composable
private fun AlarmFireAtTimeText(settings: AlarmFireSettings, modifier: Modifier = Modifier) {
  Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
    AutoSizeText(
      text = settings.fireOnDateTime.time.toFormattedString(),
      style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
      maxLines = 1,
      maxTextSize = 96.sp,
      modifier = Modifier.alignByBaseline().weight(1f),
    )

    settings.fireOnDateTime.time.amPmString().takeIf(String::isNotEmpty)?.let {
      Spacer(modifier = Modifier.width(2.dp))

      Text(
        text = it,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        maxLines = 1,
        modifier = Modifier.alignByBaseline(),
      )
    }
  }
}

// TODO: previews for both horizontal and vertical layouts
@Composable
@Preview(showBackground = true)
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
    stringResource = { it.key.capitalize(Locale.current) },
  )
}
