package com.trm.alarmist.feature.sheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
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
    ModalBottomSheet(onDismissRequest = onDismissRequest, sheetState = state) {
      when (child) {
        is BottomSheetChild.Alarm -> {
          AlarmContent(
            component = child.component,
            onDeleteActionClick = onDeleteActionClick,
            onBackClick = onBackClick,
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
            onConfirmClick = {
              child.component.feature.onConfirmClick()?.invokeOnCompletion { onConfirmCompletion() }
            },
          )
        }
      }
    }
  }
}
