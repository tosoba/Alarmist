package com.trm.alarmist.feature.sheet

import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.router.slot.ChildSlot
import com.trm.alarmist.feature.alarm.AlarmContent
import com.trm.alarmist.feature.group.GroupContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetContent(
  state: SheetState,
  slot: ChildSlot<*, BottomSheetChild>,
  onDismissRequest: () -> Unit,
  onDeleteActionClick: () -> Unit,
  onBackClick: () -> Unit,
  onConfirmCompletion: () -> Unit,
) {
  slot.child?.instance?.let { child ->
    var sheetGesturesEnabled by remember(child) { mutableStateOf(true) }

    ModalBottomSheet(
      onDismissRequest = onDismissRequest,
      sheetState = state,
      sheetGesturesEnabled = sheetGesturesEnabled,
      scrimColor = BottomSheetDefaults.ContainerColor,
    ) {
      when (child) {
        is BottomSheetChild.Alarm -> {
          AlarmContent(
            component = child.component,
            onDeleteActionClick = onDeleteActionClick,
            onBackClick = onBackClick,
            onCallScrollBackwardChange = { sheetGesturesEnabled = !it },
            onConfirmClick = {
              child.component.feature.onConfirmClick().invokeOnCompletion { onConfirmCompletion() }
            },
          )
        }
        is BottomSheetChild.Group -> {
          GroupContent(
            component = child.component,
            onDeleteActionClick = onDeleteActionClick,
            onBackClick = onBackClick,
            onCallScrollBackwardChange = { sheetGesturesEnabled = !it },
            onConfirmClick = {
              child.component.feature.onConfirmClick()?.invokeOnCompletion { onConfirmCompletion() }
            },
          )
        }
      }
    }
  }
}
