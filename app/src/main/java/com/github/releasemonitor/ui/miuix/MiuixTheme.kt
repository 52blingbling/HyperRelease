package com.github.releasemonitor.ui.miuix

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// 小米澎湃OS (HyperOS / MIUIX) 标准色彩系统
val MiuixOrange = Color(0xFFFF6900)
val MiuixBlue = Color(0xFF0070F0)
val MiuixGreen = Color(0xFF00C48C)
val MiuixRed = Color(0xFFFA3E3E)

val MiuixLightBackground = Color(0xFFF4F5F7)
val MiuixLightCard = Color(0xFFFFFFFF)
val MiuixLightTextPrimary = Color(0xFF111827)
val MiuixLightTextSecondary = Color(0xFF6B7280)

val MiuixDarkBackground = Color(0xFF121214)
val MiuixDarkCard = Color(0xFF1E1F23)
val MiuixDarkTextPrimary = Color(0xFFF9FAFB)
val MiuixDarkTextSecondary = Color(0xFF9CA3AF)

private val LightColorScheme = lightColorScheme(
    primary = MiuixOrange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF0E6),
    onPrimaryContainer = MiuixOrange,
    background = MiuixLightBackground,
    surface = MiuixLightCard,
    surfaceVariant = Color(0xFFF0F2F5),
    onSurface = MiuixLightTextPrimary,
    onSurfaceVariant = MiuixLightTextSecondary
)

private val DarkColorScheme = darkColorScheme(
    primary = MiuixOrange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF332014),
    onPrimaryContainer = MiuixOrange,
    background = MiuixDarkBackground,
    surface = MiuixDarkCard,
    surfaceVariant = Color(0xFF282A2E),
    onSurface = MiuixDarkTextPrimary,
    onSurfaceVariant = MiuixDarkTextSecondary
)

val MiuixShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp), // MIUI 标志性 20dp 卡片大圆角
    extraLarge = RoundedCornerShape(26.dp)
)

@Composable
fun MiuixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = MiuixShapes,
        content = content
    )
}
