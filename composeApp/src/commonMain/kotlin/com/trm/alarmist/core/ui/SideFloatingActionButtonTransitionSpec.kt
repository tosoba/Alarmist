package com.trm.alarmist.core.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith

fun <S> AnimatedContentTransitionScope<S>.sideFloatingActionButtonTransitionSpec():
  ContentTransform =
  scaleIn(animationSpec = tween(220)).togetherWith(scaleOut(animationSpec = tween(90)))
