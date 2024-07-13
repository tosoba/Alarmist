package com.trm.alarmist.widget.group

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
import androidx.glance.action.action
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionSendBroadcast
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.trm.alarmist.R
import com.trm.alarmist.core.common.model.Initializable
import com.trm.alarmist.core.common.model.Initialized
import com.trm.alarmist.core.common.model.Uninitialized
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.widget.common.model.WidgetAlarmListModel
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

class GroupWidget : GlanceAppWidget(), KoinComponent {
  private val repository: AlarmRepository by inject()

  override val sizeMode: SizeMode = SizeMode.Exact

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      val state = currentState<Preferences>()
      val groupId = state[GroupWidgetReceiver.groupIdKey]

      CompositionLocalProvider(LocalIsPreviewProvider provides false) {
        GlanceTheme {
          if (groupId == null) {
            // TODO: WidgetEmptyContent? with action to go to config
            Box(
              contentAlignment = Alignment.Center,
              modifier = GlanceModifier.background(GlanceTheme.colors.widgetBackground),
            ) {
              Text(
                text = "No group set",
                style = TextStyle(color = GlanceTheme.colors.onBackground),
              )
            }
          } else {
            val widgetState by
              produceState<Initializable<GroupWidgetState>>(Uninitialized, state) {
                value =
                  Initialized(
                    GroupWidgetState(
                      alarms = repository.getAlarmsInGroup(groupId).map(::WidgetAlarmListModel),
                      group = repository.getGroupById(groupId),
                    )
                  )
              }
            GroupWidgetScaffold(id = id, state = widgetState)
          }
        }
      }
    }
  }
}

private data class GroupWidgetState(
  val alarms: List<WidgetAlarmListModel>,
  val group: AlarmGroupModel,
)

@Composable
private fun GroupWidgetScaffold(id: GlanceId, state: Initializable<GroupWidgetState>) {
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
                    context.updateWidgetIntent<GroupWidgetReceiver>(
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
      GroupWidgetScaffoldContent(state = state)
    }
  }
}

@Composable
private fun GroupWidgetScaffoldContent(state: Initializable<GroupWidgetState>) {
  when (state) {
    is Uninitialized -> {
      WidgetLoadingIndicator(modifier = GlanceModifier.fillMaxWidth().padding(vertical = 20.dp))
    }
    is Initialized -> {
      if (state.data.alarms.isEmpty()) {
        val context = LocalContext.current
        WidgetEmptyContent(
          emptyText = context.getString(R.string.no_alarms_today),
          actionButtonText = context.getString(R.string.add_alarm),
          actionButtonIcon = null,
          actionButtonOnClick = deepLinkAction(context.addAlarmDeeplinkUri()),
        )
      } else {
        val action = action {} // TODO: toggle on/off (globally) action
        WidgetAlarmListContent(
          alarms = state.data.alarms,
          getGroup = { state.data.group },
          onCheckedChangeAction = { action },
        )
      }
    }
  }
}
