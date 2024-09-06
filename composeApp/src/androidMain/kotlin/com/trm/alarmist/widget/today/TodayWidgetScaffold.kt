package com.trm.alarmist.widget.today

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.action.actionSendBroadcast
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.material3.ColorProviders
import com.trm.alarmist.R
import com.trm.alarmist.core.common.model.Initializable
import com.trm.alarmist.core.common.model.Initialized
import com.trm.alarmist.core.common.model.Uninitialized
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.ui.theme.darkScheme
import com.trm.alarmist.core.ui.theme.lightScheme
import com.trm.alarmist.feature.root.RootStartMode
import com.trm.alarmist.widget.common.WidgetRefreshButton
import com.trm.alarmist.widget.common.ui.AlarmListWidgetPreview
import com.trm.alarmist.widget.common.ui.WidgetAlarmListContent
import com.trm.alarmist.widget.common.ui.WidgetAlarmListTextClock
import com.trm.alarmist.widget.common.ui.WidgetDimensions
import com.trm.alarmist.widget.common.ui.WidgetEmptyContent
import com.trm.alarmist.widget.common.ui.WidgetLoadingIndicator
import com.trm.alarmist.widget.common.ui.WidgetPreviewCompositionLocalProvider
import com.trm.alarmist.widget.common.ui.WidgetTitleBar
import com.trm.alarmist.widget.common.util.LocalAppWidgetIdProvider
import com.trm.alarmist.widget.common.util.LocalWidgetLayoutType
import com.trm.alarmist.widget.common.util.actionStartMainActivity
import com.trm.alarmist.widget.common.util.composableIfOrNull
import com.trm.alarmist.widget.common.util.emptyActionIfPreviewOrElse
import com.trm.alarmist.widget.common.util.stringResource
import com.trm.alarmist.widget.common.util.toggleAlarmOnOffOnDateIntent
import com.trm.alarmist.widget.common.util.updateWidgetIntent
import com.trm.alarmist.widget.common.util.widgetPreviewAlarmList
import kotlinx.datetime.LocalDate

@Composable
internal fun TodayWidgetScaffold(
  id: GlanceId,
  state: Initializable<TodayWidgetState>,
  showTitleBar: Boolean = LocalWidgetLayoutType.current.showTitleBar,
) {
  GlanceTheme(colors = ColorProviders(light = lightScheme, dark = darkScheme)) {
    val context = LocalContext.current

    Scaffold(
      backgroundColor = GlanceTheme.colors.widgetBackground,
      modifier =
        GlanceModifier.padding(
          top = if (showTitleBar) 0.dp else WidgetDimensions.widgetPadding,
          bottom = WidgetDimensions.widgetPadding,
        ),
      titleBar =
        composableIfOrNull(condition = showTitleBar) {
          WidgetTitleBar(
            actions = {
              WidgetRefreshButton(
                onClick =
                  emptyActionIfPreviewOrElse {
                    actionSendBroadcast(
                      context.updateWidgetIntent<TodayWidgetReceiver>(
                        LocalAppWidgetIdProvider.current.getAppWidgetId(id)
                      )
                    )
                  }
              )
            }
          ) {
            WidgetAlarmListTextClock(
              modifier = GlanceModifier.defaultWeight().padding(start = 16.dp)
            )
          }
        },
    ) {
      TodayWidgetScaffoldContent(id = id, state = state)
    }
  }
}

@Composable
private fun TodayWidgetScaffoldContent(id: GlanceId, state: Initializable<TodayWidgetState>) {
  when (state) {
    is Uninitialized -> {
      WidgetLoadingIndicator(modifier = GlanceModifier.fillMaxSize().padding(vertical = 20.dp))
    }
    is Initialized -> {
      val context = LocalContext.current
      if (state.data.alarms.isEmpty()) {
        WidgetEmptyContent(
          emptyText = stringResource(R.string.no_alarms_today),
          actionButtonText = stringResource(R.string.add_alarm),
          actionButtonIcon = null,
          actionButtonOnClick =
            emptyActionIfPreviewOrElse { actionStartMainActivity(RootStartMode.AddAlarm) },
        )
      } else {
        val today = LocalDate.now()
        val appWidgetIdProvider = LocalAppWidgetIdProvider.current

        WidgetAlarmListContent(
          alarms = state.data.alarms,
          getGroup = state.data.groups::get,
          onCheckedChangeAction = { item ->
            val now = LocalDate.now()
            actionSendBroadcast(
              if (today == now) {
                context.toggleAlarmOnOffOnDateIntent(item.id, today)
              } else {
                // if the user tries to toggle the alarm just after midnight and the latest
                // widget update was before midnight then update a widget
                context.updateWidgetIntent<TodayWidgetReceiver>(
                  appWidgetIdProvider.getAppWidgetId(id)
                )
              }
            )
          },
        )
      }
    }
  }
}

@Suppress("unused")
@AlarmListWidgetPreview
@Composable
private fun TodayWidgetScaffoldLoadingPreview() {
  WidgetPreviewCompositionLocalProvider {
    TodayWidgetScaffold(id = object : GlanceId {}, state = Uninitialized)
  }
}

@Suppress("unused")
@AlarmListWidgetPreview
@Composable
private fun TodayWidgetScaffoldEmptyPreview() {
  WidgetPreviewCompositionLocalProvider {
    TodayWidgetScaffold(
      id = object : GlanceId {},
      state = Initialized(TodayWidgetState(alarms = emptyList(), groups = emptyMap())),
    )
  }
}

@Suppress("unused")
@AlarmListWidgetPreview
@Composable
private fun TodayWidgetScaffoldNonEmptyPreview() {
  WidgetPreviewCompositionLocalProvider {
    TodayWidgetScaffold(
      id = object : GlanceId {},
      state = Initialized(TodayWidgetState(alarms = widgetPreviewAlarmList(), groups = emptyMap())),
    )
  }
}
