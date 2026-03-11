package com.stefdp.zipline.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.stefdp.zipline.network.models.Tag
import androidx.core.graphics.toColorInt
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.ui.theme.Black
import com.stefdp.zipline.ui.theme.White

@Composable
fun Tag(
    tag: Tag,
    enabled: Boolean = true
) {
    val color = Color(
        tag.color.toColorInt()
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
            .background(
                if (enabled) color else color.copy(alpha = 0.5f)
            )
            .padding(
                horizontal = 4.dp,
                vertical = 2.dp
            )
    ) {
        val luminance = ColorUtils.calculateLuminance(color.toArgb())
        val textColor = if (luminance > 0.5) Black else White

        Text(
            text = tag.name,
            color = if (enabled) textColor else textColor.copy(alpha = 0.5f),
        )
    }
}