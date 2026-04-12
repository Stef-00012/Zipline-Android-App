package com.stefdp.zipline.network.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
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
) : Parcelable

typealias BaseFolder = Folder<FolderParent>
typealias PublicFolder = Folder<PublicFolderParent>

@Parcelize
data class FolderCount(
    val children: Long? = null,
    val files: Long? = null,
) : Parcelable

interface FolderParentBase : Parcelable {
    val id: String
    val name: String
    val parentId: String?
}

@Parcelize
data class FolderParent(
    override val id: String,
    override val name: String,
    override val parentId: String? = null,
) : FolderParentBase, Parcelable

@Parcelize
data class PublicFolderParent(
    override val id: String,
    override val name: String,
    override val parentId: String? = null,
    val public: Boolean? = null,
) : FolderParentBase, Parcelable