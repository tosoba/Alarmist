package com.trm.alarmist.widget.common.ui

import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(
  widthDp = WidgetPreviewDimensions.LARGE_WIDTH,
  heightDp = WidgetPreviewDimensions.SHOW_TITLE_BAR_HEIGHT,
)
@Preview(
  widthDp = WidgetPreviewDimensions.LARGE_WIDTH,
  heightDp = WidgetPreviewDimensions.HIDE_TITLE_BAR_HEIGHT,
)
@Preview(
  widthDp = WidgetPreviewDimensions.MEDIUM_WIDTH,
  heightDp = WidgetPreviewDimensions.SHOW_TITLE_BAR_HEIGHT,
)
@Preview(
  widthDp = WidgetPreviewDimensions.MEDIUM_WIDTH,
  heightDp = WidgetPreviewDimensions.HIDE_TITLE_BAR_HEIGHT,
)
@Preview(
  widthDp = WidgetPreviewDimensions.SMALL_WIDTH,
  heightDp = WidgetPreviewDimensions.SHOW_TITLE_BAR_HEIGHT,
)
@Preview(
  widthDp = WidgetPreviewDimensions.SMALL_WIDTH,
  heightDp = WidgetPreviewDimensions.HIDE_TITLE_BAR_HEIGHT,
)
annotation class AlarmListWidgetPreview
