package com.stefdp.zipline.utils

import androidx.compose.ui.graphics.Color

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