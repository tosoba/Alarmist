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
import com.trm.alarmist.widget.common.ui.WidgetLayoutType
import com.trm.alarmist.widget.common.util.LocalIsPreview
import com.trm.alarmist.widget.common.util.LocalWidgetLayoutType
import com.trm.alarmist.widget.common.util.widgetPreviewAlarmList

internal class GroupWidgetPinPreview : GlanceAppWidget() {
  override val sizeMode: SizeMode = SizeMode.Exact

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      CompositionLocalProvider(
        LocalIsPreview provides true,
        LocalWidgetLayoutType provides WidgetLayoutType.Medium(showTitleBar = true),
      ) {
        GroupWidgetScaffold(
          id = id,
          state =
            GroupWidgetState.Initialized(
              alarms = widgetPreviewAlarmList(1L),
              group =
                AlarmGroupModel(1L, "Daily routine", Color.Red.toArgb().toColorLong(), 5, true),
            ),
          showTitleBar = true,
        )
      }
    }
  }
}
