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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.CoreSettings
import com.stefdp.zipline.screens.admin.settings.AdminSettingsUiState
import com.stefdp.zipline.screens.admin.settings.AdminSettingsViewModel
import com.stefdp.zipline.utils.verticalScrollWithScrollbar

@Composable
internal fun CoreCategory(
    updateSettings: (CoreSettings) -> Unit,
    title: String,
    viewModel: AdminSettingsViewModel,
    state: AdminSettingsUiState
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

            Switch(
                checked = state.coreReturnHttpsUrls,
                onCheckedChange = {
                    viewModel.setCoreReturnHttpsUrls(it)
                },
                label = "Return HTTPS URLs",
                description = "Return URLs with HTTPS protocol.",
                enabled = !state.isLoading && "coreReturnHttpsUrls" !in state.tamperedSettings
            )

            Switch(
                checked = state.coreTrustProxy,
                onCheckedChange = {
                    viewModel.setCoreTrustProxy(it)
                },
                label = "Trust Proxies",
                description = "Trust the X-Forwarded-* headers set by proxies. Only enable this if you are behind a trusted proxy (nginx, caddy, etc.). Requires a server restart.",
                enabled = !state.isLoading && "coreTrustProxy" !in state.tamperedSettings
            )

            TextInput(
                value = state.coreDefaultDomain,
                onValueChange = {
                    viewModel.setCoreDefaultDomain(it)
                },
                label = "Default Domain",
                description = "The domain to use when generating URLs. This value should not include the protocol.",
                enabled = !state.isLoading && "coreDefaultDomain" !in state.tamperedSettings,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.coreTempDirectory,
                onValueChange = {
                    viewModel.setCoreTempDirectory(it)
                },
                label = "Temporary Directory",
                description = "The directory to store temporary files. If the path is invalid, certain functions may break. Requires a server restart.",
                enabled = !state.isLoading && "coreTempDirectory" !in state.tamperedSettings,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val data = CoreSettings(
                        coreReturnHttpsUrls = state.coreReturnHttpsUrls,
                        coreTrustProxy = state.coreTrustProxy,
                        coreDefaultDomain = state.coreDefaultDomain.text.takeIf { it.isNotBlank() },
                        coreTempDirectory = state.coreTempDirectory.text,
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