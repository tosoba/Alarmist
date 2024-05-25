package com.trm.alarmist.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun EmptyPlaceholder(
  imageVector: ImageVector,
  primaryText: String,
  secondaryText: String,
  modifier: Modifier = Modifier,
) {
  val windowSizeClass = calculateWindowSizeClass()
  val widthSizeClass = windowSizeClass.widthSizeClass
  val heightSizeClass = windowSizeClass.heightSizeClass

  if (
    (widthSizeClass != WindowWidthSizeClass.Compact &&
      heightSizeClass == WindowHeightSizeClass.Compact) ||
      (widthSizeClass == WindowWidthSizeClass.Expanded &&
        heightSizeClass != WindowHeightSizeClass.Expanded)
  ) {
    EmptyPlaceholderHorizontal(
      imageVector = imageVector,
      primaryText = primaryText,
      secondaryText = secondaryText,
      modifier = modifier,
    )
  } else {
    EmptyPlaceholderVertical(
      imageVector = imageVector,
      primaryText = primaryText,
      secondaryText = secondaryText,
      modifier = modifier,
    )
  }
}

@Composable
private fun EmptyPlaceholderHorizontal(
  imageVector: ImageVector,
  primaryText: String,
  secondaryText: String,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
  ) {
    Icon(
      modifier = Modifier.fillMaxHeight(.5f).aspectRatio(1f),
      imageVector = imageVector,
      contentDescription = primaryText,
    )

    Spacer(Modifier.width(32.dp))

    Column(verticalArrangement = Arrangement.Center) {
      EmptyPlaceholderTexts(primaryText = primaryText, secondaryText = secondaryText)
    }
  }
}

@Composable
private fun EmptyPlaceholderVertical(
  imageVector: ImageVector,
  primaryText: String,
  secondaryText: String,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Icon(
      modifier = Modifier.fillMaxWidth(.35f).aspectRatio(1f),
      imageVector = imageVector,
      contentDescription = primaryText,
    )

    Spacer(Modifier.height(16.dp))

    EmptyPlaceholderTexts(primaryText = primaryText, secondaryText = secondaryText)
  }
}

@Composable
private fun ColumnScope.EmptyPlaceholderTexts(primaryText: String, secondaryText: String) {
  Text(
    text = primaryText,
    style = MaterialTheme.typography.headlineMedium,
    textAlign = TextAlign.Center,
  )

  Spacer(Modifier.height(4.dp))

  Text(
    text = secondaryText,
    style = MaterialTheme.typography.bodyLarge,
    textAlign = TextAlign.Center,
  )
}
