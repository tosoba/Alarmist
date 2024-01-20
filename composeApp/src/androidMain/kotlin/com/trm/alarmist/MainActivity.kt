package com.trm.alarmist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.defaultComponentContext
import com.trm.alarmist.feature.root.DefaultRootComponent
import com.trm.alarmist.feature.root.RootContent

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
          RootContent(
              component = DefaultRootComponent(componentContext = defaultComponentContext()),
              modifier = Modifier.fillMaxSize(),
          )
        }
      }
    }
  }
}
