package com.stefdp.zipline.screens.folders

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.SerializedName
import com.stefdp.zipline.Logger
import com.stefdp.zipline.network.models.BaseFolder
import com.stefdp.zipline.network.models.File
import com.stefdp.zipline.network.models.Tag
import com.stefdp.zipline.network.models.requests.DeleteFolderChildrenAction
import com.stefdp.zipline.network.models.requests.GetFilesQuerySearchField
import com.stefdp.zipline.network.models.requests.GetFilesQuerySortBy
import com.stefdp.zipline.network.requests.downloadFile
import com.stefdp.zipline.network.requests.exportFolder
import com.stefdp.zipline.network.requests.deleteFile as apiDeleteFile
import com.stefdp.zipline.network.requests.deleteFolder as apiDeleteFolder
import com.stefdp.zipline.network.requests.createFolder as apiCreateFolder
import com.stefdp.zipline.network.requests.moveFolder as apiMoveFolder
import com.stefdp.zipline.network.requests.getFiles
import com.stefdp.zipline.network.requests.getFolderExportSize
import com.stefdp.zipline.network.requests.getFolders
import com.stefdp.zipline.network.requests.getTags
import com.stefdp.zipline.network.requests.updateFolder
import com.stefdp.zipline.utils.STORAGE_DEFAULT_DOMAIN_KEY
import com.stefdp.zipline.utils.STORAGE_FILE_DOWNLOAD_FOLDER_KEY
import com.stefdp.zipline.utils.STORAGE_FOLDER_EXPORT_DOWNLOAD_FOLDER_KEY
import com.stefdp.zipline.utils.STORAGE_SERVER_URL_KEY
import com.stefdp.zipline.utils.SecureStorage
import com.stefdp.zipline.utils.SortOrder
import com.stefdp.zipline.utils.StorageUtil
import com.stefdp.zipline.utils.ZiplineViewStateType
import com.stefdp.zipline.utils.getDisplayPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant

enum class GetFoldersQuerySortBy(val value: String) {
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

enum class DownloadFolderType {
    FOLDER,
    FILE,
}

data class FoldersUiState(
    val isLoading: Boolean = false,
    val mainFolder: BaseFolder? = null,
    val folders: List<BaseFolder>? = null,
    val allFolders: List<BaseFolder> = emptyList(),
    val foldersPath: List<BaseFolder> = emptyList(),
    val folderFiles: List<File>? = null,
    val tags: List<Tag>? = null,
    val serverUrl: String? = null,
    val defaultDomain: String? = null,
    val tagsLoading: Boolean = false,
    val currentPage: Long = 1,
    val totalPages: Long = 1,
    val folderSortKey: GetFoldersQuerySortBy = GetFoldersQuerySortBy.CREATED_AT,
    val folderSortOrder: SortOrder = SortOrder.DESC,
    val fileSortKey: GetFilesQuerySortBy = GetFilesQuerySortBy.CREATED_AT,
    val fileSortOrder: SortOrder = SortOrder.DESC,
    val fileSearchKey: GetFilesQuerySearchField? = null,
    val fileSearchValue: TextFieldValue = TextFieldValue(""),
    val createNewFolderPopupOpen: Boolean = false,
    val selectedFolderExportUri: Uri? = null,
    val selectedFolderExportPath: String? = null,
    val selectedFileDownloadUri: Uri? = null,
    val selectedFileDownloadPath: String? = null,
    val downloadFolderType: DownloadFolderType = DownloadFolderType.FOLDER,
    val downloadFilePassword: String? = null,
    val fileRequiresPassword: Boolean = false,
    val fileToDownload: File? = null,
    val folderToExport: BaseFolder? = null,
    var deleteFolder: BaseFolder? = null,
    val editNameFolder: BaseFolder? = null,
    val moveFolder: BaseFolder? = null,
    val clickedFile: File? = null,
    val selectedTagIds: Set<String> = emptySet(),
    val deleteFile: File? = null,
    val selectedDeleteFolderType: Set<String> = setOf(DeleteFolderChildrenAction.MOVE_TO_ROOT.toString()),
    val selectedDeleteNewFolder: Set<String> = setOf("none"),
    val createFolderName: TextFieldValue = TextFieldValue(""),
    val createFolderIsPublic: Boolean = false,
    val editNameFolderNewName: TextFieldValue = TextFieldValue(""),
    val moveFolderDestination: Set<String> = setOf("default"),
)

class FoldersViewModel : ViewModel() {
    private val _state = MutableStateFlow(FoldersUiState())
    val state: StateFlow<FoldersUiState> = _state.asStateFlow()

