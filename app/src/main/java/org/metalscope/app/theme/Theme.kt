package org.metalscope.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class MetalCustomColors(
    val background: Color,
    val surface: Color,
    val elevated: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val gold: Color,
    val silver: Color,
    val platinum: Color,
    val palladium: Color,
    val positive: Color,
    val negative: Color,
    val neutral: Color
)

val LocalMetalColors = staticCompositionLocalOf {
    MetalCustomColors(
        background = MetalBackgroundDark,
        surface = MetalSurfaceDark,
        elevated = MetalElevatedDark,
        border = MetalBorderDark,
        textPrimary = MetalTextPrimaryDark,
        textSecondary = MetalTextSecondaryDark,
        textMuted = MetalTextMutedDark,
        gold = MetalGold,
        silver = MetalSilver,
        platinum = MetalPlatinum,
        palladium = MetalPalladium,
        positive = ChangePositive,
        negative = ChangeNegative,
        neutral = ChangeNeutral
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = MetalGold,
    onPrimary = MetalBackgroundDark,
    primaryContainer = MetalElevatedDark,
    onPrimaryContainer = MetalGoldLight,
    secondary = MetalSilver,
    onSecondary = MetalBackgroundDark,
    background = MetalBackgroundDark,
    onBackground = MetalTextPrimaryDark,
    surface = MetalSurfaceDark,
    onSurface = MetalTextPrimaryDark,
    surfaceVariant = MetalElevatedDark,
    onSurfaceVariant = MetalTextSecondaryDark,
    outline = MetalBorderDark,
    error = ChangeNegative,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = MetalGoldDark,
    onPrimary = Color.White,
    primaryContainer = MetalElevatedLight,
    onPrimaryContainer = MetalGoldDark,
    secondary = MetalSilver,
    onSecondary = MetalTextPrimaryLight,
    background = MetalBackgroundLight,
    onBackground = MetalTextPrimaryLight,
    surface = MetalSurfaceLight,
    onSurface = MetalTextPrimaryLight,
    surfaceVariant = MetalElevatedLight,
    onSurfaceVariant = MetalTextSecondaryLight,
    outline = MetalBorderLight,
    error = ChangeNegative,
    onError = Color.White
)

enum class ThemeMode {
    DARK,
    LIGHT,
    SYSTEM
}

@Composable
fun MetalScopeTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme
    val customColors = if (isDark) {
        MetalCustomColors(
            background = MetalBackgroundDark,
            surface = MetalSurfaceDark,
            elevated = MetalElevatedDark,
            border = MetalBorderDark,
            textPrimary = MetalTextPrimaryDark,
            textSecondary = MetalTextSecondaryDark,
            textMuted = MetalTextMutedDark,
            gold = MetalGold,
            silver = MetalSilver,
            platinum = MetalPlatinum,
            palladium = MetalPalladium,
            positive = ChangePositive,
            negative = ChangeNegative,
            neutral = ChangeNeutral
        )
    } else {
        MetalCustomColors(
            background = MetalBackgroundLight,
            surface = MetalSurfaceLight,
            elevated = MetalElevatedLight,
            border = MetalBorderLight,
            textPrimary = MetalTextPrimaryLight,
            textSecondary = MetalTextSecondaryLight,
            textMuted = MetalTextMutedLight,
            gold = MetalGoldDark,
            silver = Color(0xFF64748B),
            platinum = Color(0xFF475569),
            palladium = Color(0xFF475569),
            positive = ChangePositive,
            negative = ChangeNegative,
            neutral = ChangeNeutral
        )
    }

    CompositionLocalProvider(LocalMetalColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = MetalTypography,
            content = content
        )
    }
}

object MetalTheme {
    val colors: MetalCustomColors
        @Composable
        get() = LocalMetalColors.current
}
