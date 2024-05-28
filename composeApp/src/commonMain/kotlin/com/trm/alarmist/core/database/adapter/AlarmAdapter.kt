package com.trm.alarmist.core.database.adapter

import com.trm.alarmist.db.Alarm

fun alarmAdapter(): Alarm.Adapter =
  Alarm.Adapter(
    fireAtTimeAdapter = LocalTimeAdapter,
    scheduledOnDaysOfWeekAdapter = DayOfWeekListAdapter,
    scheduledOnDatesAdapter = DateListAdapter,
    offOnDatesAdapter = DateListAdapter,
    lastModificationDateTimeAdapter = LocalDateTimeAdapter,
    lastNotificationDateAdapter = LocalDateAdapter,
    lastSnoozedAtAdapter = LocalDateTimeAdapter,
  )
