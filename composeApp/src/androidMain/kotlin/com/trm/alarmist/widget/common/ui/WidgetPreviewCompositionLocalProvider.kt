package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.trm.alarmist.widget.common.util.LocalIsPreview
import com.trm.alarmist.widget.common.util.LocalWidgetLayoutType

@Composable
internal fun WidgetPreviewCompositionLocalProvider(content: @Composable () -> Unit) {
  CompositionLocalProvider(
    LocalIsPreview provides true,
    LocalWidgetLayoutType provides WidgetLayoutType.fromWidgetSize(),
    content = content,
  )
}
