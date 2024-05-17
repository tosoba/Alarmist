package com.trm.alarmist.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.trm.alarmist.core.common.util.formatCountdown
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.ui.theme.onOffContainer
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

@Composable
fun AlarmFireOnDateTimeCountdown(item: AlarmListModel, modifier: Modifier = Modifier) {
  item.fireOnDateTime?.let {
    Countdown(
      targetEpochMillis = it.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    ) { remainingMillis ->
      AnimatedVisibility(remainingMillis >= 0L, modifier = modifier) {
        Text(
          text = remainingMillis.toDuration(DurationUnit.MILLISECONDS).formatCountdown(),
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onOffContainer(item.isOn),
        )
      }
    }
  }
}

@Composable
private fun Countdown(targetEpochMillis: Long, content: @Composable (remainingTime: Long) -> Unit) {
  var remainingTime by
    remember(targetEpochMillis) {
      mutableStateOf(targetEpochMillis - Clock.System.now().toEpochMilliseconds())
    }

  content(remainingTime)

  LaunchedEffect(remainingTime) {
    val diff = remainingTime - (targetEpochMillis - Clock.System.now().toEpochMilliseconds())
    delay(1_000L - diff)
    remainingTime = targetEpochMillis - Clock.System.now().toEpochMilliseconds()
  }
}
