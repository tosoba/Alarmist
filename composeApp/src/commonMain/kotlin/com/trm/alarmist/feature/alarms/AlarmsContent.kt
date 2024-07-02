package com.trm.alarmist.feature.alarms

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.add
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.pages.Pages
import com.arkivanov.decompose.extensions.compose.pages.PagesScrollAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.trm.alarmist.feature.alarms.groups.AlarmGroupsContent
import com.trm.alarmist.feature.alarms.list.AlarmListContent
import com.trm.alarmist.feature.alarms.ui.AlarmsNavigationBar
import com.trm.alarmist.feature.alarms.ui.AlarmsNavigationRail
import com.trm.alarmist.feature.alarms.upcoming.UpcomingAlarmsContent
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalDecomposeApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun AlarmsContent(modifier: Modifier = Modifier, component: AlarmsComponent) {
  val windowSizeClass = calculateWindowSizeClass()

  val pagesState = component.pages.subscribeAsState()
  val snackbarHostState = remember(::SnackbarHostState)

  Scaffold(
    modifier = modifier,
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    floatingActionButton = {
      FloatingActionButton(onClick = component::onAddClick) {
        Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(Res.string.add))
      }
    },
    bottomBar = {
      if (
        windowSizeClass.heightSizeClass != WindowHeightSizeClass.Compact &&
          windowSizeClass.widthSizeClass != WindowWidthSizeClass.Expanded
      ) {
        AlarmsNavigationBar(
          selectedIndex = pagesState.value.selectedIndex,
          onPageSelected = component::onPageSelected,
          modifier = Modifier.fillMaxWidth(),
        )
      }
    },
  ) {
    Row(
      modifier =
        Modifier.fillMaxSize()
          .padding(
            start = it.calculateStartPadding(LocalLayoutDirection.current),
            bottom = it.calculateBottomPadding(),
            end = it.calculateEndPadding(LocalLayoutDirection.current),
          )
    ) {
      if (
        windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact ||
          windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
      ) {
        AlarmsNavigationRail(
          selectedIndex = pagesState.value.selectedIndex,
          onPageSelected = component::onPageSelected,
          modifier = Modifier.fillMaxHeight(),
        )
      }

      AlarmsMainContent(component = component, modifier = Modifier.fillMaxHeight().weight(1f))
    }
  }
}

@OptIn(ExperimentalDecomposeApi::class, ExperimentalFoundationApi::class)
@Composable
private fun AlarmsMainContent(component: AlarmsComponent, modifier: Modifier = Modifier) {
  Box(modifier = modifier) {
    Pages(
      modifier = Modifier.fillMaxSize(),
      pages = component.pages,
      onPageSelected = component::onPageSelected,
      scrollAnimation = PagesScrollAnimation.Default,
    ) { _, page ->
      when (page) {
        is AlarmsComponent.Page.AlarmsList -> {
          AlarmListContent(component = page.component, modifier = Modifier.fillMaxSize())
        }
        is AlarmsComponent.Page.UpcomingAlarms -> {
          val selectedDateAlarms by page.component.feature.selectedDateAlarmsFlow.collectAsState()
          val alarmCounts by page.component.feature.scheduledAlarmCountsFlow.collectAsState()
          val groups by page.component.feature.groups.collectAsState()

          UpcomingAlarmsContent(
            initialState = page.component.feature.calendarState,
            modifier = Modifier.fillMaxSize(),
            alarmCounts = alarmCounts,
            selectedDateAlarms = selectedDateAlarms,
            groups = groups,
            onAlarmItemClick = page.component::onAlarmClick,
            onOffButtonClick = page.component.feature::onTurnAlarmOff,
            onOffOnDateButtonClick = page.component.feature::onTurnAlarmOffOnSelectedDate,
            onOnButtonClick = page.component.feature::onTurnAlarmOnOnSelectedDate,
            onSelectedDateChange = page.component.feature::onSelectedDateChange,
            onMonthlyDateRangeChange = page.component.feature::onMonthlyDateRangeChange,
          )
        }
        is AlarmsComponent.Page.AlarmGroups -> {
          val state by page.component.feature.state.collectAsState()

          AlarmGroupsContent(
            modifier = Modifier.fillMaxSize(),
            state = state,
            onExpandGroup = page.component.feature::onExpandGroup,
            onCollapseGroup = page.component.feature::onCollapseGroup,
            onAlarmItemClick = page.component.onEditAlarmClick,
            onEditGroupClick = page.component.onEditGroupClick,
            onToggleAlarmOnOff = page.component.feature::onToggleAlarmOnOff,
            onToggleGroupOnOff = page.component.feature::onToggleGroupOnOff,
          )
        }
      }
    }

    Box(
      modifier =
        Modifier.fillMaxWidth()
          .height(8.dp)
          .background(
            brush =
              Brush.verticalGradient(
                colors =
                  listOf(
                    MaterialTheme.colorScheme.background,
                    MaterialTheme.colorScheme.background.copy(alpha = 0f),
                  )
              )
          )
          .align(Alignment.TopCenter)
    )

    Box(
      modifier =
        Modifier.fillMaxWidth()
          .height(8.dp)
          .background(
            brush =
              Brush.verticalGradient(
                colors =
                  listOf(
                    MaterialTheme.colorScheme.background.copy(alpha = 0f),
                    MaterialTheme.colorScheme.background,
                  )
              )
          )
          .align(Alignment.BottomCenter)
    )
  }
}
