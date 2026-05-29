package com.trm.alarmist.core.ui.theme

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.space_grotesk
import alarmist.composeapp.generated.resources.tektur
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font

@Composable
fun appTypography() =
  with(Typography()) {
    val tektur =
      FontFamily(
        Font(Res.font.tektur, weight = FontWeight.Light),
        Font(Res.font.tektur, weight = FontWeight.Normal),
        Font(Res.font.tektur, weight = FontWeight.Medium),
        Font(Res.font.tektur, weight = FontWeight.Bold),
      )
    val spaceGrotesk =
      FontFamily(
        Font(Res.font.space_grotesk, weight = FontWeight.Light),
        Font(Res.font.space_grotesk, weight = FontWeight.Normal),
        Font(Res.font.space_grotesk, weight = FontWeight.Medium),
        Font(Res.font.space_grotesk, weight = FontWeight.Bold),
      )
    copy(
      displayLarge = displayLarge.copy(fontFamily = tektur),
      displayMedium = displayMedium.copy(fontFamily = tektur),
      displaySmall = displaySmall.copy(fontFamily = tektur),
      headlineLarge = headlineLarge.copy(fontFamily = tektur),
      headlineMedium = headlineMedium.copy(fontFamily = tektur),
      headlineSmall = headlineSmall.copy(fontFamily = tektur),
      titleLarge = titleLarge.copy(fontFamily = spaceGrotesk),
      titleMedium = titleMedium.copy(fontFamily = spaceGrotesk),
      titleSmall = titleSmall.copy(fontFamily = spaceGrotesk),
      bodyLarge = bodyLarge.copy(fontFamily = spaceGrotesk),
      bodyMedium = bodyMedium.copy(fontFamily = spaceGrotesk),
      bodySmall = bodySmall.copy(fontFamily = spaceGrotesk),
      labelLarge = labelLarge.copy(fontFamily = spaceGrotesk),
      labelMedium = labelMedium.copy(fontFamily = spaceGrotesk),
      labelSmall = labelSmall.copy(fontFamily = spaceGrotesk),
    )
  }
