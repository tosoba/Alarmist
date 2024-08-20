package com.trm.alarmist.core.ui.calendar.basis.state

import androidx.compose.runtime.Immutable
import com.trm.alarmist.core.ui.calendar.basis.EpicCalendarConstants
import com.trm.alarmist.core.ui.calendar.basis.EpicMonth
import com.trm.alarmist.core.ui.calendar.basis.atDay
import com.trm.alarmist.core.ui.calendar.basis.config.BasisEpicCalendarConfig
import com.trm.alarmist.core.ui.calendar.basis.lastDayOfWeek
import com.trm.alarmist.core.ui.calendar.basis.next
import com.trm.alarmist.core.ui.calendar.basis.previous
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber

@Immutable
data class EpicCalendarGridInfo(
  val dateMatrix: List<List<LocalDate>>,
  val currentMonth: EpicMonth,
  val previousMonth: EpicMonth,
  val nextMonth: EpicMonth,
)

internal fun calculateEpicCalendarGridInfo(
  currentMonth: EpicMonth,
  config: BasisEpicCalendarConfig,
): EpicCalendarGridInfo {
  val firstDayOfWeek = config.daysOfWeek.first()
  val previousMonth = currentMonth.previous()
  val nextMonth = currentMonth.next()
  val previousMonthLastDayOfWeek = previousMonth.lastDayOfWeek()

  val lastDaysAmountInPreviousMonth =
    when (firstDayOfWeek) {
      DayOfWeek.MONDAY -> previousMonthLastDayOfWeek.isoDayNumber
      DayOfWeek.SUNDAY -> {
        if (previousMonthLastDayOfWeek == DayOfWeek.SATURDAY) 0
        else previousMonthLastDayOfWeek.isoDayNumber + 1
      }

      else -> error("Unexpected firstDayOfWeek: $firstDayOfWeek")
    } % EpicCalendarConstants.DayOfWeekAmount

  val daysAmountInCurrentMonth = currentMonth.numberOfDays
  val firstDaysAmountInNextMonth =
    (EpicCalendarConstants.GridCellAmount -
        lastDaysAmountInPreviousMonth -
        daysAmountInCurrentMonth)
      .let {
        if (config.displayDaysOfAdjacentMonths) it else it % EpicCalendarConstants.DayOfWeekAmount
      }

  val dates = mutableListOf<LocalDate>()

  repeat(lastDaysAmountInPreviousMonth) {
    dates.add(
      previousMonth.atDay(previousMonth.numberOfDays + it + 1 - lastDaysAmountInPreviousMonth)
    )
  }

  repeat(daysAmountInCurrentMonth) { dates.add(currentMonth.atDay(it + 1)) }

  repeat(firstDaysAmountInNextMonth) { dates.add(nextMonth.atDay(it + 1)) }

  return EpicCalendarGridInfo(
    dateMatrix = dates.chunked(EpicCalendarConstants.DayOfWeekAmount),
    currentMonth = currentMonth,
    previousMonth = previousMonth,
    nextMonth = nextMonth,
  )
}
