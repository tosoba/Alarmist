package com.trm.alarmist.feature.root

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.widgets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun RootDrawerWidgetsItem(isSelected: Boolean, onClick: () -> Unit) {
  NavigationDrawerItem(
    modifier = Modifier.padding(horizontal = 12.dp),
    icon = {
      Icon(
        imageVector = Icons.Default.Widgets,
        contentDescription = stringResource(Res.string.widgets),
      )
    },
    label = {
      Text(
        text = stringResource(Res.string.widgets),
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
      )
    },
    selected = isSelected,
    onClick = onClick,
  )
}
