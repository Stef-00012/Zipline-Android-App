package com.stefdp.zipline.widgets.recentfiles

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image as GlanceImage
import androidx.glance.ImageProvider as GlanceImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn as GlanceLazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Column as GlanceColumn
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.Alignment as GlanceAlignment
import androidx.glance.layout.Box as GlanceBox
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.ContentScale as GlanceContentScale
import androidx.glance.layout.Spacer as GlanceSpacer
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.TextAlign as GlanceTextAlign
import androidx.glance.unit.ColorProvider
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.CachePolicy
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.size.Scale
import coil3.toBitmap
import com.stefdp.zipline.R
import com.stefdp.zipline.network.models.File
import com.stefdp.zipline.network.requests.getRecentFiles
import com.stefdp.zipline.ui.theme.DarkWidgetBackground
import com.stefdp.zipline.ui.theme.DarkWidgetSurface
import com.stefdp.zipline.ui.theme.LightWidgetBackground
import com.stefdp.zipline.ui.theme.LightWidgetSurface
import com.stefdp.zipline.ui.theme.ZiplineWidgetTheme
import com.stefdp.zipline.utils.STORAGE_SERVER_URL_KEY
import com.stefdp.zipline.utils.SecureStorage
import com.stefdp.zipline.utils.formatBytes
import com.stefdp.zipline.widgets.CELL_HEIGHT
import com.stefdp.zipline.widgets.CELL_WIDTH
import com.stefdp.zipline.widgets.WIDGET_IMAGE_SIZE
import com.stefdp.zipline.widgets.cornerRadius
import com.stefdp.zipline.widgets.components.Text as GlanceText

const val StringOpacityKey = "recentFiles_backgroundOpacity"
val OpacityKey = floatPreferencesKey(StringOpacityKey)

const val StringFileCountKey = "recentFiles_fileCount"

private val supportedMimeTypes = listOf(
    "image/png",
    "image/jpeg",
    "image/webp",
    "image/svg+xml",
)

open class RecentFilesWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val secureStore = SecureStorage.getInstance(context)

        val manager = GlanceAppWidgetManager(context)

        val appWidgetId = manager.getAppWidgetId(id)

        val recentFileCount = secureStore.get("${StringFileCountKey}_$appWidgetId")?.toIntOrNull() ?: 5
        val serverUrl = secureStore.get(STORAGE_SERVER_URL_KEY) ?: return

        var recentFiles: List<File> = emptyList()

        val recentFilesRes = getRecentFiles(
            context = context,
            count = recentFileCount
        )

        recentFilesRes.onSuccess {
            recentFiles = it
        }

        provideContent {
            val prefs = currentState<Preferences>()
            val backgroundOpacity = prefs[OpacityKey] ?: 1f

            WidgetContent(
                recentFiles = recentFiles,
                serverUrl = serverUrl,
                backgroundOpacity = backgroundOpacity
            )
        }
    }
}

