package com.stefdp.zipline.screens.admin.settings

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.zipline.network.models.FilesFormat
import com.stefdp.zipline.network.models.PartialServerSettingsSettings
import com.stefdp.zipline.network.models.PublicServerConfig
import com.stefdp.zipline.network.models.ServerSettings
import com.stefdp.zipline.network.models.ServerSettingsSettingsDiscordShortenEmbed
import com.stefdp.zipline.network.models.ServerSettingsSettingsDiscordUploadEmbed
import com.stefdp.zipline.network.models.ThumbnailFormat
import com.stefdp.zipline.network.models.WebSettings
import com.stefdp.zipline.network.models.WebsiteExternalLink
import com.stefdp.zipline.network.models.requests.UploadCompressionType
import com.stefdp.zipline.network.requests.UpdateServerSettingsResult
import com.stefdp.zipline.network.requests.getServerSettings
import com.stefdp.zipline.network.requests.updateServerSettings
import com.stefdp.zipline.utils.getSettingName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Collections

enum class SettingCategory(
    val value: String,
    val categoryName: String
) {
    CORE(
        value = "CORE",
        categoryName = "Core"
    ),

    CHUNKS(
        value = "CHUNKS",
        categoryName = "Chunks"
    ),

    TASKS(
        value = "TASKS",
        categoryName = "Tasks"
    ),

    MFA(
        value = "MFA",
        categoryName = "Multi-Factor Authentication"
    ),

    FEATURES(
        value = "FEATURES",
        categoryName = "Features"
    ),

    FILES(
        value = "FILES",
        categoryName = "Files"
    ),

    URL_SHORTENER(
        value = "URL_SHORTENER",
        categoryName = "URL Shortener"
    ),

    RATELIMIT(
        value = "RATELIMIT",
        categoryName = "Ratelimit"
    ),

    INVITES(
        value = "INVITES",
        categoryName = "Invites"
    ),

    WEBSITE(
        value = "WEBSITE",
        categoryName = "Website"
    ),

    OAUTH(
        value = "OAUTH",
        categoryName = "OAuth"
    ),

    PWA(
        value = "PWA",
        categoryName = "PWA"
    ),

    HTTP_WEBHOOKS(
        value = "HTTP_WEBHOOKS",
        categoryName = "HTTP Webhooks"
    ),

    DOMAINS(
        value = "DOMAINS",
        categoryName = "Domains"
    ),

    DISCORD_WEBHOOK(
        value = "DISCORD_WEBHOOK",
        categoryName = "Discord Webhook"
    ),

    DISCORD_WEBHOOK_ON_UPLOAD(
        value = "DISCORD_WEBHOOK_ON_UPLOAD",
        categoryName = "Discord Webhook (On Upload)"
    ),

    DISCORD_WEBHOOK_ON_SHORTEN(
        value = "DISCORD_WEBHOOK_ON_SHORTEN",
        categoryName = "Discord Webhook (On Shorten)"
    );

    override fun toString(): String = value
}

enum class MoveWebsiteExternalLinkDirection {
    UP,
    DOWN
}

