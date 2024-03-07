package com.trm.alarmist.core.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate

@Composable
fun WeekArrowsRow(
  rowDates: List<LocalDate>,
  modifier: Modifier = Modifier,
  prevWeekEnabled: Boolean = false,
  onPrevWeekClick: () -> Unit = {},
  nextWeekEnabled: Boolean = false,
  onNextWeekClick: () -> Unit = {},
) {
  Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
    val monthYearText =
      remember(rowDates) {
        buildString {
          append(rowDates.first().month.name.lowercase().capitalize(Locale.current))
          if (rowDates.first().month != rowDates.last().month) {
            if (rowDates.first().year != rowDates.last().year) {
              append(" ")
              append(rowDates.first().year)
            }
            append(" - ")
            append(rowDates.last().month.name.lowercase().capitalize(Locale.current))
            append(" ")
            if (rowDates.first().year != rowDates.last().year) {
              append(rowDates.last().year)
            } else {
              append(rowDates.first().year)
            }
          } else {
            append(" ")
            append(rowDates.first().year)
          }
        }
      }

    Text(
      modifier = Modifier.padding(start = 16.dp),
      text = monthYearText,
      overflow = TextOverflow.Ellipsis,
      maxLines = 1,
    )

    Spacer(Modifier.weight(1f))

    IconButton(enabled = prevWeekEnabled, onClick = onPrevWeekClick) {
      Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous week")
    }

    IconButton(enabled = nextWeekEnabled, onClick = onNextWeekClick) {
      Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next week")
    }
  }
}
