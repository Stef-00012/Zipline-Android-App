package com.stefdp.zipline.components.table

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TableHeader(
    modifier: Modifier = Modifier,
    scrollState: ScrollState,
    headers: List<TableHeaderData>,
) {
    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .height(IntrinsicSize.Max)
    ) {
        headers.forEachIndexed { index, header ->
            if (index < headers.lastIndex) {
                header()

                VerticalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = TABLE_BORDER_ALPHA),
                    thickness = 2.dp,
                )
            } else {
                header()
            }
        }
    }
}

typealias TableHeaderData = @Composable () -> Unit