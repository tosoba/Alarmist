package com.trm.alarmist.core.common.util

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit

fun Density.toSp(dp: Dp): TextUnit = dp.toSp()

fun Density.toPx(dp: Dp): Float = dp.toPx()

fun Density.roundToPx(dp: Dp): Int = dp.roundToPx()

// TEXT UNIT
fun Density.toDp(sp: TextUnit): Dp = sp.toDp()

fun Density.toPx(sp: TextUnit): Float = sp.toPx()

fun Density.roundToPx(sp: TextUnit): Int = sp.roundToPx()

// FLOAT
fun Density.toDp(px: Float): Dp = px.toDp()

fun Density.toSp(px: Float): TextUnit = px.toSp()

// INT
fun Density.toDp(px: Int): Dp = px.toDp()

fun Density.toSp(px: Int): TextUnit = px.toSp()

// SIZE
fun Density.toIntSize(dpSize: DpSize): IntSize =
  IntSize(dpSize.width.roundToPx(), dpSize.height.roundToPx())
