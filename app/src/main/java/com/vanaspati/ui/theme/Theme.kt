package com.vanaspati.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.vanaspati.data.PreferencesStore
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember

private val GreenPrimary = Color(0xFF2E7D32)
private val GreenSecondary = Color(0xFF66BB6A)
private val GreenTertiary = Color(0xFF1B5E20)

private val LightColors: ColorScheme = lightColorScheme(
    primary = GreenPrimary,
    secondary = GreenSecondary,
    tertiary = GreenTertiary
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = GreenPrimary,
    secondary = GreenSecondary,
    tertiary = GreenTertiary
)

@Composable
fun VanaspatiTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val store = remember(context) { PreferencesStore(context) }
    val darkPref by store.darkModeFlow.collectAsState(initial = useDarkTheme)
    val colors = if (darkPref) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
