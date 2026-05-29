package com.trm.alarmist.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trm.alarmist.core.common.util.zeroPadded
import kotlin.time.Duration

enum class DurationTextLayoutType {
  Vertical,
  Horizontal,
}

@Composable
fun DurationText(
  duration: Duration,
  layoutType: DurationTextLayoutType,
  modifier: Modifier = Modifier,
) {
  val (time, fractionOfSecond) = rememberDurationText(duration)
  when (layoutType) {
    DurationTextLayoutType.Vertical -> {
      Column(horizontalAlignment = Alignment.End, modifier = modifier) {
        TimeText(text = time)
        FractionOfSecondText(text = fractionOfSecond)
      }
    }
    DurationTextLayoutType.Horizontal -> {
      Row(verticalAlignment = Alignment.Bottom, modifier = modifier) {
        TimeText(text = time, modifier = Modifier.alignByBaseline())
        Spacer(modifier = Modifier.width(8.dp))
        FractionOfSecondText(text = fractionOfSecond, modifier = Modifier.alignByBaseline())
      }
    }
  }
}

@Composable
fun rememberDurationText(duration: Duration): Pair<String, String> =
  remember(duration) {
    duration.toComponents { hours, minutes, seconds, nanoseconds ->
      buildString {
        if (hours > 0L) {
          append(hours.toInt())
          append(':')
        }
        if (hours > 0L || minutes > 0) {
          append(minutes)
          append(':')
        }
        append(seconds.zeroPadded())
      } to (nanoseconds / 10_000_000L).toInt().zeroPadded()
    }
  }

@Composable
private fun TimeText(text: String, modifier: Modifier = Modifier) {
  AutoSizeText(
    text = text,
    style = MaterialTheme.typography.headlineMedium,
    fontWeight = FontWeight.Medium,
    modifier = modifier,
    maxTextSize = 72.sp,
  )
}

@Composable
private fun FractionOfSecondText(text: String, modifier: Modifier = Modifier) {
  AutoSizeText(
    text = text,
    style = MaterialTheme.typography.headlineMedium,
    fontWeight = FontWeight.Medium,
    modifier = modifier,
    maxTextSize = 36.sp,
  )
}
