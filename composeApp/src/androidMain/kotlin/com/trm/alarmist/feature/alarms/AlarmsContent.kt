package com.trm.alarmist.feature.alarms

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.pages.Pages
import com.arkivanov.decompose.extensions.compose.pages.PagesScrollAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.trm.alarmist.feature.alarms.groups.AlarmGroupsContent
import com.trm.alarmist.feature.alarms.list.AlarmListContent
import com.trm.alarmist.feature.alarms.ui.AlarmsNavigationBar
import com.trm.alarmist.feature.alarms.upcoming.UpcomingAlarmsContent

@OptIn(ExperimentalDecomposeApi::class, ExperimentalFoundationApi::class)
@Composable
fun AlarmsContent(modifier: Modifier = Modifier, component: AlarmsComponent) {
  val pagesState = component.pages.subscribeAsState()

  Column(modifier = modifier) {
    Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
      Pages(
        pages = component.pages,
        onPageSelected = component::onPageSelected,
        scrollAnimation = PagesScrollAnimation.Default,
      ) { _, page ->
        when (page) {
          is AlarmsComponent.Page.AlarmGroups -> {
            val state by page.component.state.collectAsState()
            AlarmGroupsContent(
              modifier = Modifier.fillMaxSize(),
              state = state,
              onExpandGroup = page.component.feature::onExpandGroup,
              onCollapseGroup = page.component.feature::onCollapseGroup,
              onToggleAlarmOnOff = page.component.feature::onToggleAlarmOnOff,
              onToggleGroupOnOff = page.component.feature::onToggleGroupOnOff,
            )
          }
          is AlarmsComponent.Page.AlarmsList -> {
            AlarmListContent(modifier = Modifier.fillMaxSize(), component = page.component)
          }
          is AlarmsComponent.Page.UpcomingAlarms -> {
            val selectedDateAlarms by page.component.feature.selectedDateAlarmsFlow.collectAsState()
            UpcomingAlarmsContent(
              modifier = Modifier.fillMaxSize(),
              initialState = page.component.feature.calendarState,
              selectedDateAlarms = selectedDateAlarms,
              onAlarmItemClick = page.component::onAlarmClick,
              onAlarmToggleOnOff = page.component.feature::onToggleAlarmOnOff,
              onSelectedDateChange = page.component.feature::onSelectedDateChange,
              onMonthlyDateRangeChange = page.component.feature::onMonthlyDateRangeChange,
            )
          }
        }
      }

      FloatingActionButton(
        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        onClick = component::onAddClick,
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
      }
    }

    AlarmsNavigationBar(
      selectedIndex = pagesState.value.selectedIndex,
      onPageSelected = component::onPageSelected,
    )
  }
}
