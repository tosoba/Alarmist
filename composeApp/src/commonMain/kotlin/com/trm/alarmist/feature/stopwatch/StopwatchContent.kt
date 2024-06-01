package com.trm.alarmist.feature.stopwatch

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.stopwatch
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource

@Composable
fun StopwatchContent(modifier: Modifier = Modifier, component: StopwatchComponent) {
  Box(modifier = modifier) {
    Text(stringResource(Res.string.stopwatch), modifier = Modifier.align(Alignment.Center))
  }
}
