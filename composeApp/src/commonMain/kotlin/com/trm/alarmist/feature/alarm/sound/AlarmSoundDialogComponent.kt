package com.trm.alarmist.feature.alarm.sound

import com.arkivanov.decompose.ComponentContext
import kotlinx.serialization.Serializable

interface AlarmSoundDialogComponent {
  val onConfirm: () -> Unit
  val onDismiss: () -> Unit

  @Serializable
  object Config
}

class DefaultAlarmSoundDialogComponent(
  private val componentContext: ComponentContext,
  override val onConfirm: () -> Unit,
  override val onDismiss: () -> Unit,
) : AlarmSoundDialogComponent, ComponentContext by componentContext
