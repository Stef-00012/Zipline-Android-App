package com.stefdp.zipline.screens.metrics

import android.content.Context
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.SerializedName
import com.stefdp.zipline.Logger
import com.stefdp.zipline.network.models.Metric
import com.stefdp.zipline.network.requests.getServerStats
import com.stefdp.zipline.utils.colorHash
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.Pie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant

enum class Range(val value: String) {
    @SerializedName("alltime")
    ALL_TIME("alltime"),

    @SerializedName("custom")
    CUSTOM("custom"),

    @SerializedName("1d")
    ONE_DAY("1d");

    override fun toString(): String = value
}

data class MetricsUiState(
    val isLoading: Boolean = false,
    val stats: List<Metric>? = null,
    val firstStat: Metric? = null,
    val lastStat: Metric? = null,
    val statsRange: Range = Range.CUSTOM,
    val rangeStart: Instant = Instant
        .now()
        .minus(Duration.ofDays(7)),
    val rangeEnd: Instant = Instant.now(),
    val showRangePopup: Boolean = false,
    val lines: List<Line> = emptyList(),
    val selectedPieChartType: Pie? = null,
    val pieChartTypes: List<Pie> = emptyList(),
    val viewsLineChartActivePopupIndex: Int? = null,
    val viewsLineChartLines: List<Line> = emptyList(),
    val countLineChartActivePopupIndex: Int? = null,
    val countLineChartLines: List<Line> = emptyList(),
    val storageUsedLineChartActivePopupIndex: Int? = null,
    val storageUsedLineChartLines: List<Line> = emptyList(),
)

class MetricsViewModel : ViewModel() {
    private val _state = MutableStateFlow(MetricsUiState())
    val state: StateFlow<MetricsUiState> = _state.asStateFlow()

    fun openRangePopup() {
        _state.update {
            it.copy(showRangePopup = true)
        }
    }

    fun closeRangePopup() {
        _state.update {
            it.copy(showRangePopup = false)
        }
    }

    fun setSelectedPieChartType(pie: Pie?) {
        _state.update {
            it.copy(selectedPieChartType = pie)
        }
    }

    fun refreshPieChartTypes() {
        _state.update {
            val types = _state.value.firstStat?.data?.types ?: emptyList()

            it.copy(
                pieChartTypes = types.map { typeData ->
                    Pie(
                        label = typeData.type,
                        data = typeData.sum.toDouble(),
                        color = colorHash(typeData.type),
                        selected = typeData.type == it.selectedPieChartType?.label
                    )
                }
            )
        }
    }

    fun setViewsLineChartActivePopupIndex(index: Int?) {
        _state.update {
            it.copy(viewsLineChartActivePopupIndex = index)
        }
    }

    fun setCountLineChartActivePopupIndex(index: Int?) {
        _state.update {
            it.copy(countLineChartActivePopupIndex = index)
        }
    }

    fun setStorageLineChartUsedActivePopupIndex(index: Int?) {
        _state.update {
            it.copy(storageUsedLineChartActivePopupIndex = index)
        }
    }

    fun updateRange(
        rangeStart: Instant? = null,
        rangeEnd: Instant? = null,
        statsRange: Range
    ) {
        _state.update {
            it.copy(
                rangeStart = rangeStart ?: it.rangeStart,
                rangeEnd = rangeEnd ?: it.rangeEnd,
                statsRange = statsRange,
                selectedPieChartType = null
            )
        }
    }

