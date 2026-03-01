package com.stefdp.zipline.network.models

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
)

data class PublicServerConfigOauth(
    val bypassLocalLogin: Boolean,
    val loginOnly: Boolean,
)

data class PublicServerConfigOauthEnabled(
    val discord: Boolean,
    val github: Boolean,
    val google: Boolean,
    val oidc: Boolean,
)

data class PublicServerConfigWebsite(
    val loginBackground: String? = null,
    val loginBackgroundBlur: Boolean? = null,
    val title: String? = null,
    val tos: Boolean
)

data class PublicServerConfigFeatures(
    val oauthRegistration: Boolean,
    val userRegistration: Boolean,
    val metrics: PublicServerConfigFeaturesMetrics? = null,
)

data class PublicServerConfigFeaturesMetrics(
    val adminOnly: Boolean? = null,
)

data class PublicServerConfigMfa(
    val passkeys: Boolean,
)

data class PublicServerConfigFiles(
    val maxFileSize: Long,
    val defaultFormat: FilesFormat,
    val maxExpiration: String? = null,
)

data class PublicServerConfigChunks(
    val max: String,
    val size: String,
    val enabled: Boolean,
)