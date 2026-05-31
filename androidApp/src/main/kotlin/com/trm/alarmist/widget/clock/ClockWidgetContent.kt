package com.trm.alarmist.widget.clock

import android.annotation.SuppressLint
import android.text.format.DateFormat
import android.util.TypedValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.material3.ColorProviders
import androidx.glance.text.FontWeight
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.trm.alarmist.app.R
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.ui.theme.darkScheme
import com.trm.alarmist.core.ui.theme.lightScheme
import com.trm.alarmist.feature.root.RootStartMode
import com.trm.alarmist.widget.common.ui.ClockWidgetPreview
import com.trm.alarmist.widget.common.ui.WidgetAlarmFireAtTimeText
import com.trm.alarmist.widget.common.ui.WidgetLayoutType
import com.trm.alarmist.widget.common.ui.WidgetPreviewCompositionLocalProvider
import com.trm.alarmist.widget.common.ui.WidgetTextClock
import com.trm.alarmist.widget.common.ui.WidgetTextShadowMode
import com.trm.alarmist.widget.common.util.LocalWidgetLayoutType
import com.trm.alarmist.widget.common.util.actionStartMainActivity
import com.trm.alarmist.widget.common.util.clockWidgetPreviewAlarm
import com.trm.alarmist.widget.common.util.emptyActionIfPreviewOrElse
import com.trm.alarmist.widget.common.util.spToDp
import com.trm.alarmist.widget.common.util.stringResource

@SuppressLint("RestrictedApi")
@Composable
internal fun ClockWidgetContent(
  alarm: AlarmListModel?,
  textColor: Color = Color.White,
  backgroundColor: Color = Color.Transparent,
) {
  GlanceTheme(colors = ColorProviders(light = lightScheme, dark = darkScheme)) {
    Column(
      modifier =
        GlanceModifier.fillMaxSize()
          .background(backgroundColor)
          .cornerRadius(28.dp)
          .padding(8.dp)
          .clickable(emptyActionIfPreviewOrElse { actionStartMainActivity(RootStartMode.Normal) }),
      verticalAlignment = Alignment.Vertical.CenterVertically,
      horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
    ) {
      Box {
        WidgetTextClock(
          format12Hour = stringResource(R.string.time_format_12_h_full),
          format24Hour = stringResource(R.string.time_format_24_h_full),
          shadowMode = WidgetTextShadowMode.Dark,
        ) {
          setInt(
            R.id.widget_text_clock,
            "setTextColor",
            textColor.toArgb(),
          )
          setTextViewTextSize(
            R.id.widget_text_clock,
            TypedValue.COMPLEX_UNIT_SP,
            when (LocalWidgetLayoutType.current) {
              is WidgetLayoutType.Small -> 20f
              is WidgetLayoutType.Medium -> 24f
              is WidgetLayoutType.Large -> 28f
            },
          )
        }
      }

      Box {
        WidgetTextClock(
          format12Hour = stringResource(R.string.time_format_am_pm_date_short),
          format24Hour = stringResource(R.string.time_format_am_pm_date_short),
          shadowMode = WidgetTextShadowMode.Dark,
        ) {
          setInt(
            R.id.widget_text_clock,
            "setTextColor",
            textColor.toArgb(),
          )
          setTextViewTextSize(
            R.id.widget_text_clock,
            TypedValue.COMPLEX_UNIT_SP,
            when (LocalWidgetLayoutType.current) {
              is WidgetLayoutType.Small -> 12f
              is WidgetLayoutType.Medium -> 16f
              is WidgetLayoutType.Large -> 20f
            },
          )
        }
      }

      if (alarm != null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(contentAlignment = Alignment.Center) {
            AlarmIcon(
              modifier = GlanceModifier.size(16f.spToDp()).padding(top = 0.5.dp, start = 0.5.dp),
              colorFilter = ColorFilter.tint(ColorProvider(Color.Black)),
            )

            AlarmIcon(
              colorFilter = ColorFilter.tint(ColorProvider(textColor)),
              modifier = GlanceModifier.size(16f.spToDp()),
            )
          }

          Spacer(modifier = GlanceModifier.width(4.dp))

          WidgetAlarmFireAtTimeText(
            fireAtTime = alarm.fireAtTime,
            is24HourFormat = DateFormat.is24HourFormat(LocalContext.current),
            useFullFormat = true,
            shadowMode = WidgetTextShadowMode.Dark,
            style =
              TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize =
                  when (LocalWidgetLayoutType.current) {
                    is WidgetLayoutType.Small -> 14
                    is WidgetLayoutType.Medium -> 18
                    is WidgetLayoutType.Large -> 22
                  }.sp,
                color = ColorProvider(textColor),
              ),
          )
        }
      }
    }
  }
}

@SuppressLint("RestrictedApi")
@Suppress("unused")
@ClockWidgetPreview
@Composable
private fun ClockWidgetContentPreview() {
  WidgetPreviewCompositionLocalProvider {
    ClockWidgetContent(
      alarm = clockWidgetPreviewAlarm(),
      backgroundColor = GlanceTheme.colors.primary.getColor(LocalContext.current),
    )
  }
}

@Composable
private fun AlarmIcon(colorFilter: ColorFilter, modifier: GlanceModifier = GlanceModifier) {
  Image(
    provider = ImageProvider(R.drawable.alarm),
    contentDescription = null,
    modifier = modifier,
    colorFilter = colorFilter,
  )
}
