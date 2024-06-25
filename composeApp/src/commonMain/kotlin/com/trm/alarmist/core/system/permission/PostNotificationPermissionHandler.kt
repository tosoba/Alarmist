package com.trm.alarmist.core.system.permission

import androidx.compose.runtime.Composable

@Composable expect fun postNotificationsPermissionHandler(onGranted: () -> Unit): () -> Unit
