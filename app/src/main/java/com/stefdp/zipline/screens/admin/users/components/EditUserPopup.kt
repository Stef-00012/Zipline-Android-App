package com.stefdp.zipline.screens.admin.users.components

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.LocalLoggedUser
import com.stefdp.zipline.Logger
import com.stefdp.zipline.R
import com.stefdp.zipline.components.AvatarInput
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.components.Select
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.User
import com.stefdp.zipline.network.models.UserQuotaFilesQuota
import com.stefdp.zipline.network.models.UserRole
import com.stefdp.zipline.network.models.requests.UpdateUserBodyQuota
import com.stefdp.zipline.network.requests.createUser
import com.stefdp.zipline.network.requests.updateUser
import com.stefdp.zipline.utils.NumberRegex
import kotlinx.coroutines.launch

@Composable
fun EditUserPopup(
    context: Context,
    activity: FragmentActivity,
    currentUser: User?,
    user: User?,
    showPopup: Boolean,
    onDismissRequest: () -> Unit,
    updateUsers: suspend () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

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
                text = "Edit ${user?.username ?: "User"}",
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

        if (errorMessage != null) {
            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        var username by remember { mutableStateOf(TextFieldValue(user?.username ?: "")) }

        TextInput(
            value = username,
            onValueChange = { username = it },
            modifier = Modifier.fillMaxWidth(),
            label = "Username",
            placeholder = "Enter a username...",
            enabled = !isLoading
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        var password by remember { mutableStateOf(TextFieldValue("")) }

        TextInput(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = "Password",
            enabled = !isLoading,
            placeholder = "Enter a password...",
            isPassword = true
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        var avatar by remember { mutableStateOf(user?.avatar) }

        AvatarInput(
            context = context,
            activity = activity,
            onAvatarChange = { avatar = it },
            modifier = Modifier.fillMaxWidth(),
            label = "Avatar",
            enabled = !isLoading,
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        var selectedRole by remember { mutableStateOf(setOf((user?.role ?: UserRole.USER).toString())) }

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
            selectedIds = selectedRole,
            onSelectionChange = { selectedRole = it }
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

        val currentUserQuota = when (user?.quota?.filesQuota) {
            UserQuotaFilesQuota.BY_BYTES if !user.quota.maxBytes.isNullOrBlank() -> UserQuotaFilesQuota.BY_BYTES
            UserQuotaFilesQuota.BY_FILES if user.quota.maxFiles != null -> UserQuotaFilesQuota.BY_FILES
            UserQuotaFilesQuota.NONE -> UserQuotaFilesQuota.NONE
            null -> UserQuotaFilesQuota.NONE
            else -> UserQuotaFilesQuota.NONE
        }
        var selectedFilesQuota by remember { mutableStateOf(setOf(currentUserQuota.toString())) }

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
            selectedIds = selectedFilesQuota,
            onSelectionChange = { selectedFilesQuota = it }
        )

        val extraMenuQuotas = listOf(
            UserQuotaFilesQuota.BY_FILES,
            UserQuotaFilesQuota.BY_BYTES,
        )

        var maxBytes by remember { mutableStateOf(TextFieldValue(user?.quota?.maxBytes ?: "")) }
        var maxFileCount by remember { mutableStateOf(TextFieldValue((user?.quota?.maxFiles ?: "").toString())) }

        if (UserQuotaFilesQuota.valueOf(selectedFilesQuota.firstOrNull() ?: UserQuotaFilesQuota.NONE.toString()) in extraMenuQuotas) {
            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth()
            ) {
                when (UserQuotaFilesQuota.valueOf(selectedFilesQuota.firstOrNull() ?: UserQuotaFilesQuota.NONE.toString())) {
                    UserQuotaFilesQuota.BY_BYTES -> {
                        TextInput(
                            value = maxBytes,
                            onValueChange = { maxBytes = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = "Max Bytes",
                            description = "The maximum number of bytes the user can upload.",
                            placeholder = "Enter a human readable byte-format...",
                            enabled = !isLoading
                        )
                    }

                    UserQuotaFilesQuota.BY_FILES -> {
                        TextInput(
                            value = maxFileCount,
                            onValueChange = {
                                if (NumberRegex.matches(it.text)) {
                                    maxFileCount = it
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = "Max Files",
                            description = "The maximum number of files the user can upload.",
                            placeholder = "Enter a number...",
                            enabled = !isLoading
                        )
                    }

                    UserQuotaFilesQuota.NONE -> {}
                }
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        var maxUrls by remember { mutableStateOf(TextFieldValue((user?.quota?.maxUrls ?: "").toString())) }

        TextInput(
            value = maxUrls,
            onValueChange = {
                    if (NumberRegex.matches(it.text)) {
                        maxUrls = it
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            label = "Max URLs",
            description = "The maximum number of URLs the user can create. Leave as 0 for unlimited.",
            placeholder = "Enter a number...",
            enabled = !isLoading
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            enabled = !isLoading,
            onClick = {
                coroutineScope.launch {
                    if (user == null) {
                        return@launch
                    }

                    isLoading = true

                    val userRole = UserRole.valueOf(selectedRole.firstOrNull() ?: UserRole.USER.toString())

                    val userQuota = UserQuotaFilesQuota.valueOf(selectedFilesQuota.firstOrNull() ?: UserQuotaFilesQuota.NONE.toString())

                    val userQuotaBody = when (userQuota) {
                        UserQuotaFilesQuota.BY_BYTES -> UpdateUserBodyQuota(
                            filesType = userQuota,
                            maxBytes = maxBytes.text.ifBlank { null },
                            maxFiles = null,
                            maxUrls = if (maxUrls.text.isBlank())
                                null
                            else maxUrls.text.toLong()
                        )

                        UserQuotaFilesQuota.BY_FILES -> UpdateUserBodyQuota(
                            filesType = userQuota,
                            maxBytes = null,
                            maxFiles = if (maxFileCount.text.isBlank())
                                null
                            else maxFileCount.text.toLong(),
                            maxUrls = if (maxUrls.text.isBlank())
                                null
                            else maxUrls.text.toLong()
                        )

                        UserQuotaFilesQuota.NONE -> UpdateUserBodyQuota(
                            filesType = userQuota,
                            maxBytes = null,
                            maxFiles = null,
                            maxUrls = if (maxUrls.text.isBlank())
                                null
                            else maxUrls.text.toLong()
                        )
                    }

                    val editUserRes = updateUser(
                        context = context,
                        userId = user.id,
                        username = username.text.ifBlank { null },
                        password = password.text.ifBlank { null },
                        avatar = avatar,
                        role = userRole,
                        quota = userQuotaBody

                    )

                    editUserRes
                        .onSuccess {
                            Notification.show(
                                context = context,
                                activity = activity,
                                content = {
                                    Text(
                                        text = "User edited successfully"
                                    )
                                }
                            )

                            updateUsers()
                            onDismissRequest()
                        }
                        .onFailure {
                            Logger.error("EditUrlPopup", "Failed to edit user", it)

                            errorMessage = it.message ?: "Something went wrong..."

                            Notification.show(
                                context = context,
                                activity = activity,
                                content = {
                                    Text(
                                        text = "Failed to edit user"
                                    )
                                },
                            )
                        }

                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Update User")
        }
    }
}