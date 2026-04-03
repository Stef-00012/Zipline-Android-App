package com.stefdp.zipline.screens.admin.invites.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.stefdp.zipline.network.requests.createInvite
import com.stefdp.zipline.utils.NumberRegex
import com.stefdp.zipline.utils.inviteExpiresAtDates
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
import com.stefdp.zipline.network.models.UserRole
import com.stefdp.zipline.network.requests.createUser
import kotlinx.coroutines.launch

@Composable
fun CreateInvitePopup(
    context: Context,
    activity: FragmentActivity,
    showPopup: Boolean,
    onDismissRequest: () -> Unit,
    updateInvites: suspend () -> Unit
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
                text = "Create an invite",
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
                    contentDescription = "Close new invite menu",
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
            modifier = Modifier.height(16.dp)
        )

        var selectedExpiresAt by remember { mutableStateOf(setOf("never")) }

        Select(
            label = "Expires at",
            description = "Select an expiration for this invite, or choose \"never\" if you want the invite to never expire.",
            options = inviteExpiresAtDates.map { (id, label) ->
                SelectOption(
                    id = id,
                    label = { enabled ->
                        Text(
                            text = label,
                            color = if (enabled)
                                MaterialTheme.colorScheme.onBackground
                            else
                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                )
            },
            selectedIds = selectedExpiresAt,
            onSelectionChange = { selectedExpiresAt = it },
            enabled = !isLoading
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        var maxUses by remember { mutableStateOf(TextFieldValue("")) }

        TextInput(
            value = maxUses,
            description = "Set a maximum number of uses for this invite, or leave blank for unlimited uses.",
            onValueChange = {
                if (NumberRegex.matches(it.text) || it.text.isEmpty()) {
                    maxUses = it
                }
            },
            label = "Max Uses",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            enabled = !isLoading,
            onClick = {
                coroutineScope.launch {
                    isLoading = true

                    val createInviteRes = createInvite(
                        context = context,
                        expiresAt = selectedExpiresAt.firstOrNull() ?: "never",
                        maxUses = maxUses.text.toLongOrNull(),
                    )

                    createInviteRes
                        .onSuccess {
                            Notification.show(
                                context = context,
                                activity = activity,
                                content = {
                                    Text(
                                        text = "Invite created successfully"
                                    )
                                },
                            )

                            updateInvites()
                            onDismissRequest()
                        }
                        .onFailure {
                            Logger.error("CreateInvitePopup", "Failed to create invite", it)

                            errorMessage = it.message ?: "Something went wrong..."

                            Notification.show(
                                context = context,
                                activity = activity,
                                content = {
                                    Text(
                                        text = "Failed to create invite"
                                    )
                                }
                            )
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