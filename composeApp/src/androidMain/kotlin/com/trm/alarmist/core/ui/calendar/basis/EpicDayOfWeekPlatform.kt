package com.trm.alarmist.core.ui.calendar.basis

import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.toJavaDayOfWeek
import kotlinx.datetime.toKotlinDayOfWeek

actual fun firstDayOfWeek(): DayOfWeek =
  WeekFields.of(Locale.getDefault()).firstDayOfWeek!!.toKotlinDayOfWeek()

actual fun DayOfWeek.localized(): String =
  toJavaDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault())!!
