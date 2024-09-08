package com.trm.alarmist.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BoxScope.TopGradientBackground(color: Color = MaterialTheme.colorScheme.background) {
  Box(
    modifier =
      Modifier.fillMaxWidth()
        .height(8.dp)
        .background(brush = Brush.verticalGradient(colors = listOf(color, color.copy(alpha = 0f))))
        .align(Alignment.TopCenter)
  )
}

@Composable
fun BoxScope.BottomGradientBackground(color: Color = MaterialTheme.colorScheme.background) {
  Box(
    modifier =
      Modifier.fillMaxWidth()
        .height(8.dp)
        .background(brush = Brush.verticalGradient(colors = listOf(color.copy(alpha = 0f), color)))
        .align(Alignment.BottomCenter)
  )
}
