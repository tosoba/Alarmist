package com.trm.alarmist.feature.alarms

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetpack.pages.Pages
import com.arkivanov.decompose.extensions.compose.jetpack.pages.PagesScrollAnimation
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.trm.alarmist.feature.alarms.groups.AlarmGroupsContent
import com.trm.alarmist.feature.alarms.list.AlarmListContent
import com.trm.alarmist.feature.alarms.upcoming.UpcomingAlarmsContent

@OptIn(ExperimentalDecomposeApi::class, ExperimentalFoundationApi::class)
@Composable
fun AlarmsContent(modifier: Modifier = Modifier, component: AlarmsComponent) {
  val pagesState = component.pages.subscribeAsState()

  Column(modifier = modifier) {
    Pages(
        modifier = Modifier.weight(1f),
        pages = component.pages,
        onPageSelected = component::onPageSelected,
        scrollAnimation = PagesScrollAnimation.Default,
    ) { _, page ->
      when (page) {
        is AlarmsComponent.Page.AlarmGroups -> {
          AlarmGroupsContent(modifier = Modifier.fillMaxSize(), component = page.component)
        }
        is AlarmsComponent.Page.AlarmsList -> {
          AlarmListContent(modifier = Modifier.fillMaxSize(), component = page.component)
        }
        is AlarmsComponent.Page.UpcomingAlarms -> {
          UpcomingAlarmsContent(modifier = Modifier.fillMaxSize(), component = page.component)
        }
      }
    }

    NavigationBar(modifier = Modifier.fillMaxWidth()) {
      NavigationBarItem(
          selected = pagesState.value.selectedIndex == 0,
          onClick = { component.onPageSelected(0) },
          icon = {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = "All",
            )
          },
          label = { Text("All") })

      NavigationBarItem(
          selected = pagesState.value.selectedIndex == 1,
          onClick = { component.onPageSelected(1) },
          icon = {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Upcoming",
            )
          },
          label = { Text("Upcoming") })

      NavigationBarItem(
          selected = pagesState.value.selectedIndex == 2,
          onClick = { component.onPageSelected(2) },
          icon = {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Groups",
            )
          },
          label = { Text("Groups") })
    }
  }
}
