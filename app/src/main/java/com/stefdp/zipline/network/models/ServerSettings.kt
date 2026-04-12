package com.stefdp.zipline.network.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.stefdp.zipline.network.models.requests.UploadCompressionType
import kotlinx.parcelize.Parcelize

data class ServerSettings(
    val settings: ServerSettingsSettings,
    val tampered: List<String>
)

data class ServerSettingsSettings(
    val coreReturnHttpsUrls: Boolean,
    val coreDefaultDomain: String? = null,
    val coreTempDirectory: String,
    val coreTrustProxy: Boolean,

    val chunksEnabled: Boolean,
    val chunksMax: String,
    val chunksSize: String,

    val tasksDeleteInterval: String,
    val tasksClearInvitesInterval: String,
    val tasksMaxViewsInterval: String,
    val tasksThumbnailsInterval: String,
    val tasksMetricsInterval: String,
    val tasksCleanThumbnailsInterval: String,

    val filesRoute: String,
    val filesLength: Long,
    val filesDefaultFormat: FilesFormat,
    val filesDisabledExtensions: List<String>,
    val filesMaxFileSize: String,
    val filesDefaultExpiration: String? = null,
    val filesMaxExpiration: String? = null,
    val filesAssumeMimetypes: Boolean,
    val filesDefaultDateFormat: String,
    val filesRemoveGpsMetadata: Boolean,
    val filesRandomWordsNumAdjectives: Long,
    val filesRandomWordsSeparator: String,
    val filesDefaultCompressionFormat: UploadCompressionType? = null,
    val filesMaxFilesPerUpload: Long,

    val urlsRoute: String,
    val urlsLength: Long,

    val featuresImageCompression: Boolean,
    val featuresRobotsTxt: Boolean,
    val featuresHealthcheck: Boolean,
    val featuresUserRegistration: Boolean,
    val featuresOauthRegistration: Boolean,
    val featuresDeleteOnMaxViews: Boolean,

    val featuresThumbnailsEnabled: Boolean,
    val featuresThumbnailsNumberThreads: Long,
    val featuresThumbnailsInstantaneous: Boolean,
    val featuresThumbnailsFormat: ThumbnailFormat,

    val featuresMetricsEnabled: Boolean,
    val featuresMetricsAdminOnly: Boolean,
    val featuresMetricsShowUserSpecific: Boolean,

    val featuresVersionChecking: Boolean,
    val featuresVersionAPI: String,

    val invitesEnabled: Boolean,
    val invitesLength: Long,

    val websiteTitle: String,
    val websiteTitleLogo: String? = null,
    val websiteExternalLinks: List<WebsiteExternalLink>,
    val websiteLoginBackground: String? = null,
    val websiteLoginBackgroundBlur: Boolean,
    val websiteDefaultAvatar: String? = null,
    val websiteTos: String? = null,

    val websiteThemeDefault: String,
    val websiteThemeDark: String,
    val websiteThemeLight: String,

    val oauthBypassLocalLogin: Boolean,
    val oauthLoginOnly: Boolean,

    val oauthDiscordClientId: String? = null,
    val oauthDiscordClientSecret: String? = null,
    val oauthDiscordRedirectUri: String? = null,
    val oauthDiscordAllowedIds: List<String>,
    val oauthDiscordDeniedIds: List<String>,

    val oauthGoogleClientId: String? = null,
    val oauthGoogleClientSecret: String? = null,
    val oauthGoogleRedirectUri: String? = null,

    val oauthGithubClientId: String? = null,
    val oauthGithubClientSecret: String? = null,
    val oauthGithubRedirectUri: String? = null,

    val oauthOidcClientId: String? = null,
    val oauthOidcClientSecret: String? = null,
    val oauthOidcAuthorizeUrl: String? = null,
    val oauthOidcTokenUrl: String? = null,
    val oauthOidcUserinfoUrl: String? = null,
    val oauthOidcRedirectUri: String? = null,

    val mfaTotpEnabled: Boolean,
    val mfaTotpIssuer: String,

    val mfaPasskeysEnabled: Boolean,
    val mfaPasskeysRpID: String? = null,
    val mfaPasskeysOrigin: String? = null,

    val ratelimitEnabled: Boolean,
    val ratelimitMax: Long,
    val ratelimitWindow: Long? = null,
    val ratelimitAdminBypass: Boolean,
    val ratelimitAllowList: List<String>,

    val httpWebhookOnUpload: String? = null,
    val httpWebhookOnShorten: String? = null,

    val discordWebhookUrl: String? = null,
    val discordUsername: String? = null,
    val discordAvatarUrl: String? = null,

    val discordOnUploadWebhookUrl: String? = null,
    val discordOnUploadUsername: String? = null,
    val discordOnUploadAvatarUrl: String? = null,
    val discordOnUploadContent: String? = null,
    val discordOnUploadEmbed: ServerSettingsSettingsDiscordUploadEmbed? = null,

    val discordOnShortenWebhookUrl: String? = null,
    val discordOnShortenUsername: String? = null,
    val discordOnShortenAvatarUrl: String? = null,
    val discordOnShortenContent: String? = null,
    val discordOnShortenEmbed: ServerSettingsSettingsDiscordShortenEmbed? = null,

    val pwaEnabled: Boolean,
    val pwaTitle: String,
    val pwaShortName: String,
    val pwaDescription: String,
    val pwaThemeColor: String,
    val pwaBackgroundColor: String,

    val domains: List<String>,
)

