package com.trm.alarmist.widget.today

import android.content.Context
import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.Action
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.Switch
import androidx.glance.appwidget.action.actionSendBroadcast
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import com.trm.alarmist.R
import com.trm.alarmist.core.common.model.Initializable
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.model.UpcomingAlarmListModel
import com.trm.alarmist.core.domain.model.UpcomingAlarmListStatus
import com.trm.alarmist.core.domain.usecase.GetTodayAlarmsUseCase
import com.trm.alarmist.widget.common.ui.WidgetAlarmFireAtTimeText
import com.trm.alarmist.widget.common.ui.WidgetLayoutSize
import com.trm.alarmist.widget.common.ui.WidgetLayoutSize.Companion.showTitleBar
import com.trm.alarmist.widget.common.ui.WidgetLazyColumn
import com.trm.alarmist.widget.common.ui.WidgetLazyVerticalGrid
import com.trm.alarmist.widget.common.ui.WidgetListItem
import com.trm.alarmist.widget.common.ui.WidgetLoadingIndicator
import com.trm.alarmist.widget.common.ui.WidgetTextClock
import com.trm.alarmist.widget.common.ui.WidgetTextStyles
import com.trm.alarmist.widget.common.ui.WidgetTitleBar
import com.trm.alarmist.widget.common.util.LocalIsPreviewProvider
import com.trm.alarmist.widget.common.util.turnAlarmOffIntent
import com.trm.alarmist.widget.common.util.updateWidgetIntent
import com.trm.alarmist.widget.today.Dimensions.NUM_GRID_CELLS
import com.trm.alarmist.widget.today.Dimensions.fillItemItemPadding
import com.trm.alarmist.widget.today.Dimensions.filledItemCornerRadius
import com.trm.alarmist.widget.today.Dimensions.verticalSpacing
import com.trm.alarmist.widget.today.Dimensions.widgetPadding
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TodayAlarmsWidget : GlanceAppWidget(), KoinComponent {
  private val getTodayAlarmsUseCase: GetTodayAlarmsUseCase by inject()

  override val sizeMode: SizeMode = SizeMode.Exact

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      val state = currentState<Preferences>()
      val alarms by
        produceState(Initializable(emptyList()), state) {
          value = Initializable(getTodayAlarmsUseCase(), true)
        }

      CompositionLocalProvider(LocalIsPreviewProvider provides false) {
        TodayAlarmsWidgetContent(id = id, alarms = alarms)
      }
    }
  }
}

@Composable
private fun TodayAlarmsWidgetContent(
  id: GlanceId,
  alarms: Initializable<List<UpcomingAlarmListModel>>,
) {
  GlanceTheme {
    val context = LocalContext.current
    val widgetManager = remember(id) { GlanceAppWidgetManager(context) }

    val widgetLayoutSize = WidgetLayoutSize.fromLocalSize()

    fun titleBar(): @Composable () -> Unit = {
      WidgetTitleBar(
        // TODO: either app icon or icon representing today
        startIcon =
          if (widgetLayoutSize != WidgetLayoutSize.Small) ImageProvider(R.mipmap.ic_launcher_round)
          else null,
        iconColor = GlanceTheme.colors.primary,
        actions = {
          CircleIconButton(
            imageProvider = ImageProvider(R.drawable.refresh),
            contentDescription = "Refresh",
            contentColor = GlanceTheme.colors.secondary,
            backgroundColor = null, // transparent
            onClick =
              actionSendBroadcast(
                context.updateWidgetIntent<TodayAlarmsWidgetReceiver>(
                  widgetManager.getAppWidgetId(id)
                )
              ),
          )
        },
      ) {
        Box(
          contentAlignment = Alignment.CenterStart,
          modifier =
            GlanceModifier.defaultWeight().run {
              if (widgetLayoutSize == WidgetLayoutSize.Small) padding(start = 16.dp) else this
            },
        ) {
          WidgetTextClock(useFullTimeFormat = widgetLayoutSize != WidgetLayoutSize.Small)
        }
      }
    }

    Scaffold(
      backgroundColor = GlanceTheme.colors.widgetBackground,
      modifier =
        GlanceModifier.padding(
          top = if (showTitleBar()) 0.dp else widgetPadding,
          bottom = widgetPadding,
        ),
      titleBar = if (showTitleBar()) titleBar() else null,
    ) {
      Content(alarms = alarms)
    }
  }
}

