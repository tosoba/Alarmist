package com.trm.alarmist.feature.widget.group

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.ui.AlarmGroupsList
import com.trm.alarmist.core.ui.ExpandableIcon

@Composable
fun GroupWidgetConfigContent(
  modifier: Modifier = Modifier,
  state: GroupWidgetConfigState = GroupWidgetConfigState(),
  onExpandGroup: (AlarmGroupModel) -> Unit = {},
  onCollapseGroup: () -> Unit = {},
  onEditGroupClick: (AlarmGroupModel) -> Unit = {},
  onChooseGroup: (AlarmGroupModel) -> Unit = {},
) {
  AlarmGroupsList(
    groups = state.groups,
    modifier = modifier,
    expandedGroupId = state.expandedGroupId,
    expandedGroupAlarms = state.expandedGroupAlarms,
    onExpandGroup = onExpandGroup,
    onCollapseGroup = onCollapseGroup,
    onEditGroupClick = onEditGroupClick,
  ) { group ->
    Column(
      modifier = Modifier.padding(end = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Checkbox(
        checked = state.chosenGroupId == group.id,
        onCheckedChange = { _ -> onChooseGroup(group) },
      )

      if (group.alarmsCount > 0L) {
        Spacer(Modifier.weight(1f))
        ExpandableIcon(
          isExpanded = state.expandedGroupId == group.id,
          transitionLabel = "${group.name}Header",
        )
      }
    }
  }
}
