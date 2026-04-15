package com.stefdp.zipline.widgets.stats

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image as GlanceImage
import androidx.glance.ImageProvider as GlanceImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn as GlanceLazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment as GlanceAlignment
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.Spacer as GlanceSpacer
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.layout.Row as GlanceRow
import androidx.glance.layout.Column as GlanceColumn
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight as GlanceFontWeight
import androidx.glance.text.TextAlign as GlanceTextAlign
import androidx.glance.unit.ColorProvider
import com.stefdp.zipline.R
import com.stefdp.zipline.network.models.Metric
import com.stefdp.zipline.network.requests.getServerStats
import com.stefdp.zipline.ui.theme.DarkWidgetBackground
import com.stefdp.zipline.ui.theme.DarkWidgetOnSurfaceVariant
import com.stefdp.zipline.ui.theme.DarkWidgetSurface
import com.stefdp.zipline.ui.theme.LightWidgetBackground
import com.stefdp.zipline.ui.theme.LightWidgetOnSurfaceVariant
import com.stefdp.zipline.ui.theme.LightWidgetSurface
import com.stefdp.zipline.ui.theme.ZiplineWidgetTheme
import com.stefdp.zipline.utils.STORAGE_SERVER_URL_KEY
import com.stefdp.zipline.utils.SecureStorage
import com.stefdp.zipline.utils.formatBytes
import com.stefdp.zipline.utils.getMetricsDifference
import com.stefdp.zipline.widgets.CELL_HEIGHT
import com.stefdp.zipline.widgets.CELL_WIDTH
import com.stefdp.zipline.widgets.components.Text as GlanceText
import com.stefdp.zipline.widgets.cornerRadius
import ir.ehsannarmani.compose_charts.extensions.format
import java.time.Duration
import java.time.Instant

const val StringOpacityKey = "stats_backgroundOpacity"
val OpacityKey = floatPreferencesKey(StringOpacityKey)

