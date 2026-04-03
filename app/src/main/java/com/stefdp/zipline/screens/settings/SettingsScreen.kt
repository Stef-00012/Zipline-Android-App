package com.stefdp.zipline.screens.settings

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.stefdp.zipline.LocalLoggedUser
import com.stefdp.zipline.LocalUpdateLoggedUser
import com.stefdp.zipline.LocalUpdateLoggedUserAvatar
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.Select
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.network.models.Export
import com.stefdp.zipline.network.models.requests.UpdateCurrentUserBody
import com.stefdp.zipline.network.models.responses.GetServerVersionResponse
import com.stefdp.zipline.network.requests.UpdateCurrentUserResult
import com.stefdp.zipline.network.requests.getCurrentUser
import com.stefdp.zipline.network.requests.getExports
import com.stefdp.zipline.network.requests.getServerVersion
import com.stefdp.zipline.network.requests.getTokenWithToken
import com.stefdp.zipline.network.requests.updateCurrentUser
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.screens.settings.categories.AppSettingsCategory
import com.stefdp.zipline.screens.settings.categories.AvatarCategory
import com.stefdp.zipline.screens.settings.categories.ExportFilesCategory
import com.stefdp.zipline.screens.settings.categories.UserCategory
import com.stefdp.zipline.screens.settings.categories.ViewingFilesCategory

@Composable
fun SettingsScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity
) {
    val localLoggedUser = LocalLoggedUser.current

    if (localLoggedUser == null) {
        navController.navigate(LoginScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    val localUpdateLoggedUser = LocalUpdateLoggedUser.current

    var isLoading by remember { mutableStateOf(false) }

    var token by remember { mutableStateOf(localLoggedUser?.token) }
    var exports by remember { mutableStateOf<List<Export>>(emptyList()) }
    var ziplineVersion by remember { mutableStateOf<GetServerVersionResponse?>(null) }

    suspend fun updateToken() {
        val tokenRes = getTokenWithToken(context)

        tokenRes.onSuccess {
            token = it.token
        }
    }

    suspend fun updateExports() {
        val exportsRes = getExports(context)

        exportsRes.onSuccess {
            exports = it
        }
    }

    suspend fun updateVersion() {
        val versionRes = getServerVersion(context)

        versionRes.onSuccess {
            ziplineVersion = it
        }
    }

    LaunchedEffect(Unit) {
        updateToken()

        updateExports()

        updateVersion()
    }

    var settingsUpdateTick by remember { mutableIntStateOf(0) }

    suspend fun updateCurrentUser(data: UpdateCurrentUserBody? = null): List<String> {
        isLoading = true

        if (data != null) {
            val updateServerSettingsRes = updateCurrentUser(
                context = context,
                data = data
            )

            updateServerSettingsRes
                .onSuccess {
                    if (it is UpdateCurrentUserResult.Error) {
                        return it.error.issues?.map { error -> "${error.instancePath}: ${error.message}." } ?: listOf(it.error.message ?: it.error.error)
                    } else if (it is UpdateCurrentUserResult.Success) {
                        localUpdateLoggedUser()

                        Notification.show(
                            context = context,
                            activity = activity,
                            content = {
                                Text(
                                    text = "Settings updated successfully"
                                )
                            }
                        )

                        settingsUpdateTick += 1
                    }
                }
                .onFailure {
                    return listOf(it.message ?: "Something went wrong...")
                }
        } else {
            localUpdateLoggedUser()

            settingsUpdateTick += 1
        }

        isLoading = false
        return emptyList()
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
                text = "User Settings",
                maxLines = 1,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
            )

//            Row(
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                HeaderButton(
//                    icon = painterResource(R.drawable.refresh),
//                    contentDescription = "Refresh settings",
//                    onClick = {
//                        coroutineScope.launch {
//                            updateSettings()
//                        }
//                    },
//                    enabled = !isLoading,
//                )
//            }
        }

        var selectedCategory by remember {
            mutableStateOf(
                setOf(SettingCategory.USER.toString())
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
                SettingCategory.USER.toString() -> UserCategory(
                    context = context,
                    activity = activity,
                    token = token,
                    settingsUpdateTick = settingsUpdateTick,
                    isLoading = isLoading,
                    updateUser = ::updateCurrentUser,
                    setLoading = { isLoading = it },
                    title = SettingCategory.USER.categoryName,
                    user = localLoggedUser
                )

                SettingCategory.AVATAR.toString() -> AvatarCategory(
                    context = context,
                    activity = activity,
                    updateUser = ::updateCurrentUser,
                    isLoading = isLoading,
                    setLoading = { isLoading = it },
                    title = SettingCategory.AVATAR.categoryName,
                )

                SettingCategory.VIEWING_FILES.toString() -> ViewingFilesCategory(
                    context = context,
                    settingsUpdateTick = settingsUpdateTick,
                    isLoading = isLoading,
                    updateUser = ::updateCurrentUser,
                    setLoading = { isLoading = it },
                    title = SettingCategory.VIEWING_FILES.categoryName,
                    user = localLoggedUser
                )

                SettingCategory.EXPORT_FILES.toString() -> ExportFilesCategory(
                    context = context,
                    activity = activity,
                    settingsUpdateTick = settingsUpdateTick,
                    isLoading = isLoading,
                    setLoading = { isLoading = it },
                    title = SettingCategory.EXPORT_FILES.categoryName,
                    exports = exports,
                    updateExports = ::updateExports
                )

                SettingCategory.APP_SETTINGS.toString() -> AppSettingsCategory(
                    context = context,
                    activity = activity,
                    isLoading = isLoading,
                    title = SettingCategory.APP_SETTINGS.categoryName,
                    version = ziplineVersion,
                    user = localLoggedUser,
                    navController = navController
                )

                else -> UserCategory(
                    context = context,
                    activity = activity,
                    token = token,
                    settingsUpdateTick = settingsUpdateTick,
                    isLoading = isLoading,
                    updateUser = ::updateCurrentUser,
                    setLoading = { isLoading = it },
                    title = SettingCategory.USER.categoryName,
                    user = localLoggedUser
                )
            }
        }
    }
}

private enum class SettingCategory(
    val value: String,
    val categoryName: String
) {
    USER(
        value = "USER",
        categoryName = "User"
    ),

    AVATAR(
        value = "AVATAR",
        categoryName = "Avatar"
    ),

    VIEWING_FILES(
        value = "VIEWING_FILES",
        categoryName = "Viewing Files"
    ),

    EXPORT_FILES(
        value = "EXPORT_FILES",
        categoryName = "Export Files"
    ),

    APP_SETTINGS(
        value = "APP_SETTINGS",
        categoryName = "App Settings"
    );

    override fun toString(): String = value
}

private val settingCategories = listOf(
    SettingCategory.USER,
    SettingCategory.AVATAR,
    SettingCategory.VIEWING_FILES,
    SettingCategory.EXPORT_FILES,
    SettingCategory.APP_SETTINGS
)