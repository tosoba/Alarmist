package com.trm.alarmist.feature.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
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
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun TimerInput(onStartClick: (Duration) -> Unit) {
  // TODO: row based layout for short screens (phone in landscape)
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

    Spacer(modifier = Modifier.height(16.dp))

    BoxWithConstraints {
      val space = 5.dp
      val columnCount = 3
      val rowCount = 4
      val buttonModifier =
        Modifier.requiredSize(
          minOf(
            (maxWidth - space * (columnCount - 1)) / columnCount,
            // TODO: - 96.dp only for column based layout
            (maxHeight - space * (rowCount - 1) - 96.dp) / rowCount,
            96.dp,
          )
        )

      @Composable
      fun TimerInputTextButton(text: String) {
        TimerInputButton(onClick = { onTextInputButtonClick(text) }, modifier = buttonModifier) {
          TimerInputButtonText(text)
        }
      }

      Column(
        verticalArrangement = Arrangement.spacedBy(space, alignment = Alignment.CenterVertically)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement =
            Arrangement.spacedBy(space, alignment = Alignment.CenterHorizontally),
        ) {
          TimerInputTextButton("1")
          TimerInputTextButton("2")
          TimerInputTextButton("3")
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement =
            Arrangement.spacedBy(space, alignment = Alignment.CenterHorizontally),
        ) {
          TimerInputTextButton("4")
          TimerInputTextButton("5")
          TimerInputTextButton("6")
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement =
            Arrangement.spacedBy(space, alignment = Alignment.CenterHorizontally),
        ) {
          TimerInputTextButton("7")
          TimerInputTextButton("8")
          TimerInputTextButton("9")
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement =
            Arrangement.spacedBy(space, alignment = Alignment.CenterHorizontally),
        ) {
          TimerInputTextButton("00")
          TimerInputTextButton("0")
          TimerInputButton(
            onClick = { if (input.isNotEmpty()) input.removeFirst() },
            modifier = buttonModifier,
          ) {
            Icon(Icons.AutoMirrored.Filled.Backspace, "Backspace")
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    LargeFloatingActionButton(
      onClick = {
        onStartClick(
          "${getInputAt(5)}${getInputAt(4)}".toInt().hours +
            "${getInputAt(3)}${getInputAt(2)}".toInt().minutes +
            "${getInputAt(1)}${getInputAt(0)}".toInt().seconds
        )
      }
    ) {
      Icon(Icons.Default.PlayArrow, "Start timer")
    }
  }
}

@Composable
private fun TimerInputButtonText(text: String) {
  // TODO: AutoSizeText
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
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable BoxScope.() -> Unit,
) {
  Box(
    modifier =
      modifier
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primaryContainer)
        .clickable(onClick = onClick),
    contentAlignment = Alignment.Center,
    content = content,
  )
}
