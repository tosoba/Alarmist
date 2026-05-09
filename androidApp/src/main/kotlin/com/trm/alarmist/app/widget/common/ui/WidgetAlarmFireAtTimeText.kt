package com.trm.alarmist.app.widget.common.ui

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.layout.Box
import androidx.glance.text.FontWeight
import androidx.glance.text.TextDefaults
import androidx.glance.text.TextStyle
import com.trm.alarmist.app.R
import com.trm.alarmist.core.common.util.amPmString
import com.trm.alarmist.core.common.util.toFormattedString
import kotlinx.datetime.LocalTime

@Composable
internal fun WidgetAlarmFireAtTimeText(
  fireAtTime: LocalTime,
  is24HourFormat: Boolean,
  useFullFormat: Boolean,
  modifier: GlanceModifier = GlanceModifier,
  shadowMode: WidgetTextShadowMode = WidgetTextShadowMode.None,
  style: TextStyle = TextDefaults.defaultTextStyle,
) {
  Box(modifier = modifier) {
    AndroidRemoteViews(
      remoteViews =
        RemoteViews(
            LocalContext.current.packageName,
            when (shadowMode) {
              WidgetTextShadowMode.Dark -> {
                R.layout.widget_dark_shadow_alarm_fire_at_time_text_view
              }
              WidgetTextShadowMode.Light -> {
                R.layout.widget_light_shadow_alarm_fire_at_time_text_view
              }
              WidgetTextShadowMode.None -> {
                R.layout.widget_text_alarm_fire_at_time_view
              }
            },
          )
          .apply {
            val fireAtTimeText =
              if (useFullFormat) {
                  """${fireAtTime.toFormattedString { is24HourFormat }} ${fireAtTime.amPmString { is24HourFormat }}"""
                    .trim()
                } else {
                  fireAtTime.toFormattedString { is24HourFormat }
                }
                .replace(" ", "\u200A")

            setTextViewText(
              R.id.widget_text_view,
              SpannableString(fireAtTimeText).apply {
                val amPmIndex = fireAtTimeText.indexOfAny(listOf("am", "pm"), ignoreCase = true)

                setSpan(
                  RelativeSizeSpan(1f),
                  0,
                  if (amPmIndex != -1) amPmIndex else fireAtTimeText.length,
                  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                )

                if (amPmIndex != -1) {
                  setSpan(
                    RelativeSizeSpan(.5f),
                    amPmIndex,
                    fireAtTimeText.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                  )
                }

                if (style.fontWeight?.value != FontWeight.Normal.value) {
                  setSpan(
                    StyleSpan(Typeface.BOLD),
                    0,
                    fireAtTimeText.length,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE,
                  )
                }
              },
            )

            setInt(
              R.id.widget_text_view,
              "setTextColor",
              style.color.getColor(LocalContext.current).toArgb(),
            )

            style.fontSize?.value?.let { setFloat(R.id.widget_text_view, "setTextSize", it) }
          }
    )
  }
}
