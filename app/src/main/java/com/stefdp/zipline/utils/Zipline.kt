package com.stefdp.zipline.utils

import androidx.compose.material3.Text
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.network.models.BaseFolder
import com.stefdp.zipline.network.models.FilesFormat
import com.stefdp.zipline.network.models.requests.UploadCompressionType

val deletesAtDates = listOf(
    "never" to "Never",
    "5min" to "5 minutes",
    "10min" to "10 minutes",
    "15min" to "15 minutes",
    "30min" to "30 minutes",
    "1h" to "1 hour",
    "2h" to "2 hours",
    "3h" to "3 hours",
    "4h" to "4 hours",
    "5h" to "5 hours",
    "6h" to "6 hours",
    "8h" to "8 hours",
    "12h" to "12 hours",
    "1d" to "1 day",
    "3d" to "3 days",
    "5d" to "5 days",
    "7d" to "7 days",
    "1w" to "1 week",
    "1.5w" to "1.5 weeks",
    "2w" to "2 weeks",
    "3w" to "3 weeks",
    "30d" to "1 month (30 days)",
    "45.625d" to "1.5 months (~45 days)",
    "60d" to "2 months (60 days)",
    "90d" to "3 months (90 days)",
    "120d" to "4 months (120 days)",
    "0.5 year" to "6 months (0.5 year)",
    "1y" to "1 year"
)

val nameFormats = listOf(
    FilesFormat.RANDOM to "Random",
    FilesFormat.DATE to "Date",
    FilesFormat.UUID to "UUID",
    FilesFormat.NAME to "Use file name",
    FilesFormat.GFYCAT to "Gfycat-style name"
)

val compressionFormats = listOf(
    UploadCompressionType.JPG to ".jpg",
    UploadCompressionType.PNG to ".png",
    UploadCompressionType.WEBP to ".webp",
    UploadCompressionType.JXL to ".jxl"
)

fun getFolderPath(
    folder: BaseFolder,
    folders: List<BaseFolder> = emptyList()
): String {
    if (folder.parentId == null) {
        return folder.name
    }

    val parent = folders.find { it.id == folder.parentId }

    return if (parent != null) {
        "${getFolderPath(parent, folders)}/${folder.name}"
    } else {
        folder.name
    }
}