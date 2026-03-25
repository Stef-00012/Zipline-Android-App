package com.stefdp.zipline.screens.upload.file

import android.Manifest
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.LocalLoggedUser
import com.stefdp.zipline.LocalWebSettings
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.components.HeaderButton
import com.stefdp.zipline.components.LocalFilePreview
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.components.Select
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.BaseFolder
import com.stefdp.zipline.network.models.FilesFormat
import com.stefdp.zipline.network.models.requests.UploadCompressionType
import com.stefdp.zipline.network.requests.getFolders
import com.stefdp.zipline.network.requests.uploadFile
import com.stefdp.zipline.network.requests.uploadPartialFile
import com.stefdp.zipline.screens.FilesScreen
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.screens.SettingsScreen
import com.stefdp.zipline.screens.files.components.IconButton
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
import com.stefdp.zipline.utils.formatSpeed
import com.stefdp.zipline.utils.getFileInfo
import com.stefdp.zipline.utils.getFolderPath
import com.stefdp.zipline.utils.hasCameraPermission
import com.stefdp.zipline.utils.horizontalLazyScrollbar
import com.stefdp.zipline.utils.nameFormats
import com.stefdp.zipline.utils.parseBytes
import com.stefdp.zipline.utils.verticalLazyScrollbar
import com.stefdp.zipline.utils.verticalScrollWithScrollbar
import ir.ehsannarmani.compose_charts.extensions.format
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import kotlin.time.Clock
import kotlin.time.Duration

