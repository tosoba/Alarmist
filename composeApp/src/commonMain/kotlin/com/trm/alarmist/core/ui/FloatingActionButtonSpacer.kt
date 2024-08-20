package com.trm.alarmist.core.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun LazyGridScope.floatingActionButtonSpacerItem(key: Any? = null) {
  item(span = { GridItemSpan(maxLineSpan) }, key = key) { FloatingActionButtonSpacer() }
}

@Composable
fun FloatingActionButtonSpacer() {
  Spacer(Modifier.height(72.dp))
}
