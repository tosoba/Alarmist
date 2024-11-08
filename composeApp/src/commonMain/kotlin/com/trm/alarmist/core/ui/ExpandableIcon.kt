package com.trm.alarmist.core.ui

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate

@Composable
fun ExpandableIcon(
  isExpanded: Boolean,
  modifier: Modifier = Modifier,
  transitionLabel: String = "ExpandableIcon",
) {
  val rotation by
    updateTransition(
        targetState = isExpanded,
        label = "updateTransition-$transitionLabel-isExpanded",
      )
      .animateFloat(label = "animateFloat-$transitionLabel-rotation") { state ->
        if (state) 180f else 0f
      }
  Icon(
    modifier = modifier.rotate(rotation),
    imageVector = Icons.Outlined.ExpandMore,
    contentDescription = null,
  )
}
