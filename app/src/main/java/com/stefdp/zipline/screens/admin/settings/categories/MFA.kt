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
import com.stefdp.zipline.screens.admin.settings.AdminSettingsUiState
import com.stefdp.zipline.screens.admin.settings.AdminSettingsViewModel
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
internal fun MFACategory(
    updateSettings: (PartialServerSettingsSettings) -> Unit,
    title: String,
    viewModel: AdminSettingsViewModel,
    state: AdminSettingsUiState
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

            Switch(
                checked = state.mfaPasskeysEnabled,
                onCheckedChange = {
                    viewModel.setMfaPasskeysEnabled(it)
                },
                label = "Passkeys",
                description = "Enable the use of passwordless login with the use of WebAuthn passkeys like your phone, security keys, etc.",
                enabled = !state.isLoading
            )

            TextInput(
                value = state.mfaPasskeysRpID,
                onValueChange = {
                    viewModel.setMfaPasskeysRpID(it)
                },
                label = "Relying Party ID",
                description = "The Relying Party ID (RP ID) to use for WebAuthn passkeys.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.mfaPasskeysOrigin,
                onValueChange = {
                    viewModel.setMfaPasskeysOrigin(it)
                },
                label = "Origin",
                description = "The Origin to use for WebAuthn passkeys.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Switch(
                checked = state.mfaTotpEnabled,
                onCheckedChange = {
                    viewModel.setMfaTotpEnabled(it)
                },
                label = "Enable TOTP",
                description = "Enable Time-based One-Time Passwords with the use of an authenticator app.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.mfaTotpIssuer,
                onValueChange = {
                    viewModel.setMfaTotpIssuer(it)
                },
                label = "Issuer",
                description = "The issuer to use for the TOTP token.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val data = PartialServerSettingsSettings(
                        mfaPasskeysEnabled = state.mfaPasskeysEnabled,
                        mfaPasskeysRpID = state.mfaPasskeysRpID.text,
                        mfaPasskeysOrigin = state.mfaPasskeysOrigin.text,
                        mfaTotpEnabled = state.mfaTotpEnabled,
                        mfaTotpIssuer = state.mfaTotpIssuer.text,
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