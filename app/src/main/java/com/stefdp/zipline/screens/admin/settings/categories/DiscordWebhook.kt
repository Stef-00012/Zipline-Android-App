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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.components.Select
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.FilesFormat
import com.stefdp.zipline.network.models.PartialServerSettingsSettings
import com.stefdp.zipline.network.models.ServerSettings
import com.stefdp.zipline.network.models.requests.UploadCompressionType
import com.stefdp.zipline.utils.NumberRegex
import com.stefdp.zipline.utils.compressionFormats
import com.stefdp.zipline.utils.nameFormats
import com.stefdp.zipline.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
internal fun DiscordWebhookCategory(
    settings: ServerSettings?,
    updateSettings: suspend (PartialServerSettingsSettings) -> List<String>,
    isLoading: Boolean,
    setLoading: (Boolean) -> Unit,
    title: String,
    settingsUpdateTick: Int
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

            var errors by remember { mutableStateOf<List<String>>(emptyList()) }

            if (errors.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    errors.forEach {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }

            var webhookUrl by remember(settings?.settings?.discordWebhookUrl, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.discordWebhookUrl ?: ""))
            }

            TextInput(
                value = webhookUrl,
                onValueChange = { webhookUrl = it },
                label = "Webhook URL",
                description = "The Discord webhook URL to send notifications to.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                isPassword = true
            )

            var username by remember(settings?.settings?.discordUsername, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.discordUsername ?: ""))
            }

            TextInput(
                value = username,
                onValueChange = { username = it },
                label = "Username",
                description = "The username to send notifications as.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var avatarUrl by remember(settings?.settings?.discordAvatarUrl, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.discordAvatarUrl ?: ""))
            }

            TextInput(
                value = avatarUrl,
                onValueChange = { avatarUrl = it },
                label = "Avatar URL",
                description = "The avatar for the webhook.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            val coroutineScope = rememberCoroutineScope()

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    coroutineScope.launch {
                        setLoading(true)

                        val data = PartialServerSettingsSettings(
                            discordWebhookUrl = webhookUrl.text.takeIf { it.isNotBlank() },
                            discordUsername = username.text.takeIf { it.isNotBlank() },
                            discordAvatarUrl = avatarUrl.text.takeIf { it.isNotBlank() }
                        )

                        val updateSettingsErrors = updateSettings(data)

                        errors = updateSettingsErrors

                        setLoading(false)
                    }
                },
                enabled = !isLoading
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