package com.trm.alarmist.core.ui

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.next_month
import alarmist.composeapp.generated.resources.previous_month
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import epicarchitect.calendar.compose.pager.state.EpicCalendarPagerState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun DatePickerYearMonthControls(pagerState: EpicCalendarPagerState, modifier: Modifier = Modifier) {
  val scope = rememberCoroutineScope()
  Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
    Text(
      modifier = Modifier.padding(start = 16.dp),
      text =
        "${pagerState.currentMonth.month.name.lowercase().capitalize(Locale.current)} ${pagerState.currentMonth.year}",
      style = MaterialTheme.typography.titleMedium,
    ) // TODO: copy over year selection expandable menu from material DatePicker

    Spacer(Modifier.weight(1f))

    IconButton(
      enabled = pagerState.currentMonth > pagerState.monthRange.start,
      onClick = { scope.launch { pagerState.scrollMonths(-1) } },
    ) {
      Icon(
        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
        contentDescription = stringResource(Res.string.previous_month),
      )
    }

    IconButton(
      enabled = pagerState.currentMonth < pagerState.monthRange.endInclusive,
      onClick = { scope.launch { pagerState.scrollMonths(1) } },
    ) {
      Icon(
        Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = stringResource(Res.string.next_month),
      )
    }
  }
}
