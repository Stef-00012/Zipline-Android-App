package com.stefdp.zipline.utils

import android.os.Parcelable
import com.stefdp.zipline.network.models.BaseFolder
import com.stefdp.zipline.network.models.FilesFormat
import com.stefdp.zipline.network.models.ThumbnailFormat
import com.stefdp.zipline.network.models.requests.UploadCompressionType
import io.github.z4kn4fein.semver.toVersion
import kotlinx.parcelize.Parcelize

@Parcelize
data class ZiplineViewState(
    val adminUsers: ZiplineViewStateType,
    val adminInvites: ZiplineViewStateType,
    val files: ZiplineViewStateType,
    val folders: ZiplineViewStateType,
    val urls: ZiplineViewStateType,
) : Parcelable

@Parcelize
enum class ZiplineViewStateType : Parcelable {
    COMPACT,
    LARGE;

    companion object {
        private val names = entries.map { it.name }.toSet()

        operator fun contains(value: String): Boolean = names.contains(value)
    }
}

val minimumZiplineVersion = "4.5.0".toVersion()

val inviteExpiresAtDates = listOf(
    "never" to "Never",
    "30min" to "30 minutes",
    "1h" to "1 hour",
    "6h" to "6 hours",
    "12h" to "12 hours",
    "1d" to "1 day",
    "3d" to "3 days",
    "5d" to "5 days",
    "7d" to "7 days",
)

val deletesAtDates = listOf(
    "never" to "Never",
    "5min" to "5 minutes",
    "10min" to "10 minutes",
    "15min" to "15 minutes",
    "30min" to "30 minutes",
    "1h" to "1 hour",
    "2h" to "2 hours",
    "3h" to "3 hours",
    "4h" to "4 hours",
    "5h" to "5 hours",
    "6h" to "6 hours",
    "8h" to "8 hours",
    "12h" to "12 hours",
    "1d" to "1 day",
    "3d" to "3 days",
    "5d" to "5 days",
    "7d" to "7 days",
    "1w" to "1 week",
    "1.5w" to "1.5 weeks",
    "2w" to "2 weeks",
    "3w" to "3 weeks",
    "30d" to "1 month (30 days)",
    "45.625d" to "1.5 months (~45 days)",
    "60d" to "2 months (60 days)",
    "90d" to "3 months (90 days)",
    "120d" to "4 months (120 days)",
    "0.5 year" to "6 months (0.5 year)",
    "1y" to "1 year"
)

val nameFormats = listOf(
    FilesFormat.RANDOM to "Random",
    FilesFormat.DATE to "Date",
    FilesFormat.UUID to "UUID",
    FilesFormat.NAME to "Use file name",
    FilesFormat.GFYCAT to "Gfycat-style name"
)

val compressionFormats = listOf(
    UploadCompressionType.JPG to ".jpg",
    UploadCompressionType.PNG to ".png",
    UploadCompressionType.WEBP to ".webp",
    UploadCompressionType.JXL to ".jxl"
)

val thumbnailFormats = listOf(
    ThumbnailFormat.JPG to ".jpg",
    ThumbnailFormat.PNG to ".png",
    ThumbnailFormat.WEBP to ".webp",
)

fun getFolderPath(
    folder: BaseFolder,
    folders: List<BaseFolder> = emptyList()
): String {
    if (folder.parentId == null) {
        return folder.name
    }

    val parent = folders.find { it.id == folder.parentId }

    return if (parent != null) {
        "${getFolderPath(parent, folders)} / ${folder.name}"
    } else {
        folder.name
    }
}

fun isChildOf(
    folder: BaseFolder,
    folders: List<BaseFolder>,
    targetParentId: String
): Boolean {
    if (folder.id == targetParentId) return true

    val parentId = folder.parentId ?: return false

    val parent = folders.find { it.id == parentId }

    return if (parent != null) {
        if (parent.id == targetParentId) true
        else isChildOf(parent, folders, targetParentId)
    } else {
        false
    }
}

