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
import com.stefdp.zipline.network.models.RatelimitSettings
import com.stefdp.zipline.network.models.ServerSettings
import com.stefdp.zipline.screens.admin.settings.AdminSettingsUiState
import com.stefdp.zipline.screens.admin.settings.AdminSettingsViewModel
import com.stefdp.zipline.utils.NumberRegex
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
internal fun RatelimitCategory(
    updateSettings: (RatelimitSettings) -> Unit,
    title: String,
    viewModel: AdminSettingsViewModel,
    state: AdminSettingsUiState,
) {
    Container(
        scrollable = false,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
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

            Switch(
                checked = state.ratelimitEnabled,
                onCheckedChange = {
                    viewModel.setRatelimitEnabled(it)
                },
                label = "Enable Ratelimit",
                description = "Enable ratelimiting for the server.",
                enabled = !state.isLoading
            )

            Switch(
                checked = state.ratelimitAdminBypass,
                onCheckedChange = {
                    viewModel.setRatelimitAdminBypass(it)
                },
                label = "Admin Bypass",
                description = "Allow admins to bypass the ratelimit.",
                enabled = !state.isLoading
            )

            TextInput(
                value = state.ratelimitMax,
                onValueChange = {
                    if (NumberRegex.matches(it.text)) {
                        viewModel.setRatelimitMax(it)
                    }
                },
                label = "Max Requests",
                description = "The maximum number of requests allowed within the window. If no window is set, this is the maximum number of requests until it reaches the limit.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.ratelimitWindow,
                onValueChange = {
                    if (NumberRegex.matches(it.text)) {
                        viewModel.setRatelimitWindow(it)
                    }
                },
                label = "Window",
                description = "The window in seconds to allow the max requests.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.ratelimitAllowList,
                onValueChange = {
                    viewModel.setRatelimitAllowList(it)
                },
                label = "Allow List",
                description = "A comma-separated list of IP addresses to bypass the ratelimit.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val data = RatelimitSettings(
                        ratelimitEnabled = state.ratelimitEnabled,
                        ratelimitAdminBypass = state.ratelimitAdminBypass,
                        ratelimitMax = state.ratelimitMax.text.toLongOrNull(),
                        ratelimitWindow = state.ratelimitWindow.text.toLongOrNull(),
                        ratelimitAllowList = state.ratelimitAllowList.text
                            .split(", ", ",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() },
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