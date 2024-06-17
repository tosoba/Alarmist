package com.trm.alarmist.feature.widgets

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.widgets
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource

@Composable
fun WidgetsContent(modifier: Modifier = Modifier, component: WidgetsComponent) {
  Box(modifier = modifier) {
    Text(stringResource(Res.string.widgets), modifier = Modifier.align(Alignment.Center))
  }
}
