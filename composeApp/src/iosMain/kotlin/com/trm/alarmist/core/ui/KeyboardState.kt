package com.trm.alarmist.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue

@Composable
actual fun keyboardAsState(): State<Boolean> {
  // TODO:
  var isKeyboardVisible by remember { mutableStateOf(false) }
  return rememberUpdatedState(isKeyboardVisible)
}
