package com.stefdp.zipline.network.models

data class Tag(
    val id: String,
    val createdAt: String,
    val updatedAt: String,
    val name: String,
    val color: String,
    val files: List<TagFile>? = null,
)

data class TagFile(
    val id: String,
)