@Composable
private fun Content(alarms: Initializable<List<UpcomingAlarmListModel>>) {
  when {
    !alarms.initialized -> {
      // TODO: better loading indicator?
      WidgetLoadingIndicator(modifier = GlanceModifier.fillMaxWidth().padding(vertical = 20.dp))
    }
    alarms.data.isEmpty() -> {
      // TODO: EmptyContent()
    }
    else -> {
      when (WidgetLayoutSize.fromLocalSize()) {
        WidgetLayoutSize.Small -> {
          ListView(items = alarms.data, displayHeaderSupporting = false)
        }
        WidgetLayoutSize.Medium -> {
          ListView(items = alarms.data, displayHeaderSupporting = true)
        }
        WidgetLayoutSize.Large -> {
          GridView(items = alarms.data)
        }
      }
    }
  }
}

@Composable
private fun ListView(items: List<UpcomingAlarmListModel>, displayHeaderSupporting: Boolean) {
  WidgetLazyColumn(
    items = items,
    modifier = GlanceModifier.fillMaxSize(),
    verticalItemsSpacing = verticalSpacing,
  ) { item ->
    FilledHorizontalListItem(
      item = item,
      displayLeading = true,
      displayHeaderSupporting = displayHeaderSupporting,
      displayTrailing = true,
      onClick = null, // TODO: navigate to app
      modifier = GlanceModifier.fillMaxSize(),
    )
  }
}

@Composable
private fun GridView(items: List<UpcomingAlarmListModel>) {
  WidgetLazyVerticalGrid(
    gridCells = NUM_GRID_CELLS,
    items = items,
    modifier = GlanceModifier.fillMaxSize(),
    cellSpacing = verticalSpacing,
  ) { item ->
    FilledHorizontalListItem(
      item = item,
      displayLeading = true,
      displayHeaderSupporting = true,
      displayTrailing = true,
      onClick = null, // TODO: navigate to app
      modifier = GlanceModifier.fillMaxSize(),
    )
  }
}

@Composable
private fun FilledHorizontalListItem(
  item: UpcomingAlarmListModel,
  displayLeading: Boolean,
  displayHeaderSupporting: Boolean,
  displayTrailing: Boolean,
  onClick: Action?,
  modifier: GlanceModifier = GlanceModifier,
) {
  @Composable
  fun TitleText() {
    Text(
      text = item.fireAtTime.toString(),
      maxLines = 1,
      style = WidgetTextStyles.titleText,
    ) // TODO: group/name label (with icon)
  }

  @Composable
  fun SupportingText() {
    Text(
      text = item.fireAtTime.toString(),
      maxLines = 2,
      style = WidgetTextStyles.supportingText,
    ) // TODO: schedule desc
  }

  @Composable
  fun Leading() {
    WidgetAlarmFireAtTimeText(
      fireAtTime = item.fireAtTime,
      is24HourFormat = DateFormat.is24HourFormat(LocalContext.current),
      useFullFormat = displayHeaderSupporting,
      style = WidgetTextStyles.leadingText,
    )
  }

  @Composable
  fun Trailing() {
    Switch(
      checked = item.status == UpcomingAlarmListStatus.ON,
      onCheckedChange =
        actionSendBroadcast(LocalContext.current.turnAlarmOffIntent(item.id, LocalDate.now())),
    )
  }

  WidgetListItem( // TODO: different background color when switching alarm on/off
    modifier =
      modifier
        .padding(fillItemItemPadding)
        .cornerRadius(filledItemCornerRadius)
        .background(GlanceTheme.colors.secondaryContainer),
    headlineContent = { if (displayHeaderSupporting) TitleText() },
    supportingContent = { if (displayHeaderSupporting) SupportingText() },
    onClick = onClick,
    leadingContent =
      if (displayLeading) {
        { Leading() }
      } else {
        null
      },
    trailingContent =
      if (displayTrailing) {
        { Trailing() }
      } else {
        null
      },
  )
}

private object Dimensions {
  /** Number of cells in the grid, when items are displayed as a grid. */
  const val NUM_GRID_CELLS = 2

  /** Padding around the the widget content */
  val widgetPadding = 12.dp

  /** Corner radius for each filled list item. */
  val filledItemCornerRadius = 16.dp

  /** Padding applied to each item in the list. */
  val fillItemItemPadding = 12.dp

  /** Vertical Space between each item in the list. */
  val verticalSpacing = 4.dp
}
