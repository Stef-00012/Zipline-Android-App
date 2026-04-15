package com.stefdp.zipline.screens.settings.categories

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.stefdp.zipline.APP_VERSION
import com.stefdp.zipline.IS_DEBUG
import com.stefdp.zipline.LocalUpdateLoggedUser
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.network.models.User
import com.stefdp.zipline.network.models.UserRole
import com.stefdp.zipline.network.models.responses.GetServerVersionResponse
import com.stefdp.zipline.screens.settings.SettingsUiState
import com.stefdp.zipline.screens.settings.SettingsViewModel
import com.stefdp.zipline.screens.settings.UpdateDownloadFolderType
import com.stefdp.zipline.screens.settings.categories.components.VersionDisplay
import com.stefdp.zipline.ui.theme.DarkGray
import com.stefdp.zipline.ui.theme.getButtonColors
import com.stefdp.zipline.utils.createBiometricPrompt
import com.stefdp.zipline.utils.createPromptInfo
import com.stefdp.zipline.utils.getBiometricStatus
import com.stefdp.zipline.utils.promptBiometricAuthentication

@Composable
internal fun AppSettingsCategory(
    context: Context,
    activity: FragmentActivity,
    user: User?,
    version: GetServerVersionResponse?,
    hasNotificationPermission: Boolean,
    requestNotificationPermission: () -> Unit,
    isLoading: Boolean,
    title: String,
    navController: NavHostController,
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
                    text = AnnotatedString.fromHtml("<b>App Version:</b> $APP_VERSION"),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                )

                val appBuild = if (IS_DEBUG) "Debug" else "Release"

                Text(
                    text = AnnotatedString.fromHtml("<b>App Build:</b> $appBuild"),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Zipline Version:",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold
                    )

                    VersionDisplay(
                        version = version,
                        context = context,
                    )
                }
            }

            val lifecycleOwner = LocalLifecycleOwner.current

            var biometricAuthenticationStatus by rememberSaveable { mutableIntStateOf(getBiometricStatus(context)) }

            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        biometricAuthenticationStatus = getBiometricStatus(context)
                    }
                }

                lifecycleOwner.lifecycle.addObserver(observer)

                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            val enrolBiometricAuthenticationLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) {}

            Switch(
                checked = state.biometricAuthenticationEnabled,
                enabled = !isLoading && (
                        biometricAuthenticationStatus == BiometricManager.BIOMETRIC_SUCCESS || (
                                biometricAuthenticationStatus == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED &&
                                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                                )
                        ),
                onCheckedChange = { checked ->
                    val biometricPrompt = createBiometricPrompt(
                        activity = activity,
                        onSuccess = {
                            viewModel.setBiometricAuthenticationEnabled(
                                context = context,
                                enabled = checked
                            )
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
                        },
                    )

                    val biometricPromptInfo = createPromptInfo(context)

                    promptBiometricAuthentication(
                        activity = activity,
                        prompt = biometricPrompt,
                        promptInfo = biometricPromptInfo,
                        onBiometricNotEnrolledError = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                    putExtra(
                                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                                    )
                                }

                                enrolBiometricAuthenticationLauncher.launch(enrollIntent)
                            }
                        }
                    )
                },
                label = "Unlock with Biometrics",
                description = AnnotatedString.fromHtml("Require biometric authentication to unlock the app.${
                    if (biometricAuthenticationStatus == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) 
                        "<br> <b>NOTE:</b> You will be prompted to enroll an authentication method." 
                    else 
                        ""
                }"),
//                description = AnnotatedString.fromHtml(
//                    stringResource(
//                        R.string.unlock_with_biometrics_switch_description,
//                        if (biometricAuthenticationStatus == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
//                            stringResource(R.string.unlock_with_biometrics_switch_description_enroll_warning)
//                        else ""
//                    )
//                )
            )

            val directoryPicker = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocumentTree()
            ) { uri: Uri? ->
                uri?.let {
                    context.contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )

                    viewModel.updateDownloadFolder(
                        context = context,
                        uri = it
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!hasNotificationPermission) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            requestNotificationPermission()
                        }
                    ) {
                        Text(
                            text = "Grant Notifications Permission"
                        )
                    }
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = getButtonColors().copy(
                        containerColor = DarkGray,
                        disabledContainerColor = DarkGray.copy(alpha = 0.5f)
                    ),
                    onClick = {
                        viewModel.setUpdateDownloadFolderType(UpdateDownloadFolderType.EXPORT)

                        directoryPicker.launch(null)
                    },
                ) {
                    Text(
                        text = "Change Export Download Folder"
                    )
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = getButtonColors().copy(
                        containerColor = DarkGray,
                        disabledContainerColor = DarkGray.copy(alpha = 0.5f)
                    ),
                    onClick = {
                        viewModel.setUpdateDownloadFolderType(UpdateDownloadFolderType.FILE)

                        directoryPicker.launch(null)
                    }
                ) {
                    Text(
                        text = "Change File Download Folder"
                    )
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = getButtonColors().copy(
                        containerColor = DarkGray,
                        disabledContainerColor = DarkGray.copy(alpha = 0.5f)
                    ),
                    onClick = {
                        viewModel.setUpdateDownloadFolderType(UpdateDownloadFolderType.FOLDER_EXPORT)

                        directoryPicker.launch(null)
                    }
                ) {
                    Text(
                        text = "Change Folder Export Download Folder"
                    )
                }

                if (user != null && user.role.level <= UserRole.ADMIN.level) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = getButtonColors().copy(
                            containerColor = DarkGray,
                            disabledContainerColor = DarkGray.copy(alpha = 0.5f)
                        ),
                        onClick = {
                            viewModel.setUpdateDownloadFolderType(UpdateDownloadFolderType.SERVER_EXPORT)

                            directoryPicker.launch(null)
                        }
                    ) {
                        Text(
                            text = "Change Server Export Download Folder"
                        )
                    }
                }

                val localUpdateLoggedUser = LocalUpdateLoggedUser.current

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = getButtonColors().copy(
                        containerColor = MaterialTheme.colorScheme.error,
                        disabledContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                    ),
                    onClick = {
                        viewModel.logout(
                            context = context,
                            navController = navController,
                            localUpdateLoggedUser = localUpdateLoggedUser
                        )
                    }
                ) {
                    Text(
                        text = "Logout"
                    )
                }
            }
        }
    }
}