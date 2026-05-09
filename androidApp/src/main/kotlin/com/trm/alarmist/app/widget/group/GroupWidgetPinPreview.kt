package com.trm.alarmist.app.widget.group

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
import com.trm.alarmist.app.widget.common.ui.WidgetLayoutType
import com.trm.alarmist.app.widget.common.ui.WidgetTheme
import com.trm.alarmist.app.widget.common.util.LocalWidgetLayoutType
import com.trm.alarmist.app.widget.common.util.LocalWidgetMode
import com.trm.alarmist.app.widget.common.util.WidgetMode
import com.trm.alarmist.app.widget.common.util.widgetPreviewAlarmList

internal class GroupWidgetPinPreview(private val noLazyLayouts: Boolean) : GlanceAppWidget() {
  override val sizeMode: SizeMode = SizeMode.Exact

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      CompositionLocalProvider(
        LocalWidgetMode provides
          if (noLazyLayouts) WidgetMode.NO_LAZY_LAYOUTS_PREVIEW else WidgetMode.NORMAL_PREVIEW,
        LocalWidgetLayoutType provides WidgetLayoutType.Medium(showTitleBar = true),
      ) {
        WidgetTheme {
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
}