data class AdminSettingsUiState(
    val isLoading: Boolean = false,
    var settings: ServerSettings? = null,
    val settingsUpdateTick: Int = 0,
    val selectedCategory: Set<String> = setOf(
        SettingCategory.CORE.toString()
    ),

    val createNewExternalUrl: Boolean = false,
    val createExternalLinkName: TextFieldValue = TextFieldValue(""),
    val createExternalLinkUrl: TextFieldValue = TextFieldValue(""),

    val editExternalLinkIndex: Int = -1,
    val editExternalLinkName: TextFieldValue = TextFieldValue(""),
    val editExternalLinkUrl: TextFieldValue = TextFieldValue(""),

    val newDomain: TextFieldValue = TextFieldValue(""),

    val coreReturnHttpsUrls: Boolean = settings?.settings?.coreReturnHttpsUrls ?: false,
    val coreTrustProxy: Boolean = settings?.settings?.coreTrustProxy ?: false,
    val coreDefaultDomain: TextFieldValue = TextFieldValue(settings?.settings?.coreDefaultDomain ?: ""),
    val coreTempDirectory: TextFieldValue = TextFieldValue(settings?.settings?.coreTempDirectory ?: ""),

    val chunksEnabled: Boolean = settings?.settings?.chunksEnabled ?: false,
    val chunksMax: TextFieldValue = TextFieldValue(settings?.settings?.chunksMax ?: ""),
    val chunksSize: TextFieldValue = TextFieldValue(settings?.settings?.chunksSize ?: ""),

    val tasksDeleteInterval: TextFieldValue = TextFieldValue(settings?.settings?.tasksDeleteInterval ?: ""),
    val tasksClearInvitesInterval: TextFieldValue = TextFieldValue(settings?.settings?.tasksClearInvitesInterval ?: ""),
    val tasksMaxViewsInterval: TextFieldValue = TextFieldValue(settings?.settings?.tasksMaxViewsInterval ?: ""),
    val tasksThumbnailsInterval: TextFieldValue = TextFieldValue(settings?.settings?.tasksThumbnailsInterval ?: ""),
    val tasksCleanThumbnailsInterval: TextFieldValue = TextFieldValue(settings?.settings?.tasksCleanThumbnailsInterval ?: ""),

    val mfaPasskeysEnabled: Boolean = settings?.settings?.mfaPasskeysEnabled ?: false,
    val mfaPasskeysRpID: TextFieldValue = TextFieldValue(settings?.settings?.mfaPasskeysRpID ?: ""),
    val mfaPasskeysOrigin: TextFieldValue = TextFieldValue(settings?.settings?.mfaPasskeysOrigin ?: ""),
    val mfaTotpEnabled: Boolean = settings?.settings?.mfaTotpEnabled ?: false,
    val mfaTotpIssuer: TextFieldValue = TextFieldValue(settings?.settings?.mfaTotpIssuer ?: ""),

    val featuresImageCompression: Boolean = settings?.settings?.featuresImageCompression ?: false,
    val featuresRobotsTxt: Boolean = settings?.settings?.featuresRobotsTxt ?: false,
    val featuresHealthcheck: Boolean = settings?.settings?.featuresHealthcheck ?: false,
    val featuresUserRegistration: Boolean = settings?.settings?.featuresUserRegistration ?: false,
    val featuresOauthRegistration: Boolean = settings?.settings?.featuresOauthRegistration ?: false,
    val featuresDeleteOnMaxViews: Boolean = settings?.settings?.featuresDeleteOnMaxViews ?: false,
    val featuresMetricsEnabled: Boolean = settings?.settings?.featuresMetricsEnabled ?: false,
    val featuresMetricsAdminOnly: Boolean = settings?.settings?.featuresMetricsAdminOnly ?: false,
    val featuresMetricsShowUserSpecific: Boolean = settings?.settings?.featuresMetricsShowUserSpecific ?: false,
    val featuresThumbnailsEnabled: Boolean = settings?.settings?.featuresThumbnailsEnabled ?: false,
    val featuresThumbnailsInstantaneous: Boolean = settings?.settings?.featuresThumbnailsInstantaneous ?: false,
    val featuresThumbnailsNumberThreads: TextFieldValue = TextFieldValue(settings?.settings?.featuresThumbnailsNumberThreads?.toString() ?: ""),
    val featuresSelectedThumbnailsFormat: Set<String> = setOf((settings?.settings?.featuresThumbnailsFormat ?: ThumbnailFormat.PNG).toString()),
    val featuresVersionChecking: Boolean = settings?.settings?.featuresVersionChecking ?: false,
    val featuresVersionAPI: TextFieldValue = TextFieldValue(settings?.settings?.featuresVersionAPI ?: ""),

    val filesRoute: TextFieldValue = TextFieldValue(settings?.settings?.filesRoute ?: ""),
    val filesLength: TextFieldValue = TextFieldValue(settings?.settings?.filesLength?.toString() ?: ""),
    val filesAssumeMimetypes: Boolean = settings?.settings?.filesAssumeMimetypes ?: false,
    val filesRemoveGpsMetadata: Boolean = settings?.settings?.filesRemoveGpsMetadata ?: false,
    val filesSelectedDefaultFormat: Set<String> = setOf((settings?.settings?.filesDefaultFormat ?: FilesFormat.RANDOM).toString()),
    val filesDisabledExtensions: TextFieldValue = TextFieldValue(settings?.settings?.filesDisabledExtensions?.joinToString(", ") ?: ""),
    val filesMaxFileSize: TextFieldValue = TextFieldValue(settings?.settings?.filesMaxFileSize ?: ""),
    val filesDefaultDateFormat: TextFieldValue = TextFieldValue(settings?.settings?.filesDefaultDateFormat ?: ""),
    val filesDefaultExpiration: TextFieldValue = TextFieldValue(settings?.settings?.filesDefaultExpiration ?: ""),
    val filesMaxExpiration: TextFieldValue = TextFieldValue(settings?.settings?.filesMaxExpiration ?: ""),
    val filesRandomWordsNumAdjectives: TextFieldValue = TextFieldValue(settings?.settings?.filesRandomWordsNumAdjectives?.toString() ?: ""),
    val filesRandomWordsSeparator: TextFieldValue = TextFieldValue(settings?.settings?.filesRandomWordsSeparator ?: ""),
    val filesSelectedDefaultCompressionFormat: Set<String> = setOf((settings?.settings?.filesDefaultCompressionFormat ?: UploadCompressionType.PNG).toString()),
    val filesMaxFilesPerUpload: TextFieldValue = TextFieldValue(settings?.settings?.filesMaxFilesPerUpload?.toString() ?: ""),

    val urlsRoute: TextFieldValue = TextFieldValue(settings?.settings?.urlsRoute ?: ""),
    val urlsLength: TextFieldValue = TextFieldValue(settings?.settings?.urlsLength?.toString() ?: ""),

    val ratelimitEnabled: Boolean = settings?.settings?.ratelimitEnabled ?: false,
    val ratelimitAdminBypass: Boolean = settings?.settings?.ratelimitAdminBypass ?: false,
    val ratelimitMax: TextFieldValue = TextFieldValue(settings?.settings?.ratelimitMax?.toString() ?: ""),
    val ratelimitWindow: TextFieldValue = TextFieldValue(settings?.settings?.ratelimitWindow?.toString() ?: ""),
    val ratelimitAllowList: TextFieldValue = TextFieldValue(settings?.settings?.ratelimitAllowList?.joinToString(", ") ?: ""),

    val invitesEnabled: Boolean = settings?.settings?.invitesEnabled ?: false,
    val invitesLength: TextFieldValue = TextFieldValue(settings?.settings?.invitesLength?.toString() ?: ""),

    val websiteTitle: TextFieldValue = TextFieldValue(settings?.settings?.websiteTitle ?: ""),
    val websiteTitleLogo: TextFieldValue = TextFieldValue(settings?.settings?.websiteTitleLogo ?: ""),
    val websiteExternalLinks: List<WebsiteExternalLink> = settings?.settings?.websiteExternalLinks ?: emptyList(),
    val websiteLoginBackground: TextFieldValue = TextFieldValue(settings?.settings?.websiteLoginBackground ?: ""),
    val websiteLoginBackgroundBlur: Boolean = settings?.settings?.websiteLoginBackgroundBlur ?: false,
    val websiteDefaultAvatar: TextFieldValue = TextFieldValue(settings?.settings?.websiteDefaultAvatar ?: ""),
    val websiteTos: TextFieldValue = TextFieldValue(settings?.settings?.websiteTos ?: ""),
    val websiteThemeDefault: TextFieldValue = TextFieldValue(settings?.settings?.websiteThemeDefault ?: ""),
    val websiteThemeDark: TextFieldValue = TextFieldValue(settings?.settings?.websiteThemeDark ?: ""),
    val websiteThemeLight: TextFieldValue = TextFieldValue(settings?.settings?.websiteThemeLight ?: ""),

    val oauthBypassLocalLogin: Boolean = settings?.settings?.oauthBypassLocalLogin ?: false,
    val oauthLoginOnly: Boolean = settings?.settings?.oauthLoginOnly ?: false,

    val oauthDiscordClientId: TextFieldValue = TextFieldValue(settings?.settings?.oauthDiscordClientId ?: ""),
    val oauthDiscordClientSecret: TextFieldValue = TextFieldValue(settings?.settings?.oauthDiscordClientSecret ?: ""),
    val oauthDiscordRedirectUri: TextFieldValue = TextFieldValue(settings?.settings?.oauthDiscordRedirectUri ?: ""),
    val oauthDiscordAllowedIds: TextFieldValue = TextFieldValue(settings?.settings?.oauthDiscordAllowedIds?.joinToString(", ") ?: ""),
    val oauthDiscordDeniedIds: TextFieldValue = TextFieldValue(settings?.settings?.oauthDiscordDeniedIds?.joinToString(", ") ?: ""),

    val oauthGoogleClientId: TextFieldValue = TextFieldValue(settings?.settings?.oauthGoogleClientId ?: ""),
    val oauthGoogleClientSecret: TextFieldValue = TextFieldValue(settings?.settings?.oauthGoogleClientSecret ?: ""),
    val oauthGoogleRedirectUri: TextFieldValue = TextFieldValue(settings?.settings?.oauthGoogleRedirectUri ?: ""),

    val oauthGithubClientId: TextFieldValue = TextFieldValue(settings?.settings?.oauthGithubClientId ?: ""),
    val oauthGithubClientSecret: TextFieldValue = TextFieldValue(settings?.settings?.oauthGithubRedirectUri ?: ""),
    val oauthGithubRedirectUri: TextFieldValue = TextFieldValue(settings?.settings?.oauthGithubRedirectUri ?: ""),

    val oauthOidcClientId: TextFieldValue = TextFieldValue(settings?.settings?.oauthOidcClientId ?: ""),
    val oauthOidcClientSecret: TextFieldValue = TextFieldValue(settings?.settings?.oauthOidcClientSecret ?: ""),
    val oauthOidcAuthorizeUrl: TextFieldValue = TextFieldValue(settings?.settings?.oauthOidcAuthorizeUrl ?: ""),
    val oauthOidcTokenUrl: TextFieldValue = TextFieldValue(settings?.settings?.oauthOidcTokenUrl ?: ""),
    val oauthOidcUserinfoUrl: TextFieldValue = TextFieldValue(settings?.settings?.oauthOidcUserinfoUrl ?: ""),
    val oauthOidcRedirectUri: TextFieldValue = TextFieldValue(settings?.settings?.oauthOidcRedirectUri ?: ""),

    val pwaEnabled: Boolean = settings?.settings?.pwaEnabled ?: false,
    val pwaTitle: TextFieldValue = TextFieldValue(settings?.settings?.pwaTitle ?: ""),
    val pwaShortName: TextFieldValue = TextFieldValue(settings?.settings?.pwaShortName ?: ""),
    val pwaDescription: TextFieldValue = TextFieldValue(settings?.settings?.pwaDescription ?: ""),
    val pwaThemeColor: Color = try {
        Color((settings?.settings?.pwaThemeColor ?: "#000000").toColorInt())
    } catch (_: Exception) {
        Color.Black
    },
    val pwaBackgroundColor: Color = try {
        Color((settings?.settings?.pwaBackgroundColor ?: "#000000").toColorInt())
    } catch (_: Exception) {
        Color.Black
    },

    val httpWebhookOnUpload: TextFieldValue = TextFieldValue(settings?.settings?.httpWebhookOnUpload ?: ""),
    val httpWebhookOnShorten: TextFieldValue = TextFieldValue(settings?.settings?.httpWebhookOnShorten ?: ""),

    val domains: List<String> = settings?.settings?.domains ?: emptyList(),

    val discordWebhookUrl: TextFieldValue = TextFieldValue(settings?.settings?.discordWebhookUrl ?: ""),
    val discordUsername: TextFieldValue = TextFieldValue(settings?.settings?.discordUsername ?: ""),
    val discordAvatarUrl: TextFieldValue = TextFieldValue(settings?.settings?.discordAvatarUrl ?: ""),

    val discordOnUploadWebhookUrl: TextFieldValue = TextFieldValue(settings?.settings?.discordOnUploadWebhookUrl ?: ""),
    val discordOnUploadUsername: TextFieldValue = TextFieldValue(settings?.settings?.discordOnUploadUsername ?: ""),
    val discordOnUploadAvatarUrl: TextFieldValue = TextFieldValue(settings?.settings?.discordOnUploadAvatarUrl ?: ""),
    val discordOnUploadContent: TextFieldValue = TextFieldValue(settings?.settings?.discordOnUploadContent ?: ""),
    val discordOnUploadEmbed: ServerSettingsSettingsDiscordUploadEmbed? = settings?.settings?.discordOnUploadEmbed,
    val discordOnUploadEmbedTitle: TextFieldValue = TextFieldValue(discordOnUploadEmbed?.title ?: ""),
    val discordOnUploadEmbedDescription: TextFieldValue = TextFieldValue(discordOnUploadEmbed?.description ?: ""),
    val discordOnUploadEmbedFooter: TextFieldValue = TextFieldValue(discordOnUploadEmbed?.footer ?: ""),
    val discordOnUploadEmbedColor: Color = try {
        Color((discordOnUploadEmbed?.color ?: "#000000").toColorInt())
    } catch (_: Exception) {
        Color.Black
    },
    val discordOnUploadEmbedThumbnail: Boolean = discordOnUploadEmbed?.thumbnail ?: false,
    val discordOnUploadEmbedImageOrVideo: Boolean = discordOnUploadEmbed?.imageOrVideo ?: false,
    val discordOnUploadEmbedTimestamp: Boolean = discordOnUploadEmbed?.timestamp ?: false,
    val discordOnUploadEmbedUrl: Boolean = discordOnUploadEmbed?.url ?: false,

    val discordOnShortenWebhookUrl: TextFieldValue = TextFieldValue(settings?.settings?.discordOnShortenWebhookUrl ?: ""),
    val discordOnShortenUsername: TextFieldValue = TextFieldValue(settings?.settings?.discordOnShortenUsername ?: ""),
    val discordOnShortenAvatarUrl: TextFieldValue = TextFieldValue(settings?.settings?.discordOnShortenAvatarUrl ?: ""),
    val discordOnShortenContent: TextFieldValue = TextFieldValue(settings?.settings?.discordOnShortenContent ?: ""),
    val discordOnShortenEmbed: ServerSettingsSettingsDiscordShortenEmbed? = settings?.settings?.discordOnShortenEmbed,
    val discordOnShortenEmbedTitle: TextFieldValue = TextFieldValue(discordOnShortenEmbed?.title ?: ""),
    val discordOnShortenEmbedDescription: TextFieldValue = TextFieldValue(discordOnShortenEmbed?.description ?: ""),
    val discordOnShortenEmbedFooter: TextFieldValue = TextFieldValue(discordOnShortenEmbed?.footer ?: ""),
    val discordOnShortenEmbedColor: Color = try {
        Color((discordOnShortenEmbed?.color ?: "#000000").toColorInt())
    } catch (_: Exception) {
        Color.Black
    },
    val discordOnShortenEmbedThumbnail: Boolean = discordOnShortenEmbed?.thumbnail ?: false,
    val discordOnShortenEmbedImageOrVideo: Boolean = discordOnShortenEmbed?.imageOrVideo ?: false,
    val discordOnShortenEmbedTimestamp: Boolean = discordOnShortenEmbed?.timestamp ?: false,
    val discordOnShortenEmbedUrl: Boolean = discordOnShortenEmbed?.url ?: false,
)

