package com.trm.alarmist.feature.alarm

import androidx.compose.runtime.Composable

@Composable
actual fun alarmPermissionsHandler(
  onDenied: (String) -> Unit,
  onGranted: () -> Unit
): () -> Unit = {}
