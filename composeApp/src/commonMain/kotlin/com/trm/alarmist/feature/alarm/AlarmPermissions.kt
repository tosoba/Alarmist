package com.trm.alarmist.feature.alarm

import androidx.compose.runtime.Composable

@Composable
expect fun alarmPermissionsHandler(
  onDenied: (String) -> Unit,
  onGranted: () -> Unit
): () -> Unit