class AdminSettingsViewModel : ViewModel() {
    private val _state = MutableStateFlow(AdminSettingsUiState())
    val state: StateFlow<AdminSettingsUiState> = _state.asStateFlow()

    fun setSelectedCategory(category: Set<String>) {
        _state.update {
            it.copy(selectedCategory = category)
        }
    }

    fun openCreateNewExternalLink() {
        _state.update {
            it.copy(
                createNewExternalUrl = true,
                createExternalLinkName = TextFieldValue(""),
                createExternalLinkUrl = TextFieldValue("")
            )
        }
    }

    fun closeCreateNewExternalLink() {
        _state.update {
            it.copy(
                createNewExternalUrl = false,
                createExternalLinkName = TextFieldValue(""),
                createExternalLinkUrl = TextFieldValue("")
            )
        }
    }

    fun setCoreReturnHttpsUrls(returnHttpsUrls: Boolean) {
        _state.update {
            it.copy(coreReturnHttpsUrls = returnHttpsUrls)
        }
    }

    fun setCoreTrustProxy(trustProxy: Boolean) {
        _state.update {
            it.copy(coreTrustProxy = trustProxy)
        }
    }

    fun setCoreDefaultDomain(defaultDomain: TextFieldValue) {
        _state.update {
            it.copy(coreDefaultDomain = defaultDomain)
        }
    }

    fun setCoreTempDirectory(tempDirectory: TextFieldValue) {
        _state.update {
            it.copy(coreTempDirectory = tempDirectory)
        }
    }

    fun setChunksEnabled(enabled: Boolean) {
        _state.update {
            it.copy(chunksEnabled = enabled)
        }
    }

    fun setChunksMax(max: TextFieldValue) {
        _state.update {
            it.copy(chunksMax = max)
        }
    }

    fun setChunksSize(size: TextFieldValue) {
        _state.update {
            it.copy(chunksSize = size)
        }
    }

    fun setTasksDeleteInterval(deleteInterval: TextFieldValue) {
        _state.update {
            it.copy(tasksDeleteInterval = deleteInterval)
        }
    }

    fun setTasksClearInvitesInterval(clearInvitesInterval: TextFieldValue) {
        _state.update {
            it.copy(tasksClearInvitesInterval = clearInvitesInterval)
        }
    }

    fun setTasksMaxViewsInterval(maxViewsInterval: TextFieldValue) {
        _state.update {
            it.copy(tasksMaxViewsInterval = maxViewsInterval)
        }
    }

    fun setTasksThumbnailsInterval(thumbnailsInterval: TextFieldValue) {
        _state.update {
            it.copy(tasksThumbnailsInterval = thumbnailsInterval)
        }
    }

    fun setTasksCleanThumbnailsInterval(cleanThumbnailsInterval: TextFieldValue) {
        _state.update {
            it.copy(tasksCleanThumbnailsInterval = cleanThumbnailsInterval)
        }
    }

    fun setMfaPasskeysEnabled(passkeysEnabled: Boolean) {
        _state.update {
            it.copy(mfaPasskeysEnabled = passkeysEnabled)
        }
    }

    fun setMfaPasskeysRpID(passkeysRpID: TextFieldValue) {
        _state.update {
            it.copy(mfaPasskeysRpID = passkeysRpID)
        }
    }

    fun setMfaPasskeysOrigin(passkeysOrigin: TextFieldValue) {
        _state.update {
            it.copy(mfaPasskeysOrigin = passkeysOrigin)
        }
    }

    fun setMfaTotpEnabled(totpEnabled: Boolean) {
        _state.update {
            it.copy(mfaTotpEnabled = totpEnabled)
        }
    }

    fun setMfaTotpIssuer(totpIssuer: TextFieldValue) {
        _state.update {
            it.copy(mfaTotpIssuer = totpIssuer)
        }
    }

