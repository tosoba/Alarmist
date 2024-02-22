package com.trm.alarmist.feature.group

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.ui.keyboardAsState

@Composable
fun GroupContent(
  modifier: Modifier = Modifier,
  state: GroupState = GroupState(),
  onNameChange: (String) -> Unit = {},
  onConfirmClick: () -> Unit = {},
) {
  Box(modifier = modifier) {
    Column(
      modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      val isKeyboardOpen by keyboardAsState()
      val focusManager = LocalFocusManager.current
      LaunchedEffect(isKeyboardOpen) { if (!isKeyboardOpen) focusManager.clearFocus() }
      OutlinedTextField(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        value = state.name.orEmpty(),
        onValueChange = onNameChange,
        label = { Text("Name") },
        singleLine = true,
      )

      GroupColors(modifier = Modifier.fillMaxWidth(), selectedColor = state.color?.let(::Color))
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
private fun GroupColors(modifier: Modifier = Modifier, selectedColor: Color? = null) {
  val boxColors = remember {
    listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue, Color.Magenta)
  }
  LazyRow(modifier = modifier, contentPadding = PaddingValues(16.dp)) {
    // TODO: add a gradient circle to show a palette to pick a custom color
    items(boxColors) { color ->
      GroupColor(color = color, isSelected = color == selectedColor, onClick = {})
      Spacer(modifier = Modifier.width(16.dp))
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroupColor(color: Color, isSelected: Boolean, onClick: () -> Unit) {
  ElevatedCard(
    modifier = Modifier.size(32.dp),
    shape = CircleShape,
    colors = CardDefaults.cardColors(containerColor = color),
    elevation =
      if (isSelected) {
        CardDefaults.cardElevation(
          defaultElevation = 1.dp,
          pressedElevation = 1.dp,
          focusedElevation = 1.dp,
        )
      } else {
        CardDefaults.cardElevation(
          defaultElevation = 0.dp,
          pressedElevation = 0.dp,
          focusedElevation = 0.dp,
        )
      },
    onClick = onClick,
  ) {
    if (isSelected) {
      Icon(Icons.Default.Check, contentDescription = null)
    }
  }
}
