package com.trm.alarmist.core.common.util

import android.content.BroadcastReceiver
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun BroadcastReceiver.launch(
  context: CoroutineContext = EmptyCoroutineContext,
  block: suspend CoroutineScope.() -> Unit,
) {
  val pendingResult = goAsync()
  @OptIn(DelicateCoroutinesApi::class) // Must run globally; there's no teardown callback.
  GlobalScope.launch(context) {
    try {
      block()
    } finally {
      pendingResult.finish()
    }
  }
}