    fun setFeaturesImageCompression(imageCompression: Boolean) {
        _state.update {
            it.copy(featuresImageCompression = imageCompression)
        }
    }

    fun setFeaturesRobotsTxt(robotsTxt: Boolean) {
        _state.update {
            it.copy(featuresRobotsTxt = robotsTxt)
        }
    }

    fun setFeaturesHealthcheck(healthcheck: Boolean) {
        _state.update {
            it.copy(featuresHealthcheck = healthcheck)
        }
    }

    fun setFeaturesUserRegistration(userRegistration: Boolean) {
        _state.update {
            it.copy(featuresUserRegistration = userRegistration)
        }
    }

    fun setFeaturesOauthRegistration(oauthRegistration: Boolean) {
        _state.update {
            it.copy(featuresOauthRegistration = oauthRegistration)
        }
    }

    fun setFeaturesDeleteOnMaxViews(deleteOnMaxViews: Boolean) {
        _state.update {
            it.copy(featuresDeleteOnMaxViews = deleteOnMaxViews)
        }
    }

    fun setFeaturesMetricsEnabled(metricsEnabled: Boolean) {
        _state.update {
            it.copy(featuresMetricsEnabled = metricsEnabled)
        }
    }

    fun setFeaturesMetricsAdminOnly(metricsAdminOnly: Boolean) {
        _state.update {
            it.copy(featuresMetricsAdminOnly = metricsAdminOnly)
        }
    }

    fun setFeaturesMetricsShowUserSpecific(metricsShowUserSpecific: Boolean) {
        _state.update {
            it.copy(featuresMetricsShowUserSpecific = metricsShowUserSpecific)
        }
    }

    fun setFeaturesThumbnailsEnabled(thumbnailsEnabled: Boolean) {
        _state.update {
            it.copy(featuresThumbnailsEnabled = thumbnailsEnabled)
        }
    }

    fun setFeaturesThumbnailsInstantaneous(thumbnailsInstantaneous: Boolean) {
        _state.update {
            it.copy(featuresThumbnailsInstantaneous = thumbnailsInstantaneous)
        }
    }

    fun setFeaturesThumbnailsNumberThreads(thumbnailsNumberThreads: TextFieldValue) {
        _state.update {
            it.copy(featuresThumbnailsNumberThreads = thumbnailsNumberThreads)
        }
    }

    fun setFeaturesSelectedThumbnailsFormat(selectedThumbnailsFormat: Set<String>) {
        _state.update {
            it.copy(featuresSelectedThumbnailsFormat = selectedThumbnailsFormat)
        }
    }

    fun setFeaturesVersionChecking(versionChecking: Boolean) {
        _state.update {
            it.copy(featuresVersionChecking = versionChecking)
        }
    }

    fun setFeaturesVersionAPI(versionAPI: TextFieldValue) {
        _state.update {
            it.copy(featuresVersionAPI = versionAPI)
        }
    }

    fun setFilesRoute(route: TextFieldValue) {
        _state.update {
            it.copy(filesRoute = route)
        }
    }

    fun setFilesLength(length: TextFieldValue) {
        _state.update {
            it.copy(filesLength = length)
        }
    }

    fun setFilesAssumeMimetypes(assumeMimetypes: Boolean) {
        _state.update {
            it.copy(filesAssumeMimetypes = assumeMimetypes)
        }
    }

    fun setFilesRemoveGpsMetadata(removeGpsMetadata: Boolean) {
        _state.update {
            it.copy(filesRemoveGpsMetadata = removeGpsMetadata)
        }
    }

    fun setFilesSelectedDefaultFormat(selectedDefaultFormat: Set<String>) {
        _state.update {
            it.copy(filesSelectedDefaultFormat = selectedDefaultFormat)
        }
    }

    fun setFilesDisabledExtensions(disabledExtensions: TextFieldValue) {
        _state.update {
            it.copy(filesDisabledExtensions = disabledExtensions)
        }
    }

    fun setFilesMaxFileSize(maxFileSize: TextFieldValue) {
        _state.update {
            it.copy(filesMaxFileSize = maxFileSize)
        }
    }

    fun setFilesDefaultDateFormat(defaultDateFormat: TextFieldValue) {
        _state.update {
            it.copy(filesDefaultDateFormat = defaultDateFormat)
        }
    }

    fun setFilesDefaultExpiration(defaultExpiration: TextFieldValue) {
        _state.update {
            it.copy(filesDefaultExpiration = defaultExpiration)
        }
    }

    fun setFilesMaxExpiration(maxExpiration: TextFieldValue) {
        _state.update {
            it.copy(filesMaxExpiration = maxExpiration)
        }
    }

    fun setFilesRandomWordsNumAdjectives(randomWordsNumAdjectives: TextFieldValue) {
        _state.update {
            it.copy(filesRandomWordsNumAdjectives = randomWordsNumAdjectives)
        }
    }

    fun setFilesRandomWordsSeparator(randomWordsSeparator: TextFieldValue) {
        _state.update {
            it.copy(filesRandomWordsSeparator = randomWordsSeparator)
        }
    }

    fun setFilesSelectedDefaultCompressionFormat(selectedDefaultCompressionFormat: Set<String>) {
        _state.update {
            it.copy(filesSelectedDefaultCompressionFormat = selectedDefaultCompressionFormat)
        }
    }

    fun setFilesMaxFilesPerUpload(maxFilesPerUpload: TextFieldValue) {
        _state.update {
            it.copy(filesMaxFilesPerUpload = maxFilesPerUpload)
        }
    }

    fun setUrlsRoute(route: TextFieldValue) {
        _state.update {
            it.copy(urlsRoute = route)
        }
    }

    fun setUrlsLength(length: TextFieldValue) {
        _state.update {
            it.copy(urlsLength = length)
        }
    }

    fun setRatelimitEnabled(ratelimitEnabled: Boolean) {
        _state.update {
            it.copy(ratelimitEnabled = ratelimitEnabled)
        }
    }

    fun setRatelimitAdminBypass(ratelimitAdminBypass: Boolean) {
        _state.update {
            it.copy(ratelimitAdminBypass = ratelimitAdminBypass)
        }
    }

    fun setRatelimitMax(ratelimitMax: TextFieldValue) {
        _state.update {
            it.copy(ratelimitMax = ratelimitMax)
        }
    }

    fun setRatelimitWindow(ratelimitWindow: TextFieldValue) {
        _state.update {
            it.copy(ratelimitWindow = ratelimitWindow)
        }
    }

    fun setRatelimitAllowList(ratelimitAllowList: TextFieldValue) {
        _state.update {
            it.copy(ratelimitAllowList = ratelimitAllowList)
        }
    }

    fun setInvitesEnabled(invitesEnabled: Boolean) {
        _state.update {
            it.copy(invitesEnabled = invitesEnabled)
        }
    }

    fun setInvitesLength(invitesLength: TextFieldValue) {
        _state.update {
            it.copy(invitesLength = invitesLength)
        }
    }

    fun setWebsiteTitle(websiteTitle: TextFieldValue) {
        _state.update {
            it.copy(websiteTitle = websiteTitle)
        }
    }

    fun setWebsiteTitleLogo(websiteTitleLogo: TextFieldValue) {
        _state.update {
            it.copy(websiteTitleLogo = websiteTitleLogo)
        }
    }

    fun addWebsiteExternalLink(websiteExternalLink: WebsiteExternalLink) {
        _state.update {
            it.copy(
                websiteExternalLinks = it.websiteExternalLinks + websiteExternalLink,
                createNewExternalUrl = false,
                createExternalLinkName = TextFieldValue(""),
                createExternalLinkUrl = TextFieldValue("")
            )
        }
    }

    fun removeWebsiteExternalLink(websiteExternalLink: WebsiteExternalLink) {
        _state.update {
            it.copy(websiteExternalLinks = it.websiteExternalLinks - websiteExternalLink)
        }
    }

