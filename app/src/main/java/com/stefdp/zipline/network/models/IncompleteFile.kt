package com.stefdp.zipline.network.models

import com.google.gson.annotations.SerializedName

data class IncompleteFile(
    val id: String,
    val createdAt: String,
    val updatedAt: String,
    val status: IncompleteFileStatus,
    val chunksTotal: Long,
    val chunksComplete: Long,
    val userId: String,
    val metadata: IncompleteFileMetadata
)

enum class IncompleteFileStatus(val value: String) {
    @SerializedName("PENDING")
    PENDING("PENDING"),

    @SerializedName("PROCESSING")
    PROCESSING("PROCESSING"),

    @SerializedName("COMPLETE")
    COMPLETE("COMPLETE"),

    @SerializedName("FAILED")
    FAILED("FAILED");

    override fun toString(): String = value
}

data class IncompleteFileMetadata(
    val file: IncompleteFileMetadataFile
)

data class IncompleteFileMetadataFile(
    val filename: String,
    val type: String,
    val id: String
)