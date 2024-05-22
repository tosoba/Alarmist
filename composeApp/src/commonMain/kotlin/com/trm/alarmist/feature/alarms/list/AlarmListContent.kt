package com.trm.alarmist.feature.alarms.list

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.create_alarm_using_button
import alarmist.composeapp.generated.resources.no_alarms_created
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.ui.AlarmListItem
import com.trm.alarmist.core.ui.EmptyPlaceholder
import com.trm.alarmist.core.ui.FloatingActionButtonSpacer
import com.trm.alarmist.core.ui.floatingActionButtonSpacerItem
import com.trm.alarmist.feature.alarm.AlarmPermissionStatusCard
import com.trm.alarmist.feature.alarm.isAlarmPermissionGranted
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AlarmListContent(component: AlarmListComponent, modifier: Modifier = Modifier) {
  val alarms by component.alarms.collectAsState()
  val groups by component.groups.collectAsState()

  AnimatedVisibility(
    visible = alarms.initialized,
    enter = fadeIn(),
    exit = fadeOut(),
    modifier = modifier,
  ) {
    Crossfade(targetState = alarms.data.isEmpty()) { alarmsEmpty ->
      if (alarmsEmpty) {
        Column(
          modifier = Modifier.fillMaxSize(),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          EmptyPlaceholder(
            imageVector = Icons.Default.AlarmAdd,
            primaryText = stringResource(Res.string.no_alarms_created),
            secondaryText = stringResource(Res.string.create_alarm_using_button),
            modifier = Modifier.fillMaxWidth().weight(1f),
          )

          FloatingActionButtonSpacer()
        }
      } else {
        val alarmPermissionGranted = isAlarmPermissionGranted()

        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        ) {
          if (!alarmPermissionGranted) {
            item {
              AlarmPermissionStatusCard(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
            }
          }

          items(alarms.data) {
            AlarmListItem(
              item = it,
              group = it.groupId?.let(groups::get),
              modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
              onItemClick = component::onAlarmClick,
              onToggleOnOff = component::onToggleAlarmOnOff,
            )
          }

          floatingActionButtonSpacerItem()
        }
      }
    }
  }
}
