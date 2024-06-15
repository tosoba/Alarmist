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
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import com.trm.alarmist.R
import com.trm.alarmist.core.common.model.Initializable
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.UpcomingAlarmListModel
import com.trm.alarmist.core.domain.model.UpcomingAlarmListStatus
import com.trm.alarmist.core.domain.usecase.GetTodayAlarmsUseCase
import com.trm.alarmist.core.ui.buildAlarmLabelText
import com.trm.alarmist.widget.common.ui.WidgetAlarmFireAtTimeText
import com.trm.alarmist.widget.common.ui.WidgetDimensions.NUM_GRID_CELLS
import com.trm.alarmist.widget.common.ui.WidgetDimensions.fillItemItemPadding
import com.trm.alarmist.widget.common.ui.WidgetDimensions.filledItemCornerRadius
import com.trm.alarmist.widget.common.ui.WidgetDimensions.verticalItemSpacing
import com.trm.alarmist.widget.common.ui.WidgetDimensions.widgetPadding
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
import com.trm.alarmist.widget.common.util.stringResource
import com.trm.alarmist.widget.common.util.turnAlarmOffIntent
import com.trm.alarmist.widget.common.util.updateWidgetIntent
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TodayAlarmsWidget : GlanceAppWidget(), KoinComponent {
  private val getTodayAlarmsUseCase: GetTodayAlarmsUseCase by inject()
  private val repository: AlarmRepository by inject()

  override val sizeMode: SizeMode = SizeMode.Exact

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      val state = currentState<Preferences>()
      val widgetState by
        produceState(Initializable(TodayAlarmsWidgetState(emptyList(), emptyMap())), state) {
          value =
            Initializable(
              TodayAlarmsWidgetState(
                alarms = getTodayAlarmsUseCase(),
                groups = repository.getAllAlarmGroups().associateBy(AlarmGroupModel::id),
              ),
              true,
            )
        }
      CompositionLocalProvider(LocalIsPreviewProvider provides false) {
        TodayAlarmsWidgetScaffold(id = id, state = widgetState)
      }
    }
  }
}

private data class TodayAlarmsWidgetState(
  val alarms: List<UpcomingAlarmListModel>,
  val groups: Map<Long, AlarmGroupModel>,
)

