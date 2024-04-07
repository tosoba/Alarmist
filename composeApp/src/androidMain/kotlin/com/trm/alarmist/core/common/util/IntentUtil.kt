package com.trm.alarmist.core.common.util

import android.content.Intent
import android.os.Build
import java.io.Serializable

internal inline fun <reified T : Serializable> Intent.getSerializable(name: String): T? =
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    getSerializableExtra(name, T::class.java)
  } else {
    getSerializableExtra(name) as? T
  }
