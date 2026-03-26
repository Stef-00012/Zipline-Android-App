package com.stefdp.zipline.screens.upload.text

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.stefdp.zipline.LocalLoggedUser
import com.stefdp.zipline.LocalWebSettings
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.HeaderButton
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.components.Select
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.BaseFolder
import com.stefdp.zipline.network.models.CodeMapEntry
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
import com.stefdp.zipline.utils.NumberRegex
import com.stefdp.zipline.ui.theme.getButtonColors
import com.stefdp.zipline.utils.FileUploadState
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
import com.stefdp.zipline.utils.nameFormats
import com.stefdp.zipline.utils.parseBytes
import com.stefdp.zipline.utils.verticalScrollWithScrollbar
import ir.ehsannarmani.compose_charts.extensions.format
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import kotlin.time.Clock
import kotlin.time.Duration

const val MAX_TEXT_FILE_SIZE = 10L * 1024L * 1024L // 10MB

@Composable
fun UploadTextScreen(
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

    val mimetypes = webSettings?.codeMap ?: listOf(
        CodeMapEntry(
            name = "Plain text",
            mimetype = "text/x-zipline-plain",
            extension = "txt"
        )
    )

    val defaultDeletesAtDate = webSettings?.config?.files?.defaultExpiration ?: "never"
    val defaultNameFormat = webSettings?.config?.files?.defaultFormat ?: FilesFormat.RANDOM
    val defaultCompressionFormat = webSettings?.config?.files?.defaultCompressionFormat ?: UploadCompressionType.PNG

    var fileState by remember { mutableStateOf<FileUploadState?>(null) }
    var uploadText by remember { mutableStateOf(TextFieldValue("")) }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val fileExists = fileState?.file?.uri == uri

            if (fileExists) return@rememberLauncherForActivityResult

            getFileInfo(context, uri)?.let { (name, size, mimeType) ->
                if (size >= maxFileSize) {
                    Toast.makeText(
                        context,
                        "File is too large: $name",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@rememberLauncherForActivityResult
                }

                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                val contentResolver = context.contentResolver

                uploadText = contentResolver.openInputStream(uri)?.use { inputStream ->
                    val reader = inputStream.bufferedReader()
                    val writer = StringBuilder()
                    var totalRead = 0L
                    var limitReached = false

                    reader.lineSequence().forEach { line ->
                        if (!limitReached) {
                            if (totalRead + line.length + 1 <= MAX_TEXT_FILE_SIZE) {
                                writer.append(line).append("\n")
                                totalRead += line.length + 1
                            } else {
                                writer.append("\n[File too large to display. Showing first ${formatBytes(MAX_TEXT_FILE_SIZE)}...]")
                                limitReached = true
                            }
                        }
                    }

                    val finalString = writer.toString()

                    TextFieldValue(finalString)
                } ?: uploadText

                fileState = FileUploadState(
                    file = SelectedFile(
                        uri = uri,
                        displayName = name,
                        size = size,
                        type = mimeType
                    )
                )
            }
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
        showPopup = !isUploading && (fileState?.status == UploadStatus.COMPLETE || fileState?.status == UploadStatus.FAILED),
        onDismissRequest = {
            fileState = null
        },
        scrollable = false,
    ) {
        Text(
            text = "Uploaded File",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(
            modifier = Modifier.height(8.dp)

        )
        val isFileSuccessful = fileState?.status == UploadStatus.COMPLETE
        val isFileFailed = fileState?.status == UploadStatus.FAILED

        if (isFileSuccessful) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${fileState?.url}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier.clickable(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, fileState?.url?.toUri())
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
                                val clipData = ClipData.newRawUri("File URL", fileState?.url?.toUri()).toClipEntry()

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
                                val intent = Intent(Intent.ACTION_VIEW, fileState?.url?.toUri())
                                context.startActivity(intent)
                            }
                        },
                        color = MaterialTheme.colorScheme.primary,
                        iconColor = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        } else if (isFileFailed) {
            Text(
                text = "${fileState?.file?.displayName} - ${fileState?.errorMessage ?: "Unknown error"}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.error,
                ),
            )
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
                    text = "Upload Text",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )
            }

            HeaderButton(
                icon = painterResource(R.drawable.folder_open),
                contentDescription = "Go to files",
                onClick = {
                    navController.navigate(FilesScreen())
                },
                enabled = !isUploading,
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextInput(
            value = uploadText,
            onValueChange = {
                if (uploadText.text.length <= maxFileSize) {
                    uploadText = it
                }
            },
            singleLine = false,
            readOnly = fileState != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(8.dp),
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "${formatBytes(maxFileSize, 0)} limit",
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

        if (fileState == null) {
            Button(
                onClick = { filePicker.launch(arrayOf(
                    "text/*",
                    "application/json",
                    "application/xml",
                    "application/x-yaml",
                    "application/x-sh",
                    "application/xhtml+xml",
                    "application/ld+json"
                )) },
                enabled = !isUploading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Select File",
                )
            }
        } else {
            Button(
                onClick = {
                    fileState = null
                    uploadText = TextFieldValue("")
                },
                enabled = !isUploading,
                modifier = Modifier.fillMaxWidth(),
                colors = getButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.error,
                    disabledContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                )
            ) {
                Text(
                    text = "Remove File",
                )
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        val scrollState = rememberScrollState()

        var selectedFileType by remember { mutableStateOf(setOf("txt")) }
        var selectedDeletesAt by remember { mutableStateOf(setOf("never")) }
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
                label = "File Type",
                options = mimetypes.map { entry ->
                    SelectOption(
                        id = entry.extension,
                        label = { enabled ->
                            Text(
                                text = entry.name,
                                color = if (enabled)
                                    MaterialTheme.colorScheme.onBackground
                                else
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    )
                },
                onSelectionChange = { selectedFileType = it },
                selectedIds = selectedFileType,
                enabled = !isUploading
            )

            SelectSpacer()

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

        if (fileState != null && isUploading) {
            Text(
                text = "${fileState!!.file.displayName} (${formatBytes(fileState!!.file.size)})",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )

            LinearProgressIndicator(
                progress = { (fileState?.progressPercent ?: 0f) / 100f },
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
                    if (fileState == null) {
                        if (uploadText.text.isBlank()) return@launch

                        val fileType = mimetypes
                            .firstOrNull {
                                it.extension == selectedFileType.first()
                            } ?: mimetypes
                            .firstOrNull {
                                it.extension == "txt"
                            } ?: CodeMapEntry(
                                name = "Plain text",
                                mimetype = "text/x-zipline-plain",
                                extension = "txt"
                            )

                        val tempFile = File.createTempFile(
                            "upload",
                            ".${fileType.extension}",
                            context.cacheDir
                        )

                        tempFile.writeText(uploadText.text)

                        fileState = FileUploadState(
                            file = SelectedFile(
                                uri = tempFile.toUri(),
                                displayName = "upload.${fileType.extension}",
                                size = tempFile.length(),
                                type = fileType.mimetype
                            )
                        )
                    }

                    isUploading = true

                    if (fileState!!.status == UploadStatus.COMPLETE) {
                        return@launch
                    }

                    fileState = fileState!!.copy(
                        status = UploadStatus.UPLOADING,
                        progressPercent = 0f,
                        speedText = "",
                        bytesTransferred = 0,
                        errorMessage = null,
                    )

                    val tempFile = copyUriToTempFile(
                        context = context,
                        uri = fileState!!.file.uri,
                        displayName = fileState!!.file.displayName
                    )

                    if (tempFile == null) {
                        fileState = fileState!!.copy(
                            status = UploadStatus.FAILED,
                            errorMessage = "Failed to read file",
                        )

                        return@launch
                    }

                    val fileType = mimetypes.firstOrNull {
                        it.extension == selectedFileType.firstOrNull()
                    }?.mimetype ?: mimetypes.firstOrNull {
                        it.extension == "txt"
                    }?.mimetype ?: "text/x-zipline-plain"

                    val fileExtension = mimetypes.firstOrNull {
                        it.extension == selectedFileType.firstOrNull()
                    }?.extension ?: mimetypes.firstOrNull {
                        it.extension == "txt"
                    }?.extension ?: "txt"

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
                            fileMimeType = fileType,
                            fileExtension = fileExtension,
                            chunkSize = chunkSize,
                            originalName = if (addOriginalName) fileState!!.file.displayName else null,
                            deletesAt = deletesAt,
                            format = nameFormat,
                            maxViews = maxViews.text.toLongOrNull(),
                            folder = folder,
                            domain = overrideDomain,
                            password = password.text.takeIf { it.isNotBlank() },
                            imageCompressionType = compressionType,
                            imageCompressionPercent = compressionPercentage.text.toFloatOrNull()?.coerceIn(0f, 100f),
                            notificationTitle = "Uploading Text File",
                            notificationContent = fileState!!.file.displayName,
                            onProgress = { total, transferred, speed ->
                                val totalFloat = total.toFloat()
                                val transferredFloat = transferred.toFloat()

                                val percent = if (totalFloat > 0f) ((transferredFloat * 100f) / total) else 0f

                                fileState = fileState!!.copy(
                                    progressPercent = percent,
                                    speedText = formatSpeed(speed),
                                    bytesTransferred = transferred
                                )
                            },
                        )

                        uploadPartialFileResult
                            .onSuccess { response ->
                                fileState = fileState!!.copy(
                                    status = UploadStatus.COMPLETE,
                                    progressPercent = 100f,
                                    url = response.files.first().url
                                )
                            }
                            .onFailure { error ->
                                fileState = fileState!!.copy(
                                    status = UploadStatus.FAILED,
                                    errorMessage = error.message,
                                )
                            }
                    } else {
                        val uploadFileResult = uploadFile(
                            context = context,
                            filePath = tempFile.absolutePath,
                            filename = overrideFileName.text.takeIf { it.isNotBlank() },
                            fileMimeType = fileType,
                            fileExtension = fileExtension,
                            originalName = if (addOriginalName) fileState!!.file.displayName else null,
                            deletesAt = deletesAt,
                            format = nameFormat,
                            maxViews = maxViews.text.toLongOrNull(),
                            folder = folder,
                            domain = overrideDomain,
                            password = password.text.takeIf { it.isNotBlank() },
                            imageCompressionType = compressionType,
                            imageCompressionPercent = compressionPercentage.text.toFloatOrNull()?.coerceIn(0f, 100f),
                            notificationTitle = "Uploading Text File",
                            notificationContent = fileState!!.file.displayName,
                            onProgress = { total, transferred, speed ->
                                val totalFloat = total.toFloat()
                                val transferredFloat = transferred.toFloat()

                                val percent = if (totalFloat > 0f) ((transferredFloat * 100f) / total) else 0f

                                fileState = fileState!!.copy(
                                    progressPercent = percent,
                                    speedText = formatSpeed(speed),
                                    bytesTransferred = transferred,
                                )
                            },
                        )

                        uploadFileResult
                            .onSuccess { response ->
                                fileState = fileState!!.copy(
                                    status = UploadStatus.COMPLETE,
                                    progressPercent = 100f,
                                    url = response.files.first().url
                                )
                            }
                            .onFailure { error ->
                                fileState = fileState!!.copy(
                                    status = UploadStatus.FAILED,
                                    errorMessage = error.message,
                                )
                            }
                    }

                    tempFile.delete()

                    uploadText = TextFieldValue("")

                    isUploading = false
                }
            },
            enabled = !isUploading && ((fileState != null && fileState?.status != UploadStatus.COMPLETE && fileState?.status != UploadStatus.FAILED) || uploadText.text.isNotBlank()),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (fileState != null && isUploading)
                    "Uploading... ${fileState!!.progressPercent.toDouble().format(1)}%"
                else
                    "Upload Text",
            )
        }
    }
}