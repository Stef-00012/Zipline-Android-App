package com.stefdp.zipline.screens.home

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.LocalLoggedUser
import com.stefdp.zipline.LocalPublicSettings
import com.stefdp.zipline.LocalUpdatePublicSettings
import com.stefdp.zipline.LocalUpdateWebSettings
import com.stefdp.zipline.LocalWebSettings
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.components.FilePreview
import com.stefdp.zipline.components.largefiledisplay.LargeFileDisplay
import com.stefdp.zipline.components.table.Table
import com.stefdp.zipline.components.table.TableCellData
import com.stefdp.zipline.components.table.TableHeaderData
import com.stefdp.zipline.components.table.TableRowData
import com.stefdp.zipline.network.models.File
import com.stefdp.zipline.network.models.UserQuotaFilesQuota
import com.stefdp.zipline.network.models.responses.GetStatsResponse
import com.stefdp.zipline.network.requests.getRecentFiles
import com.stefdp.zipline.network.requests.getStats
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.screens.home.components.Stat
import com.stefdp.zipline.utils.formatBytes
import com.stefdp.zipline.utils.horizontalLazyScrollbar
import com.stefdp.zipline.utils.parseBytes
import com.stefdp.zipline.utils.shimmerable
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity
) {
    val localLoggedUser = LocalLoggedUser.current

    val publicSettings = LocalPublicSettings.current
    val updatePublicSettings = LocalUpdatePublicSettings.current

    val webSettings = LocalWebSettings.current
    val updateWebSettings = LocalUpdateWebSettings.current

    if (localLoggedUser == null) {
        navController.navigate(LoginScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    var userStats by remember { mutableStateOf<GetStatsResponse?>(null) }
    var recentFiles by remember { mutableStateOf<List<File>>(emptyList()) }

    suspend fun updateData() {
        val recentFilesRes = getRecentFiles(
            context = context,
            count = 8
        )

        val userStatsRes = getStats(
            context = context,
        )

        recentFilesRes.onSuccess {
            recentFiles = it
        }

        userStatsRes.onSuccess {
            userStats = it
        }
    }

    LaunchedEffect(Unit) {
        if (publicSettings == null) {
            updatePublicSettings()
        }

        if (webSettings == null) {
            updateWebSettings()
        }

        updateData()
    }

    val mainScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 12.dp,
                end = 12.dp,
                top = 12.dp
            )
            .verticalScroll(mainScrollState)
    ) {
        val username = localLoggedUser?.username ?: "Unknown"

        Text(
            text = "Welcome back, $username",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(
                start = 4.dp
            )
        )

        val filesUploaded = userStats?.filesUploaded

        Text(
            text = "You have $filesUploaded files uploaded.",
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .shimmerable(
                    enabled = userStats == null,
                )
                .padding(
                    start = 4.dp
                )
        )

        if (localLoggedUser?.quota?.filesQuota == UserQuotaFilesQuota.BY_FILES) {
            val maxFilesAllowed = localLoggedUser.quota.maxFiles

            Text(
                text = "You have uploaded $filesUploaded files out of $maxFilesAllowed files allowed.",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .shimmerable(
                        enabled = userStats == null,
                    )
                    .padding(
                        start = 4.dp
                    )
            )
        }

        if (localLoggedUser?.quota?.filesQuota == UserQuotaFilesQuota.BY_BYTES) {
            val storageUsed = formatBytes(userStats?.storageUsed ?: 0L)
            val maxAllowedStorage = formatBytes(
                parseBytes(localLoggedUser.quota.maxBytes)
            )

            Text(
                text = "You have used $storageUsed out of $maxAllowedStorage of storage.",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .shimmerable(
                        enabled = userStats == null,
                    )
                    .padding(
                        start = 4.dp
                    )
            )
        }

        if (localLoggedUser?.quota?.maxUrls != null) {
            val urlsCreated = userStats?.urlsCreated
            val maxUrlsAllowed = localLoggedUser.quota.maxUrls

            Text(
                text = "You have created $urlsCreated out of ${maxUrlsAllowed} links allowed.",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .shimmerable(
                        enabled = userStats == null,
                    )
                    .padding(
                        start = 4.dp
                    )
            )
        }

        Text(
            text = "Recent Files",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .padding(
                    top = 12.dp,
                    bottom = 4.dp,
                    start = 4.dp
                )
        )

        Container(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(MaterialTheme.colorScheme.surface),
            scrollable = false
        ) {
            var clickedFile by remember { mutableStateOf<File?>(null) }

            LaunchedEffect(recentFiles) {
                if (clickedFile != null) {
                    val updatedFile = recentFiles.find { it.id == clickedFile?.id }

                    if (updatedFile != null) {
                        clickedFile = updatedFile
                    }
                }
            }

            LargeFileDisplay(
                context = context,
                file = clickedFile,
                onDismissRequest = { clickedFile = null },
                updateData = ::updateData,
                onDelete = { clickedFile = null }
            )

            val lazyListState = rememberLazyListState()

            LazyRow(
                state = lazyListState,
                modifier = Modifier.horizontalLazyScrollbar(
                    listState = lazyListState
                )
            ) {
                if (recentFiles.isEmpty()) {
                    items(8) {
                        Box(
                            modifier = Modifier
                                .padding(10.dp)
                                .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                                .size(200.dp)
                                .shimmerable(
                                    enabled = true,
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    keepBackground = true
                                ),
                            contentAlignment = Alignment.Center
                        ) {}
                    }
                } else {
                    items(recentFiles.size) { index ->
                        val file = recentFiles[index]

                        FilePreview(
                            file = file,
                            context = context,
                            modifier = Modifier
                                .size(200.dp)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp)),
                            onClick = { file ->
                                clickedFile = file
                            }
                        )
                    }
                }
            }
        }

        Text(
            text = "Stats",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .padding(
                    top = 12.dp,
                    bottom = 4.dp,
                    start = 4.dp
                )
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

            Stat(
                title = "Files Uploaded",
                value = userStats?.filesUploaded,
            )

            StatDivider()

            Stat(
                title = "Favorite Files",
                value = userStats?.favoriteFiles,
            )

            StatDivider()

            Stat(
                title = "Storage Used",
                value = formatBytes(userStats?.storageUsed ?: 0L),
                loading = userStats == null
            )

            StatDivider()

            Stat(
                title = "Average Storage Used",
                value = formatBytes(userStats?.averageStorageUsed?.toLong() ?: 0L),
                loading = userStats == null
            )

            StatDivider()

            Stat(
                title = "Files Views",
                value = userStats?.views,
            )

            StatDivider()

            Stat(
                title = "Average Files Views",
                value = String.format(Locale.getDefault(), "%.2f", userStats?.averageViews ?: 0.0),
                loading = userStats == null
            )

            StatDivider()

            Stat(
                title = "Links Created",
                value = userStats?.urlsCreated,
            )

            StatDivider()

            Stat(
                title = "Links Views",
                value = userStats?.urlViews,
            )
        }

        Text(
            text = "File Types",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .padding(
                    top = 12.dp,
                    bottom = 4.dp,
                    start = 4.dp
                )
        )

        Container(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(
                    max = if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT)
                        500.dp
                    else 280.dp
                )
                .background(MaterialTheme.colorScheme.surface),
            scrollable = false
        ) {
            val tableTypeWidth = 250.dp
            val tableCountWidth = 180.dp

            val headers: List<TableHeaderData> = listOf(
                TableHeaderData(
                    content = {
                        Text(
                            text = "File Type",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    name = "file type",
                    width = tableTypeWidth,
                ),
                TableHeaderData(
                    content = {
                        Text(
                            text = "Count",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    name = "count",
                    width = tableCountWidth,
                ),
            )

            val rows: List<TableRowData>? = userStats?.sortTypeCount?.map { (type, count) ->
                TableRowData(
                    cells = listOf(
                        TableCellData(
                            content = {
                                Text(
                                    text = type,
                                )
                            },
                            width = tableTypeWidth
                        ),
                        TableCellData(
                            content = {
                                Text(
                                    text = count.toString(),
                                )
                            },
                            width = tableCountWidth
                        ),
                    )
                )
            }

            Table(
                modifier = Modifier.fillMaxSize(),
                headers = headers,
                rows = rows ?: emptyList(),
                loading = userStats == null
            )
        }
    }
}


