package com.stefdp.zipline.screens.urls.components

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.screens.urls.UrlsUiState
import com.stefdp.zipline.screens.urls.UrlsViewModel
import com.stefdp.zipline.utils.NumberRegex
import com.stefdp.zipline.utils.ZiplineViewStateType

@Composable
fun EditUrlPopup(
    context: Context,
    activity: FragmentActivity,
    showPopup: Boolean,
    onDismissRequest: () -> Unit,
    viewModel: UrlsViewModel,
    state: UrlsUiState,
    viewState: ZiplineViewStateType,
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
                text = "Editing \"${state.editUrl?.code}\"",
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
                    contentDescription = "Close shorten menu",
                )
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextInput(
            value = state.editUrlDestination,
            onValueChange = {
                viewModel.setEditUrlDestination(it)
            },
            modifier = Modifier.fillMaxWidth(),
            label = "Destination",
            placeholder = "https://google.com",
            enabled = !state.isLoading
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextInput(
            value = state.editUrlVanity,
            onValueChange = {
                viewModel.setEditUrlVanity(it)
            },
            modifier = Modifier.fillMaxWidth(),
            label = "Vanity",
            placeholder = "example",
            enabled = !state.isLoading,
            description = "A custom alias for your URL. Leave blank to use the randomly generated code."
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextInput(
            value = state.editUrlMaxViews,
            onValueChange = {
                if (NumberRegex.matches(it.text) || it.text.isBlank()) {
                    viewModel.setEditUrlMaxViews(it)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = "Max Views",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            enabled = !state.isLoading,
            description = "The maximum number of clicks this URL can have before it is automatically deleted. Leave blank to allow as many views as you want."
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Switch(
            label = "Enabled",
            checked = state.editUrlEnabled,
            onCheckedChange = {
                viewModel.setEditUrlEnabled(it)
            },
            enabled = !state.isLoading,
            description = "Prevent or allow this URL from being visited."
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextInput(
            value = state.editUrlPassword,
            onValueChange = {
                viewModel.setEditUrlPassword(it)
            },
            modifier = Modifier.fillMaxWidth(),
            label = "Password",
            enabled = !state.isLoading,
            description = "Set a password for this URL. Leave blank to disable password protection.",
            isPassword = true
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            enabled = !state.isLoading && state.editUrlDestination.text.isNotBlank(),
            onClick = {
                if (!urlRegex.matches(state.editUrlDestination.text)) {
                    Notification.show(
                        activity = activity,
                    ) {
                        Text(
                            text = "Invalid Destination URL"
                        )
                    }

                    return@Button
                }

                if (state.editUrl == null) return@Button

                viewModel.editUrl(
                    context = context,
                    viewState = viewState,
                    urlId = state.editUrl.id,
                    destination = state.editUrlDestination.text,
                    vanity = state.editUrlVanity.text.takeIf { it.isNotBlank() },
                    enabled = state.editUrlEnabled,
                    maxViews = state.editUrlMaxViews.text.toLongOrNull(),
                    password = state.editUrlPassword.text.takeIf { it.isNotBlank() },
                    onSuccess = {
                        Notification.show(
                            activity = activity,
                        ) {
                            Text(
                                text = "URL updated successfully"
                            )
                        }

                        viewModel.setEditUrl(null)
                    },
                    onError = { error ->
                        Notification.show(
                            activity = activity,
                        ) {
                            Text(
                                text = "Failed to edit URL: $error"
                            )
                        }
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Save Changes")
        }
    }
}