package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.data.AlarmLocalRepository
import com.trm.alarmist.core.domain.model.WidgetAlarmListModel
import com.trm.alarmist.core.util.addTestAlarm
import com.trm.alarmist.core.util.createTestAlarmLocalRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus

class GetTodayWidgetAlarmsUseCaseTests {
  private val dispatcher = StandardTestDispatcher()

  private lateinit var repo: AlarmLocalRepository

  @BeforeTest
  fun before() {
    repo = createTestAlarmLocalRepository(dispatcher)
  }

  @Test
  fun `given no alarms - then empty list is returned`() =
    runTest(dispatcher) { assertTrue(GetTodayWidgetAlarmsUseCase(repo)().isEmpty()) }

  @Test
  fun `given alarms scheduled for multiple days - then only alarms scheduled for today are returned`() =
    runTest(dispatcher) {
      val now = LocalDateTime(LocalDate.now(), LocalTime(12, 30))
      addTestAlarms(now)

      assertTrue(
        GetTodayWidgetAlarmsUseCase(repo)(now).all {
          (it.fireOnDateTime == null && it.fireAtTime > now.time) ||
            with(requireNotNull(it.fireOnDateTime)) { date == now.date && time > now.time }
        }
      )
    }

  @Test
  fun `given alarms scheduled for multiple days - then both on and off alarms are returned`() =
    runTest(dispatcher) {
      val now = LocalDateTime(LocalDate.now(), LocalTime(12, 30))
      addTestAlarms(now)

      val alarms = GetTodayWidgetAlarmsUseCase(repo)(now)

      assertTrue(alarms.any { it.isOn } && alarms.any { !it.isOn })
    }

  @Test
  fun `given alarms scheduled for multiple days - then alarms are sorted by fire time`() =
    runTest(dispatcher) {
      val now = LocalDateTime(LocalDate.now(), LocalTime(12, 30))
      addTestAlarms(now)

      val alarms = GetTodayWidgetAlarmsUseCase(repo)(now)

      assertContentEquals(alarms, alarms.sortedBy(WidgetAlarmListModel::fireAtTime))
    }

  @Test
  fun `given alarms scheduled for multiple days - then isCustomScheduled is correct for oneTime and scheduled alarms`() =
    runTest(dispatcher) {
      val now = LocalDateTime(LocalDate.now(), LocalTime(12, 30))
      val (oneTimeIds, scheduledIds) = addTestAlarms(now)

      val alarms = GetTodayWidgetAlarmsUseCase(repo)(now)

      assertTrue(
        alarms.filter { it.id in oneTimeIds }.none(WidgetAlarmListModel::isCustomScheduled)
      )
      assertTrue(
        alarms.filter { it.id in scheduledIds }.all(WidgetAlarmListModel::isCustomScheduled)
      )
    }

  private suspend fun addTestAlarms(now: LocalDateTime): PartitionedAlarmsIds =
    with(repo) {
      PartitionedAlarmsIds(
        oneTime =
          buildSet {
            add(addTestAlarm(fireAtTime = LocalTime(9, 30), isOn = true))
            add(addTestAlarm(fireAtTime = LocalTime(11, 30), isOn = false))
            add(addTestAlarm(fireAtTime = LocalTime(14, 45), isOn = true))
            add(addTestAlarm(fireAtTime = LocalTime(17, 25), isOn = false))
          },
        scheduled =
          buildSet {
            add(
              addTestAlarm(
                fireAtTime = LocalTime(13, 30),
                isOn = true,
                scheduledOnDates = listOf(now.date, now.date.plus(1L, DateTimeUnit.DAY)),
              )
            )
            add(
              addTestAlarm(
                fireAtTime = LocalTime(14, 50),
                isOn = false,
                scheduledOnDates = listOf(now.date, now.date.plus(1L, DateTimeUnit.DAY)),
              )
            )
            add(
              addTestAlarm(
                fireAtTime = LocalTime(13, 30),
                isOn = true,
                scheduledOnDates =
                  listOf(now.date.plus(1L, DateTimeUnit.DAY), now.date.plus(2L, DateTimeUnit.DAY)),
              )
            )
            add(
              addTestAlarm(
                fireAtTime = LocalTime(16, 15),
                isOn = false,
                scheduledOnDates =
                  listOf(now.date.plus(1L, DateTimeUnit.DAY), now.date.plus(2L, DateTimeUnit.DAY)),
              )
            )

            add(
              addTestAlarm(
                fireAtTime = LocalTime(10, 10),
                isOn = true,
                scheduledOnDaysOfWeek = listOf(now.date.dayOfWeek),
              )
            )
            add(
              addTestAlarm(
                fireAtTime = LocalTime(8, 45),
                isOn = false,
                scheduledOnDaysOfWeek = listOf(now.date.dayOfWeek),
              )
            )
            add(
              addTestAlarm(
                fireAtTime = LocalTime(15, 30),
                isOn = true,
                scheduledOnDaysOfWeek =
                  listOf(now.date.dayOfWeek, now.date.plus(1L, DateTimeUnit.DAY).dayOfWeek),
              )
            )
            add(
              addTestAlarm(
                fireAtTime = LocalTime(16, 25),
                isOn = false,
                scheduledOnDaysOfWeek =
                  listOf(now.date.dayOfWeek, now.date.plus(1L, DateTimeUnit.DAY).dayOfWeek),
              )
            )
            add(
              addTestAlarm(
                fireAtTime = LocalTime(16, 15),
                isOn = true,
                scheduledOnDaysOfWeek =
                  listOf(
                    now.date.plus(1L, DateTimeUnit.DAY).dayOfWeek,
                    now.date.plus(2L, DateTimeUnit.DAY).dayOfWeek,
                  ),
              )
            )
            add(
              addTestAlarm(
                fireAtTime = LocalTime(19, 35),
                isOn = false,
                scheduledOnDaysOfWeek =
                  listOf(
                    now.date.plus(1L, DateTimeUnit.DAY).dayOfWeek,
                    now.date.plus(2L, DateTimeUnit.DAY).dayOfWeek,
                  ),
              )
            )
          },
      )
    }

  private data class PartitionedAlarmsIds(val oneTime: Set<Long>, val scheduled: Set<Long>)
}
