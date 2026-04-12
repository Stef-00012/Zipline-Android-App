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
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.TasksSettings
import com.stefdp.zipline.screens.admin.settings.AdminSettingsUiState
import com.stefdp.zipline.screens.admin.settings.AdminSettingsViewModel
import com.stefdp.zipline.utils.verticalScrollWithScrollbar

@Composable
internal fun TasksCategory(
    updateSettings: (TasksSettings) -> Unit,
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
                    text = "All options require a restart to take effect.",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            TextInput(
                value = state.tasksDeleteInterval,
                onValueChange = {
                    viewModel.setTasksDeleteInterval(it)
                },
                label = "Delete Files Interval",
                description = "How often to check and delete expired files.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.tasksClearInvitesInterval,
                onValueChange = {
                    viewModel.setTasksClearInvitesInterval(it)
                },
                label = "Clear Invites Interval",
                description = "How often to check and clear expired/used invites.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.tasksMaxViewsInterval,
                onValueChange = {
                    viewModel.setTasksMaxViewsInterval(it)
                },
                label = "Max Views Interval",
                description = "How often to check and delete files that have reached max views.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.tasksThumbnailsInterval,
                onValueChange = {
                    viewModel.setTasksThumbnailsInterval(it)
                },
                label = "Thumbnails Interval",
                description = "How often to check and generate thumbnails for video files.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.tasksCleanThumbnailsInterval,
                onValueChange = {
                    viewModel.setTasksCleanThumbnailsInterval(it)
                },
                label = "Clean Thumbnails Interval",
                description = "How often to check and delete orphaned thumbnails from the filesystem or database.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val data = TasksSettings(
                        tasksDeleteInterval = state.tasksDeleteInterval.text,
                        tasksClearInvitesInterval = state.tasksClearInvitesInterval.text,
                        tasksMaxViewsInterval = state.tasksMaxViewsInterval.text,
                        tasksThumbnailsInterval = state.tasksThumbnailsInterval.text,
                        tasksCleanThumbnailsInterval = state.tasksCleanThumbnailsInterval.text,
                        tasksMetricsInterval = "30m" // TODO: not sure what to do with cuz API errors without it but the web ui doesn't have that
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