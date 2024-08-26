package com.trm.alarmist.widget.group

import android.content.Context
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorLong
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.widget.common.ui.WidgetLayoutSize
import com.trm.alarmist.widget.common.util.LocalIsPreview
import com.trm.alarmist.widget.common.util.LocalWidgetLayoutSize
import com.trm.alarmist.widget.common.util.widgetPinPreviewAlarms

internal class GroupWidgetPinPreview : GlanceAppWidget() {
  override val sizeMode: SizeMode = SizeMode.Exact

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      CompositionLocalProvider(
        LocalIsPreview provides true,
        LocalWidgetLayoutSize provides WidgetLayoutSize.Medium,
      ) {
        GroupWidgetScaffold(
          id = id,
          state =
            GroupWidgetState.Initialized(
              alarms = widgetPinPreviewAlarms(1L),
              group =
                AlarmGroupModel(1L, "Daily routine", Color.Red.toArgb().toColorLong(), 5, true),
            ),
          showTitleBar = true,
        )
      }
    }
  }
}
