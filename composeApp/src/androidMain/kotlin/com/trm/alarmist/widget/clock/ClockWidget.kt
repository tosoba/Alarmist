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
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.material3.ColorProviders
import androidx.glance.text.FontWeight
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.trm.alarmist.R
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.usecase.GetNextTodayAlarmUseCase
import com.trm.alarmist.core.ui.theme.darkScheme
import com.trm.alarmist.core.ui.theme.lightScheme
import com.trm.alarmist.feature.root.RootStartMode
import com.trm.alarmist.widget.common.ui.WidgetAlarmFireAtTimeText
import com.trm.alarmist.widget.common.ui.WidgetLayoutSize
import com.trm.alarmist.widget.common.ui.WidgetTextClock
import com.trm.alarmist.widget.common.ui.WidgetTextClockShadowMode
import com.trm.alarmist.widget.common.util.LocalIsPreview
import com.trm.alarmist.widget.common.util.LocalWidgetLayoutSize
import com.trm.alarmist.widget.common.util.actionStartMainActivity
import com.trm.alarmist.widget.common.util.emptyActionIfPreviewOrElse
import com.trm.alarmist.widget.common.util.spToDp
import com.trm.alarmist.widget.common.util.stringResource
import kotlin.math.pow
import kotlin.math.sqrt
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
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

      CompositionLocalProvider(
        LocalIsPreview provides false,
        LocalWidgetLayoutSize provides WidgetLayoutSize.fromLocalSize(),
      ) {
        ClockWidgetContent(alarm = alarm)
      }
    }
  }
}

internal class ClockWidgetPinPreview : GlanceAppWidget() {
  override val sizeMode: SizeMode = SizeMode.Exact

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      CompositionLocalProvider(
        LocalIsPreview provides true,
        LocalWidgetLayoutSize provides WidgetLayoutSize.Medium,
      ) {
        ClockWidgetContent(
          alarm =
            AlarmListModel(
              id = 1L,
              groupId = null,
              fireAtTime = LocalTime(7, 30),
              name = "Wake Up Alarm",
              isOn = true,
              fireOnDateTime = LocalDateTime(2024, 5, 10, 7, 30),
              scheduledOnDaysOfWeek = emptySet(),
              closestScheduledOnDate = null,
              offOnAllScheduledDates = false,
              scheduledOnMultipleDates = false,
              snoozedFireAtTime = null,
            ),
          textColorProvider = ColorProvider(Color.Black),
          backgroundColor = lightScheme.secondaryContainer,
        )
      }
    }
  }
}

@Composable
private fun ClockWidgetContent(
  alarm: AlarmListModel?,
  textColorProvider: ColorProvider = GlanceTheme.colors.widgetBackground,
  backgroundColor: Color = Color.Transparent,
) {
  val textColor = textColorProvider.getColor(LocalContext.current)
  val shadowMode =
    if (colorDistance(textColor, Color.Black) > colorDistance(textColor, Color.White)) {
      WidgetTextClockShadowMode.Dark
    } else {
      WidgetTextClockShadowMode.Light
    }

  GlanceTheme(colors = ColorProviders(light = lightScheme, dark = darkScheme)) {
    Column(
      modifier =
        GlanceModifier.background(backgroundColor)
          .cornerRadius(28.dp)
          .padding(8.dp)
          .clickable(emptyActionIfPreviewOrElse { actionStartMainActivity(RootStartMode.Normal) })
    ) {
      Box {
        WidgetTextClock(
          format12Hour = stringResource(R.string.time_format_12_h_full),
          format24Hour = stringResource(R.string.time_format_24_h_full),
          shadowMode = shadowMode,
        ) {
          setInt(
            R.id.widget_text_clock,
            "setTextColor",
            textColorProvider.getColor(LocalContext.current).toArgb(),
          )
          setTextViewTextSize(
            R.id.widget_text_clock,
            TypedValue.COMPLEX_UNIT_SP,
            when (LocalWidgetLayoutSize.current) {
              WidgetLayoutSize.Small -> 20f
              WidgetLayoutSize.Medium -> 24f
              WidgetLayoutSize.Large -> 28f
            },
          )
        }
      }

      Box {
        WidgetTextClock(
          format12Hour = stringResource(R.string.time_format_am_pm_date_short),
          format24Hour = stringResource(R.string.time_format_am_pm_date_short),
          shadowMode = shadowMode,
        ) {
          setInt(
            R.id.widget_text_clock,
            "setTextColor",
            textColorProvider.getColor(LocalContext.current).toArgb(),
          )
          setTextViewTextSize(
            R.id.widget_text_clock,
            TypedValue.COMPLEX_UNIT_SP,
            when (LocalWidgetLayoutSize.current) {
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
              colorFilter = ColorFilter.tint(textColorProvider),
            )
          }

          Spacer(modifier = GlanceModifier.width(4.dp))

          WidgetAlarmFireAtTimeText(
            fireAtTime = alarm.nextFireAtTime,
            is24HourFormat = DateFormat.is24HourFormat(LocalContext.current),
            useFullFormat = true,
            useShadow = true,
            style =
              TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize =
                  when (LocalWidgetLayoutSize.current) {
                    WidgetLayoutSize.Small -> 14
                    WidgetLayoutSize.Medium -> 18
                    WidgetLayoutSize.Large -> 22
                  }.sp,
                color = textColorProvider,
              ),
          )
        }
      }
    }
  }
}

private fun colorDistance(c1: Color, c2: Color): Float =
  sqrt((c1.red - c2.red).pow(2) + (c1.green - c2.green).pow(2) + (c1.blue - c2.blue).pow(2))
