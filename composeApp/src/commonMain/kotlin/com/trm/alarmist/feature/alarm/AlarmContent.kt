package com.trm.alarmist.feature.alarm

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.trm.alarmist.core.common.util.nextFullHour
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.ui.WheelTimePicker
import kotlinx.datetime.LocalTime

@Composable
fun AlarmContent(modifier: Modifier = Modifier, component: AlarmComponent) {
  Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
    WheelTimePicker(startTime = LocalTime(now().nextFullHour(), 0)) { println(it.toString()) }
  }
}
