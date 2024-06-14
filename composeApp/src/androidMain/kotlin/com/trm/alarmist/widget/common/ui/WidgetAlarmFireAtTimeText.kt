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
import com.trm.alarmist.core.common.util.amPmString
import com.trm.alarmist.core.common.util.toFormattedString
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
