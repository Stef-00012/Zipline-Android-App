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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.stefdp.zipline.utils.STORAGE_SERVER_URL_KEY
import com.stefdp.zipline.utils.SecureStorage
import com.stefdp.zipline.utils.formatBytes
import com.stefdp.zipline.utils.horizontalLazyScrollbar
import com.stefdp.zipline.utils.parseBytes
import com.stefdp.zipline.utils.shimmerable
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: HomeViewModel = viewModel()
) {
    val localLoggedUser = LocalLoggedUser.current

    if (localLoggedUser == null) {
        navController.navigate(LoginScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    val publicSettings = LocalPublicSettings.current
    val updatePublicSettings = LocalUpdatePublicSettings.current

    val webSettings = LocalWebSettings.current
    val updateWebSettings = LocalUpdateWebSettings.current

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initData(context)
    }

    fun updateData() {
        viewModel.updateData(context)
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

        val filesUploaded = state.userStats?.filesUploaded

        Text(
            text = "You have $filesUploaded files uploaded.",
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .shimmerable(
                    enabled = state.userStats == null,
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
                        enabled = state.userStats == null,
                    )
                    .padding(
                        start = 4.dp
                    )
            )
        }

        if (localLoggedUser?.quota?.filesQuota == UserQuotaFilesQuota.BY_BYTES) {
            val storageUsed = formatBytes(state.userStats?.storageUsed ?: 0L)
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
                        enabled = state.userStats == null,
                    )
                    .padding(
                        start = 4.dp
                    )
            )
        }

        if (localLoggedUser?.quota?.maxUrls != null) {
            val urlsCreated = state.userStats?.urlsCreated
            val maxUrlsAllowed = localLoggedUser.quota.maxUrls

            Text(
                text = "You have created $urlsCreated out of ${maxUrlsAllowed} links allowed.",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .shimmerable(
                        enabled = state.userStats == null,
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
            LaunchedEffect(state.recentFiles) {
                if (state.clickedRecentFile != null) {
                    val updatedFile = state.recentFiles?.find { it.id == state.clickedRecentFile?.id }

                    if (updatedFile != null) {
                        viewModel.setClickedRecentFile(updatedFile)
                    }
                }
            }

            LargeFileDisplay(
                context = context,
                activity = activity,
                file = state.clickedRecentFile,
                onDismissRequest = {
                    viewModel.setClickedRecentFile(null)
                },
                updateData = ::updateData,
                onDelete = {
                    viewModel.setClickedRecentFile(null)
                }
            )

            val lazyListState = rememberLazyListState()

            if (state.recentFiles == null || !state.recentFiles.isNullOrEmpty()) {
                LazyRow(
                    state = lazyListState,
                    modifier = Modifier.horizontalLazyScrollbar(
                        listState = lazyListState
                    )
                ) {
                    if (state.recentFiles == null) {
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
                        items(state.recentFiles!!.size) { index ->
                            val file = state.recentFiles!![index]

                            FilePreview(
                                file = file,
                                context = context,
                                modifier = Modifier
                                    .size(200.dp)
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp)),
                                onClick = { file ->
                                    viewModel.setClickedRecentFile(file)
                                },
                                serverUrl = state.serverUrl
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "You have no recent files. The last eight files you uploaded will appear here.",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
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
                value = state.userStats?.filesUploaded,
            )

            StatDivider()

            Stat(
                title = "Favorite Files",
                value = state.userStats?.favoriteFiles,
            )

            StatDivider()

            Stat(
                title = "Storage Used",
                value = formatBytes(state.userStats?.storageUsed ?: 0L),
                loading = state.userStats == null
            )

            StatDivider()

            Stat(
                title = "Average Storage Used",
                value = formatBytes(state.userStats?.averageStorageUsed?.toLong() ?: 0L),
                loading = state.userStats == null
            )

            StatDivider()

            Stat(
                title = "Files Views",
                value = state.userStats?.views,
            )

            StatDivider()

            Stat(
                title = "Average Files Views",
                value = String.format(Locale.getDefault(), "%.2f", state.userStats?.averageViews ?: 0.0),
                loading = state.userStats == null
            )

            StatDivider()

            Stat(
                title = "Links Created",
                value = state.userStats?.urlsCreated,
            )

            StatDivider()

            Stat(
                title = "Links Views",
                value = state.userStats?.urlViews,
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
                    name = "file type",
                    width = tableTypeWidth,
                ) {
                    Text(
                        text = "File Type",
                        fontWeight = FontWeight.Bold
                    )
                },
                TableHeaderData(
                    name = "count",
                    width = tableCountWidth,
                ) {
                    Text(
                        text = "Count",
                        fontWeight = FontWeight.Bold
                    )
                },
            )

            val rows: List<TableRowData>? = state.userStats?.sortTypeCount?.map { (type, count) ->
                TableRowData(
                    cells = listOf(
                        TableCellData(
                            width = tableTypeWidth
                        ) {
                            Text(
                                text = type,
                            )
                        },
                        TableCellData(
                            width = tableCountWidth
                        ) {
                            Text(
                                text = count.toString(),
                            )
                        },
                    )
                )
            }

            Table(
                modifier = Modifier.fillMaxSize(),
                headers = headers,
                rows = rows ?: emptyList(),
                loading = state.userStats == null
            )
        }
    }
}


