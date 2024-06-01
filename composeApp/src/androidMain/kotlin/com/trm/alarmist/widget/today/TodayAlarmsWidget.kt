package com.trm.alarmist.widget.today

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.Switch
import androidx.glance.appwidget.action.actionSendBroadcast
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.platform.ui.appwidgets.glance.layout.collections.layout.ListItem
import com.trm.alarmist.R
import com.trm.alarmist.core.common.model.Initializable
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.usecase.GetTodayAlarmsUseCase
import com.trm.alarmist.widget.common.ui.RoundedScrollingLazyColumn
import com.trm.alarmist.widget.common.ui.RoundedScrollingLazyVerticalGrid
import com.trm.alarmist.widget.common.ui.WidgetLoadingIndicator
import com.trm.alarmist.widget.common.util.LocalIsPreviewProvider
import com.trm.alarmist.widget.common.util.updateWidgetIntent
import com.trm.alarmist.widget.today.Dimensions.NUM_GRID_CELLS
import com.trm.alarmist.widget.today.Dimensions.fillItemItemPadding
import com.trm.alarmist.widget.today.Dimensions.filledItemCornerRadius
import com.trm.alarmist.widget.today.Dimensions.verticalSpacing
import com.trm.alarmist.widget.today.Dimensions.widgetPadding
import com.trm.alarmist.widget.today.ImageTextListLayoutSize.Companion.showTitleBar
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
private fun TodayAlarmsWidgetContent(id: GlanceId, alarms: Initializable<List<AlarmListModel>>) {
  GlanceTheme {
    val context = LocalContext.current
    val widgetManager = remember(id) { GlanceAppWidgetManager(context) }

    val imageTextListLayoutSize = ImageTextListLayoutSize.fromLocalSize()

    fun titleBar(): @Composable (() -> Unit) = {
      // TODO: consider using TitleBar impl to create a TitleBar with a RemoteViews TextClock
      // instead of string title
      TitleBar(
        // TODO: either app icon or icon representing today
        startIcon = ImageProvider(R.mipmap.ic_launcher_round),
        title =
          "Today".takeIf { imageTextListLayoutSize != ImageTextListLayoutSize.Small }.orEmpty(),
        iconColor = GlanceTheme.colors.primary,
        textColor = GlanceTheme.colors.onSurface,
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
      )
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
private fun Content(alarms: Initializable<List<AlarmListModel>>) {
  when {
    !alarms.initialized -> {
      // TODO: better loading indicator?
      WidgetLoadingIndicator(modifier = GlanceModifier.fillMaxWidth().padding(vertical = 20.dp))
    }
    alarms.data.isEmpty() -> {
      // TODO: EmptyContent()
    }
    else -> {
      when (ImageTextListLayoutSize.fromLocalSize()) {
        ImageTextListLayoutSize.Small -> {
          ListView(items = alarms.data, displayHeaderSupporting = false)
        }
        ImageTextListLayoutSize.Medium -> {
          ListView(items = alarms.data, displayHeaderSupporting = true)
        }
        ImageTextListLayoutSize.Large -> {
          GridView(items = alarms.data)
        }
      }
    }
  }
}

@Composable
private fun ListView(items: List<AlarmListModel>, displayHeaderSupporting: Boolean) {
  RoundedScrollingLazyColumn(
    modifier = GlanceModifier.fillMaxSize(),
    items = items,
    verticalItemsSpacing = verticalSpacing,
    itemContentProvider = { item ->
      FilledHorizontalListItem(
        item = item,
        displayLeading = true,
        displayHeaderSupporting = displayHeaderSupporting,
        displayTrailing = true,
        onClick = null, // TODO: navigate to app
        modifier = GlanceModifier.fillMaxSize(),
      )
    },
  )
}

/**
 * A grid of [FilledHorizontalListItem]s suitable for [ImageTextListLayoutSize.Large] sizes.
 *
 * Supporting the grid display allows large screen users view more information at once.
 */
@Composable
private fun GridView(items: List<AlarmListModel>) {
  RoundedScrollingLazyVerticalGrid(
    gridCells = NUM_GRID_CELLS,
    items = items,
    cellSpacing = verticalSpacing,
    itemContentProvider = { item ->
      FilledHorizontalListItem(
        item = item,
        displayLeading = true,
        displayHeaderSupporting = true,
        displayTrailing = true,
        onClick = null, // TODO: navigate to app
        modifier = GlanceModifier.fillMaxSize(),
      )
    },
    modifier = GlanceModifier.fillMaxSize(),
  )
}

/**
 * Arranges the texts, the image and the icon button in a horizontal arrangement with a filled
 * container.
 */
@Composable
private fun FilledHorizontalListItem(
  item: AlarmListModel,
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
      style = TextStyles.titleText,
    ) // TODO: group/name label (with icon)
  }

  @Composable
  fun SupportingText() {
    Text(
      text = item.fireAtTime.toString(),
      maxLines = 2,
      style = TextStyles.supportingText,
    ) // TODO: schedule desc
  }

  @Composable
  fun Leading() {
    Text(text = item.fireAtTime.toString(), maxLines = 1, style = TextStyles.leadingText)
  }

  @Composable
  fun Trailing() {
    Switch(
      checked = item.isOn,
      onCheckedChange = null, // TODO: find -> revert commit with switch action
    )
  }

  ListItem( // TODO: different background color when switching alarm on/off
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

/**
 * Reference breakpoints for deciding on widget style to display e.g. list / grid etc.
 *
 * In this layout, only width breakpoints are used to scale the layout.
 */
private enum class ImageTextListLayoutSize(val maxWidth: Dp) {
  // Single column vertical list without images or trailing button in this size.
  Small(maxWidth = 260.dp),

  // Single column horizontal list with images and optional trailing button if exists.
  Medium(maxWidth = 479.dp),

  // 2 Column Grid of horizontal list items. Images are always shown; trailing button is shown if
  // it fits.
  Large(maxWidth = 644.dp);

  companion object {
    /**
     * Returns the corresponding [ImageTextListLayoutSize] to be considered for the current widget
     * size.
     */
    @Composable
    fun fromLocalSize(): ImageTextListLayoutSize {
      val width = LocalSize.current.width
      return when {
        width >= Medium.maxWidth -> Large
        width >= Small.maxWidth -> Medium
        else -> Small
      }
    }

    @Composable fun showTitleBar(): Boolean = LocalSize.current.height >= 180.dp
  }
}

private object TextStyles {
  val leadingText: TextStyle
    @Composable
    get() =
      TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize =
          if (ImageTextListLayoutSize.fromLocalSize() == ImageTextListLayoutSize.Small) {
            18.sp
          } else {
            22.sp // M3 Title Large
          },
        color = GlanceTheme.colors.onSurface,
      )

  val titleText: TextStyle
    @Composable
    get() =
      TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize =
          if (ImageTextListLayoutSize.fromLocalSize() == ImageTextListLayoutSize.Small) {
            14.sp // M3 Title Small
          } else {
            16.sp // M3 Title Medium
          },
        color = GlanceTheme.colors.onSurface,
      )

  val supportingText: TextStyle
    @Composable
    get() =
      TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp, // M3 Label Medium
        color = GlanceTheme.colors.secondary,
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
