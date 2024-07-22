package com.trm.alarmist.feature.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TimerInput() {
  Column(
    modifier =
      Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(30.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    val input = remember { mutableStateListOf('0', '0', '0', '0', '0', '0') }
    var index by remember { mutableIntStateOf(0) }

    Text(
      text = "${input[5]}${input[4]}h ${input[3]}${input[2]}m ${input[1]}${input[0]}s",
      style =
        TextStyle(
          fontSize = MaterialTheme.typography.headlineLarge.fontSize,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground,
        ),
    )

    fun onTextInputButtonClick(text: String) {
      var charIndex = 0
      while (index <= input.lastIndex && charIndex < text.length) {
        input[index++] = text[charIndex++]
      }
      index = minOf(index, input.lastIndex)
    }

    TimerInputButtonsRow(listOf("1", "2", "3"), ::onTextInputButtonClick)
    TimerInputButtonsRow(listOf("4", "5", "6"), ::onTextInputButtonClick)
    TimerInputButtonsRow(listOf("7", "8", "9"), ::onTextInputButtonClick)
  }
}

@Composable
private fun TimerInputButtonsRow(texts: List<String>, onClick: (String) -> Unit) {
  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
    for (text in texts) {
      TimerInputButton(text = text, onClick = onClick) { TimerInputButtonText(text) }
    }
  }
}

@Composable
private fun TimerInputButtonText(text: String) {
  Text(
    text = text,
    style =
      TextStyle(
        fontSize = MaterialTheme.typography.headlineLarge.fontSize,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
      ),
  )
}

@Composable
private fun TimerInputButton(
  text: String,
  onClick: (String) -> Unit,
  content: @Composable BoxScope.() -> Unit,
) {
  Box(
    modifier = Modifier.clip(CircleShape).padding(5.dp).clickable { onClick(text) },
    contentAlignment = Alignment.Center,
    content = content,
  )
}
