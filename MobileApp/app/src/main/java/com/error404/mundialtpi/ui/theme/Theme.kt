package com.error404.mundialtpi.ui.theme

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
    primary = WebPrimary,
    secondary = WebAccent,
    tertiary = WebPrimaryDark,
    background = WebBgDark,
    surface = WebSurfaceDark,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = WebTextDark,
    onSurface = WebTextDark,
    error = WebError
)

private val LightColorScheme = lightColorScheme(
    primary = WebPrimary,
    secondary = WebAccent,
    tertiary = WebPrimaryDark,
    background = WebBgLight,
    surface = WebSurfaceLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = WebTextLight,
    onSurface = WebTextLight,
    error = WebError
)

@Composable
fun MundialTPITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            @Suppress("DEPRECATION")
            window.statusBarColor = colorScheme.tertiary.toArgb()

            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}