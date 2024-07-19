package com.trm.alarmist

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.handleDeepLink
import com.trm.alarmist.core.ui.theme.AppTheme
import com.trm.alarmist.feature.root.DefaultRootComponent
import com.trm.alarmist.feature.root.RootContent
import com.trm.alarmist.feature.root.RootStartMode

class MainActivity : ComponentActivity() {
  @OptIn(ExperimentalDecomposeApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val component =
      DefaultRootComponent(
        componentContext = defaultComponentContext(),
        startMode = handleDeepLink(::rootStartModeFrom) ?: RootStartMode.Normal,
      )

    setContent {
      AppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
          RootContent(component = component, modifier = Modifier.fillMaxSize())
        }
      }
    }
  }
}

private fun Context.rootStartModeFrom(uri: Uri?): RootStartMode =
  when {
    uri?.path?.contains(getString(R.string.deeplink_path_add_alarm)) == true -> {
      RootStartMode.AddAlarm
    }
    uri?.path?.contains(getString(R.string.deeplink_path_edit_alarm)) == true -> {
      RootStartMode.EditAlarm(uri.pathSegments.last().toLong())
    }
    uri?.path?.contains(getString(R.string.deeplink_path_stopwatch)) == true -> {
      RootStartMode.Stopwatch
    }
    else -> {
      RootStartMode.Normal
    }
  }
