package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
  primary = FlipkartBlue,
  onPrimary = Color.White,
  primaryContainer = FlipkartDarkBlue,
  secondary = MeeshoPink,
  onSecondary = Color.White,
  tertiary = DealAmber,
  background = SurfaceBgDark,
  surface = CardBgDark,
  onBackground = TextPrimaryDark,
  onSurface = TextPrimaryDark,
  surfaceVariant = Color(0xFF2C2C2C),
  onSurfaceVariant = TextSecondaryDark
)

private val LightColorScheme = lightColorScheme(
  primary = FlipkartBlue,
  onPrimary = Color.White,
  primaryContainer = FlipkartLightBlue,
  secondary = MeeshoPink,
  onSecondary = Color.White,
  tertiary = DealAmber,
  background = SurfaceBgLight,
  surface = CardBgLight,
  onBackground = TextPrimaryLight,
  onSurface = TextPrimaryLight,
  surfaceVariant = Color(0xFFF7F8FA),
  onSurfaceVariant = TextSecondaryLight
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = FlipkartBlue.toArgb()
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
