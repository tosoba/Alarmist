package com.trm.alarmist.feature.root

import com.arkivanov.decompose.ComponentContext
import kotlinx.serialization.Serializable

interface RootDialogComponent {
  val title: String
  val message: String
  val onConfirm: () -> Unit
  val onDismiss: () -> Unit

  @Serializable data class Config(val title: String, val message: String)
}

class DefaultRootDialogComponent(
  private val componentContext: ComponentContext,
  override val title: String,
  override val message: String,
  override val onConfirm: () -> Unit,
  override val onDismiss: () -> Unit,
) : RootDialogComponent, ComponentContext by componentContext
