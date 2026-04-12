package com.stefdp.zipline.screens.admin.settings.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.PartialServerSettingsSettings
import com.stefdp.zipline.network.models.ServerSettings
import com.stefdp.zipline.components.IconButton
import com.stefdp.zipline.screens.admin.settings.AdminSettingsUiState
import com.stefdp.zipline.screens.admin.settings.AdminSettingsViewModel
import com.stefdp.zipline.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
internal fun DomainsCategory(
    updateSettings: (PartialServerSettingsSettings) -> Unit,
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

            TextInput(
                value = state.newDomain,
                onValueChange = {
                    viewModel.setNewDomain(it)
                },
                description = "Enter a domain name.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = painterResource(R.drawable.add),
                trailingIconColor = MaterialTheme.colorScheme.primary,
                onTrailingIconPress = {
                    val data = PartialServerSettingsSettings(
                        domains = state.domains + state.newDomain.text.trim()
                    )

                    viewModel.setNewDomain(TextFieldValue(""))

                    updateSettings(data)
                }
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.domains.forEach { domain ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = domain
                        )

                        IconButton(
                            icon = painterResource(R.drawable.delete),
                            iconContentDescription = "Delete Domain",
                            color = MaterialTheme.colorScheme.error,
                            iconColor = MaterialTheme.colorScheme.onError,
                            onClick = {
                                val data = PartialServerSettingsSettings(
                                    domains = state.domains - domain
                                )

                                updateSettings(data)
                            }
                        )
                    }
                }
            }
        }
    }
}