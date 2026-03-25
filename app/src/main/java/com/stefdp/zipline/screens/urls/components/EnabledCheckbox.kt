package com.stefdp.zipline.screens.urls.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.R

@Composable
fun EnabledCheckbox(
    enabled: Boolean
) {
    Box(
        Modifier
            .size(20.dp)
            .clip(RoundedCornerShape(4.dp))
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(4.dp)
            )
            .background(
                if (enabled)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant
            )
    ) {
        if (enabled) {
            Icon(
                painter = painterResource(R.drawable.check),
                contentDescription = "Checkbox enabled",
            )
        }
    }
}