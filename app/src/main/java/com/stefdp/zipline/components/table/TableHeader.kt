package com.stefdp.zipline.components.table

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.gson.annotations.SerializedName
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.R
import com.stefdp.zipline.utils.SortOrder

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
            @Composable
            fun HeaderOptions() {
                if (header.sortable) {
                    val sortIcon = when (header.sortOrder) {
                        SortOrder.ASC -> painterResource(R.drawable.north)
                        SortOrder.DESC -> painterResource(R.drawable.south)
                        SortOrder.UNSPECIFIED -> painterResource(R.drawable.sort)
                    }

                    val sortDescription = when (header.sortOrder) {
                        SortOrder.ASC -> "Sorted ascending"
                        SortOrder.DESC -> "Sorted descending"
                        SortOrder.UNSPECIFIED -> "Not sorted"
                    }

                    Spacer(modifier.weight(1f))

                    Icon(
                        painter = sortIcon,
                        contentDescription = sortDescription,
                        modifier = modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable(
                                onClick = header.onSortChanged
                            )
                    )
                }

                if (header.searchable) {
                    if (!header.sortable) {
                        Spacer(modifier.weight(1f))
                    } else {
                        Spacer(modifier.width(4.dp))
                    }

                    Box(
                        modifier = Modifier
                            .size(25.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable(
                                onClick = header.onSearchClick,
                                enabled = header.searchEnabled
                            )
                            .padding(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.filter_alt),
                            contentDescription = "Search by ${header.name}",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.width(header.width)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(header.padding),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        header.content()
                        HeaderOptions()
                    }

                    if (index < headers.lastIndex) {
                        VerticalDivider(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = TABLE_BORDER_ALPHA),
                            thickness = 2.dp,
                        )
                    }
                }
            }
        }
    }
}

data class TableHeaderData(
    val content: @Composable () -> Unit,
    val name: String,
    val width: Dp,
    val padding: Dp = 12.dp,
    val searchable: Boolean = false,
    val sortable: Boolean = false,
    val sortOrder: SortOrder = SortOrder.UNSPECIFIED,
    val onSortChanged: () -> Unit = {},
    val onSearchClick: () -> Unit = {},
    val searchEnabled: Boolean = true,
)