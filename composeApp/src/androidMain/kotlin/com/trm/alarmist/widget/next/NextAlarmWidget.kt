package com.trm.alarmist.widget.next

import android.content.Context
import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.Action
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.Switch
import androidx.glance.appwidget.action.actionSendBroadcast
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextDefaults
import com.trm.alarmist.core.common.model.Initializable
import com.trm.alarmist.core.common.util.amPmString
import com.trm.alarmist.core.common.util.formatCountdown
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.common.util.toFormattedString
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.usecase.GetNextAlarmUseCase
import com.trm.alarmist.core.ui.Countdown
import com.trm.alarmist.widget.common.ui.WidgetActionButtonContent
import com.trm.alarmist.widget.common.ui.WidgetHeader
import com.trm.alarmist.widget.common.ui.WidgetLoadingIndicator
import com.trm.alarmist.widget.common.ui.WidgetOuterColumn
import com.trm.alarmist.widget.common.util.LocalIsPreviewProvider
import com.trm.alarmist.widget.common.util.mediumFontSize
import com.trm.alarmist.widget.common.util.turnAlarmOffIntent
import com.trm.alarmist.widget.common.util.updateWidgetIntent
import com.trm.alarmist.widget.common.util.widgetBackgroundCornerRadius
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NextAlarmWidget : GlanceAppWidget(), KoinComponent {
  private val getNextAlarmUseCase: GetNextAlarmUseCase by inject()

  override val sizeMode: SizeMode = SizeMode.Exact

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      val state = currentState<Preferences>()
      val nextAlarm by
        produceState<Initializable<AlarmListModel?>>(Initializable(null), state) {
          value = Initializable(getNextAlarmUseCase(), true)
        }
      CompositionLocalProvider(LocalIsPreviewProvider provides false) {
        NextAlarmWidgetContent(id = id, alarm = nextAlarm)
      }
    }
  }
}

@Composable
private fun NextAlarmWidgetContent(id: GlanceId, alarm: Initializable<AlarmListModel?>) {
  GlanceTheme {
    WidgetOuterColumn(modifier = GlanceModifier.fillMaxSize().widgetBackgroundCornerRadius()) {
      val context = LocalContext.current
      val widgetManager = remember(id) { GlanceAppWidgetManager(context) }

      WidgetHeader(
        text = "Next",
        onRefreshClick =
          actionSendBroadcast(
            context.updateWidgetIntent<NextAlarmWidgetReceiver>(widgetManager.getAppWidgetId(id))
          ),
        modifier = GlanceModifier.fillMaxWidth(),
      )

      Spacer(GlanceModifier.height(4.dp))

      when {
        !alarm.initialized -> {
          WidgetLoadingIndicator(
            modifier = GlanceModifier.fillMaxWidth().defaultWeight().padding(vertical = 20.dp)
          )
        }
        alarm.data == null -> {
          WidgetActionButtonContent(
            infoText = "No scheduled alarms",
            buttonText = "Schedule an alarm",
            modifier = GlanceModifier.fillMaxWidth().defaultWeight().padding(vertical = 20.dp),
          )
        }
        else -> {
          NextAlarm(
            alarm = alarm.data,
            modifier = GlanceModifier.fillMaxWidth(),
            onTurnAlarmOff = actionSendBroadcast(context.turnAlarmOffIntent(alarm.data.id)),
          )
        }
      }
    }
  }
}

@Composable
private fun NextAlarm(
  alarm: AlarmListModel,
  onTurnAlarmOff: Action,
  modifier: GlanceModifier = GlanceModifier,
  is24HourFormat: @Composable () -> Boolean = { DateFormat.is24HourFormat(LocalContext.current) },
) {
  Column(modifier) {
    alarm.name?.let { Text(text = it, maxLines = 1) }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = GlanceModifier.fillMaxWidth()) {
      Text(
        text =
          """${alarm.nextFireAtTime.toFormattedString(is24HourFormat)} ${alarm.nextFireAtTime.amPmString(is24HourFormat)}"""
            .trim(),
        maxLines = 1,
        style =
          TextDefaults.defaultTextStyle.copy(
            fontSize = mediumFontSize.sp,
            fontWeight = FontWeight.Medium,
          ),
      )

      Spacer(modifier = GlanceModifier.defaultWeight())

      Switch(checked = true, onCheckedChange = onTurnAlarmOff)
    }

    alarm.fireOnDateTime?.let {
      Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        val now = LocalDateTime.now()

        Text(
          text =
            if (
              alarm.scheduledOnClosestDate == now.date ||
                alarm.scheduledOnDaysOfWeek.contains(now.dayOfWeek)
            ) {
              "Custom scheduled"
            } else {
              "One time"
            }
        )

        Spacer(modifier = GlanceModifier.defaultWeight())

        Countdown(
          targetEpochMillis = it.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        ) { remainingMillis ->
          if (remainingMillis >= 0L) {
            Text(
              text = remainingMillis.toDuration(DurationUnit.MILLISECONDS).formatCountdown(),
              maxLines = 1,
            )
          }
        }
      }
    }
  }
}
