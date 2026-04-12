package com.stefdp.zipline.screens.upload.file

import android.content.Context
import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.zipline.network.models.BaseFolder
import com.stefdp.zipline.network.models.FilesFormat
import com.stefdp.zipline.network.models.requests.UploadCompressionType
import com.stefdp.zipline.network.requests.getFolders
import com.stefdp.zipline.network.requests.uploadFile
import com.stefdp.zipline.network.requests.uploadPartialFile
import com.stefdp.zipline.utils.FileUploadState
import com.stefdp.zipline.utils.SelectedFile
import com.stefdp.zipline.utils.UploadStatus
import com.stefdp.zipline.utils.compressionFormats
import com.stefdp.zipline.utils.copyUriToTempFile
import com.stefdp.zipline.utils.formatSpeed
import com.stefdp.zipline.utils.getFileInfo
import com.stefdp.zipline.utils.nameFormats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class UploadFileUiState(
    val isUploading: Boolean = false,
    val folders: List<BaseFolder> = emptyList(),
    val fileStates: List<FileUploadState> = emptyList(),
    val tempCameraUri: Uri? = null,
    val selectedDeletesAt: Set<String> = setOf("default"),
    val selectedNameFormat: Set<String> = setOf("default"),
    val selectedCompressionFormat: Set<String> = setOf("default"),
    val compressionPercentage: TextFieldValue = TextFieldValue(""),
    val maxViews: TextFieldValue = TextFieldValue(""),
    val selectedFolder: Set<String> = setOf("default"),
    val selectedOverrideDomain: Set<String> = setOf("default"),
    val overrideFileName: TextFieldValue = TextFieldValue(""),
    val password: TextFieldValue = TextFieldValue(""),
    val addOriginalName: Boolean = false,
)

class UploadFileViewModel : ViewModel() {
    private val _state = MutableStateFlow(UploadFileUiState())
    val state: StateFlow<UploadFileUiState> = _state.asStateFlow()

    fun initData(context: Context, sharedFiles: List<String>?) {
        if (sharedFiles != null) {
            _state.update {
                it.copy(
                    fileStates = sharedFiles.mapNotNull { uriString ->
                        val uri = uriString.toUri()
                        val fileInfo = getFileInfo(context, uri) ?: return@mapNotNull null

                        val fileName = fileInfo.first
                        val fileSize = fileInfo.second
                        val fileType = fileInfo.third

                        FileUploadState(
                            file = SelectedFile(
                                uri = uri,
                                displayName = fileName,
                                size = fileSize,
                                type = fileType
                            )
                        )
                    }
                )
            }
        }
    }

    fun addFileStates(fileStates: List<FileUploadState>) {
        _state.update {
            it.copy(
                fileStates = it.fileStates + fileStates
            )
        }
    }

    fun addFileStates(fileState: FileUploadState) {
        _state.update {
            it.copy(
                fileStates = it.fileStates + fileState
            )
        }
    }

    fun removeFileState(fileState: FileUploadState) {
        _state.update {
            it.copy(
                fileStates = it.fileStates - fileState
            )
        }
    }

    fun setTempCameraUri(uri: Uri?) {
        _state.update {
            it.copy(tempCameraUri = uri)
        }
    }

    fun setSelectedDeletesAt(deletesAt: Set<String>) {
        _state.update {
            it.copy(selectedDeletesAt = deletesAt)
        }
    }

    fun setSelectedNameFormat(nameFormat: Set<String>) {
        _state.update {
            it.copy(selectedNameFormat = nameFormat)
        }
    }

    fun setSelectedCompressionFormat(compressionFormat: Set<String>) {
        _state.update {
            it.copy(selectedCompressionFormat = compressionFormat)
        }
    }

    fun setCompressionPercentage(percentage: TextFieldValue) {
        _state.update {
            it.copy(compressionPercentage = percentage)
        }
    }

    fun setMaxViews(maxViews: TextFieldValue) {
        _state.update {
            it.copy(maxViews = maxViews)
        }
    }

    fun setSelectedFolder(folder: Set<String>) {
        _state.update {
            it.copy(selectedFolder = folder)
        }
    }

    fun setSelectedOverrideDomain(domain: Set<String>) {
        _state.update {
            it.copy(selectedOverrideDomain = domain)
        }
    }

