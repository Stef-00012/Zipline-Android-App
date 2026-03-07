package com.stefdp.zipline.network.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PublicServerConfig(
    val oauth: PublicServerConfigOauth,
    val oauthEnabled: PublicServerConfigOauthEnabled,
    val website: PublicServerConfigWebsite,
    val features: PublicServerConfigFeatures,
    val mfa: PublicServerConfigMfa,
    val tos: String? = null,
    val files: PublicServerConfigFiles,
    val chunks: PublicServerConfigChunks,
    val firstSetup: Boolean,
    val domains: List<String>? = null,
    val returnHttps: Boolean,
) : Parcelable

@Parcelize
data class PublicServerConfigOauth(
    val bypassLocalLogin: Boolean,
    val loginOnly: Boolean,
) : Parcelable

@Parcelize
data class PublicServerConfigOauthEnabled(
    val discord: Boolean,
    val github: Boolean,
    val google: Boolean,
    val oidc: Boolean,
) : Parcelable

@Parcelize
data class PublicServerConfigWebsite(
    val loginBackground: String? = null,
    val loginBackgroundBlur: Boolean? = null,
    val title: String? = null,
    val tos: Boolean
) : Parcelable

@Parcelize
data class PublicServerConfigFeatures(
    val oauthRegistration: Boolean,
    val userRegistration: Boolean,
    val metrics: PublicServerConfigFeaturesMetrics? = null,
) : Parcelable

@Parcelize
data class PublicServerConfigFeaturesMetrics(
    val adminOnly: Boolean? = null,
) : Parcelable

@Parcelize
data class PublicServerConfigMfa(
    val passkeys: Boolean,
) : Parcelable

@Parcelize
data class PublicServerConfigFiles(
    val maxFileSize: Long,
    val defaultFormat: FilesFormat,
    val maxExpiration: String? = null,
) : Parcelable

@Parcelize
data class PublicServerConfigChunks(
    val max: String,
    val size: String,
    val enabled: Boolean,
) : Parcelable