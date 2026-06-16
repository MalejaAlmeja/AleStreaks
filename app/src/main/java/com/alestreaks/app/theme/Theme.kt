package com.alestreaks.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.alestreaks.app.R

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

private val Nunito = FontFamily(
    Font(R.font.nunito_variable, FontWeight.Normal),
    Font(R.font.nunito_variable, FontWeight.SemiBold),
    Font(R.font.nunito_variable, FontWeight.Bold),
    Font(R.font.nunito_variable, FontWeight.Black),
)

private val AppTypography = Typography().run {
    copy(
        headlineLarge = headlineLarge.copy(fontFamily = Nunito, fontWeight = FontWeight.Bold),
        headlineMedium = headlineMedium.copy(fontFamily = Nunito, fontWeight = FontWeight.Bold),
        headlineSmall = headlineSmall.copy(fontFamily = Nunito, fontWeight = FontWeight.Bold),
        titleLarge = titleLarge.copy(fontFamily = Nunito, fontWeight = FontWeight.SemiBold),
        titleMedium = titleMedium.copy(fontFamily = Nunito, fontWeight = FontWeight.SemiBold),
        bodyLarge = bodyLarge.copy(fontFamily = Nunito),
        bodyMedium = bodyMedium.copy(fontFamily = Nunito),
        labelLarge = labelLarge.copy(fontFamily = Nunito, fontWeight = FontWeight.SemiBold),
    )
}

@Composable
fun AleStreaksTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content,
    )
}
