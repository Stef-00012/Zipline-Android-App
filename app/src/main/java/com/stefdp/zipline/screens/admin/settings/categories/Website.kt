package com.stefdp.zipline.screens.admin.settings.categories

import android.content.Context
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.PartialServerSettingsSettings
import com.stefdp.zipline.network.models.ServerSettings
import com.stefdp.zipline.network.models.WebsiteExternalLink
import com.stefdp.zipline.screens.admin.settings.categories.components.ExternalLink
import com.stefdp.zipline.components.IconButton
import com.stefdp.zipline.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch
import java.util.Collections

@Composable
internal fun WebsiteCategory(
    settings: ServerSettings?,
    updateSettings: suspend (PartialServerSettingsSettings) -> List<String>,
    isLoading: Boolean,
    setLoading: (Boolean) -> Unit,
    title: String,
    settingsUpdateTick: Int,
    context: Context,
    activity: FragmentActivity
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

            var title by remember(settings?.settings?.websiteTitle, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.websiteTitle ?: ""))
            }

            TextInput(
                value = title,
                onValueChange = { title = it },
                label = "Title",
                description = "The title of the website in browser tabs and at the top.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var titleLogo by remember(settings?.settings?.websiteTitleLogo, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.websiteTitleLogo ?: ""))
            }

            TextInput(
                value = titleLogo,
                onValueChange = { titleLogo = it },
                label = "Title Logo",
                description = "The URL to use for the title logo. This is placed to the left of the title.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var externalLinks by remember(settings?.settings?.websiteExternalLinks, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.websiteExternalLinks ?: emptyList())
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                var createNewExternalUrl by remember { mutableStateOf(false) }

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
                        onClick = { createNewExternalUrl = true },
                        enabled = !isLoading
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

                    var editExternalLinkIndex by remember { mutableIntStateOf(-1) }

                    Popup(
                        showPopup = createNewExternalUrl,
                        onDismissRequest = { createNewExternalUrl = false },
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
                                        onClick = { createNewExternalUrl = false }
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

                        var name by remember {
                            mutableStateOf(TextFieldValue(""))
                        }

                        TextInput(
                            value = name,
                            onValueChange = { name = it },
                            label = "Name",
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        var url by remember {
                            mutableStateOf(TextFieldValue(""))
                        }

                        TextInput(
                            value = url,
                            onValueChange = { url = it },
                            label = "URL",
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                if (name.text.isBlank() || url.text.isBlank()) {
                                    Notification.show(
                                        context = context,
                                        activity = activity,
                                        content = {
                                            Text(
                                                text = "Please fill in all fields"
                                            )
                                        }
                                    )

                                    return@Button
                                }

                                val newExternalLink = WebsiteExternalLink(
                                    name = name.text,
                                    url = url.text
                                )

                                externalLinks = externalLinks + newExternalLink

                                createNewExternalUrl = false
                            },
                            enabled = !isLoading
                        ) {
                            Text(
                                text = "Create Link"
                            )
                        }
                    }

                    Popup(
                        showPopup = editExternalLinkIndex != -1,
                        onDismissRequest = { editExternalLinkIndex = -1 },
                    ) {
                        val externalLink = externalLinks[editExternalLinkIndex]

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
                                        onClick = { editExternalLinkIndex = -1 }
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

                        var name by remember(externalLink.name) {
                            mutableStateOf(TextFieldValue(externalLink.name))
                        }

                        TextInput(
                            value = name,
                            onValueChange = { name = it },
                            label = "Name",
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        var url by remember(externalLink.url) {
                            mutableStateOf(TextFieldValue(externalLink.url))
                        }

                        TextInput(
                            value = url,
                            onValueChange = { url = it },
                            label = "URL",
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                val updatedExternalLink = WebsiteExternalLink(
                                    name = name.text,
                                    url = url.text
                                )

                                externalLinks = externalLinks.toMutableList().also {
                                    it[editExternalLinkIndex] = updatedExternalLink
                                }.toList()

                                editExternalLinkIndex = -1
                             },
                            enabled = !isLoading
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
                        externalLinks.forEachIndexed { index, url ->
                            ExternalLink(
                                url = url,
                                isLoading = isLoading,
                                onEdit = {
                                    editExternalLinkIndex = index
                                },
                                onDelete = {
                                    externalLinks = externalLinks - url
                                },
                                onMoveUp = {
                                    val _externalLinks = externalLinks.toMutableList()

                                    Collections.swap(_externalLinks, index, index - 1)

                                    externalLinks = _externalLinks.toList()
                                },
                                onMoveDown = {
                                    val _externalLinks = externalLinks.toMutableList()

                                    Collections.swap(_externalLinks, index, index + 1)

                                    externalLinks = _externalLinks.toList()
                                },
                                isFirst = index == 0,
                                isLast = index == externalLinks.lastIndex,
                            )
                        }
                    }
                }
            }

            var loginBackground by remember(settings?.settings?.websiteLoginBackground, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.websiteLoginBackground ?: ""))
            }

            TextInput(
                value = loginBackground,
                onValueChange = { loginBackground = it },
                label = "Login Background",
                description = "The URL to use for the login background.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var loginBackgroundBlur by remember(settings?.settings?.websiteLoginBackgroundBlur, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.websiteLoginBackgroundBlur ?: false)
            }

            Switch(
                checked = loginBackgroundBlur,
                onCheckedChange = { loginBackgroundBlur = it },
                label = "Login Background Blur",
                description = "Whether to blur the login background.",
                enabled = !isLoading
            )

            var defaultAvatar by remember(settings?.settings?.websiteDefaultAvatar, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.websiteDefaultAvatar ?: ""))
            }

            TextInput(
                value = defaultAvatar,
                onValueChange = { defaultAvatar = it },
                label = "Default Avatar",
                description = "The path to use for the default avatar. This must be a path to an image, not a URL.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var termsOfService by remember(settings?.settings?.websiteTos, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.websiteTos ?: ""))
            }

            TextInput(
                value = termsOfService,
                onValueChange = { termsOfService = it },
                label = "Terms of Service",
                description = "Path to a Markdown (.md) file to use for the terms of service.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var defaultTheme by remember(settings?.settings?.websiteThemeDefault, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.websiteThemeDefault ?: ""))
            }

            TextInput(
                value = defaultTheme,
                onValueChange = { defaultTheme = it },
                label = "Default Theme",
                description = "The default theme to use for the website.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var darkTheme by remember(settings?.settings?.websiteThemeDark, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.websiteThemeDark ?: ""))
            }

            TextInput(
                value = darkTheme,
                onValueChange = { darkTheme = it },
                label = "Dark Theme",
                description = "The dark theme to use for the website when the default theme is \"system\".",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var lightTheme by remember(settings?.settings?.websiteThemeLight, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.websiteThemeLight ?: ""))
            }

            TextInput(
                value = lightTheme,
                onValueChange = { lightTheme = it },
                label = "Light Theme",
                description = "The light theme to use for the website when the default theme is \"system\".",
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
                            websiteTitle = title.text,
                            websiteTitleLogo = titleLogo.text.takeIf { it.isNotBlank() },
                            websiteLoginBackground = loginBackground.text.takeIf { it.isNotBlank() },
                            websiteLoginBackgroundBlur = loginBackgroundBlur,
                            websiteDefaultAvatar = defaultAvatar.text.takeIf { it.isNotBlank() },
                            websiteTos = termsOfService.text.takeIf { it.isNotBlank() },
                            websiteThemeDefault = defaultTheme.text,
                            websiteThemeDark = darkTheme.text,
                            websiteThemeLight = lightTheme.text,
                            websiteExternalLinks = externalLinks
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