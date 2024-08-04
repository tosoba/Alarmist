package com.trm.alarmist.core.domain

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.usecase.IsAlarmScheduledToFireAtDateTimeUseCase
import com.trm.alarmist.core.util.alarmModel
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime

class IsAlarmScheduledToFireAtDateTimeUseCaseTests {
  @Test
  fun `given off alarm - then return false`() = runTest {
    assertFalse(
      IsAlarmScheduledToFireAtDateTimeUseCase(
        mock { everySuspend { getAlarmById(any()) } returns alarmModel(isOn = false) }
      )(1L, LocalDateTime.now())
    )
  }
}
