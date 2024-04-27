package com.trm.alarmist.feature.alarm.sound

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.default
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun alarmSoundTitle(id: String?): String = stringResource(Res.string.default)
