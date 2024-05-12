package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceModifier
import androidx.glance.action.Action
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.text.Text
import androidx.glance.text.TextDefaults
import com.trm.alarmist.widget.common.util.mediumFontSize

@Composable
internal fun WidgetActionButtonContent(
  infoText: String,
  buttonText: String,
  // onClick: Action, //TODO:
  modifier: GlanceModifier = GlanceModifier,
) {
  Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = infoText,
      maxLines = 1,
      style = TextDefaults.defaultTextStyle.copy(fontSize = mediumFontSize.sp),
    )

    Spacer(modifier = GlanceModifier.height(10.dp))

    Button(text = buttonText, maxLines = 1, onClick = {})
  }
}
