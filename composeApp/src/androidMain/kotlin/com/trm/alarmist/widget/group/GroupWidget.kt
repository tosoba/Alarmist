package com.trm.alarmist.widget.group

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
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
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import com.trm.alarmist.R
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.feature.root.RootStartMode
import com.trm.alarmist.widget.common.model.WidgetAlarmListModel
import com.trm.alarmist.widget.common.ui.WidgetAlarmListContent
import com.trm.alarmist.widget.common.ui.WidgetAlarmListTextClock
import com.trm.alarmist.widget.common.ui.WidgetDimensions.widgetPadding
import com.trm.alarmist.widget.common.ui.WidgetEmptyContent
import com.trm.alarmist.widget.common.ui.WidgetLayoutSize
import com.trm.alarmist.widget.common.ui.WidgetLayoutSize.Companion.showTitleBar
import com.trm.alarmist.widget.common.ui.WidgetLoadingIndicator
import com.trm.alarmist.widget.common.ui.WidgetTextStyles
import com.trm.alarmist.widget.common.ui.WidgetTitleBar
import com.trm.alarmist.widget.common.util.LocalIsPreviewProvider
import com.trm.alarmist.widget.common.util.actionStartGroupWidgetConfigActivity
import com.trm.alarmist.widget.common.util.actionStartMainActivity
import com.trm.alarmist.widget.common.util.composableIfOrNull
import com.trm.alarmist.widget.common.util.stringResource
import com.trm.alarmist.widget.common.util.toggleAlarmOnOffIntent
import com.trm.alarmist.widget.common.util.updateWidgetIntent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GroupWidget : GlanceAppWidget(), KoinComponent {
  private val repository: AlarmRepository by inject()

  override val sizeMode: SizeMode = SizeMode.Exact

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      val state = currentState<Preferences>()
      val widgetState by
        produceState<GroupWidgetState>(GroupWidgetState.Uninitialized, state) {
          value =
            state[GroupWidgetReceiver.groupIdKey]?.let {
              GroupWidgetState.Initialized(
                alarms = repository.getAlarmsInGroup(it).map(::WidgetAlarmListModel),
                group = repository.getGroupById(it),
              )
            } ?: GroupWidgetState.NoGroupSet
        }

      CompositionLocalProvider(LocalIsPreviewProvider provides false) {
        GroupWidgetScaffold(id = id, state = widgetState)
      }
    }
  }
}

private sealed interface GroupWidgetState {
  data object NoGroupSet : GroupWidgetState

  data object Uninitialized : GroupWidgetState

  data class Initialized(val alarms: List<WidgetAlarmListModel>, val group: AlarmGroupModel) :
    GroupWidgetState
}

@Composable
private fun GroupWidgetScaffold(id: GlanceId, state: GroupWidgetState) {
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
            startIcon = null,
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
            Column(
              modifier =
                GlanceModifier.defaultWeight().run {
                  if (widgetLayoutSize != WidgetLayoutSize.Large) padding(start = 16.dp) else this
                }
            ) {
              if (state is GroupWidgetState.Initialized) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  GroupIcon(color = state.group.color, size = 24.dp)

                  Spacer(GlanceModifier.width(8.dp))

                  Text(
                    text = state.group.name,
                    style = WidgetTextStyles.largeHeaderText,
                    maxLines = 1,
                  )
                }
              }

              WidgetAlarmListTextClock(widgetLayoutSize)
            }
          }
        },
    ) {
      GroupWidgetScaffoldContent(id = id, state = state)
    }
  }
}

@Composable
private fun GroupWidgetScaffoldContent(id: GlanceId, state: GroupWidgetState) {
  val context = LocalContext.current

  when (state) {
    is GroupWidgetState.Uninitialized -> {
      WidgetLoadingIndicator(modifier = GlanceModifier.fillMaxWidth().padding(vertical = 20.dp))
    }
    is GroupWidgetState.Initialized -> {
      if (state.alarms.isEmpty()) {
        WidgetEmptyContent(
          emptyText = context.getString(R.string.group_is_empty),
          actionButtonText = context.getString(R.string.add_alarm),
          actionButtonIcon = null,
          actionButtonOnClick = actionStartMainActivity(RootStartMode.AddAlarm),
        )
      } else {
        WidgetAlarmListContent(
          alarms = state.alarms,
          getGroup = { null },
          onCheckedChangeAction = { actionSendBroadcast(context.toggleAlarmOnOffIntent(it.id)) },
        )
      }
    }
    GroupWidgetState.NoGroupSet -> {
      val widgetManager = remember { GlanceAppWidgetManager(context) }
      WidgetEmptyContent(
        emptyText = context.getString(R.string.no_group_set),
        actionButtonText = context.getString(R.string.choose_group),
        actionButtonIcon = null,
        actionButtonOnClick = actionStartGroupWidgetConfigActivity(widgetManager.getAppWidgetId(id)),
      )
    }
  }
}

@Composable
private fun GroupIcon(color: Long, size: Dp, modifier: GlanceModifier = GlanceModifier) {
  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    Image(
      provider = ImageProvider(R.drawable.folder),
      contentDescription = null,
      modifier = GlanceModifier.size(size),
      colorFilter = ColorFilter.tint(ColorProvider(Color(color))),
    )
    Image(
      provider = ImageProvider(R.drawable.folder_open),
      contentDescription = null,
      modifier = GlanceModifier.size(size),
    )
  }
}
