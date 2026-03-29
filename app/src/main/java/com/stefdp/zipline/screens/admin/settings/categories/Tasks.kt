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
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.PartialServerSettingsSettings
import com.stefdp.zipline.network.models.ServerSettings
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
internal fun TasksCategory(
    settings: ServerSettings?,
    updateSettings: suspend (PartialServerSettingsSettings) -> List<String>,
    isLoading: Boolean,
    setLoading: (Boolean) -> Unit,
    title: String
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
                    text = "All options require a restart to take effect.",
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

            var deleteFilesInterval by remember(settings?.settings?.tasksDeleteInterval) {
                mutableStateOf(TextFieldValue(settings?.settings?.tasksDeleteInterval ?: ""))
            }

            TextInput(
                value = deleteFilesInterval,
                onValueChange = { deleteFilesInterval = it },
                label = "Delete Files Interval",
                description = "How often to check and delete expired files.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var clearInvitesInterval by remember(settings?.settings?.tasksClearInvitesInterval) {
                mutableStateOf(TextFieldValue(settings?.settings?.tasksClearInvitesInterval ?: ""))
            }

            TextInput(
                value = clearInvitesInterval,
                onValueChange = { clearInvitesInterval = it },
                label = "Clear Invites Interval",
                description = "How often to check and clear expired/used invites.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var maxViewsInterval by remember(settings?.settings?.tasksMaxViewsInterval) {
                mutableStateOf(TextFieldValue(settings?.settings?.tasksMaxViewsInterval ?: ""))
            }

            TextInput(
                value = maxViewsInterval,
                onValueChange = { maxViewsInterval = it },
                label = "Max Views Interval",
                description = "How often to check and delete files that have reached max views.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var thumbnailsInterval by remember(settings?.settings?.tasksThumbnailsInterval) {
                mutableStateOf(TextFieldValue(settings?.settings?.tasksThumbnailsInterval ?: ""))
            }

            TextInput(
                value = thumbnailsInterval,
                onValueChange = { thumbnailsInterval = it },
                label = "Thumbnails Interval",
                description = "How often to check and generate thumbnails for video files.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var cleanThumbnailsInterval by remember(settings?.settings?.tasksCleanThumbnailsInterval) {
                mutableStateOf(TextFieldValue(settings?.settings?.tasksCleanThumbnailsInterval ?: ""))
            }

            TextInput(
                value = cleanThumbnailsInterval,
                onValueChange = { cleanThumbnailsInterval = it },
                label = "Clean Thumbnails Interval",
                description = "How often to check and delete orphaned thumbnails from the filesystem or database.",
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
                            tasksDeleteInterval = deleteFilesInterval.text,
                            tasksClearInvitesInterval = clearInvitesInterval.text,
                            tasksMaxViewsInterval = maxViewsInterval.text,
                            tasksThumbnailsInterval = thumbnailsInterval.text,
                            tasksCleanThumbnailsInterval = cleanThumbnailsInterval.text,
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