    fun editWebsiteExternalLink(index: Int, newLink: WebsiteExternalLink) {
        _state.update {
            it.copy(
                websiteExternalLinks = it.websiteExternalLinks.toMutableList().also { links ->
                    links[index] = newLink
                }.toList(),
                editExternalLinkIndex = -1,
            )
        }
    }

    fun moveWebsiteExternalLink(index: Int, direction: MoveWebsiteExternalLinkDirection) {
        _state.update {
            val externalLinks = it.websiteExternalLinks.toMutableList()

            Collections.swap(
                externalLinks,
                index,
                if (direction == MoveWebsiteExternalLinkDirection.UP) index - 1 else index + 1
            )

            it.copy(
                websiteExternalLinks = externalLinks,
            )
        }
    }

    fun setCreateExternalLinkName(createExternalLinkName: TextFieldValue) {
        _state.update {
            it.copy(createExternalLinkName = createExternalLinkName)
        }
    }

    fun setCreateExternalLinkUrl(createExternalLinkUrl: TextFieldValue) {
        _state.update {
            it.copy(createExternalLinkUrl = createExternalLinkUrl)
        }
    }

    fun setEditExternalLinkIndex(editExternalLinkIndex: Int) {
        _state.update {
            it.copy(
                editExternalLinkIndex = editExternalLinkIndex,
                editExternalLinkName = if (editExternalLinkIndex != -1) TextFieldValue(it.websiteExternalLinks[editExternalLinkIndex].name) else TextFieldValue(""),
                editExternalLinkUrl = if (editExternalLinkIndex != -1) TextFieldValue(it.websiteExternalLinks[editExternalLinkIndex].url) else TextFieldValue("")
            )
        }
    }

    fun setEditExternalLinkName(editExternalLinkName: TextFieldValue) {
        _state.update {
            it.copy(editExternalLinkName = editExternalLinkName)
        }
    }

    fun setEditExternalLinkUrl(editExternalLinkUrl: TextFieldValue) {
        _state.update {
            it.copy(editExternalLinkUrl = editExternalLinkUrl)
        }
    }

    fun setNewDomain(newDomain: TextFieldValue) {
        _state.update {
            it.copy(newDomain = newDomain)
        }
    }

    fun setWebsiteLoginBackground(websiteLoginBackground: TextFieldValue) {
        _state.update {
            it.copy(websiteLoginBackground = websiteLoginBackground)
        }
    }

    fun setWebsiteLoginBackgroundBlur(websiteLoginBackgroundBlur: Boolean) {
        _state.update {
            it.copy(websiteLoginBackgroundBlur = websiteLoginBackgroundBlur)
        }
    }

    fun setWebsiteDefaultAvatar(websiteDefaultAvatar: TextFieldValue) {
        _state.update {
            it.copy(websiteDefaultAvatar = websiteDefaultAvatar)
        }
    }

    fun setWebsiteTos(websiteTos: TextFieldValue) {
        _state.update {
            it.copy(websiteTos = websiteTos)
        }
    }

    fun setWebsiteThemeDefault(websiteThemeDefault: TextFieldValue) {
        _state.update {
            it.copy(websiteThemeDefault = websiteThemeDefault)
        }
    }

    fun setWebsiteThemeDark(websiteThemeDark: TextFieldValue) {
        _state.update {
            it.copy(websiteThemeDark = websiteThemeDark)
        }
    }

    fun setWebsiteThemeLight(websiteThemeLight: TextFieldValue) {
        _state.update {
            it.copy(websiteThemeLight = websiteThemeLight)
        }
    }

    fun setOauthBypassLocalLogin(oauthBypassLocalLogin: Boolean) {
        _state.update {
            it.copy(oauthBypassLocalLogin = oauthBypassLocalLogin)
        }
    }

    fun setOauthLoginOnly(oauthLoginOnly: Boolean) {
        _state.update {
            it.copy(oauthLoginOnly = oauthLoginOnly)
        }
    }

    fun setOauthDiscordClientId(oauthDiscordClientId: TextFieldValue) {
        _state.update {
            it.copy(oauthDiscordClientId = oauthDiscordClientId)
        }
    }

    fun setOauthDiscordClientSecret(oauthDiscordClientSecret: TextFieldValue) {
        _state.update {
            it.copy(oauthDiscordClientSecret = oauthDiscordClientSecret)
        }
    }

    fun setOauthDiscordRedirectUri(oauthDiscordRedirectUri: TextFieldValue) {
        _state.update {
            it.copy(oauthDiscordRedirectUri = oauthDiscordRedirectUri)
        }
    }

    fun setOauthDiscordAllowedIds(oauthDiscordAllowedIds: TextFieldValue) {
        _state.update {
            it.copy(oauthDiscordAllowedIds = oauthDiscordAllowedIds)
        }
    }

    fun setOauthDiscordDeniedIds(oauthDiscordDeniedIds: TextFieldValue) {
        _state.update {
            it.copy(oauthDiscordDeniedIds = oauthDiscordDeniedIds)
        }
    }

    fun setOauthGoogleClientId(oauthGoogleClientId: TextFieldValue) {
        _state.update {
            it.copy(oauthGoogleClientId = oauthGoogleClientId)
        }
    }

    fun setOauthGoogleClientSecret(oauthGoogleClientSecret: TextFieldValue) {
        _state.update {
            it.copy(oauthGoogleClientSecret = oauthGoogleClientSecret)
        }
    }

    fun setOauthGoogleRedirectUri(oauthGoogleRedirectUri: TextFieldValue) {
        _state.update {
            it.copy(oauthGoogleRedirectUri = oauthGoogleRedirectUri)
        }
    }

    fun setOauthGithubClientId(oauthGithubClientId: TextFieldValue) {
        _state.update {
            it.copy(oauthGithubClientId = oauthGithubClientId)
        }
    }

    fun setOauthGithubClientSecret(oauthGithubClientSecret: TextFieldValue) {
        _state.update {
            it.copy(oauthGithubClientSecret = oauthGithubClientSecret)
        }
    }

    fun setOauthGithubRedirectUri(oauthGithubRedirectUri: TextFieldValue) {
        _state.update {
            it.copy(oauthGithubRedirectUri = oauthGithubRedirectUri)
        }
    }

    fun setOauthOidcClientId(oauthOidcClientId: TextFieldValue) {
        _state.update {
            it.copy(oauthOidcClientId = oauthOidcClientId)
        }
    }

    fun setOauthOidcClientSecret(oauthOidcClientSecret: TextFieldValue) {
        _state.update {
            it.copy(oauthOidcClientSecret = oauthOidcClientSecret)
        }
    }

    fun setOauthOidcAuthorizeUrl(oauthOidcAuthorizeUrl: TextFieldValue) {
        _state.update {
            it.copy(oauthOidcAuthorizeUrl = oauthOidcAuthorizeUrl)
        }
    }

    fun setOauthOidcTokenUrl(oauthOidcTokenUrl: TextFieldValue) {
        _state.update {
            it.copy(oauthOidcTokenUrl = oauthOidcTokenUrl)
        }
    }

    fun setOauthOidcUserinfoUrl(oauthOidcUserinfoUrl: TextFieldValue) {
        _state.update {
            it.copy(oauthOidcUserinfoUrl = oauthOidcUserinfoUrl)
        }
    }

    fun setOauthOidcRedirectUri(oauthOidcRedirectUri: TextFieldValue) {
        _state.update {
            it.copy(oauthOidcRedirectUri = oauthOidcRedirectUri)
        }
    }

    fun setPwaEnabled(pwaEnabled: Boolean) {
        _state.update {
            it.copy(pwaEnabled = pwaEnabled)
        }
    }

    fun setPwaTitle(pwaTitle: TextFieldValue) {
        _state.update {
            it.copy(pwaTitle = pwaTitle)
        }
    }

    fun setPwaShortName(pwaShortName: TextFieldValue) {
        _state.update {
            it.copy(pwaShortName = pwaShortName)
        }
    }

