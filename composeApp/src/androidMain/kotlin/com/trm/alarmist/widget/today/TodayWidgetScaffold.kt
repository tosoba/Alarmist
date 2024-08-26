package com.trm.alarmist.widget.today

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.actionSendBroadcast
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import com.trm.alarmist.R
import com.trm.alarmist.core.common.model.Initializable
import com.trm.alarmist.core.common.model.Initialized
import com.trm.alarmist.core.common.model.Uninitialized
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.feature.root.RootStartMode
import com.trm.alarmist.widget.common.ui.WidgetAlarmListContent
import com.trm.alarmist.widget.common.ui.WidgetAlarmListTextClock
import com.trm.alarmist.widget.common.ui.WidgetDimensions
import com.trm.alarmist.widget.common.ui.WidgetEmptyContent
import com.trm.alarmist.widget.common.ui.WidgetLayoutSize
import com.trm.alarmist.widget.common.ui.WidgetLoadingIndicator
import com.trm.alarmist.widget.common.ui.WidgetTitleBar
import com.trm.alarmist.widget.common.util.LocalWidgetLayoutSize
import com.trm.alarmist.widget.common.util.actionStartMainActivity
import com.trm.alarmist.widget.common.util.composableIfOrNull
import com.trm.alarmist.widget.common.util.emptyActionIfPreviewOrElse
import com.trm.alarmist.widget.common.util.stringResource
import com.trm.alarmist.widget.common.util.toggleAlarmOnOffOnDateIntent
import com.trm.alarmist.widget.common.util.updateWidgetIntent
import kotlinx.datetime.LocalDate

@Composable
internal fun TodayWidgetScaffold(
  id: GlanceId,
  state: Initializable<TodayWidgetState>,
  showTitleBar: Boolean = WidgetLayoutSize.showTitleBar(),
) {
  GlanceTheme {
    val context = LocalContext.current
    val widgetManager = remember(context) { GlanceAppWidgetManager(context) }

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
            iconColor = GlanceTheme.colors.primary,
            actions = {
              CircleIconButton(
                imageProvider = ImageProvider(R.drawable.refresh),
                contentDescription = stringResource(R.string.refresh),
                contentColor = GlanceTheme.colors.secondary,
                backgroundColor = null,
                onClick =
                  emptyActionIfPreviewOrElse {
                    actionSendBroadcast(
                      context.updateWidgetIntent<TodayWidgetReceiver>(
                        widgetManager.getAppWidgetId(id)
                      )
                    )
                  },
              )
            },
          ) {
            WidgetAlarmListTextClock(
              modifier =
                GlanceModifier.defaultWeight().run {
                  if (LocalWidgetLayoutSize.current != WidgetLayoutSize.Large) {
                    padding(start = 16.dp)
                  } else {
                    this
                  }
                }
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
      WidgetLoadingIndicator(modifier = GlanceModifier.fillMaxWidth().padding(vertical = 20.dp))
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
        WidgetAlarmListContent(
          alarms = state.data.alarms,
          getGroup = state.data.groups::get,
          onCheckedChangeAction = { item ->
            val now = LocalDate.now()
            actionSendBroadcast(
              if (today == now) {
                context.toggleAlarmOnOffOnDateIntent(item.id, today)
              } else {
                // if the user tries to toggle the alarm just after midnight and the latest widget
                // update was before midnight then update a widget
                context.updateWidgetIntent<TodayWidgetReceiver>(
                  GlanceAppWidgetManager(context).getAppWidgetId(id)
                )
              }
            )
          },
        )
      }
    }
  }
}
