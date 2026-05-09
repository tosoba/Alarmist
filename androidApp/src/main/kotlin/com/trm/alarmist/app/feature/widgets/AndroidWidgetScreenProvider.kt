package com.trm.alarmist.app.feature.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.trm.alarmist.feature.widgets.WidgetScreenProvider

class AndroidWidgetScreenProvider : WidgetScreenProvider {
    @Composable
    override fun Content(modifier: Modifier) {
        WidgetsContent(modifier = modifier)
    }
}
