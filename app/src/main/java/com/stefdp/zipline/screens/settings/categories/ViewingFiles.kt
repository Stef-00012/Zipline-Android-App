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
import com.stefdp.zipline.utils.toHex
import com.stefdp.zipline.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
internal fun ViewingFilesCategory(
    context: Context,
    user: User?,
    updateUser: suspend (UpdateCurrentUserBody?) -> List<String>,
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

            var enableViewRoutes by remember(user?.view?.enabled, settingsUpdateTick) {
                mutableStateOf(user?.view?.enabled ?: false)
            }

            Switch(
                checked = enableViewRoutes,
                onCheckedChange = { enableViewRoutes = it },
                label = "Enable View Routes",
                description = "Enable viewing files through customizable view-routes."
            )

            var showMimetype by remember(user?.view?.showMimetype, settingsUpdateTick) {
                mutableStateOf(user?.view?.showMimetype ?: false)
            }

            Switch(
                checked = showMimetype,
                onCheckedChange = { showMimetype = it },
                label = "Show Mimetype",
                description = "Show the mimetype of the file in the view-route."
            )

            var showTags by remember(user?.view?.showTags, settingsUpdateTick) {
                mutableStateOf(user?.view?.showTags ?: false)
            }

            Switch(
                checked = showTags,
                onCheckedChange = { showTags = it },
                label = "Show Tags",
                description = "Show the file's tags in the view-route."
            )

            var showFolder by remember(user?.view?.showFolder, settingsUpdateTick) {
                mutableStateOf(user?.view?.showFolder ?: false)
            }

            Switch(
                checked = showFolder,
                onCheckedChange = { showFolder = it },
                label = "Show Folder",
                description = "Show the name/link of the folder if possible in the view-route."
            )

            var viewContent by remember(user?.view?.content, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(user?.view?.content ?: ""))
            }

            TextInput(
                value = viewContent,
                onValueChange = { viewContent = it },
                label = "View Content",
                description = "Change the content within view-routes. Most HTML is valid, while the use of JavaScript is unavailable.",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(
                        max = 200.dp
                    ),
                enabled = !isLoading,
                singleLine = false,
            )

            var selectedViewContentAlignment by remember(user?.view?.align, settingsUpdateTick) {
                mutableStateOf(setOf((user?.view?.align ?: UserViewSettingsAlign.LEFT).toString()))
            }

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
                selectedIds = selectedViewContentAlignment,
                onSelectionChange = { selectedViewContentAlignment = it },
                label = "View Content Alignment",
                description = "Change the alignment of the content within view-routes.",
            )

            var enableEmbed by remember(user?.view?.embed, settingsUpdateTick) {
                mutableStateOf(user?.view?.embed ?: false)
            }

            Switch(
                checked = enableEmbed,
                onCheckedChange = { enableEmbed = it },
                label = "Enable Embed",
                description = "Enable the following embed properties. These properties take advantage of OpenGraph tags. View routes will need to be enabled for this to work."
            )

            var embedTitle by remember(user?.view?.embedTitle, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(user?.view?.embedTitle ?: ""))
            }

            TextInput(
                value = embedTitle,
                onValueChange = { embedTitle = it },
                label = "Embed Title",
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && enableEmbed,
            )

            var embedDescription by remember(user?.view?.embedDescription, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(user?.view?.embedDescription ?: ""))
            }

            TextInput(
                value = embedDescription,
                onValueChange = { embedDescription = it },
                label = "Embed Description",
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && enableEmbed,
                singleLine = false,
            )

            var embedSiteName by remember(user?.view?.embedSiteName, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(user?.view?.embedSiteName ?: ""))
            }

            TextInput(
                value = embedSiteName,
                onValueChange = { embedSiteName = it },
                label = "Embed Site Name",
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && enableEmbed,
            )

            var embedColor by remember(user?.view?.embedColor, settingsUpdateTick) {
                val color = user?.view?.embedColor?.toColorInt()

                mutableStateOf(if (color != null) Color(color) else Color.Black)
            }

            ColorPicker(
                label = "Embed Color",
                color = embedColor,
                onColorChange = { embedColor = it },
                enabled = !isLoading && enableEmbed,
                modifier = Modifier.fillMaxWidth()
            )

            val coroutineScope = rememberCoroutineScope()

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    coroutineScope.launch {
                        setLoading(true)

                        val data = UpdateCurrentUserBody(
                            view = UserViewSettings(
                                enabled = enableViewRoutes,
                                showMimetype = showMimetype,
                                showTags = showTags,
                                showFolder = showFolder,
                                content = viewContent.text,
                                align = UserViewSettingsAlign.valueOf(selectedViewContentAlignment.first()),
                                embed = enableEmbed,
                                embedTitle = embedTitle.text,
                                embedDescription = embedDescription.text,
                                embedSiteName = embedSiteName.text,
                                embedColor = embedColor.toHex()
                            )
                        )

                        errors = updateUser(data)

                        setLoading(false)
                    }
                },
                enabled = !isLoading
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