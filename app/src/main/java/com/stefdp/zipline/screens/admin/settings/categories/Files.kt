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
import com.stefdp.zipline.components.Select
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.FilesFormat
import com.stefdp.zipline.network.models.PartialServerSettingsSettings
import com.stefdp.zipline.network.models.ServerSettings
import com.stefdp.zipline.network.models.requests.UploadCompressionType
import com.stefdp.zipline.utils.NumberRegex
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.compressionFormats
import com.stefdp.zipline.utils.nameFormats
import com.stefdp.zipline.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
internal fun FilesCategory(
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

            var route by remember(settings?.settings?.filesRoute, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.filesRoute ?: "/"))
            }

            TextInput(
                value = route,
                onValueChange = {
                    if (it.text.isBlank()) {
                        route = TextFieldValue("/")
                    }

                    if (it.text.startsWith("/")) {
                        route = it
                    }
                },
                label = "Route",
                description = "The route to use for file uploads. Requires a server restart.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var length by remember(settings?.settings?.filesLength, settingsUpdateTick) {
                mutableStateOf(TextFieldValue((settings?.settings?.filesLength ?: "").toString()))
            }

            TextInput(
                value = length,
                onValueChange = {
                    if (NumberRegex.matches(it.text)) {
                        length = it
                    }
                },
                label = "Length",
                description = "The length of the file name (for randomly generated names).",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var assumeMimetypes by remember(settings?.settings?.filesAssumeMimetypes, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.filesAssumeMimetypes ?: false)
            }

            Switch(
                checked = assumeMimetypes,
                onCheckedChange = { assumeMimetypes = it },
                label = "Assume Mimetypes",
                description = "Assume the mimetype of a file for its extension.",
                enabled = !isLoading
            )

            var removeGPSMetadata by remember(settings?.settings?.filesRemoveGpsMetadata, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.filesRemoveGpsMetadata ?: false)
            }

            Switch(
                checked = removeGPSMetadata,
                onCheckedChange = { removeGPSMetadata = it },
                label = "Remove GPS Metadata",
                description = "Remove GPS metadata from files.",
                enabled = !isLoading
            )

            var selectedDefaultFormat by remember(settings?.settings?.filesDefaultFormat, settingsUpdateTick) {
                mutableStateOf(setOf((settings?.settings?.filesDefaultFormat ?: FilesFormat.RANDOM).toString()))
            }

            Select(
                label = "Default Format",
                description = "The default format to use for file names.",
                options = nameFormats.map { (id, label) ->
                    SelectOption(
                        id = id.toString(),
                        label = { enabled ->
                            Text(
                                text = label,
                                color = if (enabled)
                                    MaterialTheme.colorScheme.onBackground
                                else
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    )
                },
                onSelectionChange = { selectedDefaultFormat = it },
                selectedIds = selectedDefaultFormat,
                enabled = !isLoading
            )

            var disabledExtensions by remember(settings?.settings?.filesDisabledExtensions, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.filesDisabledExtensions?.joinToString(", ") ?: ""))
            }

            TextInput(
                value = disabledExtensions,
                onValueChange = {
                    disabledExtensions = it
                },
                label = "Disabled Extensions",
                description = "Extensions to disable, separated by commas.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var maxFileSize by remember(settings?.settings?.filesMaxFileSize, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.filesMaxFileSize ?: ""))
            }

            TextInput(
                value = maxFileSize,
                onValueChange = {
                    maxFileSize = it
                },
                label = "Max File Size",
                description = "The maximum file size allowed.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var defaultDateFormat by remember(settings?.settings?.filesDefaultDateFormat, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.filesDefaultDateFormat ?: ""))
            }

            TextInput(
                value = defaultDateFormat,
                onValueChange = {
                    defaultDateFormat = it
                },
                label = "Default Date Format",
                description = "The default date format to use.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var defaultExpiration by remember(settings?.settings?.filesDefaultExpiration, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.filesDefaultExpiration ?: ""))
            }

            TextInput(
                value = defaultExpiration,
                onValueChange = {
                    defaultExpiration = it
                },
                label = "Default Expiration",
                description = "The default expiration time for files.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var maxExpiration by remember(settings?.settings?.filesMaxExpiration, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.filesMaxExpiration ?: ""))
            }

            TextInput(
                value = maxExpiration,
                onValueChange = {
                    maxExpiration = it
                },
                label = "Max Expiration",
                description = "The maximum expiration time allowed for files.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var randomWordsAdjectivesNumber by remember(settings?.settings?.filesRandomWordsNumAdjectives, settingsUpdateTick) {
                mutableStateOf(TextFieldValue((settings?.settings?.filesRandomWordsNumAdjectives ?: "").toString()))
            }

            TextInput(
                value = randomWordsAdjectivesNumber,
                onValueChange = {
                    if (NumberRegex.matches(it.text)) {
                        randomWordsAdjectivesNumber = it
                    }
                },
                label = "Random Words Num Adjectives",
                description = "The number of adjectives to use for the random-words/gfycat format.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var randomWordsSeparator by remember(settings?.settings?.filesRandomWordsSeparator, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.filesRandomWordsSeparator ?: ""))
            }

            TextInput(
                value = randomWordsSeparator,
                onValueChange = {
                    randomWordsSeparator = it
                },
                label = "Random Words Separator",
                description = "The separator to use for the random-words/gfycat format.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var selectedDefaultCompressionFormat by remember(settings?.settings?.filesDefaultCompressionFormat, settingsUpdateTick) {
                mutableStateOf(setOf((settings?.settings?.filesDefaultCompressionFormat ?: UploadCompressionType.PNG).toString()))
            }

            Select(
                label = "Default Compression Format",
                description = "The default image compression format to use when only a compression percent is specified.",
                options = compressionFormats.map { (id, label) ->
                    SelectOption(
                        id = id.toString(),
                        label = { enabled ->
                            Text(
                                text = label,
                                color = if (enabled)
                                    MaterialTheme.colorScheme.onBackground
                                else
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    )
                },
                onSelectionChange = { selectedDefaultCompressionFormat = it },
                selectedIds = selectedDefaultCompressionFormat,
                enabled = !isLoading
            )

            var maxFilesPerUpload by remember(settings?.settings?.filesMaxFilesPerUpload, settingsUpdateTick) {
                mutableStateOf(TextFieldValue((settings?.settings?.filesMaxFilesPerUpload ?: "").toString()))
            }

            TextInput(
                value = maxFilesPerUpload,
                onValueChange = {
                    if (NumberRegex.matches(it.text)) {
                        maxFilesPerUpload = it
                    }
                },
                label = "Max Files Per Upload",
                description = "The maximum number of files allowed per upload. Requires a server restart.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            val coroutineScope = rememberCoroutineScope()

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    coroutineScope.launch {
                        setLoading(true)

                        val defaultFormat = nameFormats.firstOrNull {
                            it.first.toString() == selectedDefaultFormat.firstOrNull()
                        }?.first

                        val defaultCompressionFormat = compressionFormats.firstOrNull {
                            it.first.toString() == selectedDefaultCompressionFormat.firstOrNull()
                        }?.first

                        val data = PartialServerSettingsSettings(
                            filesRoute = route.text,
                            filesLength = length.text.toLongOrNull(),
                            filesAssumeMimetypes = assumeMimetypes,
                            filesRemoveGpsMetadata = removeGPSMetadata,
                            filesDefaultFormat = defaultFormat,
                            filesDisabledExtensions = disabledExtensions.text
                                .split(", ", ",")
                                .map { it.trim() }
                                .filter { it.isNotEmpty() },
                            filesMaxFileSize = maxFileSize.text,
                            filesDefaultDateFormat = defaultDateFormat.text,
                            filesDefaultExpiration = defaultExpiration.text,
                            filesMaxExpiration = maxExpiration.text,
                            filesRandomWordsNumAdjectives = randomWordsAdjectivesNumber.text.toLongOrNull(),
                            filesRandomWordsSeparator = randomWordsSeparator.text,
                            filesDefaultCompressionFormat = defaultCompressionFormat,
                            filesMaxFilesPerUpload = maxFilesPerUpload.text.toLongOrNull(),
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