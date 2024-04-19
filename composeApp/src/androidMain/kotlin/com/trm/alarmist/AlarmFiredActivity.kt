package com.trm.alarmist

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.dismiss
import alarmist.composeapp.generated.resources.snooze
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
    setContent {
      AppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
          Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
              text = getAlarmFireOnDateTime(intent).toString(),
              style = MaterialTheme.typography.displayLarge,
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(text = "Alarm", style = MaterialTheme.typography.displaySmall)

            Spacer(modifier = Modifier.weight(1f))

            Row(modifier = Modifier.fillMaxSize()) {
              OutlinedButton(
                onClick = {
                  lifecycleScope
                    .launch { updateAlarmOnSnoozeUseCase(getAlarmId(intent)) }
                    .invokeOnCompletion { finish() }
                },
                modifier = Modifier.weight(1f),
              ) {
                Text(
                  text = stringResource(Res.string.snooze),
                  style = MaterialTheme.typography.displayMedium,
                )
              }
              Button(
                onClick = {
                  lifecycleScope
                    .launch {
                      updateAlarmOnDismissUseCase(
                        getAlarmId(intent),
                        getAlarmFireOnDateTime(intent),
                      )
                    }
                    .invokeOnCompletion { finish() }
                },
                modifier = Modifier.weight(1f),
              ) {
                Text(
                  text = stringResource(Res.string.dismiss),
                  style = MaterialTheme.typography.displayMedium,
                )
              }
            }
          }
        }
      }
    }
  }
}
