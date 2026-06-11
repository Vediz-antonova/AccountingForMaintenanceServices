package com.vedizl.accountingformaintenanceservices.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = SurfaceLight,
    primaryContainer = PurpleLight,
    onPrimaryContainer = PurpleDark,
    secondary = BlueSecondary,
    onSecondary = SurfaceLight,
    secondaryContainer = BlueLight,
    onSecondaryContainer = BlueDark,
    tertiary = LavenderTertiary,
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = PurpleSurface,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = DividerColor,
    error = ErrorRed,
    onError = SurfaceLight,
)

@Composable
fun AccountingTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
