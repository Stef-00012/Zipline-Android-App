package com.stefdp.zipline.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xff343fa2),
    onPrimary = Color(0xffffffff),
    secondary = Color(0xff565aad),
    onSecondary = Color(0xffffffff),
    background = Color(0xff0c101c),
    onBackground = Color(0xffffffff),
    surface = Color(0xff141722),
    onSurface = Color(0xffffffff),
    surfaceVariant = Color(0xff1e222b),
    onSurfaceVariant = Color(0xffffffff),
    outline = Color(0xff262f47),
    errorContainer = Color(0xffd83333),
    error = Color(0xfff25151)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xff424aa8),
    onPrimary = Color(0xffffffff),
    secondary = Color(0xff424aa7),
    onSecondary = Color(0xffffffff),
    background = Color(0xfff2f2f2),
    onBackground = Color(0xff010101),
    surface = Color(0xfff7f7f7),
    onSurface = Color(0xff010101),
    surfaceVariant = Color(0xfff7f7f7),
    onSurfaceVariant = Color(0xff010101),
    outline = Color(0xffd6dade),
    errorContainer = Color(0xffe73f3f),
    error = Color(0xfff25151)
)

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