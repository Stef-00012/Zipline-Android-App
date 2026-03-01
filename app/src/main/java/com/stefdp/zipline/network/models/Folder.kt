package com.stefdp.zipline.network.models

import com.google.gson.annotations.SerializedName

data class Folder(
    val id: String,
    val createdAt: String,
    val updatedAt: String,
    val name: String,
    val public: Boolean,
    val allowUploads: Boolean,
    val parentId: String? = null,
    val userId: String,
    val files: List<File>? = null,
    val parent: Folder? = null,
    val children: List<Folder>? = null,
    @SerializedName("_count") val count: FolderCount? = null,
)

data class FolderCount(
    val children: Long? = null,
    val files: Long? = null,
)