    fun setPwaDescription(pwaDescription: TextFieldValue) {
        _state.update {
            it.copy(pwaDescription = pwaDescription)
        }
    }

    fun setPwaThemeColor(pwaThemeColor: Color) {
        _state.update {
            it.copy(pwaThemeColor = pwaThemeColor)
        }
    }

    fun setPwaBackgroundColor(pwaBackgroundColor: Color) {
        _state.update {
            it.copy(pwaBackgroundColor = pwaBackgroundColor)
        }
    }

    fun setHttpWebhookOnUpload(httpWebhookOnUpload: TextFieldValue) {
        _state.update {
            it.copy(httpWebhookOnUpload = httpWebhookOnUpload)
        }
    }

    fun setHttpWebhookOnShorten(httpWebhookOnShorten: TextFieldValue) {
        _state.update {
            it.copy(httpWebhookOnShorten = httpWebhookOnShorten)
        }
    }

    fun setDiscordWebhookUrl(discordWebhookUrl: TextFieldValue) {
        _state.update {
            it.copy(discordWebhookUrl = discordWebhookUrl)
        }
    }

    fun setDiscordUsername(discordUsername: TextFieldValue) {
        _state.update {
            it.copy(discordUsername = discordUsername)
        }
    }

    fun setDiscordAvatarUrl(discordAvatarUrl: TextFieldValue) {
        _state.update {
            it.copy(discordAvatarUrl = discordAvatarUrl)
        }
    }

    fun setDiscordOnUploadWebhookUrl(discordOnUploadWebhookUrl: TextFieldValue) {
        _state.update {
            it.copy(discordOnUploadWebhookUrl = discordOnUploadWebhookUrl)
        }
    }

    fun setDiscordOnUploadUsername(discordOnUploadUsername: TextFieldValue) {
        _state.update {
            it.copy(discordOnUploadUsername = discordOnUploadUsername)
        }
    }

    fun setDiscordOnUploadAvatarUrl(discordOnUploadAvatarUrl: TextFieldValue) {
        _state.update {
            it.copy(discordOnUploadAvatarUrl = discordOnUploadAvatarUrl)
        }
    }

    fun setDiscordOnUploadContent(discordOnUploadContent: TextFieldValue) {
        _state.update {
            it.copy(discordOnUploadContent = discordOnUploadContent)
        }
    }

    fun setDiscordOnUploadEmbed(discordOnUploadEmbed: ServerSettingsSettingsDiscordUploadEmbed?) {
        _state.update {
            it.copy(discordOnUploadEmbed = discordOnUploadEmbed)
        }
    }

    fun setDiscordOnUploadEmbedTitle(discordOnUploadEmbedTitle: TextFieldValue) {
        _state.update {
            it.copy(discordOnUploadEmbedTitle = discordOnUploadEmbedTitle)
        }
    }

    fun setDiscordOnUploadEmbedDescription(discordOnUploadEmbedDescription: TextFieldValue) {
        _state.update {
            it.copy(discordOnUploadEmbedDescription = discordOnUploadEmbedDescription)
        }
    }

    fun setDiscordOnUploadEmbedFooter(discordOnUploadEmbedFooter: TextFieldValue) {
        _state.update {
            it.copy(discordOnUploadEmbedFooter = discordOnUploadEmbedFooter)
        }
    }

    fun setDiscordOnUploadEmbedColor(discordOnUploadEmbedColor: Color) {
        _state.update {
            it.copy(discordOnUploadEmbedColor = discordOnUploadEmbedColor)
        }
    }

    fun setDiscordOnUploadEmbedThumbnail(discordOnUploadEmbedThumbnail: Boolean) {
        _state.update {
            it.copy(discordOnUploadEmbedThumbnail = discordOnUploadEmbedThumbnail)
        }
    }

    fun setDiscordOnUploadEmbedImageOrVideo(discordOnUploadEmbedImageOrVideo: Boolean) {
        _state.update {
            it.copy(discordOnUploadEmbedImageOrVideo = discordOnUploadEmbedImageOrVideo)
        }
    }

    fun setDiscordOnUploadEmbedTimestamp(discordOnUploadEmbedTimestamp: Boolean) {
        _state.update {
            it.copy(discordOnUploadEmbedTimestamp = discordOnUploadEmbedTimestamp)
        }
    }

    fun setDiscordOnUploadEmbedUrl(discordOnUploadEmbedUrl: Boolean) {
        _state.update {
            it.copy(discordOnUploadEmbedUrl = discordOnUploadEmbedUrl)
        }
    }

    fun setDiscordOnShortenWebhookUrl(discordOnShortenWebhookUrl: TextFieldValue) {
        _state.update {
            it.copy(discordOnShortenWebhookUrl = discordOnShortenWebhookUrl)
        }
    }

    fun setDiscordOnShortenUsername(discordOnShortenUsername: TextFieldValue) {
        _state.update {
            it.copy(discordOnShortenUsername = discordOnShortenUsername)
        }
    }

    fun setDiscordOnShortenAvatarUrl(discordOnShortenAvatarUrl: TextFieldValue) {
        _state.update {
            it.copy(discordOnShortenAvatarUrl = discordOnShortenAvatarUrl)
        }
    }

    fun setDiscordOnShortenContent(discordOnShortenContent: TextFieldValue) {
        _state.update {
            it.copy(discordOnShortenContent = discordOnShortenContent)
        }
    }

    fun setDiscordOnShortenEmbed(discordOnShortenEmbed: ServerSettingsSettingsDiscordShortenEmbed?) {
        _state.update {
            it.copy(discordOnShortenEmbed = discordOnShortenEmbed)
        }
    }

    fun setDiscordOnShortenEmbedTitle(discordOnShortenEmbedTitle: TextFieldValue) {
        _state.update {
            it.copy(discordOnShortenEmbedTitle = discordOnShortenEmbedTitle)
        }
    }

    fun setDiscordOnShortenEmbedDescription(discordOnShortenEmbedDescription: TextFieldValue) {
        _state.update {
            it.copy(discordOnShortenEmbedDescription = discordOnShortenEmbedDescription)
        }
    }

    fun setDiscordOnShortenEmbedFooter(discordOnShortenEmbedFooter: TextFieldValue) {
        _state.update {
            it.copy(discordOnShortenEmbedFooter = discordOnShortenEmbedFooter)
        }
    }

    fun setDiscordOnShortenEmbedColor(discordOnShortenEmbedColor: Color) {
        _state.update {
            it.copy(discordOnShortenEmbedColor = discordOnShortenEmbedColor)
        }
    }

    fun setDiscordOnShortenEmbedThumbnail(discordOnShortenEmbedThumbnail: Boolean) {
        _state.update {
            it.copy(discordOnShortenEmbedThumbnail = discordOnShortenEmbedThumbnail)
        }
    }

    fun setDiscordOnShortenEmbedImageOrVideo(discordOnShortenEmbedImageOrVideo: Boolean) {
        _state.update {
            it.copy(discordOnShortenEmbedImageOrVideo = discordOnShortenEmbedImageOrVideo)
        }
    }

    fun setDiscordOnShortenEmbedTimestamp(discordOnShortenEmbedTimestamp: Boolean) {
        _state.update {
            it.copy(discordOnShortenEmbedTimestamp = discordOnShortenEmbedTimestamp)
        }
    }

    fun setDiscordOnShortenEmbedUrl(discordOnShortenEmbedUrl: Boolean) {
        _state.update {
            it.copy(discordOnShortenEmbedUrl = discordOnShortenEmbedUrl)
        }
    }

