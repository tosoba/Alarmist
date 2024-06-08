package com.trm.alarmist.core.common.util

import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable

@Composable
fun CardDefaults.elevatedIf(elevated: Boolean): CardElevation =
  if (elevated) elevatedCardElevation() else cardElevation()
