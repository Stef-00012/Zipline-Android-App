package com.stefdp.zipline.screens.upload.text

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.zipline.network.models.BaseFolder
import com.stefdp.zipline.network.models.CodeMapEntry
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
import com.stefdp.zipline.utils.nameFormats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class UploadTextUiState(
    val isUploading: Boolean = false,
    val folders: List<BaseFolder> = emptyList(),
    val fileState: FileUploadState? = null,
    val uploadText: TextFieldValue = TextFieldValue(""),
    var selectedFileType: Set<String> = setOf("txt"),
    var selectedDeletesAt: Set<String> = setOf("never"),
    var selectedNameFormat: Set<String> = setOf("default"),
    var selectedCompressionFormat: Set<String> = setOf("default"),
    var compressionPercentage: TextFieldValue = TextFieldValue(""),
    var maxViews: TextFieldValue = TextFieldValue(""),
    var selectedFolder: Set<String> = setOf("default"),
    var selectedOverrideDomain: Set<String> = setOf("default"),
    var overrideFileName: TextFieldValue = TextFieldValue(""),
    var password: TextFieldValue = TextFieldValue(""),
    var addOriginalName: Boolean = false,
)

class UploadTextViewModel : ViewModel() {
    private val _state = MutableStateFlow(UploadTextUiState())
    val state: StateFlow<UploadTextUiState> = _state.asStateFlow()

