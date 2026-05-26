package com.civicsidekick.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = SurfaceWhite,
    primaryContainer = PrimaryLight,
    secondary = Secondary,
    onSecondary = SurfaceWhite,
    tertiary = Tertiary,
    onTertiary = SurfaceWhite,
    background = Background,
    onBackground = Neutral,
    surface = SurfaceWhite,
    onSurface = Neutral,
    surfaceVariant = NeutralLightest,
    onSurfaceVariant = NeutralLight,
    outline = NeutralLightest,
    error = Error,
    onError = SurfaceWhite
)

@Composable
fun CivicSidekickTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
