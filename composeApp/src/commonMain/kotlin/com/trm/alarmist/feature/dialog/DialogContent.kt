package com.trm.alarmist.feature.dialog

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.confirm
import alarmist.composeapp.generated.resources.dismiss
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource

@Composable
fun DialogContent(
  component: DialogComponent,
  onConfirmClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  AlertDialog(
    onDismissRequest = component.onDismiss,
    title = { Text(text = component.title) },
    text = { Text(text = component.message) },
    confirmButton = {
      TextButton(
        onClick = {
          component.onConfirm()
          onConfirmClick()
        }
      ) {
        Text(stringResource(Res.string.confirm))
      }
    },
    dismissButton = {
      TextButton(onClick = component.onDismiss) { Text(stringResource(Res.string.dismiss)) }
    },
    modifier = modifier,
  )
}
