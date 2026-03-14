package com.stefdp.zipline.components.largefiledisplay

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.BASE_CORNER_RADIUS

@Composable
internal fun IconButton(
    icon: Painter,
    iconContentDescription: String,
    color: Color,
    iconColor: Color,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
            .size(35.dp)
            .background(
                if (enabled) color else color.copy(alpha = 0.5f),
            ),
        shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = iconContentDescription,
            tint = if (enabled) iconColor else iconColor.copy(alpha = 0.5f),
            modifier = Modifier.size(25.dp)
        )
    }
}