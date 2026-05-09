package com.trm.alarmist.app.feature.stopwatch

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.trm.alarmist.feature.stopwatch.StopwatchComponent
import com.trm.alarmist.feature.stopwatch.StopwatchScreenProvider

class AndroidStopwatchScreenProvider : StopwatchScreenProvider {
    @Composable
    override fun Content(modifier: Modifier, component: StopwatchComponent) {
        StopwatchContent(modifier, component)
    }
}
