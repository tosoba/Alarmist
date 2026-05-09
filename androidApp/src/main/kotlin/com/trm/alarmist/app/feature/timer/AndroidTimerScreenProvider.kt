package com.trm.alarmist.app.feature.timer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.trm.alarmist.feature.timer.TimerComponent
import com.trm.alarmist.feature.timer.TimerScreenProvider

class AndroidTimerScreenProvider : TimerScreenProvider {
    @Composable
    override fun Content(modifier: Modifier, component: TimerComponent) {
        TimerContent(modifier = modifier, component = component)
    }
}
