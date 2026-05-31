package com.trm.alarmist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.defaultComponentContext
import com.trm.alarmist.core.common.util.getParcelable
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
        RootContent(
          component = component,
          modifier = Modifier.fillMaxSize(),
        )
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    component.onStartModeChanged(intent.rootStartMode())
  }

  companion object {
    fun intent(context: Context, startMode: RootStartMode): Intent =
      Intent(context, MainActivity::class.java).putExtra(RootStartMode.EXTRA_KEY, startMode)
  }
}

private fun Intent.rootStartMode(): RootStartMode? = getParcelable(RootStartMode.EXTRA_KEY)
