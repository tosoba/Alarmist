package com.trm.alarmist.widget.clock

import android.content.Context
import android.text.format.DateFormat
import android.util.TypedValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import androidx.glance.text.FontWeight
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.trm.alarmist.R
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.usecase.GetNextTodayAlarmUseCase
import com.trm.alarmist.feature.root.RootStartMode
import com.trm.alarmist.widget.common.ui.WidgetAlarmFireAtTimeText
import com.trm.alarmist.widget.common.ui.WidgetLayoutSize
import com.trm.alarmist.widget.common.ui.WidgetTextClock
import com.trm.alarmist.widget.common.util.LocalIsPreviewProvider
import com.trm.alarmist.widget.common.util.actionStartMainActivity
import com.trm.alarmist.widget.common.util.spToDp
import com.trm.alarmist.widget.common.util.stringResource
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
      val layoutSize = WidgetLayoutSize.fromLocalSize()

      Box {
        WidgetTextClock(
          format12Hour = stringResource(R.string.time_format_12_h_full),
          format24Hour = stringResource(R.string.time_format_24_h_full),
          showShadow = true,
        ) {
          setInt(
            R.id.widget_text_clock,
            "setTextColor",
            contentColorProvider.getColor(context).toArgb(),
          )
          setTextViewTextSize(
            R.id.widget_text_clock,
            TypedValue.COMPLEX_UNIT_SP,
            when (layoutSize) {
              WidgetLayoutSize.Small -> 16f
              WidgetLayoutSize.Medium -> 20f
              WidgetLayoutSize.Large -> 24f
            },
          )
        }
      }

      Box {
        WidgetTextClock(
          format12Hour = stringResource(R.string.time_format_am_pm_date_short),
          format24Hour = stringResource(R.string.time_format_am_pm_date_short),
          showShadow = true,
        ) {
          setInt(
            R.id.widget_text_clock,
            "setTextColor",
            contentColorProvider.getColor(context).toArgb(),
          )
          setTextViewTextSize(
            R.id.widget_text_clock,
            TypedValue.COMPLEX_UNIT_SP,
            when (layoutSize) {
              WidgetLayoutSize.Small -> 12f
              WidgetLayoutSize.Medium -> 16f
              WidgetLayoutSize.Large -> 20f
            },
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
            style =
              TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize =
                  when (layoutSize) {
                    WidgetLayoutSize.Small -> 12
                    WidgetLayoutSize.Medium -> 16
                    WidgetLayoutSize.Large -> 20
                  }.sp,
                color = contentColorProvider,
              ),
          )
        }
      }
    }
  }
}
