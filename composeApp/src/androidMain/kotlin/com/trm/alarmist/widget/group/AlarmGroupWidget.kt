package com.trm.alarmist.widget.group

import android.content.Context
import androidx.compose.runtime.CompositionLocalProvider
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.text.Text
import com.trm.alarmist.widget.common.util.LocalIsPreviewProvider

class AlarmGroupWidget : GlanceAppWidget() {
  override val sizeMode: SizeMode = SizeMode.Exact

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      val state = currentState<Preferences>()
      CompositionLocalProvider(LocalIsPreviewProvider provides false) {
        GlanceTheme { Box(contentAlignment = Alignment.Center) { Text(text = "Group widget") } }
      }
    }
  }
}
