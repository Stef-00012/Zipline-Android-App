package com.stefdp.zipline.screens.metrics

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.google.gson.annotations.SerializedName
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.LocalLoggedUser
import com.stefdp.zipline.LocalWebSettings
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.OutlinedButton
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.components.table.Table
import com.stefdp.zipline.components.table.TableCellData
import com.stefdp.zipline.components.table.TableHeaderData
import com.stefdp.zipline.components.table.TableRowData
import com.stefdp.zipline.network.models.Metric
import com.stefdp.zipline.network.requests.getServerStats
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.screens.metrics.components.Container
import com.stefdp.zipline.screens.metrics.components.Stat
import com.stefdp.zipline.utils.colorHash
import com.stefdp.zipline.utils.formatBytes
import com.stefdp.zipline.utils.formatDate
import com.stefdp.zipline.utils.getMetricsDifference
import com.stefdp.zipline.utils.shimmerable
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.extensions.format
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.Pie
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.StrokeStyle
import java.time.Duration
import java.time.Instant
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

@Composable
fun MetricsScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity
) {
    val localLoggedUser = LocalLoggedUser.current

    if (localLoggedUser == null) {
        navController.navigate(LoginScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    val webSettings = LocalWebSettings.current

    val today = Instant.now()

    val defaultStartDate = Instant
        .now()
        .minus(Duration.ofDays(7))

    val defaultEndDate = today

    var statsRange by rememberSaveable { mutableStateOf(Range.CUSTOM) }
    var rangeStart by rememberSaveable { mutableStateOf(defaultStartDate) }
    var rangeEnd by rememberSaveable { mutableStateOf(defaultEndDate) }

    var stats by remember { mutableStateOf<List<Metric>?>(null) }

    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(statsRange, rangeStart, rangeEnd) {
        isLoading = true
        stats = null

        when (statsRange) {
            Range.ALL_TIME -> {
                val serverStatsRes = getServerStats(
                    context = context,
                    all = true
                )

                serverStatsRes.onSuccess {
                    stats = it.reversed()
                }
            }

            Range.CUSTOM -> {
                val serverStatsRes = getServerStats(
                    context = context,
                    from = rangeStart.toString(),
                    to = rangeEnd.toString()
                )

                serverStatsRes.onSuccess {
                    stats = it.reversed()
                }
            }

            Range.ONE_DAY -> {
                val serverStatsRes = getServerStats(
                    context = context,
                    from = rangeStart.toString(),
                    to = rangeEnd.toString()
                )

                serverStatsRes.onSuccess {
                    stats = it.reversed()
                }
            }
        }

        isLoading = false
    }

    Column(
        modifier = Modifier.padding(
            start = 12.dp,
            end = 12.dp,
            top = 12.dp
        )
    ) {
        var showRangePopup by rememberSaveable { mutableStateOf(false) }
        val rangeText = when (statsRange) {
            Range.ALL_TIME -> "All Time"
            Range.CUSTOM -> "${formatDate(
                date = rangeStart.toString(),
                short = true,
                dateOnly = true
            )} - ${formatDate(
                date = rangeEnd.toString(),
                short = true,
                dateOnly = true
            )}"
            Range.ONE_DAY -> formatDate(
                date = rangeStart.toString(),
                short = true,
                dateOnly = true
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Metrics",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = rangeText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            )
        }

        OutlinedButton(
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { showRangePopup = true },

        ) {
            Text(
                text = "Change Date Range",
                color = LocalContentColor.current,
                fontWeight = FontWeight.Bold
            )
        }

        Popup(
            showPopup = showRangePopup,
            onDismissRequest = { showRangePopup = false },
        ) {
            val today = Clock.System.now().toEpochMilliseconds()

            val selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis <= today
                }
            }

            val dateRangePickerState = rememberDateRangePickerState(
                selectableDates = selectableDates,
            )

            LaunchedEffect(
                dateRangePickerState.selectedStartDateMillis,
                dateRangePickerState.selectedEndDateMillis
            ) {
                var isOneDay = false

                val startDate = dateRangePickerState.selectedStartDateMillis
                val endDate = if (dateRangePickerState.selectedEndDateMillis == startDate) {
                    val oneDayMillis = 1.days.inWholeMilliseconds // 24 * 60 * 60 * 1000L

                    isOneDay = true

                    dateRangePickerState.selectedEndDateMillis?.plus(oneDayMillis)
                } else dateRangePickerState.selectedEndDateMillis

                if (startDate == null || endDate == null) return@LaunchedEffect

                val startDateString = Instant
                    .ofEpochMilli(startDate)

                val endDateString = Instant
                    .ofEpochMilli(endDate)

                rangeStart = startDateString
                rangeEnd = endDateString
                statsRange = if (isOneDay) Range.ONE_DAY else Range.CUSTOM
                showRangePopup = false
            }

            DateRangePicker(
                state = dateRangePickerState,
                modifier = if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT)
                    Modifier.height(350.dp)
                else Modifier.height(230.dp),
                colors = DatePickerDefaults.colors().copy(
                    containerColor = Color.Transparent,
                    dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                ),
                headline = {
                    Text(
                        text = "Select Date Range"
                    )
                },
                title = {},
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(start = 5.dp, end = 5.dp, top = 8.dp, bottom = 0.dp),
                onClick = {
                    statsRange = Range.ALL_TIME
                    showRangePopup = false
                },
                enabled = statsRange != Range.ALL_TIME
            ) {
                Text(
                    text = "All Time",
                    fontWeight = FontWeight.Bold,
                    color = LocalContentColor.current
                )
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(start = 5.dp, end = 5.dp, top = 8.dp, bottom = 0.dp),
                onClick = {
                    showRangePopup = false
                }
            ) {
                Text(
                    text = "Close",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        val scrollState = rememberScrollState()

        val firstStat = stats?.lastOrNull()
        val lastStat = stats?.firstOrNull()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Container(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(MaterialTheme.colorScheme.surface),
                scrollbar = true
            ) {
                @Composable
                fun StatDivider() {
                    VerticalDivider(
                        color = MaterialTheme.colorScheme.outline,
                        thickness = 2.dp,
                        modifier = Modifier
                            .height(75.dp)
                    )
                }

                val filesDifference = if (firstStat != null && lastStat != null) {
                    getMetricsDifference(
                        firstMetric = firstStat.data.files.toDouble(),
                        lastMetric = lastStat.data.files.toDouble()
                    )
                } else 0.0

                Stat(
                    title = "Files",
                    firstMetric = firstStat?.data?.files,
                    lastMetric = lastStat?.data?.files,
                    loading = firstStat == null || lastStat == null,
                    difference = filesDifference,
                    infinite = filesDifference == Double.POSITIVE_INFINITY
                )

                StatDivider()

                val urlsDifference = if (firstStat != null && lastStat != null) {
                    getMetricsDifference(
                        firstMetric = firstStat.data.urls.toDouble(),
                        lastMetric = lastStat.data.urls.toDouble()
                    )
                } else 0.0

                Stat(
                    title = "URLs",
                    firstMetric = firstStat?.data?.urls,
                    lastMetric = lastStat?.data?.urls,
                    loading = firstStat == null || lastStat == null,
                    difference = urlsDifference,
                    infinite = urlsDifference == Double.POSITIVE_INFINITY
                )

                StatDivider()

                val storageDifference = if (firstStat != null && lastStat != null) {
                    getMetricsDifference(
                        firstMetric = firstStat.data.storage.toDouble(),
                        lastMetric = lastStat.data.storage.toDouble()
                    )
                } else 0.0

                Stat(
                    title = "Storage Used",
                    firstMetric = firstStat?.data?.storage,
                    lastMetric = lastStat?.data?.storage,
                    loading = firstStat == null || lastStat == null,
                    difference = storageDifference,
                    formatValue = { value ->
                        formatBytes(value ?: 0L)
                    },
                    infinite = storageDifference == Double.POSITIVE_INFINITY
                )

                StatDivider()

                val fileViewsDifference = if (firstStat != null && lastStat != null) {
                    getMetricsDifference(
                        firstMetric = firstStat.data.fileViews.toDouble(),
                        lastMetric = lastStat.data.fileViews.toDouble()
                    )
                } else 0.0

                Stat(
                    title = "File Views",
                    firstMetric = firstStat?.data?.fileViews,
                    lastMetric = lastStat?.data?.fileViews,
                    loading = firstStat == null || lastStat == null,
                    difference = fileViewsDifference,
                    infinite = fileViewsDifference == Double.POSITIVE_INFINITY
                )

                val urlViewsDifference = if (firstStat != null && lastStat != null) {
                    getMetricsDifference(
                        firstMetric = firstStat.data.urlViews.toDouble(),
                        lastMetric = lastStat.data.urlViews.toDouble()
                    )
                } else 0.0

                Stat(
                    title = "URL Views",
                    firstMetric = firstStat?.data?.urlViews,
                    lastMetric = lastStat?.data?.urlViews,
                    loading = firstStat == null || lastStat == null,
                    difference = urlViewsDifference,
                    infinite = urlViewsDifference == Double.POSITIVE_INFINITY
                )
            }

            val chartSize = 250.dp

            @Composable
            fun LineChartSkeleton() {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(chartSize)
                        .shimmerable(true)
                )
            }

            @Composable
            fun ChartTooltip() {
                Row {
                    Icon(
                        painter = painterResource(R.drawable.info),
                        contentDescription = "Pie chart tooltip",
                        modifier = Modifier.size(15.dp).align(Alignment.CenterVertically),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text(
                        text = "Press for details",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (webSettings?.config?.features?.metrics?.showUserSpecific == true) {
                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Container(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(
                            max = 250.dp
                        )
                        .background(MaterialTheme.colorScheme.surface),
                    scrollable = false
                ) {
                    val tableUserWidth = 200.dp
                    val tableUrlsWidth = 100.dp
                    val tableViewsWidth = 100.dp

                    val headers: List<TableHeaderData> = listOf(
                        TableHeaderData(
                            content = {
                                Text(
                                    text = "User",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            name = "user",
                            width = tableUserWidth
                        ),
                        TableHeaderData(
                            content = {
                                Text(
                                    text = "URLs",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            name = "URLs",
                            width = tableUrlsWidth
                        ),
                        TableHeaderData(
                            content = {
                                Text(
                                    text = "Views",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            name = "views",
                            width = tableViewsWidth
                        )
                    )

                    val rows: List<TableRowData>? = firstStat?.data?.urlsUsers?.map { userUrl ->
                        TableRowData(
                            cells = listOf(
                                TableCellData(
                                    content = {
                                        Text(
                                            text = userUrl.username ?: "Unknown User",
                                        )
                                    },
                                    width = tableUserWidth
                                ),
                                TableCellData(
                                    content = {
                                        Text(
                                            text = userUrl.sum.toString(),
                                        )
                                    },
                                    width = tableUrlsWidth
                                ),
                                TableCellData(
                                    content = {
                                        Text(
                                            text = userUrl.views.toString(),
                                        )
                                    },
                                    width = tableViewsWidth
                                )
                            )
                        )
                    }

                    Table(
                        headers = headers,
                        rows = rows ?: emptyList(),
                        loading = firstStat?.data?.urlsUsers == null
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Container(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(
                            max = 250.dp
                        )
                        .background(MaterialTheme.colorScheme.surface),
                    scrollable = false
                ) {
                    val tableUserWidth = 200.dp
                    val tableFilesWidth = 100.dp
                    val tableStorageUsedWidth = 150.dp
                    val tableViewsWidth = 100.dp

                    val headers: List<TableHeaderData> = listOf(
                        TableHeaderData(
                            content = {
                                Text(
                                    text = "User",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            name = "user",
                            width = tableUserWidth
                        ),
                        TableHeaderData(
                            content = {
                                Text(
                                    text = "Files",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            name = "files",
                            width = tableFilesWidth
                        ),
                        TableHeaderData(
                            content = {
                                Text(
                                    text = "Storage Used",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            name = "storage used",
                            width = tableStorageUsedWidth
                        ),
                        TableHeaderData(
                            content = {
                                Text(
                                    text = "Views",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            name = "views",
                            width = tableViewsWidth
                        )
                    )

                    val rows: List<TableRowData>? = firstStat?.data?.filesUsers?.map { userFile ->
                        TableRowData(
                            cells = listOf(
                                TableCellData(
                                    content = {
                                        Text(
                                            text = userFile.username ?: "Unknown User",
                                        )
                                    },
                                    width = tableUserWidth
                                ),
                                TableCellData(
                                    content = {
                                        Text(
                                            text = userFile.sum.toString(),
                                        )
                                    },
                                    width = tableFilesWidth
                                ),
                                TableCellData(
                                    content = {
                                        Text(
                                            text = formatBytes(userFile.storage),
                                        )
                                    },
                                    width = tableStorageUsedWidth
                                ),
                                TableCellData(
                                    content = {
                                        Text(
                                            text = userFile.views.toString(),
                                        )
                                    },
                                    width = tableViewsWidth
                                )
                            )
                        )
                    }

                    Table(
                        headers = headers,
                        rows = rows ?: emptyList(),
                        loading = firstStat?.data?.urlsUsers == null
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Container(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(
                            max = if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT)
                                350.dp
                            else 280.dp
                        )
                        .background(MaterialTheme.colorScheme.surface),
                    scrollable = false
                ) {
                    val tableTypeWidth = 300.dp
                    val tableFilesWidth = 100.dp

                    val headers: List<TableHeaderData> = listOf(
                        TableHeaderData(
                            content = {
                                Text(
                                    text = "Type",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            name = "type",
                            width = tableTypeWidth
                        ),
                        TableHeaderData(
                            content = {
                                Text(
                                    text = "Files",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            name = "files",
                            width = tableFilesWidth
                        )
                    )

                    val rows: List<TableRowData>? = firstStat?.data?.types?.map { typeData ->
                        TableRowData(
                            cells = listOf(
                                TableCellData(
                                    content = {
                                        Text(
                                            text = typeData.type,
                                        )
                                    },
                                    width = tableTypeWidth
                                ),
                                TableCellData(
                                    content = {
                                        Text(
                                            text = typeData.sum.toString(),
                                        )
                                    },
                                    width = tableFilesWidth
                                )
                            )
                        )
                    }

                    Table(
                        headers = headers,
                        rows = rows ?: emptyList(),
                        loading = firstStat?.data?.urlsUsers == null
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Container(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface),
                    scrollable = false
                ) {
                    @Composable
                    fun PieChartSkeleton() {
                        Box(
                            modifier = Modifier
                                .size(chartSize)
                                .shimmerable(
                                    enabled = true,
                                    shape = CircleShape,
                                )
                                .align(Alignment.CenterHorizontally)
                        )
                    }

                    var selectedType by remember { mutableStateOf<Pie?>(null) }
                    var pieChartTypes by remember(firstStat, selectedType) {
                        val types = firstStat?.data?.types ?: emptyList()

                        mutableStateOf(
                            types.map { typeData ->
                                Pie(
                                    label = typeData.type,
                                    data = typeData.sum.toDouble(),
                                    color = colorHash(typeData.type),
                                    selected = typeData.type == selectedType?.label
                                )
                            }
                        )
                    }

                    LaunchedEffect(statsRange, rangeStart, rangeEnd) {
                        selectedType = null
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth().padding(
                            horizontal = 15.dp,
                            vertical = 12.5.dp
                        )
                    ) {
                        Text(
                            text = "File Types",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 10.dp),
                        )

                        if (!isLoading && pieChartTypes.isNotEmpty()) {
                            PieChart(
                                data = pieChartTypes,
                                modifier = Modifier
                                    .size(chartSize)
                                    .align(Alignment.CenterHorizontally)
                                    .padding(vertical = 20.dp),
                                style = Pie.Style.Fill,
                                selectedScale = 1.2f,
                                onPieClick = { clickedPie ->
                                    selectedType = clickedPie
                                },
                                labelHelperProperties = LabelHelperProperties(
                                    enabled = false
                                )
                            )

                            if (selectedType != null && pieChartTypes.find { it.label == selectedType?.label } != null) {
                                Row {
                                    Box(
                                        modifier = Modifier
                                            .size(15.dp)
                                            .background(selectedType?.color ?: Color(0xFFFFFFFF))
                                            .align(Alignment.CenterVertically)
                                    )

                                    Spacer(
                                        modifier = Modifier.width(6.dp)
                                    )

                                    Text(
                                        text = "${selectedType?.label}: ${selectedType?.data?.toLong()}"
                                    )
                                }
                            } else {
                                ChartTooltip()
                            }
                        } else {
                            PieChartSkeleton()

                            ChartTooltip()
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            val lineChartBottomSpacing = 40.dp

            Container(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface),
                scrollable = false
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(
                        horizontal = 15.dp,
                        vertical = 12.5.dp
                    )
                ) {
                    Text(
                        text = "Views",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 10.dp),
                    )

                    val fileColor = Color.Blue
                    val urlColor = Color.Green

                    if (stats != null) {
                        var activePopupIndex by remember { mutableStateOf<Int?>(null) }

                        val fileDotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(fileColor),
                            strokeWidth = 1.dp,
                            radius = 4.dp,
                            strokeColor = SolidColor(fileColor),
                            animationEnabled = false,
                            confirmDraw = { dot ->
                                dot.valueIndex == activePopupIndex
                            }
                        )

                        val urlDotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(urlColor),
                            strokeWidth = 1.dp,
                            radius = 4.dp,
                            strokeColor = SolidColor(urlColor),
                            animationEnabled = false,
                            confirmDraw = { dot ->
                                dot.valueIndex == activePopupIndex
                            }
                        )

                        val lines by remember(stats) {
                            mutableStateOf(
                                listOf(
                                    Line(
                                        values = (stats?.map { it.data.fileViews.toDouble() } ?: emptyList()),
                                        color = SolidColor(fileColor),
                                        dotProperties = fileDotProperties,
                                        drawStyle = DrawStyle.Fill,
                                        firstGradientFillColor = fileColor.copy(alpha = 0.2f),
                                        secondGradientFillColor = fileColor.copy(alpha = 0f)
                                    ),
                                    Line(
                                        values = stats?.map { it.data.urlViews.toDouble() } ?: emptyList(),
                                        color = SolidColor(urlColor),
                                        dotProperties = urlDotProperties,
                                        drawStyle = DrawStyle.Fill,
                                        firstGradientFillColor = urlColor.copy(alpha = 0.2f),
                                        secondGradientFillColor = urlColor.copy(alpha = 0f)
                                    )
                                )
                            )
                        }

                        val xAxisProperties = GridProperties.AxisProperties(
                            enabled = true,
                            color = SolidColor(
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                            ),
                            lineCount = 5,
                            style = StrokeStyle.Dashed(
                                intervals = floatArrayOf(15f, 15f),
                            )
                        )

                        val yAxisProperties = GridProperties.AxisProperties(
                            enabled = false
                        )

                        LineChart(
                            data = lines,
                            modifier = Modifier.height(chartSize),
                            dotsProperties = DotProperties(
                                enabled = false
                            ),
                            popupProperties = PopupProperties(
                                enabled = true,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                cornerRadius = BASE_CORNER_RADIUS.dp,
                                contentVerticalPadding = 8.dp,
                                contentHorizontalPadding = 8.dp,
                                textStyle = MaterialTheme.typography.labelLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                ),
                                contentBuilder = { value ->
                                    activePopupIndex = value.valueIndex

                                    val metric = stats?.get(value.valueIndex)
                                    val formattedValue = value.value.format(0)

                                    val label = if (value.dataIndex == 0) "Files" else "URLs"

                                    if (metric == null) return@PopupProperties "$formattedValue $label"

                                    val date = formatDate(
                                        date = metric.createdAt,
                                        short = true
                                    )

                                    return@PopupProperties "$date\n$formattedValue $label"
                                }
                            ),
                            gridProperties = GridProperties(
                                enabled = true,
                                xAxisProperties = xAxisProperties,
                                yAxisProperties = yAxisProperties
                            ),
                            indicatorProperties = HorizontalIndicatorProperties(
                                enabled = true,
                                textStyle = MaterialTheme.typography.labelLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                                count = IndicatorCount.CountBased(5),
                                contentBuilder = { value ->
                                    value.format(0)
                                }
                            )
                        )
                    } else {
                        LineChartSkeleton()
                    }

                    Spacer(
                        modifier = Modifier.height(lineChartBottomSpacing)
                    )

                    ChartTooltip()
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Container(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface),
                scrollable = false
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(
                        horizontal = 15.dp,
                        vertical = 12.5.dp
                    )
                ) {
                    Text(
                        text = "Count",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 10.dp),
                    )

                    val fileColor = Color.Blue
                    val urlColor = Color.Green

                    if (stats != null) {
                        var activePopupIndex by remember { mutableStateOf<Int?>(null) }

                        val fileDotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(fileColor),
                            strokeWidth = 1.dp,
                            radius = 4.dp,
                            strokeColor = SolidColor(fileColor),
                            animationEnabled = false,
                            confirmDraw = { dot ->
                                dot.valueIndex == activePopupIndex
                            }
                        )

                        val urlDotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(urlColor),
                            strokeWidth = 1.dp,
                            radius = 4.dp,
                            strokeColor = SolidColor(urlColor),
                            animationEnabled = false,
                            confirmDraw = { dot ->
                                dot.valueIndex == activePopupIndex
                            }
                        )

                        val lines by remember(stats) {
                            mutableStateOf(
                                listOf(
                                    Line(
                                        values = (stats?.map { it.data.files.toDouble() } ?: emptyList()),
                                        color = SolidColor(fileColor),
                                        dotProperties = fileDotProperties,
                                        drawStyle = DrawStyle.Fill,
                                        firstGradientFillColor = fileColor.copy(alpha = 0.2f),
                                        secondGradientFillColor = fileColor.copy(alpha = 0f)
                                    ),
                                    Line(
                                        values = stats?.map { it.data.urls.toDouble() } ?: emptyList(),
                                        color = SolidColor(urlColor),
                                        dotProperties = urlDotProperties,
                                        drawStyle = DrawStyle.Fill,
                                        firstGradientFillColor = urlColor.copy(alpha = 0.2f),
                                        secondGradientFillColor = urlColor.copy(alpha = 0f)
                                    )
                                )
                            )
                        }

                        val xAxisProperties = GridProperties.AxisProperties(
                            enabled = true,
                            color = SolidColor(
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                            ),
                            lineCount = 5,
                            style = StrokeStyle.Dashed(
                                intervals = floatArrayOf(15f, 15f),
                            )
                        )

                        val yAxisProperties = GridProperties.AxisProperties(
                            enabled = false
                        )

                        LineChart(
                            data = lines,
                            modifier = Modifier.height(chartSize),
                            dotsProperties = DotProperties(
                                enabled = false
                            ),
                            popupProperties = PopupProperties(
                                enabled = true,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                cornerRadius = BASE_CORNER_RADIUS.dp,
                                contentVerticalPadding = 8.dp,
                                contentHorizontalPadding = 8.dp,
                                textStyle = MaterialTheme.typography.labelLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                ),
                                contentBuilder = { value ->
                                    activePopupIndex = value.valueIndex

                                    val metric = stats?.get(value.valueIndex)
                                    val formattedValue = value.value.format(0)

                                    val label = if (value.dataIndex == 0) "Files" else "URLs"

                                    if (metric == null) return@PopupProperties "$formattedValue $label"

                                    val date = formatDate(
                                        date = metric.createdAt,
                                        short = true
                                    )

                                    return@PopupProperties "$date\n$formattedValue $label"
                                }
                            ),
                            gridProperties = GridProperties(
                                enabled = true,
                                xAxisProperties = xAxisProperties,
                                yAxisProperties = yAxisProperties
                            ),
                            indicatorProperties = HorizontalIndicatorProperties(
                                enabled = true,
                                textStyle = MaterialTheme.typography.labelLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                                count = IndicatorCount.CountBased(5),
                                contentBuilder = { value ->
                                    value.format(0)
                                }
                            )
                        )
                    } else {
                        LineChartSkeleton()
                    }

                    Spacer(
                        modifier = Modifier.height(lineChartBottomSpacing)
                    )

                    ChartTooltip()
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Container(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface),
                scrollable = false
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(
                        horizontal = 15.dp,
                        vertical = 12.5.dp
                    )
                ) {
                    Text(
                        text = "Storage Used",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 10.dp),
                    )

                    val storageColor = Color.Blue

                    if (stats != null) {
                        var activePopupIndex by remember { mutableStateOf<Int?>(null) }

                        val storageDotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(storageColor),
                            strokeWidth = 1.dp,
                            radius = 4.dp,
                            strokeColor = SolidColor(storageColor),
                            animationEnabled = false,
                            confirmDraw = { dot ->
                                dot.valueIndex == activePopupIndex
                            }
                        )

                        val lines by remember(stats) {
                            mutableStateOf(
                                listOf(
                                    Line(
                                        values = (stats?.map { it.data.storage.toDouble() } ?: emptyList()),
                                        color = SolidColor(storageColor),
                                        dotProperties = storageDotProperties,
                                        drawStyle = DrawStyle.Fill,
                                        firstGradientFillColor = storageColor.copy(alpha = 0.2f),
                                        secondGradientFillColor = storageColor.copy(alpha = 0f)
                                    ),
                                )
                            )
                        }

                        val xAxisProperties = GridProperties.AxisProperties(
                            enabled = true,
                            color = SolidColor(
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                            ),
                            lineCount = 5,
                            style = StrokeStyle.Dashed(
                                intervals = floatArrayOf(15f, 15f),
                            )
                        )

                        val yAxisProperties = GridProperties.AxisProperties(
                            enabled = false
                        )

                        LineChart(
                            data = lines,
                            modifier = Modifier.height(chartSize),
                            dotsProperties = DotProperties(
                                enabled = false
                            ),
                            popupProperties = PopupProperties(
                                enabled = true,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                cornerRadius = BASE_CORNER_RADIUS.dp,
                                contentVerticalPadding = 8.dp,
                                contentHorizontalPadding = 8.dp,
                                textStyle = MaterialTheme.typography.labelLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                ),
                                contentBuilder = { value ->
                                    activePopupIndex = value.valueIndex

                                    val metric = stats?.get(value.valueIndex)
                                    val formattedValue = formatBytes(value.value.toLong())

                                    if (metric == null) return@PopupProperties formattedValue

                                    val date = formatDate(
                                        date = metric.createdAt,
                                        short = true
                                    )

                                    return@PopupProperties "$date\n$formattedValue"
                                }
                            ),
                            gridProperties = GridProperties(
                                enabled = true,
                                xAxisProperties = xAxisProperties,
                                yAxisProperties = yAxisProperties
                            ),
                            indicatorProperties = HorizontalIndicatorProperties(
                                enabled = true,
                                textStyle = MaterialTheme.typography.labelLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                                count = IndicatorCount.CountBased(5),
                                contentBuilder = { value ->
                                    formatBytes(value.toLong())
                                }
                            )
                        )
                    } else {
                        LineChartSkeleton()
                    }

                    Spacer(
                        modifier = Modifier.height(lineChartBottomSpacing)
                    )

                    ChartTooltip()
                }
            }
        }
    }
}

private enum class Range(val value: String) {
    @SerializedName("alltime")
    ALL_TIME("alltime"),

    @SerializedName("custom")
    CUSTOM("custom"),

    @SerializedName("1d")
    ONE_DAY("1d");

    override fun toString(): String = value
}





