package com.trm.alarmist.feature.alarm

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.common.util.nextFullHour
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.ui.WheelTimePicker
import kotlinx.datetime.LocalTime

@Composable
fun AlarmContent(modifier: Modifier = Modifier, component: AlarmComponent) {
  Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
    val textStyle = MaterialTheme.typography.headlineMedium
    val textHeightDp = with(LocalDensity.current) { textStyle.fontSize.toDp() } + 10.dp
    WheelTimePicker(
      startTime = LocalTime(now().nextFullHour(), 0),
      rowCount = 5,
      size = DpSize(textHeightDp, textHeightDp) * 5,
      textStyle = textStyle,
      centerTextStyle = textStyle.copy(fontWeight = FontWeight.Bold)
    ) {
      println(it.toString())
    }
  }
}
