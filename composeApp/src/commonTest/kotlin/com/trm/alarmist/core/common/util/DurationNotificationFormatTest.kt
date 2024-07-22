package com.trm.alarmist.core.common.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

class DurationNotificationFormatTest {
  @Test
  fun `given duration with zero milliseconds - then return non modified formatted duration`() {
    assertEquals("00:00:01", 1.seconds.toNotificationFormat())
    assertEquals("00:00:59", 59.seconds.toNotificationFormat())
    assertEquals("00:59:59", (59.minutes + 59.seconds).toNotificationFormat())
    assertEquals("01:59:59", (1.hours + 59.minutes + 59.seconds).toNotificationFormat())
  }

  @Test
  fun `given duration with non zero milliseconds - then return formatted duration plus 1 second`() {
    assertEquals("00:00:02", (1.seconds + 1.milliseconds).toNotificationFormat())
    assertEquals("00:01:00", (59.seconds + 1.milliseconds).toNotificationFormat())
    assertEquals("01:00:00", (59.minutes + 59.seconds + 1.milliseconds).toNotificationFormat())
    assertEquals(
      "02:00:00",
      (1.hours + 59.minutes + 59.seconds + 1.milliseconds).toNotificationFormat(),
    )
  }

  @Test
  fun `given duration with zero microseconds - then return non modified formatted duration`() {
    assertEquals("00:00:01", (1.seconds + 1.microseconds).toNotificationFormat())
    assertEquals("00:00:59", (59.seconds + 1.microseconds).toNotificationFormat())
    assertEquals("00:59:59", (59.minutes + 59.seconds + 1.microseconds).toNotificationFormat())
    assertEquals(
      "01:59:59",
      (1.hours + 59.minutes + 59.seconds + 1.microseconds).toNotificationFormat(),
    )
  }

  @Test
  fun `given duration with zero nanoseconds - then return non modified formatted duration`() {
    assertEquals("00:00:01", (1.seconds + 1.nanoseconds).toNotificationFormat())
    assertEquals("00:00:59", (59.seconds + 1.nanoseconds).toNotificationFormat())
    assertEquals("00:59:59", (59.minutes + 59.seconds + 1.nanoseconds).toNotificationFormat())
    assertEquals(
      "01:59:59",
      (1.hours + 59.minutes + 59.seconds + 1.nanoseconds).toNotificationFormat(),
    )
  }
}
