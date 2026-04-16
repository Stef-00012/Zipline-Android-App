package com.stefdp.zipline.screens.urls.components

import android.content.ClipData
import android.content.Context
import android.content.Intent
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.LocalWebSettings
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.components.Select
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.components.largefiledisplay.IconButton
import com.stefdp.zipline.screens.urls.UrlsUiState
import com.stefdp.zipline.screens.urls.UrlsViewModel
import com.stefdp.zipline.utils.NumberRegex
import com.stefdp.zipline.utils.ZiplineViewStateType
import kotlinx.coroutines.launch

val urlRegex = Regex("""^https?://([a-zA-Z0-9-]+\.)+[a-zA-Z]{2,}(?:[/?#]\S*)?$""")

@Composable
fun CreateUrlPopup(
    context: Context,
    activity: FragmentActivity,
    showPopup: Boolean,
    onDismissRequest: () -> Unit,
    viewModel: UrlsViewModel,
    state: UrlsUiState,
    viewState: ZiplineViewStateType
) {
    val webSettings = LocalWebSettings.current

    val domains = webSettings?.config?.domains ?: emptyList()

    val coroutineScope = rememberCoroutineScope()

    Popup(
        showPopup = state.createdUrlResult != null,
        onDismissRequest = {
            viewModel.clearCreatedUrlResult()
        },
        scrollable = false,
    ) {
        Text(
            text = "Shortened URL",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "${state.createdUrlResult}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.tertiary,
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier.clickable(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, state.createdUrlResult?.toUri())
                        context.startActivity(intent)
                    }
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val clipboardManager = LocalClipboard.current

                IconButton(
                    icon = painterResource(R.drawable.content_copy),
                    iconContentDescription = "Copy URL",
                    onClick = {
                        coroutineScope.launch {
                            val clipData = ClipData.newPlainText("URL", state.createdUrlResult).toClipEntry()

                            clipboardManager.setClipEntry(clipData)

                            Notification.show(
                                context = context,
                                activity = activity,
                            ) {
                                Text(
                                    text = "URL copied to clipboard"
                                )
                            }
                        }
                    },
                    color = MaterialTheme.colorScheme.primary,
                    iconColor = MaterialTheme.colorScheme.onPrimary
                )

                IconButton(
                    icon = painterResource(R.drawable.open_new),
                    iconContentDescription = "Open URL",
                    onClick = {
                        coroutineScope.launch {
                            val intent = Intent(Intent.ACTION_VIEW, state.createdUrlResult?.toUri())
                            context.startActivity(intent)
                        }
                    },
                    color = MaterialTheme.colorScheme.primary,
                    iconColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }

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
                text = "Shorten URL",
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
            value = state.createUrlDestination,
            onValueChange = {
                viewModel.setCreateUrlDestination(it)
            },
            modifier = Modifier.fillMaxWidth(),
            label = "URL",
            placeholder = "https://google.com",
            enabled = !state.isLoading
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextInput(
            value = state.createUrlVanity,
            onValueChange = {
                viewModel.setCreateUrlVanity(it)
            },
            modifier = Modifier.fillMaxWidth(),
            label = "Vanity",
            placeholder = "example",
            enabled = !state.isLoading,
            description = "Optional field, leave blank to generate a random code."
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextInput(
            value = state.createUrlMaxViews,
            onValueChange = {
                if (NumberRegex.matches(it.text) || it.text.isBlank()) {
                    viewModel.setCreateUrlMaxViews(it)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = "Max Views",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            enabled = !state.isLoading,
            description = "Optional field, leave blank to disable a view limit."
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Select(
            label = "Override Domain",
            description = "Override the domain with this value. This will change the domain returned in your uploads. Leave blank to use the default domain.",
            options = listOf(
                SelectOption(
                    id = "default",
                    label = { enabled ->
                        Text(
                            text = "Default domain",
                            color = if (enabled)
                                MaterialTheme.colorScheme.onBackground
                            else
                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }

                )
            ) + domains.map { domain ->
                SelectOption(
                    id = domain,
                    label = {
                        Text(domain)
                    }
                )
            },
            onSelectionChange = {
                viewModel.setCreateUrlSelectedOverrideDomain(it)
            },
            selectedIds = state.createUrlSelectedOverrideDomain,
            enabled = !state.isLoading
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Switch(
            label = "Enabled",
            checked = state.createUrlEnabled,
            onCheckedChange = {
                viewModel.setCreateUrlEnabled(it)
            },
            enabled = !state.isLoading,
            description = "Prevent or allow this URL from being visited."
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextInput(
            value = state.createUrlPassword,
            onValueChange = {
                viewModel.setCreateUrlPassword(it)
            },
            modifier = Modifier.fillMaxWidth(),
            label = "Password",
            enabled = !state.isLoading,
            description = "Protect your link with a password.",
            isPassword = true
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            enabled = !state.isLoading && state.createUrlDestination.text.isNotBlank(),
            onClick = {
                if (!urlRegex.matches(state.createUrlDestination.text)) {
                    Notification.show(
                        context = context,
                        activity = activity,
                    ) {
                        Text(
                            text = "Invalid Destination URL"
                        )
                    }

                    return@Button
                }

                viewModel.createUrl(
                    context = context,
                    viewState = viewState,
                    destination = state.createUrlDestination.text,
                    vanity = state.createUrlVanity.text.ifBlank { null },
                    enabled = state.createUrlEnabled,
                    maxViews = state.createUrlMaxViews.text.toLongOrNull(),
                    password = state.createUrlPassword.text.ifBlank { null },
                    domain = state.createUrlSelectedOverrideDomain.firstOrNull { it != "default" },
                    onSuccess = {
                        onDismissRequest()
                    },
                    onError = { error ->
                        Notification.show(
                            context = context,
                            activity = activity,
                        ) {
                            Text(
                                text = "Failed to create URL: $error"
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