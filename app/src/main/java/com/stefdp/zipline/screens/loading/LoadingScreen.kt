package com.stefdp.zipline.screens.loading

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.stefdp.zipline.LocalUpdateLoggedUser
import com.stefdp.zipline.LocalUpdateLoggedUserAvatar
import com.stefdp.zipline.LocalUpdatePublicSettings
import com.stefdp.zipline.LocalUpdateServerVersion
import com.stefdp.zipline.LocalUpdateWebSettings
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.handleSharedIntent
import com.stefdp.zipline.isShareIntent
import com.stefdp.zipline.screens.BiometricAuthScreen
import com.stefdp.zipline.screens.HomeScreen
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.utils.STORAGE_UNLOCK_WITH_BIOMETRICS_KEY
import com.stefdp.zipline.utils.SecureStorage
import com.stefdp.zipline.utils.getBiometricStatus
import com.stefdp.zipline.utils.minimumZiplineVersion
import io.github.z4kn4fein.semver.toVersionOrNull

@Composable
fun LoadingScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
) {
    val updateServerVersion = LocalUpdateServerVersion.current
    val updateLoggedUser = LocalUpdateLoggedUser.current
    val updateLoggedUserAvatar = LocalUpdateLoggedUserAvatar.current
    val updatePublicSettings = LocalUpdatePublicSettings.current
    val updateWebSettings = LocalUpdateWebSettings.current

    val intent = activity.intent

    LaunchedEffect(Unit) {
        val secureStore = SecureStorage.getInstance(context)

        val unlockWithBiometrics = secureStore.get(STORAGE_UNLOCK_WITH_BIOMETRICS_KEY)?.toBoolean() ?: false
        val biometricAuthenticationStatus = getBiometricStatus(context)

        val serverVersionRes = updateServerVersion()

        serverVersionRes
            .onSuccess {
                val version = it.details.version.toVersionOrNull(strict = false)

                if (version == null) {
                    Notification.show(
                        context = context,
                        activity = activity,
                        content = {
                            Text(
                                text = "Failed to fetch server version, make sure you are running Zipline v$minimumZiplineVersion or greater."
                            )
                        }
                    )

                    navController.navigate(LoginScreen) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }

                    return@LaunchedEffect
                } else if (version < minimumZiplineVersion) {
                    Notification.show(
                        context = context,
                        activity = activity,
                        content = {
                            Text(
                                text = "You are currently running Zipline v$version. Please update to at least Zipline v${minimumZiplineVersion}."
                            )
                        }
                    )

                    navController.navigate(LoginScreen) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }

                    return@LaunchedEffect
                }
            }
            .onFailure {
                Notification.show(
                    context = context,
                    activity = activity,
                    content = {
                        Text(
                            text = "Failed to fetch server version, make sure you are running Zipline v$minimumZiplineVersion or greater and have the \"Version Checking\" feature enabled (${it.message})"
                        )
                    }
                )

                navController.navigate(LoginScreen) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }

                return@LaunchedEffect
            }

        val newUserStatsRes = updateLoggedUser()

        newUserStatsRes
            .onFailure {
                navController.navigate(LoginScreen) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }

                return@LaunchedEffect
            }
            .onSuccess {
                updatePublicSettings()
                updateWebSettings()
                updateLoggedUserAvatar()

                if (unlockWithBiometrics && biometricAuthenticationStatus == BiometricManager.BIOMETRIC_SUCCESS) {
                    navController.navigate(BiometricAuthScreen) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                } else {
                    if (isShareIntent(intent)) {
                        handleSharedIntent(intent, navController)

                        activity.intent = null
                    } else {
                        navController.navigate(HomeScreen) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                }
            }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Loading...",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        CircularProgressIndicator()
    }
}