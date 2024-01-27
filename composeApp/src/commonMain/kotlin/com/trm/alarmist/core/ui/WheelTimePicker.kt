package com.trm.alarmist.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.SnapFlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.common.util.max
import com.trm.alarmist.core.common.util.min
import com.trm.alarmist.core.common.util.now
import kotlin.math.abs
import kotlinx.datetime.LocalTime

@Composable
fun WheelTimePicker(
  modifier: Modifier = Modifier,
  startTime: LocalTime = LocalTime.now(),
  minTime: LocalTime = LocalTime.min(),
  maxTime: LocalTime = LocalTime.max(),
  timeFormat: TimeFormat = TimeFormat.HOUR_24,
  size: DpSize = DpSize(128.dp, 128.dp),
  rowCount: Int = 3,
  textStyle: TextStyle = MaterialTheme.typography.titleMedium,
  textColor: Color = LocalContentColor.current,
  selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
  onSnappedTime: (snappedTime: LocalTime) -> Unit = {},
) {
  DefaultWheelTimePicker(
    modifier,
    startTime,
    minTime,
    maxTime,
    timeFormat,
    size,
    rowCount,
    textStyle,
    textColor,
    selectorProperties,
    onSnappedTime = { snappedTime, _ ->
      onSnappedTime(snappedTime.snappedLocalTime)
      snappedTime.snappedIndex
    }
  )
}

