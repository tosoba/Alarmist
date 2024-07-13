package com.trm.alarmist.widget.group

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.add
import alarmist.composeapp.generated.resources.cancel
import alarmist.composeapp.generated.resources.new_group_widget
import alarmist.composeapp.generated.resources.ok
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.essenty.backhandler.BackHandler
import com.arkivanov.essenty.lifecycle.asEssentyLifecycle
import com.trm.alarmist.core.ui.theme.AppTheme
import com.trm.alarmist.feature.dialog.DialogContent
import com.trm.alarmist.feature.sheet.BottomSheetContent
import com.trm.alarmist.feature.widget.config.group.DefaultGroupWidgetConfigComponent
import com.trm.alarmist.feature.widget.config.group.GroupWidgetConfigContent
import com.trm.alarmist.widget.common.util.showWidgetPinnedToast
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.core.component.KoinComponent

class AlarmGroupWidgetConfigActivity : ComponentActivity(), KoinComponent {
  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setResult(RESULT_CANCELED)

    val widgetId =
      intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
    if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) finish()

    val isPinned = intent.getBooleanExtra(EXTRA_IS_PINNED, false)

    val component =
      DefaultGroupWidgetConfigComponent(
        componentContext =
          DefaultComponentContext(
            lifecycle = lifecycle.asEssentyLifecycle(),
            backHandler =
              BackHandler(onBackPressedDispatcher = onBackPressedDispatcher, lifecycleOwner = this),
          )
      )

    setContent {
      AppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
          Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
              CenterAlignedTopAppBar(
                title = { Text(text = stringResource(Res.string.new_group_widget)) },
                navigationIcon = {
                  IconButton(onClick = { finish() }) {
                    Icon(
                      imageVector = Icons.Default.Close,
                      contentDescription = stringResource(Res.string.cancel),
                    )
                  }
                },
              )
            },
            bottomBar = {
              Row(
                modifier =
                  Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(16.dp)
              ) {
                if (!isPinned) {
                  OutlinedButton(onClick = { finish() }, modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(Res.string.cancel))
                  }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                  onClick = {
                    component.feature.onConfirmClick(widgetId)

                    showWidgetPinnedToast()

                    setResult(
                      RESULT_OK,
                      Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId),
                    )
                    finish()
                  },
                  modifier = Modifier.weight(1f),
                ) {
                  Text(text = stringResource(Res.string.ok))
                }
              }
            },
            floatingActionButton = {
              FloatingActionButton(onClick = component::onAddGroupClick) {
                Icon(
                  imageVector = Icons.Default.Add,
                  contentDescription = stringResource(Res.string.add),
                )
              }
            },
          ) {
            val state by component.feature.state.collectAsState()

            GroupWidgetConfigContent(
              state = state,
              modifier = Modifier.fillMaxSize().padding(it),
              onExpandGroup = component.feature::onExpandGroup,
              onCollapseGroup = component.feature::onCollapseGroup,
              onChooseGroup = component.feature::onChooseGroup,
              onEditGroupClick = component::onEditGroupClick,
            )

            val bottomSheet by component.bottomSheet.subscribeAsState()
            val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            val scope = rememberCoroutineScope()

            fun hideBottomSheet() {
              scope
                .launch { bottomSheetState.hide() }
                .invokeOnCompletion { component.onBottomSheetDismissRequest() }
            }

            BottomSheetContent(
              state = bottomSheetState,
              slot = bottomSheet,
              onDismissRequest = component::onBottomSheetDismissRequest,
              onDeleteActionClick = component.deleteDialog::onDelete,
              onBackClick = ::hideBottomSheet,
              onConfirmCompletion = ::hideBottomSheet,
            )

            val dialog by component.deleteDialog.component.subscribeAsState()
            dialog.child?.instance?.let { dialogComponent ->
              DialogContent(component = dialogComponent, onConfirmClick = ::hideBottomSheet)
            }
          }
        }
      }
    }
  }

  companion object {
    private const val EXTRA_IS_PINNED = "IS_PINNED"

    fun pendingIntent(context: Context): PendingIntent =
      PendingIntent.getActivity(
        context,
        0,
        Intent(context, AlarmGroupWidgetConfigActivity::class.java).putExtra(EXTRA_IS_PINNED, true),
        // must have FLAG_MUTABLE - otherwise EXTRA_APPWIDGET_ID will not be set
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
      )
  }
}
