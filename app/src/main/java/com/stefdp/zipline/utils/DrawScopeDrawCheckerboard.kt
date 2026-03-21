package com.stefdp.zipline.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

fun DrawScope.drawCheckerboard(squareSize: Float = 20f) {
    val rows = (size.height / squareSize).toInt() + 1
    val cols = (size.width / squareSize).toInt() + 1
    for (row in 0 until rows) {
        for (col in 0 until cols) {
            val color = if ((row + col) % 2 == 0) Color(0xFFE0E0E0) else Color(0xFFBDBDBD)
            drawRect(
                color = color,
                topLeft = Offset(col * squareSize, row * squareSize),
                size = Size(squareSize, squareSize)
            )
        }
    }
}