@Composable
internal fun DefaultWheelTimePicker(
  modifier: Modifier = Modifier,
  startTime: LocalTime = LocalTime.now(),
  minTime: LocalTime = LocalTime.min(),
  maxTime: LocalTime = LocalTime.max(),
  timeFormat: TimeFormat = TimeFormat.HOUR_24,
  size: DpSize = DpSize(128.dp, 128.dp),
  rowCount: Int = 3,
  textStyle: TextStyle = MaterialTheme.typography.titleMedium,
  textColor: Color = LocalContentColor.current,
  selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
  onSnappedTime: (snappedTime: SnappedTime, timeFormat: TimeFormat) -> Int? = { _, _ -> null },
) {
  var snappedTime by remember {
    mutableStateOf(LocalTime(hour = startTime.hour, minute = startTime.minute))
  }

  val hours = (0..23).map { Hour(text = it.toString().padStart(2, '0'), value = it, index = it) }
  val amPmHours = (1..12).map { AmPmHour(text = it.toString(), value = it, index = it - 1) }

  val minutes =
    (0..59).map { Minute(text = it.toString().padStart(2, '0'), value = it, index = it) }

  val amPms =
    listOf(
      AmPm(text = "AM", value = AmPmValue.AM, index = 0),
      AmPm(text = "PM", value = AmPmValue.PM, index = 1)
    )

  var snappedAmPm by remember {
    mutableStateOf(amPms.find { it.value == amPmValueFromTime(startTime) } ?: amPms[0])
  }

  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    if (selectorProperties.enabled().value) {
      Surface(
        modifier = Modifier.size(size.width, size.height / rowCount),
        shape = selectorProperties.shape().value,
        color = selectorProperties.color().value,
        border = selectorProperties.border().value
      ) {}
    }
    Row {
      // Hour
      WheelTextPicker(
        size =
          DpSize(
            width = size.width / if (timeFormat == TimeFormat.HOUR_24) 2 else 3,
            height = size.height
          ),
        texts =
          if (timeFormat == TimeFormat.HOUR_24) hours.map { it.text }
          else amPmHours.map { it.text },
        rowCount = rowCount,
        style = textStyle,
        color = textColor,
        startIndex =
          if (timeFormat == TimeFormat.HOUR_24) {
            hours.find { it.value == startTime.hour }?.index ?: 0
          } else amPmHours.find { it.value == localTimeToAmPmHour(startTime) }?.index ?: 0,
        selectorProperties = WheelPickerDefaults.selectorProperties(enabled = false),
        onScrollFinished = { snappedIndex ->
          val newHour =
            if (timeFormat == TimeFormat.HOUR_24) {
              hours.find { it.index == snappedIndex }?.value
            } else {
              amPmHourToHour24(
                amPmHours.find { it.index == snappedIndex }?.value ?: 0,
                snappedTime.minute,
                snappedAmPm.value
              )
            }

          newHour?.let {
            val newTime = LocalTime(newHour, snappedTime.minute)

            if (newTime in minTime..maxTime) {
              snappedTime = newTime
            }

            val newIndex =
              if (timeFormat == TimeFormat.HOUR_24) {
                hours.find { it.value == snappedTime.hour }?.index
              } else {
                amPmHours.find { it.value == localTimeToAmPmHour(snappedTime) }?.index
              }

            newIndex?.let {
              onSnappedTime(SnappedTime.Hour(localTime = snappedTime, index = newIndex), timeFormat)
                ?.let {
                  return@WheelTextPicker it
                }
            }
          }

          return@WheelTextPicker if (timeFormat == TimeFormat.HOUR_24) {
            hours.find { it.value == snappedTime.hour }?.index
          } else {
            amPmHours.find { it.value == localTimeToAmPmHour(snappedTime) }?.index
          }
        }
      )
      // Minute
      WheelTextPicker(
        size =
          DpSize(
            width = size.width / if (timeFormat == TimeFormat.HOUR_24) 2 else 3,
            height = size.height
          ),
        texts = minutes.map { it.text },
        rowCount = rowCount,
        style = textStyle,
        color = textColor,
        startIndex = minutes.find { it.value == startTime.minute }?.index ?: 0,
        selectorProperties = WheelPickerDefaults.selectorProperties(enabled = false),
        onScrollFinished = { snappedIndex ->
          val newMinute = minutes.find { it.index == snappedIndex }?.value

          val newHour =
            if (timeFormat == TimeFormat.HOUR_24) {
              hours.find { it.value == snappedTime.hour }?.value
            } else {
              amPmHourToHour24(
                amPmHours.find { it.value == localTimeToAmPmHour(snappedTime) }?.value ?: 0,
                snappedTime.minute,
                snappedAmPm.value
              )
            }

          newMinute?.let {
            newHour?.let {
              val newTime = LocalTime(hour = newHour, minute = newMinute)

              if (newTime in minTime..maxTime) {
                snappedTime = newTime
              }

              val newIndex = minutes.find { it.value == snappedTime.minute }?.index

              newIndex?.let {
                onSnappedTime(
                    SnappedTime.Minute(localTime = snappedTime, index = newIndex),
                    timeFormat
                  )
                  ?.let {
                    return@WheelTextPicker it
                  }
              }
            }
          }

          return@WheelTextPicker minutes.find { it.value == snappedTime.minute }?.index
        }
      )
      // AM_PM
      if (timeFormat == TimeFormat.AM_PM) {
        WheelTextPicker(
          size = DpSize(width = size.width / 3, height = size.height),
          texts = amPms.map { it.text },
          rowCount = rowCount,
          style = textStyle,
          color = textColor,
          startIndex = amPms.find { it.value == amPmValueFromTime(startTime) }?.index ?: 0,
          selectorProperties = WheelPickerDefaults.selectorProperties(enabled = false),
          onScrollFinished = { snappedIndex ->
            val newAmPm =
              amPms.find {
                if (snappedIndex == 2) {
                  it.index == 1
                } else {
                  it.index == snappedIndex
                }
              }

            newAmPm?.let { snappedAmPm = newAmPm }

            val newMinute = minutes.find { it.value == snappedTime.minute }?.value

            val newHour =
              amPmHourToHour24(
                amPmHours.find { it.value == localTimeToAmPmHour(snappedTime) }?.value ?: 0,
                snappedTime.minute,
                snappedAmPm.value
              )

            newMinute?.let {
              val newTime = LocalTime(hour = newHour, minute = newMinute)

              if (newTime in minTime..maxTime) {
                snappedTime = newTime
              }

              val newIndex = minutes.find { it.value == snappedTime.hour }?.index

              newIndex?.let {
                onSnappedTime(
                  SnappedTime.Hour(localTime = snappedTime, index = newIndex),
                  timeFormat
                )
              }
            }

            return@WheelTextPicker snappedIndex
          }
        )
      }
    }
    Box(
      modifier =
        Modifier.size(
            width =
              if (timeFormat == TimeFormat.HOUR_24) {
                size.width
              } else size.width * 2 / 3,
            height = size.height / 3
          )
          .align(
            alignment =
              if (timeFormat == TimeFormat.HOUR_24) {
                Alignment.Center
              } else Alignment.CenterStart
          ),
      contentAlignment = Alignment.Center
    ) {
      Text(text = ":", style = textStyle, color = textColor)
    }
  }
}

