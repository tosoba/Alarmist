package com.trm.alarmist.feature.alarm

import kotlinx.serialization.Serializable

@Serializable
sealed interface AlarmDialogChildConfig {
  @Serializable data object Sound : AlarmDialogChildConfig

  @Serializable data object Time : AlarmDialogChildConfig
}
