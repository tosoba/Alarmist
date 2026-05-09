package com.trm.alarmist.widget.clock

import android.content.Context
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.usecase.GetNextTodayAlarmUseCase
import com.trm.alarmist.widget.common.ui.WidgetLayoutType
import com.trm.alarmist.widget.common.ui.WidgetTheme
import com.trm.alarmist.widget.common.util.LocalWidgetLayoutType
import com.trm.alarmist.widget.common.util.LocalWidgetMode
import com.trm.alarmist.widget.common.util.WidgetMode
import kotlinx.datetime.LocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ClockWidget : GlanceAppWidget(), KoinComponent {
  private val getNextTodayAlarmUseCase: GetNextTodayAlarmUseCase by inject()

  override val sizeMode: SizeMode = SizeMode.Exact

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      val state = currentState<Preferences>()
      val alarm by
        produceState<AlarmListModel?>(null, state) {
          value = getNextTodayAlarmUseCase(LocalDateTime.now())
        }

      WidgetTheme {
        CompositionLocalProvider(
          LocalWidgetMode provides WidgetMode.NON_PREVIEW,
          LocalWidgetLayoutType provides WidgetLayoutType.Companion.fromWidgetSize(),
        ) {
          ClockWidgetContent(alarm = alarm)
        }
      }
    }
  }
}