enum class TimeFormat {
  HOUR_24,
  AM_PM
}

private data class Hour(val text: String, val value: Int, val index: Int)

private data class AmPmHour(val text: String, val value: Int, val index: Int)

internal fun localTimeToAmPmHour(localTime: LocalTime): Int {
  if (isBetween(localTime, LocalTime(0, 0), LocalTime(0, 59))) {
    return localTime.hour + 12
  }

  if (isBetween(localTime, LocalTime(1, 0), LocalTime(11, 59))) {
    return localTime.hour
  }

  if (isBetween(localTime, LocalTime(12, 0), LocalTime(12, 59))) {
    return localTime.hour
  }

  if (isBetween(localTime, LocalTime(13, 0), LocalTime(23, 59))) {
    return localTime.hour - 12
  }

  return localTime.hour
}

private fun isBetween(localTime: LocalTime, startTime: LocalTime, endTime: LocalTime): Boolean =
  localTime in startTime..endTime

private fun amPmHourToHour24(amPmHour: Int, amPmMinute: Int, amPmValue: AmPmValue): Int =
  when (amPmValue) {
    AmPmValue.AM -> {
      if (amPmHour == 12 && amPmMinute <= 59) {
        0
      } else {
        amPmHour
      }
    }
    AmPmValue.PM -> {
      if (amPmHour == 12 && amPmMinute <= 59) {
        amPmHour
      } else {
        amPmHour + 12
      }
    }
  }

private data class Minute(val text: String, val value: Int, val index: Int)

private data class AmPm(val text: String, val value: AmPmValue, val index: Int?)

internal enum class AmPmValue {
  AM,
  PM
}

private fun amPmValueFromTime(time: LocalTime): AmPmValue =
  if (time.hour > 11) AmPmValue.PM else AmPmValue.AM

internal sealed class SnappedTime(val snappedLocalTime: LocalTime, val snappedIndex: Int) {
  data class Hour(val localTime: LocalTime, val index: Int) : SnappedTime(localTime, index)

  data class Minute(val localTime: LocalTime, val index: Int) : SnappedTime(localTime, index)
}

