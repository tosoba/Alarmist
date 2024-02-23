package com.trm.alarmist.feature.group

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.ui.keyboardAsState

@Composable
fun GroupContent(
  modifier: Modifier = Modifier,
  state: GroupState = GroupState(),
  onNameChange: (String) -> Unit = {},
  onColorChange: (Color) -> Unit,
  onConfirmClick: () -> Unit = {},
) {
  Box(modifier = modifier) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
      item {
        val isKeyboardOpen by keyboardAsState()
        val focusManager = LocalFocusManager.current
        LaunchedEffect(isKeyboardOpen) { if (!isKeyboardOpen) focusManager.clearFocus() }

        OutlinedTextField(
          modifier = Modifier.fillMaxWidth(),
          value = state.name.orEmpty(),
          onValueChange = onNameChange,
          label = { Text("Name") },
          singleLine = true,
        )
      }

      item { Spacer(modifier = Modifier.height(16.dp)) }

      item {
        GroupColors(
          modifier = Modifier.fillMaxWidth(),
          selectedColor = Color(state.color),
          onColorClick = onColorChange,
        )
      }

      // TODO: expandable sections for:
      // 1) alarms in group (only in edit mode)
      // 2) ungrouped alarms
      // 3) alarms in other groups that can be moved (?)
    }

    FloatingActionButton(
      modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
      onClick = onConfirmClick,
    ) {
      Icon(imageVector = Icons.Default.Check, contentDescription = "Confirm")
    }
  }
}

@Composable
private fun GroupColors(
  modifier: Modifier = Modifier,
  selectedColor: Color? = null,
  onColorClick: (Color) -> Unit,
) {
  val boxColors = remember {
    listOf(Color.Transparent, Color.Red, Color.Yellow, Color.Green, Color.Blue, Color.Magenta)
  }
  LazyRow(modifier = modifier) {
    // TODO: add a gradient circle to show a color picker dialog to pick a custom color
    items(boxColors) { color ->
      GroupColor(
        color = color,
        isSelected = color == selectedColor,
        onClick = remember(color) { { onColorClick(color) } },
      )
      Spacer(modifier = Modifier.width(16.dp))
    }
  }
}

@Composable
private fun GroupColor(color: Color, isSelected: Boolean, onClick: () -> Unit) {
  Box(
    modifier =
      Modifier.size(64.dp)
        .background(color = color, shape = CircleShape)
        .border(width = 1.dp, color = MaterialTheme.colorScheme.onBackground, shape = CircleShape)
        .clip(CircleShape)
        .clickable(onClick = onClick)
  ) {
    if (isSelected) {
      Icon(
        modifier = Modifier.align(Alignment.Center),
        imageVector = Icons.Default.Check,
        contentDescription = null,
      )
    }
  }
}
