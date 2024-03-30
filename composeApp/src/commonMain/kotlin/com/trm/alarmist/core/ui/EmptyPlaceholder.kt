package com.trm.alarmist.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
  modifier: Modifier = Modifier
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
    Row(
      modifier = modifier,
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Icon(
        modifier = Modifier.fillMaxHeight(.5f).aspectRatio(1f),
        imageVector = imageVector,
        contentDescription = primaryText,
      )
      Spacer(Modifier.width(16.dp))
      Column(verticalArrangement = Arrangement.Center) {
        Text(
          text = primaryText,
          style = MaterialTheme.typography.headlineMedium,
          textAlign = TextAlign.Center,
        )
        Text(
          text = secondaryText,
          style = MaterialTheme.typography.bodyLarge,
          textAlign = TextAlign.Center,
        )
      }
    }
  } else {
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
      Text(
        text = primaryText,
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center,
      )
      Text(
        text = secondaryText,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
      )
    }
  }
}
