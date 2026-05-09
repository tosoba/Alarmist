package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.trm.alarmist.widget.common.util.LocalWidgetLayoutType
import com.trm.alarmist.widget.common.util.LocalWidgetMode
import com.trm.alarmist.widget.common.util.WidgetMode

@Composable
internal fun WidgetPreviewCompositionLocalProvider(content: @Composable () -> Unit) {
  CompositionLocalProvider(
    LocalWidgetMode provides WidgetMode.NORMAL_PREVIEW,
    LocalWidgetLayoutType provides WidgetLayoutType.fromWidgetSize(),
    content = content,
  )
}
