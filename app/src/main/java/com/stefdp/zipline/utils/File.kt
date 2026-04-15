package com.stefdp.zipline.utils

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import com.google.common.math.LongMath.pow
import nl.jacobras.humanreadable.HumanReadable
import java.io.File

data class SelectedFile(
    val uri: Uri,
    val displayName: String,
    val size: Long,
    val type: String,
)

fun getDisplayPath(uri: Uri): String {
    val docId = DocumentsContract.getTreeDocumentId(uri)

    return docId.replace("primary:", "Internal Storage/")
        .replace("home:", "Documents/")
        .ifBlank { uri.path ?: "Unknown Folder" }
}

fun formatSpeed(bytesPerSecond: Double): String {
    val bps = HumanReadable.fileSize(bytesPerSecond.toLong(), decimals = 2)
    return "$bps/s"

//    return when {
//        bytesPerSecond >= 1_000_000 -> String.format(Locale.US, "%.1f MB/s", bytesPerSecond / 1_000_000)
//        bytesPerSecond >= 1_000 -> String.format(Locale.US, "%.1f KB/s", bytesPerSecond / 1_000)
//        else -> String.format(Locale.US, "%.0f B/s", bytesPerSecond)
//    }
}

fun formatBytes(bytes: Long, decimals: Int = 2): String {
    return HumanReadable.fileSize(bytes, decimals)
}

fun parseBytes(
    size: String?,
    useBase1024: Boolean = true
): Long {
    if (size.isNullOrEmpty()) return 0L

    val cleanedSize = size
        .trim()
        .lowercase()
        .replace(',', '.')

    val regex = Regex("""^([0-9]+(?:\.[0-9]+)?)\s*([kmgtp]i?b?|b)?$""")
    val match = regex.matchEntire(cleanedSize) ?: return 0L

    val numberPart = match.groupValues[1]
    val unitPart = match.groupValues[2].ifEmpty { "b" }

    val value = numberPart.toLongOrNull() ?: return 0L

    val base = if (useBase1024) 1024L else 1000L

    val multiplier: Long = when (unitPart) {
        "b" -> 1L
        "k", "kb", "kib" -> base
        "m", "mb", "mib" -> pow(base, 2)
        "g", "gb", "gib" -> pow(base, 3)
        "t", "tb", "tib" -> pow(base, 4)
        "p", "pb", "pib" -> pow(base, 5)
        else -> return 0L
    }

    val bytes = value * multiplier

    if (bytes < 0) return Long.MAX_VALUE
    return bytes
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

fun getFileInfo(context: Context, uri: Uri): Triple<String, Long, String>? {
    return try {
        val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"

        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)

                val name = if (nameIndex >= 0) cursor.getString(nameIndex) else "unknown"
                val size = if (sizeIndex >= 0) cursor.getLong(sizeIndex) else 0L

                Triple(name, size, mimeType)
            } else {
                null
            }
        }
    } catch (e: Exception) {
        null
    }
}