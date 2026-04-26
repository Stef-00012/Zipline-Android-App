package com.stefdp.zipline.screens.admin.settings

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.zipline.LocalLoggedUser
import com.stefdp.zipline.LocalUpdatePublicSettings
import com.stefdp.zipline.LocalUpdateWebSettings
import com.stefdp.zipline.R
import com.stefdp.zipline.components.HeaderButton
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.Select
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.network.models.UserRole
import com.stefdp.zipline.screens.HomeScreen
import com.stefdp.zipline.screens.LoginScreen
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

@Composable
fun AdminSettingsScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: AdminSettingsViewModel = viewModel()
) {
    val localLoggedUser = LocalLoggedUser.current

    if (localLoggedUser == null) {
        navController.navigate(LoginScreen()) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    localLoggedUser?.role?.level?.let {
        if (it > UserRole.SUPERADMIN.level) {
            navController.navigate(HomeScreen) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    val state by viewModel.state.collectAsState()

    val updateWebSettings = LocalUpdateWebSettings.current
    val updatePublicSettings = LocalUpdatePublicSettings.current

    LaunchedEffect(Unit) {
        viewModel.refreshSettings(
            context = context,
            onSuccess = {},
            onError = { errors ->
                Notification.show(
                    activity = activity,
                    duration = 8000L,
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Failed to fetch server settings:")

                        errors.forEach {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
            }
        )
    }

    LaunchedEffect(state.settingsUpdateTick) {
        viewModel.resetInputs()
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
                        viewModel.refreshSettings(
                            context = context,
                            onSuccess = {
                                Notification.show(
                                    activity = activity,
                                ) {
                                    Text(
                                        text = "Settings fetched successfully"
                                    )
                                }
                            },
                            onError = { errors ->
                                Notification.show(
                                    activity = activity,
                                    duration = 8000L,
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("Failed to fetch server settings:")

                                        errors.forEach {
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    },
                    enabled = !state.isLoading,
                )
            }
        }

        Select(
            onSelectionChange = {
                viewModel.setSelectedCategory(it)
            },
            selectedIds = state.selectedCategory,
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
                    enabled = state.selectedCategory.first() != category.toString()
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
            when (state.selectedCategory.first()) {
                SettingCategory.CORE.toString() -> CoreCategory(
                    updateSettings = { data ->
                        viewModel.updateCoreSettings(
                            context = context,
                            updateWebSettings = updateWebSettings,
                            updatePublicSettings = updatePublicSettings,
                            data = data,
                            onSuccess = {
                                Notification.show(
                                    activity = activity,
                                ) {
                                    Text(
                                        text = "Settings updated successfully"
                                    )
                                }
                            },
                            onError = { errors ->
                                Notification.show(
                                    activity = activity,
                                    duration = 8000L,
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("Failed to update server settings:")

                                        errors.forEach {
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    },
                    title = SettingCategory.CORE.categoryName,
                    viewModel = viewModel,
                    state = state
                )

                SettingCategory.CHUNKS.toString() -> ChunksCategory(
                    updateSettings = { data ->
                        viewModel.updateChunksSettings(
                            context = context,
                            updateWebSettings = updateWebSettings,
                            updatePublicSettings = updatePublicSettings,
                            data = data,
                            onSuccess = {
                                Notification.show(
                                    activity = activity,
                                ) {
                                    Text(
                                        text = "Settings updated successfully"
                                    )
                                }
                            },
                            onError = { errors ->
                                Notification.show(
                                    activity = activity,
                                    duration = 8000L,
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("Failed to update server settings:")

                                        errors.forEach {
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    },
                    title = SettingCategory.CHUNKS.categoryName,
                    viewModel = viewModel,
                    state = state
                )

                SettingCategory.TASKS.toString() -> TasksCategory(
                    updateSettings = { data ->
                        viewModel.updateTasksSettings(
                            context = context,
                            updateWebSettings = updateWebSettings,
                            updatePublicSettings = updatePublicSettings,
                            data = data,
                            onSuccess = {
                                Notification.show(
                                    activity = activity,
                                ) {
                                    Text(
                                        text = "Settings updated successfully"
                                    )
                                }
                            },
                            onError = { errors ->
                                Notification.show(
                                    activity = activity,
                                    duration = 8000L,
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("Failed to update server settings:")

                                        errors.forEach {
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    },
                    title = SettingCategory.TASKS.categoryName,
                    viewModel = viewModel,
                    state = state
                )

                SettingCategory.MFA.toString() -> MFACategory(
                    updateSettings = { data ->
                        viewModel.updateMfaSettings(
                            context = context,
                            updateWebSettings = updateWebSettings,
                            updatePublicSettings = updatePublicSettings,
                            data = data,
                            onSuccess = {
                                Notification.show(
                                    activity = activity,
                                ) {
                                    Text(
                                        text = "Settings updated successfully"
                                    )
                                }
                            },
                            onError = { errors ->
                                Notification.show(
                                    activity = activity,
                                    duration = 8000L,
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("Failed to update server settings:")

                                        errors.forEach {
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    },
                    title = SettingCategory.MFA.categoryName,
                    viewModel = viewModel,
                    state = state
                )

                SettingCategory.FEATURES.toString() -> FeaturesCategory(
                    updateSettings = { data ->
                        viewModel.updateFeaturesSettings(
                            context = context,
                            updateWebSettings = updateWebSettings,
                            updatePublicSettings = updatePublicSettings,
                            data = data,
                            onSuccess = {
                                Notification.show(
                                    activity = activity,
                                ) {
                                    Text(
                                        text = "Settings updated successfully"
                                    )
                                }
                            },
                            onError = { errors ->
                                Notification.show(
                                    activity = activity,
                                    duration = 8000L,
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("Failed to update server settings:")

                                        errors.forEach {
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    },
                    title = SettingCategory.FEATURES.categoryName,
                    viewModel = viewModel,
                    state = state
                )

                SettingCategory.FILES.toString() -> FilesCategory(
                    updateSettings = { data ->
                        viewModel.updateFilesSettings(
                            context = context,
                            updateWebSettings = updateWebSettings,
                            updatePublicSettings = updatePublicSettings,
                            data = data,
                            onSuccess = {
                                Notification.show(
                                    activity = activity,
                                ) {
                                    Text(
                                        text = "Settings updated successfully"
                                    )
                                }
                            },
                            onError = { errors ->
                                Notification.show(
                                    activity = activity,
                                    duration = 8000L,
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("Failed to update server settings:")

                                        errors.forEach {
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    },
                    title = SettingCategory.FILES.categoryName,
                    viewModel = viewModel,
                    state = state
                )

                SettingCategory.URL_SHORTENER.toString() -> UrlShortenerCategory(
                    updateSettings = { data ->
                        viewModel.updateUrlsSettings(
                            context = context,
                            updateWebSettings = updateWebSettings,
                            updatePublicSettings = updatePublicSettings,
                            data = data,
                            onSuccess = {
                                Notification.show(
                                    activity = activity,
                                ) {
                                    Text(
                                        text = "Settings updated successfully"
                                    )
                                }
                            },
                            onError = { errors ->
                                Notification.show(
                                    activity = activity,
                                    duration = 8000L,
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("Failed to update server settings:")

                                        errors.forEach {
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    },
                    title = SettingCategory.URL_SHORTENER.categoryName,
                    viewModel = viewModel,
                    state = state
                )

                SettingCategory.RATELIMIT.toString() -> RatelimitCategory(
                    updateSettings = { data ->
                        viewModel.updateRatelimitSettings(
                            context = context,
                            updateWebSettings = updateWebSettings,
                            updatePublicSettings = updatePublicSettings,
                            data = data,
                            onSuccess = {
                                Notification.show(
                                    activity = activity,
                                ) {
                                    Text(
                                        text = "Settings updated successfully"
                                    )
                                }
                            },
                            onError = { errors ->
                                Notification.show(
                                    activity = activity,
                                    duration = 8000L,
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("Failed to update server settings:")

                                        errors.forEach {
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    },
                    title = SettingCategory.RATELIMIT.categoryName,
                    viewModel = viewModel,
                    state = state
                )

                SettingCategory.INVITES.toString() -> InvitesCategory(
                    updateSettings = { data ->
                        viewModel.updateInvitesSettings(
                            context = context,
                            updateWebSettings = updateWebSettings,
                            updatePublicSettings = updatePublicSettings,
                            data = data,
                            onSuccess = {
                                Notification.show(
                                    activity = activity,
                                ) {
                                    Text(
                                        text = "Settings updated successfully"
                                    )
                                }
                            },
                            onError = { errors ->
                                Notification.show(
                                    activity = activity,
                                    duration = 8000L,
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("Failed to update server settings:")

                                        errors.forEach {
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    },
                    title = SettingCategory.INVITES.categoryName,
                    viewModel = viewModel,
                    state = state
                )

                SettingCategory.WEBSITE.toString() -> WebsiteCategory(
                    updateSettings = { data ->
                        viewModel.updateWebsiteSettings(
                            context = context,
                            updateWebSettings = updateWebSettings,
                            updatePublicSettings = updatePublicSettings,
                            data = data,
                            onSuccess = {
                                Notification.show(
                                    activity = activity,
                                ) {
                                    Text(
                                        text = "Settings updated successfully"
                                    )
                                }
                            },
                            onError = { errors ->
                                Notification.show(
                                    activity = activity,
                                    duration = 8000L,
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("Failed to update server settings:")

                                        errors.forEach {
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    },
                    title = SettingCategory.WEBSITE.categoryName,
                    viewModel = viewModel,
                    state = state
                )

                SettingCategory.OAUTH.toString() -> OAuthCategory(
                    updateSettings = { data ->
                        viewModel.updateOauthSettings(
                            context = context,
                            updateWebSettings = updateWebSettings,
                            updatePublicSettings = updatePublicSettings,
                            data = data,
                            onSuccess = {
                                Notification.show(
                                    activity = activity,
                                ) {
                                    Text(
                                        text = "Settings updated successfully"
                                    )
                                }
                            },
                            onError = { errors ->
                                Notification.show(
                                    activity = activity,
                                    duration = 8000L,
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("Failed to update server settings:")

                                        errors.forEach {
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    },
                    context = context,
                    activity = activity,
                    title = SettingCategory.OAUTH.categoryName,
                    viewModel = viewModel,
                    state = state
                )

                SettingCategory.PWA.toString() -> PWACategory(
                    updateSettings = { data ->
                        viewModel.updatePwaSettings(
                            context = context,
                            updateWebSettings = updateWebSettings,
                            updatePublicSettings = updatePublicSettings,
                            data = data,
                            onSuccess = {
                                Notification.show(
                                    activity = activity,
                                ) {
                                    Text(
                                        text = "Settings updated successfully"
                                    )
                                }
                            },
                            onError = { errors ->
                                Notification.show(
                                    activity = activity,
                                    duration = 8000L,
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("Failed to update server settings:")

                                        errors.forEach {
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    },
                    title = SettingCategory.PWA.categoryName,
                    viewModel = viewModel,
                    state = state
                )

                SettingCategory.HTTP_WEBHOOKS.toString() -> HTTPWebhooksCategory(
                    updateSettings = { data ->
                        viewModel.updateWebhooksSettings(
                            context = context,
                            updateWebSettings = updateWebSettings,
                            updatePublicSettings = updatePublicSettings,
                            data = data,
                            onSuccess = {
                                Notification.show(
                                    activity = activity,
                                ) {
                                    Text(
                                        text = "Settings updated successfully"
                                    )
                                }
                            },
                            onError = { errors ->
                                Notification.show(
                                    activity = activity,
                                    duration = 8000L,
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("Failed to update server settings:")

                                        errors.forEach {
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    },
                    title = SettingCategory.HTTP_WEBHOOKS.categoryName,
                    viewModel = viewModel,
                    state = state
                )

                SettingCategory.DOMAINS.toString() -> DomainsCategory(
                    updateSettings = { data ->
                        viewModel.updateDomainsSettings(
                            context = context,
                            updateWebSettings = updateWebSettings,
                            updatePublicSettings = updatePublicSettings,
                            data = data,
                            onSuccess = {
                                Notification.show(
                                    activity = activity,
                                ) {
                                    Text(
                                        text = "Settings updated successfully"
                                    )
                                }
                            },
                            onError = { errors ->
                                Notification.show(
                                    activity = activity,
                                    duration = 8000L,
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("Failed to update server settings:")

                                        errors.forEach {
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    },
                    title = SettingCategory.DOMAINS.categoryName,
                    viewModel = viewModel,
                    state = state
                )

                SettingCategory.DISCORD_WEBHOOK.toString() -> DiscordWebhookCategory(
                    updateSettings = { data ->
                        viewModel.updateDiscordSettings(
                            context = context,
                            updateWebSettings = updateWebSettings,
                            updatePublicSettings = updatePublicSettings,
                            data = data,
                            onSuccess = {
                                Notification.show(
                                    activity = activity,
                                ) {
                                    Text(
                                        text = "Settings updated successfully"
                                    )
                                }
                            },
                            onError = { errors ->
                                Notification.show(
                                    activity = activity,
                                    duration = 8000L,
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("Failed to update server settings:")

                                        errors.forEach {
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    },
                    title = SettingCategory.DISCORD_WEBHOOK.categoryName,
                    viewModel = viewModel,
                    state = state
                )

                SettingCategory.DISCORD_WEBHOOK_ON_UPLOAD.toString() -> DiscordWebhookOnUploadCategory(
                    updateSettings = { data ->
                        viewModel.updateDiscordOnUploadSettings(
                            context = context,
                            updateWebSettings = updateWebSettings,
                            updatePublicSettings = updatePublicSettings,
                            data = data,
                            onSuccess = {
                                Notification.show(
                                    activity = activity,
                                ) {
                                    Text(
                                        text = "Settings updated successfully"
                                    )
                                }
                            },
                            onError = { errors ->
                                Notification.show(
                                    activity = activity,
                                    duration = 8000L,
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("Failed to update server settings:")

                                        errors.forEach {
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    },
                    title = SettingCategory.DISCORD_WEBHOOK_ON_UPLOAD.categoryName,
                    viewModel = viewModel,
                    state = state
                )

                SettingCategory.DISCORD_WEBHOOK_ON_SHORTEN.toString() -> DiscordWebhookOnShortenCategory(
                    updateSettings = { data ->
                        viewModel.updateDiscordOnShortenSettings(
                            context = context,
                            updateWebSettings = updateWebSettings,
                            updatePublicSettings = updatePublicSettings,
                            data = data,
                            onSuccess = {
                                Notification.show(
                                    activity = activity,
                                ) {
                                    Text(
                                        text = "Settings updated successfully"
                                    )
                                }
                            },
                            onError = { errors ->
                                Notification.show(
                                    activity = activity,
                                    duration = 8000L,
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("Failed to update server settings:")

                                        errors.forEach {
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    },
                    title = SettingCategory.DISCORD_WEBHOOK_ON_SHORTEN.categoryName,
                    viewModel = viewModel,
                    state = state
                )

                else -> {
                    viewModel.setSelectedCategory(
                        setOf(SettingCategory.CORE.toString())
                    )
                }
            }
        }
    }
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