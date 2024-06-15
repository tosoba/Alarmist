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
import com.trm.alarmist.widget.common.util.stringResource
import com.trm.alarmist.widget.common.util.turnAlarmOffIntent
import com.trm.alarmist.widget.common.util.updateWidgetIntent
import com.trm.alarmist.widget.today.Dimensions.NUM_GRID_CELLS
import com.trm.alarmist.widget.today.Dimensions.fillItemItemPadding
import com.trm.alarmist.widget.today.Dimensions.filledItemCornerRadius
import com.trm.alarmist.widget.today.Dimensions.verticalItemSpacing
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
          if (widgetLayoutSize == WidgetLayoutSize.Large) ImageProvider(R.mipmap.ic_launcher_round)
          else null,
        iconColor = GlanceTheme.colors.primary,
        actions = {
          CircleIconButton(
            imageProvider = ImageProvider(R.drawable.refresh),
            contentDescription = stringResource(R.string.refresh),
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
    verticalItemsSpacing = verticalItemSpacing,
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
    cellSpacing = verticalItemSpacing,
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
  const val NUM_GRID_CELLS = 2

  val widgetPadding = 12.dp
  val filledItemCornerRadius = 16.dp
  val fillItemItemPadding = 12.dp
  val verticalItemSpacing = 4.dp
}
