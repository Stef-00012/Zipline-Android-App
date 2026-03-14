package com.stefdp.zipline.components.largefiledisplay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun Stat(
    label: String,
    value: String,
    icon: Painter,
    iconContentDescription: String,
) {
    Spacer()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 4.dp
            )
    ) {
        Icon(
            painter = icon,
            contentDescription = iconContentDescription,
            modifier = Modifier.size(40.dp)
        )

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Column {
            Text(
                text = label,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            )
        }
    }
}