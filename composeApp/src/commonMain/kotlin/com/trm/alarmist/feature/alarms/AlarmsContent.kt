package com.trm.alarmist.feature.alarms

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.add
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.pages.Pages
import com.arkivanov.decompose.extensions.compose.pages.PagesScrollAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.trm.alarmist.feature.alarm.AlarmPermissionStatusCard
import com.trm.alarmist.feature.alarms.groups.AlarmGroupsContent
import com.trm.alarmist.feature.alarms.list.AlarmListContent
import com.trm.alarmist.feature.alarms.ui.AlarmsNavigationBar
import com.trm.alarmist.feature.alarms.ui.AlarmsNavigationRail
import com.trm.alarmist.feature.alarms.upcoming.UpcomingAlarmsContent
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalDecomposeApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun AlarmsContent(modifier: Modifier = Modifier, component: AlarmsComponent) {
  val pagesState = component.pages.subscribeAsState()

  val windowSizeClass = calculateWindowSizeClass()
  if (
    windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact ||
      windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
  ) {
    Row(modifier = modifier) {
      AlarmsNavigationRail(
        selectedIndex = pagesState.value.selectedIndex,
        onPageSelected = component::onPageSelected,
        modifier = Modifier.fillMaxHeight(),
      )

      AlarmsMainContent(component = component, modifier = Modifier.fillMaxHeight().weight(1f))
    }
  } else {
    Column(modifier = modifier) {
      AlarmsMainContent(component = component, modifier = Modifier.fillMaxWidth().weight(1f))

      AlarmsNavigationBar(
        selectedIndex = pagesState.value.selectedIndex,
        onPageSelected = component::onPageSelected,
        modifier = Modifier.fillMaxWidth(),
      )
    }
  }
}

@OptIn(
  ExperimentalDecomposeApi::class,
  ExperimentalFoundationApi::class,
  ExperimentalResourceApi::class,
)
@Composable
private fun AlarmsMainContent(component: AlarmsComponent, modifier: Modifier = Modifier) {
  Box(modifier = modifier) {
    var bottomSpacerHeightPx by mutableStateOf(with(LocalDensity.current) { 72.dp.toPx() })

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
            bottomSpacerHeightDp =
              with(LocalDensity.current) { bottomSpacerHeightPx.toDp() + 16.dp },
            state = state,
            onExpandGroup = page.component.feature::onExpandGroup,
            onCollapseGroup = page.component.feature::onCollapseGroup,
            onAlarmItemClick = page.component::onEditAlarmClick,
            onEditGroupClick = page.component::onEditGroupClick,
            onToggleAlarmOnOff = page.component.feature::onToggleAlarmOnOff,
            onToggleGroupOnOff = page.component.feature::onToggleGroupOnOff,
          )
        }
        is AlarmsComponent.Page.AlarmsList -> {
          AlarmListContent(
            component = page.component,
            modifier = Modifier.fillMaxSize(),
            bottomSpacerHeightDp =
              with(LocalDensity.current) { bottomSpacerHeightPx.toDp() + 16.dp },
          )
        }
        is AlarmsComponent.Page.UpcomingAlarms -> {
          val selectedDateAlarms by page.component.feature.selectedDateAlarmsFlow.collectAsState()
          val alarmCounts by page.component.feature.scheduledAlarmCountsFlow.collectAsState()
          UpcomingAlarmsContent(
            initialState = page.component.feature.calendarState,
            modifier = Modifier.fillMaxSize(),
            bottomSpacerHeightDp =
              with(LocalDensity.current) { bottomSpacerHeightPx.toDp() + 16.dp },
            alarmCounts = alarmCounts,
            selectedDateAlarms = selectedDateAlarms,
            onAlarmItemClick = page.component::onAlarmClick,
            onAlarmToggleOnOff = page.component.feature::onToggleAlarmOnOff,
            onSelectedDateChange = page.component.feature::onSelectedDateChange,
            onMonthlyDateRangeChange = page.component.feature::onMonthlyDateRangeChange,
          )
        }
      }
    }

    Row(
      modifier =
        Modifier.fillMaxWidth().align(Alignment.BottomEnd).padding(16.dp).onGloballyPositioned {
          bottomSpacerHeightPx = it.size.height.toFloat()
        },
      verticalAlignment = Alignment.Bottom,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      AlarmPermissionStatusCard(modifier = Modifier.weight(1f).padding(end = 16.dp))

      FloatingActionButton(onClick = component::onAddClick) {
        Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(Res.string.add))
      }
    }
  }
}
