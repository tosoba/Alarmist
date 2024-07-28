package com.trm.alarmist.widget.clock

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

class ClockWidget : GlanceAppWidget(), KoinComponent {
  private val getNextAlarmUseCase: GetNextAlarmUseCase by inject()

  override val sizeMode: SizeMode = SizeMode.Exact

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      val state = currentState<Preferences>()
      val alarm by produceState<AlarmListModel?>(null, state) { value = getNextAlarmUseCase() }

      CompositionLocalProvider(LocalIsPreviewProvider provides false) {
        ClockWidgetContent(alarm = alarm)
      }
    }
  }
}

@Composable
private fun ClockWidgetContent(alarm: AlarmListModel?) {
  GlanceTheme {
    Column(modifier = GlanceModifier.padding(8.dp)) { // TODO: on click go to app
      val context = LocalContext.current
      val textColor = GlanceTheme.colors.widgetBackground

      // TODO: variable text sizes depending on widget size

      Box {
        WidgetTextClock(
          format12Hour = context.getString(R.string.time_format_12_h_full),
          format24Hour = context.getString(R.string.time_format_24_h_full),
          showShadow = true,
        ) {
          setInt(R.id.widget_text_clock, "setTextColor", textColor.getColor(context).toArgb())
        }
      }

      Box {
        WidgetTextClock(
          format12Hour = context.getString(R.string.time_format_am_pm_date_short),
          format24Hour = context.getString(R.string.time_format_am_pm_date_short),
          showShadow = true,
        ) {
          setInt(R.id.widget_text_clock, "setTextColor", textColor.getColor(context).toArgb())
          setFloat(
            R.id.widget_text_clock,
            "setTextSize",
            context.resources.getInteger(R.integer.widget_text_clock_am_pm_font_size).toFloat(),
          )
        }
      }

      if (alarm != null) {
        // TODO: alarm icon next to alarm fireAtTime
        WidgetAlarmFireAtTimeText(
          fireAtTime = alarm.nextFireAtTime,
          is24HourFormat = DateFormat.is24HourFormat(context),
          useFullFormat = true,
          style = WidgetTextStyles.titleText.copy(color = textColor),
        )
      }
    }
  }
}