    fun initData(
        context: Context,
        maxFileSize: Long,
        mimetypes: List<CodeMapEntry>,
        sharedText: String?
    ) {
        if (sharedText != null) {
            if (sharedText.length <= maxFileSize) {
                _state.update {
                    it.copy(uploadText = TextFieldValue(sharedText))
                }
            } else {
                val fileType = mimetypes
                    .firstOrNull {
                        it.extension == "text/plain"
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

                tempFile.writeText(sharedText)

                _state.update {
                    it.copy(
                        uploadText = TextFieldValue(sharedText.take(maxFileSize.toInt())),
                        fileState = FileUploadState(
                            file = SelectedFile(
                                uri = tempFile.toUri(),
                                displayName = "upload.${fileType.extension}",
                                size = tempFile.length(),
                                type = fileType.mimetype
                            )
                        )
                    )
                }
            }
        }
    }

    fun clearFileState() {
        _state.update {
            it.copy(
                fileState = null,
                uploadText = TextFieldValue("")
            )
        }
    }

    fun setFileState(fileState: FileUploadState) {
        _state.update {
            it.copy(
                fileState = fileState
            )
        }
    }

    fun setUploadText(text: TextFieldValue) {
        _state.update {
            it.copy(
                uploadText = text
            )
        }
    }

    fun setSelectedFileType(types: Set<String>) {
        _state.update {
            it.copy(
                selectedFileType = types
            )
        }
    }

    fun setSelectedDeletesAt(deletesAt: Set<String>) {
        _state.update {
            it.copy(
                selectedDeletesAt = deletesAt
            )
        }
    }

    fun setSelectedNameFormat(nameFormat: Set<String>) {
        _state.update {
            it.copy(
                selectedNameFormat = nameFormat
            )
        }
    }

    fun setSelectedCompressionFormat(compressionFormat: Set<String>) {
        _state.update {
            it.copy(
                selectedCompressionFormat = compressionFormat
            )
        }
    }

    fun setCompressionPercentage(percentage: TextFieldValue) {
        _state.update {
            it.copy(
                compressionPercentage = percentage
            )
        }
    }

    fun setMaxViews(maxViews: TextFieldValue) {
        _state.update {
            it.copy(
                maxViews = maxViews
            )
        }
    }

    fun setSelectedFolder(folder: Set<String>) {
        _state.update {
            it.copy(
                selectedFolder = folder
            )
        }
    }

    fun setSelectedOverrideDomain(overrideDomain: Set<String>) {
        _state.update {
            it.copy(
                selectedOverrideDomain = overrideDomain
            )
        }
    }

    fun setOverrideFileName(fileName: TextFieldValue) {
        _state.update {
            it.copy(
                overrideFileName = fileName
            )
        }
    }

    fun setPassword(password: TextFieldValue) {
        _state.update {
            it.copy(
                password = password
            )
        }
    }

    fun setAddOriginalName(addOriginalName: Boolean) {
        _state.update {
            it.copy(
                addOriginalName = addOriginalName
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
        mimetypes: List<CodeMapEntry>,
        defaultNameFormat: FilesFormat,
        defaultCompressionFormat: UploadCompressionType,
        domains: List<String>,
        defaultDeletesAtDate: String,
        chunksEnabled: Boolean,
        maxChunkSize: Long,
        chunkSize: Long,
    ) {
        viewModelScope.launch {
            if (_state.value.fileState == null) {
                if (_state.value.uploadText.text.isBlank()) return@launch

                val fileType = mimetypes
                    .firstOrNull {
                        it.extension == _state.value.selectedFileType.first()
                    } ?: mimetypes
                    .firstOrNull {
                        it.extension == "txt"
                    } ?: CodeMapEntry(
                    name = "Plain text",
                    mimetype = "text/x-zipline-plain",
                    extension = "txt"
                )

                val tempFile = withContext(Dispatchers.IO) {
                    File.createTempFile(
                        "upload",
                        ".${fileType.extension}",
                        context.cacheDir
                    )
                }

                tempFile.writeText(_state.value.uploadText.text)

                setFileState(
                    FileUploadState(
                        file = SelectedFile(
                            uri = tempFile.toUri(),
                            displayName = "upload.${fileType.extension}",
                            size = tempFile.length(),
                            type = fileType.mimetype
                        )
                    )
                )
            }

            if (_state.value.fileState!!.status == UploadStatus.COMPLETE) {
                return@launch
            }

            _state.update {
                it.copy(
                    isUploading = true,
                    fileState = it.fileState!!.copy(
                        status = UploadStatus.UPLOADING,
                        progressPercent = 0f,
                        speedText = "",
                        bytesTransferred = 0,
                        errorMessage = null,
                    )
                )
            }

            val tempFile = withContext(Dispatchers.IO) {
                copyUriToTempFile(
                    context = context,
                    uri = _state.value.fileState!!.file.uri,
                    displayName = _state.value.fileState!!.file.displayName
                )
            }

            if (tempFile == null) {
                _state.update {
                    it.copy(
                        isUploading = false,
                        fileState = it.fileState!!.copy(
                            status = UploadStatus.FAILED,
                            errorMessage = "Failed to read file"
                        )
                    )
                }
                return@launch
            }

            val fileType = mimetypes.firstOrNull {
                it.extension == _state.value.selectedFileType.firstOrNull()
            }?.mimetype ?: mimetypes.firstOrNull {
                it.extension == "txt"
            }?.mimetype ?: "text/x-zipline-plain"

            val fileExtension = mimetypes.firstOrNull {
                it.extension == _state.value.selectedFileType.firstOrNull()
            }?.extension ?: mimetypes.firstOrNull {
                it.extension == "txt"
            }?.extension ?: "txt"

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
                    fileMimeType = fileType,
                    fileExtension = fileExtension,
                    chunkSize = chunkSize,
                    originalName = if (_state.value.addOriginalName) _state.value.fileState!!.file.displayName else null,
                    deletesAt = deletesAt,
                    format = nameFormat,
                    maxViews = _state.value.maxViews.text.toLongOrNull(),
                    folder = folder,
                    domain = overrideDomain,
                    password = _state.value.password.text.takeIf { it.isNotBlank() },
                    imageCompressionType = compressionType,
                    imageCompressionPercent = _state.value.compressionPercentage.text.toFloatOrNull()?.coerceIn(0f, 100f),
                    notificationTitle = "Uploading Text File",
                    notificationContent = _state.value.fileState!!.file.displayName,
                    onProgress = { total, transferred, speed ->
                        val totalFloat = total.toFloat()
                        val transferredFloat = transferred.toFloat()

                        val percent = if (totalFloat > 0f) ((transferredFloat * 100f) / total) else 0f

                        _state.update {
                            it.copy(
                                fileState = it.fileState!!.copy(
                                    progressPercent = percent,
                                    speedText = formatSpeed(speed),
                                    bytesTransferred = transferred
                                )
                            )
                        }
                    },
                )

                uploadPartialFileResult
                    .onSuccess { response ->
                        _state.update {
                            it.copy(
                                fileState = it.fileState!!.copy(
                                    status = UploadStatus.COMPLETE,
                                    progressPercent = 100f,
                                    url = response.files.first().url
                                )
                            )
                        }
                    }
                    .onFailure { error ->
                        _state.update {
                            it.copy(
                                fileState = it.fileState!!.copy(
                                    status = UploadStatus.FAILED,
                                    errorMessage = error.message,
                                )
                            )
                        }
                    }
            } else {
                val uploadFileResult = uploadFile(
                    context = context,
                    filePath = tempFile.absolutePath,
                    filename = _state.value.overrideFileName.text.takeIf { it.isNotBlank() },
                    fileMimeType = fileType,
                    fileExtension = fileExtension,
                    originalName = if (_state.value.addOriginalName) _state.value.fileState!!.file.displayName else null,
                    deletesAt = deletesAt,
                    format = nameFormat,
                    maxViews = _state.value.maxViews.text.toLongOrNull(),
                    folder = folder,
                    domain = overrideDomain,
                    password = _state.value.password.text.takeIf { it.isNotBlank() },
                    imageCompressionType = compressionType,
                    imageCompressionPercent = _state.value.compressionPercentage.text.toFloatOrNull()?.coerceIn(0f, 100f),
                    notificationTitle = "Uploading Text File",
                    notificationContent = _state.value.fileState!!.file.displayName,
                    onProgress = { total, transferred, speed ->
                        val totalFloat = total.toFloat()
                        val transferredFloat = transferred.toFloat()

                        val percent = if (totalFloat > 0f) ((transferredFloat * 100f) / total) else 0f

                        _state.update {
                            it.copy(
                                fileState = it.fileState!!.copy(
                                    progressPercent = percent,
                                    speedText = formatSpeed(speed),
                                    bytesTransferred = transferred,
                                )
                            )
                        }
                    },
                )

                uploadFileResult
                    .onSuccess { response ->
                        _state.update {
                            it.copy(
                                fileState = it.fileState!!.copy(
                                    status = UploadStatus.COMPLETE,
                                    progressPercent = 100f,
                                    url = response.files.first().url
                                )
                            )
                        }
                    }
                    .onFailure { error ->
                        _state.update {
                            it.copy(
                                fileState = it.fileState!!.copy(
                                    status = UploadStatus.FAILED,
                                    errorMessage = error.message,
                                )
                            )
                        }
                    }
            }

            withContext(Dispatchers.IO) {
                tempFile.delete()
            }

            _state.update {
                it.copy(
                    isUploading = false,
                    uploadText = TextFieldValue(""),
                )
            }
        }
    }
}