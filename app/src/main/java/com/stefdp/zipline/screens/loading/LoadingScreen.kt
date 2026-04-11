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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.stefdp.zipline.utils.STORAGE_SERVER_URL_KEY
import com.stefdp.zipline.utils.STORAGE_TOKEN_KEY
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
    viewModel: LoadingViewModel = viewModel()
) {
    val updateServerVersion = LocalUpdateServerVersion.current
    val updateLoggedUser = LocalUpdateLoggedUser.current
    val updateLoggedUserAvatar = LocalUpdateLoggedUserAvatar.current
    val updatePublicSettings = LocalUpdatePublicSettings.current
    val updateWebSettings = LocalUpdateWebSettings.current

    val state by viewModel.state.collectAsState()

    val intent = activity.intent

    LaunchedEffect(Unit) {
        if (state.isLogging) return@LaunchedEffect

        viewModel.startLoading(
            context = context,
            onError = { error ->
                if (error != null) {
                    Notification.show(
                        context = context,
                        activity = activity,
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                navController.navigate(LoginScreen) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            },
            onSuccess = { goToBiometrics ->
                if (goToBiometrics) {
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
            },
            updateServerVersion = updateServerVersion,
            updateLoggedUser = updateLoggedUser,
            updatePublicSettings = updatePublicSettings,
            updateWebSettings = updateWebSettings,
            updateLoggedUserAvatar = updateLoggedUserAvatar
        )
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