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
import com.stefdp.zipline.network.models.PartialServerSettingsSettings
import com.stefdp.zipline.network.models.ServerSettings
import com.stefdp.zipline.utils.toHex
import com.stefdp.zipline.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
internal fun PWACategory(
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
        val scrollState = rememberScrollState()

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
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

            var pwaEnabled by remember(settings?.settings?.pwaEnabled, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.pwaEnabled ?: false)
            }

            Switch(
                checked = pwaEnabled,
                onCheckedChange = { pwaEnabled = it },
                label = "PWA Enabled",
                description = "Allow users to install the Zipline PWA on their devices.",
                enabled = !isLoading
            )

            var title by remember(settings?.settings?.pwaTitle, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.pwaTitle ?: ""))
            }

            TextInput(
                value = title,
                onValueChange = {
                    title = it
                },
                label = "Title",
                description = "The title for the PWA.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var shortName by remember(settings?.settings?.pwaShortName, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.pwaShortName ?: ""))
            }

            TextInput(
                value = shortName,
                onValueChange = {
                    shortName = it
                },
                label = "Short Name",
                description = "The short name for the PWA.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var description by remember(settings?.settings?.pwaDescription, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.pwaDescription ?: ""))
            }

            TextInput(
                value = description,
                onValueChange = {
                    description = it
                },
                label = "Description",
                description = "he description for the PWA.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var themeColor by remember(settings?.settings?.pwaThemeColor, settingsUpdateTick) {
                val color = settings?.settings?.pwaThemeColor?.toColorInt()

                mutableStateOf(if (color != null) Color(color) else Color.Black)
            }

            ColorPicker(
                color = themeColor,
                onColorChange = { themeColor = it },
                label = "Theme Color",
                description = "The theme color for the PWA.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var backgroundColor by remember(settings?.settings?.pwaBackgroundColor, settingsUpdateTick) {
                val color = settings?.settings?.pwaBackgroundColor?.toColorInt()

                mutableStateOf(if (color != null) Color(color) else Color.Black)
            }

            ColorPicker(
                color = backgroundColor,
                onColorChange = { backgroundColor = it },
                label = "Background Color",
                description = "The background color for the PWA.",
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
                            pwaEnabled = pwaEnabled,
                            pwaTitle = title.text.takeIf { it.isNotBlank() },
                            pwaShortName = shortName.text.takeIf { it.isNotBlank() },
                            pwaDescription = description.text.takeIf { it.isNotBlank() },
                            pwaThemeColor = themeColor.toHex(),
                            pwaBackgroundColor = backgroundColor.toHex(),
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