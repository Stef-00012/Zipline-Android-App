package com.stefdp.zipline.network.models

data class Export(
    val id: String,
    val createdAt: String,
    val updatedAt: String,
    val completed: Boolean,
    val path: String,
    val files: Long,
    val size: String,
    val userId: String
)
