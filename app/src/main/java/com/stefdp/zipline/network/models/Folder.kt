package com.stefdp.zipline.network.models

import com.google.gson.annotations.SerializedName

data class Folder<T : FolderParentBase>(
    val id: String,
    val createdAt: String,
    val updatedAt: String,
    val name: String,
    val public: Boolean,
    val allowUploads: Boolean,
    val parentId: String? = null,
    val userId: String,
    val files: List<File>? = null,
    val parent: FolderParent? = null,
    val children: List<T>? = null,
    @SerializedName("_count") val count: FolderCount? = null,
)

typealias BaseFolder = Folder<FolderParent>
typealias PublicFolder = Folder<PublicFolderParent>

data class FolderCount(
    val children: Long? = null,
    val files: Long? = null,
)

interface FolderParentBase {
    val id: String
    val name: String
    val parentId: String?
}

data class FolderParent(
    override val id: String,
    override val name: String,
    override val parentId: String? = null,
) : FolderParentBase

data class PublicFolderParent(
    override val id: String,
    override val name: String,
    override val parentId: String? = null,
    val public: Boolean? = null,
) : FolderParentBase