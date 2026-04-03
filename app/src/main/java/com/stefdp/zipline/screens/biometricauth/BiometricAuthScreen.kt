package com.stefdp.zipline.screens.biometricauth

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.screens.HomeScreen
import com.stefdp.zipline.utils.createBiometricPrompt
import com.stefdp.zipline.utils.createPromptInfo
import com.stefdp.zipline.utils.promptBiometricAuthentication

@Composable
fun BiometricAuthScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity
) {
    fun promptBiometrics() {
        val biometricPrompt = createBiometricPrompt(
            activity = activity,
            onSuccess = {
                navController.navigate(HomeScreen) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            },
            onError = { _, _ ->
                Notification.show(
                    context = context,
                    activity = activity,
                    content = {
                        Text(
                            text = "Biometric authentication failed"
                        )
                    }
                )
            },
        )

        val biometricPromptInfo = createPromptInfo(context)

        promptBiometricAuthentication(
            activity = activity,
            prompt = biometricPrompt,
            promptInfo = biometricPromptInfo,
        )
    }

    LaunchedEffect(Unit) {
        promptBiometrics()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.lock),
            contentDescription = "Zipline Locked",
            modifier = Modifier.size(50.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Zipline Locked",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(
            modifier = Modifier.weight(0.7f)
        )

        TextButton(
            onClick = ::promptBiometrics
        ) {
            Text(
                text = "Unlock",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )
    }
}