package com.trm.alarmist.core.ui

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate

@Composable
fun ExpandableHeaderRow(
  modifier: Modifier = Modifier,
  isExpanded: Boolean = false,
  transitionLabel: String = "Header",
  content: @Composable RowScope.() -> Unit = {},
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    content()

    val expandedTransition =
      updateTransition(
        targetState = isExpanded,
        label = "updateTransition-$transitionLabel-isExpanded",
      )
    val expandImageRotation by
      expandedTransition.animateFloat(label = "animateFloat-$transitionLabel-rotation") { state ->
        if (state) 180f else 0f
      }
    Image(
      modifier = Modifier.rotate(expandImageRotation),
      imageVector = Icons.Default.ArrowDropDown,
      contentDescription = null,
    )
  }
}
