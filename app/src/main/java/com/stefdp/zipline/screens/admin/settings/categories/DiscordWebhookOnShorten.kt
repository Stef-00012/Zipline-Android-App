package com.stefdp.zipline.screens.admin.settings.categories

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.components.colorpicker.ColorPicker
import com.stefdp.zipline.network.models.DiscordOnShortenSettings
import com.stefdp.zipline.network.models.ServerSettingsSettingsDiscordShortenEmbed
import com.stefdp.zipline.screens.admin.settings.AdminSettingsUiState
import com.stefdp.zipline.screens.admin.settings.AdminSettingsViewModel
import com.stefdp.zipline.utils.toHex
import com.stefdp.zipline.utils.verticalScrollWithScrollbar

@Composable
internal fun DiscordWebhookOnShortenCategory(
    updateSettings: (DiscordOnShortenSettings) -> Unit,
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
                value = state.discordOnShortenWebhookUrl,
                onValueChange = {
                    viewModel.setDiscordOnShortenWebhookUrl(it)
                },
                label = "Webhook URL",
                description = "The Discord webhook URL to send notifications to. If this is left blank, the main webhook url will be used.",
                enabled = !state.isLoading && "discordOnShortenWebhookUrl" !in state.tamperedSettings,
                modifier = Modifier.fillMaxWidth(),
                isPassword = true
            )

            TextInput(
                value = state.discordOnShortenUsername,
                onValueChange = {
                    viewModel.setDiscordOnShortenUsername(it)
                },
                label = "Username",
                description = "The username to send notifications as. If this is left blank, the main username will be used.",
                enabled = !state.isLoading && "discordOnShortenUsername" !in state.tamperedSettings,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.discordOnShortenAvatarUrl,
                onValueChange = {
                    viewModel.setDiscordOnShortenAvatarUrl(it)
                },
                label = "Avatar URL",
                description = "The avatar for the webhook. If this is left blank, the main avatar will be used.",
                enabled = !state.isLoading && "discordOnShortenAvatarUrl" !in state.tamperedSettings,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.discordOnShortenContent,
                singleLine = false,
                onValueChange = {
                    viewModel.setDiscordOnShortenContent(it)
                },
                label = "Content",
                description = "The content of the notification. This can be blank, but at least one of the content or embed fields must be filled out.",
                enabled = !state.isLoading && "discordOnShortenContent" !in state.tamperedSettings,
                modifier = Modifier.fillMaxWidth()
            )

            Switch(
                checked = state.discordOnShortenEmbed != null,
                onCheckedChange = { checked ->
                    val embed = if (checked) ServerSettingsSettingsDiscordShortenEmbed(
                        title = state.discordOnShortenEmbedTitle.text,
                        description = state.discordOnShortenEmbedDescription.text,
                        footer = state.discordOnShortenEmbedFooter.text,
                        color = state.discordOnShortenEmbedColor.toHex(),
                        thumbnail = state.discordOnShortenEmbedThumbnail,
                        imageOrVideo = state.discordOnShortenEmbedImageOrVideo,
                        timestamp = state.discordOnShortenEmbedTimestamp,
                        url = state.discordOnShortenEmbedUrl
                    )
                    else null

                    viewModel.setDiscordOnShortenEmbed(embed)
                },
                label = "Embed",
                description = "Send the notification as an embed. This will allow for more customization below.",
                enabled = !state.isLoading && "discordOnShortenEmbed" !in state.tamperedSettings,
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(
                visible = state.discordOnShortenEmbed != null
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
                            value = state.discordOnShortenEmbedTitle,
                            onValueChange = {
                                viewModel.setDiscordOnShortenEmbedTitle(it)
                            },
                            label = "Title",
                            description = "The title of the embed",
                            enabled = !state.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )

                        TextInput(
                            value = state.discordOnShortenEmbedDescription,
                            onValueChange = {
                                viewModel.setDiscordOnShortenEmbedDescription(it)
                            },
                            singleLine = false,
                            label = "Description",
                            description = "The description of the embed",
                            enabled = !state.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )

                        TextInput(
                            value = state.discordOnShortenEmbedFooter,
                            onValueChange = {
                                viewModel.setDiscordOnShortenEmbedFooter(it)
                            },
                            label = "Footer",
                            description = "The footer of the embed",
                            enabled = !state.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )

                        ColorPicker(
                            color = state.discordOnShortenEmbedColor,
                            onColorChange = {
                                viewModel.setDiscordOnShortenEmbedColor(it)
                            },
                            label = "Color",
                            description = "The color of the embed",
                            enabled = !state.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )

//                        Switch(
//                            checked = thumbnail,
//                            onCheckedChange = { thumbnail = it },
//                            label = "Thumbnail",
//                            description = "Show the thumbnail (it will show the file if it's an image) in the embed.",
//                            enabled = !state.isLoading,
//                        )
//
//                        Switch(
//                            checked = imageOrVideo,
//                            onCheckedChange = { imageOrVideo = it },
//                            label = "Image/Video",
//                            description = "Show the image or video in the embed.",
//                            enabled = !state.isLoading,
//                        )

                        Switch(
                            checked = state.discordOnShortenEmbedTimestamp,
                            onCheckedChange = {
                                viewModel.setDiscordOnShortenEmbedTimestamp(it)
                            },
                            label = "Timestamp",
                            description = "Show the timestamp in the embed.",
                            enabled = !state.isLoading,
                        )

                        Switch(
                            checked = state.discordOnShortenEmbedUrl,
                            onCheckedChange = {
                                viewModel.setDiscordOnShortenEmbedUrl(it)
                            },
                            label = "URL",
                            description = "Makes the title clickable and links to the URL of the file.",
                            enabled = !state.isLoading,
                        )
                    }
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val data = DiscordOnShortenSettings(
                        discordOnShortenWebhookUrl = state.discordOnShortenWebhookUrl.text.takeIf { it.isNotBlank() },
                        discordOnShortenUsername = state.discordOnShortenUsername.text,
                        discordOnShortenAvatarUrl = state.discordOnShortenAvatarUrl.text.takeIf { it.isNotBlank() },
                        discordOnShortenContent = state.discordOnShortenContent.text,
                        discordOnShortenEmbed = state.discordOnShortenEmbed?.copy(
                            title = state.discordOnShortenEmbedTitle.text,
                            description = state.discordOnShortenEmbedDescription.text,
                            footer = state.discordOnShortenEmbedFooter.text,
                            color = state.discordOnShortenEmbedColor.toHex(),
                            thumbnail = state.discordOnShortenEmbedThumbnail,
                            imageOrVideo = state.discordOnShortenEmbedImageOrVideo,
                            timestamp = state.discordOnShortenEmbedTimestamp,
                            url = state.discordOnShortenEmbedUrl
                        )
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