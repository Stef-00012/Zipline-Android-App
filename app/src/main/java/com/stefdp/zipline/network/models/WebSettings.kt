package com.stefdp.zipline.network.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.stefdp.zipline.network.models.requests.UploadCompressionType
import kotlinx.parcelize.Parcelize

@Parcelize
data class WebSettings(
    val config: WebSettingsConfig? = null,
    val codeMap: List<CodeMapEntry>,
) : Parcelable

@Parcelize
data class WebSettingsConfig(
    val chunks: WebSettingsConfigChunks,
    val tasks: WebSettingsConfigTasks,
    val files: WebSettingsConfigFiles,
    val urls: WebSettingsConfigUrls,
    val features: WebSettingsConfigFeatures,
    val domains: List<String>,
    val invites: WebSettingsConfigInvites,
    val website: WebSettingsConfigWebsite,
    val mfa: WebSettingsConfigMfa,
    val pwa: WebSettingsConfigPwa,
    val oauthEnabled: WebSettingsConfigOauthEnabled,
    val oauth: WebSettingsConfigOauth,
    val version: String? = null,
) : Parcelable

@Parcelize
data class WebSettingsConfigChunks(
    val max: String,
    val size: String,
    val enabled: Boolean,
) : Parcelable

@Parcelize
data class WebSettingsConfigTasks(
    val deleteInterval: String,
    val clearInvitesInterval: String,
    val maxViewsInterval: String,
    val thumbnailsInterval: String,
    val metricsInterval: String,
    val cleanThumbnailsInterval: String,
) : Parcelable

@Parcelize
data class WebSettingsConfigFiles(
    val route: String,
    val length: Long,
    val defaultFormat: FilesFormat,
    val disabledExtensions: List<String>,
    val maxFileSize: String,
    val defaultExpiration: String? = null,
    val maxExpiration: String? = null,
    val assumeMimetypes: Boolean,
    val defaultDateFormat: String,
    val removeGpsMetadata: Boolean,
    val randomWordsNumAdjectives: Long,
    val randomWordsSeparator: String,
    val defaultCompressionFormat: UploadCompressionType,
    val maxFilesPerUpload: Long,
) : Parcelable

@Parcelize
data class WebSettingsConfigUrls(
    val route: String,
    val length: Long,
) : Parcelable

@Parcelize
data class WebSettingsConfigFeatures(
    val imageCompression: Boolean,
    val robotsTxt: Boolean,
    val healthcheck: Boolean,
    val userRegistration: Boolean,
    val oauthRegistration: Boolean,
    val deleteOnMaxViews: Boolean,
    val thumbnails: WebSettingsConfigFeaturesThumbnails,
    val metrics: WebSettingsConfigFeaturesMetrics,
    val versionChecking: Boolean,
    val versionAPI: String,
) : Parcelable

@Parcelize
data class WebSettingsConfigFeaturesThumbnails(
    val enabled: Boolean,
    @SerializedName("num_threads") val threadsCount: Long,
    val format: ThumbnailFormat,
    val instantaneous: Boolean,
) : Parcelable

@Parcelize
data class WebSettingsConfigFeaturesMetrics(
    val enabled: Boolean,
    val adminOnly: Boolean,
    val showUserSpecific: Boolean,
) : Parcelable

@Parcelize
data class WebSettingsConfigInvites(
    val enabled: Boolean,
    val length: Long,
) : Parcelable

@Parcelize
data class WebSettingsConfigWebsite(
    val title: String,
    val titleLogo: String? = null,
    val externalLinks: List<WebsiteExternalLink>,
    val loginBackground: String? = null,
    val loginBackgroundBlur: Boolean,
    val defaultAvatar: String? = null,
    val theme: WebSettingsConfigWebsiteTheme,
    val tos: String? = null,
) : Parcelable

@Parcelize
data class WebSettingsConfigWebsiteTheme(
    val default: String,
    val dark: String,
    val light: String,
) : Parcelable

@Parcelize
data class WebSettingsConfigMfa(
    val totp: WebSettingsConfigMfaTotp,
    val passkeys: WebSettingsConfigMfaPasskeys,
) : Parcelable

@Parcelize
data class WebSettingsConfigMfaTotp(
    val enabled: Boolean,
    val issuer: String,
) : Parcelable

@Parcelize
data class WebSettingsConfigMfaPasskeys(
    val enabled: Boolean,
    val rpID: String? = null,
    val origin: String? = null,
) : Parcelable

@Parcelize
data class WebSettingsConfigPwa(
    val enabled: Boolean,
    val title: String,
    val shortName: String,
    val description: String,
    val themeColor: String,
    val backgroundColor: String,
) : Parcelable

@Parcelize
data class WebSettingsConfigOauthEnabled(
    val discord: Boolean,
    val github: Boolean,
    val google: Boolean,
    val oidc: Boolean,
) : Parcelable

@Parcelize
data class WebSettingsConfigOauth(
    val bypassLocalLogin: Boolean,
    val loginOnly: Boolean,
) : Parcelable

@Parcelize
data class CodeMapEntry(
    @SerializedName("ext") val extension: String,
    @SerializedName("mime") val mimetype: String,
    val name: String,
) : Parcelable