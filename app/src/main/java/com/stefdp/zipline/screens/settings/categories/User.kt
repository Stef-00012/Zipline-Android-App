package com.stefdp.zipline.screens.settings.categories

import android.content.ClipData
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.User
import com.stefdp.zipline.network.models.requests.UpdateCurrentUserBody
import com.stefdp.zipline.screens.settings.SettingsUiState
import com.stefdp.zipline.screens.settings.SettingsViewModel
import com.stefdp.zipline.utils.createBiometricPrompt
import com.stefdp.zipline.utils.createPromptInfo
import com.stefdp.zipline.utils.promptBiometricAuthentication
import com.stefdp.zipline.utils.shimmerable
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@Composable
internal fun UserCategory(
    context: Context,
    activity: FragmentActivity,
    user: User?,
    updateUser: (UpdateCurrentUserBody) -> Unit,
    title: String,
    viewModel: SettingsViewModel,
    state: SettingsUiState
) {
    Container(
        scrollable = false,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(12.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )

                Text(
                    text = user?.id ?: "--------------------------------------",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    modifier = Modifier.shimmerable(
                        enabled = user?.id == null,
                        color = MaterialTheme.colorScheme.surface,
                    )
                )
            }

            val clipboardManager = LocalClipboard.current

            val coroutineScope = rememberCoroutineScope()

            suspend fun promptBiometrics(): Boolean = suspendCancellableCoroutine { continuation ->
                val biometricPrompt = createBiometricPrompt(
                    activity = activity,
                    onSuccess = {
                        if (continuation.isActive) continuation.resume(true)
                    },
                    onError = { _, _ ->
                        Notification.show(
                            context = context,
                            activity = activity,
                        ) {
                            Text(
                                text = "Biometric authentication failed"
                            )
                        }

                        continuation.resume(false)
                    },
                )

                val biometricPromptInfo = createPromptInfo(context)

                promptBiometricAuthentication(
                    activity = activity,
                    prompt = biometricPrompt,
                    promptInfo = biometricPromptInfo,
                )
            }

            TextInput(
                value = state.tokenInput,
                onValueChange = {},
                readOnly = true,
                label = "Token",
                enabled = !state.isLoading,
                isPassword = true,
                onPasswordToggle = suspend { isCurrentlyVisible ->
                    return@TextInput if (isCurrentlyVisible || !state.biometricAuthenticationEnabled)
                        true
                    else
                        promptBiometrics()
                },
                modifier = Modifier
                    .fillMaxWidth(),
                leadingIcon = painterResource(R.drawable.content_copy),
                onLeadingIconPress = {
                    coroutineScope.launch {
                        val proceed = if (state.biometricAuthenticationEnabled) {
                            promptBiometrics()
                        } else {
                            true
                        }

                        if (!proceed) return@launch

                        val clipData = ClipData.newPlainText("User Token", state.tokenInput.text)
                            .apply {
                                description.extras = PersistableBundle().apply {
                                    putBoolean("android.content.extra,IS_SENSITIVE", true)
                                }
                            }
                            .toClipEntry()

                        clipboardManager.setClipEntry(clipData)

                        Notification.show(
                            context = context,
                            activity = activity,
                        ) {
                            Text(
                                text = "Token copied to clipboard"
                            )
                        }
                    }
                }
            )

            TextInput(
                value = state.usernameInput,
                onValueChange = {
                    viewModel.setUsernameInput(it)
                },
                label = "Username",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.passwordInput,
                onValueChange = {
                    viewModel.setPasswordInput(it)
                },
                label = "Password",
                description = "Leave blank to keep the same password.",
                isPassword = true,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    coroutineScope.launch {
                        val data = UpdateCurrentUserBody(
                            password = state.passwordInput.text.takeIf { it.isNotEmpty() },
                            username = state.usernameInput.text.takeIf { it.isNotEmpty() && it != user?.username },
                        )

                        updateUser(data)
                    }
                },
                enabled = !state.isLoading
            ) {
                Icon(
                    painter = painterResource(R.drawable.save),
                    contentDescription = "Save"
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Save"
                )
            }
        }
    }
}