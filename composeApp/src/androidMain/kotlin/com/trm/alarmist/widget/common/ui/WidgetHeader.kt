package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextDefaults
import com.trm.alarmist.R
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.widget.common.util.LocalIsPreviewProvider
import com.trm.alarmist.widget.common.util.largeFontSize
import com.trm.alarmist.widget.common.util.smallFontSize
import kotlinx.datetime.LocalDate

@Composable
internal fun WidgetHeader(onRefreshClick: Action, modifier: GlanceModifier = GlanceModifier) {
  Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
    Image(
      provider = ImageProvider(R.mipmap.ic_launcher_round),
      contentDescription = "Today alarms",
      modifier = GlanceModifier.size(32.dp),
    )

    Spacer(modifier = GlanceModifier.width(10.dp))

    Column(modifier = GlanceModifier.defaultWeight()) {
      Text(
        text = "Today",
        maxLines = 1,
        style = TextDefaults.defaultTextStyle.copy(fontSize = largeFontSize.sp),
      )
      Text(
        text = LocalDate.now().toString(),
        maxLines = 1,
        style = TextDefaults.defaultTextStyle.copy(fontSize = smallFontSize.sp),
      )
    }

    Spacer(modifier = GlanceModifier.width(10.dp))

    Image(
      provider = ImageProvider(R.drawable.refresh),
      contentDescription = "Refresh",
      modifier =
        GlanceModifier.cornerRadius(8.dp).run {
          if (LocalIsPreviewProvider.current) this else clickable(onRefreshClick)
        },
    )
  }
}
