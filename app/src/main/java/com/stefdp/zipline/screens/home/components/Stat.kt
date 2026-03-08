package com.stefdp.zipline.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.utils.shimmerable

@Composable
fun Stat(
    modifier: Modifier = Modifier,
    title: String,
    value: Any?,
    loading: Boolean = false
) {
    Container(
        modifier = modifier
//            .padding(12.dp)
            .fillMaxHeight()
            .fillMaxHeight(),
//            .background(
//                color = MaterialTheme.colorScheme.surfaceVariant,
//                shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
//            ),
        scrollable = false,
        border = false
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "$value",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .shimmerable(
                        enabled = value == null || loading,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ),
            )
        }
    }
}