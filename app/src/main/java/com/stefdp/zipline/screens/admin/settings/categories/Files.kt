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
import com.stefdp.zipline.screens.admin.settings.AdminSettingsUiState
import com.stefdp.zipline.screens.admin.settings.AdminSettingsViewModel
import com.stefdp.zipline.utils.NumberRegex
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.compressionFormats
import com.stefdp.zipline.utils.nameFormats
import com.stefdp.zipline.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
internal fun FilesCategory(
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
                value = state.filesRoute,
                onValueChange = {
                    if (it.text.isBlank()) {
                        viewModel.setFilesRoute(TextFieldValue("/"))
                    }

                    if (it.text.startsWith("/")) {
                        viewModel.setFilesRoute(it)
                    }
                },
                label = "Route",
                description = "The route to use for file uploads. Requires a server restart.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            
            TextInput(
                value = state.filesLength,
                onValueChange = {
                    if (NumberRegex.matches(it.text)) {
                        viewModel.setFilesLength(it)
                    }
                },
                label = "Length",
                description = "The length of the file name (for randomly generated names).",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Switch(
                checked = state.filesAssumeMimetypes,
                onCheckedChange = {
                    viewModel.setFilesAssumeMimetypes(it)
                },
                label = "Assume Mimetypes",
                description = "Assume the mimetype of a file for its extension.",
                enabled = !state.isLoading
            )

            Switch(
                checked = state.filesRemoveGpsMetadata,
                onCheckedChange = {
                    viewModel.setFilesRemoveGpsMetadata(it)
                },
                label = "Remove GPS Metadata",
                description = "Remove GPS metadata from files.",
                enabled = !state.isLoading
            )

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
                onSelectionChange = {
                    viewModel.setFilesSelectedDefaultFormat(it)
                },
                selectedIds = state.filesSelectedDefaultFormat,
                enabled = !state.isLoading
            )

            TextInput(
                value = state.filesDisabledExtensions,
                onValueChange = {
                    viewModel.setFilesDisabledExtensions(it)
                },
                label = "Disabled Extensions",
                description = "Extensions to disable, separated by commas.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.filesMaxFileSize,
                onValueChange = {
                    viewModel.setFilesMaxFileSize(it)
                },
                label = "Max File Size",
                description = "The maximum file size allowed.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.filesDefaultDateFormat,
                onValueChange = {
                    viewModel.setFilesDefaultDateFormat(it)
                },
                label = "Default Date Format",
                description = "The default date format to use.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.filesDefaultExpiration,
                onValueChange = {
                    viewModel.setFilesDefaultExpiration(it)
                },
                label = "Default Expiration",
                description = "The default expiration time for files.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.filesMaxExpiration,
                onValueChange = {
                    viewModel.setFilesMaxExpiration(it)
                },
                label = "Max Expiration",
                description = "The maximum expiration time allowed for files.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.filesRandomWordsNumAdjectives,
                onValueChange = {
                    if (NumberRegex.matches(it.text)) {
                        viewModel.setFilesRandomWordsNumAdjectives(it)
                    }
                },
                label = "Random Words Num Adjectives",
                description = "The number of adjectives to use for the random-words/gfycat format.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.filesRandomWordsSeparator,
                onValueChange = {
                    viewModel.setFilesRandomWordsSeparator(it)
                },
                label = "Random Words Separator",
                description = "The separator to use for the random-words/gfycat format.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

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
                onSelectionChange = {
                    viewModel.setFilesSelectedDefaultCompressionFormat(it)
                },
                selectedIds = state.filesSelectedDefaultCompressionFormat,
                enabled = !state.isLoading
            )

            TextInput(
                value = state.filesMaxFilesPerUpload,
                onValueChange = {
                    if (NumberRegex.matches(it.text)) {
                        viewModel.setFilesMaxFilesPerUpload(it)
                    }
                },
                label = "Max Files Per Upload",
                description = "The maximum number of files allowed per upload. Requires a server restart.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val defaultFormat = nameFormats.firstOrNull {
                        it.first.toString() == state.filesSelectedDefaultFormat.firstOrNull()
                    }?.first

                    val defaultCompressionFormat = compressionFormats.firstOrNull {
                        it.first.toString() == state.filesSelectedDefaultCompressionFormat.firstOrNull()
                    }?.first

                    val data = PartialServerSettingsSettings(
                        filesRoute = state.filesRoute.text,
                        filesLength = state.filesLength.text.toLongOrNull(),
                        filesAssumeMimetypes = state.filesAssumeMimetypes,
                        filesRemoveGpsMetadata = state.filesRemoveGpsMetadata,
                        filesDefaultFormat = defaultFormat,
                        filesDisabledExtensions = state.filesDisabledExtensions.text
                            .split(", ", ",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() },
                        filesMaxFileSize = state.filesMaxFileSize.text,
                        filesDefaultDateFormat = state.filesDefaultDateFormat.text,
                        filesDefaultExpiration = state.filesDefaultExpiration.text.takeIf { it.isNotBlank() },
                        filesMaxExpiration = state.filesMaxExpiration.text.takeIf { it.isNotBlank() },
                        filesRandomWordsNumAdjectives = state.filesRandomWordsNumAdjectives.text.toLongOrNull(),
                        filesRandomWordsSeparator = state.filesRandomWordsSeparator.text,
                        filesDefaultCompressionFormat = defaultCompressionFormat,
                        filesMaxFilesPerUpload = state.filesMaxFilesPerUpload.text.toLongOrNull(),
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