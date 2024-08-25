package com.trm.alarmist.feature.widgets

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.widget_pin_unavailable_description
import alarmist.composeapp.generated.resources.widget_pin_unavailable_title
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.common.util.pinWidget
import com.trm.alarmist.core.common.util.widgetReceiverComponentName
import com.trm.alarmist.core.ui.BottomGradientBackground
import com.trm.alarmist.core.ui.TopGradientBackground
import com.trm.alarmist.widget.common.system.WidgetPinnedReceiver
import com.trm.alarmist.widget.group.GroupWidgetConfigActivity
import com.trm.alarmist.widget.group.GroupWidgetReceiver
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun WidgetsContent(modifier: Modifier, component: WidgetsComponent) {
  val context = LocalContext.current
  val widgetManager = AppWidgetManager.getInstance(context)

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

@Composable
private fun WidgetsGrid(
  isRequestPinAppWidgetSupported: Boolean,
  providers: List<AppWidgetProviderInfo>,
  modifier: Modifier = Modifier,
) {
  Scaffold(modifier = modifier) {
    Box(modifier = Modifier.fillMaxSize()) {
      LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 250.dp),
        contentPadding =
          PaddingValues(
            top = 0.dp,
            bottom = it.calculateBottomPadding(),
            start = it.calculateStartPadding(LocalLayoutDirection.current) + 8.dp,
            end = it.calculateStartPadding(LocalLayoutDirection.current) + 8.dp,
          ),
      ) {
        if (!isRequestPinAppWidgetSupported) {
          item { WidgetPinUnavailableCard(modifier = Modifier.fillMaxWidth().padding(8.dp)) }
        } else {
          items(providers) { provider ->
            WidgetInfoCard(provider = provider, modifier = Modifier.fillMaxWidth().padding(8.dp))
          }
        }
      }

      TopGradientBackground()
      BottomGradientBackground()
    }
  }
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
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(text = stringResource(Res.string.widget_pin_unavailable_description))
    }
  }
}

@Composable
private fun WidgetInfoCard(provider: AppWidgetProviderInfo, modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val description =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      provider.loadDescription(context)?.toString()
    } else {
      null
    }

  val scope = rememberCoroutineScope()
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
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
        )
        description?.let {
          Spacer(modifier = Modifier.height(4.dp))
          Text(text = it, style = MaterialTheme.typography.bodyMedium)
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      Image(
        painter = painterResource(provider.previewImage),
        contentDescription = description,
        modifier = Modifier.weight(.5f),
      )
    }
  }
}

private fun AppWidgetProviderInfo.pinCallback(context: Context): PendingIntent =
  if (provider == context.widgetReceiverComponentName<GroupWidgetReceiver>()) {
    GroupWidgetConfigActivity.pendingIntent(context)
  } else {
    WidgetPinnedReceiver.pendingIntent(context)
  }
