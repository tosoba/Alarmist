package com.trm.alarmist.widget.today

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionSendBroadcast
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import com.trm.alarmist.R
import com.trm.alarmist.core.common.model.Initializable
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.UpcomingAlarmListModel
import com.trm.alarmist.core.domain.usecase.GetTodayAlarmsUseCase
import com.trm.alarmist.widget.common.ui.WidgetAlarmListContent
import com.trm.alarmist.widget.common.ui.WidgetAlarmListTextClock
import com.trm.alarmist.widget.common.ui.WidgetDimensions.widgetPadding
import com.trm.alarmist.widget.common.ui.WidgetEmptyContent
import com.trm.alarmist.widget.common.ui.WidgetLayoutSize
import com.trm.alarmist.widget.common.ui.WidgetLayoutSize.Companion.showTitleBar
import com.trm.alarmist.widget.common.ui.WidgetLoadingIndicator
import com.trm.alarmist.widget.common.ui.WidgetTitleBar
import com.trm.alarmist.widget.common.util.LocalIsPreviewProvider
import com.trm.alarmist.widget.common.util.addAlarmDeeplinkUri
import com.trm.alarmist.widget.common.util.composableIfOrNull
import com.trm.alarmist.widget.common.util.deepLinkAction
import com.trm.alarmist.widget.common.util.stringResource
import com.trm.alarmist.widget.common.util.updateWidgetIntent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TodayWidget : GlanceAppWidget(), KoinComponent {
  private val getTodayAlarmsUseCase: GetTodayAlarmsUseCase by inject()
  private val repository: AlarmRepository by inject()

  override val sizeMode: SizeMode = SizeMode.Exact

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      val state = currentState<Preferences>()
      val widgetState by
        produceState(Initializable(TodayWidgetState(emptyList(), emptyMap())), state) {
          value =
            Initializable(
              TodayWidgetState(
                alarms = getTodayAlarmsUseCase(),
                groups = repository.getAllAlarmGroups().associateBy(AlarmGroupModel::id),
              ),
              true,
            )
        }
      CompositionLocalProvider(LocalIsPreviewProvider provides false) {
        TodayWidgetScaffold(id = id, state = widgetState)
      }
    }
  }
}

private data class TodayWidgetState(
  val alarms: List<UpcomingAlarmListModel>,
  val groups: Map<Long, AlarmGroupModel>,
)

@Composable
private fun TodayWidgetScaffold(id: GlanceId, state: Initializable<TodayWidgetState>) {
  GlanceTheme {
    val context = LocalContext.current
    val widgetManager = remember(id) { GlanceAppWidgetManager(context) }

    Scaffold(
      backgroundColor = GlanceTheme.colors.widgetBackground,
      modifier =
        GlanceModifier.padding(
          top = if (showTitleBar()) 0.dp else widgetPadding,
          bottom = widgetPadding,
        ),
      titleBar =
        composableIfOrNull(condition = showTitleBar()) {
          val widgetLayoutSize = WidgetLayoutSize.fromLocalSize()

          WidgetTitleBar(
            // TODO: either app icon or icon representing today
            startIcon =
              if (widgetLayoutSize == WidgetLayoutSize.Large) {
                ImageProvider(R.mipmap.ic_launcher_round)
              } else {
                null
              },
            iconColor = GlanceTheme.colors.primary,
            actions = {
              CircleIconButton(
                imageProvider = ImageProvider(R.drawable.refresh),
                contentDescription = stringResource(R.string.refresh),
                contentColor = GlanceTheme.colors.secondary,
                backgroundColor = null,
                onClick =
                  actionSendBroadcast(
                    context.updateWidgetIntent<TodayWidgetReceiver>(
                      widgetManager.getAppWidgetId(id)
                    )
                  ),
              )
            },
          ) {
            WidgetAlarmListTextClock(widgetLayoutSize)
          }
        },
    ) {
      TodayWidgetScaffoldContent(state = state)
    }
  }
}

@Composable
private fun TodayWidgetScaffoldContent(state: Initializable<TodayWidgetState>) {
  when {
    !state.initialized -> {
      WidgetLoadingIndicator(modifier = GlanceModifier.fillMaxWidth().padding(vertical = 20.dp))
    }
    state.data.alarms.isEmpty() -> {
      val context = LocalContext.current
      WidgetEmptyContent(
        emptyText = context.getString(R.string.no_alarms_today),
        actionButtonText = context.getString(R.string.add_alarm),
        actionButtonIcon = null,
        actionButtonOnClick = deepLinkAction(context.addAlarmDeeplinkUri()),
      )
    }
    else -> {
      WidgetAlarmListContent(alarms = state.data.alarms, getGroup = state.data.groups::get)
    }
  }
}
