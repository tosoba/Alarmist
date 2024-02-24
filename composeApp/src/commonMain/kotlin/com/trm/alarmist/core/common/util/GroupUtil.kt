package com.trm.alarmist.core.common.util

import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.db.AlarmGroup

fun AlarmGroup.toModel(): AlarmGroupModel = AlarmGroupModel(id, name, color)
