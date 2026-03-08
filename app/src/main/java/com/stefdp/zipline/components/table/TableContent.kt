package com.stefdp.zipline.components.table

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.verticalLazyScrollbar

@Composable
fun TableContent(
    modifier: Modifier = Modifier,
    scrollState: ScrollState,
    rows: List<List<TableCellData>>,
) {
    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        modifier = modifier.fillMaxSize().verticalLazyScrollbar(
            listState = lazyListState,
            scrollbarConfig = ScrollbarConfig(alwaysKeepScrollbar = true)
        )
    ) {
        items(rows.size) { rowNumber ->
            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)
                    .height(IntrinsicSize.Max)
            ) {
                rows[rowNumber].forEach { cell ->
                    cell()

                    VerticalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = TABLE_BORDER_ALPHA),
                        thickness = 2.dp
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = TABLE_BORDER_ALPHA),
                thickness = 2.dp,
            )
        }
    }
}

typealias TableCellData = @Composable () -> Unit