package com.stefdp.zipline.components.table

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.utils.shimmerable

const val TABLE_BORDER_ALPHA = 0.3f

@Composable
fun Table(
    modifier: Modifier = Modifier,
    headers: List<TableHeaderData>,
    rows: List<List<TableCellData>>,
    loading: Boolean
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .shimmerable(
                enabled = loading,
                color = MaterialTheme.colorScheme.surface,
                keepBackground = true
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
            rows = rows
        )
    }
}