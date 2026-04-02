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
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
internal fun CoreCategory(
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(12.dp)
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

            var returnHttpsUrls by remember(settings?.settings?.coreReturnHttpsUrls, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.coreReturnHttpsUrls ?: false)
            }

            Switch(
                checked = returnHttpsUrls,
                onCheckedChange = { returnHttpsUrls = it },
                label = "Return HTTPS URLs",
                description = "Return URLs with HTTPS protocol.",
                enabled = !isLoading
            )

            var trustProxies by remember(settings?.settings?.coreTrustProxy, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.coreTrustProxy ?: false)
            }

            Switch(
                checked = trustProxies,
                onCheckedChange = { trustProxies = it },
                label = "Trust Proxies",
                description = "Trust the X-Forwarded-* headers set by proxies. Only enable this if you are behind a trusted proxy (nginx, caddy, etc.). Requires a server restart.",
                enabled = !isLoading
            )

            var defaultDomain by remember(settings?.settings?.coreDefaultDomain, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.coreDefaultDomain ?: ""))
            }

            TextInput(
                value = defaultDomain,
                onValueChange = { defaultDomain = it },
                label = "Default Domain",
                description = "The domain to use when generating URLs. This value should not include the protocol.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var temporaryDirectory by remember(settings?.settings?.coreTempDirectory, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.coreTempDirectory ?: ""))
            }

            TextInput(
                value = temporaryDirectory,
                onValueChange = { temporaryDirectory = it },
                label = "Temporary Directory",
                description = "The directory to store temporary files. If the path is invalid, certain functions may break. Requires a server restart.",
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
                            coreReturnHttpsUrls = returnHttpsUrls,
                            coreTrustProxy = trustProxies,
                            coreDefaultDomain = defaultDomain.text.takeIf { it.isNotBlank() },
                            coreTempDirectory = temporaryDirectory.text,
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