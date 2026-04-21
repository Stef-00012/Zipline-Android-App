package com.stefdp.zipline.screens.admin.users.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.stefdp.zipline.network.models.UserRole
import com.stefdp.zipline.screens.admin.users.AdminUsersUiState
import com.stefdp.zipline.screens.admin.users.AdminUsersViewModel
import com.stefdp.zipline.utils.ZiplineViewStateType

@Composable
fun CreateUserPopup(
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
                text = "Create a new user",
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
                    contentDescription = "Close new user menu",
                )
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextInput(
            value = state.newUserUsername,
            onValueChange = {
                viewModel.setNewUserUsername(it)
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
            value = state.newUserPassword,
            onValueChange = {
                viewModel.setNewUserPassword(it)
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
                viewModel.setNewUserAvatar(it)
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
            selectedIds = state.newUserSelectedRole,
            onSelectionChange = {
                viewModel.setNewUserSelectedRole(it)
            }
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            enabled = !state.isLoading && state.newUserUsername.text.isNotBlank() && state.newUserPassword.text.isNotBlank(),
            onClick = {
                viewModel.createUser(
                    context = context,
                    viewState = viewState,
                    onSuccess = {
                        Notification.show(
                            activity = activity,
                        ) {
                            Text(
                                text = "User created successfully"
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
            Text(text = "Create")
        }
    }
}