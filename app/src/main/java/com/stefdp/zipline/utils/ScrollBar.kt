package com.stefdp.zipline.utils

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.verticalScroll
import kotlin.math.max

fun Modifier.scrollbar(
    scrollState: ScrollState,
    direction: Orientation,
    config: ScrollbarConfig = ScrollbarConfig(),
): Modifier = composed {
    var (
        indicatorThickness,
        indicatorColor,
        indicatorCornerRadius,
        alpha,
        alphaAnimationSpec,
        padding
    ) = config

    val isScrollingOrPanning = scrollState.isScrollInProgress
    val isVertical = direction == Orientation.Vertical

    alpha = when {
        config.alwaysKeepScrollbar -> 0.8f
        alpha != null -> alpha
        isScrollingOrPanning -> 0.8f
        else -> 0f
    }

    alphaAnimationSpec = alphaAnimationSpec ?: tween(
        delayMillis = if (isScrollingOrPanning || config.alwaysKeepScrollbar) 0 else 1500,
        durationMillis = if (isScrollingOrPanning || config.alwaysKeepScrollbar) 150 else 500
    )

    val scrollbarAlpha by animateFloatAsState(alpha, alphaAnimationSpec)

    drawWithContent {
        drawContent()

        val showScrollbar = config.alwaysKeepScrollbar || isScrollingOrPanning || scrollbarAlpha > 0.0f

        if (showScrollbar) {
            val (topPadding, bottomPadding, startPadding, endPadding) = arrayOf(
                padding.calculateTopPadding().toPx(),
                padding.calculateBottomPadding().toPx(),
                padding.calculateStartPadding(layoutDirection).toPx(),
                padding.calculateEndPadding(layoutDirection).toPx()
            )

            val isLtr = layoutDirection == LayoutDirection.Ltr
            val contentOffset = scrollState.value

            val viewPortLength = if (isVertical) size.height else size.width
            val viewPortCrossAxisLength = if (isVertical) size.width else size.height

            val contentLength = max(viewPortLength + scrollState.maxValue, 0.001f)
            val scrollbarLength = viewPortLength -
                    (if (isVertical) topPadding + bottomPadding else startPadding + endPadding)

            val indicatorThicknessPx = indicatorThickness.toPx()
            val indicatorLength = max((scrollbarLength / contentLength) * viewPortLength, 20f.dp.toPx())
            val indicatorOffset = (scrollbarLength / contentLength) * contentOffset

            val scrollIndicatorSize = if (isVertical) Size(indicatorThicknessPx, indicatorLength)
            else Size(indicatorLength, indicatorThicknessPx)

            val scrollIndicatorPosition = if (isVertical)
                Offset(
                    x = if (isLtr)
                        viewPortCrossAxisLength - indicatorThicknessPx - endPadding
                    else startPadding,

                    y = indicatorOffset + topPadding
                )
            else
                Offset(
                    x = if (isLtr)
                        indicatorOffset + startPadding
                    else
                        viewPortLength - indicatorOffset - indicatorLength - endPadding,

                    y = viewPortCrossAxisLength - indicatorThicknessPx - bottomPadding
                )

            drawRoundRect(
                color = indicatorColor,
                cornerRadius = indicatorCornerRadius.let {
                    CornerRadius(
                        x = it.toPx(),
                        y = it.toPx()
                    )
                },
                topLeft = scrollIndicatorPosition,
                size = scrollIndicatorSize,
                alpha = scrollbarAlpha
            )
        }
    }
}


data class ScrollbarConfig(
    val indicatorThickness: Dp = 4.dp,
    val indicatorColor: Color = Color.Gray.copy(alpha = 0.7f),
    val indicatorCornerRadius: Dp = indicatorThickness / 2,
    val alpha: Float? = null,
    val alphaAnimationSpec: AnimationSpec<Float>? = null,
    val padding: PaddingValues = PaddingValues(
        bottom = 5.dp,
        start = 5.dp,
        end = 5.dp,
    ),
    val alwaysKeepScrollbar: Boolean = false
)

fun Modifier.verticalScrollWithScrollbar(
    scrollState: ScrollState,
    enabled: Boolean = true,
    flingBehavior: FlingBehavior? = null,
    reverseScrolling: Boolean = false,
    scrollbarConfig: ScrollbarConfig = ScrollbarConfig()
): Modifier = this
    .scrollbar(
        scrollState = scrollState,
        direction = Orientation.Vertical,
        config = scrollbarConfig
    )
    .verticalScroll(
        state = scrollState,
        enabled = enabled,
        flingBehavior = flingBehavior,
        reverseScrolling = reverseScrolling
    )

