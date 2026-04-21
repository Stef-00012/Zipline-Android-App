package com.stefdp.zipline.screens.admin.users.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.R
import com.stefdp.zipline.components.AvatarInput
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.components.Select
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.User
import com.stefdp.zipline.network.models.UserQuotaFilesQuota
import com.stefdp.zipline.network.models.UserRole
import com.stefdp.zipline.screens.admin.users.AdminUsersUiState
import com.stefdp.zipline.screens.admin.users.AdminUsersViewModel
import com.stefdp.zipline.utils.NumberRegex
import com.stefdp.zipline.utils.ZiplineViewStateType

@Composable
fun EditUserPopup(
    context: Context,
    activity: FragmentActivity,
    currentUser: User?,
    showPopup: Boolean,
    onDismissRequest: () -> Unit,
    viewModel: AdminUsersViewModel,
    state: AdminUsersUiState,
    viewState: ZiplineViewStateType
) {
    Popup(
        showPopup = showPopup,
        onDismissRequest = onDismissRequest,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Edit ${state.editUser?.username ?: "User"}",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
            )

            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(
                        onClick = {
                            onDismissRequest()
                        }
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.close),
                    contentDescription = "Close edit user menu",
                )
            }
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Any fields that are blank will be omitted, and will not be updated.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            ),
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextInput(
            value = state.editUserUsername,
            onValueChange = {
                viewModel.setEditUserUsername(it)
            },
            modifier = Modifier.fillMaxWidth(),
            label = "Username",
            placeholder = "Enter a username...",
            enabled = !state.isLoading
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextInput(
            value = state.editUserPassword,
            onValueChange = {
                viewModel.setEditUserPassword(it)
            },
            modifier = Modifier.fillMaxWidth(),
            label = "Password",
            enabled = !state.isLoading,
            placeholder = "Enter a password...",
            isPassword = true
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        AvatarInput(
            context = context,
            activity = activity,
            onAvatarChange = {
                viewModel.setEditUserAvatar(it)
            },
            modifier = Modifier.fillMaxWidth(),
            label = "Avatar",
            enabled = !state.isLoading,
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Select(
            options = listOf(
                SelectOption(
                    id = UserRole.USER.toString(),
                    label = {
                        Text(
                            text = UserRole.USER.roleName
                        )
                    }
                ),
                SelectOption(
                    id = UserRole.ADMIN.toString(),
                    label = {
                        Text(
                            text = UserRole.ADMIN.roleName
                        )
                    },
                    enabled = currentUser?.role == UserRole.SUPERADMIN
                )
            ),
            enabled = !state.isLoading,
            selectedIds = state.editUserSelectedRole,
            onSelectionChange = {
                viewModel.setEditUserSelectedRole(it)
            }
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Quota",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Select(
            label = "File Quota Type",
            description = "Whether to set a quota on files by total bytes or the total number of files.",
            options = listOf(
                SelectOption(
                    id = UserQuotaFilesQuota.BY_BYTES.toString(),
                    label = {
                        Text(
                            text = "By Bytes"
                        )
                    }
                ),
                SelectOption(
                    id = UserQuotaFilesQuota.BY_FILES.toString(),
                    label = {
                        Text(
                            text = "By File Count"
                        )
                    }
                ),
                SelectOption(
                    id = UserQuotaFilesQuota.NONE.toString(),
                    label = {
                        Text(
                            text = "No Files Quota"
                        )
                    }
                ),
            ),
            enabled = !state.isLoading,
            selectedIds = state.editUserSelectedFilesQuota,
            onSelectionChange = {
                viewModel.setEditUserSelectedFilesQuota(it)
            }
        )

        val extraMenuQuotas = listOf(
            UserQuotaFilesQuota.BY_FILES,
            UserQuotaFilesQuota.BY_BYTES,
        )

        if (UserQuotaFilesQuota.valueOf(state.editUserSelectedFilesQuota.firstOrNull() ?: UserQuotaFilesQuota.NONE.toString()) in extraMenuQuotas) {
            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth()
            ) {
                when (UserQuotaFilesQuota.valueOf(state.editUserSelectedFilesQuota.firstOrNull() ?: UserQuotaFilesQuota.NONE.toString())) {
                    UserQuotaFilesQuota.BY_BYTES -> {
                        TextInput(
                            value = state.editUserMaxBytes,
                            onValueChange = {
                                viewModel.setEditUserMaxBytes(it)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = "Max Bytes",
                            description = "The maximum number of bytes the user can upload.",
                            placeholder = "Enter a human readable byte-format...",
                            enabled = !state.isLoading
                        )
                    }

                    UserQuotaFilesQuota.BY_FILES -> {
                        TextInput(
                            value = state.editUserMaxFileCount,
                            onValueChange = {
                                if (NumberRegex.matches(it.text)) {
                                    viewModel.setEditUserMaxFileCount(it)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = "Max Files",
                            description = "The maximum number of files the user can upload.",
                            placeholder = "Enter a number...",
                            enabled = !state.isLoading
                        )
                    }

                    UserQuotaFilesQuota.NONE -> {}
                }
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextInput(
            value = state.editUserMaxUrls,
            onValueChange = {
                    if (NumberRegex.matches(it.text)) {
                        viewModel.setEditUserMaxUrls(it)
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            label = "Max URLs",
            description = "The maximum number of URLs the user can create. Leave as 0 for unlimited.",
            placeholder = "Enter a number...",
            enabled = !state.isLoading
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        val isButtonEnabled = !state.isLoading && (
                state.editUserUsername.text.isNotBlank() ||
                state.editUserPassword.text.isNotBlank() ||
                (
                        state.editUserSelectedFilesQuota.firstOrNull() == UserQuotaFilesQuota.BY_BYTES.toString() &&
                        state.editUserMaxBytes.text.isNotBlank()
                ) || (
                        state.editUserSelectedFilesQuota.firstOrNull() == UserQuotaFilesQuota.BY_FILES.toString() &&
                        state.editUserMaxFileCount.text.isNotBlank()
                )
        )

        Button(
            enabled = isButtonEnabled,
            onClick = {
                viewModel.editUser(
                    context = context,
                    viewState = viewState,
                    onSuccess = {
                        Notification.show(
                            activity = activity,
                        ) {
                            Text(
                                text = "User edited successfully"
                            )
                        }
                    },
                    onError = { error ->
                        Notification.show(
                            activity = activity,
                        ) {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Update User")
        }
    }
}