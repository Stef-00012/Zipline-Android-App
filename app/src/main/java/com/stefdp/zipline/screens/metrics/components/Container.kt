package com.stefdp.zipline.screens.metrics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.horizontalScrollWithScrollbar

@Composable
fun Container(
    modifier: Modifier = Modifier,
    scrollable: Boolean = true,
    border: Boolean = true,
    scrollbar: Boolean = false,
    content: @Composable () -> Unit,
) {
    val scrollState = rememberScrollState()

    val base = Modifier
        .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
        .then(modifier)

    val borderModifier = if (border) base
        .border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.outline,
            shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
        )
    else base

    val scrollModifier = if (scrollable) {
        if (scrollbar) borderModifier
            .horizontalScrollWithScrollbar(
                scrollState = scrollState,
                scrollbarConfig = ScrollbarConfig(
                    alwaysKeepScrollbar = true
                )
            )
        else borderModifier
            .horizontalScroll(
                scrollState,
            )
    } else borderModifier

    Row(
        modifier = scrollModifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
    }
}