package com.trm.alarmist.feature.alarms

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.value.Value
import com.trm.alarmist.feature.alarms.groups.AlarmGroupsComponent
import com.trm.alarmist.feature.alarms.groups.DefaultAlarmGroupsComponent
import com.trm.alarmist.feature.alarms.list.AlarmListComponent
import com.trm.alarmist.feature.alarms.list.DefaultAlarmListComponent
import com.trm.alarmist.feature.alarms.upcoming.DefaultUpcomingAlarmsComponent
import com.trm.alarmist.feature.alarms.upcoming.UpcomingAlarmsComponent
import kotlinx.serialization.Serializable

@OptIn(ExperimentalDecomposeApi::class)
interface AlarmsComponent {
  val pages: Value<ChildPages<*, Page>>

  fun onPageSelected(index: Int)

  fun onAddClick()

  sealed interface Page {
    class AlarmsList(val component: AlarmListComponent) : Page

    class UpcomingAlarms(val component: UpcomingAlarmsComponent) : Page

    class AlarmGroups(val component: AlarmGroupsComponent) : Page
  }
}

@OptIn(ExperimentalDecomposeApi::class)
class DefaultAlarmsComponent(
  componentContext: ComponentContext,
  private val onAddAlarmClick: () -> Unit,
  private val onAddGroupClick: () -> Unit,
) : AlarmsComponent, ComponentContext by componentContext {
  private val navigation = PagesNavigation<PageConfig>()

  override val pages: Value<ChildPages<*, AlarmsComponent.Page>> =
    childPages(
      source = navigation,
      serializer = PageConfig.serializer(),
      initialPages = {
        Pages(
          items =
            listOf(
              PageConfig.AlarmsList,
              PageConfig.UpcomingAlarms,
              PageConfig.AlarmGroups,
            ),
          selectedIndex = 0,
        )
      },
      childFactory = ::createPage
    )

  private fun createPage(
    config: PageConfig,
    componentContext: ComponentContext
  ): AlarmsComponent.Page =
    when (config) {
      PageConfig.AlarmsList -> {
        AlarmsComponent.Page.AlarmsList(
          DefaultAlarmListComponent(
            componentContext = componentContext
          )
        )
      }
      PageConfig.UpcomingAlarms -> {
        AlarmsComponent.Page.UpcomingAlarms(
          DefaultUpcomingAlarmsComponent(
            componentContext = componentContext
          )
        )
      }
      PageConfig.AlarmGroups -> {
        AlarmsComponent.Page.AlarmGroups(
          DefaultAlarmGroupsComponent(
            componentContext = componentContext
          )
        )
      }
    }

  override fun onPageSelected(index: Int) {
    navigation.select(index = index)
  }

  override fun onAddClick() {
    if (pages.value.selectedIndex == 2) {
      onAddGroupClick()
    } else {
      onAddAlarmClick()
    }
  }

  @Serializable
  private sealed interface PageConfig {
    @Serializable data object AlarmsList : PageConfig

    @Serializable data object UpcomingAlarms : PageConfig

    @Serializable data object AlarmGroups : PageConfig
  }
}
