package com.stefdp.zipline.network.models

import com.google.gson.annotations.SerializedName

data class ForgejoRelease(
    @SerializedName("archive_download_count") val archiveDownloadCount: ForgejoTagArchiveDownloadCount,
    val assets: List<ForgejoAttachment>,
    val author: ForgejoUser,
    val body: String,
    @SerializedName("created_at") val createdAt: String,
    val draft: Boolean,
    @SerializedName("hide_archive_links") val hideArchiveLinks: Boolean,
    @SerializedName("html_url") val htmlUrl: String,
    val id: Long,
    val name: String,
    val prerelease: Boolean,
    @SerializedName("published_at") val publishedAt: String,
    @SerializedName("tag_name") val tagName: String,
    @SerializedName("tarball_url") val tarballUrl: String,
    @SerializedName("target_commitish") val targetCommitish: String,
    @SerializedName("upload_url") val uploadUrl: String,
    val url: String,
    @SerializedName("zipball_url") val zipballUrl: String
)

data class ForgejoTagArchiveDownloadCount(
    @SerializedName("tar_gz") val tarGz: Long,
    val zip: Long
)

data class ForgejoAttachment(
    @SerializedName("browser_download_url") val browserDownloadUrl: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("download_count") val downloadCount: Long,
    val id: Long,
    val name: String,
    val size: Long,
    val type: ForgejoAttachmentType,
    val uuid: String,
)

enum class ForgejoAttachmentType(val value: String) {
    @SerializedName("attachment")
    ATTACHMENT("attachment"),

    @SerializedName("external")
    EXTERNAL("external");

    override fun toString(): String = value
}

data class ForgejoUser(
    val active: Boolean,
    @SerializedName("avatar_url") val avatarUrl: String,
    val created: String,
    val description: String,
    val email: String,
    @SerializedName("followers_count") val followersCount: Long,
    @SerializedName("following_count") val followingCount: Long,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("html_url") val htmlUrl: String,
    val id: Long,
    @SerializedName("is_admin") val isAdmin: Boolean,
    val language: String,
    @SerializedName("last_login") val lastLogin: String,
    val location: String,
    val login: String,
    @SerializedName("login_name") val loginName: String,
    @SerializedName("prohibit_login") val prohibitLogin: Boolean,
    val pronouns: String,
    val restricted: Boolean,
    @SerializedName("source_id") val sourceId: Long,
    val visibility: String,
    val website: String
)