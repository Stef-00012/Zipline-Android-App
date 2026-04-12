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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.components.colorpicker.ColorPicker
import com.stefdp.zipline.network.models.PwaSettings
import com.stefdp.zipline.network.models.ServerSettings
import com.stefdp.zipline.screens.admin.settings.AdminSettingsUiState
import com.stefdp.zipline.screens.admin.settings.AdminSettingsViewModel
import com.stefdp.zipline.utils.toHex
import com.stefdp.zipline.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
internal fun PWACategory(
    updateSettings: (PwaSettings) -> Unit,
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
                    text = "Refresh the website after enabling PWA to see any changes.",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            Switch(
                checked = state.pwaEnabled,
                onCheckedChange = {
                    viewModel.setPwaEnabled(it)
                },
                label = "PWA Enabled",
                description = "Allow users to install the Zipline PWA on their devices.",
                enabled = !state.isLoading
            )

            TextInput(
                value = state.pwaTitle,
                onValueChange = {
                    viewModel.setPwaTitle(it)
                },
                label = "Title",
                description = "The title for the PWA.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.pwaShortName,
                onValueChange = {
                    viewModel.setPwaShortName(it)
                },
                label = "Short Name",
                description = "The short name for the PWA.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.pwaDescription,
                onValueChange = {
                    viewModel.setPwaDescription(it)
                },
                label = "Description",
                description = "he description for the PWA.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            ColorPicker(
                color = state.pwaThemeColor,
                onColorChange = {
                    viewModel.setPwaThemeColor(it)
                },
                label = "Theme Color",
                description = "The theme color for the PWA.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            ColorPicker(
                color = state.pwaBackgroundColor,
                onColorChange = {
                    viewModel.setPwaBackgroundColor(it)
                },
                label = "Background Color",
                description = "The background color for the PWA.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val data = PwaSettings(
                        pwaEnabled = state.pwaEnabled,
                        pwaTitle = state.pwaTitle.text.takeIf { it.isNotBlank() },
                        pwaShortName = state.pwaShortName.text.takeIf { it.isNotBlank() },
                        pwaDescription = state.pwaDescription.text.takeIf { it.isNotBlank() },
                        pwaThemeColor = state.pwaThemeColor.toHex(),
                        pwaBackgroundColor = state.pwaBackgroundColor.toHex(),
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