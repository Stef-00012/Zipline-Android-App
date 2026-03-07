package com.stefdp.zipline.network.models.responses

data class GetServerDataCountsResponse(
    val users: Long,
    val files: Long,
    val urls: Long,
    val folders: Long,
    val invites: Long,
    val thumbnails: Long,
    val metrics: Long,
)