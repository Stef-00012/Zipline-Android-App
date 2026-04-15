package com.stefdp.zipline.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.R

@Composable
fun MoreActionsButton(
    modifier: Modifier = Modifier,
    items: List<MoreActionsMenuItem>,
    enabled: Boolean = true,
    menuColor: Color? = null,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Row {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = menuColor ?: MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
        ) {
            items.forEach { item ->
                val textColor = item.labelColor ?: MaterialTheme.colorScheme.onSurfaceVariant
                val iconColor = item.iconColor ?: MaterialTheme.colorScheme.onSurfaceVariant

                DropdownMenuItem(
                    text = {
                        Text(
                            text = item.label,
                            color = if (enabled && item.enabled)
                                textColor
                            else
                                textColor.copy(alpha = 0.5f)
                        )
                    },
                    onClick = {
                        item.onClick()
                        expanded = false
                    },
                    enabled = item.enabled,
                    leadingIcon = {
                        Icon(
                            painter = item.icon,
                            contentDescription = item.iconDescription,
                            tint = if (enabled && item.enabled)
                                iconColor
                            else
                                iconColor.copy(alpha = 0.5f)
                        )
                    }
                )
            }
        }

        LargeDisplayIconButton(
            onClick = { expanded = true },
            icon = painterResource(R.drawable.more_horiz),
            iconContentDescription = "More options",
            enabled = enabled
        )

//        Box(
//            modifier = modifier
//                .size(35.dp)
//                .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
//                .background(
//                    color = Color.Transparent,
//                    shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
//                )
//                .clickable(
//                    enabled = enabled,
//                    onClick = {  }
//                ),
//            contentAlignment = Alignment.Center
//        ) {
//            Icon(
//                painter = painterResource(R.drawable.more_horiz),
//                contentDescription = "More options",
//                tint = if (enabled)
//                    MaterialTheme.colorScheme.primary
//                else
//                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
//                modifier = Modifier.size(25.dp)
//            )
//        }
    }
}

data class MoreActionsMenuItem(
    val label: String,
    val labelColor: Color? = null,
    val onClick: () -> Unit,
    val icon: Painter,
    val iconColor: Color? = null,
    val iconDescription: String,
    val enabled: Boolean = true
)