    fun initData(context: Context) {
        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)

            val serverUrl = secureStore.get(STORAGE_SERVER_URL_KEY)
            val defaultDomain = secureStore.get(STORAGE_DEFAULT_DOMAIN_KEY)

            val fileDownloadFolder = secureStore.get(STORAGE_FILE_DOWNLOAD_FOLDER_KEY)
            val folderExportFolder = secureStore.get(STORAGE_FOLDER_EXPORT_DOWNLOAD_FOLDER_KEY)

            val fileDownloadFolderUri = fileDownloadFolder?.toUri()
            val folderExportFolderUri = folderExportFolder?.toUri()

            _state.update {
                it.copy(
                    serverUrl = serverUrl,
                    defaultDomain = defaultDomain,
                    selectedFileDownloadUri = fileDownloadFolderUri,
                    selectedFolderExportUri = folderExportFolderUri,
                    selectedFileDownloadPath = fileDownloadFolderUri?.let { uri -> getDisplayPath(uri) },
                    selectedFolderExportPath = folderExportFolderUri?.let { uri -> getDisplayPath(uri) },
                )
            }
        }
    }

    fun updateFoldersSort(key: GetFoldersQuerySortBy, order: SortOrder) {
        _state.update {
            it.copy(
                folderSortKey = key,
                folderSortOrder = order
            )
        }
    }

    fun openCreateNewFolderPopup() {
        _state.update {
            it.copy(createNewFolderPopupOpen = true)
        }
    }

    fun closeCreateNewFolderPopup() {
        _state.update {
            it.copy(
                createNewFolderPopupOpen = false,
                createFolderName = TextFieldValue(""),
                createFolderIsPublic = false
            )
        }
    }

    fun setMainFolder(folder: BaseFolder?) {
        _state.update {
            it.copy(mainFolder = folder, currentPage = 1)
        }
    }

    fun setFolderPath(path: List<BaseFolder>) {
        _state.update {
            it.copy(
                foldersPath = path
            )
        }
    }

    fun setEditNameFolder(folder: BaseFolder?) {
        _state.update {
            it.copy(editNameFolder = folder)
        }
    }

    fun setMoveFolderDestination(destination: Set<String>) {
        _state.update {
            it.copy(moveFolderDestination = destination)
        }
    }

    fun setMoveFolder(folder: BaseFolder?) {
        _state.update {
            it.copy(moveFolder = folder)
        }
    }

    fun setDeleteFolder(folder: BaseFolder?) {
        _state.update {
            it.copy(deleteFolder = folder)
        }
    }

    fun setFolderToExport(folder: BaseFolder?) {
        _state.update {
            it.copy(folderToExport = folder)
        }
    }

    fun setDownloadFolderType(type: DownloadFolderType) {
        _state.update {
            it.copy(downloadFolderType = type)
        }
    }

    fun setSelectedDeleteFolderType(type: Set<String>) {
        _state.update {
            it.copy(selectedDeleteFolderType = type)
        }
    }

    fun setSelectedDeleteNewFolder(folderId: Set<String>) {
        _state.update {
            it.copy(selectedDeleteNewFolder = folderId)
        }
    }

    fun setCreateFolderName(name: TextFieldValue) {
        _state.update {
            it.copy(createFolderName = name)
        }
    }

    fun setCreateFolderIsPublic(isPublic: Boolean) {
        _state.update {
            it.copy(createFolderIsPublic = isPublic)
        }
    }

    fun setEditNameFolderNewName(name: TextFieldValue) {
        _state.update {
            it.copy(editNameFolderNewName = name)
        }
    }

    fun setClickedFile(file: File?) {
        _state.update {
            it.copy(clickedFile = file)
        }
    }

    fun updateFileSearchKey(key: GetFilesQuerySearchField?) {
        _state.update {
            it.copy(
                fileSearchKey = key,
                fileSearchValue = TextFieldValue("")
            )
        }
    }

    fun updateFileSearchValue(value: TextFieldValue) {
        _state.update {
            it.copy(fileSearchValue = value)
        }
    }

    fun setSelectedTagIds(tagIds: Set<String>) {
        _state.update {
            it.copy(
                selectedTagIds = tagIds,
                fileSearchValue = TextFieldValue(
                    "," + tagIds.joinToString(",")
                )
            )
        }
    }

    fun updateFilesSort(key: GetFilesQuerySortBy, order: SortOrder) {
        _state.update {
            it.copy(
                fileSortKey = key,
                fileSortOrder = order
            )
        }
    }

    fun setDeleteFile(file: File?) {
        _state.update {
            it.copy(deleteFile = file)
        }
    }

    fun setFileRequiresPassword(requires: Boolean) {
        _state.update {
            it.copy(fileRequiresPassword = requires)
        }
    }

    fun setDownloadFilePassword(password: String?) {
        _state.update {
            it.copy(downloadFilePassword = password)
        }
    }

    fun setFileToDownload(file: File?) {
        _state.update {
            it.copy(fileToDownload = file)
        }
    }

    fun setCurrentPage(page: Long) {
        _state.update {
            it.copy(currentPage = page)
        }
    }

    fun setDownloadFolderExportUri(context: Context, uri: Uri?) {
        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)

            _state.update {
                it.copy(
                    selectedFolderExportUri = uri,
                    selectedFolderExportPath = uri?.let { folderExportUri -> getDisplayPath(folderExportUri) }
                )
            }

            uri?.let { folderExportUri ->
                secureStore.set(STORAGE_FOLDER_EXPORT_DOWNLOAD_FOLDER_KEY, folderExportUri.toString())
            }
        }
    }

    fun setDownloadFileUri(context: Context, uri: Uri?) {
        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)

            _state.update {
                it.copy(
                    selectedFileDownloadUri = uri,
                    selectedFileDownloadPath = uri?.let { fileDownloadUri -> getDisplayPath(fileDownloadUri) }
                )
            }

            uri?.let { fileDownloadUri ->
                secureStore.set(STORAGE_FILE_DOWNLOAD_FOLDER_KEY, fileDownloadUri.toString())
            }
        }
    }

    fun deleteFile(
        context: Context,
        filesPerPage: Long,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            if (_state.value.deleteFile == null) return@launch

            _state.update {
                it.copy(isLoading = true)
            }

            val deleteRes = apiDeleteFile(
                context = context,
                fileId = _state.value.deleteFile!!.id
            )

            deleteRes
                .onSuccess {
                    refreshFiles(context, filesPerPage)

                    _state.update {
                        it.copy(deleteFile = null)
                    }

                    onSuccess()
                }
                .onFailure {
                    onError("Failed to delete file: ${it.message}")
                }
        }
    }

    fun deleteFolder(
        context: Context,
        viewState: ZiplineViewStateType,
        filesPerPage: Long,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            if (_state.value.deleteFolder == null) return@launch

            _state.update {
                it.copy(isLoading = true)
            }

            val deleteRes = apiDeleteFolder(
                context = context,
                folderId = _state.value.deleteFolder!!.id,
                childrenAction = DeleteFolderChildrenAction.valueOf(_state.value.selectedDeleteFolderType.first()),
                targetFolderId = _state.value.selectedDeleteNewFolder.firstOrNull()
            )

            deleteRes
                .onSuccess {
                    refreshFolders(context, viewState)
                    refreshFiles(context, filesPerPage)
                    refreshAllFolders(context)

                    onSuccess()

                    _state.update {
                        it.copy(
                            deleteFolder = null,
                            isLoading = false
                        )
                    }
                }
                .onFailure {
                    onError("Failed to delete folder: ${it.message}")

                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
        }
    }

    fun createFolder(
        context: Context,
        viewState: ZiplineViewStateType,
        onError: (String) -> Unit,
        onSuccess: (BaseFolder) -> Unit
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val createFolderRes = apiCreateFolder(
                context = context,
                parentId = _state.value.mainFolder?.id,
                name = _state.value.createFolderName.text,
                isPublic = _state.value.createFolderIsPublic
            )

            createFolderRes
                .onSuccess { folder ->
                    refreshFolders(context, viewState)
                    refreshAllFolders(context)

                    onSuccess(folder)

                    _state.update {
                        it.copy(
                            createFolderName = TextFieldValue(""),
                            createFolderIsPublic = false,
                            createNewFolderPopupOpen = false,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    onError("Failed to create folder: ${error.message}")

                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
        }
    }

    fun editFolderName(
        context: Context,
        viewState: ZiplineViewStateType,
        onError: (String) -> Unit,
        onSuccess: (String, String) -> Unit
    ) {
        viewModelScope.launch {
            if (_state.value.editNameFolder == null) return@launch

            _state.update {
                it.copy(isLoading = true)
            }

            val updateFolderRes = updateFolder(
                context = context,
                folderId = _state.value.editNameFolder!!.id,
                name = _state.value.editNameFolderNewName.text
            )

            updateFolderRes
                .onSuccess { folder ->
                    refreshFolders(context, viewState)

                    val oldFolderName = _state.value.editNameFolder!!.name
                    val newFolderName = folder.name

                    onSuccess(oldFolderName, newFolderName)

                    _state.update {
                        it.copy(
                            editNameFolder = null,
                            editNameFolderNewName = TextFieldValue(""),
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    onError("Failed to rename folder: ${error.message}")

                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
        }
    }

    fun moveFolder(
        context: Context,
        viewState: ZiplineViewStateType,
        onError: (String) -> Unit,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (_state.value.moveFolder == null) return@launch

            _state.update {
                it.copy(isLoading = true)
            }

            val newParentId = _state.value.moveFolderDestination.firstOrNull()

            val moveFolderRes = apiMoveFolder(
                context = context,
                folderId = _state.value.moveFolder!!.id,
                newParentId = if (newParentId == "default") null else newParentId
            )

            moveFolderRes
                .onSuccess {
                    refreshFolders(context, viewState)

                    onSuccess(_state.value.moveFolder!!.name)

                    _state.update {
                        it.copy(
                            moveFolder = null,
                            moveFolderDestination = setOf("default"),
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    onError("Failed to move folder: ${error.message}")

                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
        }
    }

    fun refreshFolders(context: Context, viewState: ZiplineViewStateType) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    folders = null
                )
            }

            val foldersRes = getFolders(
                context = context,
                parent = _state.value.mainFolder?.id,
                root = _state.value.mainFolder == null,
                excludeFiles = true,
            )

            foldersRes
                .onSuccess { folders ->
                    val newFolders = if (viewState == ZiplineViewStateType.COMPACT) sortFoldersList(folders, viewState) else folders

                    _state.update {
                        it.copy(
                            folders = newFolders,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun refreshAllFolders(context: Context) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val allFoldersRes = getFolders(
                context = context,
                excludeFiles = true,
            )

            allFoldersRes
                .onSuccess { allFolders ->
                    _state.update {
                        it.copy(
                            allFolders = allFolders,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun refreshFiles(context: Context, filesPerPage: Long) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    folderFiles = null
                )
            }

            if (_state.value.mainFolder == null) {
                _state.update {
                    it.copy(
                        folderFiles = null,
                        isLoading = false,
                        totalPages = 1,
                        currentPage = 1
                    )
                }
            } else {
                val filesRes = getFiles(
                    context = context,
                    folderId = _state.value.mainFolder!!.id,
                    perPage = filesPerPage,
                    page = _state.value.currentPage,
                    sortOrder = _state.value.fileSortOrder,
                    sortBy = _state.value.fileSortKey,
                    searchField = _state.value.fileSearchKey,
                    searchQuery = _state.value.fileSearchValue.text.ifEmpty { null }
                )

                filesRes
                    .onSuccess { files ->
                        val folderFiles = files.page
                        val totalPages = files.pages ?: 1L

                        _state.update {
                            it.copy(
                                folderFiles = folderFiles,
                                isLoading = false,
                                totalPages = totalPages
                            )
                        }
                    }
            }
        }
    }

    fun refreshTags(context: Context) {
        viewModelScope.launch {
            _state.update {
                it.copy(tagsLoading = true)
            }

            val tagsRes = getTags(context)

            tagsRes
                .onSuccess { tags ->
                    _state.update {
                        it.copy(
                            tags = tags,
                            tagsLoading = false
                        )
                    }
                }
        }
    }

    fun toggleFolderPublic(
        context: Context,
        folder: BaseFolder,
        viewState: ZiplineViewStateType,
        onError: (String) -> Unit,
        onSuccess: (BaseFolder) -> Unit
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val updateRes = updateFolder(
                context = context,
                folderId = folder.id,
                isPublic = !folder.public
            )

            updateRes
                .onSuccess { folder ->
                    refreshFolders(context, viewState)

                    onSuccess(folder)
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(isLoading = false)
                    }

                    onError("Failed to update folder: ${error.message}")
                }
        }
    }

    fun toggleFolderAnonymousUploads(
        context: Context,
        folder: BaseFolder,
        viewState: ZiplineViewStateType,
        onError: (String) -> Unit,
        onSuccess: (BaseFolder) -> Unit
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val updateRes = updateFolder(
                context = context,
                folderId = folder.id,
                allowUploads = !folder.allowUploads
            )

            updateRes
                .onSuccess { folder ->
                    refreshFolders(context, viewState)

                    onSuccess(folder)
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(isLoading = false)
                    }

                    onError("Failed to update folder: ${error.message}")
                }
        }
    }

    fun triggerFolderSort(viewState: ZiplineViewStateType) {
        val currentFolders = _state.value.folders ?: return

        _state.update {
            it.copy(folders = sortFoldersList(currentFolders, viewState))
        }
    }

    private fun sortFoldersList(
        folders: List<BaseFolder>,
        viewState: ZiplineViewStateType
    ): List<BaseFolder> {
        if (viewState == ZiplineViewStateType.LARGE) return folders.sortedBy { Instant.parse(it.createdAt) }.reversed()

        val sortKey = _state.value.folderSortKey
        val sortOrder = _state.value.folderSortOrder

        val ascending = when (sortKey) {
            GetFoldersQuerySortBy.NAME -> folders.sortedBy { it.name }
            GetFoldersQuerySortBy.PUBLIC -> folders.sortedBy { it.public }
            GetFoldersQuerySortBy.UPLOADS -> folders.sortedBy { it.allowUploads }
            GetFoldersQuerySortBy.CREATED_AT -> folders.sortedBy { Instant.parse(it.createdAt) }
            GetFoldersQuerySortBy.UPDATED_AT -> folders.sortedBy { Instant.parse(it.updatedAt) }
        }

        return when (sortOrder) {
            SortOrder.ASC -> ascending
            SortOrder.DESC -> ascending.reversed()
            SortOrder.UNSPECIFIED -> folders
        }
    }

    fun performFileDownload(
        context: Context,
        file: File,
        fileDownloadUri: Uri,
        sendNotification: (content: @Composable () -> Unit) -> Unit,
    ) {
        viewModelScope.launch {
            val fileFits = withContext(Dispatchers.IO) {
                StorageUtil.canFitFile(
                    context = context,
                    uri = fileDownloadUri,
                    fileSize = file.size
                )
            }

            if (!fileFits) {
                sendNotification {
                    Text(
                        text = "Not enough space in the selected directory to download the file",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                return@launch
            }

            val fileFitsCache = withContext(Dispatchers.IO) {
                StorageUtil.canFitInternalCache(
                    context = context,
                    fileSize = file.size
                )
            }

            if (!fileFitsCache) {
                sendNotification {
                    Text(
                        text = "Not enough space in the internal cache to download the file",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                return@launch
            }

            if (file.password == true && _state.value.downloadFilePassword.isNullOrBlank()) {
                setFileRequiresPassword(true)

                return@launch
            }

            sendNotification {
                Text("Starting download...")
            }

            val fileName = file.originalName ?: file.name

            val tempFile = java.io.File(context.cacheDir, fileName)
            val tempDestinationPath = tempFile.absolutePath

            withContext(Dispatchers.IO) {
                if (tempFile.exists()) tempFile.delete()

                val downloadRes = downloadFile(
                    context = context,
                    fileId = file.id,
                    destinationPath = tempDestinationPath,
                    notificationTitle = "Downloading file",
                    notificationContent = "Downloading ${file.name}",
                    password = _state.value.downloadFilePassword
                )

                downloadRes
                    .onSuccess {
                        try {
                            val docUri =
                                DocumentsContract.buildDocumentUriUsingTree(
                                    fileDownloadUri,
                                    DocumentsContract.getTreeDocumentId(
                                        fileDownloadUri
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

                                sendNotification {
                                    Text("File downloaded to ${_state.value.selectedFileDownloadPath}/$fileName")
                                }
                            } else {
                                sendNotification {
                                    Text(
                                        text = "Failed to create file in selected directory",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            Logger.error(
                                "FoldersScreen",
                                "Failed to copy file to selected directory",
                                e
                            )

                            sendNotification {
                                Text(
                                    text = "Failed to copy file to selected directory: ${e.message}",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        } finally {
                            tempFile.delete()
                        }
                    }
                    .onFailure {
                        Logger.error("FoldersScreen", "Failed to download file", it)

                        sendNotification {
                            Text(
                                text = "Failed to download file: ${it.message}",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
            }

            _state.update {
                it.copy(
                    fileRequiresPassword = false,
                    downloadFilePassword = null,
                    fileToDownload = null
                )
            }
        }
    }

    fun performFolderExport(
        context: Context,
        folder: BaseFolder,
        folderExportUri: Uri,
        sendNotification: (content: @Composable () -> Unit) -> Unit,
    ) {
        viewModelScope.launch {
            val folderExportSize = getFolderExportSize(
                context = context,
                folderId = folder.id
            )

            folderExportSize
                .onSuccess {
                    val fileFits = withContext(Dispatchers.IO) {
                        StorageUtil.canFitFile(
                            context = context,
                            uri = folderExportUri,
                            fileSize = it
                        )
                    }

                    if (!fileFits) {
                        sendNotification {
                            Text(
                                text = "Not enough space in the selected directory to download the folder",
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        return@launch
                    }

                    val fileFitsCache = withContext(Dispatchers.IO) {
                        StorageUtil.canFitInternalCache(
                            context = context,
                            fileSize = it
                        )
                    }

                    if (!fileFitsCache) {
                        sendNotification {
                            Text(
                                text = "Not enough space in the internal cache to download the folder",
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        return@launch
                    }
                }
                .onFailure {
                    sendNotification {
                        Text(
                            text = "Failed to get folder export size: ${it.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

            sendNotification {
                Text("Starting download...")
            }

            val fileName = folder.name

            val tempFile = java.io.File(context.cacheDir, fileName)
            val tempDestinationPath = tempFile.absolutePath

            withContext(Dispatchers.IO) {
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
                                    folderExportUri,
                                    DocumentsContract.getTreeDocumentId(
                                        folderExportUri
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

                                sendNotification {
                                    Text("Folder export downloaded to ${_state.value.selectedFolderExportPath}/$fileName.zip")
                                }
                            } else {
                                sendNotification {
                                    Text(
                                        text = "Failed to create folder export in selected directory",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            Logger.error(
                                "FoldersScreen",
                                "Failed to copy folder export to selected directory",
                                e
                            )

                            sendNotification {
                                Text(
                                    text = "Failed to copy folder export to selected directory: ${e.message}",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        } finally {
                            tempFile.delete()
                        }
                    }
                    .onFailure {
                        Logger.error("FoldersScreen", "Failed to download folder export", it)

                        sendNotification {
                            Text(
                                text = "Failed to download folder export: ${it.message}",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
            }

            _state.update {
                it.copy(
                    folderToExport = null
                )
            }
        }
    }
}