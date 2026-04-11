package com.stefdp.zipline.screens.files

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.zipline.Logger
import com.stefdp.zipline.network.models.File
import com.stefdp.zipline.network.models.IncompleteFile
import com.stefdp.zipline.network.models.Tag
import com.stefdp.zipline.network.models.UserRole
import com.stefdp.zipline.network.models.requests.GetFilesQuerySearchField
import com.stefdp.zipline.network.models.requests.GetFilesQuerySortBy
import com.stefdp.zipline.network.requests.downloadFile
import com.stefdp.zipline.network.requests.getFiles
import com.stefdp.zipline.network.requests.getIncompleteFiles
import com.stefdp.zipline.network.requests.getTags
import com.stefdp.zipline.network.requests.getUser
import com.stefdp.zipline.utils.STORAGE_FILE_DOWNLOAD_FOLDER_KEY
import com.stefdp.zipline.network.requests.deleteIncompleteFiles as apiDeleteIncompleteFiles
import com.stefdp.zipline.network.requests.deleteFile as apiDeleteFile
import com.stefdp.zipline.network.requests.deleteTag as apiDeleteTag
import com.stefdp.zipline.network.requests.updateTag
import com.stefdp.zipline.network.requests.createTag as apiCreateTag
import com.stefdp.zipline.utils.STORAGE_SERVER_URL_KEY
import com.stefdp.zipline.utils.SecureStorage
import com.stefdp.zipline.utils.SortOrder
import com.stefdp.zipline.utils.StorageUtil
import com.stefdp.zipline.utils.canInteract
import com.stefdp.zipline.utils.getDisplayPath
import com.stefdp.zipline.utils.toHex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class FilesUiState(
    val isLoading: Boolean = false,
    val username: String? = null,
    val files: List<File>? = null,
    val incompleteFiles: List<IncompleteFile>? = null,
    val tags: List<Tag>? = null,
    val serverUrl: String? = null,
    val tagsLoading: Boolean = false,
    val manageTagsPopupOpen: Boolean = false,
    val showCreateTagPopup: Boolean = false,
    val tagToEdit: Tag? = null,
    val pendingFilesPopupOpen: Boolean = false,
    val favoriteFilter: Boolean = false,
    val currentPage: Long = 1L,
    val totalPages: Long = 1L,
    val sortKey: GetFilesQuerySortBy = GetFilesQuerySortBy.CREATED_AT,
    val sortOrder: SortOrder = SortOrder.DESC,
    val searchKey: GetFilesQuerySearchField? = null,
    val searchValue: TextFieldValue = TextFieldValue(""),
    val clickedFile: File? = null,
    val selectedTagIds: Set<String> = emptySet(),
    val deleteFile: File? = null,
    val selectedUri: Uri? = null,
    val selectedPath: String? = null,
    val downloadFilePassword: String? = null,
    val fileRequiresPassword: Boolean = false,
    val pendingFilesLoading: Boolean = false,
    val createTagName: TextFieldValue = TextFieldValue(""),
    val createTagColor: Color = Color.Black,
    val editTagName: TextFieldValue = TextFieldValue(""),
    val editTagColor: Color = Color.Black,
)

class FilesViewModel : ViewModel() {
    private val _state = MutableStateFlow(FilesUiState())
    val state: StateFlow<FilesUiState> = _state.asStateFlow()

    fun initData(context: Context) {
        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)

            val fileDownloadFolder = secureStore.get(STORAGE_FILE_DOWNLOAD_FOLDER_KEY)
            val serverUrl = secureStore.get(STORAGE_SERVER_URL_KEY)

            val fileDownloadFolderUri = fileDownloadFolder?.toUri()

