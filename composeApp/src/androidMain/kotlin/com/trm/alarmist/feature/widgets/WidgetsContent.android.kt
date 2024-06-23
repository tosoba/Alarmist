package com.trm.alarmist.feature.widgets

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.widget_pin_unavailable_description
import alarmist.composeapp.generated.resources.widget_pin_unavailable_title
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trm.alarmist.widget.common.WidgetPinnedReceiver
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun WidgetsContent(modifier: Modifier, component: WidgetsComponent) {
  val context = LocalContext.current
  val widgetManager = AppWidgetManager.getInstance(context)

  if (!widgetManager.isRequestPinAppWidgetSupported) {
    Box(modifier = modifier) {
      WidgetPinUnavailableCard(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
    }
  } else {
    LazyColumn(
      modifier = modifier,
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    ) {
      items(widgetManager.getInstalledProvidersForPackage(context.packageName, null)) {
        WidgetInfoCard(
          providerInfo = it,
          modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        )
      }
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
private fun WidgetInfoCard(providerInfo: AppWidgetProviderInfo, modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val label = providerInfo.loadLabel(context.packageManager)
  val description =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      providerInfo.loadDescription(context)?.toString()
    } else {
      null
    }

  Card(modifier = modifier, onClick = { providerInfo.pin(context) }) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Column(modifier = Modifier.padding(end = 8.dp)) {
        Text(text = label, style = MaterialTheme.typography.titleLarge)
        description?.let { Text(text = it, style = MaterialTheme.typography.bodyMedium) }
      }

      Image(painter = painterResource(providerInfo.previewImage), contentDescription = description)
    }
  }
}

private fun AppWidgetProviderInfo.pin(context: Context) {
  AppWidgetManager.getInstance(context)
    .requestPinAppWidget(
      provider,
      null,
      PendingIntent.getBroadcast(
        context,
        0,
        Intent(context, WidgetPinnedReceiver::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
      ),
    )
}
