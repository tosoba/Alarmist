package com.trm.alarmist.feature.timer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trm.alarmist.core.ui.AutoSizeText
import com.trm.alarmist.core.ui.sideFloatingActionButtonTransitionSpec
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun TimerInput(modifier: Modifier = Modifier, onStartClick: (Duration) -> Unit) {
  val windowSizeClass = calculateWindowSizeClass()

  val input = remember { mutableStateListOf<Char>() }
  fun getInputAt(index: Int): Char = input.getOrNull(index) ?: '0'

  @Composable
  fun TimerDurationText() {
    Text(
      text =
        "${getInputAt(5)}${getInputAt(4)}h ${getInputAt(3)}${getInputAt(2)}m ${getInputAt(1)}${getInputAt(0)}s",
      style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Medium),
    )
  }

  @Composable
  fun TimerInputTextButton(
    text: String,
    size: Dp,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
  ) {
    Box(
      modifier =
        Modifier.inputTextButton(size, color) {
          var charIndex = 0
          while (input.size < 6 && charIndex < text.length) {
            if (text[charIndex] != '0' || input.isNotEmpty()) {
              input.add(0, text[charIndex++])
            } else {
              break
            }
          }
        },
      contentAlignment = Alignment.Center,
    ) {
      TimerInputButtonText(text)
    }
  }

  @Composable
  fun TimerStartButton() {
    AnimatedContent(
      targetState = input.any { it != '0' },
      transitionSpec =
        AnimatedContentTransitionScope<Boolean>::sideFloatingActionButtonTransitionSpec,
    ) {
      if (it) {
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
      } else {
        Spacer(modifier = Modifier.size(96.dp))
      }
    }
  }

  @Composable
  fun TimerInputKeyboard(
    space: Dp = 5.dp,
    modifier: Modifier = Modifier,
    inputButtonSize: BoxWithConstraintsScope.(Dp) -> Dp,
  ) {
    BoxWithConstraints(modifier = modifier) {
      val buttonSize = inputButtonSize(space)

      Column(
        verticalArrangement = Arrangement.spacedBy(space, alignment = Alignment.CenterVertically)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement =
            Arrangement.spacedBy(space, alignment = Alignment.CenterHorizontally),
        ) {
          TimerInputTextButton("1", size = buttonSize)
          TimerInputTextButton("2", size = buttonSize)
          TimerInputTextButton("3", size = buttonSize)
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement =
            Arrangement.spacedBy(space, alignment = Alignment.CenterHorizontally),
        ) {
          TimerInputTextButton("4", size = buttonSize)
          TimerInputTextButton("5", size = buttonSize)
          TimerInputTextButton("6", size = buttonSize)
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement =
            Arrangement.spacedBy(space, alignment = Alignment.CenterHorizontally),
        ) {
          TimerInputTextButton("7", size = buttonSize)
          TimerInputTextButton("8", size = buttonSize)
          TimerInputTextButton("9", size = buttonSize)
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement =
            Arrangement.spacedBy(space, alignment = Alignment.CenterHorizontally),
        ) {
          TimerInputTextButton("00", size = buttonSize)
          TimerInputTextButton("0", size = buttonSize)
          Box(
            modifier =
              Modifier.inputTextButton(
                size = buttonSize,
                color = MaterialTheme.colorScheme.errorContainer,
              ) {
                if (input.isNotEmpty()) input.removeFirst()
              },
            contentAlignment = Alignment.Center,
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

  if (
    windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact ||
      windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
  ) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
      Spacer(modifier = Modifier.width(16.dp))
      TimerDurationText()
      Spacer(modifier = Modifier.weight(1f))

      TimerInputKeyboard(
        modifier = Modifier.padding(vertical = 16.dp),
        inputButtonSize = { space ->
          minOf(
            (maxWidth - space * (keyboardColumnsCount - 1)) / keyboardColumnsCount,
            (maxHeight - space * (keyboardRowsCount - 1)) / keyboardRowsCount,
            96.dp,
          )
        },
      )

      Spacer(modifier = Modifier.weight(1f))
      TimerStartButton()
      Spacer(modifier = Modifier.width(16.dp))
    }
  } else {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
      Spacer(modifier = Modifier.height(16.dp))
      TimerDurationText()
      Spacer(modifier = Modifier.weight(1f))

      TimerInputKeyboard(
        inputButtonSize = { space ->
          minOf(
            (maxWidth - space * (keyboardColumnsCount - 1)) / keyboardColumnsCount,
            (maxHeight - space * (keyboardRowsCount - 1) - 96.dp) / keyboardRowsCount,
            96.dp,
          )
        }
      )

      Spacer(modifier = Modifier.weight(1f))
      TimerStartButton()
      Spacer(modifier = Modifier.height(16.dp))
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
private fun Modifier.inputTextButton(
  size: Dp,
  color: Color = MaterialTheme.colorScheme.primaryContainer,
  onClick: () -> Unit,
): Modifier = composed {
  val interactionSource = remember(::MutableInteractionSource)
  val isFocused by interactionSource.collectIsPressedAsState()
  val cornerRadius by
    animateDpAsState(
      targetValue = if (isFocused) 16.dp else size / 2,
      animationSpec =
        spring(visibilityThreshold = Dp.VisibilityThreshold, stiffness = Spring.StiffnessMediumLow),
    )

  Modifier.requiredSize(size)
    .clip(RoundedCornerShape(cornerRadius))
    .clickable(interactionSource = interactionSource, indication = ripple(), onClick = onClick)
    .background(color)
}
