package com.stefdp.zipline.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.R
import com.stefdp.zipline.network.models.BaseFolder
import com.stefdp.zipline.network.models.File
import com.stefdp.zipline.network.models.Tag
import com.stefdp.zipline.network.requests.addFileToFolder
import com.stefdp.zipline.network.requests.deleteFile
import com.stefdp.zipline.network.requests.downloadFile
import com.stefdp.zipline.network.requests.getFolders
import com.stefdp.zipline.network.requests.getTags
import com.stefdp.zipline.network.requests.removeFileFromFolder
import com.stefdp.zipline.network.requests.updateFile
import com.stefdp.zipline.ui.theme.DarkGray
import com.stefdp.zipline.ui.theme.Orange
import com.stefdp.zipline.ui.theme.White
import com.stefdp.zipline.ui.theme.Yellow
import com.stefdp.zipline.utils.SecureStorage
import com.stefdp.zipline.utils.formatBytes
import com.stefdp.zipline.utils.formatDate
import com.stefdp.zipline.utils.getDisplayPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val UNKNOWN_FOLDER_ID = "none"
const val UNKNOWN_FOLDER_NAME = "No Folder"

@Composable
fun LargeFileDisplay(
    context: Context,
    file: File?,
    onDismissRequest: () -> Unit,
    updateData: suspend () -> Unit,
) {
    var activeFile by remember { mutableStateOf<File?>(null) }
    var isPopupVisible by remember { mutableStateOf(false) }

    var allTags by remember { mutableStateOf<List<Tag>>(emptyList()) }
    var allFolders by remember { mutableStateOf<List<BaseFolder>>(emptyList()) }

    var isLoading by remember { mutableStateOf(true) }

    var serverUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val secureStore = SecureStorage.getInstance(context)

        serverUrl = secureStore.get("serverUrl")

        val tagsRes = getTags(
            context = context
        )

        val foldersRes = getFolders(
            context = context
        )

        tagsRes.onSuccess { tags ->
            allTags = tags
        }

        foldersRes.onSuccess { folders ->
            allFolders = listOf(
                BaseFolder(
                    id = UNKNOWN_FOLDER_ID,
                    name = UNKNOWN_FOLDER_NAME,
                    createdAt = "",
                    updatedAt = "",
                    public = true,
                    allowUploads = false,
                    userId = ""
                ),
            ) + folders
        }

        isLoading = false
    }

    LaunchedEffect(file) {
        if (file != null) {
            activeFile = file
            isPopupVisible = true
        }
    }

    if (isPopupVisible) {
        Popup(
            showPopup = true,
            onDismissRequest = onDismissRequest,
        ) {
            AnimatedVisibility(
                visible = file != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                DisposableEffect(Unit) {
                    onDispose {
                        isPopupVisible = false
                    }
                }

                activeFile?.let { currentFile ->
                    var isPreviewLoading by remember { mutableStateOf(true) }

                    val baseModifier = Modifier
                        .fillMaxWidth()

                    val heightModifier = if (isPreviewLoading) {
                        baseModifier.height(200.dp)
                    } else {
                        baseModifier.heightIn(min = 200.dp, max = 700.dp)
                    }

                    Column {
                        Text(
                            text = currentFile.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(
                                start = 4.dp,
                                top = 10.dp,
                                bottom = 5.dp
                            )
                        )

                        FilePreview(
                            file = currentFile,
                            context = context,
                            modifier = heightModifier,
                            clickEnabled = false,
                            previewVideos = true,
                            onImageLoaded = {
                                isPreviewLoading = false
                            }
                        )

                        Stat(
                            label = "Type",
                            value = currentFile.type,
                            icon = painterResource(id = R.drawable.description),
                            iconContentDescription = "File type"
                        )

                        Stat(
                            label = "Size",
                            value = formatBytes(currentFile.size),
                            icon = painterResource(id = R.drawable.sd_card),
                            iconContentDescription = "File size"
                        )

                        Stat(
                            label = "Views",
                            value = currentFile.views.toString(),
                            icon = painterResource(id = R.drawable.visibility),
                            iconContentDescription = "File views${if (currentFile.maxViews != null) " / ${currentFile.maxViews}" else ""}"
                        )

                        Stat(
                            label = "Created At",
                            value = formatDate(currentFile.createdAt),
                            icon = painterResource(id = R.drawable.upload),
                            iconContentDescription = "File creation date"
                        )

                        Stat(
                            label = "Updated At",
                            value = formatDate(currentFile.updatedAt),
                            icon = painterResource(id = R.drawable.autorenew),
                            iconContentDescription = "File updated date"
                        )

                        if (currentFile.deletesAt != null) {
                            Stat(
                                label = "Deletes At",
                                value = formatDate(currentFile.deletesAt),
                                icon = painterResource(id = R.drawable.auto_delete),
                                iconContentDescription = "File deletion date"
                            )
                        }

                        if (currentFile.originalName != null) {
                            Stat(
                                label = "Original Name",
                                value = currentFile.originalName,
                                icon = painterResource(id = R.drawable.title),
                                iconContentDescription = "File original name"
                            )
                        }

                        Spacer()

                        val fileTags by remember(currentFile.tags) { mutableStateOf(currentFile.tags ?: emptyList()) }
                        var selectedTagIds by remember(currentFile.tags) {
                            mutableStateOf(
                                fileTags.map { tag -> tag.id }.toSet()
                            )
                        }

                        val coroutineScope = rememberCoroutineScope()

                        Select(
                            label = "Tags",
                            multiple = true,
                            enabled = !isLoading,
                            options = allTags.map { tag ->
                                SelectOption(
                                    id = tag.id,
                                    label = { enabled ->
                                        Tag(
                                            tag = tag,
                                            enabled = enabled
                                        )
                                    },
                                )
                            },
                            selectedIds = selectedTagIds,
                            onSelectionChange = { newSelectedIds ->
                                if (newSelectedIds == selectedTagIds) return@Select

                                coroutineScope.launch {
                                    isLoading = true

                                    val updateFileRes = updateFile(
                                        context = context,
                                        fileId = currentFile.id,
                                        tags = newSelectedIds.toList()
                                    )

                                    updateFileRes
                                        .onSuccess {
                                            updateData()
                                        }

                                    isLoading = false
                                }
                            }
                        )

                        Spacer()

                        val fileFolderId by remember(currentFile.folderId) { mutableStateOf(currentFile.folderId ?: UNKNOWN_FOLDER_ID) }
                        var selectedFolderId by remember(currentFile.folderId) {
                            mutableStateOf(
                                setOf(fileFolderId)
                            )
                        }

                        Select(
                            label = "Folder",
                            enabled = !isLoading,
                            options = allFolders.map { folder ->
                                SelectOption(
                                    id = folder.id,
                                    label = { enabled ->
                                        Text(
                                            text = folder.name,
                                            color = if (enabled)
                                                MaterialTheme.colorScheme.onSurface
                                            else
                                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                    },
                                    enabled = folder.id !in selectedFolderId
                                )
                            },
                            selectedIds = selectedFolderId,
                            onSelectionChange = { newSelectedIds ->
                                val folder = newSelectedIds.firstOrNull()

                                if (folder == selectedFolderId.firstOrNull()) return@Select

                                coroutineScope.launch {
                                    isLoading = true

                                    if (folder == null || folder == UNKNOWN_FOLDER_ID) {
                                        val removeFileFromFolderRes = removeFileFromFolder(
                                            context = context,
                                            fileId = currentFile.id,
                                            folderId = fileFolderId
                                        )

                                        removeFileFromFolderRes.onSuccess {
                                            updateData()
                                        }
                                    } else {
                                        val addFileToFolderRes = addFileToFolder(
                                            context = context,
                                            fileId = currentFile.id,
                                            folderId = folder
                                        )

                                        addFileToFolderRes.onSuccess {
                                            updateData()
                                        }
                                    }

                                    isLoading = false
                                }
                            }
                        )

                        Spacer()

                        Text(
                            text = currentFile.id,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                fontWeight = FontWeight.Bold
                            ),
                        )

                        Spacer()

                        Row(
                            modifier = Modifier
                                .padding(
                                    start = 4.dp,
                                    bottom = 4.dp
                                )
                        ) {
                            IconButton(
                                onClick = {
                                    // TODO: show file edit popup
                                },
                                enabled = !isLoading,
                                color = Orange,
                                iconColor = White,
                                icon = painterResource(id = R.drawable.edit),
                                iconContentDescription = "Edit file"
                            )

                            IconButtonSpacer()

                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        isLoading = true

                                        // TODO: first delete confirmation popup

//                                        val deleteRes = deleteFile(
//                                            context = context,
//                                            fileId = currentFile.id
//                                        )
//
//                                        deleteRes
//                                            .onSuccess {
//                                                updateData()
//                                                onDismissRequest()
//                                            }
//                                            .onFailure {
//                                                Toast.makeText(
//                                                    context,
//                                                    "Failed to delete file: ${it.message}",
//                                                    Toast.LENGTH_LONG
//                                                ).show()
//                                            }

                                        isLoading = false
                                    }
                                },
                                enabled = !isLoading,
                                color = MaterialTheme.colorScheme.error,
                                iconColor = MaterialTheme.colorScheme.onError,
                                icon = painterResource(id = R.drawable.delete),
                                iconContentDescription = "Delete file"
                            )

                            IconButtonSpacer()

                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        isLoading = true

                                        val updateFileRes = updateFile(
                                            context = context,
                                            fileId = currentFile.id,
                                            favorite = !(currentFile.favorite)
                                        )

                                        updateFileRes
                                            .onSuccess {
                                                updateData()
                                            }
                                            .onFailure {
                                                Toast.makeText(
                                                    context,
                                                    "Failed to update file: ${it.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }

                                        isLoading = false
                                    }
                                },
                                enabled = !isLoading,
                                color = if (currentFile.favorite) Yellow else DarkGray,
                                iconColor = White,
                                icon = if (currentFile.favorite)
                                    painterResource(id = R.drawable.star_filled)
                                else
                                    painterResource(id = R.drawable.star),
                                iconContentDescription = if (currentFile.favorite) "Unfavorite file" else "Favorite file"
                            )

                            IconButtonSpacer()

                            IconButton(
                                onClick = {
                                    val fileUrl = "${serverUrl}${currentFile.url}"

                                    val intent = Intent(Intent.ACTION_VIEW, fileUrl.toUri())
                                    context.startActivity(intent)
                                },
                                enabled = !isLoading || serverUrl == null,
                                color = MaterialTheme.colorScheme.primary,
                                iconColor = MaterialTheme.colorScheme.onPrimary,
                                icon = painterResource(id = R.drawable.open_new),
                                iconContentDescription = "Open file in browser"
                            )

                            IconButtonSpacer()

                            CopyUrlButton(
                                context = context,
                                enabled = !isLoading || serverUrl == null,
                                standardUrl = "${serverUrl}${currentFile.url}",
                                rawUrl = "$serverUrl/raw${currentFile.url}"
                            )

                            IconButtonSpacer()

                            var selectedUri by remember { mutableStateOf<Uri?>(null) }
                            var selectedPath by remember { mutableStateOf<String?>(null) }

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

                                    val fileName = currentFile.originalName ?: currentFile.name

                                    val tempFile = java.io.File(context.cacheDir, fileName)
                                    val tempDestinationPath = tempFile.absolutePath

                                    if (tempFile.exists()) tempFile.delete()

                                    val downloadRes = downloadFile(
                                        context = context,
                                        fileId = currentFile.id,
                                        destinationPath = tempDestinationPath,
                                        notificationTitle = "Downloading file",
                                        notificationContent = "Downloading ${currentFile.name}"
                                    )

                                    downloadRes
                                        .onSuccess {
                                            try {
                                                val docUri = DocumentsContract.buildDocumentUriUsingTree(
                                                    selectedUri,
                                                    DocumentsContract.getTreeDocumentId(selectedUri)
                                                )

                                                val fileUri = DocumentsContract.createDocument(
                                                    context.contentResolver,
                                                    docUri,
                                                    currentFile.type,
                                                    fileName
                                                )

                                                if (fileUri != null) {
                                                    context.contentResolver.openOutputStream(fileUri)?.use { out ->
                                                        tempFile.inputStream().use { inp ->
                                                            inp.copyTo(out)
                                                        }
                                                    }

                                                    showToast("File downloaded to ${selectedPath}/$fileName")
                                                } else {
                                                    showToast("Failed to create file in selected directory")
                                                }
                                            } catch (e: Exception) {
                                                Log.e("LargeFileDisplay", "Failed to copy file to selected directory", e)

                                                showToast("Failed to copy file to selected directory: ${e.message}")
                                            } finally {
                                                tempFile.delete()
                                            }
                                        }
                                        .onFailure {
                                            Log.e("LargeFileDisplay", "Failed to download file", it)

                                            showToast("Failed to download file: ${it.message}")
                                        }
                                }
                            }

                            val directoryPicker = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.OpenDocumentTree()
                            ) { uri: Uri? ->
                                uri?.let {
                                    context.contentResolver.takePersistableUriPermission(
                                        it,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                    )
                                    selectedUri = it
                                    selectedPath = getDisplayPath(it)
                                    performDownload()
                                }
                            }

                            IconButton(
                                onClick = {
                                    if (selectedUri == null) {
                                        directoryPicker.launch(null)

                                        return@IconButton
                                    }

                                    performDownload()
                                },
                                enabled = !isLoading,
                                color = DarkGray,
                                iconColor = White,
                                icon = painterResource(id = R.drawable.download),
                                iconContentDescription = "Download file"
                            )

                            if (currentFile.type == APK_MIMETYPE) {
                                IconButtonSpacer()

                                IconButton(
                                    onClick = {},
                                    enabled = !isLoading,
                                    color = DarkGray,
                                    iconColor = White,
                                    icon = painterResource(id = R.drawable.apk_install),
                                    iconContentDescription = "Install APK file"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Stat(
    label: String,
    value: String,
    icon: Painter,
    iconContentDescription: String,
) {
    Spacer()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 4.dp
            )
    ) {
        Icon(
            painter = icon,
            contentDescription = iconContentDescription,
            modifier = Modifier.size(40.dp)
        )

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Column {
            Text(
                text = label,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
private fun Spacer() {
    Spacer(
        modifier = Modifier.height(15.dp)
    )
}

@Composable
private fun IconButton(
    icon: Painter,
    iconContentDescription: String,
    color: Color,
    iconColor: Color,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
            .size(35.dp)
            .background(
                if (enabled) color else color.copy(alpha = 0.5f),
            ),
        shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = iconContentDescription,
            tint = if (enabled) iconColor else iconColor.copy(alpha = 0.5f),
            modifier = Modifier.size(25.dp)
        )
    }
}

@Composable
private fun IconButtonSpacer() {
    Spacer(
        modifier = Modifier.width(8.dp)
    )
}

@Composable
private fun CopyUrlButton(
    context: Context,
    standardUrl: String,
    rawUrl: String,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopStart)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { expanded = true },
                enabled = enabled,
                color = DarkGray,
                iconColor = White,
                icon = painterResource(id = R.drawable.content_copy),
                iconContentDescription = "Copy file URL"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Copy URL"
                    )
                },
                onClick = {
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.content_copy),
                        contentDescription = "Copy file URL"
                    )
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Copy Raw URL"
                    )
                },
                onClick = {
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.copy_all),
                        contentDescription = "Copy raw file URL"
                    )
                }
            )
        }
    }
}