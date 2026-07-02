package com.example.proyectomovileslevelup.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val LevelUpColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    secondary = Secondary,
    onSecondary = OnSecondary,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
)

@Composable
fun ProyectoMovilesLevelUPTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LevelUpColorScheme,
        typography = Typography,
        content = content
    )
}
