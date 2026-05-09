package com.trm.alarmist.app

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.defaultComponentContext
import com.trm.alarmist.core.ui.theme.AppTheme
import com.trm.alarmist.feature.root.DefaultRootComponent
import com.trm.alarmist.feature.root.RootContent
import com.trm.alarmist.feature.root.RootStartMode

class MainActivity : ComponentActivity() {
  private val component by
    lazy(LazyThreadSafetyMode.NONE) {
      DefaultRootComponent(
        componentContext = defaultComponentContext(),
        startMode = intent.rootStartMode() ?: RootStartMode.Normal,
      )
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)

    setContent {
      AppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
          RootContent(
            component = component,
            modifier =
              Modifier.fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
          )
        }
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    component.onStartModeChanged(intent.rootStartMode())
  }
}

private fun Intent.rootStartMode(): RootStartMode? =
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    getParcelableExtra(RootStartMode.EXTRA_KEY, RootStartMode::class.java)
  } else {
    @Suppress("DEPRECATION") getParcelableExtra(RootStartMode.EXTRA_KEY)
  }
