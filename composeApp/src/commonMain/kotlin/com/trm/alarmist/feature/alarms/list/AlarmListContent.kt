package com.trm.alarmist.feature.alarms.list

import androidx.compose.animation.Crossfade
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.ui.AlarmListItem
import com.trm.alarmist.core.ui.EmptyPlaceholder
import com.trm.alarmist.core.ui.floatingActionButtonSpacerItem

@Composable
fun AlarmListContent(modifier: Modifier = Modifier, component: AlarmListComponent) {
  val alarms by component.alarms.collectAsState()
  val groups by component.groups.collectAsState()

  Crossfade(targetState = alarms.isEmpty(), modifier = modifier) { alarmsEmpty ->
    if (alarmsEmpty) {
      EmptyPlaceholder(
        imageVector = Icons.Default.AlarmAdd,
        primaryText = "No alarms created",
        secondaryText = "Create one using the button in bottom right.",
        modifier = Modifier.fillMaxSize()
      )
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
      ) {
        items(alarms) {
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
