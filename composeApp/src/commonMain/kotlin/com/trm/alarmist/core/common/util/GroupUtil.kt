package com.trm.alarmist.core.common.util

import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.db.SelectAllGroups

fun SelectAllGroups.toModel(): AlarmGroupModel =
  AlarmGroupModel(id = id, name = name, color = color, alarmsCount = alarmsCount)
