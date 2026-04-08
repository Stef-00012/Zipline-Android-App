package com.stefdp.zipline.network.models.responses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetServerVersionResponse(
    val details: GetServerVersionDetails,
    val data: GetServerVersionData,
    val cache: Boolean
) : Parcelable

@Parcelize
data class GetServerVersionDetails(
    val version: String,
    var sha: String? = null,
) : Parcelable

@Parcelize
data class GetServerVersionData(
    val isUpstream: Boolean,
    val isRelease: Boolean,
    val isLatest: Boolean,
    val version: GetServerVersionResponseVersion,
    val latest: GetServerVersionResponseLatest
) : Parcelable

@Parcelize
data class GetServerVersionResponseVersion(
    val tag: String,
    val sha: String,
    val url: String
) : Parcelable

@Parcelize
data class GetServerVersionResponseLatest(
    val tag: String,
    val url: String,
    val commit: GetServerVersionResponseLatestCommit?
) : Parcelable

@Parcelize
data class GetServerVersionResponseLatestCommit(
    val sha: String,
    val url: String,
    val pull: Boolean
) : Parcelable