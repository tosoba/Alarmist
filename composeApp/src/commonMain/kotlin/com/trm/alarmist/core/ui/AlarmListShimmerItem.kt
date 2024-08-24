package com.trm.alarmist.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.common.util.elevatedIf
import com.trm.alarmist.core.ui.theme.onOffCardColors
import kotlin.random.Random

@Composable
fun AlarmListShimmerItem(modifier: Modifier = Modifier, shape: Shape = CardDefaults.shape) {
  val isOn = remember(Random.Default::nextBoolean)

  Card(
    modifier = modifier,
    elevation = CardDefaults.elevatedIf(isOn),
    shape = shape,
    colors = CardDefaults.onOffCardColors(isOn),
  ) {
    Spacer(modifier = Modifier.height(16.dp))

    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Box(
        modifier =
          Modifier.width(64.dp)
            .height(
              with(LocalDensity.current) { MaterialTheme.typography.displayMedium.fontSize.toDp() }
            )
            .clip(RoundedCornerShape(8.dp))
            .shimmerEffect()
      )

      Spacer(modifier = Modifier.width(8.dp))

      Box(
        modifier =
          Modifier.width(52.dp).height(32.dp).clip(RoundedCornerShape(8.dp)).shimmerEffect()
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Box(
        modifier =
          Modifier.width(32.dp)
            .height(
              with(LocalDensity.current) { MaterialTheme.typography.bodyMedium.fontSize.toDp() }
            )
            .clip(RoundedCornerShape(4.dp))
            .shimmerEffect()
      )

      Spacer(modifier = Modifier.weight(1f))

      Box(
        modifier =
          Modifier.width(32.dp)
            .height(
              with(LocalDensity.current) { MaterialTheme.typography.bodyMedium.fontSize.toDp() }
            )
            .clip(RoundedCornerShape(4.dp))
            .shimmerEffect()
      )
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}
