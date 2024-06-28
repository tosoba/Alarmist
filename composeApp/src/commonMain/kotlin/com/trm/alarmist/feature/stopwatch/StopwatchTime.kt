package com.trm.alarmist.feature.stopwatch

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.domain.model.StopwatchState

@Composable
fun StopwatchTime(
  hours: String,
  minutes: String,
  seconds: String,
  state: StopwatchState,
  onStartStopClick: () -> Unit,
  onCancelClick: () -> Unit,
) {
  Column(
    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface).padding(30.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Column(
      modifier = Modifier.weight(weight = 9f),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      AnimatedContent(targetState = hours, label = "AnimatedContent-Hours") {
        Text(
          text = it,
          style =
            TextStyle(
              fontSize = MaterialTheme.typography.headlineLarge.fontSize,
              fontWeight = FontWeight.Bold,
              color = if (hours == "00") Color.White else Color.Blue,
            ),
        )
      }
      AnimatedContent(targetState = minutes, label = "AnimatedContent-Minutes") {
        Text(
          text = it,
          style =
            TextStyle(
              fontSize = MaterialTheme.typography.headlineLarge.fontSize,
              fontWeight = FontWeight.Bold,
              color = if (minutes == "00") Color.White else Color.Blue,
            ),
        )
      }
      AnimatedContent(targetState = seconds, label = "AnimatedContent-Seconds") {
        Text(
          text = it,
          style =
            TextStyle(
              fontSize = MaterialTheme.typography.headlineLarge.fontSize,
              fontWeight = FontWeight.Bold,
              color = if (seconds == "00") Color.White else Color.Blue,
            ),
        )
      }
    }

    Row(modifier = Modifier.weight(weight = 1f)) {
      Button(
        modifier = Modifier.weight(1f).fillMaxHeight(0.8f),
        onClick = onStartStopClick,
        colors =
          ButtonDefaults.buttonColors(
            containerColor = if (state == StopwatchState.Started) Color.Red else Color.Blue,
            contentColor = Color.White,
          ),
      ) {
        Text(
          text =
            when (state) {
              StopwatchState.Started -> "Stop"
              StopwatchState.Stopped -> "Resume"
              else -> "Start"
            }
        )
      }

      Spacer(modifier = Modifier.width(30.dp))

      Button(
        modifier = Modifier.weight(1f).fillMaxHeight(0.8f),
        onClick = onCancelClick,
        enabled = seconds != "00" && state != StopwatchState.Started,
        colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0xFF17162B)),
      ) {
        Text(text = "Cancel")
      }
    }
  }
}
