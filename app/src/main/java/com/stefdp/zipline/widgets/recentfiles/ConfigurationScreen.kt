package com.stefdp.zipline.widgets.recentfiles

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.lifecycle.lifecycleScope
import com.stefdp.zipline.components.Slider
import com.stefdp.zipline.network.models.File
import com.stefdp.zipline.network.requests.getRecentFiles
import com.stefdp.zipline.ui.theme.ZiplineTheme
import com.stefdp.zipline.utils.STORAGE_SERVER_URL_KEY
import com.stefdp.zipline.utils.SecureStorage
import com.stefdp.zipline.widgets.CELL_HEIGHT
import com.stefdp.zipline.widgets.CELL_WIDTH
import com.stefdp.zipline.widgets.cornerRadius
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class RecentFilesWidgetConfigurationActivity : ComponentActivity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val resultValue = Intent().putExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            appWidgetId
        )

        setResult(RESULT_CANCELED, resultValue)

        setContent {
            ZiplineTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                        color = Color.Transparent
                    ) {
                        WidgetConfigScreen(
                            context = applicationContext,
                            appWidgetId = appWidgetId,
                            onSaveConfig = { backgroundOpacity, fileCount ->
                                saveWidgetConfiguration(
                                    context = applicationContext,
                                    backgroundOpacity = backgroundOpacity,
                                    fileCount = fileCount
                                )
                            },
                            finish = { finish() }
                        )
                    }
                }
            }
        }
    }

    private fun saveWidgetConfiguration(
        context: Context,
        backgroundOpacity: Float,
        fileCount: Int,
    ) {
        lifecycleScope.launch {
            val secureStore = SecureStorage.getInstance(context)

            secureStore.set("${StringOpacityKey}_$appWidgetId", backgroundOpacity.toString())
            secureStore.set("${StringFileCountKey}_$appWidgetId", fileCount.toString())

            val glanceAppWidgetManager = GlanceAppWidgetManager(applicationContext)
            val glanceId = glanceAppWidgetManager.getGlanceIdBy(appWidgetId)

            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[OpacityKey] = backgroundOpacity
            }

            RecentFilesWidget().update(applicationContext, glanceId)

            val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(RESULT_OK, resultValue)

            finish()
        }
    }
}

@Composable
fun WidgetConfigScreen(
    context: Context,
    appWidgetId: Int,
    onSaveConfig: (Float, Int) -> Unit,
    finish: () -> Unit
) {
    var backgroundOpacity by remember { mutableFloatStateOf(1f) }
    var recentFiles by remember { mutableStateOf<List<File>>(emptyList()) }
    var serverUrl by remember { mutableStateOf<String?>(null) }
    var fileCount by remember { mutableIntStateOf(5) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val secureStore = SecureStorage.getInstance(context)

        serverUrl = secureStore.get(STORAGE_SERVER_URL_KEY)
        fileCount = secureStore.get("${StringFileCountKey}_$appWidgetId")?.toIntOrNull() ?: 5

        if (serverUrl == null) {
            Toast.makeText(
                context,
                "Please login first",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return@LaunchedEffect
        }

        backgroundOpacity = secureStore.get("${StringOpacityKey}_$appWidgetId")?.toFloatOrNull() ?: 1f
    }

    LaunchedEffect(fileCount) {
        val recentFilesRes = getRecentFiles(
            context = context,
            count = fileCount
        )

        recentFilesRes.onSuccess {
            recentFiles = it
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            WidgetPreview(
                recentFiles = recentFiles,
                serverUrl = serverUrl ?: "https://example.com",
                backgroundOpacity = backgroundOpacity,
                modifier = Modifier.clip(
                    RoundedCornerShape(cornerRadius)
                ),
                height = (CELL_HEIGHT * 2).dp,
                width = (CELL_WIDTH * 2).dp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(
                    topStart = 40.dp,
                    topEnd = 40.dp
                ))
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp)
        ) {
            Text(
                text = "Widget Configuration",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Slider(
                value = backgroundOpacity,
                onValueChange = { backgroundOpacity = it },
                label = "Opacity:",
            )

            Slider(
                value = fileCount.toFloat() / 100f,
                onValueChange = { fileCount = (it * 100f).roundToInt() },
                steps = 0,
                valueRange = 0.01f..0.3f,
                label = "File Count ($fileCount):",
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Row {
                Button(
                    onClick = { finish() },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = "Cancel",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(
                    modifier = Modifier.width(20.dp)
                )

                Button(
                    onClick = {
                        onSaveConfig(backgroundOpacity, fileCount)
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = "Save",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.NONE, backgroundColor = 0xFF3F008B,
)
@Composable
fun Preview() {
    val context = LocalContext.current

    ZiplineTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                color = Color.Transparent
            ) {
                WidgetConfigScreen(
                    context = context,
                    appWidgetId = 1,
                    onSaveConfig = { backgroundOpacity, fileCount ->
                        Log.d("WidgetConfigScreen", "Saving config with backgroundOpacity: $backgroundOpacity")
//                        saveWidgetConfiguration(
//                            context = applicationContext,
//                            backgroundOpacity = backgroundOpacity
//                        )
                    },
                    finish = { Log.d("WidgetConfigScreen", "Finish called") }
                )
            }
        }
    }
}

