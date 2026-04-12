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
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.components.Select
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.PartialServerSettingsSettings
import com.stefdp.zipline.network.models.ServerSettings
import com.stefdp.zipline.network.models.ThumbnailFormat
import com.stefdp.zipline.screens.admin.settings.AdminSettingsUiState
import com.stefdp.zipline.screens.admin.settings.AdminSettingsViewModel
import com.stefdp.zipline.utils.NumberRegex
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.thumbnailFormats
import com.stefdp.zipline.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
internal fun FeaturesCategory(
    updateSettings: (PartialServerSettingsSettings) -> Unit,
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
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
            )

            Switch(
                checked = state.featuresImageCompression,
                onCheckedChange = {
                    viewModel.setFeaturesImageCompression(it)
                },
                label = "Image Compression",
                description = "Allows the ability for users to compress images.",
                enabled = !state.isLoading
            )

            Switch(
                checked = state.featuresRobotsTxt,
                onCheckedChange = {
                    viewModel.setFeaturesRobotsTxt(it)
                },
                label = "/robots.txt",
                description = "Enables a /robots.txt to stop search crawlers. Requires a server restart.",
                enabled = !state.isLoading
            )

            Switch(
                checked = state.featuresHealthcheck,
                onCheckedChange = {
                    viewModel.setFeaturesHealthcheck(it)
                },
                label = "Healthcheck",
                description = "Enables a healthcheck route for uptime monitoring. Requires a server restart.",
                enabled = !state.isLoading
            )

            Switch(
                checked = state.featuresUserRegistration,
                onCheckedChange = {
                    viewModel.setFeaturesUserRegistration(it)
                },
                label = "User Registration",
                description = "Allows users to register an account on the server.",
                enabled = !state.isLoading
            )

            Switch(
                checked = state.featuresOauthRegistration,
                onCheckedChange = {
                    viewModel.setFeaturesOauthRegistration(it)
                },
                label = "OAuth Registration",
                description = "Allows users to register an account using OAuth providers.",
                enabled = !state.isLoading
            )

            Switch(
                checked = state.featuresDeleteOnMaxViews,
                onCheckedChange = {
                    viewModel.setFeaturesDeleteOnMaxViews(it)
                },
                label = "Delete on Max Views",
                description = "Automatically deletes files/urls after they reach the maximum view count. Requires a server restart.",
                enabled = !state.isLoading
            )

            Switch(
                checked = state.featuresMetricsEnabled,
                onCheckedChange = {
                    viewModel.setFeaturesMetricsEnabled(it)
                },
                label = "Enable Metrics",
                description = "Enables metrics for the server. Requires a server restart.",
                enabled = !state.isLoading
            )

            Switch(
                checked = state.featuresMetricsAdminOnly,
                onCheckedChange = {
                    viewModel.setFeaturesMetricsAdminOnly(it)
                },
                label = "Admin Only Metrics",
                description = "Requires an administrator to view metrics.",
                enabled = !state.isLoading
            )

            Switch(
                checked = state.featuresMetricsShowUserSpecific,
                onCheckedChange = {
                    viewModel.setFeaturesMetricsShowUserSpecific(it)
                },
                label = "Show User Specific Metrics",
                description = "Shows metrics specific to each user, for all users.",
                enabled = !state.isLoading
            )

            Switch(
                checked = state.featuresThumbnailsEnabled,
                onCheckedChange = {
                    viewModel.setFeaturesThumbnailsEnabled(it)
                },
                label = "Enable Thumbnails",
                description = "Enables thumbnail generation for images. Requires a server restart.",
                enabled = !state.isLoading
            )

            Switch(
                checked = state.featuresThumbnailsInstantaneous,
                onCheckedChange = {
                    viewModel.setFeaturesThumbnailsInstantaneous(it)
                },
                label = "Instantaneous Thumbnails",
                description = "Generates thumbnails immediately after a file is uploaded, instead of waiting for the task to run.",
                enabled = !state.isLoading
            )

            TextInput(
                value = state.featuresThumbnailsNumberThreads,
                onValueChange = {
                    if (NumberRegex.matches(it.text)) {
                        viewModel.setFeaturesThumbnailsNumberThreads(it)
                    }
                },
                label = "Thumbnails Number of Threads",
                description = "The number of threads to use for thumbnail generation.",
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Select(
                label = "Thumbnails Format",
                description = "The output format for thumbnails. Requires a server restart.",
                options = thumbnailFormats.map { (id, label) ->
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
                selectedIds = state.featuresSelectedThumbnailsFormat,
                onSelectionChange = {
                    viewModel.setFeaturesSelectedThumbnailsFormat(it)
                },
                enabled = !state.isLoading,
            )

            Switch(
                checked = state.featuresVersionChecking,
                onCheckedChange = {
                    viewModel.setFeaturesVersionChecking(it)
                },
                label = "Version Checking",
                description = "Enable version checking for the server. This will check for updates and display the status on the sidebar to all users.",
                enabled = !state.isLoading
            )

            TextInput(
                value = state.featuresVersionAPI,
                onValueChange = {
                    viewModel.setFeaturesVersionAPI(it)
                },
                label = "Version API URL",
                description = buildAnnotatedString {
                    val rawString = "The URL of the version checking server. The default is [link1]https://zipline-version.diced.sh[/link1]. Visit the [link2]GitHub[/link2] to host your own version checking server."

                    val linkUrls = mapOf(
                        "link1" to "https://zipline-version.diced.sh",
                        "link2" to "https://github.com/diced/zipline-version-worker"
                    )

                    val regex = Regex("""\[(link\d+)](.*?)\[/\1]""")

                    var currentIndex = 0
                    val matches = regex.findAll(rawString)

                    for (match in matches) {
                        append(rawString.substring(currentIndex, match.range.first))

                        val tag = match.groupValues[1]
                        val visibleText = match.groupValues[2]
                        val url = linkUrls[tag]

                        val startIndex = this.length

                        append(visibleText)

                        if (url != null) {
                            addLink(
                                url = LinkAnnotation.Url(
                                    url = url,
                                    styles = TextLinkStyles(
                                        style = SpanStyle(
                                            color = MaterialTheme.colorScheme.tertiary,
                                            textDecoration = TextDecoration.Underline
                                        )
                                    )
                                ),
                                start = startIndex,
                                end = this.length
                            )
                        }

                        currentIndex = match.range.last + 1
                    }

                    if (currentIndex < rawString.length) {
                        append(rawString.substring(currentIndex))
                    }
                },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val thumbnailsFormat = thumbnailFormats.firstOrNull {
                        it.first.toString() == state.featuresSelectedThumbnailsFormat.firstOrNull()
                    }?.first

                    val data = PartialServerSettingsSettings(
                        featuresImageCompression = state.featuresImageCompression,
                        featuresRobotsTxt = state.featuresRobotsTxt,
                        featuresHealthcheck = state.featuresHealthcheck,
                        featuresUserRegistration = state.featuresUserRegistration,
                        featuresOauthRegistration = state.featuresOauthRegistration,
                        featuresDeleteOnMaxViews = state.featuresDeleteOnMaxViews,
                        featuresMetricsEnabled = state.featuresMetricsEnabled,
                        featuresMetricsAdminOnly = state.featuresMetricsAdminOnly,
                        featuresMetricsShowUserSpecific = state.featuresMetricsShowUserSpecific,
                        featuresThumbnailsEnabled = state.featuresThumbnailsEnabled,
                        featuresThumbnailsNumberThreads = state.featuresThumbnailsNumberThreads.text.toLongOrNull(),
                        featuresThumbnailsFormat = thumbnailsFormat,
                        featuresVersionChecking = state.featuresVersionChecking,
                        featuresVersionAPI = state.featuresVersionAPI.text,
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