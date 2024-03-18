package com.trm.alarmist.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun AlarmCountDotsRow(count: Int, modifier: Modifier = Modifier) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
  ) {
    repeat(count.coerceAtMost(3)) {
      Box(
        Modifier.size(7.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.onPrimaryContainer)
      )
    }
  }
}

// TODO: better counts layout? Check how google calendar/other calendars do it.
