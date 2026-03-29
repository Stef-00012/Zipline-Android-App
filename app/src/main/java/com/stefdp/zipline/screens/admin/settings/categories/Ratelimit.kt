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
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.PartialServerSettingsSettings
import com.stefdp.zipline.network.models.ServerSettings
import com.stefdp.zipline.utils.NumberRegex
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
internal fun RatelimitCategory(
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
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(12.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )

                Text(
                    text = "All options require a restart to take effect.",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

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

            var enableRatelimit by remember(settings?.settings?.ratelimitEnabled, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.ratelimitEnabled ?: false)
            }

            Switch(
                checked = enableRatelimit,
                onCheckedChange = { enableRatelimit = it },
                label = "Enable Ratelimit",
                description = "Enable ratelimiting for the server.",
                enabled = !isLoading
            )

            var adminBypass by remember(settings?.settings?.ratelimitAdminBypass, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.ratelimitAdminBypass ?: false)
            }

            Switch(
                checked = adminBypass,
                onCheckedChange = { adminBypass = it },
                label = "Admin Bypass",
                description = "Allow admins to bypass the ratelimit.",
                enabled = !isLoading
            )

            var maxRequests by remember(settings?.settings?.ratelimitMax, settingsUpdateTick) {
                mutableStateOf(TextFieldValue((settings?.settings?.ratelimitMax ?: "").toString()))
            }

            TextInput(
                value = maxRequests,
                onValueChange = {
                    if (NumberRegex.matches(it.text)) {
                        maxRequests = it
                    }
                },
                label = "Max Requests",
                description = "The maximum number of requests allowed within the window. If no window is set, this is the maximum number of requests until it reaches the limit.",
                enabled = !isLoading && settings?.settings?.invitesEnabled == true,
                modifier = Modifier.fillMaxWidth()
            )

            var window by remember(settings?.settings?.ratelimitWindow, settingsUpdateTick) {
                mutableStateOf(TextFieldValue((settings?.settings?.ratelimitWindow ?: "").toString()))
            }

            TextInput(
                value = window,
                onValueChange = {
                    if (NumberRegex.matches(it.text)) {
                        window = it
                    }
                },
                label = "Window",
                description = "The window in seconds to allow the max requests.",
                enabled = !isLoading && settings?.settings?.invitesEnabled == true,
                modifier = Modifier.fillMaxWidth()
            )

            var allowList by remember(settings?.settings?.ratelimitAllowList, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.ratelimitAllowList?.joinToString(", ") ?: ""))
            }

            TextInput(
                value = allowList,
                onValueChange = {
                    allowList = it
                },
                label = "Allow List",
                description = "A comma-separated list of IP addresses to bypass the ratelimit.",
                enabled = !isLoading && settings?.settings?.invitesEnabled == true,
                modifier = Modifier.fillMaxWidth()
            )

            val coroutineScope = rememberCoroutineScope()

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    coroutineScope.launch {
                        setLoading(true)

                        val data = PartialServerSettingsSettings(
                            ratelimitEnabled = enableRatelimit,
                            ratelimitAdminBypass = adminBypass,
                            ratelimitMax = maxRequests.text.toLongOrNull(),
                            ratelimitWindow = window.text.toLongOrNull(),
                            ratelimitAllowList = allowList.text
                                .split(", ", ",")
                                .map { it.trim() }
                                .filter { it.isNotEmpty() },
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