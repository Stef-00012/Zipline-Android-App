package com.stefdp.zipline.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.glance.GlanceTheme
import androidx.glance.material3.ColorProviders

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    errorContainer = ErrorContainerDark,
    error = ErrorDark,
    onError = OnErrorDark,
    tertiary = LinkDark
)

val DarkWidgetBackground = BackgroundDark
val DarkWidgetSurface = SurfaceDark
val DarkWidgetOnSurfaceVariant = OnSurfaceVariantDark

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    errorContainer = ErrorContainerLight,
    error = ErrorLight,
    onError = OnErrorLight,
    tertiary = LinkLight
)

val LightWidgetBackground = BackgroundLight
val LightWidgetSurface = SurfaceLight
val LightWidgetOnSurfaceVariant = OnSurfaceVariantLight

@Composable
fun ZiplineTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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

@Composable
fun ZiplineWidgetTheme(
    content: @Composable () -> Unit
) {
    val colorProviders = ColorProviders(
        light = LightColorScheme,
        dark = DarkColorScheme
    )

    GlanceTheme(
        colors = colorProviders,
        content = content
    )
}