package com.stefdp.zipline.screens.metrics.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.utils.shimmerable
import ir.ehsannarmani.compose_charts.extensions.format

@Composable
fun Stat(
    modifier: Modifier = Modifier,
    title: String,
    firstMetric: Long?,
    lastMetric: Long?,
    difference: Double,
    infinite: Boolean = false,
    loading: Boolean = false,
    formatValue: (Long?) -> String? = { it.toString() }
) {
    Container(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxHeight(),
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

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatValue(firstMetric) ?: "Loading",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    modifier = Modifier
                        .shimmerable(
                            enabled = firstMetric == null || lastMetric == null || loading,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ),
                )

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                StatDifference(
                    difference = if (infinite) 1.0 else difference,
                    infinite = infinite,
                    loading = loading || firstMetric == null || lastMetric == null
                )
            }
        }
    }
}

@Composable
private fun StatDifference(
    modifier: Modifier = Modifier,
    difference: Double,
    infinite: Boolean = false,
    loading: Boolean = false
) {
    val diff = if (infinite) "∞" else "${difference.format(2)}%"

    val color = when {
        difference > 0 -> Color.Green
        difference < 0 -> Color.Red
        else -> Color.Gray
    }

    Row(
        modifier = modifier
            .shimmerable(
                enabled = loading,
                color = color.copy(alpha = 0.1f),
                keepBackground = true
            )
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = if (difference > 0) painterResource(R.drawable.north)
            else if (difference < 0) painterResource(R.drawable.south)
            else painterResource(R.drawable.remove),
            contentDescription = if (difference >= 0) "$diff% increase"
            else if (difference < 0) "$diff% decrease"
            else "No change",
            tint = color.copy(alpha = 0.7f),
            modifier = Modifier.size(16.dp)
        )

        Text(
            text = "$diff%",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = color.copy(alpha = 0.7f),
            )
        )
    }
}