    fun setOverrideFileName(fileName: TextFieldValue) {
        _state.update {
            it.copy(overrideFileName = fileName)
        }
    }

    fun setPassword(password: TextFieldValue) {
        _state.update {
            it.copy(password = password)
        }
    }

    fun setAddOriginalName(addOriginalName: Boolean) {
        _state.update {
            it.copy(addOriginalName = addOriginalName)
        }
    }

    fun clearFinishedFileStates() {
        _state.update {
            it.copy(
                fileStates = it.fileStates.filter { fileState -> fileState.status == UploadStatus.UPLOADING || fileState.status == UploadStatus.PENDING }
            )
        }
    }

    fun refreshFolders(context: Context, onError: (String) -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isUploading = true) }

            val foldersRes = getFolders(
                context = context,
                excludeFiles = true
            )

            foldersRes
                .onSuccess { folders ->
                    _state.update {
                        it.copy(
                            isUploading = false,
                            folders = folders
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isUploading = false) }

                    onError(error.message ?: "Failed to fetch folders")
                }
        }
    }

    fun upload(
        context: Context,
        defaultNameFormat: FilesFormat,
        defaultCompressionFormat: UploadCompressionType,
        domains: List<String>,
        defaultDeletesAtDate: String,
        chunksEnabled: Boolean,
        maxChunkSize: Long,
        chunkSize: Long,
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isUploading = true)
            }

            val totalFiles = _state.value.fileStates.size

            for (index in _state.value.fileStates.indices) {
                val state = _state.value.fileStates[index]

                if (state.status == UploadStatus.COMPLETE) {
                    continue
                }

                _state.update {
                    it.copy(
                        fileStates = it.fileStates.toMutableList().also { fileStates ->
                            fileStates[index] = state.copy(
                                status = UploadStatus.UPLOADING,
                                progressPercent = 0f,
                                speedText = "",
                                bytesTransferred = 0,
                                errorMessage = null,
                            )
                        }
                    )
                }

                val tempFile = withContext(Dispatchers.IO) {
                    copyUriToTempFile(
                        context = context,
                        uri = state.file.uri,
                        displayName = state.file.displayName
                    )
                }

                if (tempFile == null) {
                   _state.update {
                       it.copy(
                           fileStates = it.fileStates.toMutableList().also { fileStates ->
                               fileStates[index] = state.copy(
                                   status = UploadStatus.FAILED,
                                   errorMessage = "Failed to read file",
                               )
                           }
                       )
                   }

                    continue
                }

                val fileExtension = state.file.displayName.substringAfterLast('.', "")

                val deletesAt = _state.value.selectedDeletesAt.firstOrNull().takeIf { it != "default" } ?: defaultDeletesAtDate

                val nameFormat = if (_state.value.selectedNameFormat.firstOrNull() == "default")
                    defaultNameFormat
                else
                    nameFormats.firstOrNull {
                        it.first.toString() == _state.value.selectedNameFormat.firstOrNull()
                    }?.first ?: defaultNameFormat

                val compressionType = if (_state.value.selectedCompressionFormat.firstOrNull() == "default")
                    defaultCompressionFormat
                else
                    compressionFormats.firstOrNull {
                        it.first.toString() == _state.value.selectedCompressionFormat.firstOrNull()
                    }?.first ?: defaultCompressionFormat

                val folder = if (_state.value.selectedFolder.firstOrNull() == "default")
                    null
                else
                    _state.value.folders.firstOrNull {
                        it.id == _state.value.selectedFolder.firstOrNull()
                    }?.id

                val overrideDomain = if (_state.value.selectedOverrideDomain.firstOrNull() == "default")
                    null
                else
                    domains.firstOrNull {
                        it == _state.value.selectedOverrideDomain.firstOrNull()
                    }

                if (chunksEnabled && tempFile.length() >= maxChunkSize) {
                    val uploadPartialFileResult = uploadPartialFile(
                        context = context,
                        filePath = tempFile.absolutePath,
                        filename = _state.value.overrideFileName.text.takeIf { it.isNotBlank() },
                        fileExtension = fileExtension,
                        chunkSize = chunkSize,
                        originalName = if (_state.value.addOriginalName) state.file.displayName else null,
                        deletesAt = deletesAt,
                        format = nameFormat,
                        maxViews = _state.value.maxViews.text.toLongOrNull(),
                        folder = folder,
                        domain = overrideDomain,
                        password = _state.value.password.text.takeIf { it.isNotBlank() },
                        imageCompressionType = compressionType,
                        imageCompressionPercent = _state.value.compressionPercentage.text.toFloatOrNull()?.coerceIn(0f, 100f),
                        notificationTitle = "Uploading (${index + 1}/$totalFiles)",
                        notificationContent = state.file.displayName,
                        onProgress = { total, transferred, speed ->
                            val totalFloat = total.toFloat()
                            val transferredFloat = transferred.toFloat()

                            val percent = if (totalFloat > 0f) ((transferredFloat * 100f) / total) else 0f

                            _state.update {
                                it.copy(
                                    fileStates = it.fileStates.toMutableList().also { fileStates ->
                                        fileStates[index] = fileStates[index].copy(
                                            progressPercent = percent,
                                            speedText = formatSpeed(speed),
                                            bytesTransferred = transferred
                                        )
                                    }
                                )
                            }
                        },
                    )

                    uploadPartialFileResult
                        .onSuccess { response ->
                            _state.update {
                                it.copy(
                                    fileStates = it.fileStates.toMutableList().also { fileStates ->
                                        fileStates[index] = fileStates[index].copy(
                                            status = UploadStatus.COMPLETE,
                                            progressPercent = 100f,
                                            url = response.files.first().url
                                        )
                                    }
                                )
                            }
                        }
                        .onFailure { error ->
                            _state.update {
                                it.copy(
                                    fileStates = it.fileStates.toMutableList().also { fileStates ->
                                        fileStates[index] = fileStates[index].copy(
                                            status = UploadStatus.FAILED,
                                            errorMessage = error.message,
                                        )
                                    }
                                )
                            }
                        }
                } else {
                    val uploadFileResult = uploadFile(
                        context = context,
                        filePath = tempFile.absolutePath,
                        filename = _state.value.overrideFileName.text.takeIf { it.isNotBlank() },
                        fileExtension = fileExtension,
                        originalName = if (_state.value.addOriginalName) state.file.displayName else null,
                        deletesAt = deletesAt,
                        format = nameFormat,
                        maxViews = _state.value.maxViews.text.toLongOrNull(),
                        folder = folder,
                        domain = overrideDomain,
                        password = _state.value.password.text.takeIf { it.isNotBlank() },
                        imageCompressionType = compressionType,
                        imageCompressionPercent = _state.value.compressionPercentage.text.toFloatOrNull()?.coerceIn(0f, 100f),
                        notificationTitle = "Uploading (${index + 1}/$totalFiles)",
                        notificationContent = state.file.displayName,
                        onProgress = { total, transferred, speed ->
                            val totalFloat = total.toFloat()
                            val transferredFloat = transferred.toFloat()

                            val percent = if (totalFloat > 0f) ((transferredFloat * 100f) / total) else 0f

                            _state.update {
                                it.copy(
                                    fileStates = it.fileStates.toMutableList().also { fileStates ->
                                        fileStates[index] = fileStates[index].copy(
                                            progressPercent = percent,
                                            speedText = formatSpeed(speed),
                                            bytesTransferred = transferred,
                                        )
                                    }
                                )
                            }
                        },
                    )

                    uploadFileResult
                        .onSuccess { response ->
                            _state.update {
                                it.copy(
                                    fileStates = it.fileStates.toMutableList().also { fileStates ->
                                        fileStates[index] = fileStates[index].copy(
                                            status = UploadStatus.COMPLETE,
                                            progressPercent = 100f,
                                            url = response.files.first().url
                                        )
                                    }
                                )
                            }
                        }
                        .onFailure { error ->
                            _state.update {
                                it.copy(
                                    fileStates = it.fileStates.toMutableList().also { fileStates ->
                                        fileStates[index] = fileStates[index].copy(
                                            status = UploadStatus.FAILED,
                                            errorMessage = error.message,
                                        )
                                    }
                                )
                            }
                        }
                }

                withContext(Dispatchers.IO) {
                    tempFile.delete()
                }
            }

            _state.update {
                it.copy(isUploading = false)
            }
        }
    }
}