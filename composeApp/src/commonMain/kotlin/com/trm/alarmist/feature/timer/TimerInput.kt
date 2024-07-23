package com.trm.alarmist.feature.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trm.alarmist.core.ui.AutoSizeText
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun TimerInput(onStartClick: (Duration) -> Unit, modifier: Modifier = Modifier) {
  val windowSizeClass = calculateWindowSizeClass()

  val input = remember { mutableStateListOf<Char>() }
  fun getInputAt(index: Int): Char = input.getOrNull(index) ?: '0'

  @Composable
  fun TimerDurationText() {
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
  }

  @Composable
  fun TimerInputTextButton(text: String, modifier: Modifier) {
    TimerInputButton(
      onClick = {
        var charIndex = 0
        while (input.size < 6 && charIndex < text.length) {
          if (text[charIndex] != '0' || input.isNotEmpty()) {
            input.add(0, text[charIndex++])
          } else {
            break
          }
        }
      },
      modifier = modifier,
    ) {
      TimerInputButtonText(text)
    }
  }

  @Composable
  fun TimerStartButton() {
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

  @Composable
  fun TimerInputKeyboard(
    space: Dp = 5.dp,
    modifier: Modifier = Modifier,
    calculateButtonModifier: BoxWithConstraintsScope.(Dp) -> Modifier,
  ) {
    BoxWithConstraints(modifier = modifier) {
      val buttonModifier = calculateButtonModifier(space)

      Column(
        verticalArrangement = Arrangement.spacedBy(space, alignment = Alignment.CenterVertically)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement =
            Arrangement.spacedBy(space, alignment = Alignment.CenterHorizontally),
        ) {
          TimerInputTextButton("1", modifier = buttonModifier)
          TimerInputTextButton("2", modifier = buttonModifier)
          TimerInputTextButton("3", modifier = buttonModifier)
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement =
            Arrangement.spacedBy(space, alignment = Alignment.CenterHorizontally),
        ) {
          TimerInputTextButton("4", modifier = buttonModifier)
          TimerInputTextButton("5", modifier = buttonModifier)
          TimerInputTextButton("6", modifier = buttonModifier)
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement =
            Arrangement.spacedBy(space, alignment = Alignment.CenterHorizontally),
        ) {
          TimerInputTextButton("7", modifier = buttonModifier)
          TimerInputTextButton("8", modifier = buttonModifier)
          TimerInputTextButton("9", modifier = buttonModifier)
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement =
            Arrangement.spacedBy(space, alignment = Alignment.CenterHorizontally),
        ) {
          TimerInputTextButton("00", modifier = buttonModifier)
          TimerInputTextButton("0", modifier = buttonModifier)
          // TODO: different color for backspace button
          TimerInputButton(
            onClick = { if (input.isNotEmpty()) input.removeFirst() },
            modifier = buttonModifier,
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Backspace,
              contentDescription = "Backspace",
              modifier = Modifier.padding(2.dp),
            )
          }
        }
      }
    }
  }

  val keyboardColumnsCount = 3
  val keyboardRowsCount = 4

  // TODO: spacing between layout elements + their alignments
  if (
    windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact ||
      windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
  ) {
    Row(
      modifier = modifier,
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
    ) {
      TimerDurationText()

      Spacer(modifier = Modifier.width(16.dp))

      TimerInputKeyboard(
        modifier = Modifier.padding(vertical = 16.dp),
        calculateButtonModifier = { space ->
          Modifier.requiredSize(
            minOf(
              (maxWidth - space * (keyboardColumnsCount - 1)) / keyboardColumnsCount,
              (maxHeight - space * (keyboardRowsCount - 1)) / keyboardRowsCount,
              96.dp,
            )
          )
        },
      )

      Spacer(modifier = Modifier.width(16.dp))

      TimerStartButton()
    }
  } else {
    Column(
      modifier = modifier,
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      TimerDurationText()

      Spacer(modifier = Modifier.height(16.dp))

      TimerInputKeyboard(
        calculateButtonModifier = { space ->
          Modifier.requiredSize(
            minOf(
              (maxWidth - space * (keyboardColumnsCount - 1)) / keyboardColumnsCount,
              (maxHeight - space * (keyboardRowsCount - 1) - 96.dp) / keyboardRowsCount,
              96.dp,
            )
          )
        }
      )

      Spacer(modifier = Modifier.height(16.dp))

      TimerStartButton()
    }
  }
}

@Composable
private fun TimerInputButtonText(text: String) {
  AutoSizeText(
    modifier = Modifier.padding(2.dp),
    text = text,
    alignment = Alignment.Center,
    maxTextSize = 32.sp,
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
