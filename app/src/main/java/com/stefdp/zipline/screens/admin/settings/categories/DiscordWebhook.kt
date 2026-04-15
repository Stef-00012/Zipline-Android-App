package com.stefdp.zipline.screens.admin.settings.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.DiscordSettings
import com.stefdp.zipline.screens.admin.settings.AdminSettingsUiState
import com.stefdp.zipline.screens.admin.settings.AdminSettingsViewModel
import com.stefdp.zipline.utils.verticalScrollWithScrollbar

@Composable
internal fun DiscordWebhookCategory(
    updateSettings: (DiscordSettings) -> Unit,
    title: String,
    viewModel: AdminSettingsViewModel,
    state: AdminSettingsUiState,
) {
    Container(
        scrollable = false,
        modifier = Modifier.fillMaxWidth()
    ) {
        val scrollState = rememberScrollState()

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .verticalScrollWithScrollbar(
                    scrollState = scrollState,
                )
                .padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
            )

            TextInput(
                value = state.discordWebhookUrl,
                onValueChange = {
                    viewModel.setDiscordWebhookUrl(it)
                },
                label = "Webhook URL",
                description = "The Discord webhook URL to send notifications to.",
                enabled = !state.isLoading && "discordWebhookUrl" !in state.tamperedSettings,
                modifier = Modifier.fillMaxWidth(),
                isPassword = true
            )

            TextInput(
                value = state.discordUsername,
                onValueChange = {
                    viewModel.setDiscordUsername(it)
                },
                label = "Username",
                description = "The username to send notifications as.",
                enabled = !state.isLoading && "discordUsername" !in state.tamperedSettings,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.discordUsername,
                onValueChange = {
                    viewModel.setDiscordAvatarUrl(it)
                },
                label = "Avatar URL",
                description = "The avatar for the webhook.",
                enabled = !state.isLoading && "discordAvatarUrl" !in state.tamperedSettings,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val data = DiscordSettings(
                        discordWebhookUrl = state.discordWebhookUrl.text.takeIf { it.isNotBlank() },
                        discordUsername = state.discordUsername.text.takeIf { it.isNotBlank() },
                        discordAvatarUrl = state.discordAvatarUrl.text.takeIf { it.isNotBlank() }
                    )

                    updateSettings(data)
                },
                enabled = !state.isLoading
            ) {
                Icon(
                    painter = painterResource(R.drawable.save),
                    contentDescription = "Save settings"
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Save"
                )
            }
        }
    }
}