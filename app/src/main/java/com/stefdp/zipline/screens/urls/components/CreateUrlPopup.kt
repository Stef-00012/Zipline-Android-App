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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.LocalWebSettings
import com.stefdp.zipline.Logger
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.components.Select
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.components.largefiledisplay.IconButton
import com.stefdp.zipline.network.requests.createUrl
import com.stefdp.zipline.utils.NumberRegex
import kotlinx.coroutines.launch

val urlRegex = Regex("""^https?://([a-zA-Z0-9-]+\.)+[a-zA-Z]{2,}(?:[/?#]\S*)?$""")

@Composable
fun CreateUrlPopup(
    context: Context,
    activity: FragmentActivity,
    showPopup: Boolean,
    onDismissRequest: () -> Unit,
    updateUrls: suspend () -> Unit,
    baseUrl: String? = null,
) {
    var isLoading by remember { mutableStateOf(false) }

    val webSettings = LocalWebSettings.current

    val domains = webSettings?.config?.domains ?: emptyList()

    var errorMessage by remember { mutableStateOf<String?>(null) }

    var createdUrl by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    Popup(
        showPopup = createdUrl != null,
        onDismissRequest = {
            createdUrl = null
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
                text = "$createdUrl",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.tertiary,
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier.clickable(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, createdUrl?.toUri())
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
                            val clipData = ClipData.newRawUri("URL", createdUrl?.toUri()).toClipEntry()

                            clipboardManager.setClipEntry(clipData)

                            Notification.show(
                                context = context,
                                activity = activity,
                                content = {
                                    Text(
                                        text = "URL copied to clipboard"
                                    )
                                }
                            )
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
                            val intent = Intent(Intent.ACTION_VIEW, createdUrl?.toUri())
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

        var destination by remember { mutableStateOf(TextFieldValue(baseUrl ?: "")) }

        TextInput(
            value = destination,
            onValueChange = { destination = it },
            modifier = Modifier.fillMaxWidth(),
            label = "URL",
            placeholder = "https://google.com",
            enabled = !isLoading
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        var vanity by remember { mutableStateOf(TextFieldValue("")) }

        TextInput(
            value = vanity,
            onValueChange = { vanity = it },
            modifier = Modifier.fillMaxWidth(),
            label = "Vanity",
            placeholder = "example",
            enabled = !isLoading,
            description = "Optional field, leave blank to generate a random code."
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        var maxViews by remember { mutableStateOf(TextFieldValue("")) }

        TextInput(
            value = maxViews,
            onValueChange = {
                if (NumberRegex.matches(it.text) || it.text.isBlank()) {
                    maxViews = it
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = "Max Views",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            enabled = !isLoading,
            description = "Optional field, leave blank to disable a view limit."
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        var selectedOverrideDomain by remember { mutableStateOf(setOf("default")) }

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
            onSelectionChange = { selectedOverrideDomain = it },
            selectedIds = selectedOverrideDomain,
            enabled = !isLoading
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        var enabled by remember { mutableStateOf(true) }

        Switch(
            label = "Enabled",
            checked = enabled,
            onCheckedChange = { enabled = it },
            enabled = !isLoading,
            description = "Prevent or allow this URL from being visited."
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
            description = "Protect your link with a password.",
            isPassword = true
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            enabled = !isLoading,
            onClick = {
                if (!urlRegex.matches(destination.text)) {
                    errorMessage = "Invalid URL"

                    return@Button
                }

                coroutineScope.launch {
                    isLoading = true

                    val createUrlRes = createUrl(
                        context = context,
                        destination = destination.text,
                        vanity = vanity.text.ifBlank { null },
                        maxViews = maxViews.text.toLongOrNull(),
                        enabled = enabled,
                        domain = selectedOverrideDomain.firstOrNull { it != "default" },
                        password = password.text.ifBlank { null }
                    )

                    createUrlRes
                        .onSuccess {
                            Notification.show(
                                context = context,
                                activity = activity,
                                content = {
                                    Text(
                                        text = "URL created successfully"
                                    )
                                }
                            )

                            createdUrl = it.url

                            updateUrls()
                            onDismissRequest()
                        }
                        .onFailure {
                            Logger.error("CreateUrlPopup", "Failed to create url", it)

                            errorMessage = it.message ?: "Something went wrong..."

                            Notification.show(
                                context = context,
                                activity = activity,
                                content = {
                                    Text(
                                        text = "Failed to create URL"
                                    )
                                }
                            )
                        }

                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Create")
        }
    }
}