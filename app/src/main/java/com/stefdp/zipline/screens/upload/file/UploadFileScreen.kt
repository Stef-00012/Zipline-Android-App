package com.stefdp.zipline.screens.upload.file

import android.Manifest
import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.LocalLoggedUser
import com.stefdp.zipline.LocalWebSettings
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.components.HeaderButton
import com.stefdp.zipline.components.LocalFilePreview
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.components.Select
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.FilesFormat
import com.stefdp.zipline.network.models.requests.UploadCompressionType
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.screens.SettingsScreen
import com.stefdp.zipline.components.IconButton
import com.stefdp.zipline.screens.FilesScreen
import com.stefdp.zipline.utils.DecimalRegex
import com.stefdp.zipline.utils.FileUploadState
import com.stefdp.zipline.utils.NumberRegex
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.SelectedFile
import com.stefdp.zipline.utils.UploadStatus
import com.stefdp.zipline.utils.compressionFormats
import com.stefdp.zipline.utils.copyUriToTempFile
import com.stefdp.zipline.utils.deletesAtDates
import com.stefdp.zipline.utils.formatBytes
import com.stefdp.zipline.utils.getFileInfo
import com.stefdp.zipline.utils.getFolderPath
import com.stefdp.zipline.utils.hasCameraPermission
import com.stefdp.zipline.utils.horizontalLazyScrollbar
import com.stefdp.zipline.utils.nameFormats
import com.stefdp.zipline.utils.parseBytes
import com.stefdp.zipline.utils.parseTimeToMillis
import com.stefdp.zipline.utils.verticalLazyScrollbar
import com.stefdp.zipline.utils.verticalScrollWithScrollbar
import ir.ehsannarmani.compose_charts.extensions.format
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun UploadFileScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    sharedFiles: List<String>? = null,
    viewModel: UploadFileViewModel = viewModel()
) {
    val localLoggedUser = LocalLoggedUser.current

    if (localLoggedUser == null) {
        navController.navigate(LoginScreen()) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    val state by viewModel.state.collectAsState()

    val webSettings = LocalWebSettings.current

    val domains = webSettings?.config?.domains ?: emptyList()

    val maxFileSize = parseBytes(
        webSettings?.config?.files?.maxFileSize ?: "100mb"
    )
    val maxChunkSize = parseBytes(
        webSettings?.config?.chunks?.max ?: "95mb"
    )
    val chunkSize = parseBytes(
        webSettings?.config?.chunks?.size ?: "25mb"
    )
    val maxFileExpiration = parseTimeToMillis(
        webSettings?.config?.files?.maxExpiration ?: "365d"
    )

    val chunksEnabled = webSettings?.config?.chunks?.enabled ?: false

    val defaultDeletesAtDate = webSettings?.config?.files?.defaultExpiration ?: "never"
    val defaultNameFormat = webSettings?.config?.files?.defaultFormat ?: FilesFormat.RANDOM
    val defaultCompressionFormat = webSettings?.config?.files?.defaultCompressionFormat ?: UploadCompressionType.PNG

    LaunchedEffect(Unit) {
        viewModel.initData(
            context = context,
            sharedFiles = sharedFiles,
        )
    }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            val newFiles = uris.mapNotNull { uri ->
                val fileExists = state.fileStates.find { it.file.uri == uri } != null

                if (fileExists) return@mapNotNull null

                getFileInfo(context, uri)?.let { (name, size, mimeType) ->
                    if (size >= maxFileSize) {
                        Notification.show(
                            activity = activity,
                        ) {
                            Text(
                                text = "File is too large: $name"
                            )
                        }

                        return@mapNotNull null
                    }

                    FileUploadState(
                        file = SelectedFile(
                            uri = uri,
                            displayName = name,
                            size = size,
                            type = mimeType
                        )
                    )
                }
            }

            viewModel.addFileStates(newFiles)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.refreshFolders(
            context = context,
            onError = {
                Notification.show(
                    activity = activity,
                ) {
                    Text(
                        text = it
                    )
                }
            }
        )
    }

    val clipboardManager = LocalClipboard.current

    val coroutineScope = rememberCoroutineScope()

    Popup(
        showPopup = !state.isUploading && state.fileStates.any { it.status == UploadStatus.FAILED || it.status == UploadStatus.COMPLETE },
        onDismissRequest = {
            viewModel.clearFinishedFileStates()
        },
        scrollable = false,
    ) {
        val lazyListState = rememberLazyListState()

        Text(
            text = "Uploaded Files",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxWidth()
                .verticalLazyScrollbar(
                    listState = lazyListState,
                ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val successfulFiles = state.fileStates.filter { it.status == UploadStatus.COMPLETE }
            val failedFiles = state.fileStates.filter { it.status == UploadStatus.FAILED }

            if (successfulFiles.isNotEmpty()) {
                items(successfulFiles.size) { index ->
                    val fileState = successfulFiles[index]

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${fileState.url}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.tertiary,
                                textDecoration = TextDecoration.Underline
                            ),
                            modifier = Modifier
                                .weight(0.75f)
                                .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                                .clickable(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, fileState.url?.toUri())
                                        activity.startActivity(intent)
                                    }
                                )
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(0.25f)
                        ) {
                            IconButton(
                                icon = painterResource(R.drawable.content_copy),
                                iconContentDescription = "Copy URL",
                                onClick = {
                                    coroutineScope.launch {
                                        val clipData = ClipData.newPlainText("File URL", fileState.url).toClipEntry()

                                        clipboardManager.setClipEntry(clipData)

                                        Notification.show(
                                            activity = activity,
                                        ) {
                                            Text(
                                                text = "File link copied to clipboard"
                                            )
                                        }
                                    }
                                },
                                color = MaterialTheme.colorScheme.primary,
                                iconColor = MaterialTheme.colorScheme.onPrimary
                            )

                            IconButton(
                                icon = painterResource(R.drawable.open_new),
                                iconContentDescription = "Open URL",
                                onClick = {
                                    coroutineScope.launch {
                                        val intent = Intent(Intent.ACTION_VIEW, fileState.url?.toUri())
                                        activity.startActivity(intent)
                                    }
                                },
                                color = MaterialTheme.colorScheme.primary,
                                iconColor = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }

            if (failedFiles.isNotEmpty()) {
                items(failedFiles.size) { index ->
                    val fileState = failedFiles[index]

                    Text(
                        text = "${fileState.file.displayName} - ${fileState.errorMessage ?: "Unknown error"}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.error,
                        ),
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier.padding(
            start = 12.dp,
            end = 12.dp,
            top = 12.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "Upload Files",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )

                Text(
                    text = "Click a file to remove it",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    ),
                )
            }

            fun checkCameraPermission(): Boolean {
                val result = hasCameraPermission(context)

                return result
            }

            val takePictureLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicture()
            ) { success ->
                if (success) {
                    state.tempCameraUri?.let { uri ->
                        val cachedFile = copyUriToTempFile(context, uri, "camera_capture.jpg")

                        if (cachedFile != null) {
                            viewModel.addFileStates(
                                FileUploadState(
                                    file = SelectedFile(
                                        uri = uri,
                                        displayName = cachedFile.name,
                                        size = cachedFile.length(),
                                        type = context.contentResolver.getType(uri) ?: "application/octet-stream"
                                    )
                                )
                            )
                        }
                    }
                }
            }

            fun launchCameraAction() {
                val directory = File(context.cacheDir, "camera_temp").apply { mkdirs() }
                val file = File(directory, "temp_image_${System.currentTimeMillis()}.jpg")

                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )

                viewModel.setTempCameraUri(uri)
                takePictureLauncher.launch(uri)
            }

            val requestPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    launchCameraAction()
                } else {
                    Notification.show(
                        activity = activity,
                    ) {
                        Text(
                            text = "Permission denied"
                        )
                    }
                }
            }

            fun handleCameraButton() {
                val hasPermission = checkCameraPermission()

                if (hasPermission) {
                    launchCameraAction()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderButton(
                    icon = painterResource(R.drawable.photo_camera),
                    contentDescription = "Take a picture",
                    enabled = !state.isUploading,
                    onClick = { handleCameraButton() },
                    iconColor = MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.width(5.dp)
                )

                HeaderButton(
                    icon = painterResource(R.drawable.folder_open),
                    contentDescription = "Go to files",
                    onClick = {
                        navController.navigate(FilesScreen())
                    },
                    enabled = !state.isUploading,
                )
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Container(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(MaterialTheme.colorScheme.surface),
            scrollable = false
        ) {
            val lazyListState = rememberLazyListState()

            LazyRow(
                state = lazyListState,
                modifier = Modifier
                    .horizontalLazyScrollbar(lazyListState)
            ) {
                val validFilesStates = state.fileStates.filter { it.status == UploadStatus.UPLOADING || it.status == UploadStatus.PENDING || it.status == UploadStatus.FAILED }

                items(validFilesStates.size) { index ->
                    val fileState = validFilesStates[index]

                    LocalFilePreview(
                        fileState = fileState,
                        modifier = Modifier
                            .size(200.dp)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp)),
                        onClick = {
                            viewModel.removeFileState(fileState)
                        },
                        clickEnabled = !state.isUploading
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "${formatBytes(maxFileSize, 0)} limit per file",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Button(
            onClick = { filePicker.launch(arrayOf("*/*")) },
            enabled = !state.isUploading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Select File(s)",
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScrollWithScrollbar(
                    scrollState = scrollState,
                    scrollbarConfig = ScrollbarConfig(
                        alwaysKeepScrollbar = true
                    )
                )
                .padding(8.dp)
        ) {
            @Composable
            fun SelectSpacer() {
                Spacer(
                    modifier = Modifier.height(24.dp)
                )
            }

            @Composable
            fun TextSpacer() {
                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }

            Select(
                label = "Deletes At",
                description = buildAnnotatedString {
                    val rawString = "The file will automatically delete itself after this time. You can set a default expiration time in the [link]settings[/link]."
                    val linkTag = "[link]"
                    val linkEndTag = "[/link]"

                    val startIndex = rawString.indexOf(linkTag)
                    val endIndex = rawString.indexOf(linkEndTag)

                    if (startIndex != -1 && endIndex != -1) {
                        val cleanString = rawString.replace(linkTag, "").replace(linkEndTag, "")

                        append(cleanString)

                        addLink(
                            clickable = LinkAnnotation.Clickable(
                                linkInteractionListener = LinkInteractionListener {
                                    navController.navigate(SettingsScreen)
                                },
                                tag = "settings",
                                styles = TextLinkStyles(
                                    style = SpanStyle(
                                        color = MaterialTheme.colorScheme.tertiary,
                                        textDecoration = TextDecoration.Underline
                                    ),
                                )
                            ),
                            start = startIndex,
                            end = endIndex - linkTag.length
                        )
                    } else {
                        append(rawString)
                    }
                },
                options = listOf(
                    SelectOption(
                        id = "default",
                        label = { enabled ->
                            Text(
                                text = "Default (${defaultDeletesAtDate})",
                                color = if (enabled)
                                    MaterialTheme.colorScheme.onBackground
                                else
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }

                    )
                ) + deletesAtDates
                    .filter { (id, _) -> parseTimeToMillis(id) <= maxFileExpiration }
                    .map { (id, label) ->
                        SelectOption(
                            id = id,
                            label = { enabled ->
                                Text(
                                    text = label,
                                    color = if (enabled)
                                        MaterialTheme.colorScheme.onBackground
                                    else
                                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            }
                        )
                    },
                onSelectionChange = {
                    viewModel.setSelectedDeletesAt(it)
                },
                selectedIds = state.selectedDeletesAt,
                enabled = !state.isUploading
            )

            SelectSpacer()

            Select(
                label = "Name Format",
                description = "The file name format to use when upload this file, the \"File name\" field will override this value.",
                options = listOf(
                    SelectOption(
                        id = "default",
                        label = { enabled ->
                            Text(
                                text = "Default (${defaultNameFormat})",
                                color = if (enabled)
                                    MaterialTheme.colorScheme.onBackground
                                else
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }

                    )
                ) + nameFormats.map { (id, label) ->
                    SelectOption(
                        id = id.toString(),
                        label = { enabled ->
                            Text(
                                text = label,
                                color = if (enabled)
                                    MaterialTheme.colorScheme.onBackground
                                else
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    )
                },
                onSelectionChange = {
                    viewModel.setSelectedNameFormat(it)
                },
                selectedIds = state.selectedNameFormat,
                enabled = !state.isUploading
            )

            SelectSpacer()

            Select(
                label = "Compression Format",
                description = buildAnnotatedString {
                    val rawString = "The image compression format to use [bold]only when a compression percent is specified[/bold]. Leave at \"default\" to use the server default compression format."
                    val boldTag = "[bold]"
                    val boldEndTag = "[/bold]"

                    val startIndex = rawString.indexOf(boldTag)
                    val endIndex = rawString.indexOf(boldEndTag)

                    if (startIndex != -1 && endIndex != -1) {
                        val cleanString = rawString.replace(boldTag, "").replace(boldEndTag, "")

                        append(cleanString)

                        addStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold
                            ),
                            start = startIndex,
                            end = endIndex - boldTag.length
                        )
                    } else {
                        append(rawString)
                    }
                },
                options = listOf(
                    SelectOption(
                        id = "default",
                        label = { enabled ->
                            Text(
                                text = "Default (.${defaultCompressionFormat})",
                                color = if (enabled)
                                    MaterialTheme.colorScheme.onBackground
                                else
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }

                    )
                ) + compressionFormats.map { (id, label) ->
                    SelectOption(
                        id = id.toString(),
                        label = { enabled ->
                            Text(
                                text = label,
                                color = if (enabled)
                                    MaterialTheme.colorScheme.onBackground
                                else
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    )
                },
                onSelectionChange = {
                    viewModel.setSelectedCompressionFormat(it)
                },
                selectedIds = state.selectedCompressionFormat,
                enabled = !state.isUploading
            )

            TextSpacer()

            TextInput(
                value = state.compressionPercentage,
                description = "The compression level to use on images (only). The above format will be used to compress images. Leave blank to disable compression.",
                onValueChange = {
                    if (DecimalRegex.matches(it.text) || it.text.isEmpty()) {
                        viewModel.setCompressionPercentage(it)
                    }
                },
                label = "Compression Percent",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isUploading
            )

            TextSpacer()

            TextInput(
                value = state.maxViews,
                description = "The maximum number of views the files can have before they are deleted. Leave blank to allow as many views as you want.",
                onValueChange = {
                    if (NumberRegex.matches(it.text) || it.text.isEmpty()) {
                        viewModel.setMaxViews(it)
                    }
                },
                label = "Max Views",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isUploading
            )

            SelectSpacer()

            Select(
                label = "Add to a Folder",
                description = "Add this file to a folder. Use the \"/ (Root)\" option to not add the file to a folder. This value is not saved to your browser, and is cleared after uploading.",
                options = listOf(
                    SelectOption(
                        id = "default",
                        label = {
                            Text("/ (Root)")
                        }
                    )
                ) + state.folders.map { folder ->
                    SelectOption(
                        id = folder.id,
                        label = { enabled ->
                            Text(
                                text = getFolderPath(folder, state.folders),
                                color = if (enabled)
                                    MaterialTheme.colorScheme.onBackground
                                else
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    )
                },
                onSelectionChange = {
                    viewModel.setSelectedFolder(it)
                },
                selectedIds = state.selectedFolder,
                enabled = !state.isUploading
            )

            SelectSpacer()

            Select(
                label = "Override Domain",
                description = "Override the domain with this value. This will change the domain returned in your uploads. Leave blank to use the default domain.",
                options = listOf(
                    SelectOption(
                        id = "default",
                        label = { enabled ->
                            Text(
                                text = "Default domain",
                                color = if (enabled)
                                    MaterialTheme.colorScheme.onBackground
                                else
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }

                    )
                ) + domains.map { domain ->
                    SelectOption(
                        id = domain,
                        label = {
                            Text(domain)
                        }
                    )
                },
                onSelectionChange = {
                    viewModel.setSelectedOverrideDomain(it)
                },
                selectedIds = state.selectedOverrideDomain,
                enabled = !state.isUploading
            )

            TextSpacer()

            TextInput(
                value = state.overrideFileName,
                description = "Override the file name with this value. Leave blank to use the \"Name Format\" option. This value is ignored if you are uploading more than one file. This value is not saved to your browser, and is cleared after uploading.",
                onValueChange = {
                    viewModel.setOverrideFileName(it)
                },
                label = "Override File Name",
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isUploading
            )

            TextSpacer()

            TextInput(
                value = state.password,
                description = "Set a password for these files. Leave blank to disable password protection. This value is not saved to your browser, and is cleared after uploading.",
                onValueChange = {
                    viewModel.setPassword(it)
                },
                label = "Password",
                isPassword = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isUploading
            )

            TextSpacer()

            Switch(
                checked = state.addOriginalName,
                onCheckedChange = {
                    viewModel.setAddOriginalName(it)
                },
                label = "Add Original Name",
                description = "Add the original file name, so that the file can be downloaded with the original name. This will still use the \"Name Format\" option for its file name.",
                enabled = !state.isUploading
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        val uploadingFile = state.fileStates.find { it.status == UploadStatus.UPLOADING }

        if (uploadingFile != null) {
            Text(
                text = "${uploadingFile.file.displayName} (${formatBytes(uploadingFile.file.size)})",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )

            LinearProgressIndicator(
                progress = { uploadingFile.progressPercent / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = ProgressIndicatorDefaults.linearColor,
                trackColor = ProgressIndicatorDefaults.linearTrackColor,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                drawStopIndicator = {}
            )
        }

        Button(
            onClick = {
                viewModel.upload(
                    context = context,
                    defaultNameFormat = defaultNameFormat,
                    defaultCompressionFormat = defaultCompressionFormat,
                    domains = domains,
                    defaultDeletesAtDate = defaultDeletesAtDate,
                    chunksEnabled = chunksEnabled,
                    maxChunkSize = maxChunkSize,
                    chunkSize = chunkSize,
                )
            },
            enabled = !state.isUploading && state.fileStates.any { it.status == UploadStatus.PENDING || it.status == UploadStatus.FAILED },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (uploadingFile != null && state.isUploading)
                    "Uploading... ${uploadingFile.progressPercent.toDouble().format(1)}%"
                else
                    "Upload ${if (state.fileStates.size > 1) "${state.fileStates.size} files" else "file"}",
            )
        }
    }
}