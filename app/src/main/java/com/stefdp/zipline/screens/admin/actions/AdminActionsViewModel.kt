package com.stefdp.zipline.screens.admin.actions

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.zipline.Logger
import com.stefdp.zipline.network.requests.exportData
import com.stefdp.zipline.network.requests.getExportSize
import com.stefdp.zipline.network.requests.runDeleteTemporaryFilesJob
import com.stefdp.zipline.network.requests.runRequerySizeJob
import com.stefdp.zipline.network.requests.runThumbnailGenerationJob
import com.stefdp.zipline.network.requests.scanForZeroByteFiles
import com.stefdp.zipline.network.requests.deleteZeroByteFiles as apiDeleteZeroByteFiles
import com.stefdp.zipline.utils.STORAGE_ADMIN_EXPORT_DOWNLOAD_FOLDER_KEY
import com.stefdp.zipline.utils.SecureStorage
import com.stefdp.zipline.utils.StorageUtil
import com.stefdp.zipline.utils.getDisplayPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class AdminActionType {
    IMPORT_EXPORT,
    CLEAR_TEMP_FILES,
    CLEAR_ZERO_BYTE_FILES,
    REQUERY_FILE_SIZES,
    GENERATE_THUMBNAILS
}

data class AdminActionsUiState(
    val isLoading: Boolean = false,
    val popupEnabled: AdminActionType? = null,
    val exportConfirmationPopup: Boolean = false,
    val zeroByteFileCount: Int = 0,
    val selectedUri: Uri? = null,
    val selectedPath: String? = null,
    val excludeMetricsFromExport: Boolean = false,
    val requeryFileSizeForceUpdate: Boolean = false,
    val requeryFileSizeForceDelete: Boolean = false,
    val rerunThumbnailsGeneration: Boolean = false
)

class AdminActionsViewModel : ViewModel() {
    private val _state = MutableStateFlow(AdminActionsUiState())
    val state: StateFlow<AdminActionsUiState> = _state.asStateFlow()

    fun initData(context: Context) {
        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)

            val exportDownloadFolder = secureStore.get(STORAGE_ADMIN_EXPORT_DOWNLOAD_FOLDER_KEY)
            val exportDownloadFolderUri = exportDownloadFolder?.toUri()

