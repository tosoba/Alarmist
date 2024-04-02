package com.trm.alarmist.feature.timer

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.timer
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun TimerContent(modifier: Modifier = Modifier, component: TimerComponent) {
  Box(modifier = modifier) {
    Text(stringResource(Res.string.timer), modifier = Modifier.align(Alignment.Center))
  }
}
