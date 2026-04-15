package com.stefdp.zipline.screens.settings.categories.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.network.models.responses.GetServerVersionResponse
import com.stefdp.zipline.ui.theme.DarkGreen
import com.stefdp.zipline.utils.shimmerable

@Composable
fun VersionDisplay(
    modifier: Modifier = Modifier,
    version: GetServerVersionResponse?,
    context: Context
) {
    val versionData = version?.data

    var showVersionPopup by rememberSaveable { mutableStateOf(false) }

    Popup(
        showPopup = showVersionPopup,
        onDismissRequest = { showVersionPopup = false },
    ) {
        Text(
            text = "Zipline Version",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        if (versionData?.isLatest == true) {
            Text(
                text = "Running the latest version of Zipline."
            )
        }

        if (versionData?.isUpstream == true) {
            Text(
                text = AnnotatedString.fromHtml(
                    "You are running an <b>unstable</b> version of Zipline. Upstream versions are not fully tested and may contain bugs."
                )
            )
        }

        if (
            versionData?.isLatest != true &&
            versionData?.isUpstream != true &&
            versionData?.isRelease == true
        ) {
            Text(
                text = buildAnnotatedString {
                    val baseString = AnnotatedString.fromHtml(
                        "You are running an <b>outdated</b> version of Zipline. It is recommended to update to the <a href=\"placeholder\">latest version</a>."
                    )

                    append(baseString)

                    baseString.getLinkAnnotations(0, baseString.length).forEach { range ->
                        addLink(
                            url = LinkAnnotation.Url(
                                url = versionData.latest.url,
                                styles = TextLinkStyles(
                                    style = SpanStyle(
                                        color = MaterialTheme.colorScheme.tertiary,
                                        textDecoration = TextDecoration.Underline
                                    )
                                )
                            ),
                            start = range.start,
                            end = range.end
                        )
                    }
                }
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Current version",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(15.dp)
                    .background(
                        if (versionData?.isLatest == true)
                            DarkGreen
                        else
                            MaterialTheme.colorScheme.error
                    )
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Container(
            scrollable = false,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Version",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = versionData?.version?.tag ?: "v0.0.0",
                        color = MaterialTheme.colorScheme.tertiary,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable(
                            enabled = versionData != null,
                            onClick = {
                                val url = "https://github.com/diced/zipline/releases/${versionData?.version?.tag}"

                                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                context.startActivity(intent)
                            }
                        )
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Commit",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = versionData?.version?.sha?.take(7) ?: "0000000",
                        color = MaterialTheme.colorScheme.tertiary,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable(
                            enabled = versionData != null,
                            onClick = {
                                val url = "https://github.com/diced/zipline/commit/${versionData?.version?.sha}"

                                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                context.startActivity(intent)
                            }
                        )
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Upstream?",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = if (versionData?.isUpstream == true) "Yes" else "No",
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        if (
            versionData?.isLatest != true &&
            versionData?.isUpstream == true &&
            versionData.latest.commit != null
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Latest Commit Available",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = "This is only visible when running an upstream version.",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Container(
                scrollable = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Commit",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "test" + versionData.latest.commit.sha.take(7),
                            color = MaterialTheme.colorScheme.tertiary,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable(
                                onClick = {
                                    val url = "https://github.com/diced/zipline/commit/${versionData.latest.commit.sha}"

                                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                    context.startActivity(intent)
                                }
                            )
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Available to update",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = if (versionData.latest.commit.pull) "Yes" else "No",
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        if (
            versionData?.isLatest != true &&
            versionData?.isRelease == true
        ) {
            Text(
                text = "${versionData.latest.tag} is available",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val url = versionData.latest.url

                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    context.startActivity(intent)
                }
            ) {
                Text(
                    text = "Changelog for ${versionData.latest.tag}",
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val url = "https://zipline.diced.sh/docs/get-started/docker#updating"

                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    context.startActivity(intent)
                }
            ) {
                Text(
                    text = "Update to ${versionData.latest.tag}",
                )
            }
        }
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
            .background(MaterialTheme.colorScheme.surface)
            .shimmerable(
                enabled = version == null,
                shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
            )
            .clickable(
                onClick = { showVersionPopup = true },
                enabled = version != null
            )
            .padding(
                vertical = 2.dp,
                horizontal = 4.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(15.dp)
                .background(
                    if (versionData?.isLatest == true)
                        DarkGreen
                    else
                        MaterialTheme.colorScheme.error
                )
        )

        Text(
            text = version?.details?.version ?: "0.0.0"
        )
    }
}