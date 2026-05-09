package com.trm.alarmist.app.widget.group

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.action.actionSendBroadcast
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import com.trm.alarmist.app.R
import com.trm.alarmist.feature.root.RootStartMode
import com.trm.alarmist.app.widget.common.ui.WidgetRefreshButton
import com.trm.alarmist.app.widget.common.ui.AlarmListWidgetPreview
import com.trm.alarmist.app.widget.common.ui.WidgetAlarmListContent
import com.trm.alarmist.app.widget.common.ui.WidgetAlarmListTextClock
import com.trm.alarmist.app.widget.common.ui.WidgetDimensions.widgetPadding
import com.trm.alarmist.app.widget.common.ui.WidgetEmptyContent
import com.trm.alarmist.app.widget.common.ui.WidgetLoadingIndicator
import com.trm.alarmist.app.widget.common.ui.WidgetPreviewCompositionLocalProvider
import com.trm.alarmist.app.widget.common.ui.WidgetTextStyles
import com.trm.alarmist.app.widget.common.ui.WidgetTitleBar
import com.trm.alarmist.app.widget.common.util.LocalAppWidgetIdProvider
import com.trm.alarmist.app.widget.common.util.LocalWidgetLayoutType
import com.trm.alarmist.app.widget.common.util.actionStartGroupWidgetConfigActivity
import com.trm.alarmist.app.widget.common.util.actionStartMainActivity
import com.trm.alarmist.app.widget.common.util.composableIfOrNull
import com.trm.alarmist.app.widget.common.util.emptyActionIfPreviewOrElse
import com.trm.alarmist.app.widget.common.util.stringResource
import com.trm.alarmist.app.widget.common.util.toggleAlarmOnOffIntent
import com.trm.alarmist.app.widget.common.util.updateWidgetIntent
import com.trm.alarmist.app.widget.common.util.widgetPreviewAlarmGroup
import com.trm.alarmist.app.widget.common.util.widgetPreviewAlarmList

@Composable
internal fun GroupWidgetScaffold(
  id: GlanceId,
  state: GroupWidgetState,
  showTitleBar: Boolean = LocalWidgetLayoutType.current.showTitleBar,
) {
  val context = LocalContext.current

  Scaffold(
    backgroundColor = GlanceTheme.colors.widgetBackground,
    modifier =
      GlanceModifier.padding(
        top = if (showTitleBar) 0.dp else widgetPadding,
        bottom = widgetPadding,
      ),
    titleBar =
      composableIfOrNull(condition = showTitleBar) {
        WidgetTitleBar(
          startIcon = null,
          iconColor = GlanceTheme.colors.primary,
          actions = {
            WidgetRefreshButton(
              onClick =
                emptyActionIfPreviewOrElse {
                  actionSendBroadcast(
                    context.updateWidgetIntent<GroupWidgetReceiver>(
                      LocalAppWidgetIdProvider.current.getAppWidgetId(id)
                    )
                  )
                }
            )
          },
        ) {
          Column(modifier = GlanceModifier.defaultWeight().padding(start = 16.dp)) {
            if (state is GroupWidgetState.Initialized) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                GroupIcon(color = state.group.color, iconSize = 24.dp)

                Spacer(GlanceModifier.width(8.dp))

                Text(
                  text = state.group.name,
                  style = WidgetTextStyles.largeHeaderText,
                  maxLines = 1,
                )
              }
            }

            WidgetAlarmListTextClock()
          }
        }
      },
  ) {
    GroupWidgetScaffoldContent(id = id, state = state)
  }
}

@Composable
private fun GroupWidgetScaffoldContent(id: GlanceId, state: GroupWidgetState) {
  val context = LocalContext.current

  when (state) {
    is GroupWidgetState.Uninitialized -> {
      WidgetLoadingIndicator(modifier = GlanceModifier.fillMaxSize().padding(vertical = 20.dp))
    }
    is GroupWidgetState.Initialized -> {
      if (state.alarms.isEmpty()) {
        WidgetEmptyContent(
          emptyText = stringResource(R.string.group_is_empty),
          actionButtonText = stringResource(R.string.add_alarm),
          actionButtonIcon = null,
          actionButtonOnClick =
            emptyActionIfPreviewOrElse { actionStartMainActivity(RootStartMode.AddAlarm) },
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
      WidgetEmptyContent(
        emptyText = stringResource(R.string.no_group_set),
        actionButtonText = stringResource(R.string.choose_group),
        actionButtonIcon = null,
        actionButtonOnClick =
          emptyActionIfPreviewOrElse {
            actionStartGroupWidgetConfigActivity(
              LocalAppWidgetIdProvider.current.getAppWidgetId(id)
            )
          },
      )
    }
  }
}

@Composable
private fun GroupIcon(color: Long, iconSize: Dp, modifier: GlanceModifier = GlanceModifier) {
  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    Image(
      provider = ImageProvider(R.drawable.folder),
      contentDescription = null,
      modifier = GlanceModifier.size(iconSize),
      colorFilter = ColorFilter.tint(ColorProvider(Color(color))),
    )
    Image(
      provider = ImageProvider(R.drawable.folder_open),
      contentDescription = null,
      modifier = GlanceModifier.size(iconSize),
    )
  }
}

@Suppress("unused")
@AlarmListWidgetPreview
@Composable
private fun GroupWidgetScaffoldNoGroupSetPreview() {
  WidgetPreviewCompositionLocalProvider {
    GroupWidgetScaffold(id = object : GlanceId {}, state = GroupWidgetState.NoGroupSet)
  }
}

@Suppress("unused")
@AlarmListWidgetPreview
@Composable
private fun GroupWidgetScaffoldLoadingPreview() {
  WidgetPreviewCompositionLocalProvider {
    GroupWidgetScaffold(id = object : GlanceId {}, state = GroupWidgetState.Uninitialized)
  }
}

@Suppress("unused")
@AlarmListWidgetPreview
@Composable
private fun GroupWidgetScaffoldEmptyPreview() {
  WidgetPreviewCompositionLocalProvider {
    GroupWidgetScaffold(
      id = object : GlanceId {},
      state = GroupWidgetState.Initialized(alarms = emptyList(), group = widgetPreviewAlarmGroup()),
    )
  }
}

@Suppress("unused")
@AlarmListWidgetPreview
@Composable
private fun GroupWidgetScaffoldNonEmptyPreview() {
  WidgetPreviewCompositionLocalProvider {
    GroupWidgetScaffold(
      id = object : GlanceId {},
      state =
        GroupWidgetState.Initialized(
          alarms = widgetPreviewAlarmList(1L),
          group = widgetPreviewAlarmGroup(),
        ),
    )
  }
}
