package com.trm.alarmist.widget.today

import android.content.Context
import androidx.compose.runtime.CompositionLocalProvider
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import com.trm.alarmist.core.common.model.Initialized
import com.trm.alarmist.widget.common.ui.WidgetLayoutType
import com.trm.alarmist.widget.common.ui.WidgetTheme
import com.trm.alarmist.widget.common.util.LocalWidgetLayoutType
import com.trm.alarmist.widget.common.util.LocalWidgetMode
import com.trm.alarmist.widget.common.util.WidgetMode
import com.trm.alarmist.widget.common.util.widgetPreviewAlarmList

internal class TodayWidgetPinPreview(private val noLazyLayouts: Boolean) : GlanceAppWidget() {
  override val sizeMode: SizeMode = SizeMode.Exact

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      CompositionLocalProvider(
        LocalWidgetMode provides
          if (noLazyLayouts) WidgetMode.NO_LAZY_LAYOUTS_PREVIEW else WidgetMode.NORMAL_PREVIEW,
        LocalWidgetLayoutType provides WidgetLayoutType.Medium(showTitleBar = true),
      ) {
        WidgetTheme {
          TodayWidgetScaffold(
            id = id,
            state =
              Initialized(TodayWidgetState(alarms = widgetPreviewAlarmList(), groups = emptyMap())),
            showTitleBar = true,
          )
        }
      }
    }
  }
}