open class StatsWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val secureStore = SecureStorage.getInstance(context)

        secureStore.get(STORAGE_SERVER_URL_KEY) ?: return

        val startDate = Instant
            .now()
            .minus(Duration.ofDays(7))
            .toString()

        val endDate = Instant.now().toString()

        var stats = emptyList<Metric>()

        val statsRes = getServerStats(
            context = context,
            from = startDate,
            to = endDate
        )

        statsRes.onSuccess {
            stats = it
        }

        provideContent {
            val prefs = currentState<Preferences>()
            val backgroundOpacity = prefs[OpacityKey] ?: 1f

            WidgetContent(
                stats = stats,
                backgroundOpacity = backgroundOpacity
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(
    widthDp = CELL_WIDTH * 2,
    heightDp = CELL_HEIGHT * 2
)
@Composable
private fun WidgetContent(
    stats: List<Metric> = emptyList(),
    backgroundOpacity: Float = 1f,
) {
    ZiplineWidgetTheme {
        val firstStat = stats.firstOrNull()
        val lastStat = stats.lastOrNull()

        val dynamicBackground = ColorProvider(
            day = LightWidgetBackground.copy(alpha = backgroundOpacity),
            night = DarkWidgetBackground.copy(alpha = backgroundOpacity)
        )

        val boxOpacity = (backgroundOpacity + (80f / 255f)).coerceAtMost(1f)

        val dynamicBoxBackground = ColorProvider(
            day = LightWidgetSurface.copy(alpha = boxOpacity),
            night = DarkWidgetSurface.copy(alpha = boxOpacity)
        )

        GlanceLazyColumn(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(dynamicBackground)
                .padding(8.dp)
                .cornerRadius(cornerRadius),
            horizontalAlignment = GlanceAlignment.CenterHorizontally,
        ) {
            @Composable
            fun StatsSpacer() {
                GlanceSpacer(
                    modifier = GlanceModifier.height(8.dp)
                )
            }

            item {
                val filesDifference = if (firstStat != null && lastStat != null) {
                    getMetricsDifference(
                        firstMetric = firstStat.data.files.toDouble(),
                        lastMetric = lastStat.data.files.toDouble()
                    )
                } else 0.0

                Stat(
                    title = "Files",
                    firstMetric = firstStat?.data?.files,
                    difference = filesDifference,
                    infinite = filesDifference == Double.POSITIVE_INFINITY,
                    color = dynamicBoxBackground
                )
            }

            item {
                StatsSpacer()
            }

            item {
                val urlsDifference = if (firstStat != null && lastStat != null) {
                    getMetricsDifference(
                        firstMetric = firstStat.data.urls.toDouble(),
                        lastMetric = lastStat.data.urls.toDouble()
                    )
                } else 0.0

                Stat(
                    title = "URLs",
                    firstMetric = firstStat?.data?.urls,
                    difference = urlsDifference,
                    infinite = urlsDifference == Double.POSITIVE_INFINITY,
                    color = dynamicBoxBackground
                )
            }

            item {
                StatsSpacer()
            }

            item {
                val storageDifference = if (firstStat != null && lastStat != null) {
                    getMetricsDifference(
                        firstMetric = firstStat.data.storage.toDouble(),
                        lastMetric = lastStat.data.storage.toDouble()
                    )
                } else 0.0

                Stat(
                    title = "Storage Used",
                    firstMetric = firstStat?.data?.storage,
                    difference = storageDifference,
                    formatValue = { value ->
                        formatBytes(value ?: 0L)
                    },
                    infinite = storageDifference == Double.POSITIVE_INFINITY,
                    color = dynamicBoxBackground
                )
            }

            item {
                StatsSpacer()
            }

            item {
                val fileViewsDifference = if (firstStat != null && lastStat != null) {
                    getMetricsDifference(
                        firstMetric = firstStat.data.fileViews.toDouble(),
                        lastMetric = lastStat.data.fileViews.toDouble()
                    )
                } else 0.0

                Stat(
                    title = "File Views",
                    firstMetric = firstStat?.data?.fileViews,
                    difference = fileViewsDifference,
                    infinite = fileViewsDifference == Double.POSITIVE_INFINITY,
                    color = dynamicBoxBackground
                )
            }

            item {
                StatsSpacer()
            }

            item {
                val urlViewsDifference = if (firstStat != null && lastStat != null) {
                    getMetricsDifference(
                        firstMetric = firstStat.data.urlViews.toDouble(),
                        lastMetric = lastStat.data.urlViews.toDouble()
                    )
                } else 0.0

                Stat(
                    title = "URL Views",
                    firstMetric = firstStat?.data?.urlViews,
                    difference = urlViewsDifference,
                    infinite = urlViewsDifference == Double.POSITIVE_INFINITY,
                    color = dynamicBoxBackground
                )
            }
        }
    }
}

@Composable
fun Stat(
    title: String,
    firstMetric: Long?,
    difference: Double,
    infinite: Boolean = false,
    formatValue: (Long?) -> String? = { it.toString() },
    color: ColorProvider
) {
    val dynamicTitleColor = ColorProvider(
        day = LightWidgetOnSurfaceVariant.copy(alpha = 0.5f),
        night = DarkWidgetOnSurfaceVariant.copy(alpha = 0.5f)
    )

    GlanceRow(
        verticalAlignment = GlanceAlignment.CenterVertically,
        horizontalAlignment = GlanceAlignment.CenterHorizontally,
        modifier = GlanceModifier
            .fillMaxWidth()
            .cornerRadius(cornerRadius)
            .background(color)
    ) {
        GlanceColumn(
            modifier = GlanceModifier.padding(12.dp),
            horizontalAlignment = GlanceAlignment.CenterHorizontally
        ) {
            GlanceText(
                text = title,
                color = dynamicTitleColor,
                fontWeight = GlanceFontWeight.Bold,
                textAlign = GlanceTextAlign.Center,
                fontSize = 18.sp
            )

            GlanceSpacer(
                modifier = GlanceModifier.height(8.dp)
            )

            GlanceRow(
                verticalAlignment = GlanceAlignment.CenterVertically
            ) {
                GlanceText(
                    text = formatValue(firstMetric) ?: "Loading",
                    color = GlanceTheme.colors.onSurfaceVariant,
                )

                GlanceSpacer(
                    modifier = GlanceModifier.width(12.dp)
                )

                StatDifference(
                    difference = if (infinite) 1.0 else difference,
                    infinite = infinite,
                )
            }
        }
    }
}

@Composable
private fun StatDifference(
    difference: Double,
    infinite: Boolean = false,
) {
    val diff = if (infinite) "∞" else difference.format(2)

    val _color = when {
        difference > 0 -> Color.Green.copy(alpha = 0.7f)
        difference < 0 -> Color.Red.copy(alpha = 0.7f)
        else -> Color.Gray.copy(alpha = 0.7f)
    }

    val color = ColorProvider(
        day = _color,
        night = _color
    )

    val backgroundColor = ColorProvider(
        day = _color.copy(alpha = 0.1f),
        night = _color.copy(alpha = 0.1f)
    )

    GlanceRow(
        modifier = GlanceModifier
            .background(backgroundColor)
            .cornerRadius(4.dp)
            .padding(4.dp),
        verticalAlignment = GlanceAlignment.CenterVertically
    ) {
        val image = if (difference > 0) GlanceImageProvider(R.drawable.north)
            else if (difference < 0) GlanceImageProvider(R.drawable.south)
            else GlanceImageProvider(R.drawable.remove)

        val imageContentDescription = if (difference >= 0) "$diff% increase"
            else if (difference < 0) "$diff% decrease"
            else "No change"

        GlanceImage(
            provider = image,
            contentDescription = imageContentDescription,
            colorFilter = ColorFilter.tint(color),
            modifier = GlanceModifier.size(16.dp)
        )

        GlanceText(
            text = "$diff%",
            color = color,
        )
    }
}

@Composable
fun WidgetPreview(
    modifier: Modifier = Modifier,
    stats: List<Metric> = emptyList(),
    backgroundOpacity: Float = 1f,
    width: Dp = (CELL_WIDTH * 2).dp,
    height: Dp = (CELL_HEIGHT * 2).dp,
) {
    val firstStat = stats.firstOrNull()
    val lastStat = stats.lastOrNull()

    val dynamicBackground = MaterialTheme.colorScheme.background.copy(alpha = backgroundOpacity)

    val boxOpacity = (backgroundOpacity + (80f / 255f)).coerceAtMost(1f)

    val dynamicBoxBackground = MaterialTheme.colorScheme.surface.copy(alpha = boxOpacity)

    LazyColumn(
        modifier = modifier
            .width(width)
            .height(height)
            .background(dynamicBackground)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        @Composable
        fun StatsSpacer() {
            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }

        item {
            val filesDifference = if (firstStat != null && lastStat != null) {
                getMetricsDifference(
                    firstMetric = firstStat.data.files.toDouble(),
                    lastMetric = lastStat.data.files.toDouble()
                )
            } else 0.0

            StatPreview(
                title = "Files",
                firstMetric = firstStat?.data?.files,
                difference = filesDifference,
                infinite = filesDifference == Double.POSITIVE_INFINITY,
                color = dynamicBoxBackground
            )
        }

        item {
            StatsSpacer()
        }

        item {
            val urlsDifference = if (firstStat != null && lastStat != null) {
                getMetricsDifference(
                    firstMetric = firstStat.data.urls.toDouble(),
                    lastMetric = lastStat.data.urls.toDouble()
                )
            } else 0.0

            StatPreview(
                title = "URLs",
                firstMetric = firstStat?.data?.urls,
                difference = urlsDifference,
                infinite = urlsDifference == Double.POSITIVE_INFINITY,
                color = dynamicBoxBackground
            )
        }

        item {
            StatsSpacer()
        }

        item {
            val storageDifference = if (firstStat != null && lastStat != null) {
                getMetricsDifference(
                    firstMetric = firstStat.data.storage.toDouble(),
                    lastMetric = lastStat.data.storage.toDouble()
                )
            } else 0.0

            StatPreview(
                title = "Storage Used",
                firstMetric = firstStat?.data?.storage,
                difference = storageDifference,
                formatValue = { value ->
                    formatBytes(value ?: 0L)
                },
                infinite = storageDifference == Double.POSITIVE_INFINITY,
                color = dynamicBoxBackground
            )
        }

        item {
            StatsSpacer()
        }

        item {
            val fileViewsDifference = if (firstStat != null && lastStat != null) {
                getMetricsDifference(
                    firstMetric = firstStat.data.fileViews.toDouble(),
                    lastMetric = lastStat.data.fileViews.toDouble()
                )
            } else 0.0

            StatPreview(
                title = "File Views",
                firstMetric = firstStat?.data?.fileViews,
                difference = fileViewsDifference,
                infinite = fileViewsDifference == Double.POSITIVE_INFINITY,
                color = dynamicBoxBackground
            )
        }

        item {
            StatsSpacer()
        }

        item {
            val urlViewsDifference = if (firstStat != null && lastStat != null) {
                getMetricsDifference(
                    firstMetric = firstStat.data.urlViews.toDouble(),
                    lastMetric = lastStat.data.urlViews.toDouble()
                )
            } else 0.0

            StatPreview(
                title = "URL Views",
                firstMetric = firstStat?.data?.urlViews,
                difference = urlViewsDifference,
                infinite = urlViewsDifference == Double.POSITIVE_INFINITY,
                color = dynamicBoxBackground
            )
        }
    }
}

@Composable
fun StatPreview(
    title: String,
    firstMetric: Long?,
    difference: Double,
    infinite: Boolean = false,
    formatValue: (Long?) -> String? = { it.toString() },
    color: Color
) {
    val dynamicTitleColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .background(color)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = dynamicTitleColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 18.sp
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatValue(firstMetric) ?: "Loading",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                StatDifferencePreview(
                    difference = if (infinite) 1.0 else difference,
                    infinite = infinite,
                )
            }
        }
    }
}

@Composable
private fun StatDifferencePreview(
    difference: Double,
    infinite: Boolean = false,
) {
    val diff = if (infinite) "∞" else difference.format(2)

    val color = when {
        difference > 0 -> Color.Green.copy(alpha = 0.7f)
        difference < 0 -> Color.Red.copy(alpha = 0.7f)
        else -> Color.Gray.copy(alpha = 0.7f)
    }

    Row(
        modifier = Modifier
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val image = if (difference > 0) painterResource(R.drawable.north)
            else if (difference < 0) painterResource(R.drawable.south)
            else painterResource(R.drawable.remove)

        val imageContentDescription = if (difference >= 0) "$diff% increase"
            else if (difference < 0) "$diff% decrease"
            else "No change"

        Icon(
            painter = image,
            contentDescription = imageContentDescription,
            tint = color,
            modifier = Modifier.size(16.dp),
        )

        Text(
            text = "$diff%",
            color = color,
        )
    }
}