package com.trm.alarmist.core.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CardDefaults.outlinedCardColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
expect fun AppTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = true, // Dynamic color is available on Android 12+
  content: @Composable () -> Unit,
)

@Composable
fun onOffContainer(isOn: Boolean): Color =
  if (isOn) outlinedCardColors().contentColor else cardColors().contentColor

@Composable
fun CardDefaults.onOffCardColors(isOn: Boolean): CardColors =
  if (isOn) outlinedCardColors() else cardColors()

@Composable
fun CardDefaults.onOffCardBorder(isOn: Boolean): BorderStroke? =
  if (isOn) outlinedCardBorder() else null
