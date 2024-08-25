package com.trm.alarmist.widget.today

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import com.trm.alarmist.core.common.model.Initialized
import com.trm.alarmist.widget.common.model.WidgetAlarmListModel
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

internal class TodayWidgetPinPreview : GlanceAppWidget() {
  override val sizeMode: SizeMode = SizeMode.Exact

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      TodayWidgetScaffold(
        id = object : GlanceId {},
        state =
          Initialized(
            TodayWidgetState(
              alarms =
                listOf(
                  WidgetAlarmListModel(
                    id = 1L,
                    groupId = null,
                    fireAtTime = LocalTime(7, 30),
                    name = "Morning Alarm",
                    isOn = true,
                    fireOnDateTime = LocalDateTime(2024, 8, 25, 7, 30),
                    isCustomScheduled = false,
                  ),
                  WidgetAlarmListModel(
                    id = 2L,
                    groupId = null,
                    fireAtTime = LocalTime(12, 0),
                    name = "Lunch Reminder",
                    isOn = true,
                    fireOnDateTime = LocalDateTime(2024, 8, 25, 12, 0),
                    isCustomScheduled = true,
                  ),
                  WidgetAlarmListModel(
                    id = 3L,
                    groupId = null,
                    fireAtTime = LocalTime(18, 0),
                    name = "Evening Workout",
                    isOn = false,
                    fireOnDateTime = LocalDateTime(2024, 8, 25, 18, 0),
                    isCustomScheduled = false,
                  ),
                  WidgetAlarmListModel(
                    id = 4L,
                    groupId = null,
                    fireAtTime = LocalTime(22, 0),
                    name = "Bedtime",
                    isOn = true,
                    fireOnDateTime = LocalDateTime(2024, 8, 25, 22, 0),
                    isCustomScheduled = true,
                  ),
                  WidgetAlarmListModel(
                    id = 5L,
                    groupId = null,
                    fireAtTime = LocalTime(14, 30),
                    name = "Afternoon Break",
                    isOn = false,
                    fireOnDateTime = null,
                    isCustomScheduled = false,
                  ),
                ),
              groups = emptyMap(),
            )
          ),
      )
    }
  }
}
