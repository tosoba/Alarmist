package com.trm.alarmist.feature.alarm.time

import com.arkivanov.decompose.ComponentContext
import kotlinx.datetime.LocalTime

interface AlarmTimeDialogComponent {
  val time: LocalTime

  val onConfirm: (LocalTime) -> Unit

  val onDismiss: () -> Unit
}

class DefaultAlarmTimeDialogComponent(
  private val componentContext: ComponentContext,
  override val time: LocalTime,
  override val onConfirm: (LocalTime) -> Unit,
  override val onDismiss: () -> Unit,
) : AlarmTimeDialogComponent, ComponentContext by componentContext
