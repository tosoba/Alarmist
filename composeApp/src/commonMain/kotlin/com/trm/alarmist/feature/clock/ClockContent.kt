package com.trm.alarmist.feature.clock

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.clock
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource

@Composable
fun ClockContent(modifier: Modifier = Modifier, component: ClockComponent) {
  Box(modifier = modifier) {
    Text(stringResource(Res.string.clock), modifier = Modifier.align(Alignment.Center))
  }
}
