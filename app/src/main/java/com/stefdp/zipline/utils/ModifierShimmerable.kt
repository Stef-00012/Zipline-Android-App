package com.stefdp.zipline.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@Composable
fun Modifier.shimmerable(
    enabled: Boolean,
    color: Color = MaterialTheme.colorScheme.surfaceVariant,
    shape: Shape = RoundedCornerShape(8.dp),
    keepBackground: Boolean = false,
    height: Dp? = null,
    width: Dp? = null
): Modifier {
    if (!enabled) {
        return if (keepBackground) this
            .background(color = color, shape = shape)
        else this
    }

    val heightModifier = if (height != null) this.height(height) else this
    val widthModifier = if (width != null) heightModifier.width(width) else heightModifier

    return widthModifier
        .shimmer()
        .background(color = color, shape = shape)
        .drawWithContent {}
}