@Composable
fun WheelTextPicker(
  modifier: Modifier = Modifier,
  startIndex: Int = 0,
  size: DpSize = DpSize(128.dp, 128.dp),
  texts: List<String>,
  rowCount: Int,
  style: TextStyle = MaterialTheme.typography.titleMedium,
  color: Color = LocalContentColor.current,
  selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
  onScrollFinished: (snappedIndex: Int) -> Int? = { null },
) {
  WheelPicker(
    modifier = modifier,
    startIndex = startIndex,
    size = size,
    count = texts.size,
    rowCount = rowCount,
    selectorProperties = selectorProperties,
    onScrollFinished = onScrollFinished
  ) { index ->
    Text(text = texts[index], style = style, color = color, maxLines = 1)
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun WheelPicker(
  modifier: Modifier = Modifier,
  startIndex: Int = 0,
  count: Int,
  rowCount: Int,
  size: DpSize = DpSize(128.dp, 128.dp),
  selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
  onScrollFinished: (snappedIndex: Int) -> Int? = { null },
  content: @Composable LazyItemScope.(index: Int) -> Unit,
) {
  val lazyListState = rememberLazyListState(startIndex)
  val snappingLayout = remember(lazyListState) { SnapLayoutInfoProvider(lazyListState) }
  val flingBehavior: SnapFlingBehavior = rememberSnapFlingBehavior(snappingLayout)

  val isScrollInProgress = lazyListState.isScrollInProgress
  LaunchedEffect(isScrollInProgress, count) {
    if (!isScrollInProgress) {
      val layoutInfo = lazyListState.layoutInfo
      val visibleItems = layoutInfo.visibleItemsInfo
      val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
      onScrollFinished(
        minOf(
          visibleItems.minByOrNull { abs((it.offset + it.size / 2) - viewportCenter) }?.index ?: 0,
          count - 1
        )
      )
    }
  }

  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    if (selectorProperties.enabled().value) {
      Surface(
        modifier = Modifier.size(size.width, size.height / rowCount),
        shape = selectorProperties.shape().value,
        color = selectorProperties.color().value,
        border = selectorProperties.border().value
      ) {}
    }

    LazyColumn(
      modifier = Modifier.height(size.height).width(size.width),
      state = lazyListState,
      contentPadding = PaddingValues(vertical = size.height / rowCount * ((rowCount - 1) / 2)),
      flingBehavior = flingBehavior
    ) {
      items(count) { index ->
        val rotationX =
          calculateAnimatedRotationX(
            lazyListState = lazyListState,
            index = index,
            rowCount = rowCount
          )
        Box(
          modifier =
            Modifier.height(size.height / rowCount)
              .width(size.width)
              .alpha(calculateAnimatedAlpha(lazyListState = lazyListState, index = index))
              .graphicsLayer { this.rotationX = rotationX },
          contentAlignment = Alignment.Center
        ) {
          content(index)
        }
      }
    }
  }
}

object WheelPickerDefaults {
  @Composable
  fun selectorProperties(
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(16.dp),
    color: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
    border: BorderStroke? = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
  ): SelectorProperties =
    DefaultSelectorProperties(enabled = enabled, shape = shape, color = color, border = border)
}

interface SelectorProperties {
  @Composable fun enabled(): State<Boolean>

  @Composable fun shape(): State<Shape>

  @Composable fun color(): State<Color>

  @Composable fun border(): State<BorderStroke?>
}

@Immutable
internal class DefaultSelectorProperties(
  private val enabled: Boolean,
  private val shape: Shape,
  private val color: Color,
  private val border: BorderStroke?
) : SelectorProperties {
  @Composable override fun enabled(): State<Boolean> = rememberUpdatedState(enabled)

  @Composable override fun shape(): State<Shape> = rememberUpdatedState(shape)

  @Composable override fun color(): State<Color> = rememberUpdatedState(color)

  @Composable override fun border(): State<BorderStroke?> = rememberUpdatedState(border)
}

@Composable
private fun calculateAnimatedAlpha(lazyListState: LazyListState, index: Int): Float {
  val layoutInfo: LazyListLayoutInfo =
    remember { derivedStateOf { lazyListState.layoutInfo } }.value
  val centerIndex = layoutInfo.centerIndex()
  val indexDelta = abs(centerIndex - index)
  var alpha = 1f
  repeat(indexDelta) { alpha /= 2f }
  return alpha
}

@Composable
private fun calculateAnimatedRotationX(
  lazyListState: LazyListState,
  index: Int,
  rowCount: Int,
  maxRotationDegrees: Float = 60f
): Float {
  val layoutInfo: LazyListLayoutInfo =
    remember { derivedStateOf { lazyListState.layoutInfo } }.value
  val centerIndex = layoutInfo.centerIndex()
  val indexDelta = abs(centerIndex - index)
  var rotationXDegrees = 0f
  repeat(indexDelta) { rotationXDegrees += maxRotationDegrees / rowCount }
  return rotationXDegrees
}

private fun LazyListLayoutInfo.centerIndex(): Int {
  val viewportCenter = (viewportStartOffset + viewportEndOffset) / 2
  return visibleItemsInfo.minByOrNull { abs((it.offset + it.size / 2) - viewportCenter) }?.index
    ?: 0
}
