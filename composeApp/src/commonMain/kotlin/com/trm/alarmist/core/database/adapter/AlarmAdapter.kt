package com.trm.alarmist.core.database.adapter

import com.trm.alarmist.db.Alarm

fun alarmAdapter(): Alarm.Adapter =
  Alarm.Adapter(
    fireAtTimeAdapter = LocalTimeAdapter,
    scheduledOnDaysOfWeekAdapter = DayOfWeekSetAdapter,
    scheduledOnDatesAdapter = DateSetAdapter,
    offOnDatesAdapter = DateSetAdapter,
    lastModificationDateTimeAdapter = LocalDateTimeAdapter,
    lastNotificationDateAdapter = LocalDateAdapter,
    lastSnoozedAtAdapter = LocalDateTimeAdapter,
  )
