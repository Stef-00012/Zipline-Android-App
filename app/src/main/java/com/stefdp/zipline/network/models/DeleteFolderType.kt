package com.stefdp.zipline.network.models

import com.google.gson.annotations.SerializedName

enum class DeleteFolderType(val value: String) {
    @SerializedName("folder")
    FOLDER("folder"),

    @SerializedName("file")
    FILE("file");

    override fun toString(): String = value
}