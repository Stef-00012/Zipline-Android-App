package com.stefdp.zipline.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.BASE_CORNER_RADIUS

@Composable
fun HeaderButton(
    modifier: Modifier = Modifier,
    icon: Painter,
    contentDescription: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
    iconColor: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = modifier
            .size(35.dp)
            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
            )
            .border(
                width = 2.dp,
                color = if (enabled) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
            )
            .clickable(
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            tint = if (enabled) iconColor else iconColor.copy(alpha = 0.5f),
            modifier = Modifier.size(25.dp)
        )
    }
}