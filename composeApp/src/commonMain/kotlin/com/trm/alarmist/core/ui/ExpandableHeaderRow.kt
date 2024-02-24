package com.trm.alarmist.core.ui

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableHeaderRow(
  modifier: Modifier = Modifier,
  isExpanded: Boolean = false,
  text: String = "",
  transitionLabel: String = "Header",
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(modifier = Modifier.padding(horizontal = 8.dp), text = text)
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
