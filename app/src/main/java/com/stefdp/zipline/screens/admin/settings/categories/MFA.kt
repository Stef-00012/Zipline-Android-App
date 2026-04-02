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
internal fun MFACategory(
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

            var passkeys by remember(settings?.settings?.mfaPasskeysEnabled, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.mfaPasskeysEnabled ?: false)
            }

            Switch(
                checked = passkeys,
                onCheckedChange = { passkeys = it },
                label = "Passkeys",
                description = "Enable the use of passwordless login with the use of WebAuthn passkeys like your phone, security keys, etc.",
                enabled = !isLoading
            )

            var relyingPartyId by remember(settings?.settings?.mfaPasskeysRpID, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.mfaPasskeysRpID ?: ""))
            }

            TextInput(
                value = relyingPartyId,
                onValueChange = { relyingPartyId = it },
                label = "Relying Party ID",
                description = "The Relying Party ID (RP ID) to use for WebAuthn passkeys.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var origin by remember(settings?.settings?.mfaPasskeysOrigin, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.mfaPasskeysOrigin ?: ""))
            }

            TextInput(
                value = origin,
                onValueChange = { origin = it },
                label = "Origin",
                description = "The Origin to use for WebAuthn passkeys.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var enableTOTP by remember(settings?.settings?.mfaTotpEnabled, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.mfaTotpEnabled ?: false)
            }

            Switch(
                checked = enableTOTP,
                onCheckedChange = { enableTOTP = it },
                label = "Enable TOTP",
                description = "Enable Time-based One-Time Passwords with the use of an authenticator app.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var issuer by remember(settings?.settings?.mfaTotpIssuer, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.mfaTotpIssuer ?: ""))
            }

            TextInput(
                value = issuer,
                onValueChange = { issuer = it },
                label = "Issuer",
                description = "The issuer to use for the TOTP token.",
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
                            mfaPasskeysEnabled = passkeys,
                            mfaPasskeysRpID = relyingPartyId.text,
                            mfaPasskeysOrigin = origin.text,
                            mfaTotpEnabled = enableTOTP,
                            mfaTotpIssuer = issuer.text,
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