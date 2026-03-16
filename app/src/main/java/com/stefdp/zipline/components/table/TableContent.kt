package com.stefdp.zipline.components.table

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.horizontalScrollWithScrollbar
import com.stefdp.zipline.utils.verticalLazyScrollbar

@Composable
fun TableContent(
    modifier: Modifier = Modifier,
    scrollState: ScrollState,
    rows: List<TableRowData>,
    scrollbarConfig: TableScrollbarConfig
) {
    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        modifier = modifier
            .verticalLazyScrollbar(
                listState = lazyListState,
                scrollbarConfig = scrollbarConfig.vertical
            )
            .horizontalScrollWithScrollbar(
                scrollState = scrollState,
                scrollbarConfig = scrollbarConfig.horizontal
            )
    ) {
        items(rows.size) { rowNumber ->
            val row = rows[rowNumber]

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
                    .clickable(
                        enabled = row.clickable,
                        onClick = row.onClick
                    )
            ) {
                row.cells.forEachIndexed { index, cell ->
                    Column(
                        modifier = Modifier
                            .width(cell.width)
                            .fillMaxHeight()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(cell.padding)
                                    .align(Alignment.CenterStart)
                            ) {
                                cell.content()
                            }

                            if (index < row.cells.lastIndex) {
                                VerticalDivider(
                                    modifier = Modifier.align(Alignment.CenterEnd),
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = TABLE_BORDER_ALPHA),
                                    thickness = 2.dp
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = TABLE_BORDER_ALPHA),
                            thickness = 2.dp,
                        )
                    }
                }
            }
        }
    }
}

data class TableCellData(
    val content: @Composable () -> Unit,
    val width: Dp,
    val padding: Dp = 12.dp,
)