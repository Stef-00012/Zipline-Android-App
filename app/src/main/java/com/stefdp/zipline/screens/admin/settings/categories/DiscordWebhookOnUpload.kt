package com.stefdp.zipline.screens.admin.settings.categories

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.stefdp.zipline.Logger
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.components.Select
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.components.colorpicker.ColorPicker
import com.stefdp.zipline.network.models.FilesFormat
import com.stefdp.zipline.network.models.PartialServerSettingsSettings
import com.stefdp.zipline.network.models.ServerSettings
import com.stefdp.zipline.network.models.ServerSettingsSettingsDiscordUploadEmbed
import com.stefdp.zipline.network.models.requests.UploadCompressionType
import com.stefdp.zipline.utils.NumberRegex
import com.stefdp.zipline.utils.compressionFormats
import com.stefdp.zipline.utils.nameFormats
import com.stefdp.zipline.utils.toHex
import com.stefdp.zipline.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
internal fun DiscordWebhookOnUploadCategory(
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

            var webhookUrl by remember(settings?.settings?.discordOnUploadWebhookUrl, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.discordOnUploadWebhookUrl ?: ""))
            }

            TextInput(
                value = webhookUrl,
                onValueChange = { webhookUrl = it },
                label = "Webhook URL",
                description = "The Discord webhook URL to send notifications to. If this is left blank, the main webhook url will be used.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                isPassword = true
            )

            var username by remember(settings?.settings?.discordOnUploadUsername, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.discordOnUploadUsername ?: ""))
            }

            TextInput(
                value = username,
                onValueChange = { username = it },
                label = "Username",
                description = "The username to send notifications as. If this is left blank, the main username will be used.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var avatarUrl by remember(settings?.settings?.discordOnUploadAvatarUrl, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.discordOnUploadAvatarUrl ?: ""))
            }

            TextInput(
                value = avatarUrl,
                onValueChange = { avatarUrl = it },
                label = "Avatar URL",
                description = "The avatar for the webhook. If this is left blank, the main avatar will be used.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var content by remember(settings?.settings?.discordOnUploadContent, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.discordOnUploadContent ?: ""))
            }

            TextInput(
                value = content,
                singleLine = false,
                onValueChange = { content = it },
                label = "Content",
                description = "The content of the notification. This can be blank, but at least one of the content or embed fields must be filled out.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var embed by remember(settings?.settings?.discordOnUploadEmbed, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.discordOnUploadEmbed)
            }

            var embedTitle by remember(settings?.settings?.discordOnUploadEmbed?.title, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.discordOnUploadEmbed?.title ?: ""))
            }

            var embedDescription by remember(settings?.settings?.discordOnUploadEmbed?.description, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.discordOnUploadEmbed?.description ?: ""))
            }

            var embedFooter by remember(settings?.settings?.discordOnUploadEmbed?.footer, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.discordOnUploadEmbed?.footer ?: ""))
            }

            var embedColor by remember(settings?.settings?.discordOnUploadEmbed?.color, settingsUpdateTick) {
                Logger.debug("DiscordWebhookOnUploadCategory", "embed: ${settings?.settings?.discordOnUploadEmbed}")
                Logger.debug("DiscordWebhookOnUploadCategory", "Initial color: ${settings?.settings?.discordOnUploadEmbed?.color}")

                val color = settings?.settings?.discordOnUploadEmbed?.color?.toColorInt()

                Logger.debug("DiscordWebhookOnUploadCategory", "Parsed color int: $color, Color: ${if (color != null) Color(color) else "none"}")

                mutableStateOf(if (color != null) Color(color) else Color.Black)
            }

            var thumbnail by remember(settings?.settings?.discordOnUploadEmbed?.thumbnail, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.discordOnUploadEmbed?.thumbnail ?: false)
            }

            var imageOrVideo by remember(settings?.settings?.discordOnUploadEmbed?.imageOrVideo, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.discordOnUploadEmbed?.imageOrVideo ?: false)
            }

            var timestamp by remember(settings?.settings?.discordOnUploadEmbed?.timestamp, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.discordOnUploadEmbed?.timestamp ?: false)
            }

            var url by remember(settings?.settings?.discordOnUploadEmbed?.url, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.discordOnUploadEmbed?.url ?: false)
            }

            Switch(
                checked = embed != null,
                onCheckedChange = { checked ->
                    embed = if (checked) ServerSettingsSettingsDiscordUploadEmbed(
                        title = embedTitle.text,
                        description = embedDescription.text,
                        footer = embedFooter.text,
                        color = embedColor.toHex(),
                        thumbnail = thumbnail,
                        imageOrVideo = imageOrVideo,
                        timestamp = timestamp,
                        url = url
                    )
                    else null
                },
                label = "Embed",
                description = "Send the notification as an embed. This will allow for more customization below.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(
                visible = embed != null
            ) {
                Container(
                    scrollable = false,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(12.dp)
                    ) {
                        TextInput(
                            value = embedTitle,
                            onValueChange = { embedTitle = it },
                            label = "Title",
                            description = "The title of the embed",
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )

                        TextInput(
                            value = embedDescription,
                            onValueChange = { embedDescription = it },
                            singleLine = false,
                            label = "Description",
                            description = "The description of the embed",
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )

                        TextInput(
                            value = embedFooter,
                            onValueChange = { embedFooter = it },
                            label = "Footer",
                            description = "The footer of the embed",
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )

                        ColorPicker(
                            color = embedColor,
                            onColorChange = { embedColor = it },
                            label = "Color",
                            description = "The color of the embed",
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Switch(
                            checked = thumbnail,
                            onCheckedChange = { thumbnail = it },
                            label = "Thumbnail",
                            description = "Show the thumbnail (it will show the file if it's an image) in the embed.",
                            enabled = !isLoading,
                        )

                        Switch(
                            checked = imageOrVideo,
                            onCheckedChange = { imageOrVideo = it },
                            label = "Image/Video",
                            description = "Show the image or video in the embed.",
                            enabled = !isLoading,
                        )

                        Switch(
                            checked = timestamp,
                            onCheckedChange = { timestamp = it },
                            label = "Timestamp",
                            description = "Show the timestamp in the embed.",
                            enabled = !isLoading,
                        )

                        Switch(
                            checked = url,
                            onCheckedChange = { url = it },
                            label = "URL",
                            description = "Makes the title clickable and links to the URL of the file.",
                            enabled = !isLoading,
                        )
                    }
                }
            }

            val coroutineScope = rememberCoroutineScope()

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    coroutineScope.launch {
                        setLoading(true)

                        val data = PartialServerSettingsSettings(
                            discordOnUploadWebhookUrl = webhookUrl.text.takeIf { it.isNotBlank() },
                            discordOnUploadUsername = username.text,
                            discordOnUploadAvatarUrl = avatarUrl.text.takeIf { it.isNotBlank() },
                            discordOnUploadContent = content.text,
                            discordOnUploadEmbed = embed?.copy(
                                title = embedTitle.text,
                                description = embedDescription.text,
                                footer = embedFooter.text,
                                color = embedColor.toHex(),
                                thumbnail = thumbnail,
                                imageOrVideo = imageOrVideo,
                                timestamp = timestamp,
                                url = url
                            )
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