val settingNames = listOf(
    "coreDefaultDomain" to "Default Domain",
    "coreReturnHttpsUrls" to "Return HTTPS URLs",
    "coreTempDirectory" to "Temporary Directory",

    "chunksEnabled" to "Enable Chunks",
    "chunksMax" to "Max Chunks Size",
    "chunksSize" to "Chunks Size",

    "tasksDeleteInterval" to "Delete Files Interval",
    "tasksClearInvitesInterval" to "Clear Invites Interval",
    "tasksMaxViewsInterval" to "Max Views Interval",
    "tasksThumbnailsInterval" to "Thumbnail Interval",
    "tasksMetricsInterval" to "Metrics Interval",

    "mfaPasskeys" to "Passkeys",
    "mfaTotpEnabled" to "Enable TOTP",
    "mfaTotpIssuer" to "Issuer",

    "featuresImageCompression" to "Image Compression",
    "featuresRobotsTxt" to "/robots.txt",
    "featuresHealthcheck" to "Healthcheck",
    "featuresUserRegistration" to "User Registration",
    "featuresOauthRegistration" to "OAuth Registration",
    "featuresDeleteOnMaxViews" to "Delete on Max Views",
    "featuresMetricsEnabled" to "Enable Metrics",
    "featuresMetricsAdminOnly" to "Admin Only Metrics",
    "featuresMetricsShowUserSpecific" to "Show User Specific Metrics",
    "featuresThumbnailsEnabled" to "Enable Thumbnails",
    "featuresThumbnailsNumberThreads" to "Thumbnails Number Threads",
    "featuresThumbnailsFormat" to "Thumbnails Format",
    "featuresVersionChecking" to "Version Checking",
    "featuresVersionAPI" to "Version API URL",

    "filesRoute" to "Route",
    "filesLength" to "Length",
    "filesAssumeMimetypes" to "Assume Mimetypes",
    "filesRemoveGpsMetadata" to "Remove GPS Metadata",
    "filesDefaultFormat" to "Default Format",
    "filesDisabledExtensions" to "Disabled Extensions",
    "filesMaxFileSize" to "Max File Size",
    "filesDefaultExpiration" to "Default Expiration",
    "filesDefaultDateFormat" to "Default Date Format",

    "urlsRoute" to "Route",
    "urlsLength" to "Length",

    "invitesEnabled" to "Enable Invites",
    "invitesLength" to "Length",

    "ratelimitEnabled" to "Enable Ratelimit",
    "ratelimitAdminBypass" to "Admin Bypass",
    "ratelimitMax" to "Max Requests",
    "ratelimitWindow" to "Window",
    "ratelimitAllowList" to "Allow List",

    "websiteTitle" to "Title",
    "websiteTitleLogo" to "Title Logo",
    "websiteExternalLinks" to "External Links",
    "websiteLoginBackground" to "Login Background",
    "websiteLoginBackgroundBlur" to "Login Background Blur",
    "websiteDefaultAvatar" to "Default Avatar",
    "websiteTos" to "Terms of Service",
    "websiteThemeDefault" to "Default Theme",
    "websiteThemeDark" to "Dark Theme",
    "websiteThemeLight" to "Light Theme",

    "oauthBypassLocalLogin" to "Bypass Local Login",
    "oauthLoginOnly" to "Login Only",

    "oauthDiscordClientId" to "Discord Client ID",
    "oauthDiscordClientSecret" to "Discord Client Secret",
    "oauthDiscordRedirectUri" to "Discord Redirect URL",

    "oauthGoogleClientId" to "Google Client ID",
    "oauthGoogleClientSecret" to "Google Client Secret",
    "oauthGoogleRedirectUri" to "Google Redirect URL",

    "oauthGithubClientId" to "GitHub Client ID",
    "oauthGithubClientSecret" to "GitHub Client Secret",
    "oauthGithubRedirectUri" to "GitHub Redirect URL",

    "oauthOidcClientId" to "OIDC Client ID",
    "oauthOidcClientSecret" to "OIDC Client Secret",
    "oauthOidcAuthorizeUrl" to "OIDC Authorize URL",
    "oauthOidcTokenUrl" to "OIDC Token URL",
    "oauthOidcUserinfoUrl" to "OIDC Userinfo URL",
    "oauthOidcRedirectUri" to "OIDC Redirect URL",

    "pwaEnabled" to "PWA Enabled",
    "pwaTitle" to "Title",
    "pwaShortName" to "Short Name",
    "pwaDescription" to "Description",
    "pwaThemeColor" to "Theme Color",
    "pwaBackgroundColor" to "Background Color",

    "httpWebhookOnUpload" to "On Upload",
    "httpWebhookOnShorten" to "On Shorten",

    "discordWebhookUrl" to "Webhook URL",
    "discordUsername" to "Username",
    "discordAvatarUrl" to "Avatar URL",

    "discordOnUploadWebhookUrl" to "Webhook URL",
    "discordOnUploadAvatarUrl" to "Avatar URL",
    "discordOnUploadUsername" to "Username",
    "discordOnUploadContent" to "Content",
    "discordOnUploadEmbed" to "Embed",

    "discordOnUploadEmbed.color" to "Color",
    "discordOnUploadEmbed.description" to "Description",
    "discordOnUploadEmbed.footer" to "Footer",
    "discordOnUploadEmbed.imageOrVideo" to "Image/Video",
    "discordOnUploadEmbed.thumbnail" to "Thumbnail",
    "discordOnUploadEmbed.timestamp" to "Timestamp",
    "discordOnUploadEmbed.title" to "Title",
    "discordOnUploadEmbed.url" to "URL",

    "discordOnShortenWebhookUrl" to "Webhook URL",
    "discordOnShortenUsername" to "Username",
    "discordOnShortenAvatarUrl" to "Avatar URL",
    "discordOnShortenContent" to "Content",
    "discordOnShortenEmbed" to "Embed",

    "discordOnShortenEmbed.color" to "Color",
    "discordOnShortenEmbed.description" to "Description",
    "discordOnShortenEmbed.footer" to "Footer",
    "discordOnShortenEmbed.timestamp" to "Timestamp",
    "discordOnShortenEmbed.title" to "Title",
    "discordOnShortenEmbed.url" to "URL",
)

fun getSettingName(path: List<String>): String {
    val key = path.joinToString(".")

    return settingNames.find { it.first == key }?.second ?: key
}