            if (exportDownloadFolderUri != null) {
                _state.update {
                    it.copy(
                        selectedUri = exportDownloadFolderUri,
                        selectedPath = getDisplayPath(exportDownloadFolderUri)
                    )
                }
            }
        }
    }

    fun setSelectedUri(
        context: Context,
        uri: Uri
    ) {
        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)

            secureStore.set(STORAGE_ADMIN_EXPORT_DOWNLOAD_FOLDER_KEY, uri.toString())

            _state.update {
                it.copy(
                    selectedUri = uri,
                    selectedPath = getDisplayPath(uri)
                )
            }
        }
    }

    fun setPopupEnabled(actionType: AdminActionType?) {
        _state.update {
            it.copy(
                popupEnabled = actionType
            )
        }
    }

    fun setExportConfirmationPopup(enabled: Boolean) {
        _state.update {
            it.copy(
                exportConfirmationPopup = enabled
            )
        }
    }

    fun setExcludeMetricsFromExport(exclude: Boolean) {
        _state.update {
            it.copy(
                excludeMetricsFromExport = exclude
            )
        }
    }

    fun setRequeryFileSizeForceUpdate(forceUpdate: Boolean) {
        _state.update {
            it.copy(
                requeryFileSizeForceUpdate = forceUpdate
            )
        }
    }

    fun setRequeryFileSizeForceDelete(forceDelete: Boolean) {
        _state.update {
            it.copy(
                requeryFileSizeForceDelete = forceDelete
            )
        }
    }

    fun setRerunThumbnailsGeneration(rerun: Boolean) {
        _state.update {
            it.copy(
                rerunThumbnailsGeneration = rerun
            )
        }
    }

    fun refreshZeroByteFiles(
        context: Context,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            val zeroByteFilesRes = scanForZeroByteFiles(
                context = context
            )

            zeroByteFilesRes
                .onSuccess { scanResult ->
                    _state.update {
                        it.copy(
                            zeroByteFileCount = scanResult.files?.size ?: 0
                        )
                    }
                }
                .onFailure {
                    onError("Failed to scan for zero byte files: ${it.message}")
                }
        }
    }

    fun deleteTemporaryFiles(
        context: Context,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val deleteTempFilesRes = runDeleteTemporaryFilesJob(
                context = context
            )

            deleteTempFilesRes
                .onSuccess { response ->
                    onSuccess(response)

                    _state.update {
                        it.copy(
                            popupEnabled = null,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                   onError("Failed to delete temporary files: ${error.message}")

                    _state.update {
                        it.copy(
                            popupEnabled = null,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun deleteZeroBytesFiles(
        context: Context,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val deleteZeroByteFilesRes = apiDeleteZeroByteFiles(
                context = context
            )

            deleteZeroByteFilesRes
                .onSuccess { temporaryFiles ->
                    onSuccess("Cleared ${temporaryFiles.files?.size ?: 0} files with a size of 0B.")

                    _state.update {
                        it.copy(
                            popupEnabled = null,
                            isLoading = false,
                            zeroByteFileCount = 0
                        )
                    }

                    refreshZeroByteFiles(context, onError)
                }
                .onFailure { error ->
                    onError("Failed to delete zero byte files: ${error.message}")

                    _state.update {
                        it.copy(
                            popupEnabled = null,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun requeryFileSize(
        context: Context,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val requeryFileSizeRes = runRequerySizeJob(
                context = context,
                forceUpdate = _state.value.requeryFileSizeForceUpdate,
                forceDelete = _state.value.requeryFileSizeForceDelete
            )

            requeryFileSizeRes
                .onSuccess { response ->
                    onSuccess(response)

                    _state.update {
                        it.copy(
                            popupEnabled = null,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    onError("Failed to delete requery file sizes: ${error.message}")

                    _state.update {
                        it.copy(
                            popupEnabled = null,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun generateThumbnails(
        context: Context,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val generateThumbnailsRes = runThumbnailGenerationJob(
                context = context,
                rerun = _state.value.rerunThumbnailsGeneration
            )

            generateThumbnailsRes
                .onSuccess { response ->
                    onSuccess(response)

                    _state.update {
                        it.copy(
                            popupEnabled = null,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    onError("Failed to delete requery file sizes: ${error.message}")

                    _state.update {
                        it.copy(
                            popupEnabled = null,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun performDownload(
        context: Context,
        uri: Uri,
        sendNotification: (@Composable () -> Unit) -> Unit,
    ) {
        viewModelScope.launch {
            val exportSize = getExportSize(
                context = context,
                excludeMetrics = _state.value.excludeMetricsFromExport.takeIf { it }
            )

            exportSize
                .onSuccess { exportSize ->
                    val exportFits = withContext(Dispatchers.IO) {
                        StorageUtil.canFitFile(
                            context = context,
                            uri = uri,
                            fileSize = exportSize
                        )
                    }

                    if (!exportFits) {
                        sendNotification {
                            Text(
                                text = "Not enough space in the selected directory to download the export",
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        return@launch
                    }

                    val exportFitsCache = withContext(Dispatchers.IO) {
                        StorageUtil.canFitInternalCache(
                            context = context,
                            fileSize = exportSize
                        )
                    }

                    if (!exportFitsCache) {
                        sendNotification {
                            Text(
                                text = "Not enough space in the internal cache to download the export",
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        return@launch
                    }
                }
                .onFailure {
                    sendNotification {
                        Text(
                            text = "Failed to get export size: ${it.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

            sendNotification {
                Text(
                    text = "Starting download..."
                )
            }

            val fileName = "zipline_export_${System.currentTimeMillis()}.json"

            val tempFile = java.io.File(context.cacheDir, fileName)
            val tempDestinationPath = tempFile.absolutePath

            withContext(Dispatchers.IO) {
                if (tempFile.exists()) tempFile.delete()

                val downloadRes = exportData(
                    context = context,
                    destinationPath = tempDestinationPath,
                    excludeMetrics = _state.value.excludeMetricsFromExport.takeIf { it }
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

                                sendNotification {
                                    Text(
                                        text = "Export downloaded to ${_state.value.selectedPath}/$fileName",
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
                                "AdminActionsScreen[ExportData]",
                                "Failed to copy export to selected directory",
                                e
                            )

                            sendNotification {
                                Text(
                                    text = "Failed to copy export to selected directory: ${e.message}",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        } finally {
                            tempFile.delete()
                        }
                    }
                    .onFailure {
                        Logger.error("AdminActionsScreen[ExportData]", "Failed to download export", it)

                        sendNotification {
                            Text(
                                text = "Failed to download export: ${it.message}",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
            }
        }
    }
}