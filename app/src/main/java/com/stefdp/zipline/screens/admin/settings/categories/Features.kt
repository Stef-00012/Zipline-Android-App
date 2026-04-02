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
import com.stefdp.zipline.utils.NumberRegex
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.thumbnailFormats
import com.stefdp.zipline.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
internal fun FeaturesCategory(
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

            var imageCompression by remember(settings?.settings?.featuresImageCompression, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.featuresImageCompression ?: false)
            }

            Switch(
                checked = imageCompression,
                onCheckedChange = { imageCompression = it },
                label = "Image Compression",
                description = "Allows the ability for users to compress images.",
                enabled = !isLoading
            )

            var robotsTxt by remember(settings?.settings?.featuresRobotsTxt, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.featuresRobotsTxt ?: false)
            }

            Switch(
                checked = robotsTxt,
                onCheckedChange = { robotsTxt = it },
                label = "/robots.txt",
                description = "Enables a /robots.txt to stop search crawlers. Requires a server restart.",
                enabled = !isLoading
            )

            var healthcheck by remember(settings?.settings?.featuresHealthcheck, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.featuresHealthcheck ?: false)
            }

            Switch(
                checked = healthcheck,
                onCheckedChange = { healthcheck = it },
                label = "Healthcheck",
                description = "Enables a healthcheck route for uptime monitoring. Requires a server restart.",
                enabled = !isLoading
            )

            var userRegistration by remember(settings?.settings?.featuresUserRegistration, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.featuresUserRegistration ?: false)
            }

            Switch(
                checked = userRegistration,
                onCheckedChange = { userRegistration = it },
                label = "User Registration",
                description = "Allows users to register an account on the server.",
                enabled = !isLoading
            )

            var oauthRegistration by remember(settings?.settings?.featuresOauthRegistration, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.featuresOauthRegistration ?: false)
            }

            Switch(
                checked = oauthRegistration,
                onCheckedChange = { oauthRegistration = it },
                label = "OAuth Registration",
                description = "Allows users to register an account using OAuth providers.",
                enabled = !isLoading
            )

            var deleteOnMaxViews by remember(settings?.settings?.featuresDeleteOnMaxViews, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.featuresDeleteOnMaxViews ?: false)
            }

            Switch(
                checked = deleteOnMaxViews,
                onCheckedChange = { deleteOnMaxViews = it },
                label = "Delete on Max Views",
                description = "Automatically deletes files/urls after they reach the maximum view count. Requires a server restart.",
                enabled = !isLoading
            )

            var enableMetrics by remember(settings?.settings?.featuresMetricsEnabled, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.featuresMetricsEnabled ?: false)
            }

            Switch(
                checked = enableMetrics,
                onCheckedChange = { enableMetrics = it },
                label = "Enable Metrics",
                description = "Enables metrics for the server. Requires a server restart.",
                enabled = !isLoading
            )

            var adminOnlyMetrics by remember(settings?.settings?.featuresMetricsAdminOnly, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.featuresMetricsAdminOnly ?: false)
            }

            Switch(
                checked = adminOnlyMetrics,
                onCheckedChange = { adminOnlyMetrics = it },
                label = "Admin Only Metrics",
                description = "Requires an administrator to view metrics.",
                enabled = !isLoading
            )

            var showUserSpecificMetrics by remember(settings?.settings?.featuresMetricsShowUserSpecific, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.featuresMetricsShowUserSpecific ?: false)
            }

            Switch(
                checked = showUserSpecificMetrics,
                onCheckedChange = { showUserSpecificMetrics = it },
                label = "Show User Specific Metrics",
                description = "Shows metrics specific to each user, for all users.",
                enabled = !isLoading
            )

            var enableThumbnails by remember(settings?.settings?.featuresThumbnailsEnabled, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.featuresThumbnailsEnabled ?: false)
            }

            Switch(
                checked = enableThumbnails,
                onCheckedChange = { enableThumbnails = it },
                label = "Enable Thumbnails",
                description = "Enables thumbnail generation for images. Requires a server restart.",
                enabled = !isLoading
            )

            var thumbnailsNumberThreads by remember(settings?.settings?.featuresThumbnailsNumberThreads, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.featuresThumbnailsNumberThreads?.toString() ?: ""))
            }

            TextInput(
                value = thumbnailsNumberThreads,
                onValueChange = {
                    if (NumberRegex.matches(it.text)) {
                        thumbnailsNumberThreads = it
                    }
                },
                label = "Thumbnails Number of Threads",
                description = "The number of threads to use for thumbnail generation.",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var selectedThumbnailsFormat by remember(settings?.settings?.featuresThumbnailsFormat, settingsUpdateTick) {
                mutableStateOf(setOf((settings?.settings?.featuresThumbnailsFormat ?: ThumbnailFormat.PNG).toString()))
            }

            Select(
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
                selectedIds = selectedThumbnailsFormat,
                onSelectionChange = { selectedThumbnailsFormat = it },
            )

            var versionChecking by remember(settings?.settings?.featuresVersionChecking, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.featuresVersionChecking ?: false)
            }

            Switch(
                checked = versionChecking,
                onCheckedChange = { versionChecking = it },
                label = "Version Checking",
                description = "Enable version checking for the server. This will check for updates and display the status on the sidebar to all users.",
                enabled = !isLoading
            )

            var versionAPIUrl by remember(settings?.settings?.featuresVersionAPI, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.featuresVersionAPI ?: ""))
            }

            TextInput(
                value = versionAPIUrl,
                onValueChange = {
                    versionAPIUrl = it
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
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            val coroutineScope = rememberCoroutineScope()

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    coroutineScope.launch {
                        setLoading(true)

                        val thumbnailsFormat = thumbnailFormats.firstOrNull {
                            it.first.toString() == selectedThumbnailsFormat.firstOrNull()
                        }?.first

                        val data = PartialServerSettingsSettings(
                            featuresImageCompression = imageCompression,
                            featuresRobotsTxt = robotsTxt,
                            featuresHealthcheck = healthcheck,
                            featuresUserRegistration = userRegistration,
                            featuresOauthRegistration = oauthRegistration,
                            featuresDeleteOnMaxViews = deleteOnMaxViews,
                            featuresMetricsEnabled = enableMetrics,
                            featuresMetricsAdminOnly = adminOnlyMetrics,
                            featuresMetricsShowUserSpecific = showUserSpecificMetrics,
                            featuresThumbnailsEnabled = enableThumbnails,
                            featuresThumbnailsNumberThreads = thumbnailsNumberThreads.text.toLongOrNull(),
                            featuresThumbnailsFormat = thumbnailsFormat,
                            featuresVersionChecking = versionChecking,
                            featuresVersionAPI = versionAPIUrl.text,
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