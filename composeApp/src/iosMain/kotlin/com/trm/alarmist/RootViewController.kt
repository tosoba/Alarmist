package com.trm.alarmist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.PredictiveBackGestureIcon
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.PredictiveBackGestureOverlay
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.trm.alarmist.core.ui.theme.AppTheme
import com.trm.alarmist.feature.root.RootComponent
import com.trm.alarmist.feature.root.RootContent
import platform.UIKit.UIViewController

@OptIn(ExperimentalDecomposeApi::class)
fun rootViewController(component: RootComponent, backDispatcher: BackDispatcher): UIViewController =
  ComposeUIViewController {
    AppTheme {
      PredictiveBackGestureOverlay(
        backDispatcher = backDispatcher,
        backIcon = { progress, _ ->
          PredictiveBackGestureIcon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            progress = progress,
          )
        },
        modifier = Modifier.fillMaxSize(),
      ) {
        RootContent(component = component, modifier = Modifier.fillMaxSize())
      }
    }
  }
