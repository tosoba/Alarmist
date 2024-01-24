package com.trm.alarmist.feature.alarm

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.trm.alarmist.core.common.util.nextFullHour
import com.trm.alarmist.core.common.util.now

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmContent(modifier: Modifier = Modifier, component: AlarmComponent) {
  Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
    TimePicker(state = rememberTimePickerState(now().nextFullHour(), 0))
  }
}