            _state.update {
                it.copy(
                    serverUrl = serverUrl,
                    selectedUri = fileDownloadFolderUri,
                    selectedPath = fileDownloadFolderUri?.let { uri -> getDisplayPath(uri) }
                )
            }
        }
    }

    fun openManageTagsPopup() {
        _state.update {
            it.copy(manageTagsPopupOpen = true)
        }
    }

    fun closeManageTagsPopup() {
        _state.update {
            it.copy(manageTagsPopupOpen = false)
        }
    }

    fun openCreateTagPopup() {
        _state.update {
            it.copy(showCreateTagPopup = true)
        }
    }

    fun closeCreateTagPopup() {
        _state.update {
            it.copy(showCreateTagPopup = false)
        }
    }

    fun openPendingFilesPopup() {
        _state.update {
            it.copy(pendingFilesPopupOpen = true)
        }
    }

    fun closePendingFilesPopup() {
        _state.update {
            it.copy(pendingFilesPopupOpen = false)
        }
    }

    fun toggleFavoriteFilter() {
        _state.update {
            it.copy(favoriteFilter = !it.favoriteFilter)
        }
    }

    fun setClickedFile(file: File?) {
        _state.update {
            it.copy(clickedFile = file)
        }
    }

    fun setDeleteFile(file: File?) {
        _state.update {
            it.copy(deleteFile = file)
        }
    }

    fun setTagToEdit(tag: Tag?) {
        val color = tag?.color?.toColorInt()

        _state.update {
            it.copy(
                tagToEdit = tag,
                editTagName = TextFieldValue(tag?.name ?: ""),
                editTagColor = if (color != null) Color(color) else Color.Black
            )
        }
    }

    fun updateSearchKey(key: GetFilesQuerySearchField?) {
        _state.update {
            it.copy(
                searchKey = key,
                searchValue = TextFieldValue("")
            )
        }
    }

    fun updateSearchValue(value: TextFieldValue) {
        _state.update {
            it.copy(searchValue = value)
        }
    }

    fun updateSort(key: GetFilesQuerySortBy, order: SortOrder) {
        _state.update {
            it.copy(
                sortKey = key,
                sortOrder = order
            )
        }
    }

    fun setSelectedTagIds(tagIds: Set<String>) {
        _state.update {
            it.copy(
                selectedTagIds = tagIds,
                searchValue = TextFieldValue(
                    "," + tagIds.joinToString(",")
                )
            )
        }
    }

    fun setCurrentPage(page: Long) {
        _state.update {
            it.copy(currentPage = page)
        }
    }

    fun setSelectedUri(
        context: Context,
        uri: Uri
    ) {
        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)

            secureStore.set(STORAGE_FILE_DOWNLOAD_FOLDER_KEY, uri.toString())

            _state.update {
                it.copy(
                    selectedUri = uri,
                    selectedPath = getDisplayPath(uri)
                )
            }
        }
    }

    fun setDownloadFilePassword(password: String?) {
        _state.update {
            it.copy(downloadFilePassword = password)
        }
    }

    fun setFileRequiresPassword(requiresPassword: Boolean) {
        _state.update {
            it.copy(
                fileRequiresPassword = requiresPassword,
                downloadFilePassword = null
            )
        }
    }

    fun setCreateTagName(name: TextFieldValue) {
        _state.update {
            it.copy(createTagName = name)
        }
    }

    fun setCreateTagColor(color: Color) {
        _state.update {
            it.copy(createTagColor = color)
        }
    }

    fun setEditTagName(name: TextFieldValue) {
        _state.update {
            it.copy(editTagName = name)
        }
    }

    fun setEditTagColor(color: Color) {
        _state.update {
            it.copy(editTagColor = color)
        }
    }

    fun deleteFile(
        context: Context,
        filesPerPage: Long,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
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
                .onSuccess { _ ->
                    refreshFiles(context, filesPerPage)
                    onSuccess()

                    _state.update {
                        it.copy(
                            deleteFile = null,
                            isLoading = false,
                        )
                    }
                }
                .onFailure { error ->
                    onError("Failed to delete file: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                }
        }
    }

    fun deleteTag(
        context: Context,
        tagId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(tagsLoading = true)
            }

            val deleteTagRes = apiDeleteTag(
                context = context,
                tagId = tagId
            )

            val deleteStatus = deleteTagRes.getOrNull()

            if (deleteStatus == true) {
                refreshTags(context)

                onSuccess()

                _state.update {
                    it.copy(
                        tagsLoading = false,
                        tagToEdit = null,
                    )
                }
            } else {
                onError("Failed to delete tag")

                _state.update {
                    it.copy(tagsLoading = false)
                }
            }
        }
    }

    fun editTag(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (_state.value.tagToEdit == null) return@launch

            _state.update {
                it.copy(tagsLoading = true)
            }

            val editTagRes = updateTag(
                context = context,
                tagId = _state.value.tagToEdit!!.id,
                name = _state.value.editTagName.text.takeIf { name -> name != _state.value.tagToEdit?.name },
                color = _state.value.editTagColor.toHex()
            )

            editTagRes
                .onSuccess { _ ->
                    refreshTags(context)

                    onSuccess()

                    _state.update {
                        it.copy(
                            tagsLoading = false,
                            tagToEdit = null
                        )
                    }
                }
                .onFailure { error ->
                    Logger.error("CreateTagPopup", "Failed to create tag", error)

                    onError("Failed to edit tag")

                    _state.update {
                        it.copy(tagsLoading = false)
                    }
                }
        }
    }

    fun createTag(
        context: Context,
        name: String,
        color: Color,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(tagsLoading = true)
            }

            val createTagRes = apiCreateTag(
                context = context,
                name = name,
                color = color.toHex()
            )

            createTagRes
                .onSuccess { _ ->
                    refreshTags(context)

                    onSuccess()

                    _state.update {
                        it.copy(
                            tagsLoading = false,
                            showCreateTagPopup = false,
                            createTagName = TextFieldValue(""),
                            createTagColor = Color.Black,
                        )
                    }
                }
                .onFailure { error ->
                    Logger.error("CreateTagPopup", "Failed to create tag", error)

                    onError("Failed to create tag")

                    _state.update {
                        it.copy(tagsLoading = false)
                    }
                }
        }
    }

    fun deleteIncompleteFiles(
        context: Context,
        ids: List<String>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(pendingFilesLoading = true)
            }

            val deleteIncompleteFileRes = apiDeleteIncompleteFiles(
                context = context,
                ids = ids
            )

            deleteIncompleteFileRes
                .onSuccess {
                    refreshIncompleteFiles(context)

                    onSuccess()

                    _state.update {
                        it.copy(pendingFilesLoading = false)
                    }
                }
                .onFailure {
                    onError("Failed to delete pending file")

                    _state.update {
                        it.copy(pendingFilesLoading = false)
                    }
                }
        }
    }

    fun refreshFiles(
        context: Context,
        filesPerPage: Long,
        userId: String? = null,
        currentUserRole: UserRole? = null,
        onUnauthorized: () -> Unit = {},
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            if (userId != null && currentUserRole in validRoles) {
                val userRes = getUser(
                    context = context,
                    userId = userId,
                )

                userRes.onSuccess {
                    if (!canInteract(currentUserRole, it.role)) {
                        onUnauthorized()

                        return@launch
                    }

                    _state.update { state ->
                        state.copy(
                            username = it.username,
                        )
                    }
                }

                val userFilesRes = getFiles(
                    context = context,
                    userId = userId,
                    perPage = filesPerPage,
                    page = _state.value.currentPage,
                    filterFavorite = _state.value.favoriteFilter,
                    sortOrder = _state.value.sortOrder,
                    sortBy = _state.value.sortKey,
                    searchField = _state.value.searchKey,
                    searchQuery = _state.value.searchValue.text.ifEmpty { null }
                )

                userFilesRes.onSuccess { filesResponse ->
                    _state.update {
                        it.copy(
                            files = filesResponse.page,
                            totalPages = filesResponse.pages ?: 1L,
                            isLoading = false
                        )
                    }
                }
            } else {
                val userFilesRes = getFiles(
                    context = context,
                    perPage = filesPerPage,
                    page = _state.value.currentPage,
                    filterFavorite = _state.value.favoriteFilter,
                    sortOrder = _state.value.sortOrder,
                    sortBy = _state.value.sortKey,
                    searchField = _state.value.searchKey,
                    searchQuery = _state.value.searchValue.text.ifEmpty { null }
                )

                userFilesRes.onSuccess { filesResponse ->
                    _state.update {
                        it.copy(
                            files = filesResponse.page,
                            totalPages = filesResponse.pages ?: 1L,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun refreshIncompleteFiles(context: Context) {
        viewModelScope.launch {
            _state.update {
                it.copy(pendingFilesLoading = true,)
            }

            val incompleteFilesRes = getIncompleteFiles(
                context = context,
            )

            incompleteFilesRes.onSuccess { incompleteFiles ->
                _state.update {
                    it.copy(
                        incompleteFiles = incompleteFiles,
                        pendingFilesLoading = false
                    )
                }
            }
        }
    }

    fun refreshTags(context: Context) {
        viewModelScope.launch {
            _state.update {
                it.copy(tagsLoading = true)
            }

            val tagsRes = getTags(
                context = context,
            )

            tagsRes.onSuccess { tags ->
                _state.update {
                    it.copy(
                        tags = tags,
                        tagsLoading = false,
                    )
                }
            }
        }
    }

    fun performDownload(
        context: Context,
        file: File,
        uri: Uri,
        password: String?,
        sendNotification: (content: @Composable () -> Unit) -> Unit,
    ) {
        viewModelScope.launch {
            val fileFits = withContext(Dispatchers.IO) {
                StorageUtil.canFitFile(
                    context = context,
                    uri = uri,
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

            if (file.password == true && password.isNullOrBlank()) {
                setFileRequiresPassword(true)

                return@launch
            }

            sendNotification {
                Text(
                    text = "Starting download...",
                )
            }

            val fileName = file.originalName ?: file.name

            val tempFile = java.io.File(context.cacheDir, fileName)
            val tempDestinationPath = tempFile.absolutePath

            if (tempFile.exists()) {
                withContext(Dispatchers.IO) {
                    tempFile.delete()
                }
            }

            withContext(Dispatchers.IO) {
                val downloadRes = downloadFile(
                    context = context,
                    fileId = file.id,
                    destinationPath = tempDestinationPath,
                    notificationTitle = "Downloading file",
                    notificationContent = "Downloading ${file.name}",
                    password = password
                )

                downloadRes
                    .onSuccess {
                        try {
                            val docUri =
                                DocumentsContract.buildDocumentUriUsingTree(
                                    uri,
                                    DocumentsContract.getTreeDocumentId(
                                        uri
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
                                    Text(
                                        text = "File downloaded to ${_state.value.selectedPath}/$fileName",
                                    )
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
                                "LargeFileDisplay",
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
                        Logger.error("LargeFileDisplay", "Failed to download file", it)

                        sendNotification {
                            Text(
                                text = "Failed to download file: ${it.message}",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
            }

            setFileRequiresPassword(false)
        }
    }
}