package com.stefdp.zipline.screens.folders

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.google.gson.annotations.SerializedName
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.LocalLoggedUser
import com.stefdp.zipline.LocalScreenViewState
import com.stefdp.zipline.LocalUpdateScreenViewState
import com.stefdp.zipline.Logger
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.DownloadFilePasswordPrompt
import com.stefdp.zipline.components.EnabledCheckbox
import com.stefdp.zipline.components.FilePreview
import com.stefdp.zipline.components.HeaderButton
import com.stefdp.zipline.components.table.TableCellData
import com.stefdp.zipline.components.table.TableHeaderData
import com.stefdp.zipline.components.table.TableRowData
import com.stefdp.zipline.network.models.BaseFolder
import com.stefdp.zipline.network.models.File
import com.stefdp.zipline.network.models.requests.GetFilesQuerySearchField
import com.stefdp.zipline.components.IconButton
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.Pager
import com.stefdp.zipline.components.PromptPopup
import com.stefdp.zipline.components.Select
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.components.Tag
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.components.largefiledisplay.LargeFileDisplay
import com.stefdp.zipline.components.table.Table
import com.stefdp.zipline.components.table.TableScrollbarConfig
import com.stefdp.zipline.network.models.Tag
import com.stefdp.zipline.network.models.requests.GetFilesQuerySortBy
import com.stefdp.zipline.network.requests.deleteFile
import com.stefdp.zipline.network.requests.downloadFile
import com.stefdp.zipline.network.requests.exportFolder
import com.stefdp.zipline.network.requests.getFiles
import com.stefdp.zipline.network.requests.getFolderExportSize
import com.stefdp.zipline.network.requests.getFolders
import com.stefdp.zipline.network.requests.getTags
import com.stefdp.zipline.network.requests.updateFolder
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.screens.UploadFileScreen
import com.stefdp.zipline.screens.folders.components.CreateFolderPopup
import com.stefdp.zipline.screens.folders.components.DeleteFolderPopup
import com.stefdp.zipline.screens.folders.components.EditFolderNamePopup
import com.stefdp.zipline.screens.folders.components.LargeFolderDisplay
import com.stefdp.zipline.screens.folders.components.MoveFolderPopup
import com.stefdp.zipline.ui.theme.DarkGray
import com.stefdp.zipline.ui.theme.White
import com.stefdp.zipline.ui.theme.Yellow
import com.stefdp.zipline.utils.STORAGE_FILE_DOWNLOAD_FOLDER_KEY
import com.stefdp.zipline.utils.STORAGE_FOLDER_EXPORT_DOWNLOAD_FOLDER_KEY
import com.stefdp.zipline.utils.STORAGE_SERVER_URL_KEY
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.SecureStorage
import com.stefdp.zipline.utils.SortOrder
import com.stefdp.zipline.utils.StorageUtil
import com.stefdp.zipline.utils.ZiplineViewStateType
import com.stefdp.zipline.utils.camelCaseToHumanReadable
import com.stefdp.zipline.utils.formatBytes
import com.stefdp.zipline.utils.getDisplayPath
import com.stefdp.zipline.utils.shimmerable
import com.stefdp.zipline.utils.verticalLazyScrollbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nl.jacobras.humanreadable.HumanReadable
import kotlin.time.Instant

const val COMPACT_VIEW_FILE_COUNT = 20L
const val DETAILED_VIEW_FILE_COUNT = 15L

const val VIEW_STATE_KEY = "foldersViewState"

