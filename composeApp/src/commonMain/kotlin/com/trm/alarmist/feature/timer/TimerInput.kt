package com.trm.alarmist.feature.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
    val input = remember { mutableStateListOf<Char>() }

    fun getInputAt(index: Int): Char = input.getOrNull(index) ?: '0'

    Text(
      text =
        "${getInputAt(5)}${getInputAt(4)}h ${getInputAt(3)}${getInputAt(2)}m ${getInputAt(1)}${getInputAt(0)}s",
      style =
        TextStyle(
          fontSize = MaterialTheme.typography.headlineLarge.fontSize,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground,
        ),
    )

    fun onTextInputButtonClick(text: String) {
      var charIndex = 0
      while (input.size < 6 && charIndex < text.length) {
        if (text[charIndex] != '0' || input.isNotEmpty()) {
          input.add(0, text[charIndex++])
        } else {
          break
        }
      }
    }

    // TODO: consistent sizing for all buttons regardless of screen size/orientation
    TimerInputButtonsRow(listOf("1", "2", "3"), ::onTextInputButtonClick)
    TimerInputButtonsRow(listOf("4", "5", "6"), ::onTextInputButtonClick)
    TimerInputButtonsRow(listOf("7", "8", "9"), ::onTextInputButtonClick)
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
    ) {
      TimerInputButton(onClick = { onTextInputButtonClick("00") }) { TimerInputButtonText("00") }
      TimerInputButton(onClick = { onTextInputButtonClick("0") }) { TimerInputButtonText("0") }
      TimerInputButton(onClick = { if (input.isNotEmpty()) input.removeFirst() }) {
        Icon(Icons.AutoMirrored.Filled.Backspace, "Backspace")
      }
    }
  }
}

@Composable
private fun TimerInputButtonsRow(texts: List<String>, onClick: (String) -> Unit) {
  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
    texts.forEach { text ->
      TimerInputButton(onClick = { onClick(text) }) { TimerInputButtonText(text) }
    }
  }
}

@Composable
private fun TimerInputButtonText(text: String) {
  Text(
    modifier = Modifier.padding(5.dp),
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
private fun TimerInputButton(onClick: () -> Unit, content: @Composable BoxScope.() -> Unit) {
  Box(
    modifier =
      Modifier.heightIn(max = 64.dp)
        .widthIn(max = 64.dp)
        .aspectRatio(1f)
        .padding(5.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primaryContainer)
        .clickable(onClick = onClick),
    contentAlignment = Alignment.Center,
    content = content,
  )
}
