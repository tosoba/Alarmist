package com.trm.alarmist.core.common.util

import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

@OptIn(ExperimentalResourceApi::class)
fun getStringBlocking(resource: StringResource): String = runBlocking { getString(resource) }
