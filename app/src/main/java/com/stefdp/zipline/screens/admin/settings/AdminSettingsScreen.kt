package com.stefdp.zipline.screens.admin.settings

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.stefdp.zipline.LocalLoggedUser
import com.stefdp.zipline.LocalUpdatePublicSettings
import com.stefdp.zipline.LocalUpdateWebSettings
import com.stefdp.zipline.R
import com.stefdp.zipline.components.HeaderButton
import com.stefdp.zipline.components.Select
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.network.models.PartialServerSettingsSettings
import com.stefdp.zipline.network.models.ServerSettings
import com.stefdp.zipline.network.requests.UpdateServerSettingsResult
import com.stefdp.zipline.network.requests.getServerSettings
import com.stefdp.zipline.network.requests.updateServerSettings
import com.stefdp.zipline.screens.admin.settings.categories.ChunksCategory
import com.stefdp.zipline.screens.admin.settings.categories.CoreCategory
import com.stefdp.zipline.screens.admin.settings.categories.DiscordWebhookCategory
import com.stefdp.zipline.screens.admin.settings.categories.DiscordWebhookOnShortenCategory
import com.stefdp.zipline.screens.admin.settings.categories.DiscordWebhookOnUploadCategory
import com.stefdp.zipline.screens.admin.settings.categories.DomainsCategory
import com.stefdp.zipline.screens.admin.settings.categories.FeaturesCategory
import com.stefdp.zipline.screens.admin.settings.categories.FilesCategory
import com.stefdp.zipline.screens.admin.settings.categories.HTTPWebhooksCategory
import com.stefdp.zipline.screens.admin.settings.categories.InvitesCategory
import com.stefdp.zipline.screens.admin.settings.categories.MFACategory
import com.stefdp.zipline.screens.admin.settings.categories.OAuthCategory
import com.stefdp.zipline.screens.admin.settings.categories.PWACategory
import com.stefdp.zipline.screens.admin.settings.categories.RatelimitCategory
import com.stefdp.zipline.screens.admin.settings.categories.TasksCategory
import com.stefdp.zipline.screens.admin.settings.categories.UrlShortenerCategory
import com.stefdp.zipline.screens.admin.settings.categories.WebsiteCategory
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.getSettingName
import com.stefdp.zipline.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
fun AdminSettingsScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity
) {
    val localLoggedUser = LocalLoggedUser.current

//    if (localLoggedUser == null) {
//        navController.navigate(LoginScreen) {
//            popUpTo(navController.graph.id) { inclusive = true }
//        }
//    }
//
//    localLoggedUser?.role?.level?.let {
//        if (it > UserRole.SUPERADMIN.level) {
//            navController.navigate(HomeScreen) {
//                popUpTo(navController.graph.id) { inclusive = true }
//            }
//        }
//    }

    var isLoading by remember { mutableStateOf(false) }

    var settings by remember { mutableStateOf<ServerSettings?>(null) }
    var settingsUpdateTick by remember { mutableIntStateOf(0) }

    val coroutineScope = rememberCoroutineScope()

    val updateWebSettings = LocalUpdateWebSettings.current
    val updatePublicSettings = LocalUpdatePublicSettings.current

    fun setLoading(loading: Boolean) {
        isLoading = loading
    }

    suspend fun updateSettings(data: PartialServerSettingsSettings? = null): List<String> {
        isLoading = true

        if (data != null) {
            val updateServerSettingsRes = updateServerSettings(
                context = context,
                data = data
            )

            updateServerSettingsRes
                .onSuccess {
                    if (it is UpdateServerSettingsResult.Error) {
                        return it.error.issues?.map { error -> "${getSettingName(error.path)}: ${error.message}." } ?: listOf(it.error.message ?: it.error.error)
                    } else if (it is UpdateServerSettingsResult.Success) {
                        settings = it.settings

                        updateWebSettings()
                        updatePublicSettings()

                        Toast.makeText(
                            context,
                            "Settings updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        settingsUpdateTick += 1
                    }
                }
                .onFailure {
                    return listOf(it.message ?: "Something went wrong...")
                }
        } else {
            val settingsRes = getServerSettings(
                context = context,
            )

            settingsRes.onSuccess {
                settings = it
            }

            settingsUpdateTick += 1
        }

        isLoading = false
        return emptyList()
    }

    LaunchedEffect(Unit) {
        updateSettings()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 12.dp
                ),
        ) {
            Text(
                text = "Server Settings",
                maxLines = 1,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderButton(
                    icon = painterResource(R.drawable.refresh),
                    contentDescription = "Refresh settings",
                    onClick = {
                        coroutineScope.launch {
                            updateSettings()
                        }
                    },
                    enabled = !isLoading,
                )
            }
        }

        var selectedCategory by remember {
            mutableStateOf(
                setOf(SettingCategory.CORE.toString())
            )
        }

        Select(
            onSelectionChange = { selectedCategory = it },
            selectedIds = selectedCategory,
            options = settingCategories.map { category ->
                SelectOption(
                    id = category.toString(),
                    label = { enabled ->
                        Text(
                            text = category.categoryName,
                            color = if (enabled)
                                MaterialTheme.colorScheme.onBackground
                            else
                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    },
                    enabled = selectedCategory.first() != category.toString()
                )
            },
            containerModifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        )

//        val scrollState = rememberScrollState()

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
//                .verticalScrollWithScrollbar(
//                    scrollState = scrollState,
//                    scrollbarConfig = ScrollbarConfig(
//                        alwaysKeepScrollbar = true
//                    )
//                )
                .padding(
                    horizontal = 12.dp,
                ),
        ) {
            when (selectedCategory.first()) {
                SettingCategory.CORE.toString() -> CoreCategory(
                    settings = settings,
                    isLoading = isLoading,
                    updateSettings = ::updateSettings,
                    setLoading = ::setLoading,
                    title = SettingCategory.CORE.categoryName,
                    settingsUpdateTick = settingsUpdateTick
                )

                SettingCategory.CHUNKS.toString() -> ChunksCategory(
                    settings = settings,
                    isLoading = isLoading,
                    updateSettings = ::updateSettings,
                    setLoading = ::setLoading,
                    title = SettingCategory.CHUNKS.categoryName,
                    settingsUpdateTick = settingsUpdateTick
                )

                SettingCategory.TASKS.toString() -> TasksCategory(
                    settings = settings,
                    isLoading = isLoading,
                    updateSettings = ::updateSettings,
                    setLoading = ::setLoading,
                    title = SettingCategory.TASKS.categoryName,
                    settingsUpdateTick = settingsUpdateTick
                )

                SettingCategory.MFA.toString() -> MFACategory(
                    settings = settings,
                    isLoading = isLoading,
                    updateSettings = ::updateSettings,
                    setLoading = ::setLoading,
                    title = SettingCategory.MFA.categoryName,
                    settingsUpdateTick = settingsUpdateTick
                )

                SettingCategory.FEATURES.toString() -> FeaturesCategory(
                    settings = settings,
                    isLoading = isLoading,
                    updateSettings = ::updateSettings,
                    setLoading = ::setLoading,
                    title = SettingCategory.FEATURES.categoryName,
                    settingsUpdateTick = settingsUpdateTick
                )

                SettingCategory.FILES.toString() -> FilesCategory(
                    settings = settings,
                    isLoading = isLoading,
                    updateSettings = ::updateSettings,
                    setLoading = ::setLoading,
                    title = SettingCategory.FILES.categoryName,
                    settingsUpdateTick = settingsUpdateTick
                )

                SettingCategory.URL_SHORTENER.toString() -> UrlShortenerCategory(
                    settings = settings,
                    isLoading = isLoading,
                    updateSettings = ::updateSettings,
                    setLoading = ::setLoading,
                    title = SettingCategory.URL_SHORTENER.categoryName,
                    settingsUpdateTick = settingsUpdateTick
                )

                SettingCategory.RATELIMIT.toString() -> RatelimitCategory(
                    settings = settings,
                    isLoading = isLoading,
                    updateSettings = ::updateSettings,
                    setLoading = ::setLoading,
                    title = SettingCategory.RATELIMIT.categoryName,
                    settingsUpdateTick = settingsUpdateTick
                )

                SettingCategory.INVITES.toString() -> InvitesCategory(
                    settings = settings,
                    isLoading = isLoading,
                    updateSettings = ::updateSettings,
                    setLoading = ::setLoading,
                    title = SettingCategory.INVITES.categoryName,
                    settingsUpdateTick = settingsUpdateTick
                )

                SettingCategory.WEBSITE.toString() -> WebsiteCategory(
                    settings = settings,
                    isLoading = isLoading,
                    updateSettings = ::updateSettings,
                    setLoading = ::setLoading,
                    title = SettingCategory.WEBSITE.categoryName,
                    settingsUpdateTick = settingsUpdateTick,
                    context = context
                )

                SettingCategory.OAUTH.toString() -> OAuthCategory(
                    settings = settings,
                    isLoading = isLoading,
                    updateSettings = ::updateSettings,
                    setLoading = ::setLoading,
                    context = context,
                    title = SettingCategory.OAUTH.categoryName,
                    settingsUpdateTick = settingsUpdateTick
                )

                SettingCategory.PWA.toString() -> PWACategory(
                    settings = settings,
                    isLoading = isLoading,
                    updateSettings = ::updateSettings,
                    setLoading = ::setLoading,
                    title = SettingCategory.PWA.categoryName,
                    settingsUpdateTick = settingsUpdateTick
                )

                SettingCategory.HTTP_WEBHOOKS.toString() -> HTTPWebhooksCategory(
                    settings = settings,
                    isLoading = isLoading,
                    updateSettings = ::updateSettings,
                    setLoading = ::setLoading,
                    title = SettingCategory.HTTP_WEBHOOKS.categoryName,
                    settingsUpdateTick = settingsUpdateTick
                )

                SettingCategory.DOMAINS.toString() -> DomainsCategory(
                    settings = settings,
                    isLoading = isLoading,
                    updateSettings = ::updateSettings,
                    setLoading = ::setLoading,
                    title = SettingCategory.DOMAINS.categoryName,
                    settingsUpdateTick = settingsUpdateTick
                )

                SettingCategory.DISCORD_WEBHOOK.toString() -> DiscordWebhookCategory(
                    settings = settings,
                    isLoading = isLoading,
                    updateSettings = ::updateSettings,
                    setLoading = ::setLoading,
                    title = SettingCategory.DISCORD_WEBHOOK.categoryName,
                    settingsUpdateTick = settingsUpdateTick
                )

                SettingCategory.DISCORD_WEBHOOK_ON_UPLOAD.toString() -> DiscordWebhookOnUploadCategory(
                    settings = settings,
                    isLoading = isLoading,
                    updateSettings = ::updateSettings,
                    setLoading = ::setLoading,
                    title = SettingCategory.DISCORD_WEBHOOK_ON_UPLOAD.categoryName,
                    settingsUpdateTick = settingsUpdateTick
                )

                SettingCategory.DISCORD_WEBHOOK_ON_SHORTEN.toString() -> DiscordWebhookOnShortenCategory(
                    settings = settings,
                    isLoading = isLoading,
                    updateSettings = ::updateSettings,
                    setLoading = ::setLoading,
                    title = SettingCategory.DISCORD_WEBHOOK_ON_SHORTEN.categoryName,
                    settingsUpdateTick = settingsUpdateTick
                )

                else -> CoreCategory(
                    settings = settings,
                    isLoading = isLoading,
                    updateSettings = ::updateSettings,
                    setLoading = ::setLoading,
                    title = SettingCategory.CORE.categoryName,
                    settingsUpdateTick = settingsUpdateTick
                )
            }
        }
    }
}

private enum class SettingCategory(
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

private val settingCategories = listOf(
    SettingCategory.CORE,
    SettingCategory.CHUNKS,
    SettingCategory.TASKS,
    SettingCategory.MFA,
    SettingCategory.FEATURES,
    SettingCategory.FILES,
    SettingCategory.URL_SHORTENER,
    SettingCategory.RATELIMIT,
    SettingCategory.INVITES,
    SettingCategory.WEBSITE,
    SettingCategory.OAUTH,
    SettingCategory.PWA,
    SettingCategory.HTTP_WEBHOOKS,
    SettingCategory.DOMAINS,
    SettingCategory.DISCORD_WEBHOOK,
    SettingCategory.DISCORD_WEBHOOK_ON_UPLOAD,
    SettingCategory.DISCORD_WEBHOOK_ON_SHORTEN,
)