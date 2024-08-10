package com.trm.alarmist.core.ui

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ShapeDefaults
import androidx.compose.ui.unit.dp

fun groupedAlarmItemShape(
  index: Int,
  firstInLastRowAlarmIndex: Int?,
  lastInLastRowAlarmIndex: Int?,
  fullSpan: Int,
  groupAlarmsCount: Int,
): CornerBasedShape =
  ShapeDefaults.Medium.copy(
    topStart = CornerSize(0.dp),
    topEnd = CornerSize(0.dp),
    bottomStart =
      if (index == firstInLastRowAlarmIndex && groupAlarmsCount % fullSpan != 0) {
        ShapeDefaults.Medium.bottomStart
      } else {
        CornerSize(0.dp)
      },
    bottomEnd =
      if ((index == lastInLastRowAlarmIndex || index == groupAlarmsCount - 1) && fullSpan != 1) {
        ShapeDefaults.Medium.bottomEnd
      } else {
        CornerSize(0.dp)
      },
  )
