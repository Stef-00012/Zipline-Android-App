package com.stefdp.zipline.components.table

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.shimmerable

const val TABLE_BORDER_ALPHA = 0.3f

@Composable
fun Table(
    modifier: Modifier = Modifier,
    headers: List<TableHeaderData>,
    rows: List<TableRowData>,
    loading: Boolean,
    scrollbarConfig: TableScrollbarConfig = TableScrollbarConfig()
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .shimmerable(
                enabled = loading,
                color = MaterialTheme.colorScheme.surfaceVariant,
                height = 250.dp
            )
    ) {
        TableHeader(
            scrollState = scrollState,
            headers = headers
        )

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.outline.copy(alpha = TABLE_BORDER_ALPHA),
            thickness = 2.dp
        )

        TableContent(
            scrollState = scrollState,
            rows = rows,
            scrollbarConfig = scrollbarConfig
        )
    }
}

data class TableRowData(
    val cells: List<TableCellData>,
    val clickable: Boolean = false,
    val onClick: () -> Unit = {}
)

data class TableScrollbarConfig(
    val vertical: ScrollbarConfig = ScrollbarConfig(
        alwaysKeepScrollbar = true
    ),
    val horizontal: ScrollbarConfig = ScrollbarConfig()
)