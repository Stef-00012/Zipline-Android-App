package com.stefdp.zipline.screens.admin.settings.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.stefdp.zipline.network.models.ChunksSettings
import com.stefdp.zipline.screens.admin.settings.AdminSettingsUiState
import com.stefdp.zipline.screens.admin.settings.AdminSettingsViewModel

@Composable
internal fun ChunksCategory(
    updateSettings: (ChunksSettings) -> Unit,
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
                checked = state.chunksEnabled,
                onCheckedChange = {
                    viewModel.setChunksEnabled(it)
                },
                label = "Enable Chunks",
                description = "Enable chunked uploads.",
                enabled = !state.isLoading && "chunksEnabled" !in state.tamperedSettings
            )

            TextInput(
                value = state.chunksMax,
                onValueChange = {
                    viewModel.setChunksMax(it)
                },
                label = "Max Chunk Size",
                description = "Maximum size of an upload before it is split into chunks.",
                enabled = !state.isLoading && "chunksMax" !in state.tamperedSettings,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.chunksSize,
                onValueChange = {
                    viewModel.setChunksSize(it)
                },
                label = "Chunk Size",
                description = "Size of each chunk.",
                enabled = !state.isLoading && "chunksSize" !in state.tamperedSettings,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val data = ChunksSettings(
                        chunksEnabled = state.chunksEnabled,
                        chunksMax = state.chunksMax.text,
                        chunksSize = state.chunksSize.text,
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