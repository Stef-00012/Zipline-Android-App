package com.stefdp.zipline.screens.admin.actions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
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
import com.stefdp.zipline.network.requests.deleteZeroByteFiles
import com.stefdp.zipline.network.requests.exportData
import com.stefdp.zipline.network.requests.runDeleteTemporaryFilesJob
import com.stefdp.zipline.network.requests.runRequerySizeJob
import com.stefdp.zipline.network.requests.runThumbnailGenerationJob
import com.stefdp.zipline.network.requests.scanForZeroByteFiles
import com.stefdp.zipline.screens.HomeScreen
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.screens.admin.actions.components.ActionContainer
import com.stefdp.zipline.utils.SecureStorage
import com.stefdp.zipline.utils.getDisplayPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AdminActionsScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
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

    var isLoading by remember { mutableStateOf(false) }

    var popupEnabled by remember { mutableStateOf<AdminActionType?>(null) }
    var exportConfirmationPopup by remember { mutableStateOf(false) }

    var zeroByteFileCount by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        val zeroByteFilesRes = scanForZeroByteFiles(
            context = context
        )

        zeroByteFilesRes
            .onSuccess {
                zeroByteFileCount = it.files?.size
            }
             .onFailure {
                 Toast.makeText(
                     context,
                     "Failed to scan for zero byte files: ${it.message}",
                     Toast.LENGTH_LONG
                 ).show()
            }
    }

    val coroutineScope = rememberCoroutineScope()

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPath by remember { mutableStateOf<String?>(null) }

    var excludeMetricsFromExport by remember { mutableStateOf(false) }

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
        coroutineScope.launch(Dispatchers.IO) {
            showToast("Starting download...")

            val fileName = "zipline_export_${System.currentTimeMillis()}.json"

            val tempFile = java.io.File(context.cacheDir, fileName)
            val tempDestinationPath = tempFile.absolutePath

            if (tempFile.exists()) tempFile.delete()

            val downloadRes = exportData(
                context = context,
                destinationPath = tempDestinationPath,
                excludeMetrics = excludeMetricsFromExport.takeIf { it }
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
                            "application/json",
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
                            "AdminActionsScreen[ExportData]",
                            "Failed to copy export to selected directory",
                            e
                        )

                        showToast("Failed to copy export to selected directory: ${e.message}")
                    } finally {
                        tempFile.delete()
                    }
                }
                .onFailure {
                    Log.e("AdminActionsScreen[ExportData]", "Failed to download export", it)

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
        }
    }

    LaunchedEffect(Unit) {
        val secureStore = SecureStorage.getInstance(context)

        val exportDownloadFolder = secureStore.get("exportDownloadFolder")

        if (exportDownloadFolder != null) {
            selectedUri = exportDownloadFolder.toUri()
            selectedPath = getDisplayPath(exportDownloadFolder.toUri())
        }
    }

    Popup(
        showPopup = popupEnabled == AdminActionType.IMPORT_EXPORT && !exportConfirmationPopup,
        onDismissRequest = { popupEnabled = null },
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
                exportConfirmationPopup = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Export V4")
        }
    }

    PromptPopup(
        showPopup = popupEnabled == AdminActionType.IMPORT_EXPORT && exportConfirmationPopup,
        onDismissRequest = {
            popupEnabled = null
            exportConfirmationPopup = false
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
        onCancel = { exportConfirmationPopup = false },
        onSuccess = {
            coroutineScope.launch {
                isLoading = true

                if (selectedUri == null) {
                    directoryPicker.launch(null)

                    return@launch
                }

                performDownload()

                isLoading = false
                popupEnabled = null
                exportConfirmationPopup = false
            }
        },
        successText = "Download Export",
        cancelColor = MaterialTheme.colorScheme.error,
        successColor = MaterialTheme.colorScheme.primary,
        isLoading = isLoading
    ) {
        HorizontalDivider(
            modifier = Modifier.padding(
                vertical = 8.dp
            ),
            color = MaterialTheme.colorScheme.outline
        )

        Switch(
            checked = excludeMetricsFromExport,
            onCheckedChange = { excludeMetricsFromExport = it },
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
        showPopup = popupEnabled == AdminActionType.CLEAR_TEMP_FILES,
        title = "Are you sure?",
        description = "This will delete temporary files stored within the temporary directory (defined in the configuration). This should not cause harm unless there are files that are being processed still.",
        onDismissRequest = { popupEnabled = null },
        onCancel = { popupEnabled = null },
        successText = "Yes, Delete",
        onSuccess = {
            coroutineScope.launch {
                isLoading = true

                val deleteTempFilesRes = runDeleteTemporaryFilesJob(
                    context = context
                )

                deleteTempFilesRes
                    .onSuccess {
                        Toast.makeText(
                            context,
                            it,
                            Toast.LENGTH_LONG
                        ).show()

                        popupEnabled = null
                    }
                    .onFailure {
                        Toast.makeText(
                            context,
                            "Failed to delete temporary files: ${it.message}",
                            Toast.LENGTH_LONG
                        ).show()

                        popupEnabled = null
                    }

                isLoading = false
            }
        },
        isLoading = isLoading
    )

    PromptPopup(
        showPopup = popupEnabled == AdminActionType.CLEAR_ZERO_BYTE_FILES,
        title = "Are you sure?",
        description = "This will delete ${zeroByteFileCount ?: 0} files from the database and datasource.",
        onDismissRequest = { popupEnabled = null },
        onCancel = { popupEnabled = null },
        successText = "Yes, Delete",
        onSuccess = {
            coroutineScope.launch {
                isLoading = true

                val deleteZeroByteFilesRes = deleteZeroByteFiles(
                    context = context
                )

                deleteZeroByteFilesRes
                    .onSuccess {
                        Toast.makeText(
                            context,
                            "Cleared ${it.files?.size ?: 0} files with a size of 0B.",
                            Toast.LENGTH_LONG
                        ).show()

                        popupEnabled = null
                    }
                    .onFailure {
                        Toast.makeText(
                            context,
                            "Failed to delete zero byte files: ${it.message}",
                            Toast.LENGTH_LONG
                        ).show()

                        popupEnabled = null
                    }

                isLoading = false
            }
        },
        isLoading = isLoading
    )

    var requeryFileSizeForceUpdate by remember { mutableStateOf(false) }
    var requeryFileSizeForceDelete by remember { mutableStateOf(false) }

    PromptPopup(
        showPopup = popupEnabled == AdminActionType.REQUERY_FILE_SIZES,
        title = "Are you sure?",
        description = "This will requery the size of every file stored within the database. Additionally you can use the options below.",
        onDismissRequest = { popupEnabled = null },
        onCancel = { popupEnabled = null },
        successText = "Requery",
        onSuccess = {
            coroutineScope.launch {
                isLoading = true

                val requeryFileSizesRes = runRequerySizeJob(
                    context = context,
                    forceUpdate = requeryFileSizeForceUpdate,
                    forceDelete = requeryFileSizeForceDelete
                )

                requeryFileSizesRes
                    .onSuccess {
                        Notification.show(
                            context = context,
                            activity = activity,
                            message = it,
                            duration = 8000L
                        )

                        popupEnabled = null
                    }
                    .onFailure {
                        Toast.makeText(
                            context,
                            "Failed to delete requery file sizes: ${it.message}",
                            Toast.LENGTH_LONG
                        ).show()

                        popupEnabled = null
                    }

                isLoading = false
            }
        },
        isLoading = isLoading
    ) {
        Switch(
            checked = requeryFileSizeForceUpdate,
            onCheckedChange = { requeryFileSizeForceUpdate = it },
            label = "Force Update",
            description = "Force update the size of every file, even if it already has a size set."
        )

        Switch(
            checked = requeryFileSizeForceDelete,
            onCheckedChange = { requeryFileSizeForceDelete = it },
            label = "Force Delete",
            description = "Delete files that are not found in the database, or have a size of 0."
        )
    }

    var rerunThumbnailsGeneration by remember { mutableStateOf(false) }

    PromptPopup(
        showPopup = popupEnabled == AdminActionType.GENERATE_THUMBNAILS,
        title = "Are you sure?",
        description = "This will generate thumbnails for all files that do not have a thumbnail set. Additionally you can use the options below.",
        onDismissRequest = { popupEnabled = null },
        onCancel = { popupEnabled = null },
        successText = "Generate",
        onSuccess = {
            coroutineScope.launch {
                isLoading = true

                val generateThumbnailsRes = runThumbnailGenerationJob(
                    context = context,
                    rerun = rerunThumbnailsGeneration
                )

                generateThumbnailsRes
                    .onSuccess {
                        Notification.show(
                            context = context,
                            activity = activity,
                            message = it,
                            duration = 8000L
                        )

                        popupEnabled = null
                    }
                    .onFailure {
                        Toast.makeText(
                            context,
                            "Failed to delete requery file sizes: ${it.message}",
                            Toast.LENGTH_LONG
                        ).show()

                        popupEnabled = null
                    }

                isLoading = false
            }
        },
        isLoading = isLoading
    ) {
        Switch(
            checked = rerunThumbnailsGeneration,
            onCheckedChange = { rerunThumbnailsGeneration = it },
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
            onClick = { popupEnabled = AdminActionType.IMPORT_EXPORT },
            enabled = !isLoading
        )

        ActionContainer(
            title = "Clear Temporary Files",
            description = "Removes all temporary files from the temporary directory.",
            icon = painterResource(R.drawable.delete),
            iconContentDescription = "Clear Temporary Files",
            onClick = { popupEnabled = AdminActionType.CLEAR_TEMP_FILES },
            enabled = !isLoading
        )

        ActionContainer(
            title = "Clear Zero Byte Files",
            description = "Deletes all files with zero bytes from the database and/or storage.",
            icon = painterResource(R.drawable.delete),
            iconContentDescription = "Clear Zero Byte Files",
            onClick = { popupEnabled = AdminActionType.CLEAR_ZERO_BYTE_FILES },
            enabled = !isLoading
        )

        ActionContainer(
            title = "Requery File Sizes",
            description = "Recalculates and updates the sizes of all files in the database.",
            icon = painterResource(R.drawable.play_arrow),
            iconContentDescription = "Requery File Sizes",
            onClick = { popupEnabled = AdminActionType.REQUERY_FILE_SIZES },
            enabled = !isLoading
        )

        ActionContainer(
            title = "Generate Thumbnails",
            description = "Creates thumbnails for all image and video files that lack them.",
            icon = painterResource(R.drawable.flare),
            iconContentDescription = "Generate Thumbnails",
            onClick = { popupEnabled = AdminActionType.GENERATE_THUMBNAILS },
            enabled = !isLoading
        )
    }
}

private enum class AdminActionType {
    IMPORT_EXPORT,
    CLEAR_TEMP_FILES,
    CLEAR_ZERO_BYTE_FILES,
    REQUERY_FILE_SIZES,
    GENERATE_THUMBNAILS
}