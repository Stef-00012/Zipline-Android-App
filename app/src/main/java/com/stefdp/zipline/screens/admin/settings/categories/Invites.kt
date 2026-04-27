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
import com.stefdp.zipline.network.models.InvitesSettings
import com.stefdp.zipline.screens.admin.settings.AdminSettingsUiState
import com.stefdp.zipline.screens.admin.settings.AdminSettingsViewModel
import com.stefdp.zipline.utils.NumberRegex
import com.stefdp.zipline.utils.verticalScrollWithScrollbar

@Composable
internal fun InvitesCategory(
    updateSettings: (InvitesSettings) -> Unit,
    title: String,
    viewModel: AdminSettingsViewModel,
    state: AdminSettingsUiState,
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
                checked = state.invitesEnabled,
                onCheckedChange = {
                    viewModel.setInvitesEnabled(it)
                },
                label = "Invites Enabled",
                description = "Enable the use of invite links to register new users.",
                enabled = !state.isLoading && "invitesEnabled" !in state.tamperedSettings
            )

            TextInput(
                value = state.invitesLength,
                onValueChange = {
                    if (NumberRegex.matches(it.text)) {
                        viewModel.setInvitesLength(it)
                    }
                },
                label = "Length",
                description = "The length of the invite code.",
                enabled = !state.isLoading && state.settings?.settings?.invitesEnabled == true && "invitesLength" !in state.tamperedSettings,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val data = InvitesSettings(
                        invitesEnabled = state.invitesEnabled,
                        invitesLength = state.invitesLength.text.toLongOrNull(),
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