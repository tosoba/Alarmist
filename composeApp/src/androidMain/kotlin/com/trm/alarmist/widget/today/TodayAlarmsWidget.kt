package com.trm.alarmist.widget.today

import android.content.Context
import android.text.format.DateFormat
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.Switch
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextDefaults
import com.trm.alarmist.core.common.model.Initializable
import com.trm.alarmist.core.common.util.amPmString
import com.trm.alarmist.core.common.util.toFormattedString
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.usecase.GetAlarmsScheduledTodayUseCase
import com.trm.alarmist.widget.common.WidgetHeader
import com.trm.alarmist.widget.common.WidgetOuterColumn
import com.trm.alarmist.widget.common.util.LocalIsPreviewProvider
import com.trm.alarmist.widget.common.util.mediumFontSize
import com.trm.alarmist.widget.common.util.widgetBackgroundCornerRadius
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TodayAlarmsWidget : GlanceAppWidget(), KoinComponent {
  private val getAlarmsScheduledTodayUseCase: GetAlarmsScheduledTodayUseCase by inject()

  override val sizeMode: SizeMode = SizeMode.Exact

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      val alarms by
        produceState(Initializable(emptyList())) {
          value = Initializable(getAlarmsScheduledTodayUseCase(), true)
        }
      CompositionLocalProvider(LocalIsPreviewProvider provides true) {
        GlanceTheme {
          WidgetOuterColumn(
            modifier = GlanceModifier.fillMaxSize().widgetBackgroundCornerRadius()
          ) {
            WidgetHeader(modifier = GlanceModifier.fillMaxWidth())

            when {
              !alarms.initialized -> {
                Box(
                  modifier =
                    GlanceModifier.defaultWeight().padding(horizontal = 10.dp, vertical = 20.dp),
                  contentAlignment = Alignment.Center,
                ) {
                  CircularProgressIndicator()
                }
              }
              alarms.data.isEmpty() -> {
                Column(
                  modifier =
                    GlanceModifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 20.dp),
                  horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                  Text(
                    text = "No scheduled alarms",
                    maxLines = 1,
                    style = TextDefaults.defaultTextStyle.copy(fontSize = mediumFontSize.sp),
                  )

                  Spacer(modifier = GlanceModifier.height(10.dp))

                  Button(
                    text = "Schedule an alarm",
                    maxLines = 1,
                    onClick = {
                      // TODO: start activity action
                    },
                  )
                }
              }
              else -> {
                LazyColumn(modifier = GlanceModifier.defaultWeight().padding(vertical = 10.dp)) {
                  items(alarms.data, itemId = AlarmListModel::id) {
                    Column {
                      // TODO: label if exists
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = GlanceModifier.fillMaxWidth(),
                      ) {
                        Text(
                          text =
                            """${
                              it.nextFireAtTime.toFormattedString {
                                DateFormat.is24HourFormat(LocalContext.current)
                              }
                            } ${
                              it.nextFireAtTime.amPmString {
                                DateFormat.is24HourFormat(LocalContext.current)
                              }
                            }"""
                              .trim(),
                          maxLines = 1,
                          style = TextDefaults.defaultTextStyle.copy(fontSize = mediumFontSize.sp),
                        )

                        Spacer(modifier = GlanceModifier.defaultWeight())

                        Switch(
                          checked = true,
                          onCheckedChange = {
                            // TODO: send broadcast to turn off alarm
                          },
                        )
                      }
                      // TODO: countdown
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