private suspend fun Context.fetchImage(url: String, force: Boolean = false): Bitmap? {
    val request = ImageRequest
        .Builder(this)
        .data(url)
        .size(
            width = WIDGET_IMAGE_SIZE,
            height = WIDGET_IMAGE_SIZE
        )
        .scale(Scale.FILL)
        .apply {
            if (force) {
                memoryCachePolicy(CachePolicy.DISABLED)
                diskCachePolicy(CachePolicy.DISABLED)
            }
        }.build()

    return when (val result = imageLoader.execute(request)) {
        is ErrorResult -> throw result.throwable
        is SuccessResult -> result.image.toBitmap()
    }
}
//TODO: limit recent file count
// 300x300 = 30 images
@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(
    widthDp = CELL_WIDTH * 2,
    heightDp = CELL_HEIGHT * 2
)
@Composable
private fun WidgetContent(
    recentFiles: List<File> = emptyList(),
    serverUrl: String = "https://example.com",
    backgroundOpacity: Float = 1f,
) {
    ZiplineWidgetTheme {
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
                .padding(8.dp),
            horizontalAlignment = GlanceAlignment.CenterHorizontally,
        ) {
            items(recentFiles.size) { index ->
                val file = recentFiles[index]
                val isLast = index == recentFiles.lastIndex

                GlanceColumn {
                    File(
                        file = file,
                        serverUrl = serverUrl,
                        color = dynamicBoxBackground
                    )

                    if (!isLast) {
                        GlanceSpacer(
                            modifier = GlanceModifier.height(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun File(
    file: File,
    serverUrl: String,
    color: ColorProvider
) {
    val context = LocalContext.current

    GlanceBox(
        contentAlignment = GlanceAlignment.Center,
        modifier = GlanceModifier
            .fillMaxWidth()
            .height(150.dp)
            .cornerRadius(cornerRadius)
            .background(color)
            .padding(8.dp)
    ) {
        if (file.type in supportedMimeTypes) {
            var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

            LaunchedEffect(Unit) {
                imageBitmap = context.fetchImage("$serverUrl/raw/${file.name}")
            }

            if (imageBitmap != null) {
                GlanceImage(
                    provider = GlanceImageProvider(imageBitmap!!),
                    contentScale = GlanceContentScale.FillBounds,
                    contentDescription = file.name,
                    modifier = GlanceModifier
                        .cornerRadius(cornerRadius)
                )
            }
        } else {
            GlanceColumn(
                horizontalAlignment = GlanceAlignment.CenterHorizontally,
            ) {
                GlanceImage(
                    provider = GlanceImageProvider(R.drawable.description),
                    contentScale = GlanceContentScale.FillBounds,
                    contentDescription = file.name,
                    modifier = GlanceModifier
                        .cornerRadius(cornerRadius)
                        .size(50.dp)
                )

                GlanceSpacer(
                    modifier = GlanceModifier.height(4.dp)
                )

                GlanceText(
                    text = "${file.name}\n${file.type}\n(${formatBytes(file.size)})",
                    maxLines = 4,
                    textAlign = GlanceTextAlign.Center
                )
            }
        }
    }
}

@Composable
fun WidgetPreview(
    modifier: Modifier = Modifier,
    recentFiles: List<File> = emptyList(),
    serverUrl: String = "https://example.com",
    backgroundOpacity: Float = 1f,
    width: Dp = (CELL_WIDTH * 2).dp,
    height: Dp = (CELL_HEIGHT * 2).dp,
) {
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
        items(recentFiles.size) { index ->
            val file = recentFiles[index]
            val isLast = index == recentFiles.lastIndex

            Column() {
                FilePreview(
                    file = file,
                    serverUrl = serverUrl,
                    color = dynamicBoxBackground
                )

                if (!isLast) {
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FilePreview(
    file: File,
    serverUrl: String,
    color: Color
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(cornerRadius))
            .background(color)
            .padding(8.dp)
    ) {
        val isLoadingSuccessful = remember { mutableStateOf(true) }

        if (file.type in supportedMimeTypes) {
            AsyncImage(
                model = "$serverUrl/raw/${file.name}",
                contentDescription = file.name,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .clip(RoundedCornerShape(cornerRadius)),
                onError = {
                    isLoadingSuccessful.value = false
                }
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
//                GlanceImage(
//                    provider = GlanceImageProvider(R.drawable.description),
//                    contentScale = GlanceContentScale.FillBounds,
//                    contentDescription = file.name,
//                    modifier = GlanceModifier
//                        .cornerRadius(cornerRadius)
//                        .size(50.dp)
//                )

                Icon(
                    painter = painterResource(R.drawable.description),
                    contentDescription = file.name,
                    modifier = Modifier
                        .clip(RoundedCornerShape(cornerRadius))
                        .size(50.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "${file.name}\n${file.type}\n(${formatBytes(file.size)})",
                    maxLines = 4,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}