package com.trm.alarmist.widget.clock

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.CompositionLocalProvider
import androidx.glance.GlanceId
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.unit.ColorProvider
import com.trm.alarmist.core.ui.theme.colorScheme
import com.trm.alarmist.widget.common.ui.WidgetLayoutType
import com.trm.alarmist.widget.common.ui.WidgetTheme
import com.trm.alarmist.widget.common.util.LocalWidgetLayoutType
import com.trm.alarmist.widget.common.util.LocalWidgetMode
import com.trm.alarmist.widget.common.util.WidgetMode
import com.trm.alarmist.widget.common.util.clockWidgetPreviewAlarm

internal class ClockWidgetPinPreview : GlanceAppWidget() {
  override val sizeMode: SizeMode = SizeMode.Exact

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      WidgetTheme {
        CompositionLocalProvider(
          LocalWidgetMode provides WidgetMode.NORMAL_PREVIEW,
          LocalWidgetLayoutType provides WidgetLayoutType.Medium(showTitleBar = true),
        ) {
          ClockWidgetContent(
            alarm = clockWidgetPreviewAlarm(),
            textColorProvider =
              ColorProvider(
                LocalContext.current
                  .colorScheme(
                    darkTheme =
                      (LocalContext.current.resources.configuration.uiMode and
                        Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
                  )
                  .onPrimaryContainer
              ),
          )
        }
      }
    }
  }
}