    fun refreshStats(context: Context) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    stats = null
                )
            }

            fun updateStats(stats: List<Metric>) {
                val firstStat = stats.lastOrNull()
                val lastStat = stats.firstOrNull()

                Logger.debug("MetricsViewModel", "First stat: $firstStat, Last stat: $lastStat")

                val types = firstStat?.data?.types ?: emptyList()

                val viewsLineChartFileDotProperties = DotProperties(
                    enabled = true,
                    color = SolidColor(metricsFileColor),
                    strokeWidth = 1.dp,
                    radius = 4.dp,
                    strokeColor = SolidColor(metricsFileColor),
                    animationEnabled = false,
                    confirmDraw = { dot ->
                        dot.valueIndex == _state.value.viewsLineChartActivePopupIndex
                    }
                )

                val viewsLineChartUrlDotProperties = DotProperties(
                    enabled = true,
                    color = SolidColor(metricsUrlColor),
                    strokeWidth = 1.dp,
                    radius = 4.dp,
                    strokeColor = SolidColor(metricsUrlColor),
                    animationEnabled = false,
                    confirmDraw = { dot ->
                        dot.valueIndex == _state.value.viewsLineChartActivePopupIndex
                    }
                )

                val countLineChartFileDotProperties = DotProperties(
                    enabled = true,
                    color = SolidColor(metricsFileColor),
                    strokeWidth = 1.dp,
                    radius = 4.dp,
                    strokeColor = SolidColor(metricsFileColor),
                    animationEnabled = false,
                    confirmDraw = { dot ->
                        dot.valueIndex == _state.value.countLineChartActivePopupIndex
                    }
                )

                val countLineChartUrlDotProperties = DotProperties(
                    enabled = true,
                    color = SolidColor(metricsUrlColor),
                    strokeWidth = 1.dp,
                    radius = 4.dp,
                    strokeColor = SolidColor(metricsUrlColor),
                    animationEnabled = false,
                    confirmDraw = { dot ->
                        dot.valueIndex == _state.value.countLineChartActivePopupIndex
                    }
                )

                val storageUsedLineChartStorageDotProperties = DotProperties(
                    enabled = true,
                    color = SolidColor(metricsStorageColor),
                    strokeWidth = 1.dp,
                    radius = 4.dp,
                    strokeColor = SolidColor(metricsStorageColor),
                    animationEnabled = false,
                    confirmDraw = { dot ->
                        dot.valueIndex == _state.value.storageUsedLineChartActivePopupIndex
                    }
                )

                _state.update {
                    it.copy(
                        stats = stats,
                        firstStat = firstStat,
                        lastStat = lastStat,
                        pieChartTypes = types.map { typeData ->
                            Pie(
                                label = typeData.type,
                                data = typeData.sum.toDouble(),
                                color = colorHash(typeData.type),
                                selected = typeData.type == it.selectedPieChartType?.label
                            )
                        },
                        viewsLineChartLines = listOf(
                            Line(
                                values = stats.map { stat -> stat.data.fileViews.toDouble() },
                                color = SolidColor(metricsFileColor),
                                dotProperties = viewsLineChartFileDotProperties,
                                drawStyle = DrawStyle.Fill,
                                firstGradientFillColor = metricsFileColor.copy(alpha = 0.2f),
                                secondGradientFillColor = metricsFileColor.copy(alpha = 0f)
                            ),
                            Line(
                                values = stats.map { stat -> stat.data.urlViews.toDouble() },
                                color = SolidColor(metricsUrlColor),
                                dotProperties = viewsLineChartUrlDotProperties,
                                drawStyle = DrawStyle.Fill,
                                firstGradientFillColor = metricsUrlColor.copy(alpha = 0.2f),
                                secondGradientFillColor = metricsUrlColor.copy(alpha = 0f)
                            )
                        ),
                        countLineChartLines = listOf(
                            Line(
                                values = stats.map { stat -> stat.data.files.toDouble() },
                                color = SolidColor(metricsFileColor),
                                dotProperties = countLineChartFileDotProperties,
                                drawStyle = DrawStyle.Fill,
                                firstGradientFillColor = metricsFileColor.copy(alpha = 0.2f),
                                secondGradientFillColor = metricsFileColor.copy(alpha = 0f)
                            ),
                            Line(
                                values = stats.map { stat -> stat.data.urls.toDouble() },
                                color = SolidColor(metricsUrlColor),
                                dotProperties = countLineChartUrlDotProperties,
                                drawStyle = DrawStyle.Fill,
                                firstGradientFillColor = metricsUrlColor.copy(alpha = 0.2f),
                                secondGradientFillColor = metricsUrlColor.copy(alpha = 0f)
                            )
                        ),
                        storageUsedLineChartLines = listOf(
                            Line(
                                values = stats.map { stat -> stat.data.storage.toDouble() },
                                color = SolidColor(metricsStorageColor),
                                dotProperties = storageUsedLineChartStorageDotProperties,
                                drawStyle = DrawStyle.Fill,
                                firstGradientFillColor = metricsStorageColor.copy(alpha = 0.2f),
                                secondGradientFillColor = metricsStorageColor.copy(alpha = 0f)
                            ),
                        ),
                        isLoading = false
                    )
                }
            }

            when (_state.value.statsRange) {
                Range.ALL_TIME -> {
                    val serverStatsRes = getServerStats(
                        context = context,
                        all = true
                    )

                    serverStatsRes.onSuccess { stats ->
                        updateStats(
                            stats = stats.reversed(),
                        )
                    }
                }

                Range.CUSTOM -> {
                    val serverStatsRes = getServerStats(
                        context = context,
                        from = _state.value.rangeStart.toString(),
                        to = _state.value.rangeEnd.toString()
                    )

                    serverStatsRes.onSuccess { stats ->
                        updateStats(
                            stats = stats.reversed(),
                        )
                    }
                }

                Range.ONE_DAY -> {
                    val serverStatsRes = getServerStats(
                        context = context,
                        from = _state.value.rangeStart.toString(),
                        to = _state.value.rangeEnd.toString()
                    )

                    serverStatsRes.onSuccess { stats ->
                        updateStats(
                            stats = stats.reversed(),
                        )
                    }
                }
            }
        }
    }
}