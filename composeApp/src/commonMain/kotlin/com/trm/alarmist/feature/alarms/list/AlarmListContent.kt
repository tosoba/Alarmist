package com.trm.alarmist.feature.alarms.list

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.create_alarm_using_button
import alarmist.composeapp.generated.resources.no_alarms_created
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.system.permission.isPostNotificationPermissionGranted
import com.trm.alarmist.core.ui.AlarmListItem
import com.trm.alarmist.core.ui.AlarmListShimmerItem
import com.trm.alarmist.core.ui.EmptyPlaceholder
import com.trm.alarmist.core.ui.FloatingActionButtonSpacer
import com.trm.alarmist.core.ui.floatingActionButtonSpacerItem
import com.trm.alarmist.feature.alarm.AlarmPermissionStatusCard
import org.jetbrains.compose.resources.stringResource

@Composable
fun AlarmListContent(component: AlarmListComponent, modifier: Modifier = Modifier) {
  val alarms by component.alarms.collectAsState()
  val groups by component.groups.collectAsState()

  Crossfade(targetState = alarms.initialized && alarms.data.isEmpty(), modifier = modifier) {
    showEmptyPlaceholder ->
    if (showEmptyPlaceholder) {
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
      val alarmPermissionGranted = isPostNotificationPermissionGranted()

      LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(minSize = 300.dp),
        contentPadding = PaddingValues(8.dp),
        userScrollEnabled = alarms.initialized,
      ) {
        if (!alarms.initialized) {
          items(25) {
            AlarmListShimmerItem(
              modifier = Modifier.fillMaxWidth().padding(8.dp).alpha(.5f).animateItem()
            )
          }
        } else if (!alarmPermissionGranted) {
          item {
            AlarmPermissionStatusCard(
              modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).animateItem()
            )
          }
        }

        items(alarms.data) {
          AlarmListItem(
            item = it,
            group = it.groupId?.let(groups::get),
            modifier = Modifier.fillMaxWidth().padding(8.dp).animateItem(),
            onItemClick = component::onAlarmClick,
            onToggleOnOff = component::onToggleAlarmOnOff,
          )
        }

        floatingActionButtonSpacerItem()
      }
    }
  }
}
