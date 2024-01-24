package com.trm.alarmist.core.common.util

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

fun now(): Instant = Clock.System.now()

fun Instant.nextFullHour(): Int =
  plus(1, DateTimeUnit.HOUR).toLocalDateTime(TimeZone.currentSystemDefault()).hour
