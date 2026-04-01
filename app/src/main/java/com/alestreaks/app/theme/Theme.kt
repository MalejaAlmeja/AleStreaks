package com.alestreaks.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF9AB17A),
    secondary = Color(0xFFC3CC9B),
    tertiary = Color(0xFFE4DFB5),
    surface = Color(0xFFB4D3D9),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7FA37E),
    secondary = Color(0xFF7DA3AE),
    tertiary = Color(0xFF8A6F56),
    background = Color(0xFF1F2A2E),
    surface = Color(0xFF2A3A33),
)

@Composable
fun AleStreaksTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
