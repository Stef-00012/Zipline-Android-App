package com.stefdp.zipline.screens.urls.components

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.LocalWebSettings
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.Url
import com.stefdp.zipline.network.requests.createUrl
import com.stefdp.zipline.network.requests.updateUrl
import com.stefdp.zipline.utils.NumberRegex
import kotlinx.coroutines.launch

@Composable
fun EditUrlPopup(
    url: Url?,
    context: Context,
    showPopup: Boolean,
    onDismissRequest: () -> Unit,
    updateUrls: suspend () -> Unit
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
                text = "Editing \"${url?.code}\"",
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

        var destination by remember { mutableStateOf(TextFieldValue(url?.destination ?: "")) }

        TextInput(
            value = destination,
            onValueChange = { destination = it },
            modifier = Modifier.fillMaxWidth(),
            label = "Destination",
            placeholder = "https://google.com",
            enabled = !isLoading
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        var vanity by remember { mutableStateOf(TextFieldValue(url?.vanity ?: "")) }

        TextInput(
            value = vanity,
            onValueChange = { vanity = it },
            modifier = Modifier.fillMaxWidth(),
            label = "Vanity",
            placeholder = "example",
            enabled = !isLoading,
            description = "A custom alias for your URL. Leave blank to use the randomly generated code."
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        var maxViews by remember { mutableStateOf(TextFieldValue(
            if (url?.maxViews != null)
                (url.maxViews.takeIf { it > 0L } ?: "").toString()
            else ""
        )) }

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
            description = "The maximum number of clicks this URL can have before it is automatically deleted. Leave blank to allow as many views as you want."
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        var enabled by remember { mutableStateOf(url?.enabled ?: true) }

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
            description = "Set a password for this URL. Leave blank to disable password protection.",
            isPassword = true
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            enabled = !isLoading,
            onClick = {
                if (!urlRegex.matches(destination.text)) {
                    errorMessage = "Invalid Destination"

                    return@Button
                }

                coroutineScope.launch {
                    if (url == null) return@launch

                    isLoading = true

                    val editUrlRes = updateUrl(
                        urlId = url.id,
                        context = context,
                        destination = destination.text,
                        vanity = vanity.text.takeIf { it.isNotBlank() },
                        maxViews = maxViews.text.toLongOrNull(),
                        enabled = enabled,
                        password = password.text.takeIf { it.isNotBlank() }
                    )

                    editUrlRes
                        .onSuccess {
                            Toast.makeText(
                                context,
                                "URL updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            updateUrls()
                            onDismissRequest()
                        }
                        .onFailure {
                            Log.e("EditUrlPopup", "Failed to update url", it)

                            errorMessage = it.message

                            Toast.makeText(
                                context,
                                "Failed to update URL",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Save Changes")
        }
    }
}