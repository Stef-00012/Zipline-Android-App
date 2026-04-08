package com.stefdp.zipline.screens.settings.categories

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.components.Select
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.components.colorpicker.ColorPicker
import com.stefdp.zipline.network.models.User
import com.stefdp.zipline.network.models.UserViewSettings
import com.stefdp.zipline.network.models.UserViewSettingsAlign
import com.stefdp.zipline.network.models.requests.UpdateCurrentUserBody
import com.stefdp.zipline.screens.settings.SettingsUiState
import com.stefdp.zipline.screens.settings.SettingsViewModel
import com.stefdp.zipline.utils.toHex
import com.stefdp.zipline.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
internal fun ViewingFilesCategory(
    updateUser: (UpdateCurrentUserBody?) -> Unit,
    title: String,
    viewModel: SettingsViewModel,
    state: SettingsUiState
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
                    text = buildAnnotatedString {
                        val rawString = "All text fields support using [link]variables[/link]."
                        val linkTag = "[link]"
                        val linkEndTag = "[/link]"

                        val startIndex = rawString.indexOf(linkTag)
                        val endIndex = rawString.indexOf(linkEndTag)

                        if (startIndex != -1 && endIndex != -1) {
                            val cleanString = rawString.replace(linkTag, "").replace(linkEndTag, "")

                            append(cleanString)
                            addStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold
                                ),
                                start = 0,
                                end = startIndex - 1,
                            )

                            addLink(
                                url = LinkAnnotation.Url(
                                    url = "https://zipline.diced.sh/docs/guides/variables/",
                                    styles = TextLinkStyles(
                                        style = SpanStyle(
                                            color = MaterialTheme.colorScheme.tertiary,
                                            textDecoration = TextDecoration.Underline
                                        ),
                                    )
                                ),
                                start = startIndex,
                                end = endIndex - linkTag.length
                            )
                        } else {
                            append(rawString)
                        }
                    },
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                )
            }

            Switch(
                checked = state.enableViewRoutes,
                onCheckedChange = {
                    viewModel.setEnableViewRoutes(it)
                },
                enabled = !state.isLoading,
                label = "Enable View Routes",
                description = "Enable viewing files through customizable view-routes."
            )

            Switch(
                checked = state.showMimetype,
                onCheckedChange = {
                    viewModel.setShowMimetype(it)
                },
                enabled = !state.isLoading,
                label = "Show Mimetype",
                description = "Show the mimetype of the file in the view-route."
            )

            Switch(
                checked = state.showTags,
                onCheckedChange = {
                    viewModel.setShowTags(it)
                },
                enabled = !state.isLoading,
                label = "Show Tags",
                description = "Show the file's tags in the view-route."
            )

            Switch(
                checked = state.showFolder,
                onCheckedChange = {
                    viewModel.setShowFolder(it)
                },
                enabled = !state.isLoading,
                label = "Show Folder",
                description = "Show the name/link of the folder if possible in the view-route."
            )

            TextInput(
                value = state.viewContent,
                onValueChange = {
                    viewModel.setViewContent(it)
                },
                label = "View Content",
                description = "Change the content within view-routes. Most HTML is valid, while the use of JavaScript is unavailable.",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(
                        max = 200.dp
                    ),
                enabled = !state.isLoading,
                singleLine = false,
            )

            Select(
                options = viewContentAlignmentOptions.map { option ->
                    SelectOption(
                        id = option.toString(),
                        label = { enabled ->
                            Text(
                                text = option.displayName,
                                color = if (enabled)
                                    MaterialTheme.colorScheme.onBackground
                                else
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        },
                    )
                },
                selectedIds = state.selectedViewContentAlignment,
                onSelectionChange = {
                    viewModel.setSelectedViewContentAlignment(it)
                },
                enabled = !state.isLoading,
                label = "View Content Alignment",
                description = "Change the alignment of the content within view-routes.",
            )

            Switch(
                checked = state.enableEmbed,
                onCheckedChange = {
                    viewModel.setEnableEmbed(it)
                },
                enabled = !state.isLoading,
                label = "Enable Embed",
                description = "Enable the following embed properties. These properties take advantage of OpenGraph tags. View routes will need to be enabled for this to work."
            )

            TextInput(
                value = state.embedTitle,
                onValueChange = {
                    viewModel.setEmbedTitle(it)
                },
                label = "Embed Title",
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading && state.enableEmbed,
            )

            TextInput(
                value = state.embedDescription,
                onValueChange = {
                    viewModel.setEmbedDescription(it)
                },
                label = "Embed Description",
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading && state.enableEmbed,
                singleLine = false,
            )

            TextInput(
                value = state.embedSiteName,
                onValueChange = {
                    viewModel.setEmbedSiteName(it)
                },
                label = "Embed Site Name",
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading && state.enableEmbed,
            )

            ColorPicker(
                label = "Embed Color",
                color = state.embedColor,
                onColorChange = {
                    viewModel.setEmbedColor(it)
                },
                enabled = !state.isLoading && state.enableEmbed,
                modifier = Modifier.fillMaxWidth()
            )

            val coroutineScope = rememberCoroutineScope()

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    coroutineScope.launch {
                        val data = UpdateCurrentUserBody(
                            view = UserViewSettings(
                                enabled = state.enableViewRoutes,
                                showMimetype = state.showMimetype,
                                showTags = state.showTags,
                                showFolder = state.showFolder,
                                content = state.viewContent.text,
                                align = UserViewSettingsAlign.valueOf(state.selectedViewContentAlignment.first()),
                                embed = state.enableEmbed,
                                embedTitle = state.embedTitle.text,
                                embedDescription = state.embedDescription.text,
                                embedSiteName = state.embedSiteName.text,
                                embedColor = state.embedColor.toHex()
                            )
                        )

                        updateUser(data)
                    }
                },
                enabled = !state.isLoading
            ) {
                Icon(
                    painter = painterResource(R.drawable.save),
                    contentDescription = "Save"
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Save"
                )
            }
        }
    }
}

private val viewContentAlignmentOptions = listOf(
    UserViewSettingsAlign.LEFT,
    UserViewSettingsAlign.CENTER,
    UserViewSettingsAlign.RIGHT
)