    fun resetInputs() {
        _state.update {
            it.copy(
                coreReturnHttpsUrls = it.settings?.settings?.coreReturnHttpsUrls ?: false,
                coreTrustProxy = it.settings?.settings?.coreTrustProxy ?: false,
                coreDefaultDomain = TextFieldValue(it.settings?.settings?.coreDefaultDomain ?: ""),
                coreTempDirectory = TextFieldValue(it.settings?.settings?.coreTempDirectory ?: ""),

                chunksEnabled = it.settings?.settings?.chunksEnabled ?: false,
                chunksMax = TextFieldValue(it.settings?.settings?.chunksMax ?: ""),
                chunksSize = TextFieldValue(it.settings?.settings?.chunksSize ?: ""),

                tasksDeleteInterval = TextFieldValue(it.settings?.settings?.tasksDeleteInterval ?: ""),
                tasksClearInvitesInterval = TextFieldValue(it.settings?.settings?.tasksClearInvitesInterval ?: ""),
                tasksMaxViewsInterval = TextFieldValue(it.settings?.settings?.tasksMaxViewsInterval ?: ""),
                tasksThumbnailsInterval = TextFieldValue(it.settings?.settings?.tasksThumbnailsInterval ?: ""),
                tasksCleanThumbnailsInterval = TextFieldValue(it.settings?.settings?.tasksCleanThumbnailsInterval ?: ""),

                mfaPasskeysEnabled = it.settings?.settings?.mfaPasskeysEnabled ?: false,
                mfaPasskeysRpID = TextFieldValue(it.settings?.settings?.mfaPasskeysRpID ?: ""),
                mfaPasskeysOrigin = TextFieldValue(it.settings?.settings?.mfaPasskeysOrigin ?: ""),
                mfaTotpEnabled = it.settings?.settings?.mfaTotpEnabled ?: false,
                mfaTotpIssuer = TextFieldValue(it.settings?.settings?.mfaTotpIssuer ?: ""),

                featuresImageCompression = it.settings?.settings?.featuresImageCompression ?: false,
                featuresRobotsTxt = it.settings?.settings?.featuresRobotsTxt ?: false,
                featuresHealthcheck = it.settings?.settings?.featuresHealthcheck ?: false,
                featuresUserRegistration = it.settings?.settings?.featuresUserRegistration ?: false,
                featuresOauthRegistration = it.settings?.settings?.featuresOauthRegistration ?: false,
                featuresDeleteOnMaxViews = it.settings?.settings?.featuresDeleteOnMaxViews ?: false,
                featuresMetricsEnabled = it.settings?.settings?.featuresMetricsEnabled ?: false,
                featuresMetricsAdminOnly = it.settings?.settings?.featuresMetricsAdminOnly ?: false,
                featuresMetricsShowUserSpecific = it.settings?.settings?.featuresMetricsShowUserSpecific ?: false,
                featuresThumbnailsEnabled = it.settings?.settings?.featuresThumbnailsEnabled ?: false,
                featuresThumbnailsInstantaneous = it.settings?.settings?.featuresThumbnailsInstantaneous ?: false,
                featuresThumbnailsNumberThreads = TextFieldValue(it.settings?.settings?.featuresThumbnailsNumberThreads ?.toString() ?: ""),
                featuresSelectedThumbnailsFormat = setOf((it.settings?.settings?.featuresThumbnailsFormat ?: ThumbnailFormat.PNG).toString()),
                featuresVersionChecking = it.settings?.settings?.featuresVersionChecking ?: false,
                featuresVersionAPI = TextFieldValue(it.settings?.settings?.featuresVersionAPI ?: ""),

                filesRoute = TextFieldValue(it.settings?.settings?.filesRoute ?: ""),
                filesLength = TextFieldValue(it.settings?.settings?.filesLength?.toString() ?: ""),
                filesAssumeMimetypes = it.settings?.settings?.filesAssumeMimetypes ?: false,
                filesRemoveGpsMetadata = it.settings?.settings?.filesRemoveGpsMetadata ?: false,
                filesSelectedDefaultFormat = setOf((it.settings?.settings?.filesDefaultFormat ?: FilesFormat.RANDOM).toString()),
                filesDisabledExtensions = TextFieldValue(it.settings?.settings?.filesDisabledExtensions?.joinToString(", ") ?: ""),
                filesMaxFileSize = TextFieldValue(it.settings?.settings?.filesMaxFileSize ?: ""),
                filesDefaultDateFormat = TextFieldValue(it.settings?.settings?.filesDefaultDateFormat ?: ""),
                filesDefaultExpiration = TextFieldValue(it.settings?.settings?.filesDefaultExpiration ?: ""),
                filesMaxExpiration = TextFieldValue(it.settings?.settings?.filesMaxExpiration ?: ""),
                filesRandomWordsNumAdjectives = TextFieldValue(it.settings?.settings?.filesRandomWordsNumAdjectives?.toString() ?: ""),
                filesRandomWordsSeparator = TextFieldValue(it.settings?.settings?.filesRandomWordsSeparator ?: ""),
                filesSelectedDefaultCompressionFormat = setOf((it.settings?.settings?.filesDefaultCompressionFormat ?: UploadCompressionType.PNG).toString()),
                filesMaxFilesPerUpload = TextFieldValue(it.settings?.settings?.filesMaxFilesPerUpload?.toString() ?: ""),

                urlsRoute = TextFieldValue(it.settings?.settings?.urlsRoute ?: ""),
                urlsLength = TextFieldValue(it.settings?.settings?.urlsLength?.toString() ?: ""),

                ratelimitEnabled = it.settings?.settings?.ratelimitEnabled ?: false,
                ratelimitAdminBypass = it.settings?.settings?.ratelimitAdminBypass ?: false,
                ratelimitMax = TextFieldValue(it.settings?.settings?.ratelimitMax?.toString() ?: ""),
                ratelimitWindow = TextFieldValue(it.settings?.settings?.ratelimitWindow?.toString() ?: ""),
                ratelimitAllowList = TextFieldValue(it.settings?.settings?.ratelimitAllowList?.joinToString(", ") ?: ""),

                invitesEnabled = it.settings?.settings?.invitesEnabled ?: false,
                invitesLength = TextFieldValue(it.settings?.settings?.invitesLength?.toString() ?: ""),

                websiteTitle = TextFieldValue(it.settings?.settings?.websiteTitle ?: ""),
                websiteTitleLogo = TextFieldValue(it.settings?.settings?.websiteTitleLogo ?: ""),
                websiteExternalLinks = it.settings?.settings?.websiteExternalLinks ?: emptyList(),
                websiteLoginBackground = TextFieldValue(it.settings?.settings?.websiteLoginBackground ?: ""),
                websiteLoginBackgroundBlur = it.settings?.settings?.websiteLoginBackgroundBlur ?: false,
                websiteDefaultAvatar = TextFieldValue(it.settings?.settings?.websiteDefaultAvatar ?: ""),
                websiteTos = TextFieldValue(it.settings?.settings?.websiteTos ?: ""),
                websiteThemeDefault = TextFieldValue(it.settings?.settings?.websiteThemeDefault ?: ""),
                websiteThemeDark = TextFieldValue(it.settings?.settings?.websiteThemeDark ?: ""),
                websiteThemeLight = TextFieldValue(it.settings?.settings?.websiteThemeLight ?: ""),

                oauthBypassLocalLogin = it.settings?.settings?.oauthBypassLocalLogin ?: false,
                oauthLoginOnly = it.settings?.settings?.oauthLoginOnly ?: false,

                oauthDiscordClientId = TextFieldValue(it.settings?.settings?.oauthDiscordClientId ?: ""),
                oauthDiscordClientSecret = TextFieldValue(it.settings?.settings?.oauthDiscordClientSecret ?: ""),
                oauthDiscordRedirectUri = TextFieldValue(it.settings?.settings?.oauthDiscordRedirectUri ?: ""),
                oauthDiscordAllowedIds = TextFieldValue(it.settings?.settings?.oauthDiscordAllowedIds?.joinToString(", ") ?: ""),
                oauthDiscordDeniedIds = TextFieldValue(it.settings?.settings?.oauthDiscordDeniedIds?.joinToString(", ") ?: ""),

                oauthGoogleClientId = TextFieldValue(it.settings?.settings?.oauthGoogleClientId ?: ""),
                oauthGoogleClientSecret = TextFieldValue(it.settings?.settings?.oauthGoogleClientSecret ?: ""),
                oauthGoogleRedirectUri = TextFieldValue(it.settings?.settings?.oauthGoogleRedirectUri ?: ""),

                oauthGithubClientId = TextFieldValue(it.settings?.settings?.oauthGithubClientId ?: ""),
                oauthGithubClientSecret = TextFieldValue(it.settings?.settings?.oauthGithubClientSecret ?: ""),
                oauthGithubRedirectUri = TextFieldValue(it.settings?.settings?.oauthGithubRedirectUri ?: ""),

                oauthOidcClientId = TextFieldValue(it.settings?.settings?.oauthOidcClientId ?: ""),
                oauthOidcClientSecret = TextFieldValue(it.settings?.settings?.oauthOidcClientSecret ?: ""),
                oauthOidcAuthorizeUrl = TextFieldValue(it.settings?.settings?.oauthOidcAuthorizeUrl ?: ""),
                oauthOidcTokenUrl = TextFieldValue(it.settings?.settings?.oauthOidcTokenUrl ?: ""),
                oauthOidcUserinfoUrl = TextFieldValue(it.settings?.settings?.oauthOidcUserinfoUrl ?: ""),
                oauthOidcRedirectUri = TextFieldValue(it.settings?.settings?.oauthOidcRedirectUri ?: ""),

                pwaEnabled = it.settings?.settings?.pwaEnabled ?: false,
                pwaTitle = TextFieldValue(it.settings?.settings?.pwaTitle ?: ""),
                pwaShortName = TextFieldValue(it.settings?.settings?.pwaShortName ?: ""),
                pwaDescription = TextFieldValue(it.settings?.settings?.pwaDescription ?: ""),
                pwaThemeColor = try {
                    Color((it.settings?.settings?.pwaThemeColor ?: "#000000").toColorInt())
                } catch (_: Exception) {
                    Color.Black
                },
                pwaBackgroundColor = try {
                    Color((it.settings?.settings?.pwaBackgroundColor ?: "#000000").toColorInt())
                } catch (_: Exception) {
                    Color.Black
                },

                httpWebhookOnUpload = TextFieldValue(it.settings?.settings?.httpWebhookOnUpload ?: ""),
                httpWebhookOnShorten = TextFieldValue(it.settings?.settings?.httpWebhookOnShorten ?: ""),

                domains = it.settings?.settings?.domains ?: emptyList(),

                discordWebhookUrl = TextFieldValue(it.settings?.settings?.discordWebhookUrl ?: ""),
                discordUsername = TextFieldValue(it.settings?.settings?.discordUsername ?: ""),
                discordAvatarUrl = TextFieldValue(it.settings?.settings?.discordAvatarUrl ?: ""),

                discordOnUploadWebhookUrl = TextFieldValue(it.settings?.settings?.discordOnUploadWebhookUrl ?: ""),
                discordOnUploadUsername = TextFieldValue(it.settings?.settings?.discordOnUploadUsername ?: ""),
                discordOnUploadAvatarUrl = TextFieldValue(it.settings?.settings?.discordOnUploadAvatarUrl ?: ""),
                discordOnUploadContent = TextFieldValue(it.settings?.settings?.discordOnUploadContent ?: ""),
                discordOnUploadEmbed = it.settings?.settings?.discordOnUploadEmbed,
                discordOnUploadEmbedTitle = TextFieldValue(it.discordOnUploadEmbed?.title ?: ""),
                discordOnUploadEmbedDescription = TextFieldValue(it.discordOnUploadEmbed?.description ?: ""),
                discordOnUploadEmbedFooter = TextFieldValue(it.discordOnUploadEmbed?.footer ?: ""),
                discordOnUploadEmbedColor = try {
                    Color((it.discordOnUploadEmbed?.color ?: "#000000").toColorInt())
                } catch (_: Exception) {
                    Color.Black
                },
                discordOnUploadEmbedThumbnail = it.discordOnUploadEmbed?.thumbnail ?: false,
                discordOnUploadEmbedImageOrVideo = it.discordOnUploadEmbed?.imageOrVideo ?: false,
                discordOnUploadEmbedTimestamp = it.discordOnUploadEmbed?.timestamp ?: false,
                discordOnUploadEmbedUrl = it.discordOnUploadEmbed?.url ?: false,

                discordOnShortenWebhookUrl = TextFieldValue(it.settings?.settings?.discordOnShortenWebhookUrl ?: ""),
                discordOnShortenUsername = TextFieldValue(it.settings?.settings?.discordOnShortenUsername ?: ""),
                discordOnShortenAvatarUrl = TextFieldValue(it.settings?.settings?.discordOnShortenAvatarUrl ?: ""),
                discordOnShortenContent = TextFieldValue(it.settings?.settings?.discordOnShortenContent ?: ""),
                discordOnShortenEmbed = it.settings?.settings?.discordOnShortenEmbed,
                discordOnShortenEmbedTitle = TextFieldValue(it.discordOnShortenEmbed?.title ?: ""),
                discordOnShortenEmbedDescription = TextFieldValue(it.discordOnShortenEmbed?.description ?: ""),
                discordOnShortenEmbedFooter = TextFieldValue(it.discordOnShortenEmbed?.footer ?: ""),
                discordOnShortenEmbedColor = try {
                    Color((it.discordOnShortenEmbed?.color ?: "#000000").toColorInt())
                } catch (_: Exception) {
                    Color.Black
                },
                discordOnShortenEmbedThumbnail = it.discordOnShortenEmbed?.thumbnail ?: false,
                discordOnShortenEmbedImageOrVideo = it.discordOnShortenEmbed?.imageOrVideo ?: false,
                discordOnShortenEmbedTimestamp = it.discordOnShortenEmbed?.timestamp ?: false,
                discordOnShortenEmbedUrl = it.discordOnShortenEmbed?.url ?: false,
            )
        }
    }