data class PartialServerSettingsSettings(
    val coreReturnHttpsUrls: Boolean? = null,
    val coreDefaultDomain: String? = null,
    val coreTempDirectory: String? = null,
    val coreTrustProxy: Boolean? = null,

    val chunksEnabled: Boolean? = null,
    val chunksMax: String? = null,
    val chunksSize: String? = null,

    val tasksDeleteInterval: String? = null,
    val tasksClearInvitesInterval: String? = null,
    val tasksMaxViewsInterval: String? = null,
    val tasksThumbnailsInterval: String? = null,
    val tasksMetricsInterval: String? = null,
    val tasksCleanThumbnailsInterval: String? = null,

    val filesRoute: String? = null,
    val filesLength: Long? = null,
    val filesDefaultFormat: FilesFormat? = null,
    val filesDisabledExtensions: List<String>? = null,
    val filesMaxFileSize: String? = null,
    val filesDefaultExpiration: String? = null,
    val filesMaxExpiration: String? = null,
    val filesAssumeMimetypes: Boolean? = null,
    val filesDefaultDateFormat: String? = null,
    val filesRemoveGpsMetadata: Boolean? = null,
    val filesRandomWordsNumAdjectives: Long? = null,
    val filesRandomWordsSeparator: String? = null,
    val filesDefaultCompressionFormat: UploadCompressionType? = null,
    val filesMaxFilesPerUpload: Long? = null,

    val urlsRoute: String? = null,
    val urlsLength: Long? = null,

    val featuresImageCompression: Boolean? = null,
    val featuresRobotsTxt: Boolean? = null,
    val featuresHealthcheck: Boolean? = null,
    val featuresUserRegistration: Boolean? = null,
    val featuresOauthRegistration: Boolean? = null,
    val featuresDeleteOnMaxViews: Boolean? = null,

    val featuresThumbnailsEnabled: Boolean? = null,
    val featuresThumbnailsNumberThreads: Long? = null,
    val featuresThumbnailsInstantaneous: Boolean? = null,
    val featuresThumbnailsFormat: ThumbnailFormat? = null,

    val featuresMetricsEnabled: Boolean? = null,
    val featuresMetricsAdminOnly: Boolean? = null,
    val featuresMetricsShowUserSpecific: Boolean? = null,

    val featuresVersionChecking: Boolean? = null,
    val featuresVersionAPI: String? = null,

    val invitesEnabled: Boolean? = null,
    val invitesLength: Long? = null,

    val websiteTitle: String? = null,
    val websiteTitleLogo: String? = null,
    val websiteExternalLinks: List<WebsiteExternalLink>? = null,
    val websiteLoginBackground: String? = null,
    val websiteLoginBackgroundBlur: Boolean? = null,
    val websiteDefaultAvatar: String? = null,
    val websiteTos: String? = null,

    val websiteThemeDefault: String? = null,
    val websiteThemeDark: String? = null,
    val websiteThemeLight: String? = null,

    val oauthBypassLocalLogin: Boolean? = null,
    val oauthLoginOnly: Boolean? = null,

    val oauthDiscordClientId: String? = null,
    val oauthDiscordClientSecret: String? = null,
    val oauthDiscordRedirectUri: String? = null,
    val oauthDiscordAllowedIds: List<String>? = null,
    val oauthDiscordDeniedIds: List<String>? = null,

    val oauthGoogleClientId: String? = null,
    val oauthGoogleClientSecret: String? = null,
    val oauthGoogleRedirectUri: String? = null,

    val oauthGithubClientId: String? = null,
    val oauthGithubClientSecret: String? = null,
    val oauthGithubRedirectUri: String? = null,

    val oauthOidcClientId: String? = null,
    val oauthOidcClientSecret: String? = null,
    val oauthOidcAuthorizeUrl: String? = null,
    val oauthOidcTokenUrl: String? = null,
    val oauthOidcUserinfoUrl: String? = null,
    val oauthOidcRedirectUri: String? = null,

    val mfaTotpEnabled: Boolean? = null,
    val mfaTotpIssuer: String? = null,

    val mfaPasskeysEnabled: Boolean? = null,
    val mfaPasskeysRpID: String? = null,
    val mfaPasskeysOrigin: String? = null,

    val ratelimitEnabled: Boolean? = null,
    val ratelimitMax: Long? = null,
    val ratelimitWindow: Long? = null,
    val ratelimitAdminBypass: Boolean? = null,
    val ratelimitAllowList: List<String>? = null,

    val httpWebhookOnUpload: String? = null,
    val httpWebhookOnShorten: String? = null,

    val discordWebhookUrl: String? = null,
    val discordUsername: String? = null,
    val discordAvatarUrl: String? = null,

    val discordOnUploadWebhookUrl: String? = null,
    val discordOnUploadUsername: String? = null,
    val discordOnUploadAvatarUrl: String? = null,
    val discordOnUploadContent: String? = null,
    val discordOnUploadEmbed: ServerSettingsSettingsDiscordUploadEmbed? = null,

    val discordOnShortenWebhookUrl: String? = null,
    val discordOnShortenUsername: String? = null,
    val discordOnShortenAvatarUrl: String? = null,
    val discordOnShortenContent: String? = null,
    val discordOnShortenEmbed: ServerSettingsSettingsDiscordShortenEmbed? = null,

    val pwaEnabled: Boolean? = null,
    val pwaTitle: String? = null,
    val pwaShortName: String? = null,
    val pwaDescription: String? = null,
    val pwaThemeColor: String? = null,
    val pwaBackgroundColor: String? = null,

    val domains: List<String>? = null,
)

@Parcelize
data class WebsiteExternalLink(
    val name: String,
    val url: String,
) : Parcelable

data class ServerSettingsSettingsDiscordUploadEmbed(
    val url: Boolean,
    val color: String? = null,
    val title: String? = null,
    val footer: String? = null,
    val thumbnail: Boolean,
    val timestamp: Boolean,
    val description: String? = null,
    val imageOrVideo: Boolean,
)

data class ServerSettingsSettingsDiscordShortenEmbed(
    val url: Boolean,
    val color: String? = null,
    val title: String? = null,
    val footer: String? = null,
    val thumbnail: Boolean,
    val timestamp: Boolean,
    val description: String? = null,
    val imageOrVideo: Boolean,
)

enum class ThumbnailFormat(val value: String) {
    @SerializedName("jpg")
    JPG("jpg"),

    @SerializedName("png")
    PNG("png"),

    @SerializedName("webp")
    WEBP("webp");

    override fun toString(): String = value
}