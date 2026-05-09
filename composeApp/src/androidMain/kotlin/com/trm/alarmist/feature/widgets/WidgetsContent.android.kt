package com.trm.alarmist.feature.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject

@Composable
actual fun WidgetsContent(modifier: Modifier, component: WidgetsComponent) {
    val provider = koinInject<WidgetScreenProvider>()
    provider.Content(modifier)
}