    fun updateSettings(
        context: Context,
        data: PartialServerSettingsSettings? = null,
        updateWebSettings: suspend () -> Result<WebSettings>,
        updatePublicSettings: suspend () -> Result<PublicServerConfig>,
        onSuccess: () -> Unit,
        onError: (errors: List<String>, isUpdate: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            if (data != null) {
                val updateServerSettingsRes = updateServerSettings(
                    context = context,
                    data = data
                )

                updateServerSettingsRes
                    .onSuccess { response ->
                        if (response is UpdateServerSettingsResult.Error) {
                            val errors = response.error.issues?.map { error -> "${getSettingName(error.path)}: ${error.message}." } ?: listOf(response.error.message ?: response.error.error)

                            onError(errors, true)

                            _state.update {
                                it.copy(isLoading = false)
                            }
                        } else if (response is UpdateServerSettingsResult.Success) {
                            updateWebSettings()
                            updatePublicSettings()

                            onSuccess()

                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    settings = response.settings,
                                    settingsUpdateTick = it.settingsUpdateTick + 1
                                )
                            }
                        }
                    }
                    .onFailure { error ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                            )
                        }

                        val error = listOf(error.message ?: "Something went wrong...")

                        onError(error, true)
                    }
            } else {
                val settingsRes = getServerSettings(
                    context = context,
                )

                settingsRes
                    .onSuccess { settings ->
                        _state.update {
                            it.copy(
                                settings = settings,
                                isLoading = false,
                                settingsUpdateTick = it.settingsUpdateTick + 1
                            )
                        }
                    }
                    .onFailure { error ->
                        _state.update {
                            it.copy(isLoading = false)
                        }

                        val error = listOf(error.message ?: "Something went wrong...")

                        onError(error, false)
                    }
            }
        }
    }
}