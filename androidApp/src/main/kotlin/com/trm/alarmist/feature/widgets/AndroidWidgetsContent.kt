package com.trm.alarmist.feature.widgets

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.widget_pin_unavailable_description
import alarmist.composeapp.generated.resources.widget_pin_unavailable_title
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.widget.FrameLayout
import android.widget.RemoteViews
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.glance.appwidget.compose
import com.trm.alarmist.GroupWidgetConfigActivity
import com.trm.alarmist.core.common.util.glanceAppWidgetPreview
import com.trm.alarmist.core.common.util.pinWidget
import com.trm.alarmist.core.common.util.widgetReceiverComponentName
import com.trm.alarmist.core.ui.AnimatedNullableVisibility
import com.trm.alarmist.core.ui.BottomGradientBackground
import com.trm.alarmist.core.ui.TopGradientBackground
import com.trm.alarmist.widget.common.system.WidgetPinnedReceiver
import com.trm.alarmist.widget.group.GroupWidgetReceiver
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.math.min

@Composable
fun AndroidWidgetsContent(modifier: Modifier) {
  val context = LocalContext.current
  val widgetManager = AppWidgetManager.getInstance(context)

  PinGroupWidgetReceiverEffect()

  WidgetsGrid(
    isRequestPinAppWidgetSupported = widgetManager.isRequestPinAppWidgetSupported,
    providers =
      if (widgetManager.isRequestPinAppWidgetSupported) {
        widgetManager.getInstalledProvidersForPackage(context.packageName, null)
      } else {
        emptyList()
      },
    modifier = modifier,
  )
}

private const val ACTION_PIN_GROUP_WIDGET = "com.trm.alarmist.ACTION_PIN_GROUP_WIDGET"

@Composable
private fun PinGroupWidgetReceiverEffect() {
  val context = LocalContext.current

  DisposableEffect(context) {
    val receiver =
      object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
          context.startActivity(
            GroupWidgetConfigActivity.pinWidgetIntent(
              context = context,
              widgetId =
                intent.getIntExtra(
                  AppWidgetManager.EXTRA_APPWIDGET_ID,
                  AppWidgetManager.INVALID_APPWIDGET_ID,
                ),
            )
          )
        }
      }

    ContextCompat.registerReceiver(
      context,
      receiver,
      IntentFilter(ACTION_PIN_GROUP_WIDGET),
      ContextCompat.RECEIVER_NOT_EXPORTED,
    )

    onDispose { context.unregisterReceiver(receiver) }
  }
}

@Composable
private fun WidgetsGrid(
  isRequestPinAppWidgetSupported: Boolean,
  providers: List<AppWidgetProviderInfo>,
  modifier: Modifier = Modifier,
) {
  Scaffold(modifier = modifier) {
    Box(modifier = Modifier.fillMaxSize()) {
      val widgetRemoteViews = rememberWidgetRemoteViews(providers)

      LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 400.dp),
        contentPadding =
          PaddingValues(
            top = 0.dp,
            bottom = it.calculateBottomPadding(),
            start = it.calculateStartPadding(LocalLayoutDirection.current) + 8.dp,
            end = it.calculateEndPadding(LocalLayoutDirection.current) + 8.dp,
          ),
      ) {
        if (!isRequestPinAppWidgetSupported) {
          item {
            WidgetPinUnavailableCard(modifier = Modifier.fillMaxWidth().padding(8.dp).animateItem())
          }
        } else {
          itemsIndexed(providers) { index, provider ->
            WidgetInfoCard(
              provider = provider,
              modifier = Modifier.fillMaxWidth().animateContentSize().padding(8.dp).animateItem(),
              widgetRemoteViews = widgetRemoteViews[index],
            )
          }
        }
      }

      TopGradientBackground()
      BottomGradientBackground()
    }
  }
}

@Composable
private fun rememberWidgetRemoteViews(
  providers: List<AppWidgetProviderInfo>
): SnapshotStateList<RemoteViews?> {
  val context = LocalContext.current
  val density = LocalDensity.current

  val widgetRemoteViews =
    remember(providers) { mutableStateListOf<RemoteViews?>(*providers.map { null }.toTypedArray()) }
  val providerPxSizes = remember(providers) { providers.map(AppWidgetProviderInfo::getMinSize) }

  LaunchedEffect(providers, context) {
    providers.forEachIndexed { index, provider ->
      widgetRemoteViews[index] =
        context
          .glanceAppWidgetPreview(providerInfo = provider, noLazyLayouts = true)
          ?.compose(
            context = context,
            size =
              with(density) {
                val pxSize = providerPxSizes[index]
                DpSize(pxSize.width.toDp(), pxSize.height.toDp())
              },
          )
    }
  }

  return widgetRemoteViews
}

@Composable
private fun WidgetPinUnavailableCard(modifier: Modifier) {
  ElevatedCard(
    modifier = modifier,
    colors =
      CardColors(
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
        disabledContentColor = MaterialTheme.colorScheme.onErrorContainer,
      ),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = stringResource(Res.string.widget_pin_unavailable_title),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Medium,
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(text = stringResource(Res.string.widget_pin_unavailable_description))
    }
  }
}

@Composable
private fun WidgetInfoCard(
  provider: AppWidgetProviderInfo,
  modifier: Modifier = Modifier,
  widgetRemoteViews: RemoteViews?,
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  val description =
    remember(provider, context) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        provider.loadDescription(context)?.toString()
      } else {
        null
      }
    }

  Card(
    modifier = modifier,
    onClick = {
      scope.launch {
        context.pinWidget(providerInfo = provider, callback = provider.pinCallback(context))
      }
    },
  ) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
      Column(modifier = Modifier.padding(end = 8.dp).weight(.5f)) {
        Text(
          text = provider.loadLabel(context.packageManager),
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Medium,
        )
        description?.let {
          Spacer(modifier = Modifier.height(4.dp))
          Text(text = it, style = MaterialTheme.typography.bodyMedium)
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      AnimatedNullableVisibility(value = widgetRemoteViews, modifier = Modifier.weight(.5f)) {
        remoteViews ->
        AndroidView(factory = { FrameLayout(it).apply { addView(remoteViews.apply(it, this)) } })
      }
    }
  }
}

private fun AppWidgetProviderInfo.pinCallback(context: Context): PendingIntent =
  if (provider == context.widgetReceiverComponentName<GroupWidgetReceiver>()) {
    PendingIntent.getBroadcast(
      context,
      0,
      Intent(ACTION_PIN_GROUP_WIDGET).setPackage(context.packageName),
      // must have FLAG_MUTABLE - otherwise EXTRA_APPWIDGET_ID will not be set
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
    )
  } else {
    WidgetPinnedReceiver.Companion.pendingIntent(context)
  }

private fun AppWidgetProviderInfo.getMinSize(): IntSize =
  IntSize(
    width =
      min(
        minWidth,
        if (resizeMode and AppWidgetProviderInfo.RESIZE_HORIZONTAL != 0) {
          minResizeWidth
        } else {
          Int.MAX_VALUE
        },
      ),
    height =
      min(
        minHeight,
        if (resizeMode and AppWidgetProviderInfo.RESIZE_VERTICAL != 0) {
          minResizeHeight
        } else {
          Int.MAX_VALUE
        },
      ),
  )
