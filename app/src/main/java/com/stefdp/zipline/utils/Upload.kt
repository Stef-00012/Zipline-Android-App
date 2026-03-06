package com.stefdp.zipline.utils

data class FileUploadState(
    val file: SelectedFile,
    val status: UploadStatus = UploadStatus.PENDING,
    val progressPercent: Int = 0,
    val speedText: String = "",
    val bytesTransferred: Long = 0L,
    val errorMessage: String? = null,
)

enum class UploadStatus {
    PENDING,
    UPLOADING,
    COMPLETE,
    FAILED,
}