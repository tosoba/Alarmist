package com.trm.alarmist.feature.alarm

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.ui.WheelTimePicker
import kotlinx.datetime.LocalTime

@Composable
fun AlarmContent(
  modifier: Modifier = Modifier,
  state: AlarmState,
  onFireAtChange: (LocalTime) -> Unit,
  onConfirmClick: () -> Unit,
) {
  Box(modifier = modifier) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
      val textStyle = MaterialTheme.typography.headlineMedium
      val textHeightDp = with(LocalDensity.current) { textStyle.fontSize.toDp() } + 10.dp
      WheelTimePicker(
        startTime = state.fireAt,
        rowCount = 5,
        size = DpSize(textHeightDp, textHeightDp) * 5,
        textStyle = textStyle,
        centerTextStyle = textStyle.copy(fontWeight = FontWeight.Bold),
        onSnappedTime = onFireAtChange,
      )
    }

    FloatingActionButton(modifier = Modifier.align(Alignment.BottomEnd), onClick = onConfirmClick) {
      Icon(imageVector = Icons.Default.Check, contentDescription = "Confirm")
    }
  }
}
