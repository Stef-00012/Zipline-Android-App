package com.stefdp.zipline.utils

import android.content.Context
import android.util.Log
import androidx.compose.material3.Text
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.network.models.BaseFolder
import com.stefdp.zipline.network.requests.getFolders

val deletesAtDates: List<SelectOption> = listOf(
    SelectOption(
        id = "never",
        label = {
            Text("Never")
        }
    ),
    SelectOption(
        id = "5min",
        label = {
            Text("5 minutes")
        }
    ),
    SelectOption(
        id = "10min",
        label = {
            Text("10 minutes")
        }
    ),
    SelectOption(
        id = "15min",
        label = {
            Text("15 minutes")
        }
    ),
    SelectOption(
        id = "30min",
        label = {
            Text("30 minutes")
        }
    ),
    SelectOption(
        id = "1h",
        label = {
            Text("1 hour")
        }
    ),
    SelectOption(
        id = "2h",
        label = {
            Text("2 hours")
        }
    ),
    SelectOption(
        id = "3h",
        label = {
            Text("3 hours")
        }
    ),
    SelectOption(
        id = "4h",
        label = {
            Text("4 hours")
        }
    ),
    SelectOption(
        id = "5h",
        label = {
            Text("5 hours")
        }
    ),
    SelectOption(
        id = "6h",
        label = {
            Text("6 hours")
        }
    ),
    SelectOption(
        id = "8h",
        label = {
            Text("8 hours")
        }
    ),
    SelectOption(
        id = "12h",
        label = {
            Text("12 hours")
        }
    ),
    SelectOption(
        id = "1d",
        label = {
            Text("1 day")
        }
    ),
    SelectOption(
        id = "3d",
        label = {
            Text("3 days")
        }
    ),
    SelectOption(
        id = "5d",
        label = {
            Text("5 days")
        }
    ),
    SelectOption(
        id = "7d",
        label = {
            Text("7 days")
        }
    ),
    SelectOption(
        id = "1w",
        label = {
            Text("1 week")
        }
    ),
    SelectOption(
        id = "1.5w",
        label = {
            Text("1.5 weeks")
        }
    ),
    SelectOption(
        id = "2w",
        label = {
            Text("2 weeks")
        }
    ),
    SelectOption(
        id = "3w",
        label = {
            Text("3 weeks")
        }
    ),
    SelectOption(
        id = "1mo",
        label = {
            Text("1 month (30 days)")
        }
    ),
    SelectOption(
        id = "1.5mo",
        label = {
            Text("1.5 months (~45 days)")
        }
    ),
    SelectOption(
        id = "2mo",
        label = {
            Text("2 months (60 days)")
        }
    ),
    SelectOption(
        id = "3mo",
        label = {
            Text("3 months (90 days)")
        }
    ),
    SelectOption(
        id = "4mo",
        label = {
            Text("4 months (120 days)")
        }
    ),
    SelectOption(
        id = "6mo",
        label = {
            Text("6 months (0.5 year)")
        }
    ),
    SelectOption(
        id = "1y",
        label = {
            Text("1 year")
        }
    ),
)

val nameFormats: List<SelectOption> = listOf(
    SelectOption(
        id = "random",
        label = {
            Text("Random")
        }
    ),
    SelectOption(
        id = "date",
        label = {
            Text("Date")
        }
    ),
    SelectOption(
        id = "uuid",
        label = {
            Text("UUID")
        }
    ),
    SelectOption(
        id = "filename",
        label = {
            Text("Use file name")
        }
    ),
    SelectOption(
        id = "gfycat",
        label = {
            Text("Gfycat-style name")
        }
    ),
)

val compressionFormats: List<SelectOption> = listOf(
    SelectOption(
        id = "jpg",
        label = {
            Text(".jpg")
        }
    ),
    SelectOption(
        id = "png",
        label = {
            Text(".png")
        }
    ),
    SelectOption(
        id = "webp",
        label = {
            Text(".webp")
        }
    ),
    SelectOption(
        id = "jxl",
        label = {
            Text(".jxl")
        }
    ),
)

fun getFolderPath(
    folder: BaseFolder,
    folders: List<BaseFolder> = emptyList()
): String {
    Log.d("getFolderPath", "folders: ${folders.isNotEmpty()}")
    Log.d("getFolderPath", "parentId: ${folder.parentId}, folder: $folder")

    if (folder.parentId == null) {
        Log.d("getFolderPath", "parentId == null, ${folder.parentId}")

        return folder.name
    }

    val parent = folders.find { it.id == folder.parentId }

    Log.d("getFolderPath", "new parent: $parent")

    return if (parent != null) {
        "${getFolderPath(parent, folders)}/${folder.name}"
    } else {
        folder.name
    }
}