package com.stefdp.zipline.widgets

import androidx.compose.ui.unit.dp

const val CELL_WIDTH = 91
const val CELL_HEIGHT = 126

val cornerRadius = 16.dp

const val WIDGET_IMAGE_SIZE = 300

data class WidgetConfigSize(
    val width: Int,
    val height: Int,
    val label: String,
    val default: Boolean = false
)