package com.stefdp.zipline.screens.login

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.LocalLoggedUser
import com.stefdp.zipline.LocalServerVersion
import com.stefdp.zipline.LocalUpdateLoggedUser
import com.stefdp.zipline.LocalUpdateLoggedUserAvatar
import com.stefdp.zipline.LocalUpdatePublicSettings
import com.stefdp.zipline.LocalUpdateServerVersion
import com.stefdp.zipline.LocalUpdateWebSettings
import com.stefdp.zipline.Logger
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.PromptPopup
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextDivider
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.User
import com.stefdp.zipline.screens.*
import com.stefdp.zipline.ui.theme.DarkGray
import com.stefdp.zipline.ui.theme.getButtonColors
import com.stefdp.zipline.utils.NumberRegex
import com.stefdp.zipline.utils.hasNotificationsPermission
import com.stefdp.zipline.utils.minimumZiplineVersion
import io.github.z4kn4fein.semver.toVersionOrNull

@Composable
fun LoginScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    serverUrl: String? = null,
    viewModel: LoginViewModel = viewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val localLoggedUser = LocalLoggedUser.current
    val localServerVersion = LocalServerVersion.current

    val serverVersion = localServerVersion?.details?.version?.toVersionOrNull(
        strict = false
    )

    if (
        localLoggedUser is User &&
        serverVersion != null &&
        serverVersion >= minimumZiplineVersion &&
        currentDestination?.route == LoginScreen::class.qualifiedName
        ) {
        navController.navigate(HomeScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    val state by viewModel.state.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.init(context, serverUrl)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val hasPermission = hasNotificationsPermission(context)

                viewModel.setShowNotificationsPopup(!hasPermission)
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
        if (isGranted) {
            viewModel.closeNotificationsPopup()
        }

        val notificationText = if (isGranted)
            "Notifications Permission Granted"
        else
            "Notifications Permission Denied, please go to the app settings and allow it from there"

        Notification.show(
            activity = activity,
        ) {
            Text(notificationText)
        }
    }

    PromptPopup(
        showPopup = state.showNotificationsPopup,
        onDismissRequest = {
            viewModel.closeNotificationsPopup()
        },
        title = "Notifications Permission",
        description = "The notifications permission is required to properly use background uploads and downloads.\n" +
                "Without this permission, the app will still upload files but it has a higher chance of being killed by the Android system while in the background.",
        successText = "Grant Permission",
        successColor = MaterialTheme.colorScheme.primary,
        onSuccess = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                Notification.show(
                    activity = activity,
                ) {
                    Text("Notifications Permission is automatically granted on this version of Android")
                }
            }
        },
        cancelText = "Not Now",
        cancelColor = MaterialTheme.colorScheme.error,
        onCancel = {
            viewModel.closeNotificationsPopup()
        }
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp),
                )
                .padding(16.dp),
        ) {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier.verticalScroll(scrollState)
            ) {
                AnimatedVisibility(
                    visible = state.isInsecureUrl
                ) {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "Warning: You are using an unencrypted connection. Your password and data may be visible to others on your network. It is recommended to use HTTPS.",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(
                                modifier = Modifier.size(8.dp)
                            )

                            Switch(
                                checked = state.hasAcknowledgedInsecureUrlWarning,
                                onCheckedChange = {
                                    viewModel.setHasAcknowledgedInsecureUrlWarning(it)
                                },
                                label = "I understand the risks",
                                description = "I acknowledge that using an unencrypted connection may expose my password and data to others on the network, and I accept these risks."
                            )
                        }

                        Spacer(
                            modifier = Modifier.size(8.dp)
                        )
                    }
                }

                Text(
                    text = "Login",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )

                TextInput(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.serverUrl,
                    onValueChange = {
                        viewModel.setServerUrl(
                            context = context,
                            url = it
                        )
                    },
                    placeholder = "https://example.com",
                    label = "Zipline URL",
                    enabled = !state.isLoading
                )

                if (state.isTokenLogin) {
                    TextInput(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.token,
                        onValueChange = {
                            viewModel.setToken(it)
                        },
                        isPassword = true,
                        placeholder = "MTc5GzAwNDB5Nzk47A==.OGE4ODN2",
                        label = "Token",
                        enabled = !state.isLoading
                    )
                } else {
                    TextInput(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.username,
                        onValueChange = {
                            viewModel.setUsername(it)
                        },
                        placeholder = "My Username",
                        label = "Username",
                        enabled = !state.isLoading
                    )

                    TextInput(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.password,
                        onValueChange = {
                            viewModel.setPassword(it)
                        },
                        isPassword = true,
                        placeholder = "myCO0lP4ssW0rd!",
                        label = "Password",
                        enabled = !state.isLoading
                    )

                    if (state.isTotpRequired) {
                        TextInput(
                            modifier = Modifier.fillMaxWidth(),
                            value = state.totp,
                            onValueChange = {
                                if (NumberRegex.matches(it.text) && it.text.length <= 6) {
                                    viewModel.setTotp(it)
                                }
                            },
                            placeholder = "123456",
                            label = "TOTP",
                            enabled = !state.isLoading
                        )
                    }
                }

                Switch(
                    label = "Anonymize Device Info",
                    description = "Anonymize the device info sent to Zipline for device sessions management.",
                    checked = state.anonymizeDeviceInfo,
                    onCheckedChange = {
                        viewModel.setAnonymizeDeviceInfo(it)
                    },
                    enabled = !state.isLoading,
                )

                Button(
                    onClick = {
                        viewModel.toggleTokenLogin()
                    },
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 12.dp
                        ),
                    colors = getButtonColors().copy(
                        containerColor = DarkGray,
                        disabledContainerColor = DarkGray.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = "Use ${if (state.isTokenLogin) "Password" else "Token"} Login",
                    )
                }

                val updateServerVersion = LocalUpdateServerVersion.current
                val updateLoggedUser = LocalUpdateLoggedUser.current
                val updateLoggedUserAvatar = LocalUpdateLoggedUserAvatar.current
                val updatePublicSettings = LocalUpdatePublicSettings.current
                val updateWebSettings = LocalUpdateWebSettings.current

                Button(
                    onClick = {
                        viewModel.onLogin(
                            context = context,
                            onSuccess = {
                                if (currentDestination?.route?.startsWith(LoginScreen::class.qualifiedName ?: "") == true) {
                                    navController.navigate(HomeScreen) {
                                        popUpTo(navController.graph.id) { inclusive = true }
                                    }
                                }
                            },
                            onError = { error ->
                                Logger.debug("LoginScreen", "Login error: $error")

                                Notification.show(
                                    activity = activity,
                                    duration = 6000L
                                ) {
                                    Text(
                                        text = error,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            updateServerVersion = updateServerVersion,
                            updateLoggedUser = updateLoggedUser,
                            updateLoggedUserAvatar = updateLoggedUserAvatar,
                            updatePublicSettings = updatePublicSettings,
                            updateWebSettings = updateWebSettings,
                        )
                    },
                    enabled = !state.isLoading && (
                            if (state.isInsecureUrl) state.hasAcknowledgedInsecureUrlWarning
                            else true
                    ) && (
                        if (state.isTokenLogin) state.token.text.isNotBlank()
                        else state.username.text.isNotBlank() && state.password.text.isNotBlank()
                    ),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = LocalContentColor.current,
                            )

                            Spacer(
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = "Login",
                            fontWeight = FontWeight.Bold,
                            color = LocalContentColor.current,
                        )
                    }
                }

                AnimatedVisibility(
                    visible = state.supportsRegistration
                ) {
                    Column {
                        TextDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            text = "or"
                        )

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                navController.navigate(RegisterScreen(serverUrl = state.serverUrl.text)) {
                                    popUpTo(navController.graph.id) { inclusive = true }
                                }
                            }
                        ) {
                            Text(
                                text = "Register",
                                fontWeight = FontWeight.Bold,
                                color = LocalContentColor.current,
                            )
                        }
                    }
                }
            }
        }
    }
}