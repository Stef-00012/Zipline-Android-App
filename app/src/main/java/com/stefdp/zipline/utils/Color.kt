package com.stefdp.zipline.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

fun colorHash(string: String): Color {
    var hash = 0

    for (char in string) {
        hash = char.code + ((hash shl 5) - hash)
    }

    val color = StringBuilder()

    for (i in 0..<3) {
        val value = (hash shr (i * 8)) and 0xff
        color.append(
            value
                .toString(16)
                .padStart(2, '0')
                .takeLast(2)
        )
    }

    return Color(("FF$color").toLong(16).toInt())
}

fun Color.toHex(withAlpha: Boolean = false): String {
    val argb = this.toArgb()
    val red = (argb shr 16) and 0xFF
    val green = (argb shr 8) and 0xFF
    val blue = argb and 0xFF

    return if (withAlpha) {
        val alpha = (argb shr 24) and 0xFF

        String.format("#%02x%02x%02x%02x", alpha, red, green, blue)
    } else {
        String.format("#%02x%02x%02x", red, green, blue)
    }
}