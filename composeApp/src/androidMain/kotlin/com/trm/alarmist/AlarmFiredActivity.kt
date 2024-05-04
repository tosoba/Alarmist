package com.trm.alarmist

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.alarm
import alarmist.composeapp.generated.resources.dismiss
import alarmist.composeapp.generated.resources.snooze
import android.app.Activity
import android.app.KeyguardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.trm.alarmist.core.common.util.formattedTime
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnDismissUseCase
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnSnoozeUseCase
import com.trm.alarmist.core.system.AlarmFireSettings
import com.trm.alarmist.core.system.AndroidAlarmService
import com.trm.alarmist.core.system.getAlarmFireSettings
import com.trm.alarmist.core.ui.theme.AppTheme
import kotlinx.coroutines.launch
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
            modifier = Modifier.fillMaxSize().padding(10.dp),
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
      text = settings.fireOnDateTime.formattedTime(LocalContext.current),
      style = MaterialTheme.typography.displayLarge,
    )

    Spacer(modifier = Modifier.height(15.dp))

    Text(
      text = settings.name ?: stringResource(Res.string.alarm),
      style = MaterialTheme.typography.displaySmall,
    )

    Spacer(modifier = Modifier.weight(1f))

    Row(modifier = Modifier.fillMaxWidth()) {
      OutlinedButton(onClick = onSnoozeClick, modifier = Modifier.weight(1f)) {
        Text(
          text = stringResource(Res.string.snooze),
          style = MaterialTheme.typography.displaySmall,
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      Button(onClick = onDismissClick, modifier = Modifier.weight(1f)) {
        Text(
          text = stringResource(Res.string.dismiss),
          style = MaterialTheme.typography.displaySmall,
        )
      }
    }
  }
}

fun Activity.turnScreenOnAndKeyguardOff() {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
    setShowWhenLocked(true)
    setTurnScreenOn(true)
  } else {
    window.addFlags(
      WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
        WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
    )
  }

  getSystemService(KeyguardManager::class.java)
    .requestDismissKeyguard(this@turnScreenOnAndKeyguardOff, null)
}

fun Activity.turnScreenOffAndKeyguardOn() {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
    setShowWhenLocked(false)
    setTurnScreenOn(false)
  } else {
    window.clearFlags(
      WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
        WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
    )
  }
}
