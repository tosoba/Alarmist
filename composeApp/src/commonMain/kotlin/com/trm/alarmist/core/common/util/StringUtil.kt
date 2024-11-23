package com.trm.alarmist.core.common.util

import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

fun getStringBlocking(resource: StringResource): String = runBlocking { getString(resource) }

fun getStringBlocking(resource: StringResource, vararg formatArgs: Any): String = runBlocking {
  getString(resource, formatArgs)
}
