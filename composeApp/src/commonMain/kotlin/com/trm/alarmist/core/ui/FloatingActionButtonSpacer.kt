package com.trm.alarmist.core.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun LazyListScope.floatingActionButtonSpacerItem() {
  item { FloatingActionButtonSpacer() }
}

@Composable
fun FloatingActionButtonSpacer() {
  Spacer(Modifier.height(72.dp))
}