@Composable
fun FoldersScreen(
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

    var mainFolder by remember { mutableStateOf<BaseFolder?>(null) }
    var folders by remember { mutableStateOf<List<BaseFolder>?>(null) }
    var allFolders by remember { mutableStateOf<List<BaseFolder>>(emptyList()) }

    var foldersPath by remember { mutableStateOf<List<BaseFolder>>(emptyList()) }

    var folderFiles by remember { mutableStateOf<List<File>?>(null) }

    var tags by remember { mutableStateOf<List<Tag>?>(null) }

    var serverUrl by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var tagsLoading by remember { mutableStateOf(true) }

    val screenViewState = LocalScreenViewState.current
    val updateScreenViewState = LocalUpdateScreenViewState.current

    val viewState = screenViewState.folders

    var currentPage by remember { mutableLongStateOf(1L) }
    var totalPages by remember { mutableLongStateOf(1L) }

    val filesPerPage = if (viewState == ZiplineViewStateType.COMPACT) COMPACT_VIEW_FILE_COUNT else DETAILED_VIEW_FILE_COUNT

    var folderSortKey by remember { mutableStateOf(GetFoldersQuerySortBy.CREATED_AT) }
    var folderSortOrder by remember { mutableStateOf(SortOrder.DESC) }

    var fileSortKey by remember { mutableStateOf(GetFilesQuerySortBy.CREATED_AT) }
    var fileSortOrder by remember { mutableStateOf(SortOrder.DESC) }

    val defaultSortOrder = SortOrder.UNSPECIFIED

    var fileSearchKey by remember { mutableStateOf<GetFilesQuerySearchField?>(null) }
    var fileSearchValue by remember { mutableStateOf(TextFieldValue("")) }

    var createNewFolderPopupOpen by remember { mutableStateOf(false) }

    var selectedFolderExportUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFolderExportPath by remember { mutableStateOf<String?>(null) }

    var selectedFileDownloadUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileDownloadPath by remember { mutableStateOf<String?>(null) }

    fun sortFolders(folders: List<BaseFolder>): List<BaseFolder> {
        if (viewState == ZiplineViewStateType.LARGE) return folders.sortedBy { Instant.parse(it.createdAt) }.reversed()

        val ascending = when (folderSortKey) {
            GetFoldersQuerySortBy.NAME ->
                folders.sortedBy { it.name }

            GetFoldersQuerySortBy.PUBLIC ->
                folders.sortedBy { it.public }

            GetFoldersQuerySortBy.UPLOADS ->
                folders.sortedBy { it.allowUploads }

            GetFoldersQuerySortBy.CREATED_AT ->
                folders.sortedBy { Instant.parse(it.createdAt) }

            GetFoldersQuerySortBy.UPDATED_AT ->
                folders.sortedBy { Instant.parse(it.updatedAt) }
        }

        return when (folderSortOrder) {
            SortOrder.ASC -> ascending
            SortOrder.DESC -> ascending.reversed()
            SortOrder.UNSPECIFIED -> folders
        }
    }

    LaunchedEffect(
        folderSortKey,
        folderSortOrder,
        viewState
    ) {
        if (viewState == ZiplineViewStateType.COMPACT) {
            folders?.let {
                folders = sortFolders(it)
            }
        }
    }

    suspend fun updateFolders() {
        isLoading = true

        val foldersRes = getFolders(
            context = context,
            parent = mainFolder?.id,
            root = mainFolder == null,
            excludeFiles = true,
        )

        foldersRes
            .onSuccess {
                folders = if (viewState == ZiplineViewStateType.COMPACT) sortFolders(it) else it
            }

        isLoading = false
    }

    suspend fun updateAllFolders() {
        val allFoldersRes = getFolders(
            context = context,
            excludeFiles = true,
        )

        allFoldersRes
            .onSuccess {
                allFolders = it
            }
    }

    suspend fun updateFiles() {
        if (mainFolder == null) {
            folderFiles = null
        } else {
            val filesRes = getFiles(
                context = context,
                folderId = mainFolder!!.id,
                perPage = filesPerPage,
                page = currentPage,
                sortOrder = fileSortOrder,
                sortBy = fileSortKey,
                searchField = fileSearchKey,
                searchQuery = fileSearchValue.text.ifEmpty { null }
            )

            filesRes
                .onSuccess {
                    folderFiles = it.page
                    totalPages = it.pages ?: 1L
                }
        }
    }

    suspend fun toggleAnonymousUploads(
        folder: BaseFolder,
    ) {
        isLoading = true

        val updateRes = updateFolder(
            context = context,
            folderId = folder.id,
            allowUploads = !folder.allowUploads
        )

        updateRes
            .onSuccess {
                updateFolders()

                Notification.show(
                    context = context,
                    activity = activity,
                    content = {
                        Text(
                            text = "${it.name} will ${if (it.allowUploads) "now allow" else "no longer allow"} anonymous uploads"
                        )
                    }
                )
            }

        isLoading = false
    }

    suspend fun togglePublic(
        folder: BaseFolder,
    ) {
        isLoading = true

        val updateRes = updateFolder(
            context = context,
            folderId = folder.id,
            isPublic = !folder.public
        )

        updateRes
            .onSuccess {
                updateFolders()

                Notification.show(
                    context = context,
                    activity = activity,
                    content = {
                        Text(
                            text = "${it.name} is now ${if (it.public) "public" else "private"}"
                        )
                    }
                )
            }

        isLoading = false
    }

    LaunchedEffect(mainFolder) {
        updateFolders()
        updateFiles()
    }

    LaunchedEffect(Unit) {
        val secureStore = SecureStorage.getInstance(context)

        serverUrl = secureStore.get(STORAGE_SERVER_URL_KEY)

        val fileDownloadFolder = secureStore.get(STORAGE_FILE_DOWNLOAD_FOLDER_KEY)

        if (fileDownloadFolder != null) {
            selectedFileDownloadUri = fileDownloadFolder.toUri()
            selectedFileDownloadPath = getDisplayPath(fileDownloadFolder.toUri())
        }

        val folderExportFolder = secureStore.get(STORAGE_FOLDER_EXPORT_DOWNLOAD_FOLDER_KEY)

        if (folderExportFolder != null) {
            selectedFolderExportUri = folderExportFolder.toUri()
            selectedFolderExportPath = getDisplayPath(folderExportFolder.toUri())
        }

        updateFolders()
        updateFiles()
        updateAllFolders()

        val tagsRes = getTags(context)

        tagsRes
            .onSuccess {
                tags = it
            }
    }

    val coroutineScope = rememberCoroutineScope()

    fun downloadShowToast(message: String) {
        coroutineScope.launch(Dispatchers.Main) {
            Notification.show(
                context = context,
                activity = activity,
                content = {
                    Text(message)
                }
            )
        }
    }

    var downloadFolderType by remember { mutableStateOf(DownloadFolderType.FOLDER) }

    var downloadFilePassword by remember { mutableStateOf<String?>(null) }
    var fileRequiresPassword by remember { mutableStateOf(false) }

    var fileToDownload by remember { mutableStateOf<File?>(null) }
    var folderToExport by remember { mutableStateOf<BaseFolder?>(null) }

    fun performFileDownload(file: File) {
        val fileFits = StorageUtil.canFitFile(
            context = context,
            uri = selectedFileDownloadUri!!,
            fileSize = file.size
        )

        if (!fileFits) {
            downloadShowToast("Not enough space in the selected directory to download the file")

            return
        }

        val fileFitsCache = StorageUtil.canFitInternalCache(
            context = context,
            fileSize = file.size
        )

        if (!fileFitsCache) {
            downloadShowToast("Not enough space in the internal cache to download the file")

            return
        }

        if (file.password == true && downloadFilePassword.isNullOrBlank()) {
            fileRequiresPassword = true
            return
        }

        coroutineScope.launch(Dispatchers.IO) {
            downloadShowToast("Starting download...")

            val fileName = file.originalName ?: file.name

            val tempFile = java.io.File(context.cacheDir, fileName)
            val tempDestinationPath = tempFile.absolutePath

            if (tempFile.exists()) tempFile.delete()

            val downloadRes = downloadFile(
                context = context,
                fileId = file.id,
                destinationPath = tempDestinationPath,
                notificationTitle = "Downloading file",
                notificationContent = "Downloading ${file.name}",
                password = downloadFilePassword
            )

            downloadRes
                .onSuccess {
                    try {
                        val docUri =
                            DocumentsContract.buildDocumentUriUsingTree(
                                selectedFileDownloadUri,
                                DocumentsContract.getTreeDocumentId(
                                    selectedFileDownloadUri
                                )
                            )

                        val fileUri = DocumentsContract.createDocument(
                            context.contentResolver,
                            docUri,
                            file.type,
                            fileName
                        )

                        if (fileUri != null) {
                            context.contentResolver.openOutputStream(fileUri)
                                ?.use { out ->
                                    tempFile.inputStream().use { inp ->
                                        inp.copyTo(out)
                                    }
                                }

                            downloadShowToast("File downloaded to ${selectedFileDownloadPath}/$fileName")
                        } else {
                            downloadShowToast("Failed to create file in selected directory")
                        }
                    } catch (e: Exception) {
                        Logger.error(
                            "FoldersScreen",
                            "Failed to copy file to selected directory",
                            e
                        )

                        downloadShowToast("Failed to copy file to selected directory: ${e.message}")
                    } finally {
                        tempFile.delete()
                    }
                }
                .onFailure {
                    Logger.error("FoldersScreen", "Failed to download file", it)

                    downloadShowToast("Failed to download file: ${it.message}")
                }

            fileRequiresPassword = false
            downloadFilePassword = null
            fileToDownload = null
        }
    }

    fun performFolderExport(folder: BaseFolder) {
        coroutineScope.launch(Dispatchers.IO) {
            val folderExportSize = getFolderExportSize(
                context = context,
                folderId = folder.id
            )

            folderExportSize
                .onSuccess {
                    val fileFits = StorageUtil.canFitFile(
                        context = context,
                        uri = selectedFolderExportUri!!,
                        fileSize = it
                    )

                    if (!fileFits) {
                        downloadShowToast("Not enough space in the selected directory to download the folder")

                        return@launch
                    }

                    val fileFitsCache = StorageUtil.canFitInternalCache(
                        context = context,
                        fileSize = it
                    )

                    if (!fileFitsCache) {
                        downloadShowToast("Not enough space in the internal cache to download the folder")

                        return@launch
                    }
                }
                .onFailure {
                    Notification.show(
                        context = context,
                        activity = activity,
                        content = {
                            Text(
                                text = "Failed to get folder export size: ${it.message}"
                            )
                        }
                    )
                }

            downloadShowToast("Starting download...")

            val fileName = folder.name

            val tempFile = java.io.File(context.cacheDir, fileName)
            val tempDestinationPath = tempFile.absolutePath

            if (tempFile.exists()) tempFile.delete()

            val exportRes = exportFolder(
                context = context,
                folderId = folder.id,
                destinationPath = tempDestinationPath,
                notificationTitle = "Downloading folder export",
                notificationContent = "Downloading ${folder.name}",
            )

            exportRes
                .onSuccess {
                    try {
                        val docUri =
                            DocumentsContract.buildDocumentUriUsingTree(
                                selectedFolderExportUri,
                                DocumentsContract.getTreeDocumentId(
                                    selectedFolderExportUri
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

                            downloadShowToast("Folder export downloaded to ${selectedFolderExportPath}/$fileName.zip")
                        } else {
                            downloadShowToast("Failed to create folder export in selected directory")
                        }
                    } catch (e: Exception) {
                        Logger.error(
                            "FoldersScreen",
                            "Failed to copy folder export to selected directory",
                            e
                        )

                        downloadShowToast("Failed to copy folder export to selected directory: ${e.message}")
                    } finally {
                        tempFile.delete()
                    }
                }
                .onFailure {
                    Logger.error("FoldersScreen", "Failed to download folder export", it)

                    downloadShowToast("Failed to download folder export: ${it.message}")
                }

            fileRequiresPassword = false
            downloadFilePassword = null
            folderToExport = null
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

            if (downloadFolderType == DownloadFolderType.FOLDER) {
                selectedFolderExportUri = it
                selectedFolderExportPath = getDisplayPath(it)

                coroutineScope.launch {
                    secureStore.set(STORAGE_FOLDER_EXPORT_DOWNLOAD_FOLDER_KEY, selectedFolderExportUri.toString())
                }

                folderToExport?.let { folder ->
                    performFolderExport(folder)
                }
            } else {
                selectedFileDownloadUri = it
                selectedFileDownloadPath = getDisplayPath(it)

                coroutineScope.launch {
                    secureStore.set(STORAGE_FILE_DOWNLOAD_FOLDER_KEY, selectedFileDownloadUri.toString())
                }

                fileToDownload?.let { file ->
                    performFileDownload(file)
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
            Text(
                text = "Folders",
                maxLines = 1,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderButton(
                    icon = painterResource(R.drawable.create_new_folder),
                    contentDescription = "Create Folder",
                    onClick = {
                        createNewFolderPopupOpen = true
                    },
                    enabled = !isLoading,
                )

                Spacer(
                    modifier = Modifier.width(5.dp)
                )

                HeaderButton(
                    icon = if (viewState == ZiplineViewStateType.COMPACT)
                        painterResource(R.drawable.view_agenda)
                    else  painterResource(R.drawable.view_module),
                    contentDescription = if (viewState == ZiplineViewStateType.COMPACT) "Switch to detailed view" else "Switch to compact view",
                    onClick = {
                        val newState = if (viewState == ZiplineViewStateType.COMPACT) ZiplineViewStateType.LARGE else ZiplineViewStateType.COMPACT

                        updateScreenViewState(
                            screenViewState.copy(
                                folders = newState
                            )
                        )
                    },
                    enabled = !isLoading,
                )
            }
        }

        if (foldersPath.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.home),
                    contentDescription = "Root folder",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            onClick = {
                                mainFolder = null
                                foldersPath = emptyList()
                            }
                        )
                )

                foldersPath.forEachIndexed { index, folder ->
                    Text("/")

                    Text(
                        text = folder.name,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.clickable(
                            onClick = {
                                mainFolder = folder
                                foldersPath = foldersPath.subList(0, index + 1)
                            }
                        )
                    )
                }
            }
        }

        var deleteFolder by remember { mutableStateOf<BaseFolder?>(null) }

        DeleteFolderPopup(
            context = context,
            activity = activity,
            folder = deleteFolder,
            isLoading = isLoading,
            setIsLoading = { isLoading = it },
            allFolders = allFolders,
            updateFolders = ::updateFolders,
            updateAllFolders = ::updateAllFolders,
            showPopup = deleteFolder != null,
            onDismissRequest = { deleteFolder = null },
        )

        CreateFolderPopup(
            context = context,
            activity = activity,
            mainFolder = mainFolder,
            isLoading = isLoading,
            setIsLoading = { isLoading = it },
            updateFolders = ::updateFolders,
            updateAllFolders = ::updateAllFolders,
            showPopup = createNewFolderPopupOpen,
            onDismissRequest = { createNewFolderPopupOpen = false },
        )

        var editNameFolder by remember { mutableStateOf<BaseFolder?>(null) }

        EditFolderNamePopup(
            context = context,
            activity = activity,
            folder = editNameFolder,
            isLoading = isLoading,
            setIsLoading = { isLoading = it },
            updateFolders = ::updateFolders,
            showPopup = editNameFolder != null,
            onDismissRequest = { editNameFolder = null },
        )

        var moveFolder by remember { mutableStateOf<BaseFolder?>(null) }

        MoveFolderPopup(
            context = context,
            activity = activity,
            folder = moveFolder,
            allFolders = allFolders,
            isLoading = isLoading,
            setIsLoading = { isLoading = it },
            updateFolders = ::updateFolders,
            showPopup = moveFolder != null,
            onDismissRequest = { moveFolder = null },
        )

        val foldersLazyColumnListState = rememberLazyListState()

        if (viewState == ZiplineViewStateType.COMPACT) {
            Column(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .weight(0.35f)
                    .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                    )
            ) {
                val tableNameWidth = 350.dp
                val tablePublicWidth = 120.dp
                val tableUploadsWidth = 130.dp
                val tableCreatedWidth = 170.dp
                val tableLastUpdatedAtWidth = 170.dp
                val tableActionsWidth = 130.dp

                val headers: List<TableHeaderData> = listOf(
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Name",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableNameWidth,
                        name = "name",
                        sortable = true,
                        sortOrder = if (folderSortKey == GetFoldersQuerySortBy.NAME) folderSortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (folderSortKey != GetFoldersQuerySortBy.NAME) {
                                folderSortKey = GetFoldersQuerySortBy.NAME
                                folderSortOrder = SortOrder.ASC
                            } else if (folderSortOrder == SortOrder.UNSPECIFIED || folderSortOrder == SortOrder.DESC) {
                                folderSortOrder = SortOrder.ASC
                            } else {
                                folderSortOrder = SortOrder.DESC
                            }
                        },
                    ),
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Public",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tablePublicWidth,
                        name = "public",
                        sortable = true,
                        sortOrder = if (folderSortKey == GetFoldersQuerySortBy.PUBLIC) folderSortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (folderSortKey != GetFoldersQuerySortBy.PUBLIC) {
                                folderSortKey = GetFoldersQuerySortBy.PUBLIC
                                folderSortOrder = SortOrder.ASC
                            } else if (folderSortOrder == SortOrder.UNSPECIFIED || folderSortOrder == SortOrder.DESC) {
                                folderSortOrder = SortOrder.ASC
                            } else {
                                folderSortOrder = SortOrder.DESC
                            }
                        },
                    ),
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Uploads?",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableUploadsWidth,
                        name = "uploads",
                        sortable = true,
                        sortOrder = if (folderSortKey == GetFoldersQuerySortBy.UPLOADS) folderSortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (folderSortKey != GetFoldersQuerySortBy.UPLOADS) {
                                folderSortKey = GetFoldersQuerySortBy.UPLOADS
                                folderSortOrder = SortOrder.ASC
                            } else if (folderSortOrder == SortOrder.UNSPECIFIED || folderSortOrder == SortOrder.DESC) {
                                folderSortOrder = SortOrder.ASC
                            } else {
                                folderSortOrder = SortOrder.DESC
                            }
                        },
                    ),
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Created",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableCreatedWidth,
                        name = "created at",
                        sortable = true,
                        sortOrder = if (folderSortKey == GetFoldersQuerySortBy.CREATED_AT) folderSortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (folderSortKey != GetFoldersQuerySortBy.CREATED_AT) {
                                folderSortKey = GetFoldersQuerySortBy.CREATED_AT
                                folderSortOrder = SortOrder.ASC
                            } else if (folderSortOrder == SortOrder.UNSPECIFIED || folderSortOrder == SortOrder.DESC) {
                                folderSortOrder = SortOrder.ASC
                            } else {
                                folderSortOrder = SortOrder.DESC
                            }
                        },
                    ),
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Last update at",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableLastUpdatedAtWidth,
                        name = "lastUpdatedAt",
                        sortable = true,
                        sortOrder = if (folderSortKey == GetFoldersQuerySortBy.UPDATED_AT) folderSortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (folderSortKey != GetFoldersQuerySortBy.UPDATED_AT) {
                                folderSortKey = GetFoldersQuerySortBy.UPDATED_AT
                                folderSortOrder = SortOrder.ASC
                            } else if (folderSortOrder == SortOrder.UNSPECIFIED || folderSortOrder == SortOrder.DESC) {
                                folderSortOrder = SortOrder.ASC
                            } else {
                                folderSortOrder = SortOrder.DESC
                            }
                        },
                    ),
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Actions",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableActionsWidth,
                        name = "actions"
                    ),
                )

                val rows: List<TableRowData> = folders?.map { folder ->
                    TableRowData(
                        clickable = true,
                        onClick = {
                            mainFolder = folder
                            foldersPath = foldersPath + folder
                        },
                        cells = listOf(
                            TableCellData(
                                content = {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = folder.name,
                                        )

                                        val folderChildrenCount = folder.count?.children ?: 0

                                        if (folderChildrenCount > 0) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "$folderChildrenCount SUBFOLDER${if (folderChildrenCount > 1) "S" else ""}",
                                                )
                                            }
                                        }
                                    }
                                },
                                width = tableNameWidth,
                            ),
                            TableCellData(
                                content = {
                                    EnabledCheckbox(
                                        enabled = folder.public
                                    )
                                },
                                width = tablePublicWidth,
                            ),
                            TableCellData(
                                content = {
                                    EnabledCheckbox(
                                        enabled = folder.allowUploads
                                    )
                                },
                                width = tableUploadsWidth,
                            ),
                            TableCellData(
                                content = {
                                    Text(
                                        text = HumanReadable.timeAgo(Instant.parse(folder.createdAt)),
                                    )
                                },
                                width = tableCreatedWidth,
                            ),
                            TableCellData(
                                content = {
                                    Text(
                                        text = HumanReadable.timeAgo(Instant.parse(folder.updatedAt)),
                                    )
                                },
                                width = tableLastUpdatedAtWidth,
                            ),
                            TableCellData(
                                content = {
                                    @Composable
                                    fun ActionButtonSpacer() {
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }

                                    var expanded by remember { mutableStateOf(false) }

                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                                    ) {
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = "Open Folder",
                                                    color = if (isLoading)
                                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                                    else
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            },
                                            enabled = !isLoading,
                                            onClick = {
                                                mainFolder = folder
                                                foldersPath = foldersPath + folder

                                                expanded = false
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    painter = painterResource(R.drawable.folder_open),
                                                    contentDescription = "Open folder",
                                                    tint = if (isLoading)
                                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                                    else
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        )

                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = "Move Folder",
                                                    color = if (!isLoading)
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                    else
                                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                                )
                                            },
                                            enabled = !isLoading,
                                            onClick = {
                                                moveFolder = folder
                                                expanded = false
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    painter = painterResource(R.drawable.folder_copy),
                                                    contentDescription = "Move folder",
                                                    tint = if (!isLoading)
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                    else
                                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                                )
                                            }
                                        )

                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = "Export as ZIP",
                                                    color = if (!isLoading)
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                    else
                                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                                )
                                            },
                                            enabled = !isLoading,
                                            onClick = {
                                                folderToExport = folder

                                                if (selectedFolderExportUri == null) {
                                                    downloadFolderType = DownloadFolderType.FOLDER

                                                    directoryPicker.launch(null)

                                                    return@DropdownMenuItem
                                                }

                                                performFolderExport(folder)

                                                expanded = false
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    painter = painterResource(R.drawable.folder_zip),
                                                    contentDescription = "Export as ZIP",
                                                    tint = if (!isLoading)
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                    else
                                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                                )
                                            }
                                        )

                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = if (folder.public) "Make Private" else "Make Public",
                                                    color = if (!isLoading)
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                    else
                                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                                )
                                            },
                                            enabled = !isLoading,
                                            onClick = {
                                                coroutineScope.launch {
                                                    togglePublic(folder)
                                                    expanded = false
                                                }
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    painter = painterResource(
                                                        if (folder.public)
                                                            R.drawable.lock
                                                        else
                                                            R.drawable.lock_open
                                                    ),
                                                    contentDescription = if (folder.public) "Make private" else "Make public",
                                                    tint = if (!isLoading)
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                    else
                                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                                )
                                            }
                                        )

                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = if (folder.allowUploads) "Disallow Anonymous Uploads" else "Allow Anonymous Uploads",
                                                    color = if (!isLoading)
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                    else
                                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                                )
                                            },
                                            enabled = !isLoading,
                                            onClick = {
                                                coroutineScope.launch {
                                                    toggleAnonymousUploads(folder)
                                                    expanded = false
                                                }
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    painter = painterResource(
                                                        if (folder.allowUploads)
                                                            R.drawable.share_off
                                                        else
                                                            R.drawable.share
                                                    ),
                                                    contentDescription = if (folder.allowUploads) "Disallow anonymous uploads" else "Allow anonymous uploads",
                                                    tint = if (!isLoading)
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                    else
                                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                                )
                                            }
                                        )

                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = "Edit Name",
                                                    color = if (!isLoading)
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                    else
                                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                                )
                                            },
                                            enabled = !isLoading,
                                            onClick = {
                                                editNameFolder = folder
                                                expanded = false
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    painter = painterResource(R.drawable.edit),
                                                    contentDescription = "Edit name",
                                                    tint = if (!isLoading)
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                    else
                                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                                )
                                            }
                                        )

                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = "Delete",
                                                    color = if (!isLoading)
                                                        MaterialTheme.colorScheme.error
                                                    else
                                                        MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                                                )
                                            },
                                            enabled = !isLoading,
                                            onClick = {
                                                deleteFolder = folder
                                                expanded = false
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    painter = painterResource(R.drawable.delete),
                                                    contentDescription = "Delete",
                                                    tint = if (!isLoading)
                                                        MaterialTheme.colorScheme.error
                                                    else
                                                        MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                                                )
                                            }
                                        )
                                    }

                                    IconButton(
                                        icon = painterResource(R.drawable.more_horiz),
                                        iconContentDescription = "More actions",
                                        color = MaterialTheme.colorScheme.primary,
                                        iconColor = MaterialTheme.colorScheme.onPrimary,
                                        onClick = { expanded = true },
                                        enabled = !isLoading
                                    )

                                    ActionButtonSpacer()

                                    val clipboardManager = LocalClipboard.current

                                    IconButton(
                                        icon = painterResource(R.drawable.content_copy),
                                        iconContentDescription = "Copy folder link",
                                        color = MaterialTheme.colorScheme.primary,
                                        iconColor = MaterialTheme.colorScheme.onPrimary,
                                        onClick = {
                                            val fileUrl = "${serverUrl}/folder/${folder.id}"

                                            val clipData = ClipData.newRawUri("Folder URL", fileUrl.toUri()).toClipEntry()

                                            coroutineScope.launch {
                                                clipboardManager.setClipEntry(clipData)

                                                Notification.show(
                                                    context = context,
                                                    activity = activity,
                                                    content = {
                                                        Text(
                                                            text = "Folder link copied to clipboard"
                                                        )
                                                    }
                                                )
                                            }
                                        },
                                        enabled = serverUrl != null && !isLoading && folder.public
                                    )

                                    ActionButtonSpacer()

                                    IconButton(
                                        icon = painterResource(R.drawable.delete),
                                        iconContentDescription = "Delete folder",
                                        color = MaterialTheme.colorScheme.error,
                                        iconColor = MaterialTheme.colorScheme.onError,
                                        onClick = {
                                            deleteFolder = folder
                                        },
                                        enabled = serverUrl != null && !isLoading
                                    )
                                },
                                width = tableActionsWidth,
                            )
                        )
                    )
                } ?: emptyList()

                Table(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.35f),
                    headers = headers,
                    rows = rows,
                    loading = isLoading,
                    scrollbarConfig = TableScrollbarConfig(
                        vertical = ScrollbarConfig(
                            alwaysKeepScrollbar = true
                        ),
                        horizontal = ScrollbarConfig(
                            alwaysKeepScrollbar = true
                        )
                    )
                )
            }
        } else {
            val foldersModifier = Modifier.fillMaxWidth()
            val foldersHeightModifier = if (folders?.isEmpty() == true && mainFolder != null)
                foldersModifier.height(0.dp)
            else
                foldersModifier.weight(0.35f)

            LazyColumn(
                state = foldersLazyColumnListState,
                modifier = foldersHeightModifier
                    .padding(vertical = 8.dp)
                    .verticalLazyScrollbar(
                        listState = foldersLazyColumnListState,
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (folders == null || isLoading) {
                    items(5) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                                .fillMaxWidth()
                                .height(170.dp)
                                .shimmerable(
                                    enabled = true,
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    keepBackground = true
                                ),
                            contentAlignment = Alignment.Center
                        ) {}
                    }
                } else {
                    folders?.let { folders ->
                        if (folders.size > 0L) {
                            items(folders.size) { index ->
                                val folder = folders[index]

                                LargeFolderDisplay(
                                    context = context,
                                    activity = activity,
                                    folder = folder,
                                    serverUrl = serverUrl,
                                    onOpen = {
                                        mainFolder = folder
                                        foldersPath = foldersPath + folder
                                    },
                                    onMove = { moveFolder = folder },
                                    onExportZip = {
                                        folderToExport = folder

                                        if (selectedFolderExportUri == null) {
                                            downloadFolderType = DownloadFolderType.FOLDER

                                            directoryPicker.launch(null)

                                            return@LargeFolderDisplay
                                        }

                                        performFolderExport(folder)
                                    },
                                    onTogglePublic = {
                                        coroutineScope.launch { togglePublic(folder) }
                                    },
                                    onToggleAnonymousUploads = {
                                        coroutineScope.launch { toggleAnonymousUploads(folder) }
                                    },
                                    onEditName = { editNameFolder = folder },
                                    onDelete = { deleteFolder = folder },
                                )
                            }
                        } else {
                            item {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.folder),
                                            contentDescription = "Folder",
                                            modifier = Modifier.size(32.dp)
                                        )

                                        Text(
                                            text = "No folders found",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }

                                    Text(
                                        text = "Create a folder to see it here.",
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (mainFolder != null) {
            val filesLazyColumnListState = rememberLazyListState()

            var clickedFile by remember { mutableStateOf<File?>(null) }

            LaunchedEffect(
                currentPage,
                fileSortOrder,
                fileSortKey,
                fileSearchValue,
            ) {
                updateFiles()
                filesLazyColumnListState.animateScrollToItem(0)
                clickedFile = null
            }

            LaunchedEffect(folderFiles) {
                if (clickedFile != null) {
                    val updatedFile = folderFiles?.find { it.id == clickedFile?.id }

                    if (updatedFile != null) {
                        clickedFile = updatedFile
                    }
                }
            }

            LargeFileDisplay(
                context = context,
                activity = activity,
                file = clickedFile,
                updateData = ::updateFolders,
                onDismissRequest = { clickedFile = null },
                tags = tags,
            )

            if (viewState == ZiplineViewStateType.COMPACT) {
                var _searchValue by remember { mutableStateOf(fileSearchValue) }

                LaunchedEffect(fileSearchKey) {
                    _searchValue = TextFieldValue("")
                }

                if (fileSearchKey != null) {
                    Column(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Search by ${camelCaseToHumanReadable(fileSearchKey.toString())}",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                            )

                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable(
                                        onClick = {
                                            fileSearchKey = null
                                            fileSearchValue = TextFieldValue("")
                                        }
                                    )
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.close),
                                    contentDescription = "Close search",
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        if (fileSearchKey == GetFilesQuerySearchField.TAGS) {
                            var selectedTagIds by remember { mutableStateOf(emptySet<String>()) }

                            Select(
                                label = "Tags",
                                multiple = true,
                                enabled = !tagsLoading && tags?.isNotEmpty() ?: false,
                                options = tags?.map { tag ->
                                    SelectOption(
                                        id = tag.id,
                                        label = { enabled ->
                                            Tag(
                                                tag = tag,
                                                enabled = enabled
                                            )
                                        },
                                    )
                                } ?: emptyList(),
                                selectedIds = selectedTagIds,
                                onSelectionChange = { newSelectedIds ->
                                    if (newSelectedIds == selectedTagIds) return@Select

                                    selectedTagIds = newSelectedIds
                                    fileSearchValue = TextFieldValue(
                                        "," + newSelectedIds.joinToString(",")
                                    )
                                }
                            )
                        } else {
                            fun onEnter() {
                                fileSearchValue = _searchValue
                            }

                            val focusManager = LocalFocusManager.current

                            TextInput(
                                value = _searchValue,
                                onValueChange = { _searchValue = it },
                                label = "Search",
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        onEnter()
                                        focusManager.clearFocus()
                                    }
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onPreviewKeyEvent { keyEvent ->
                                        if (keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyUp) {
                                            onEnter()
                                            focusManager.clearFocus()

                                            true
                                        } else {
                                            false
                                        }
                                    }
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                        .weight(0.65f)
                        .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                        )
                ) {
                    val tableNameWidth = 300.dp
                    val tableTagsWidth = 200.dp
                    val tableTypeWidth = 250.dp
                    val tableSizeWidth = 150.dp
                    val tableCreatedAtWidth = 170.dp
                    val tableFavoriteWidth = 130.dp
                    val tableIdWidth = 300.dp
                    val tableActionsWidth = 220.dp

                    val headers: List<TableHeaderData> = listOf(
                        TableHeaderData(
                            content = {
                                Text(
                                    text = "Name",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            width = tableNameWidth,
                            name = "name",
                            searchable = true,
                            sortable = true,
                            sortOrder = if (fileSortKey == GetFilesQuerySortBy.NAME) fileSortOrder else defaultSortOrder,
                            onSortChanged = {
                                if (fileSortKey != GetFilesQuerySortBy.NAME) {
                                    fileSortKey = GetFilesQuerySortBy.NAME
                                    fileSortOrder = SortOrder.ASC
                                } else if (fileSortOrder == SortOrder.UNSPECIFIED || fileSortOrder == SortOrder.DESC) {
                                    fileSortOrder = SortOrder.ASC
                                } else {
                                    fileSortOrder = SortOrder.DESC
                                }
                            },
                            onSearchClick = {
                                fileSearchKey = GetFilesQuerySearchField.NAME
                            }
                        ),
                        TableHeaderData(
                            content = {
                                Text(
                                    text = "Tags",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            width = tableTagsWidth,
                            name = "tags",
                            searchable = true,
                            onSearchClick = {
                                fileSearchKey = GetFilesQuerySearchField.TAGS
                            },
                            searchEnabled = !tagsLoading && tags?.isNotEmpty() ?: false
                        ),
                        TableHeaderData(
                            content = {
                                Text(
                                    text = "Type",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            width = tableTypeWidth,
                            name = "type",
                            searchable = true,
                            sortable = true,
                            sortOrder = if (fileSortKey == GetFilesQuerySortBy.TYPE) fileSortOrder else defaultSortOrder,
                            onSortChanged = {
                                if (fileSortKey != GetFilesQuerySortBy.TYPE) {
                                    fileSortKey = GetFilesQuerySortBy.TYPE
                                    fileSortOrder = SortOrder.ASC
                                } else if (fileSortOrder == SortOrder.UNSPECIFIED || fileSortOrder == SortOrder.DESC) {
                                    fileSortOrder = SortOrder.ASC
                                } else {
                                    fileSortOrder = SortOrder.DESC
                                }
                            },
                            onSearchClick = {
                                fileSearchKey = GetFilesQuerySearchField.TYPE
                            }
                        ),
                        TableHeaderData(
                            content = {
                                Text(
                                    text = "Size",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            width = tableSizeWidth,
                            name = "size",
                            sortable = true,
                            sortOrder = if (fileSortKey == GetFilesQuerySortBy.SIZE) fileSortOrder else defaultSortOrder,
                            onSortChanged = {
                                if (fileSortKey != GetFilesQuerySortBy.SIZE) {
                                    fileSortKey = GetFilesQuerySortBy.SIZE
                                    fileSortOrder = SortOrder.ASC
                                } else if (fileSortOrder == SortOrder.UNSPECIFIED || fileSortOrder == SortOrder.DESC) {
                                    fileSortOrder = SortOrder.ASC
                                } else {
                                    fileSortOrder = SortOrder.DESC
                                }
                            }
                        ),
                        TableHeaderData(
                            content = {
                                Text(
                                    text = "Created At",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            width = tableCreatedAtWidth,
                            name = "created at",
                            sortable = true,
                            sortOrder = if (fileSortKey == GetFilesQuerySortBy.CREATED_AT) fileSortOrder else defaultSortOrder,
                            onSortChanged = {
                                if (fileSortKey != GetFilesQuerySortBy.CREATED_AT) {
                                    fileSortKey = GetFilesQuerySortBy.CREATED_AT
                                    fileSortOrder = SortOrder.ASC
                                } else if (fileSortOrder == SortOrder.UNSPECIFIED || fileSortOrder == SortOrder.DESC) {
                                    fileSortOrder = SortOrder.ASC
                                } else {
                                    fileSortOrder = SortOrder.DESC
                                }
                            }
                        ),
                        TableHeaderData(
                            content = {
                                Text(
                                    text = "Favorite",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            width = tableFavoriteWidth,
                            name = "favorite",
                            sortable = true,
                            sortOrder = if (fileSortKey == GetFilesQuerySortBy.FAVORITE) fileSortOrder else defaultSortOrder,
                            onSortChanged = {
                                if (fileSortKey != GetFilesQuerySortBy.FAVORITE) {
                                    fileSortKey = GetFilesQuerySortBy.FAVORITE
                                    fileSortOrder = SortOrder.ASC
                                } else if (fileSortOrder == SortOrder.UNSPECIFIED || fileSortOrder == SortOrder.DESC) {
                                    fileSortOrder = SortOrder.ASC
                                } else {
                                    fileSortOrder = SortOrder.DESC
                                }
                            }
                        ),
                        TableHeaderData(
                            content = {
                                Text(
                                    text = "ID",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            width = tableIdWidth,
                            name = "ID",
                            searchable = true,
                            sortable = true,
                            sortOrder = if (fileSortKey == GetFilesQuerySortBy.ID) fileSortOrder else defaultSortOrder,
                            onSortChanged = {
                                if (fileSortKey != GetFilesQuerySortBy.ID) {
                                    fileSortKey = GetFilesQuerySortBy.ID
                                    fileSortOrder = SortOrder.ASC
                                } else if (fileSortOrder == SortOrder.UNSPECIFIED || fileSortOrder == SortOrder.DESC) {
                                    fileSortOrder = SortOrder.ASC
                                } else {
                                    fileSortOrder = SortOrder.DESC
                                }
                            },
                            onSearchClick = {
                                fileSearchKey = GetFilesQuerySearchField.ID
                            }
                        ),
                        TableHeaderData(
                            content = {
                                Text(
                                    text = "Actions",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            width = tableActionsWidth,
                            name = "actions"
                        ),
                    )

                    var deleteFile by remember { mutableStateOf<File?>(null) }
                    val coroutineScope = rememberCoroutineScope()

                    PromptPopup(
                        showPopup = deleteFile != null,
                        onDismissRequest = { deleteFile = null },
                        onCancel = { deleteFile = null },
                        isLoading = isLoading,
                        title = "Are you sure?",
                        description = "Are you sure you want to delete ${deleteFile?.originalName ?: deleteFile?.name}? This action cannot be undone.",
                        onSuccess = {
                            coroutineScope.launch {
                                if (deleteFile == null) return@launch

                                isLoading = true

                                val deleteRes = deleteFile(
                                    context = context,
                                    fileId = deleteFile!!.id
                                )

                                deleteRes
                                    .onSuccess {
                                        updateFiles()
                                    }
                                    .onFailure {
                                        Notification.show(
                                            context = context,
                                            activity = activity,
                                            content = {
                                                Text(
                                                    text = "Failed to delete file: ${it.message}"
                                                )
                                            },
                                        )
                                    }

                                deleteFile = null

                                isLoading = false
                            }
                        }
                    )

                    val rows: List<TableRowData> = folderFiles?.map { file ->
                        TableRowData(
                            clickable = true,
                            onClick = { clickedFile = file },
                            cells = listOf(
                                TableCellData(
                                    content = {
                                        Text(
                                            text = file.name,
                                        )
                                    },
                                    width = tableNameWidth,
                                ),
                                TableCellData(
                                    content = {
                                        FlowRow(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            file.tags?.forEach { tag ->
                                                Tag(tag = tag)
                                            }
                                        }
                                    },
                                    width = tableTagsWidth,
                                ),
                                TableCellData(
                                    content = {
                                        Text(
                                            text = file.type,
                                        )
                                    },
                                    width = tableTypeWidth,
                                ),
                                TableCellData(
                                    content = {
                                        Text(
                                            text = formatBytes(file.size),
                                        )
                                    },
                                    width = tableSizeWidth,
                                ),
                                TableCellData(
                                    content = {
                                        Text(
                                            text = HumanReadable.timeAgo(Instant.parse(file.createdAt)),
                                        )
                                    },
                                    width = tableCreatedAtWidth,
                                ),
                                TableCellData(
                                    content = {
                                        Text(
                                            text = if (file.favorite) "Yes" else "No",
                                            color = if (file.favorite) Yellow else MaterialTheme.colorScheme.onBackground
                                        )
                                    },
                                    width = tableFavoriteWidth,
                                ),
                                TableCellData(
                                    content = {
                                        Text(
                                            text = file.id,
                                        )
                                    },
                                    width = tableIdWidth,
                                ),
                                TableCellData(
                                    content = {
                                        @Composable
                                        fun ActionButtonSpacer() {
                                            Spacer(modifier = Modifier.width(8.dp))
                                        }

                                        IconButton(
                                            icon = painterResource(R.drawable.draft),
                                            iconContentDescription = "More details",
                                            color = MaterialTheme.colorScheme.primary,
                                            iconColor = MaterialTheme.colorScheme.onPrimary,
                                            onClick = { clickedFile = file },
                                            enabled = !isLoading
                                        )

                                        ActionButtonSpacer()

                                        IconButton(
                                            icon = painterResource(R.drawable.open_new),
                                            iconContentDescription = "Open file in browser",
                                            color = MaterialTheme.colorScheme.primary,
                                            iconColor = MaterialTheme.colorScheme.onPrimary,
                                            onClick = {
                                                val fileUrl = "${serverUrl}${file.url}"

                                                val intent = Intent(Intent.ACTION_VIEW, fileUrl.toUri())
                                                context.startActivity(intent)
                                            },
                                            enabled = serverUrl != null && !isLoading
                                        )

                                        ActionButtonSpacer()

                                        val clipboardManager = LocalClipboard.current

                                        IconButton(
                                            icon = painterResource(R.drawable.content_copy),
                                            iconContentDescription = "Copy file link",
                                            color = MaterialTheme.colorScheme.primary,
                                            iconColor = MaterialTheme.colorScheme.onPrimary,
                                            onClick = {
                                                val fileUrl = "${serverUrl}${file.url}"

                                                val clipData = ClipData.newRawUri("File URL", fileUrl.toUri()).toClipEntry()

                                                coroutineScope.launch {
                                                    clipboardManager.setClipEntry(clipData)

                                                    Notification.show(
                                                        context = context,
                                                        activity = activity,
                                                        content = {
                                                            Text(
                                                                text = "File link copied to clipboard"
                                                            )
                                                        }
                                                    )
                                                }
                                            },
                                            enabled = serverUrl != null && !isLoading
                                        )

                                        ActionButtonSpacer()

                                        DownloadFilePasswordPrompt(
                                            context = context,
                                            activity = activity,
                                            fileId = file.id,
                                            showPopup = fileRequiresPassword && downloadFilePassword == null,
                                            onDismissRequest = {
                                                fileRequiresPassword = false
                                                downloadFilePassword = null
                                            },
                                            onDownload = {
                                                downloadFilePassword = it

                                                fileToDownload?.let { file ->
                                                    performFileDownload(file)
                                                }
                                            }
                                        )

                                        IconButton(
                                            icon = painterResource(R.drawable.download),
                                            iconContentDescription = "Download file",
                                            color = DarkGray,
                                            iconColor = White,
                                            onClick = {
                                                fileToDownload = file

                                                if (selectedFileDownloadUri == null) {
                                                    downloadFolderType = DownloadFolderType.FILE

                                                    directoryPicker.launch(null)

                                                    return@IconButton
                                                }

                                                performFileDownload(file)
                                            },
                                            enabled = serverUrl != null && !isLoading
                                        )

                                        ActionButtonSpacer()

                                        IconButton(
                                            icon = painterResource(R.drawable.delete),
                                            iconContentDescription = "Open file in browser",
                                            color = MaterialTheme.colorScheme.error,
                                            iconColor = MaterialTheme.colorScheme.onError,
                                            onClick = {
                                                deleteFile = file
                                            },
                                            enabled = serverUrl != null && !isLoading
                                        )
                                    },
                                    width = tableActionsWidth,
                                )
                            )
                        )
                    } ?: emptyList()

                    Table(
                        modifier = Modifier.fillMaxSize(),
                        headers = headers,
                        rows = rows,
                        loading = isLoading,
                        scrollbarConfig = TableScrollbarConfig(
                            vertical = ScrollbarConfig(
                                alwaysKeepScrollbar = true
                            ),
                            horizontal = ScrollbarConfig(
                                alwaysKeepScrollbar = true
                            )
                        )
                    )
                }
            } else {
                LazyColumn(
                    state = filesLazyColumnListState,
                    modifier = Modifier
                        .weight(0.65f)
                        .padding(vertical = 8.dp)
                        .verticalLazyScrollbar(
                            listState = filesLazyColumnListState,
                        )
                ) {
                    if (folderFiles == null || isLoading) {
                        items(5) {
                            Box(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                                    .fillMaxWidth()
                                    .height(250.dp)
                                    .shimmerable(
                                        enabled = true,
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        keepBackground = true
                                    ),
                                contentAlignment = Alignment.Center
                            ) {}
                        }
                    } else {
                        folderFiles?.let { files ->
                            if (files.size > 0L) {
                                items(files.size) { index ->
                                    val file = files[index]

                                    FilePreview(
                                        file = file,
                                        context = context,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(250.dp)
                                            .padding(5.dp),
                                        onClick = {
                                            clickedFile = file
                                        },
                                        serverUrl = serverUrl,
                                    )
                                }
                            } else {
                                item {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.file_copy_off),
                                                contentDescription = "User",
                                                modifier = Modifier.size(32.dp)
                                            )

                                            Text(
                                                text = "No files found",
                                                style = MaterialTheme.typography.titleLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                navController.navigate(UploadFileScreen())
                                            },
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.upload),
                                                contentDescription = "Upload file",
                                            )

                                            Spacer(
                                                modifier = Modifier.width(8.dp)
                                            )

                                            Text(
                                                text = "Upload a file",
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Pager(
                currentPage = currentPage,
                totalPages = totalPages,
                onFirstPageClick = { currentPage = 1 },
                onPreviousPageClick = { currentPage-- },
                onCustomPageInput = { newPage ->
                    currentPage = newPage
                },
                onNextPageClick = { currentPage++ },
                onLastPageClick = { currentPage = totalPages },
                enabled = !isLoading && totalPages > 1
            )
        }
    }
}

private enum class GetFoldersQuerySortBy(val value: String) {
    @SerializedName("name")
    NAME("name"),

    @SerializedName("public")
    PUBLIC("public"),

    @SerializedName("uploads")
    UPLOADS("uploads"),

    @SerializedName("createdAt")
    CREATED_AT("createdAt"),

    @SerializedName("updatedAt")
    UPDATED_AT("updatedAt");

    override fun toString(): String = value
}

private enum class DownloadFolderType {
    FOLDER,
    FILE,
}