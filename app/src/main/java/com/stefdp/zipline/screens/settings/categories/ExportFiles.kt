package com.stefdp.zipline.screens.settings.categories

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.table.Table
import com.stefdp.zipline.components.table.TableCellData
import com.stefdp.zipline.components.table.TableHeaderData
import com.stefdp.zipline.components.table.TableRowData
import com.stefdp.zipline.components.IconButton
import com.stefdp.zipline.screens.settings.SettingsUiState
import com.stefdp.zipline.screens.settings.SettingsViewModel
import com.stefdp.zipline.ui.theme.DarkGreen
import com.stefdp.zipline.utils.formatBytes
import nl.jacobras.humanreadable.HumanReadable
import kotlin.time.Instant

@Composable
internal fun ExportFilesCategory(
    context: Context,
    activity: FragmentActivity,
    title: String,
    viewModel: SettingsViewModel,
    state: SettingsUiState
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

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.startExport(
                        context = context,
                        onError = { error ->
                            Notification.show(
                                activity = activity,
                            ) {
                                Text(
                                    text = "Failed to start export: $error",
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        },
                        onSuccess = {
                            Notification.show(
                                activity = activity,
                            ) {
                                Text(
                                    text = "Export started successfully",
                                )
                            }
                        }
                    )
                },
                enabled = !state.isLoading
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
                    name = "id",
                    width = tableIdWidth
                ) {
                    Text(
                        text = "ID",
                        fontWeight = FontWeight.Bold,
                    )
                },
                TableHeaderData(
                    name = "started",
                    width = tableStartedWidth
                ) {
                    Text(
                        text = "Started",
                        fontWeight = FontWeight.Bold,
                    )
                },
                TableHeaderData(
                    name = "files",
                    width = tableFilesWidth
                ) {
                    Text(
                        text = "Files",
                        fontWeight = FontWeight.Bold,
                    )
                },
                TableHeaderData(
                    name = "size",
                    width = tableSizeWidth
                ) {
                    Text(
                        text = "Size",
                        fontWeight = FontWeight.Bold,
                    )
                },
                TableHeaderData(
                    name = "actions",
                    width = tableActionsWidth
                ) {
                    Text(
                        text = "Actions",
                        fontWeight = FontWeight.Bold,
                    )
                }
            )

            val tableRows = state.exports?.map { export ->
                TableRowData(
                    cells = listOf(
                        TableCellData(
                            width = tableIdWidth
                        ) {
                            Text(
                                text = export.id,
                                color = if (export.completed)
                                    DarkGreen
                                else
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        },
                        TableCellData(
                            width = tableStartedWidth
                        ) {
                            Text(
                                text = HumanReadable.timeAgo(Instant.parse(export.createdAt)),
                            )
                        },
                        TableCellData(
                            width = tableFilesWidth
                        ) {
                            Text(
                                text = export.files.toString(),
                            )
                        },
                        TableCellData(
                            width = tableSizeWidth,
                        ) {
                            Text(
                                text = formatBytes(export.size.toLong()),
                            )
                        },
                        TableCellData(
                            width = tableActionsWidth
                        ) {
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
                                    viewModel.deleteExport(
                                        context = context,
                                        exportId = export.id,
                                        onError = { error ->
                                            Notification.show(
                                                activity = activity,
                                            ) {
                                                Text(
                                                    text = "Failed to delete export: $error",
                                                    color = MaterialTheme.colorScheme.error,
                                                )
                                            }
                                        },
                                        onSuccess = {
                                            Notification.show(
                                                activity = activity,
                                            ) {
                                                Text(
                                                    text = "Export deleted successfully",
                                                )
                                            }
                                        }
                                    )
                                },
                                enabled = !state.isLoading
                            )

                            ActionButtonSpacer()

                            val directoryPicker = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.OpenDocumentTree()
                            ) { uri: Uri? ->
                                uri?.let {
                                    context.contentResolver.takePersistableUriPermission(
                                        it,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                    )

                                    viewModel.setSelectedExportUri(
                                        context = context,
                                        exportUri = it
                                    )

                                    viewModel.performExportDownload(
                                        context = context,
                                        export = export,
                                        exportUri = it,
                                        sendNotification = { content ->
                                            Notification.show(
                                                activity = activity,
                                                content = content
                                            )
                                        }
                                    )
                                }
                            }

                            IconButton(
                                icon = painterResource(R.drawable.download),
                                iconContentDescription = "Download export",
                                color = MaterialTheme.colorScheme.primary,
                                iconColor = MaterialTheme.colorScheme.onPrimary,
                                onClick = {
                                    if (state.selectedExportUri == null) {
                                        directoryPicker.launch(null)

                                        return@IconButton
                                    }

                                    viewModel.performExportDownload(
                                        context = context,
                                        export = export,
                                        exportUri = state.selectedExportUri,
                                        sendNotification = { content ->
                                            Notification.show(
                                                activity = activity,
                                                content = content,
                                                duration = 5000L
                                            )
                                        }
                                    )
                                },
                                enabled = !state.isLoading && export.completed
                            )
                        },
                    )
                )
            } ?: emptyList()

            Table(
                headers = tableHeaders,
                rows = tableRows,
                loading = state.exports == null
            )
        }
    }
}