package com.trm.alarmist.core.common.util

import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.db.SelectAllGroups
import com.trm.alarmist.db.SelectGroupById

fun SelectAllGroups.toModel(): AlarmGroupModel =
  AlarmGroupModel(
    id = id,
    name = name,
    color = color,
    alarmsCount = alarmsCount,
    isOn = isOn == DB_ON,
  )

fun SelectGroupById.toModel(): AlarmGroupModel =
  AlarmGroupModel(
    id = id,
    name = name,
    color = color,
    alarmsCount = alarmsCount,
    isOn = isOn == DB_ON,
  )
