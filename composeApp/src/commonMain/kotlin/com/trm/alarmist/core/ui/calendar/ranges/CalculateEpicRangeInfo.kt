package com.trm.alarmist.core.ui.calendar.ranges

import androidx.compose.ui.unit.IntOffset
import com.trm.alarmist.core.ui.calendar.basis.EpicCalendarConstants
import com.trm.alarmist.core.ui.calendar.basis.atEndDay
import com.trm.alarmist.core.ui.calendar.basis.atStartDay
import com.trm.alarmist.core.ui.calendar.basis.epicMonth
import com.trm.alarmist.core.ui.calendar.basis.index
import com.trm.alarmist.core.ui.calendar.basis.state.EpicCalendarGridInfo
import kotlinx.datetime.LocalDate

internal fun calculateEpicRangeInfo(
  displayDaysOfAdjacentMonths: Boolean,
  gridInfo: EpicCalendarGridInfo,
  startDate: LocalDate,
  endDate: LocalDate,
): EpicRangeInfo? {
  val startDateOfGrid =
    if (displayDaysOfAdjacentMonths) gridInfo.dateMatrix.first().first()
    else gridInfo.currentMonth.atStartDay()

  val endDateOfGrid =
    if (displayDaysOfAdjacentMonths) gridInfo.dateMatrix.last().last()
    else gridInfo.currentMonth.atEndDay()

  if (startDate > endDateOfGrid || endDate < startDateOfGrid) return null

  val startGridOffset = if (displayDaysOfAdjacentMonths) 0 else startDateOfGrid.dayOfWeek.index()

  val isStartInGrid = startDate >= startDateOfGrid
  val isEndInGrid = endDate <= endDateOfGrid

  val startGridItemOffset =
    if (isStartInGrid) {
      if (displayDaysOfAdjacentMonths) {
        when (startDate.epicMonth) {
          gridInfo.currentMonth -> {
            gridInfo.currentMonth.atStartDay().dayOfWeek.index() + startDate.day - 1
          }

          gridInfo.previousMonth -> {
            startDate.dayOfWeek.index()
          }

          gridInfo.nextMonth -> {
            gridInfo.currentMonth.atStartDay().dayOfWeek.index() +
              gridInfo.currentMonth.numberOfDays +
              startDate.day - 1
          }

          else -> {
            0
          }
        }
      } else {
        startGridOffset + startDate.day - 1
      }
    } else {
      startGridOffset
    }

  val endGridItemOffset =
    if (isEndInGrid) {
      if (displayDaysOfAdjacentMonths) {
        when (endDate.epicMonth) {
          gridInfo.currentMonth -> {
            gridInfo.currentMonth.atStartDay().dayOfWeek.index() + endDate.day - 1
          }

          gridInfo.previousMonth -> {
            endDate.dayOfWeek.index()
          }

          gridInfo.nextMonth -> {
            gridInfo.currentMonth.atStartDay().dayOfWeek.index() +
              gridInfo.currentMonth.numberOfDays +
              endDate.day - 1
          }

          else -> {
            EpicCalendarConstants.GridCellAmount - 1
          }
        }
      } else {
        startGridOffset + endDate.day - 1
      }
    } else {
      if (displayDaysOfAdjacentMonths) EpicCalendarConstants.GridCellAmount - 1
      else startGridOffset + gridInfo.currentMonth.numberOfDays - 1
    }

  val startCoordinates =
    IntOffset(
      x = startGridItemOffset % EpicCalendarConstants.DayOfWeekAmount,
      y = startGridItemOffset / EpicCalendarConstants.DayOfWeekAmount,
    )

  val endCoordinates =
    IntOffset(
      x = endGridItemOffset % EpicCalendarConstants.DayOfWeekAmount,
      y = endGridItemOffset / EpicCalendarConstants.DayOfWeekAmount,
    )

  return EpicRangeInfo(
    gridCoordinates = startCoordinates to endCoordinates,
    isStartInGrid = isStartInGrid,
    isEndInGrid = isEndInGrid,
  )
}
