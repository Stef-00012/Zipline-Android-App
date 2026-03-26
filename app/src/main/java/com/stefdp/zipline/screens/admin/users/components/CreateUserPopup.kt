package com.stefdp.zipline.screens.admin.users.components

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import com.stefdp.zipline.LocalLoggedUser
import com.stefdp.zipline.R
import com.stefdp.zipline.components.AvatarInput
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.components.Select
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.User
import com.stefdp.zipline.network.models.UserRole
import com.stefdp.zipline.network.requests.createUser
import kotlinx.coroutines.launch

@Composable
fun CreateUserPopup(
    currentUser: User?,
    context: Context,
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

        var username by remember { mutableStateOf(TextFieldValue("")) }

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

        var avatar by remember { mutableStateOf<String?>(null) }

        AvatarInput(
            context = context,
            onAvatarChange = { avatar = it },
            modifier = Modifier.fillMaxWidth(),
            label = "Avatar",
            enabled = !isLoading,
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        var selectedRole by remember { mutableStateOf(setOf(UserRole.USER.toString())) }

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

        Button(
            enabled = !isLoading,
            onClick = {
                coroutineScope.launch {
                    isLoading = true

                    val userRole = UserRole.valueOf(selectedRole.firstOrNull() ?: UserRole.USER.toString())

                    val createUserRes = createUser(
                        context = context,
                        username = username.text,
                        password = password.text,
                        role = userRole,
                        avatar = avatar
                    )

                    createUserRes
                        .onSuccess {
                            Toast.makeText(
                                context,
                                "User created successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            updateUsers()
                            onDismissRequest()
                        }
                        .onFailure {
                            Log.e("CreateUrlPopup", "Failed to create user", it)

                            errorMessage = it.message ?: "Something went wrong..."

                            Toast.makeText(
                                context,
                                "Failed to create user",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    isLoading = false
                    onDismissRequest()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Create")
        }
    }
}