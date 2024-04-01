package com.trm.alarmist.core.domain

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.usecase.calculateAlarmNextFireOnDateTime
import kotlin.test.Test
import kotlin.test.assertNull
import kotlinx.datetime.LocalTime

class CalculateAlarmNextFireOnDateTimeTests {
  @Test
  fun `given off alarm - then return null`() {
    assertNull(
      calculateAlarmNextFireOnDateTime(
        fireAtTime = LocalTime.now(),
        scheduledOnDaysOfWeek = emptyList(),
        scheduledOnDates = emptyList(),
        offOnDates = emptyList(),
        isOn = false
      )
    )
  }
}
