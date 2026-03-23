package com.stefdp.zipline.utils

data class FileUploadState(
    val file: SelectedFile,
    val status: UploadStatus = UploadStatus.PENDING,
    val progressPercent: Float = 0f,
    val speedText: String = "",
    val bytesTransferred: Long = 0,
    val errorMessage: String? = null,
    val url: String? = null,
)

enum class UploadStatus {
    PENDING,
    UPLOADING,
    COMPLETE,
    FAILED,
}