@Composable
private fun TodayAlarmsWidgetScaffold(id: GlanceId, state: Initializable<TodayAlarmsWidgetState>) {
  GlanceTheme {
    val context = LocalContext.current
    val widgetManager = remember(id) { GlanceAppWidgetManager(context) }
    val widgetLayoutSize = WidgetLayoutSize.fromLocalSize()

    fun titleBar(): @Composable () -> Unit = {
      WidgetTitleBar(
        // TODO: either app icon or icon representing today
        startIcon =
          if (widgetLayoutSize == WidgetLayoutSize.Large) ImageProvider(R.mipmap.ic_launcher_round)
          else null,
        iconColor = GlanceTheme.colors.primary,
        actions = {
          CircleIconButton(
            imageProvider = ImageProvider(R.drawable.refresh),
            contentDescription = stringResource(R.string.refresh),
            contentColor = GlanceTheme.colors.secondary,
            backgroundColor = null,
            onClick =
              actionSendBroadcast(
                context.updateWidgetIntent<TodayAlarmsWidgetReceiver>(
                  widgetManager.getAppWidgetId(id)
                )
              ),
          )
        },
      ) {
        Column(
          modifier =
            GlanceModifier.defaultWeight().run {
              if (widgetLayoutSize != WidgetLayoutSize.Large) padding(start = 16.dp) else this
            }
        ) {
          Box {
            WidgetTextClock(
              format12Hour =
                context.getString(
                  if (widgetLayoutSize != WidgetLayoutSize.Small) R.string.time_format_12_h_full
                  else R.string.time_format_12_h_short
                ),
              format24Hour =
                context.getString(
                  if (widgetLayoutSize != WidgetLayoutSize.Small) R.string.time_format_24_h_full
                  else R.string.time_format_24_h_short
                ),
            ) {
              setFloat(
                R.id.widget_text_clock,
                "setTextSize",
                context.resources.getInteger(R.integer.widget_text_clock_large_font_size).toFloat(),
              )
            }
          }

          Box {
            val amPmFormat =
              context.getString(
                if (widgetLayoutSize != WidgetLayoutSize.Small) R.string.time_format_am_pm_full
                else R.string.time_format_am_pm_short
              )
            WidgetTextClock(format12Hour = amPmFormat, format24Hour = amPmFormat) {
              setFloat(
                R.id.widget_text_clock,
                "setTextSize",
                context.resources.getInteger(R.integer.widget_text_clock_am_pm_font_size).toFloat(),
              )
            }
          }
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
      TodayAlarmsWidgetScaffoldContent(state = state)
    }
  }
}

@Composable
private fun TodayAlarmsWidgetScaffoldContent(state: Initializable<TodayAlarmsWidgetState>) {
  when {
    !state.initialized -> {
      WidgetLoadingIndicator(modifier = GlanceModifier.fillMaxWidth().padding(vertical = 20.dp))
    }
    state.data.alarms.isEmpty() -> {
      // TODO: EmptyContent() with button action deeplink to create alarm
    }
    else -> {
      when (WidgetLayoutSize.fromLocalSize()) {
        WidgetLayoutSize.Small -> {
          TodayAlarmsWidgetList(state = state.data, displayHeaderSupporting = false)
        }
        WidgetLayoutSize.Medium -> {
          TodayAlarmsWidgetList(state = state.data, displayHeaderSupporting = true)
        }
        WidgetLayoutSize.Large -> {
          TodayAlarmsWidgetGrid(state = state.data)
        }
      }
    }
  }
}

@Composable
private fun TodayAlarmsWidgetList(state: TodayAlarmsWidgetState, displayHeaderSupporting: Boolean) {
  WidgetLazyColumn(
    items = state.alarms,
    modifier = GlanceModifier.fillMaxSize(),
    verticalItemsSpacing = verticalItemSpacing,
  ) { item ->
    TodayAlarmsWidgetListItem(
      item = item,
      group = item.groupId?.let(state.groups::get),
      displayHeaderSupporting = displayHeaderSupporting,
      onClick = null, // TODO: navigate to app - edit alarm
      modifier = GlanceModifier.fillMaxSize(),
    )
  }
}

@Composable
private fun TodayAlarmsWidgetGrid(state: TodayAlarmsWidgetState) {
  WidgetLazyVerticalGrid(
    gridCells = NUM_GRID_CELLS,
    items = state.alarms,
    modifier = GlanceModifier.fillMaxSize(),
    cellSpacing = verticalItemSpacing,
  ) { item ->
    TodayAlarmsWidgetListItem(
      item = item,
      group = item.groupId?.let(state.groups::get),
      displayHeaderSupporting = true,
      onClick = null, // TODO: navigate to app - edit alarm
      modifier = GlanceModifier.fillMaxSize(),
    )
  }
}

@Composable
private fun TodayAlarmsWidgetListItem(
  item: UpcomingAlarmListModel,
  group: AlarmGroupModel?,
  displayHeaderSupporting: Boolean,
  onClick: Action?,
  modifier: GlanceModifier = GlanceModifier,
) {
  @Composable
  fun TitleText() {
    buildAlarmLabelText(item.name, group).takeIf(String::isNotEmpty)?.let {
      Text(text = it, maxLines = 1, style = WidgetTextStyles.titleText)
    }
  }

  @Composable
  fun SupportingText() {
    Text(
      text =
        stringResource(
          id =
            if (item.scheduledOnDaysOfWeek.isNotEmpty() || item.date != null) {
              R.string.scheduled_for_today
            } else {
              R.string.one_time
            }
        ),
      maxLines = 2,
      style = WidgetTextStyles.supportingText,
    )
  }

  @Composable
  fun Leading() {
    WidgetAlarmFireAtTimeText(
      fireAtTime = item.fireAtTime,
      is24HourFormat = DateFormat.is24HourFormat(LocalContext.current),
      useFullFormat = displayHeaderSupporting,
      style =
        WidgetTextStyles.leadingText(
          fontWeight =
            if (item.status == UpcomingAlarmListStatus.ON) FontWeight.Medium else FontWeight.Normal
        ),
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

  WidgetListItem(
    modifier =
      modifier
        .padding(fillItemItemPadding)
        .cornerRadius(filledItemCornerRadius)
        .background(
          if (item.status == UpcomingAlarmListStatus.ON) GlanceTheme.colors.primaryContainer
          else GlanceTheme.colors.secondaryContainer
        ),
    headlineContent = { if (displayHeaderSupporting) TitleText() },
    supportingContent = { if (displayHeaderSupporting) SupportingText() },
    onClick = onClick,
    leadingContent = { Leading() },
    trailingContent = { Trailing() },
  )
}
