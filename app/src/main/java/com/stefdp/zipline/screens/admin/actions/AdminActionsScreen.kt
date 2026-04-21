package com.stefdp.zipline.screens.admin.actions

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.LocalLoggedUser
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.PromptPopup
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.network.models.UserRole
import com.stefdp.zipline.screens.HomeScreen
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.screens.admin.actions.components.ActionContainer
import kotlinx.coroutines.launch

@Composable
fun AdminActionsScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: AdminActionsViewModel = viewModel()
) {
    val localLoggedUser = LocalLoggedUser.current

    if (localLoggedUser == null) {
        navController.navigate(LoginScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    localLoggedUser?.role?.level?.let {
        if (it > UserRole.ADMIN.level) {
            navController.navigate(HomeScreen) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshZeroByteFiles(
            context = context,
            onError = { error ->
                Notification.show(
                    activity = activity,
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
    }

    val coroutineScope = rememberCoroutineScope()

    val directoryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )

            viewModel.setSelectedUri(
                context = context,
                uri = it
            )

            viewModel.performDownload(
                context = context,
                uri = it,
                sendNotification = { content ->
                    Notification.show(
                        activity = activity,
                        content = content
                    )
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.initData(context)
    }

    Popup(
        showPopup = state.popupEnabled == AdminActionType.IMPORT_EXPORT && !state.exportConfirmationPopup,
        onDismissRequest = {
            viewModel.setPopupEnabled(null)
        },
    ) {
        Text(
            text = "Import / Export Data",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
        )

        // TODO: Implement import functionality

//        Row(
//            horizontalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            Button(
//                onClick = {},
//                modifier = Modifier.weight(1f)
//            ) {
//                Text("Import V3")
//            }
//
//            Button(
//                onClick = {},
//                modifier = Modifier.weight(1f)
//            ) {
//                Text("Import V4")
//            }
//        }
//
//        HorizontalDivider(
//            modifier = Modifier.padding(
//                vertical = 8.dp
//            ),
//            color = MaterialTheme.colorScheme.outline
//        )

        Button(
            onClick = {
                viewModel.setExportConfirmationPopup(true)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Export V4")
        }
    }

    PromptPopup(
        showPopup = state.popupEnabled == AdminActionType.IMPORT_EXPORT && state.exportConfirmationPopup,
        onDismissRequest = {
            viewModel.setPopupEnabled(null)
            viewModel.setExportConfirmationPopup(false)
        },
        title = "Are you sure?",
        description = AnnotatedString.fromHtml("""
            The export provides a complete snapshot of Zipline’s data and environment. It includes:
            <br />
            <ul>
                <li><b>Users:</b> Account information including usernames, optional passwords, avatars, roles, view settings, and optional TOTP secrets.</li>
                <li><b>Passkeys:</b> Registered WebAuthn passkeys with creation dates, last-used timestamps, and credential registration data.</li>
                <li><b>User Quotas:</b> Quota settings such as max bytes, max files, max URLs, and quota types.</li>
                <li><b>OAuth Providers:</b> Linked OAuth accounts including provider type, tokens, and OAuth IDs.</li>
                <li><b>User Tags:</b> Tags created by users, including names, colors, and associated file IDs.</li>
                <li><b>Files:</b> Metadata about uploaded files including size, type, timestamps, expiration, views, password protection, owner, and folder association. (Actual file contents are not included.)</li>
                <li><b>Folders:</b> Folder metadata including visibility settings, upload permissions, file lists, and ownership.</li>
                <li><b>URLs:</b> Metadata for shortened URLs including destinations, vanity codes, view counts, passwords, and user assignments.</li>
                <li><b>Thumbnails:</b> Thumbnail path and associated file ID. (Image data is not included.)</li>
                <li><b>Invites:</b> Invite codes, creation/expiration dates, and usage counts.</li>
                <li><b>Metrics:</b> System and usage statistics stored internally by Zipline.</li>
            </ul>
            <br />
            Additionally, the export includes <b>system-specific information</b>:
            <br />
            <ul>
                <li><b>CPU Count:</b> The number of available processor cores.</li>
                <li><b>Hostname:</b> The host system’s network identifier.</li>
                <li><b>Architecture:</b> The hardware architecture (e.g., x64, arm64).</li>
                <li><b>Platform:</b> The operating system platform (e.g., linux, darwin).</li>
                <li><b>OS Release:</b> The OS or kernel version.</li>
                <li><b>Environment Variables:</b> A full snapshot of environment variables at the time of export.</li>
                <li><b>Versions:</b> The Zipline version, Node version, and export format version.</li>
            </ul>
        """.trimIndent()),
        onCancel = {
            viewModel.setExportConfirmationPopup(false)
        },
        onSuccess = {
            coroutineScope.launch {
                if (state.selectedUri == null) {
                    directoryPicker.launch(null)

                    return@launch
                }

                viewModel.performDownload(
                    context = context,
                    uri = state.selectedUri!!,
                    sendNotification = { content ->
                        Notification.show(
                            activity = activity,
                            content = content
                        )
                    }
                )

                viewModel.setPopupEnabled(null)
                viewModel.setExportConfirmationPopup(false)
            }
        },
        successText = "Download Export",
        cancelColor = MaterialTheme.colorScheme.error,
        successColor = MaterialTheme.colorScheme.primary,
        isLoading = state.isLoading
    ) {
        HorizontalDivider(
            modifier = Modifier.padding(
                vertical = 8.dp
            ),
            color = MaterialTheme.colorScheme.outline
        )

        Switch(
            checked = state.excludeMetricsFromExport,
            onCheckedChange = {
                viewModel.setExcludeMetricsFromExport(it)
            },
            label = "Exclude Metrics Data",
            description = "Exclude system and usage metrics from the export. This can reduce the export file size."
        )

        HorizontalDivider(
            modifier = Modifier.padding(
                vertical = 8.dp
            ),
            color = MaterialTheme.colorScheme.outline
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                .background(
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                )
                .padding(8.dp)
        ) {
            Text(
                text = "Warning",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = "This export contains a significant amount of sensitive data, including user accounts, authentication credentials, environment variables, and system metadata. Handle this file securely and do not share it with untrusted parties."
            )
        }
    }

    PromptPopup(
        showPopup = state.popupEnabled == AdminActionType.CLEAR_TEMP_FILES,
        title = "Are you sure?",
        description = "This will delete temporary files stored within the temporary directory (defined in the configuration). This should not cause harm unless there are files that are being processed still.",
        onDismissRequest = {
            viewModel.setPopupEnabled(null)
        },
        onCancel = {
            viewModel.setPopupEnabled(null)
        },
        successText = "Yes, Delete",
        onSuccess = {
            viewModel.deleteTemporaryFiles(
                context = context,
                onSuccess = { response ->
                    Notification.show(
                        activity = activity,
                    ) {
                        Text(response)
                    }
                },
                onError = { error ->
                    Notification.show(
                        activity = activity,
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        isLoading = state.isLoading
    )

    PromptPopup(
        showPopup = state.popupEnabled == AdminActionType.CLEAR_ZERO_BYTE_FILES,
        title = "Are you sure?",
        description = "This will delete ${state.zeroByteFileCount} files from the database and datasource.",
        onDismissRequest = {
            viewModel.setPopupEnabled(null)
        },
        onCancel = {
            viewModel.setPopupEnabled(null)
        },
        successText = "Yes, Delete",
        onSuccess = {
            viewModel.deleteZeroBytesFiles(
                context = context,
                onSuccess = { response ->
                    Notification.show(
                        activity = activity,
                    ) {
                        Text(response)
                    }
                },
                onError = { error ->
                    Notification.show(
                        activity = activity,
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        isLoading = state.isLoading
    )

    PromptPopup(
        showPopup = state.popupEnabled == AdminActionType.REQUERY_FILE_SIZES,
        title = "Are you sure?",
        description = "This will requery the size of every file stored within the database. Additionally you can use the options below.",
        onDismissRequest = {
            viewModel.setPopupEnabled(null)
        },
        onCancel = {
            viewModel.setPopupEnabled(null)
        },
        successText = "Requery",
        onSuccess = {
            viewModel.requeryFileSize(
                context = context,
                onSuccess = { response ->
                    Notification.show(
                        activity = activity,
                        duration = 8000L,
                    ) {
                        Text(response)
                    }
                },
                onError = { error ->
                    Notification.show(
                        activity = activity,
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        isLoading = state.isLoading
    ) {
        Switch(
            checked = state.requeryFileSizeForceUpdate,
            onCheckedChange = {
                viewModel.setRequeryFileSizeForceUpdate(it)
            },
            label = "Force Update",
            description = "Force update the size of every file, even if it already has a size set."
        )

        Switch(
            checked = state.requeryFileSizeForceDelete,
            onCheckedChange = {
                viewModel.setRequeryFileSizeForceDelete(it)
            },
            label = "Force Delete",
            description = "Delete files that are not found in the database, or have a size of 0."
        )
    }

    PromptPopup(
        showPopup = state.popupEnabled == AdminActionType.GENERATE_THUMBNAILS,
        title = "Are you sure?",
        description = "This will generate thumbnails for all files that do not have a thumbnail set. Additionally you can use the options below.",
        onDismissRequest = {
            viewModel.setPopupEnabled(null)
        },
        onCancel = {
            viewModel.setPopupEnabled(null)
        },
        successText = "Generate",
        onSuccess = {
            viewModel.generateThumbnails(
                context = context,
                onSuccess = { response ->
                    Notification.show(
                        activity = activity,
                        duration = 8000L,
                    ) {
                        Text(response)
                    }
                },
                onError = { error ->
                    Notification.show(
                        activity = activity,
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        isLoading = state.isLoading
    ) {
        Switch(
            checked = state.rerunThumbnailsGeneration,
            onCheckedChange = {
                viewModel.setRerunThumbnailsGeneration(it)
            },
            label = "Re-run",
            description = "Re-run the thumbnail generation for all files regardless of whether they have a thumbnail set."
        )
    }

    Column(
        modifier = Modifier.padding(
            start = 12.dp,
            end = 12.dp,
            top = 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column {
            Text(
                text = "Server Actions",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
            )

            Text(
                text = "Useful tools and scripts for server management.",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                ),
            )
        }

        ActionContainer(
            title = "Import/Export Data",
            description = "Allows you to import or export server data and configurations.",
            icon = painterResource(R.drawable.database_upload),
            iconContentDescription = "Import/Export Data",
            onClick = {
                viewModel.setPopupEnabled(AdminActionType.IMPORT_EXPORT)
            },
            enabled = !state.isLoading
        )

        ActionContainer(
            title = "Clear Temporary Files",
            description = "Removes all temporary files from the temporary directory.",
            icon = painterResource(R.drawable.delete),
            iconContentDescription = "Clear Temporary Files",
            onClick = {
                viewModel.setPopupEnabled(AdminActionType.CLEAR_TEMP_FILES)
            },
            enabled = !state.isLoading
        )

        ActionContainer(
            title = "Clear Zero Byte Files",
            description = "Deletes all files with zero bytes from the database and/or storage.",
            icon = painterResource(R.drawable.delete),
            iconContentDescription = "Clear Zero Byte Files",
            onClick = {
                viewModel.setPopupEnabled(AdminActionType.CLEAR_ZERO_BYTE_FILES)
            },
            enabled = !state.isLoading
        )

        ActionContainer(
            title = "Requery File Sizes",
            description = "Recalculates and updates the sizes of all files in the database.",
            icon = painterResource(R.drawable.play_arrow),
            iconContentDescription = "Requery File Sizes",
            onClick = {
                viewModel.setPopupEnabled(AdminActionType.REQUERY_FILE_SIZES)
            },
            enabled = !state.isLoading
        )

        ActionContainer(
            title = "Generate Thumbnails",
            description = "Creates thumbnails for all image and video files that lack them.",
            icon = painterResource(R.drawable.flare),
            iconContentDescription = "Generate Thumbnails",
            onClick = {
                viewModel.setPopupEnabled(AdminActionType.GENERATE_THUMBNAILS)
            },
            enabled = !state.isLoading
        )
    }
}