package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = BrandAccentBlue,
    onPrimary = Color.White,
    primaryContainer = BrandDarkSurfaceVariant,
    onPrimaryContainer = Color.White,
    secondary = ProfitGreenDark,
    onSecondary = Color.Black,
    secondaryContainer = ProfitGreenContainerDark,
    onSecondaryContainer = ProfitGreenDark,
    background = BrandDarkBg,
    onBackground = BrandDarkText,
    surface = BrandDarkSurface,
    onSurface = BrandDarkText,
    surfaceVariant = BrandDarkSurfaceVariant,
    onSurfaceVariant = BrandDarkMuted,
    outline = BrandDarkLine
  )

private val LightColorScheme =
  lightColorScheme(
    primary = BrandAccentBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEAF1FB),
    onPrimaryContainer = BrandDarkBlue,
    secondary = ProfitGreen,
    onSecondary = Color.White,
    secondaryContainer = ProfitGreenContainer,
    onSecondaryContainer = ProfitGreen,
    background = BrandLightBg,
    onBackground = BrandTextDark,
    surface = BrandSurface,
    onSurface = BrandTextDark,
    surfaceVariant = Color(0xFFF1F5FA),
    onSurfaceVariant = BrandMuted,
    outline = BrandLine
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep consistent branding colors by default
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
