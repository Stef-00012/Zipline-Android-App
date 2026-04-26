package com.stefdp.zipline.screens.admin.settings.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import com.stefdp.zipline.Logger
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.WebsiteExternalLink
import com.stefdp.zipline.screens.admin.settings.categories.components.ExternalLink
import com.stefdp.zipline.components.IconButton
import com.stefdp.zipline.network.models.WebsiteSettings
import com.stefdp.zipline.screens.admin.settings.AdminSettingsUiState
import com.stefdp.zipline.screens.admin.settings.AdminSettingsViewModel
import com.stefdp.zipline.screens.admin.settings.MoveWebsiteExternalLinkDirection
import com.stefdp.zipline.utils.verticalScrollWithScrollbar

@Composable
internal fun WebsiteCategory(
    updateSettings: (WebsiteSettings) -> Unit,
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
                value = state.websiteTitle,
                onValueChange = {
                    viewModel.setWebsiteTitle(it)
                },
                label = "Title",
                description = "The title of the website in browser tabs and at the top.",
                enabled = !state.isLoading && "websiteTitle" !in state.tamperedSettings,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.websiteTitleLogo,
                onValueChange = {
                    viewModel.setWebsiteTitleLogo(it)
                },
                label = "Title Logo",
                description = "The URL to use for the title logo. This is placed to the left of the title.",
                enabled = !state.isLoading && "websiteTitleLogo" !in state.tamperedSettings,
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = "External Links",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Text(
                            text = "The external links to show in the website footer.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            ),
                        )
                    }

                    IconButton(
                        icon = painterResource(R.drawable.add),
                        iconContentDescription = "Add external link",
                        color = MaterialTheme.colorScheme.primary,
                        iconColor = MaterialTheme.colorScheme.onPrimary,
                        onClick = {
                            viewModel.openCreateNewExternalLink()
                        },
                        enabled = !state.isLoading && "websiteExternalLinks" !in state.tamperedSettings
                    )
                }

                Container(
                    scrollable = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(
                            min = 100.dp,
                            max = 300.dp
                        )
                ) {
                    val externalLinksScrollState = rememberScrollState()

                    Popup(
                        showPopup = state.createNewExternalUrl,
                        onDismissRequest = {
                            viewModel.closeCreateNewExternalLink()
                        },
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Create External Link",
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
                                            viewModel.closeCreateNewExternalLink()
                                        }
                                    )
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.close),
                                    contentDescription = "Close create external link menu",
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        TextInput(
                            value = state.createExternalLinkName,
                            onValueChange = {
                                viewModel.setCreateExternalLinkName(it)
                            },
                            label = "Name",
                            enabled = !state.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        TextInput(
                            value = state.createExternalLinkUrl,
                            onValueChange = {
                                viewModel.setCreateExternalLinkUrl(it)
                            },
                            label = "URL",
                            enabled = !state.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                val newExternalLink = WebsiteExternalLink(
                                    name = state.createExternalLinkName.text,
                                    url = state.createExternalLinkUrl.text
                                )

                                viewModel.addWebsiteExternalLink(newExternalLink)
                            },
                            enabled = !state.isLoading && state.createExternalLinkName.text.isNotBlank() && state.createExternalLinkUrl.text.isNotBlank()
                        ) {
                            Text(
                                text = "Create Link"
                            )
                        }
                    }

                    Popup(
                        showPopup = state.editExternalLinkIndex != -1,
                        onDismissRequest = {
                            viewModel.setEditExternalLinkIndex(-1)
                        },
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Edit External Link",
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
                                            viewModel.setEditExternalLinkIndex(-1)
                                        }
                                    )
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.close),
                                    contentDescription = "Close edit external link menu",
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        TextInput(
                            value = state.editExternalLinkName,
                            onValueChange = {
                                viewModel.setEditExternalLinkName(it)
                            },
                            label = "Name",
                            enabled = !state.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        TextInput(
                            value = state.editExternalLinkUrl,
                            onValueChange = {
                                viewModel.setEditExternalLinkUrl(it)
                            },
                            label = "URL",
                            enabled = !state.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                val updatedExternalLink = WebsiteExternalLink(
                                    name = state.editExternalLinkName.text,
                                    url = state.editExternalLinkUrl.text
                                )

                                viewModel.editWebsiteExternalLink(state.editExternalLinkIndex, updatedExternalLink)
                             },
                            enabled = !state.isLoading && state.editExternalLinkName.text.isNotBlank() && state.editExternalLinkUrl.text.isNotBlank()
                        ) {
                            Text(
                                text = "Edit Link"
                            )
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScrollWithScrollbar(
                                scrollState = externalLinksScrollState,
                            )
                            .padding(8.dp)
                    ) {
                        state.websiteExternalLinks.forEachIndexed { index, url ->
                            Logger.debug("External Urls", "Displaying external link: $index (lastIndex: ${state.websiteExternalLinks.lastIndex}, isLast: ${index == state.websiteExternalLinks.lastIndex}, isFirst: ${index == 0})")

                            ExternalLink(
                                url = url,
                                isLoading = state.isLoading || "websiteExternalLinks" in state.tamperedSettings,
                                onEdit = {
                                    viewModel.setEditExternalLinkIndex(index)
                                },
                                onDelete = {
                                    viewModel.removeWebsiteExternalLink(url)
                                },
                                onMoveUp = {
                                    viewModel.moveWebsiteExternalLink(
                                        index = index,
                                        direction = MoveWebsiteExternalLinkDirection.UP
                                    )
                                },
                                onMoveDown = {
                                    viewModel.moveWebsiteExternalLink(
                                        index = index,
                                        direction = MoveWebsiteExternalLinkDirection.DOWN
                                    )
                                },
                                isFirst = index == 0,
                                isLast = index == state.websiteExternalLinks.lastIndex,
                            )
                        }
                    }
                }
            }

            TextInput(
                value = state.websiteLoginBackground,
                onValueChange = {
                    viewModel.setWebsiteLoginBackground(it)
                },
                label = "Login Background",
                description = "The URL to use for the login background.",
                enabled = !state.isLoading && "websiteLoginBackground" !in state.tamperedSettings,
                modifier = Modifier.fillMaxWidth()
            )

            Switch(
                checked = state.websiteLoginBackgroundBlur,
                onCheckedChange = {
                    viewModel.setWebsiteLoginBackgroundBlur(it)
                },
                label = "Login Background Blur",
                description = "Whether to blur the login background.",
                enabled = !state.isLoading && "websiteLoginBackgroundBlur" !in state.tamperedSettings,
            )

            TextInput(
                value = state.websiteDefaultAvatar,
                onValueChange = {
                    viewModel.setWebsiteDefaultAvatar(it)
                },
                label = "Default Avatar",
                description = "The path to use for the default avatar. This must be a path to an image, not a URL.",
                enabled = !state.isLoading && "websiteDefaultAvatar" !in state.tamperedSettings,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.websiteTos,
                onValueChange = {
                    viewModel.setWebsiteTos(it)
                },
                label = "Terms of Service",
                description = "Path to a Markdown (.md) file to use for the terms of service.",
                enabled = !state.isLoading && "websiteTos" !in state.tamperedSettings,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.websiteThemeDefault,
                onValueChange = {
                    viewModel.setWebsiteThemeDefault(it)
                },
                label = "Default Theme",
                description = "The default theme to use for the website.",
                enabled = !state.isLoading && "websiteThemeDefault" !in state.tamperedSettings,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.websiteThemeDark,
                onValueChange = {
                    viewModel.setWebsiteThemeDark(it)
                },
                label = "Dark Theme",
                description = "The dark theme to use for the website when the default theme is \"system\".",
                enabled = !state.isLoading && "websiteThemeDark" !in state.tamperedSettings,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.websiteThemeLight,
                onValueChange = {
                    viewModel.setWebsiteThemeLight(it)
                },
                label = "Light Theme",
                description = "The light theme to use for the website when the default theme is \"system\".",
                enabled = !state.isLoading && "websiteThemeLight" !in state.tamperedSettings,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val data = WebsiteSettings(
                        websiteTitle = state.websiteTitle.text,
                        websiteTitleLogo = state.websiteTitleLogo.text.takeIf { it.isNotBlank() },
                        websiteLoginBackground = state.websiteLoginBackground.text.takeIf { it.isNotBlank() },
                        websiteLoginBackgroundBlur = state.websiteLoginBackgroundBlur,
                        websiteDefaultAvatar = state.websiteDefaultAvatar.text.takeIf { it.isNotBlank() },
                        websiteTos = state.websiteTos.text.takeIf { it.isNotBlank() },
                        websiteThemeDefault = state.websiteThemeDefault.text,
                        websiteThemeDark = state.websiteThemeDark.text,
                        websiteThemeLight = state.websiteThemeLight.text,
                        websiteExternalLinks = state.websiteExternalLinks
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