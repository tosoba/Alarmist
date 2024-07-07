package com.trm.alarmist.feature.dialog

import com.arkivanov.decompose.ComponentContext
import kotlinx.serialization.Serializable

interface DialogComponent {
  val title: String
  val message: String
  val onConfirm: () -> Unit
  val onDismiss: () -> Unit

  @Serializable data class Config(val title: String, val message: String)
}

class DefaultDialogComponent(
  private val componentContext: ComponentContext,
  override val title: String,
  override val message: String,
  override val onConfirm: () -> Unit,
  override val onDismiss: () -> Unit,
) : DialogComponent, ComponentContext by componentContext
