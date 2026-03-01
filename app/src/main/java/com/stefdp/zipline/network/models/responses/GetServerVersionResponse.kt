package com.stefdp.zipline.network.models.responses

data class GetServerVersionResponse(
    val isUpstream: Boolean,
    val isRelease: Boolean,
    val isLatest: Boolean,
    val version: GetServerVersionResponseVersion,
    val latest: GetServerVersionResponseLatest
)

data class GetServerVersionResponseVersion(
    val tag: String,
    val sha: String,
    val url: String
)

data class GetServerVersionResponseLatest(
    val tag: String,
    val url: String,
    val commit: GetServerVersionResponseLatestCommit?
)

data class GetServerVersionResponseLatestCommit(
    val sha: String,
    val url: String,
    val pull: Boolean
)