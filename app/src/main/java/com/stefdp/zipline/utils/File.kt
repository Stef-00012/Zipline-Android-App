package com.stefdp.zipline.utils

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import java.io.File
import java.util.Locale

data class SelectedFile(
    val uri: Uri,
    val displayName: String,
    val size: Long,
)

fun getDisplayPath(uri: Uri): String {
    val docId = DocumentsContract.getTreeDocumentId(uri)
    // docId is typically "primary:Download/subfolder"
    return docId.replace("primary:", "Internal Storage/")
        .replace("home:", "Documents/")
        .ifBlank { uri.path ?: "Selected folder" }
}

fun formatSpeed(bytesPerSecond: Double): String {
    return when {
        bytesPerSecond >= 1_000_000 -> String.format(Locale.US, "%.1f MB/s", bytesPerSecond / 1_000_000)
        bytesPerSecond >= 1_000 -> String.format(Locale.US, "%.1f KB/s", bytesPerSecond / 1_000)
        else -> String.format(Locale.US, "%.0f B/s", bytesPerSecond)
    }
}

fun formatBytes(bytes: Long): String {
    return when {
        bytes >= 1_000_000_000 -> String.format(Locale.US, "%.1f GB", bytes / 1_000_000_000.0)
        bytes >= 1_000_000 -> String.format(Locale.US, "%.1f MB", bytes / 1_000_000.0)
        bytes >= 1_000 -> String.format(Locale.US, "%.1f KB", bytes / 1_000.0)
        else -> "$bytes B"
    }
}

fun copyUriToTempFile(context: Context, uri: Uri, displayName: String): File? {
    return try {
        val tempFile = File(context.cacheDir, "upload_${System.currentTimeMillis()}_$displayName")
        context.contentResolver.openInputStream(uri)?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        tempFile
    } catch (e: Exception) {
        null
    }
}

fun getFileInfo(context: Context, uri: Uri): Pair<String, Long>? {
    return try {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)

                val name = if (nameIndex >= 0) cursor.getString(nameIndex) else "unknown"
                val size = if (sizeIndex >= 0) cursor.getLong(sizeIndex) else 0L

                Pair(name, size)
            } else {
                null
            }
        }
    } catch (e: Exception) {
        null
    }
}