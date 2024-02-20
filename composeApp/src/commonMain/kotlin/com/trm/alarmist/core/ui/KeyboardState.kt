package com.trm.alarmist.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

@Composable expect fun keyboardAsState(): State<Boolean>
