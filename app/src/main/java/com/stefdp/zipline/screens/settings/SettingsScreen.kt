package com.stefdp.zipline.screens.settings

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.zipline.LocalLoggedUser
import com.stefdp.zipline.LocalUpdateLoggedUser
import com.stefdp.zipline.LocalUpdateLoggedUserAvatar
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.Select
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.network.models.requests.UpdateCurrentUserBody
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.screens.settings.categories.AppSettingsCategory
import com.stefdp.zipline.screens.settings.categories.AvatarCategory
import com.stefdp.zipline.screens.settings.categories.ExportFilesCategory
import com.stefdp.zipline.screens.settings.categories.UserCategory
import com.stefdp.zipline.screens.settings.categories.ViewingFilesCategory
import com.stefdp.zipline.utils.hasNotificationsPermission

@Composable
fun SettingsScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: SettingsViewModel = viewModel()
) {
    val localLoggedUser = LocalLoggedUser.current

    if (localLoggedUser == null) {
        navController.navigate(LoginScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    val localUpdateLoggedUser = LocalUpdateLoggedUser.current
    val localUpdateLoggedUserAvatar = LocalUpdateLoggedUserAvatar.current

    val state by viewModel.state.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.setHasNotificationPermission(
                    hasNotificationsPermission(context)
                )
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.setHasNotificationPermission(isGranted)

        val notificationText = if (isGranted)
            "Notifications Permission Granted"
        else
            "Notifications Permission Denied, please go to the app settings and allow it from there"

        Notification.show(
            context = context,
            activity = activity,
        ) {
            Text(notificationText)
        }
    }

    fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            Notification.show(
                context = context,
                activity = activity,
            ) {
                Text("Notifications Permission is automatically granted on this version of Android")
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.initData(context)
    }

    LaunchedEffect(state.settingsUpdateTick, localLoggedUser) {
        viewModel.resetInputs(localLoggedUser)
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

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(
                    horizontal = 12.dp,
                ),
        ) {
            fun updateUser(
                data: UpdateCurrentUserBody? = null,
                updateAvatar: Boolean = false,
            ) {
                viewModel.updateUser(
                    context = context,
                    data = data,
                    updateAvatar = updateAvatar,
                    localUpdateLoggedUser = localUpdateLoggedUser,
                    localUpdateLoggedUserAvatar = localUpdateLoggedUserAvatar,
                    onError = { errors ->
                        Notification.show(
                            context = context,
                            activity = activity,
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("Failed to update user settings:")

                                errors.forEach {
                                    Text(
                                        text = it,
                                        color = MaterialTheme.colorScheme.error,
                                    )
                                }
                            }
                        }
                    },
                    onSuccess = {
                        Notification.show(
                            context = context,
                            activity = activity,
                        ) {
                            Text("User settings updated successfully")
                        }
                    }
                )
            }

            when (state.selectedCategory.first()) {
                SettingCategory.USER.toString() -> UserCategory(
                    context = context,
                    activity = activity,
                    updateUser = { updateUser(it) },
                    title = SettingCategory.USER.categoryName,
                    user = localLoggedUser,
                    viewModel = viewModel,
                    state = state
                )

                SettingCategory.AVATAR.toString() -> AvatarCategory(
                    context = context,
                    activity = activity,
                    updateUser = {
                        updateUser(
                            data = it,
                            updateAvatar = true
                        )
                    },
                    title = SettingCategory.AVATAR.categoryName,
                    viewModel = viewModel,
                    state = state
                )

                SettingCategory.VIEWING_FILES.toString() -> ViewingFilesCategory(
                    updateUser = { updateUser(it) },
                    title = SettingCategory.VIEWING_FILES.categoryName,
                    viewModel = viewModel,
                    state = state
                )

                SettingCategory.EXPORT_FILES.toString() -> ExportFilesCategory(
                    context = context,
                    activity = activity,
                    title = SettingCategory.EXPORT_FILES.categoryName,
                    viewModel = viewModel,
                    state = state
                )

                SettingCategory.APP_SETTINGS.toString() -> AppSettingsCategory(
                    context = context,
                    activity = activity,
                    isLoading = state.isLoading,
                    title = SettingCategory.APP_SETTINGS.categoryName,
                    version = state.ziplineVersion,
                    user = localLoggedUser,
                    navController = navController,
                    hasNotificationPermission = state.hasNotificationPermission,
                    requestNotificationPermission = ::requestNotificationPermission,
                    viewModel = viewModel,
                    state = state
                )

                else -> {
                    viewModel.setSelectedCategory(
                        setOf(SettingCategory.USER.toString())
                    )
                }
            }
        }
    }
}

private val settingCategories = listOf(
    SettingCategory.USER,
    SettingCategory.AVATAR,
    SettingCategory.VIEWING_FILES,
    SettingCategory.EXPORT_FILES,
    SettingCategory.APP_SETTINGS
)