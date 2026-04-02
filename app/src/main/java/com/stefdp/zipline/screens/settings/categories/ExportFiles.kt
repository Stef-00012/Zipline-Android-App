package com.stefdp.zipline.screens.settings.categories

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.components.table.Table
import com.stefdp.zipline.components.table.TableCellData
import com.stefdp.zipline.components.table.TableHeaderData
import com.stefdp.zipline.components.table.TableRowData
import com.stefdp.zipline.network.models.Export
import com.stefdp.zipline.network.requests.deleteExport
import com.stefdp.zipline.network.requests.downloadExport
import com.stefdp.zipline.network.requests.startExport
import com.stefdp.zipline.screens.files.components.IconButton
import com.stefdp.zipline.ui.theme.DarkGreen
import com.stefdp.zipline.utils.SecureStorage
import com.stefdp.zipline.utils.StorageUtil
import com.stefdp.zipline.utils.formatBytes
import com.stefdp.zipline.utils.getDisplayPath
import com.stefdp.zipline.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nl.jacobras.humanreadable.HumanReadable
import kotlin.time.Instant

@Composable
internal fun ExportFilesCategory(
    context: Context,
    exports: List<Export>,
    isLoading: Boolean,
    setLoading: (Boolean) -> Unit,
    title: String,
    updateExports: suspend () -> Unit,
    settingsUpdateTick: Int
) {
    Container(
        scrollable = false,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
            )

            val coroutineScope = rememberCoroutineScope()

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    coroutineScope.launch {
                        setLoading(true)

                        startExport(context)
                        updateExports()

                        setLoading(false)
                    }
                },
                enabled = !isLoading
            ) {
                Icon(
                    painter = painterResource(R.drawable.add),
                    contentDescription = "New export"
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "New Export"
                )
            }

            val tableIdWidth = 300.dp
            val tableStartedWidth = 170.dp
            val tableFilesWidth = 100.dp
            val tableSizeWidth = 150.dp
            val tableActionsWidth = 100.dp

            val tableHeaders = listOf(
                TableHeaderData(
                    content = {
                        Text(
                            text = "ID",
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    name = "id",
                    width = tableIdWidth
                ),
                TableHeaderData(
                    content = {
                        Text(
                            text = "Started",
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    name = "started",
                    width = tableStartedWidth
                ),
                TableHeaderData(
                    content = {
                        Text(
                            text = "Files",
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    name = "files",
                    width = tableFilesWidth
                ),
                TableHeaderData(
                    content = {
                        Text(
                            text = "Size",
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    name = "size",
                    width = tableSizeWidth
                ),
                TableHeaderData(
                    content = {
                        Text(
                            text = "Actions",
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    name = "actions",
                    width = tableActionsWidth
                )
            )

            val tableRows = exports.map { export ->
                TableRowData(
                    cells = listOf(
                        TableCellData(
                            content = {
                                Text(
                                    text = export.id,
                                    color = if (export.completed)
                                        DarkGreen
                                    else
                                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            },
                            width = tableIdWidth
                        ),
                        TableCellData(
                            content = {
                                Text(
                                    text = HumanReadable.timeAgo(Instant.parse(export.createdAt)),
                                )
                            },
                            width = tableStartedWidth
                        ),
                        TableCellData(
                            content = {
                                Text(
                                    text = export.files.toString(),
                                )
                            },
                            width = tableFilesWidth
                        ),
                        TableCellData(
                            content = {
                                Text(
                                    text = formatBytes(export.size.toLong()),
                                )
                            },
                            width = tableSizeWidth,
                        ),
                        TableCellData(
                            content = {
                                @Composable
                                fun ActionButtonSpacer() {
                                    Spacer(modifier = Modifier.width(8.dp))
                                }

                                IconButton(
                                    icon = painterResource(R.drawable.delete),
                                    iconContentDescription = "Delete",
                                    color = MaterialTheme.colorScheme.error,
                                    iconColor = MaterialTheme.colorScheme.onError,
                                    onClick = {
                                        coroutineScope.launch {
                                            setLoading(true)

                                            val deleteExportRes = deleteExport(
                                                context = context,
                                                exportId = export.id
                                            )

                                            deleteExportRes.onSuccess {
                                                if (it.deleted) {
                                                    updateExports()
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Failed to delete export",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }

                                            setLoading(false)
                                        }
                                    },
                                    enabled = !isLoading
                                )

                                ActionButtonSpacer()

                                var selectedUri by remember { mutableStateOf<Uri?>(null) }
                                var selectedPath by remember { mutableStateOf<String?>(null) }

                                LaunchedEffect(Unit) {
                                    val secureStore = SecureStorage.getInstance(context)

                                    val exportDownloadFolder = secureStore.get("exportDownloadFolder")

                                    if (exportDownloadFolder != null) {
                                        selectedUri = exportDownloadFolder.toUri()
                                        selectedPath = getDisplayPath(exportDownloadFolder.toUri())
                                    }
                                }

                                fun showToast(message: String) {
                                    coroutineScope.launch(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            message,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }

                                fun performDownload() {
                                    val exportFits = StorageUtil.canFitFile(
                                        context = context,
                                        uri = selectedUri!!,
                                        fileSize = export.size.toLong()
                                    )

                                    if (!exportFits) {
                                        showToast("Not enough space in the selected directory to download the export")

                                        return
                                    }

                                    val exportFitsCache = StorageUtil.canFitInternalCache(
                                        context = context,
                                        fileSize = export.size.toLong()
                                    )

                                    if (!exportFitsCache) {
                                        showToast("Not enough space in the internal cache to download the export")

                                        return
                                    }

                                    Log.d("ExportFiles", "Starting download of export ${export.id} with size ${export.size} bytes to $selectedPath")

                                    coroutineScope.launch(Dispatchers.IO) {
                                        showToast("Starting download...")

                                        val fileName = "export_${export.id}_${System.currentTimeMillis()}.zip"

                                        val tempFile = java.io.File(context.cacheDir, fileName)
                                        val tempDestinationPath = tempFile.absolutePath

                                        if (tempFile.exists()) tempFile.delete()

                                        val downloadRes = downloadExport(
                                            context = context,
                                            exportId = export.id,
                                            destinationPath = tempDestinationPath,
                                            notificationTitle = "Downloading export",
                                            notificationContent = "Downloading export ${export.id}",
                                        )

                                        downloadRes
                                            .onSuccess {
                                                try {
                                                    val docUri =
                                                        DocumentsContract.buildDocumentUriUsingTree(
                                                            selectedUri,
                                                            DocumentsContract.getTreeDocumentId(
                                                                selectedUri
                                                            )
                                                        )

                                                    val fileUri = DocumentsContract.createDocument(
                                                        context.contentResolver,
                                                        docUri,
                                                        "application/zip",
                                                        fileName
                                                    )

                                                    if (fileUri != null) {
                                                        context.contentResolver.openOutputStream(fileUri)
                                                            ?.use { out ->
                                                                tempFile.inputStream().use { inp ->
                                                                    inp.copyTo(out)
                                                                }
                                                            }

                                                        showToast("Export downloaded to ${selectedPath}/$fileName")
                                                    } else {
                                                        showToast("Failed to create file in selected directory")
                                                    }
                                                } catch (e: Exception) {
                                                    Log.e(
                                                        "ExportFiles",
                                                        "Failed to copy file to selected directory",
                                                        e
                                                    )

                                                    showToast("Failed to copy file to selected directory: ${e.message}")
                                                } finally {
                                                    tempFile.delete()
                                                }
                                            }
                                            .onFailure {
                                                Log.e("ExportFiles", "Failed to download export", it)

                                                showToast("Failed to download export: ${it.message}")
                                            }
                                    }
                                }

                                val directoryPicker = rememberLauncherForActivityResult(
                                    contract = ActivityResultContracts.OpenDocumentTree()
                                ) { uri: Uri? ->
                                    uri?.let {
                                        val secureStore = SecureStorage.getInstance(context)

                                        context.contentResolver.takePersistableUriPermission(
                                            it,
                                            Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                        )
                                        selectedUri = it
                                        selectedPath = getDisplayPath(it)

                                        coroutineScope.launch {
                                            secureStore.set("exportDownloadFolder", selectedUri.toString())
                                        }

                                        performDownload()

                                        setLoading(false)
                                    }
                                }

                                IconButton(
                                    icon = painterResource(R.drawable.download),
                                    iconContentDescription = "Download export",
                                    color = MaterialTheme.colorScheme.primary,
                                    iconColor = MaterialTheme.colorScheme.onPrimary,
                                    onClick = {
                                        setLoading(true)

                                        if (selectedUri == null) {
                                            directoryPicker.launch(null)

                                            return@IconButton
                                        }

                                        performDownload()

                                        setLoading(false)
                                    },
                                    enabled = !isLoading && export.completed
                                )
                            },
                            width = tableActionsWidth
                        ),
                    )
                )
            }

            Table(
                headers = tableHeaders,
                rows = tableRows,
                loading = isLoading
            )
        }
    }
}