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

private val DarkColorScheme = darkColorScheme(
    primary = AccentGold,
    onPrimary = NavyDeep,
    primaryContainer = DarkSlate,
    onPrimaryContainer = TextPrimaryDark,
    secondary = RoyalBlue,
    onSecondary = TextPrimaryDark,
    tertiary = VibrantCyan,
    onTertiary = NavyDeep,
    background = NavyDeep,
    onBackground = TextPrimaryDark,
    surface = CardBackgroundDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSlate,
    onSurfaceVariant = TextSecondaryDark,
    outline = SurfaceBorderDark,
    error = CrimsonError,
    onError = TextPrimaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = DarkSlate,
    onPrimary = CardBackgroundLight,
    primaryContainer = CardBackgroundLight,
    onPrimaryContainer = TextPrimaryLight,
    secondary = RoyalBlue,
    onSecondary = CardBackgroundLight,
    tertiary = AccentGold,
    onTertiary = NavyDeep,
    background = Color(0xFFF1F5F9),
    onBackground = TextPrimaryLight,
    surface = CardBackgroundLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = TextSecondaryLight,
    outline = SurfaceBorderLight,
    error = CrimsonError,
    onError = CardBackgroundLight
)

@Composable
fun OmniControlTheme(
    darkTheme: Boolean = true, // Default to dark luxury theme for enterprise UI
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
