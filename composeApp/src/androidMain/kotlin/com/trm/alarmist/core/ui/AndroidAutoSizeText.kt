package com.trm.alarmist.core.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Preview(widthDp = 200, heightDp = 100)
@Preview(widthDp = 200, heightDp = 30)
@Preview(widthDp = 60, heightDp = 30)
@Composable
fun PreviewAutoSizeTextWithMaxLinesSetToIntMaxValue() {
  MaterialTheme {
    Surface(color = MaterialTheme.colorScheme.primary) {
      AutoSizeText(
        text = "This is a bunch of text that will be auto sized",
        modifier = Modifier.fillMaxSize(),
        alignment = Alignment.CenterStart,
        style = MaterialTheme.typography.bodyMedium,
      )
    }
  }
}

@Preview(widthDp = 200, heightDp = 100)
@Preview(widthDp = 200, heightDp = 30)
@Preview(widthDp = 60, heightDp = 30)
@Composable
fun PreviewAutoSizeTextWithMinSizeSetTo14() {
  MaterialTheme {
    Surface(color = MaterialTheme.colorScheme.secondary) {
      AutoSizeText(
        text = "This is a bunch of text that will be auto sized",
        modifier = Modifier.fillMaxSize(),
        minTextSize = 14.sp,
        alignment = Alignment.CenterStart,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.bodyMedium,
      )
    }
  }
}

@Preview(widthDp = 200, heightDp = 100)
@Preview(widthDp = 200, heightDp = 30)
@Preview(widthDp = 60, heightDp = 30)
@Composable
fun PreviewAutoSizeTextWithMaxLinesSetToOne() {
  MaterialTheme {
    Surface(color = MaterialTheme.colorScheme.tertiary) {
      AutoSizeText(
        text = "This is a bunch of text that will be auto sized",
        modifier = Modifier.fillMaxSize(),
        alignment = Alignment.Center,
        maxLines = 1,
        style = MaterialTheme.typography.bodyMedium,
      )
    }
  }
}

@Preview(widthDp = 100, heightDp = 50)
@Preview(widthDp = 50, heightDp = 100)
@Composable
fun PreviewAutoSizeTextWithMCharacter() {
  MaterialTheme {
    Surface(color = MaterialTheme.colorScheme.error) {
      AutoSizeText(
        text = "m",
        modifier = Modifier.fillMaxSize(),
        alignment = Alignment.Center,
        style = MaterialTheme.typography.bodyMedium,
        lineSpacingRatio = 1F,
      )
    }
  }
}

@Preview(widthDp = 100, heightDp = 50)
@Preview(widthDp = 50, heightDp = 100)
@Composable
fun PreviewAutoSizeTextWithYCharacter() {
  MaterialTheme {
    Surface(color = MaterialTheme.colorScheme.error) {
      AutoSizeText(
        text = "y",
        modifier = Modifier.fillMaxSize(),
        alignment = Alignment.Center,
        style =
          MaterialTheme.typography.bodyMedium.copy(
            lineHeightStyle =
              LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Center,
                trim = LineHeightStyle.Trim.Both,
              )
          ),
      )
    }
  }
}
