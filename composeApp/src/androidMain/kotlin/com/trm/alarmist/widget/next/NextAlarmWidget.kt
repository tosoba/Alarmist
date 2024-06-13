package com.trm.alarmist.widget.next

import android.content.Context
import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.padding
import com.trm.alarmist.R
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.usecase.GetNextAlarmUseCase
import com.trm.alarmist.widget.common.ui.WidgetAlarmFireAtTimeText
import com.trm.alarmist.widget.common.ui.WidgetTextClock
import com.trm.alarmist.widget.common.ui.WidgetTextStyles
import com.trm.alarmist.widget.common.util.LocalIsPreviewProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NextAlarmWidget : GlanceAppWidget(), KoinComponent {
  private val getNextAlarmUseCase: GetNextAlarmUseCase by inject()

  override val sizeMode: SizeMode = SizeMode.Exact

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      val state = currentState<Preferences>()
      val alarm by produceState<AlarmListModel?>(null, state) { value = getNextAlarmUseCase() }

      CompositionLocalProvider(LocalIsPreviewProvider provides false) {
        NextAlarmWidgetContent(alarm = alarm)
      }
    }
  }
}

@Composable
private fun NextAlarmWidgetContent(alarm: AlarmListModel?) {
  GlanceTheme {
    Column(modifier = GlanceModifier.padding(8.dp)) { // TODO: on click go to app
      val context = LocalContext.current
      val textColor = GlanceTheme.colors.widgetBackground

      Box(contentAlignment = Alignment.CenterStart, modifier = GlanceModifier.defaultWeight()) {
        WidgetTextClock(useFullTimeFormat = true) {
          setInt(R.id.widget_text_clock, "setTextColor", textColor.getColor(context).toArgb())
        }
      }

      if (alarm != null) {
        // TODO: current date (maybe do it via TextClock that just displays a date to avoid needing
        // to update it)
        // TODO: alarm icon next to alarm fireAtTime
        val is24HourFormat = DateFormat.is24HourFormat(context)
        WidgetAlarmFireAtTimeText(
          fireAtTime = alarm.nextFireAtTime,
          is24HourFormat = is24HourFormat,
          useFullFormat = true,
          style = WidgetTextStyles.titleText.copy(color = textColor),
        )
      }
    }
  }
}
