package com.trm.alarmist.core.domain

import com.trm.alarmist.core.domain.usecase.CalculateMissedAlarmsDateTimesUseCase
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class CalculateMissedAlarmsDateTimesUseCaseTests {
  @Test
  fun `given empty on alarms - then return emptyMap`() = runTest {
    val repository = mock<AlarmRepository>()
    everySuspend { repository.getAllOnAlarms() } returns emptyList()
    assertEquals(emptyMap(), CalculateMissedAlarmsDateTimesUseCase(repository)())
  }
}
