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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
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
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.network.models.UserRole
import com.stefdp.zipline.screens.HomeScreen
import com.stefdp.zipline.screens.metrics.components.Stat
import com.stefdp.zipline.utils.formatBytes
import com.stefdp.zipline.utils.formatDate
import com.stefdp.zipline.utils.getMetricsDifference
import com.stefdp.zipline.utils.shimmerable
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.extensions.format
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.Pie
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.StrokeStyle
import java.time.Instant
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

val metricsFileColor = Color.Blue
val metricsUrlColor = Color.Green
val metricsStorageColor = Color.Blue

@Composable
fun MetricsScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: MetricsViewModel = viewModel()
) {
    val localLoggedUser = LocalLoggedUser.current

    if (localLoggedUser == null) {
        navController.navigate(LoginScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    val webSettings = LocalWebSettings.current

    if (webSettings?.config?.features?.metrics?.enabled != true) {
        navController.navigate(HomeScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    localLoggedUser?.role?.level?.let {
        if (webSettings?.config?.features?.metrics?.adminOnly == true && it > UserRole.ADMIN.level) {
            navController.navigate(HomeScreen) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    val state by viewModel.state.collectAsState()

    LaunchedEffect(
        state.statsRange,
        state.rangeStart,
        state.rangeEnd
    ) {
        viewModel.refreshStats(context)
    }

    Column(
        modifier = Modifier.padding(
            start = 12.dp,
            end = 12.dp,
            top = 12.dp
        )
    ) {
        val rangeText = when (state.statsRange) {
            Range.ALL_TIME -> "All Time"
            Range.CUSTOM -> "${formatDate(
                date = state.rangeStart.toString(),
                short = true,
                dateOnly = true
            )} - ${formatDate(
                date = state.rangeEnd.toString(),
                short = true,
                dateOnly = true
            )}"
            Range.ONE_DAY -> formatDate(
                date = state.rangeStart.toString(),
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
            enabled = !state.isLoading,
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { viewModel.openRangePopup() },

        ) {
            Text(
                text = "Change Date Range",
                color = LocalContentColor.current,
                fontWeight = FontWeight.Bold
            )
        }

        Popup(
            showPopup = state.showRangePopup,
            onDismissRequest = { viewModel.closeRangePopup() },
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

                viewModel.updateRange(
                    rangeStart = startDateString,
                    rangeEnd = endDateString,
                    statsRange = if (isOneDay) Range.ONE_DAY else Range.CUSTOM
                )

                viewModel.closeRangePopup()
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
                    viewModel.updateRange(
                        statsRange = Range.ALL_TIME
                    )

                    viewModel.closeRangePopup()
                },
                enabled = state.statsRange != Range.ALL_TIME
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
                   viewModel.closeRangePopup()
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

                val filesDifference = if (state.firstStat != null && state.lastStat != null) {
                    getMetricsDifference(
                        firstMetric = state.firstStat!!.data.files.toDouble(),
                        lastMetric = state.lastStat!!.data.files.toDouble()
                    )
                } else 0.0

                Stat(
                    title = "Files",
                    firstMetric = state.firstStat?.data?.files,
                    lastMetric = state.lastStat?.data?.files,
                    loading = state.firstStat == null || state.lastStat == null,
                    difference = filesDifference,
                    infinite = filesDifference == Double.POSITIVE_INFINITY
                )

                StatDivider()

                val urlsDifference = if (state.firstStat != null && state.lastStat != null) {
                    getMetricsDifference(
                        firstMetric = state.firstStat!!.data.urls.toDouble(),
                        lastMetric = state.lastStat!!.data.urls.toDouble()
                    )
                } else 0.0

                Stat(
                    title = "URLs",
                    firstMetric = state.firstStat?.data?.urls,
                    lastMetric = state.lastStat?.data?.urls,
                    loading = state.firstStat == null || state.lastStat == null,
                    difference = urlsDifference,
                    infinite = urlsDifference == Double.POSITIVE_INFINITY
                )

                StatDivider()

                val storageDifference = if (state.firstStat != null && state.lastStat != null) {
                    getMetricsDifference(
                        firstMetric = state.firstStat!!.data.storage.toDouble(),
                        lastMetric = state.lastStat!!.data.storage.toDouble()
                    )
                } else 0.0

                Stat(
                    title = "Storage Used",
                    firstMetric = state.firstStat?.data?.storage,
                    lastMetric = state.lastStat?.data?.storage,
                    loading = state.firstStat == null || state.lastStat == null,
                    difference = storageDifference,
                    formatValue = { value ->
                        formatBytes(value ?: 0L)
                    },
                    infinite = storageDifference == Double.POSITIVE_INFINITY
                )

                StatDivider()

                val fileViewsDifference = if (state.firstStat != null && state.lastStat != null) {
                    getMetricsDifference(
                        firstMetric = state.firstStat!!.data.fileViews.toDouble(),
                        lastMetric = state.lastStat!!.data.fileViews.toDouble()
                    )
                } else 0.0

                Stat(
                    title = "File Views",
                    firstMetric = state.firstStat?.data?.fileViews,
                    lastMetric = state.lastStat?.data?.fileViews,
                    loading = state.firstStat == null || state.lastStat == null,
                    difference = fileViewsDifference,
                    infinite = fileViewsDifference == Double.POSITIVE_INFINITY
                )

                val urlViewsDifference = if (state.firstStat != null && state.lastStat != null) {
                    getMetricsDifference(
                        firstMetric = state.firstStat!!.data.urlViews.toDouble(),
                        lastMetric = state.lastStat!!.data.urlViews.toDouble()
                    )
                } else 0.0

                Stat(
                    title = "URL Views",
                    firstMetric = state.firstStat?.data?.urlViews,
                    lastMetric = state.lastStat?.data?.urlViews,
                    loading = state.firstStat == null || state.lastStat == null,
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
                            name = "user",
                            width = tableUserWidth
                        ) {
                            Text(
                                text = "User",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        TableHeaderData(
                            name = "URLs",
                            width = tableUrlsWidth
                        ) {
                            Text(
                                text = "URLs",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        TableHeaderData(
                            name = "views",
                            width = tableViewsWidth
                        ) {
                            Text(
                                text = "Views",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )

                    val rows: List<TableRowData>? = state.firstStat?.data?.urlsUsers?.map { userUrl ->
                        TableRowData(
                            cells = listOf(
                                TableCellData(
                                    width = tableUserWidth
                                ) {
                                    Text(
                                        text = userUrl.username ?: "Unknown User",
                                    )
                                },
                                TableCellData(
                                    width = tableUrlsWidth
                                ) {
                                    Text(
                                        text = userUrl.sum.toString(),
                                    )
                                },
                                TableCellData(
                                    width = tableViewsWidth
                                ) {
                                    Text(
                                        text = userUrl.views.toString(),
                                    )
                                }
                            )
                        )
                    }

                    Table(
                        headers = headers,
                        rows = rows ?: emptyList(),
                        loading = state.firstStat?.data?.urlsUsers == null
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
                            name = "user",
                            width = tableUserWidth
                        ) {
                            Text(
                                text = "User",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        TableHeaderData(
                            name = "files",
                            width = tableFilesWidth
                        ) {
                            Text(
                                text = "Files",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        TableHeaderData(
                            name = "storage used",
                            width = tableStorageUsedWidth
                        ) {
                            Text(
                                text = "Storage Used",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        TableHeaderData(
                            name = "views",
                            width = tableViewsWidth
                        ) {
                            Text(
                                text = "Views",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )

                    val rows: List<TableRowData>? = state.firstStat?.data?.filesUsers?.map { userFile ->
                        TableRowData(
                            cells = listOf(
                                TableCellData(
                                    width = tableUserWidth
                                ) {
                                    Text(
                                        text = userFile.username ?: "Unknown User",
                                    )
                                },
                                TableCellData(
                                    width = tableFilesWidth
                                ) {
                                    Text(
                                        text = userFile.sum.toString(),
                                    )
                                },
                                TableCellData(
                                    width = tableStorageUsedWidth
                                ) {
                                    Text(
                                        text = formatBytes(userFile.storage),
                                    )
                                },
                                TableCellData(
                                    width = tableViewsWidth
                                ) {
                                    Text(
                                        text = userFile.views.toString(),
                                    )
                                }
                            )
                        )
                    }

                    Table(
                        headers = headers,
                        rows = rows ?: emptyList(),
                        loading = state.firstStat?.data?.urlsUsers == null
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
                            name = "type",
                            width = tableTypeWidth
                        ) {
                            Text(
                                text = "Type",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        TableHeaderData(
                            name = "files",
                            width = tableFilesWidth
                        ) {
                            Text(
                                text = "Files",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )

                    val rows: List<TableRowData>? = state.firstStat?.data?.types?.map { typeData ->
                        TableRowData(
                            cells = listOf(
                                TableCellData(
                                    width = tableTypeWidth
                                ) {
                                    Text(
                                        text = typeData.type,
                                    )
                                },
                                TableCellData(
                                    width = tableFilesWidth
                                ) {
                                    Text(
                                        text = typeData.sum.toString(),
                                    )
                                }
                            )
                        )
                    }

                    Table(
                        headers = headers,
                        rows = rows ?: emptyList(),
                        loading = state.firstStat?.data?.urlsUsers == null
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

                    LaunchedEffect(state.firstStat, state.selectedPieChartType) {
                        viewModel.refreshPieChartTypes()
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

                        if (!state.isLoading && state.pieChartTypes.isNotEmpty()) {
                            PieChart(
                                data = state.pieChartTypes,
                                modifier = Modifier
                                    .size(chartSize)
                                    .align(Alignment.CenterHorizontally)
                                    .padding(vertical = 20.dp),
                                style = Pie.Style.Fill,
                                selectedScale = 1.2f,
                                onPieClick = { clickedPie ->
                                    viewModel.setSelectedPieChartType(clickedPie)
                                },
                                labelHelperProperties = LabelHelperProperties(
                                    enabled = false
                                )
                            )

                            if (state.selectedPieChartType != null && state.pieChartTypes.find { it.label == state.selectedPieChartType?.label } != null) {
                                Row {
                                    Box(
                                        modifier = Modifier
                                            .size(15.dp)
                                            .background(state.selectedPieChartType?.color ?: Color(0xFFFFFFFF))
                                            .align(Alignment.CenterVertically)
                                    )

                                    Spacer(
                                        modifier = Modifier.width(6.dp)
                                    )

                                    Text(
                                        text = "${state.selectedPieChartType?.label}: ${state.selectedPieChartType?.data?.toLong()}"
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

                    if (state.stats != null) {
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
                            data = state.viewsLineChartLines,
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
                                    viewModel.setViewsLineChartActivePopupIndex(value.valueIndex)

                                    val metric = state.stats?.get(value.valueIndex)
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

                    if (state.stats != null) {
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
                            data = state.countLineChartLines,
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
                                    viewModel.setCountLineChartActivePopupIndex(value.valueIndex)

                                    val metric = state.stats?.get(value.valueIndex)
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



                    if (state.stats != null) {
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
                            data = state.storageUsedLineChartLines,
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
                                    viewModel.setStorageLineChartUsedActivePopupIndex(value.valueIndex)

                                    val metric = state.stats?.get(value.valueIndex)
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