fun Modifier.horizontalScrollWithScrollbar(
    scrollState: ScrollState,
    enabled: Boolean = true,
    flingBehavior: FlingBehavior? = null,
    reverseScrolling: Boolean = false,
    scrollbarConfig: ScrollbarConfig = ScrollbarConfig()
): Modifier = this
    .scrollbar(
        scrollState = scrollState,
        direction = Orientation.Horizontal,
        config = scrollbarConfig
    )
    .horizontalScroll(
        state = scrollState,
        enabled = enabled,
        flingBehavior = flingBehavior,
        reverseScrolling = reverseScrolling
    )

fun Modifier.lazyScrollbar(
    listState: LazyListState,
    direction: Orientation,
    config: ScrollbarConfig = ScrollbarConfig(),
): Modifier = composed {
    var (
        indicatorThickness,
        indicatorColor,
        indicatorCornerRadius,
        alpha,
        alphaAnimationSpec,
        padding
    ) = config

    val isScrollingOrPanning = listState.isScrollInProgress
    val isVertical = direction == Orientation.Vertical

    alpha = when {
        config.alwaysKeepScrollbar -> 0.8f
        alpha != null -> alpha
        isScrollingOrPanning -> 0.8f
        else -> 0f
    }

    alphaAnimationSpec = alphaAnimationSpec ?: tween(
        delayMillis = if (isScrollingOrPanning || config.alwaysKeepScrollbar) 0 else 1500,
        durationMillis = if (isScrollingOrPanning || config.alwaysKeepScrollbar) 150 else 500
    )

    val scrollbarAlpha by animateFloatAsState(alpha, alphaAnimationSpec)

    drawWithContent {
        drawContent()

        val showScrollbar = config.alwaysKeepScrollbar || isScrollingOrPanning || scrollbarAlpha > 0.0f

        if (showScrollbar && listState.layoutInfo.totalItemsCount > 0) {
            val topPadding = padding.calculateTopPadding().toPx()
            val bottomPadding = padding.calculateBottomPadding().toPx()
            val startPadding = padding.calculateStartPadding(layoutDirection).toPx()
            val endPadding = padding.calculateEndPadding(layoutDirection).toPx()

            val isLtr = layoutDirection == LayoutDirection.Ltr

            val firstVisibleIndex = listState.firstVisibleItemIndex
            val firstVisibleOffset = listState.firstVisibleItemScrollOffset
            val totalItems = listState.layoutInfo.totalItemsCount

            val viewPortLength = if (isVertical) size.height else size.width
            val viewPortCrossAxisLength = if (isVertical) size.width else size.height

            val scrollbarLength = viewPortLength - (if (isVertical) topPadding + bottomPadding else startPadding + endPadding)

            val estimatedItemHeight = if (listState.layoutInfo.visibleItemsInfo.isNotEmpty()) {
                listState.layoutInfo.visibleItemsInfo.first().size.toFloat()
            } else 0f

            val estimatedTotalLength = totalItems * estimatedItemHeight
            val scrollOffset = firstVisibleIndex * estimatedItemHeight + firstVisibleOffset

            val indicatorThicknessPx = indicatorThickness.toPx()
            val indicatorLength = max((viewPortLength / estimatedTotalLength) * scrollbarLength, 20f.dp.toPx())
            val indicatorOffset = (scrollOffset / estimatedTotalLength) * scrollbarLength

            val scrollIndicatorSize = if (isVertical) Size(indicatorThicknessPx, indicatorLength)
            else Size(indicatorLength, indicatorThicknessPx)

            val scrollIndicatorPosition = if (isVertical)
                Offset(
                    x = if (isLtr) viewPortCrossAxisLength - indicatorThicknessPx - endPadding
                    else startPadding,
                    y = indicatorOffset + topPadding
                )
            else
                Offset(
                    x = if (isLtr) indicatorOffset + startPadding
                    else viewPortLength - indicatorOffset - indicatorLength - endPadding,
                    y = viewPortCrossAxisLength - indicatorThicknessPx - bottomPadding
                )

            drawRoundRect(
                color = indicatorColor,
                cornerRadius = indicatorCornerRadius.let { CornerRadius(it.toPx(), it.toPx()) },
                topLeft = scrollIndicatorPosition,
                size = scrollIndicatorSize,
                alpha = scrollbarAlpha
            )
        }
    }
}

fun Modifier.verticalLazyScrollbar(
    listState: LazyListState,
    scrollbarConfig: ScrollbarConfig = ScrollbarConfig()
): Modifier = this.lazyScrollbar(
    listState = listState,
    direction = Orientation.Vertical,
    config = scrollbarConfig
)

fun Modifier.horizontalLazyScrollbar(
    listState: LazyListState,
    scrollbarConfig: ScrollbarConfig = ScrollbarConfig()
): Modifier = this.lazyScrollbar(
    listState = listState,
    direction = Orientation.Horizontal,
    config = scrollbarConfig
)