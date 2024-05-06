package com.trm.alarmist.feature.alarm

import androidx.compose.runtime.Composable

@Composable expect fun alarmPermissionsHandler(onGranted: () -> Unit): () -> Unit
