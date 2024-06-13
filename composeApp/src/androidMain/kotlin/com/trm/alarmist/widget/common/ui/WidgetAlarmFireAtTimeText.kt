package com.trm.alarmist.widget.common.ui

import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.layout.Box
import androidx.glance.text.TextDefaults
import androidx.glance.text.TextStyle
import com.trm.alarmist.R
import kotlinx.datetime.LocalTime

@Composable
fun WidgetAlarmFireAtTimeText(
  fireAtTime: LocalTime,
  is24HourFormat: Boolean,
  useFullFormat: Boolean,
  modifier: GlanceModifier = GlanceModifier,
  style: TextStyle = TextDefaults.defaultTextStyle,
) {
  Box(modifier = modifier) {
    AndroidRemoteViews(
      remoteViews =
        RemoteViews(LocalContext.current.packageName, R.layout.widget_text_view).apply {
          val s = "BIG SMALL"
          setTextViewText(
            R.id.widget_text_view,
            SpannableString(s).apply {
              setSpan(RelativeSizeSpan(1f), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
              setSpan(RelativeSizeSpan(0.75f), 6, s.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            },
          )
        }
    )
  }

  //  Text(
  //    text =
  //      if (useFullFormat) {
  //        """${fireAtTime.toFormattedString { is24HourFormat }} ${fireAtTime.amPmString {
  // is24HourFormat }}"""
  //          .trim()
  //      } else {
  //        fireAtTime.toFormattedString { is24HourFormat }
  //      },
  //    modifier = modifier,
  //    style = style,
  //    maxLines = 1,
  //  )
}
