package com.trm.alarmist.feature.alarms.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.ui.AlarmListItem
import com.trm.alarmist.core.ui.floatingActionButtonSpacerItem

@Composable
fun AlarmListContent(modifier: Modifier = Modifier, component: AlarmListComponent) {
  val alarms by component.alarms.collectAsState()
  val groups by component.groups.collectAsState()

  LazyColumn(
    modifier = modifier,
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
  ) {
    if (alarms.isEmpty()) {
      item {
        Box(modifier = Modifier.fillParentMaxSize()) {
          Text(modifier = Modifier.align(Alignment.Center), text = "No alarms created")
        }
      }
    }

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
