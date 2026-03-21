package com.stefdp.zipline.screens.upload.file

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.LocalWebSettings
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.components.HeaderButton
import com.stefdp.zipline.components.LocalFilePreview
import com.stefdp.zipline.components.Select
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.BaseFolder
import com.stefdp.zipline.network.requests.getFolders
import com.stefdp.zipline.screens.FilesScreen
import com.stefdp.zipline.screens.SettingsScreen
import com.stefdp.zipline.utils.FileUploadState
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.SelectedFile
import com.stefdp.zipline.utils.compressionFormats
import com.stefdp.zipline.utils.deletesAtDates
import com.stefdp.zipline.utils.formatBytes
import com.stefdp.zipline.utils.getFileInfo
import com.stefdp.zipline.utils.getFolderPath
import com.stefdp.zipline.utils.horizontalLazyScrollbar
import com.stefdp.zipline.utils.nameFormats
import com.stefdp.zipline.utils.parseBytes
import com.stefdp.zipline.utils.verticalScrollWithScrollbar

val DecimalRegex = Regex("""^(\d)+\.?(\d?)+$""")
val NumberRegex = Regex("""^(\d)*$""")

@Composable
fun UploadFileScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity
) {
    var isUploading by remember { mutableStateOf(false) }

    var folders by remember { mutableStateOf(emptyList<BaseFolder>()) }

    val webSettings = LocalWebSettings.current

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
    val defaultNameFormat = webSettings?.config?.files?.defaultFormat ?: "random"
    val defaultCompressionFormat = webSettings?.config?.files?.defaultCompressionFormat ?: "png"

    var fileStates by remember { mutableStateOf(listOf<FileUploadState>()) }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
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

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderButton(
                    icon = painterResource(R.drawable.photo_camera),
                    contentDescription = "Take a picture",
                    enabled = !isUploading,
                    onClick = {
                        TODO("use camera")
                    },
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
                items(fileStates.size) { index ->
                    val fileState = fileStates[index]

                    LocalFilePreview(
                        fileState = fileState,
                        modifier = Modifier
                            .size(200.dp)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp)),
                        onClick = {
                            fileStates = fileStates.filter { it != fileState }
                        }
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
                    modifier = Modifier.height(16.dp)
                )
            }

            @Composable
            fun TextSpacer() {
                Spacer(
                    modifier = Modifier.height(8.dp)
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
                        label = {
                            Text("Default (${defaultDeletesAtDate})")
                        }

                    )
                ) + deletesAtDates,
                onSelectionChange = {
                    selectedDeletesAt = it
                },
                selectedIds = selectedDeletesAt,
            )

            SelectSpacer()

            Select(
                label = "Name Format",
                description = "The file name format to use when upload this file, the \"File name\" field will override this value.",
                options = listOf(
                    SelectOption(
                        id = "default",
                        label = {
                            Text("Default (${defaultNameFormat})")
                        }

                    )
                ) + nameFormats,
                onSelectionChange = { selectedNameFormat = it },
                selectedIds = selectedNameFormat,
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
                        label = {
                            Text("Default (.${defaultCompressionFormat})")
                        }

                    )
                ) + compressionFormats,
                onSelectionChange = { selectedCompressionFormat = it },
                selectedIds = selectedCompressionFormat,
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
                modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier.fillMaxWidth()
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
                        label = {
                            Text(
                                text = getFolderPath(folder, folders)
                            )
                        }
                    )
                },
                onSelectionChange = { selectedFolder = it },
                selectedIds = selectedFolder,
            )

            SelectSpacer()

            val domains = webSettings?.config?.domains ?: emptyList()

            Select(
                label = "Override Domain",
                description = "Override the domain with this value. This will change the domain returned in your uploads. Leave blank to use the default domain.",
                options = listOf(
                    SelectOption(
                        id = "default",
                        label = {
                            Text("Default domain")
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
            )

            TextSpacer()

            TextInput(
                value = overrideFileName,
                description = "Override the file name with this value. Leave blank to use the \"Name Format\" option. This value is ignored if you are uploading more than one file. This value is not saved to your browser, and is cleared after uploading.",
                onValueChange = {
                    overrideFileName = it
                },
                label = "Override File Name",
                modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier.fillMaxWidth()
            )

            TextSpacer()

            Switch(
                checked = addOriginalName,
                onCheckedChange = { addOriginalName = it },
                label = "Add Original Name",
                description = "Add the original file name, so that the file can be downloaded with the original name. This will still use the \"Name Format\" option for its file name."
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            onClick = {},
            enabled = !isUploading && fileStates.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Upload ${if (fileStates.size > 1) "${fileStates.size} files" else "file"}",
            )
        }
    }
}