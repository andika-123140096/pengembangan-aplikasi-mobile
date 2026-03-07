package com.andika.newsfeedsimulator.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = DarkTeal,
    onPrimary = Cream,
    secondary = LightTeal,
    onSecondary = Cream,
    background = Cream,
    onBackground = OffBlack,
    surface = Cream,
    onSurface = OffBlack,
    outline = LightGray
)

@Composable
fun NewsFeedTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
