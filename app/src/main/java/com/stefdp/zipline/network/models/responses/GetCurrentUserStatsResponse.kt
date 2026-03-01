package com.stefdp.zipline.network.models.responses

import com.google.gson.annotations.SerializedName

data class GetCurrentUserStatsResponse(
    val filesUploaded: Long,
    val favoriteFiles: Long,
    val views: Long,
    @SerializedName("avgViews") val averageViews: Double,
    val storageUsed: Long,
    @SerializedName("avgStorageUsed") val averageStorageUsed: Double,
    val urlsCreated: Long,
    val urlViews: Long,
    val sortTypeCount: Map<String, Long>
)