@Composable
fun UploadFileScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity
) {
    val localLoggedUser = LocalLoggedUser.current

    if (localLoggedUser == null) {
        navController.navigate(LoginScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    var isUploading by remember { mutableStateOf(false) }

    var folders by remember { mutableStateOf(emptyList<BaseFolder>()) }

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

    val defaultDeletesAtDate = webSettings?.config?.files?.defaultExpiration ?: "never"
    val defaultNameFormat = webSettings?.config?.files?.defaultFormat ?: FilesFormat.RANDOM
    val defaultCompressionFormat = webSettings?.config?.files?.defaultCompressionFormat ?: UploadCompressionType.PNG

    var fileStates by remember { mutableStateOf(listOf<FileUploadState>()) }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            val newFiles = uris.mapNotNull { uri ->
                val fileExists = fileStates.find { it.file.uri == uri } != null

                if (fileExists) return@mapNotNull null

                getFileInfo(context, uri)?.let { (name, size, mimeType) ->
                    if (size >= maxFileSize) {
                        Toast.makeText(
                            context,
                            "File is too large: $name",
                            Toast.LENGTH_SHORT
                        ).show()

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

            fileStates = fileStates + newFiles
        }
    }

    LaunchedEffect(Unit) {
        val foldersRes = getFolders(
            context = context,
            excludeFiles = true
        )

        foldersRes.onSuccess {
            folders = it
        }
    }

    val clipboardManager = LocalClipboard.current

    val coroutineScope = rememberCoroutineScope()

    Popup(
        showPopup = !isUploading && fileStates.any { it.status == UploadStatus.FAILED || it.status == UploadStatus.COMPLETE },
        onDismissRequest = {
            fileStates = fileStates.filter { it.status == UploadStatus.UPLOADING || it.status == UploadStatus.PENDING }
        },
        scrollable = false,
    ) {
        val successfulFiles by remember(fileStates) { mutableStateOf(fileStates.filter { it.status == UploadStatus.COMPLETE }) }
        val failedFiles by remember(fileStates) { mutableStateOf(fileStates.filter { it.status == UploadStatus.FAILED }) }

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
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline
                            ),
                            modifier = Modifier.clickable(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, fileState.url?.toUri())
                                    context.startActivity(intent)
                                }
                            )
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            IconButton(
                                icon = painterResource(R.drawable.content_copy),
                                iconContentDescription = "Copy URL",
                                onClick = {
                                    coroutineScope.launch {
                                        val clipData = ClipData.newRawUri("File URL", fileState.url?.toUri()).toClipEntry()

                                        clipboardManager.setClipEntry(clipData)

                                        Toast.makeText(
                                            context,
                                            "File link copied to clipboard",
                                            Toast.LENGTH_LONG
                                        ).show()
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
                                        context.startActivity(intent)
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

            var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

            val takePictureLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicture()
            ) { success ->
                if (success) {
                    tempCameraUri?.let { uri ->
                        val cachedFile = copyUriToTempFile(context, uri, "camera_capture.jpg")

                        if (cachedFile != null) {
                            fileStates += FileUploadState(
                                file = SelectedFile(
                                    uri = uri,
                                    displayName = cachedFile.name,
                                    size = cachedFile.length(),
                                    type = context.contentResolver.getType(uri) ?: "application/octet-stream"
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

                tempCameraUri = uri
                takePictureLauncher.launch(uri)
            }

            val requestPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    launchCameraAction()
                } else {
                    Toast.makeText(
                        context,
                        "Permission denied",
                        Toast.LENGTH_SHORT
                    ).show()
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
                    enabled = !isUploading,
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
                    enabled = !isUploading,
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
                val validFilesStates = fileStates.filter { it.status == UploadStatus.UPLOADING || it.status == UploadStatus.PENDING || it.status == UploadStatus.FAILED }

                items(validFilesStates.size) { index ->
                    val fileState = validFilesStates[index]

                    LocalFilePreview(
                        fileState = fileState,
                        modifier = Modifier
                            .size(200.dp)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp)),
                        onClick = {
                            fileStates = fileStates.filter { it != fileState }
                        },
                        clickEnabled = !isUploading
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
            enabled = !isUploading,
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

        var selectedDeletesAt by remember { mutableStateOf(setOf("default")) }
        var selectedNameFormat by remember { mutableStateOf(setOf("default")) }
        var selectedCompressionFormat by remember { mutableStateOf(setOf("default")) }
        var compressionPercentage by remember { mutableStateOf(TextFieldValue("")) }
        var maxViews by remember { mutableStateOf(TextFieldValue("")) }
        var selectedFolder by remember { mutableStateOf(setOf("default")) }
        var selectedOverrideDomain by remember { mutableStateOf(setOf("default")) }
        var overrideFileName by remember { mutableStateOf(TextFieldValue("")) }
        var password by remember { mutableStateOf(TextFieldValue("")) }
        var addOriginalName by remember { mutableStateOf(false) }

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
                                        color = MaterialTheme.colorScheme.primary,
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
                ) + deletesAtDates.map { (id, label) ->
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
                    selectedDeletesAt = it
                },
                selectedIds = selectedDeletesAt,
                enabled = !isUploading
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
                onSelectionChange = { selectedNameFormat = it },
                selectedIds = selectedNameFormat,
                enabled = !isUploading
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
                onSelectionChange = { selectedCompressionFormat = it },
                selectedIds = selectedCompressionFormat,
                enabled = !isUploading
            )

            TextSpacer()

            TextInput(
                value = compressionPercentage,
                description = "The compression level to use on images (only). The above format will be used to compress images. Leave blank to disable compression.",
                onValueChange = {
                    if (DecimalRegex.matches(it.text) || it.text.isEmpty()) {
                        compressionPercentage = it
                    }
                },
                label = "Compression Percent",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isUploading
            )

            TextSpacer()

            TextInput(
                value = maxViews,
                description = "The maximum number of views the files can have before they are deleted. Leave blank to allow as many views as you want.",
                onValueChange = {
                    if (NumberRegex.matches(it.text) || it.text.isEmpty()) {
                        maxViews = it
                    }
                },
                label = "Max Views",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isUploading
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
                ) + folders.map { folder ->
                    SelectOption(
                        id = folder.id,
                        label = { enabled ->
                            Text(
                                text = getFolderPath(folder, folders),
                                color = if (enabled)
                                    MaterialTheme.colorScheme.onBackground
                                else
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    )
                },
                onSelectionChange = { selectedFolder = it },
                selectedIds = selectedFolder,
                enabled = !isUploading
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
                onSelectionChange = { selectedOverrideDomain = it },
                selectedIds = selectedOverrideDomain,
                enabled = !isUploading
            )

            TextSpacer()

            TextInput(
                value = overrideFileName,
                description = "Override the file name with this value. Leave blank to use the \"Name Format\" option. This value is ignored if you are uploading more than one file. This value is not saved to your browser, and is cleared after uploading.",
                onValueChange = {
                    overrideFileName = it
                },
                label = "Override File Name",
                modifier = Modifier.fillMaxWidth(),
                enabled = !isUploading
            )

            TextSpacer()

            TextInput(
                value = password,
                description = "Set a password for these files. Leave blank to disable password protection. This value is not saved to your browser, and is cleared after uploading.",
                onValueChange = {
                    password = it
                },
                label = "Password",
                isPassword = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isUploading
            )

            TextSpacer()

            Switch(
                checked = addOriginalName,
                onCheckedChange = { addOriginalName = it },
                label = "Add Original Name",
                description = "Add the original file name, so that the file can be downloaded with the original name. This will still use the \"Name Format\" option for its file name.",
                enabled = !isUploading
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        val uploadingFile = fileStates.find { it.status == UploadStatus.UPLOADING }

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
                coroutineScope.launch(Dispatchers.IO) {
                    isUploading = true

                    val totalFiles = fileStates.size

                    for (index in fileStates.indices) {
                        val state = fileStates[index]

                        if (state.status == UploadStatus.COMPLETE) {
                            continue
                        }

                        fileStates = fileStates.toMutableList().also {
                            it[index] = state.copy(
                                status = UploadStatus.UPLOADING,
                                progressPercent = 0f,
                                speedText = "",
                                bytesTransferred = 0,
                                errorMessage = null,
                            )
                        }

                        val tempFile = copyUriToTempFile(
                            context = context,
                            uri = state.file.uri,
                            displayName = state.file.displayName
                        )

                        if (tempFile == null) {
                            fileStates = fileStates.toMutableList().also {
                                it[index] = state.copy(
                                    status = UploadStatus.FAILED,
                                    errorMessage = "Failed to read file",
                                )
                            }

                            continue
                        }

                        val fileExtension = state.file.displayName.substringAfterLast('.', "")

                        val deletesAt = selectedDeletesAt.firstOrNull().takeIf { it != "default" } ?: defaultDeletesAtDate

                        val nameFormat = if (selectedNameFormat.firstOrNull() == "default")
                            defaultNameFormat
                        else
                            nameFormats.firstOrNull {
                                it.first.toString() == selectedNameFormat.firstOrNull()
                            }?.first ?: defaultNameFormat

                        val compressionType = if (selectedCompressionFormat.firstOrNull() == "default")
                            defaultCompressionFormat
                        else
                            compressionFormats.firstOrNull {
                                it.first.toString() == selectedCompressionFormat.firstOrNull()
                            }?.first ?: defaultCompressionFormat

                        val folder = if (selectedFolder.firstOrNull() == "default")
                            null
                        else
                            folders.firstOrNull {
                                it.id == selectedFolder.firstOrNull()
                            }?.id

                        val overrideDomain = if (selectedOverrideDomain.firstOrNull() == "default")
                            null
                        else
                            domains.firstOrNull {
                                it == selectedOverrideDomain.firstOrNull()
                            }

                        if (tempFile.length() >= maxChunkSize) {
                            val uploadPartialFileResult = uploadPartialFile(
                                context = context,
                                filePath = tempFile.absolutePath,
                                filename = overrideFileName.text.takeIf { it.isNotBlank() },
                                fileExtension = fileExtension,
                                chunkSize = chunkSize,
                                originalName = if (addOriginalName) state.file.displayName else null,
                                deletesAt = deletesAt,
                                format = nameFormat,
                                maxViews = maxViews.text.toLongOrNull(),
                                folder = folder,
                                domain = overrideDomain,
                                password = password.text.takeIf { it.isNotBlank() },
                                imageCompressionType = compressionType,
                                imageCompressionPercent = compressionPercentage.text.toFloatOrNull()?.coerceIn(0f, 100f),
                                notificationTitle = "Uploading (${index + 1}/$totalFiles)",
                                notificationContent = state.file.displayName,
                                onProgress = { total, transferred, speed ->
                                    val totalFloat = total.toFloat()
                                    val transferredFloat = transferred.toFloat()

                                    val percent = if (totalFloat > 0f) ((transferredFloat * 100f) / total) else 0f

                                    fileStates = fileStates.toMutableList().also {
                                        it[index] = it[index].copy(
                                            progressPercent = percent,
                                            speedText = formatSpeed(speed),
                                            bytesTransferred = transferred
                                        )
                                    }
                                },
                            )

                            uploadPartialFileResult
                                .onSuccess { response ->
                                    fileStates = fileStates.toMutableList().also {
                                        it[index] = it[index].copy(
                                            status = UploadStatus.COMPLETE,
                                            progressPercent = 100f,
                                            url = response.files.first().url
                                        )
                                    }
                                }
                                .onFailure { error ->
                                    fileStates = fileStates.toMutableList().also {
                                        it[index] = it[index].copy(
                                            status = UploadStatus.FAILED,
                                            errorMessage = error.message,
                                        )
                                    }
                                }
                        } else {
                            val uploadFileResult = uploadFile(
                                context = context,
                                filePath = tempFile.absolutePath,
                                filename = overrideFileName.text.takeIf { it.isNotBlank() },
                                fileExtension = fileExtension,
                                originalName = if (addOriginalName) state.file.displayName else null,
                                deletesAt = deletesAt,
                                format = nameFormat,
                                maxViews = maxViews.text.toLongOrNull(),
                                folder = folder,
                                domain = overrideDomain,
                                password = password.text.takeIf { it.isNotBlank() },
                                imageCompressionType = compressionType,
                                imageCompressionPercent = compressionPercentage.text.toFloatOrNull()?.coerceIn(0f, 100f),
                                notificationTitle = "Uploading (${index + 1}/$totalFiles)",
                                notificationContent = state.file.displayName,
                                onProgress = { total, transferred, speed ->
                                    val totalFloat = total.toFloat()
                                    val transferredFloat = transferred.toFloat()

                                    val percent = if (totalFloat > 0f) ((transferredFloat * 100f) / total) else 0f

                                    fileStates = fileStates.toMutableList().also {
                                        it[index] = it[index].copy(
                                            progressPercent = percent,
                                            speedText = formatSpeed(speed),
                                            bytesTransferred = transferred,
                                        )
                                    }
                                },
                            )

                            uploadFileResult
                                .onSuccess { response ->
                                    fileStates = fileStates.toMutableList().also {
                                        it[index] = it[index].copy(
                                            status = UploadStatus.COMPLETE,
                                            progressPercent = 100f,
                                            url = response.files.first().url
                                        )
                                    }
                                }
                                .onFailure { error ->
                                    fileStates = fileStates.toMutableList().also {
                                        it[index] = it[index].copy(
                                            status = UploadStatus.FAILED,
                                            errorMessage = error.message,
                                        )
                                    }
                                }
                        }

                        tempFile.delete()
                    }

                    isUploading = false
                }
            },
            enabled = !isUploading && fileStates.any { it.status == UploadStatus.PENDING || it.status == UploadStatus.FAILED },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (uploadingFile != null && isUploading)
                    "Uploading... ${uploadingFile.progressPercent.toDouble().format(1)}%"
                else
                    "Upload ${if (fileStates.size > 1) "${fileStates.size} files" else "file"}",
            )
        }
    }
}