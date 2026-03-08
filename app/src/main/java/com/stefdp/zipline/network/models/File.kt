package com.stefdp.zipline.network.models

data class File(
    val createdAt: String,
    val updatedAt: String,
    val deletesAt: String? = null,
    val favorite: Boolean,
    val id: String,
    val originalName: String? = null,
    val name: String,
    val size: Long,
    val type: String,
    val views: Long,
    val maxViews: Long? = null,
    val password: Boolean? = null,
    val folderId: String? = null,
    val thumbnail: FileThumbnail? = null,
    val tags: List<Tag>? = null,
    val url: String? = null,
    val similarity: Double? = null,
)

data class FileThumbnail(
    val path: String
)