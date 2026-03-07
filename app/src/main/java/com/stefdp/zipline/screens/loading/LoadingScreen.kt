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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.stefdp.zipline.LocalUpdateLoggedUser
import com.stefdp.zipline.R
import com.stefdp.zipline.screens.BiometricAuthScreen
import com.stefdp.zipline.screens.HomeScreen
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.utils.SecureStorage
import com.stefdp.zipline.utils.getBiometricStatus

@Composable
fun LoadingScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity
) {
    val updateLoggedUser = LocalUpdateLoggedUser.current

    LaunchedEffect(Unit) {
        val secureStore = SecureStorage.getInstance(context)

        val unlockWithBiometrics = secureStore.get("unlockWithBiometrics")?.toBoolean() ?: false
        val biometricAuthenticationStatus = getBiometricStatus(context)

        val newUserStatsRes = updateLoggedUser()

        newUserStatsRes
            .onFailure {
                navController.navigate(LoginScreen) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
            .onSuccess {
                if (unlockWithBiometrics && biometricAuthenticationStatus == BiometricManager.BIOMETRIC_SUCCESS) {
                    navController.navigate(BiometricAuthScreen) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                } else {
                    navController.navigate(HomeScreen) {
                        popUpTo(navController.graph.id) { inclusive = true }
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
            text = "Loading...",//stringResource(R.string.loading_title),
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