package com.trm.alarmist

import alarmist.composeapp.generated.resources.Res
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnDismissUseCase
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnSnoozeUseCase
import com.trm.alarmist.core.system.AndroidAlarmService
import com.trm.alarmist.core.system.getAlarmFireOnDateTime
import com.trm.alarmist.core.system.getAlarmId
import com.trm.alarmist.core.ui.theme.AppTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.koin.android.ext.android.inject

class AlarmFiredActivity : ComponentActivity() {
  private val updateAlarmOnDismissUseCase: UpdateAlarmOnDismissUseCase by inject()
  private val updateAlarmOnSnoozeUseCase: UpdateAlarmOnSnoozeUseCase by inject()

  @OptIn(ExperimentalResourceApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    requestWindowFeature(Window.FEATURE_NO_TITLE)
    turnScreenOnAndKeyguardOff()

    setContent {
      AppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
          Column(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
              text = getAlarmFireOnDateTime(intent).time.toString(),
              style = MaterialTheme.typography.displayLarge,
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(text = "Alarm", style = MaterialTheme.typography.displaySmall)

            Spacer(modifier = Modifier.weight(1f))

            Row(modifier = Modifier.fillMaxWidth()) {
              OutlinedButton(
                onClick = {
                  lifecycleScope
                    .launch { updateAlarmOnSnoozeUseCase(getAlarmId(intent)) }
                    .invokeOnCompletion {
                      stopService(Intent(this@AlarmFiredActivity, AndroidAlarmService::class.java))
                      finish()
                    }
                },
                modifier = Modifier.weight(1f),
              ) {
                Text(
                  text = stringResource(Res.string.snooze),
                  style = MaterialTheme.typography.displaySmall,
                )
              }

              Spacer(modifier = Modifier.width(10.dp))

              Button(
                onClick = {
                  lifecycleScope
                    .launch {
                      updateAlarmOnDismissUseCase(
                        getAlarmId(intent),
                        getAlarmFireOnDateTime(intent),
                      )
                    }
                    .invokeOnCompletion {
                      stopService(Intent(this@AlarmFiredActivity, AndroidAlarmService::class.java))
                      finish()
                    }
                },
                modifier = Modifier.weight(1f),
              ) {
                Text(
                  text = stringResource(Res.string.dismiss),
                  style = MaterialTheme.typography.displaySmall,
                )
              }
            }
          }
        }
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    turnScreenOffAndKeyguardOn()
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
