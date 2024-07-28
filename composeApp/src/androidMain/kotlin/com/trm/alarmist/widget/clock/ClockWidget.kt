package com.trm.alarmist.widget.clock

import android.content.Context
import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.unit.ColorProvider
import com.trm.alarmist.R
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.usecase.GetNextTodayAlarmUseCase
import com.trm.alarmist.feature.root.RootStartMode
import com.trm.alarmist.widget.common.ui.WidgetAlarmFireAtTimeText
import com.trm.alarmist.widget.common.ui.WidgetTextClock
import com.trm.alarmist.widget.common.ui.WidgetTextStyles
import com.trm.alarmist.widget.common.util.LocalIsPreviewProvider
import com.trm.alarmist.widget.common.util.actionStartMainActivity
import com.trm.alarmist.widget.common.util.spToDp
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

      CompositionLocalProvider(LocalIsPreviewProvider provides false) {
        ClockWidgetContent(alarm = alarm)
      }
    }
  }
}

@Composable
private fun ClockWidgetContent(alarm: AlarmListModel?) {
  GlanceTheme {
    Column(
      modifier =
        GlanceModifier.padding(8.dp).clickable(actionStartMainActivity(RootStartMode.Normal))
    ) {
      val context = LocalContext.current
      val contentColorProvider = GlanceTheme.colors.widgetBackground

      // TODO: variable text sizes depending on widget size

      Box {
        WidgetTextClock(
          format12Hour = context.getString(R.string.time_format_12_h_full),
          format24Hour = context.getString(R.string.time_format_24_h_full),
          showShadow = true,
        ) {
          setInt(
            R.id.widget_text_clock,
            "setTextColor",
            contentColorProvider.getColor(context).toArgb(),
          )
        }
      }

      Box {
        WidgetTextClock(
          format12Hour = context.getString(R.string.time_format_am_pm_date_short),
          format24Hour = context.getString(R.string.time_format_am_pm_date_short),
          showShadow = true,
        ) {
          setInt(
            R.id.widget_text_clock,
            "setTextColor",
            contentColorProvider.getColor(context).toArgb(),
          )
          setFloat(
            R.id.widget_text_clock,
            "setTextSize",
            context.resources.getInteger(R.integer.widget_text_clock_am_pm_font_size).toFloat(),
          )
        }
      }

      if (alarm != null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(contentAlignment = Alignment.Center) {
            Image(
              provider = ImageProvider(R.drawable.alarm),
              contentDescription = null,
              modifier = GlanceModifier.size(16f.spToDp()).padding(top = 1.dp, start = 1.dp),
              colorFilter = ColorFilter.tint(ColorProvider(Color.Black)),
            )

            Image(
              provider = ImageProvider(R.drawable.alarm),
              contentDescription = null,
              modifier = GlanceModifier.size(16f.spToDp()),
              colorFilter = ColorFilter.tint(contentColorProvider),
            )
          }

          Spacer(modifier = GlanceModifier.width(4.dp))

          WidgetAlarmFireAtTimeText(
            fireAtTime = alarm.nextFireAtTime,
            is24HourFormat = DateFormat.is24HourFormat(context),
            useFullFormat = true,
            useShadow = true,
            style = WidgetTextStyles.titleText.copy(color = contentColorProvider),
          )
        }
      }
    }
  }
}
