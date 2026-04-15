package com.stefdp.zipline.screens.urls.components

import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.painterResource
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
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.R
import com.stefdp.zipline.components.MoreActionsButton
import com.stefdp.zipline.components.MoreActionsMenuItem
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.network.models.Url
import kotlinx.coroutines.launch
import nl.jacobras.humanreadable.HumanReadable
import kotlin.time.Instant

@Composable
fun LargeUrlDisplay(
    context: Context,
    activity: FragmentActivity,
    url: Url,
    serverUrl: String?,
    urlsRoute: String,
    onShowQRCode: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (url.vanity.isNullOrBlank())
                    url.code
                else url.vanity,
                color = if (url.enabled)
                    MaterialTheme.colorScheme.tertiary
                else MaterialTheme.colorScheme.onSurface,
                textDecoration = if (url.enabled)
                    TextDecoration.Underline
                else null,
                modifier = Modifier.clickable(
                    enabled = serverUrl != null && url.enabled,
                    onClick = {
                        val urlUrl = "${serverUrl}${urlsRoute}/${if (url.vanity.isNullOrBlank()) url.code else url.vanity}"

                        val intent = Intent(Intent.ACTION_VIEW, urlUrl.toUri())
                        context.startActivity(intent)
                    }
                )
            )

            val clipboardManager = LocalClipboard.current
            val coroutineScope = rememberCoroutineScope()

            val moreActionsButtonItems = listOf(
                MoreActionsMenuItem(
                    label = "Copy Short Link",
                    icon = painterResource(R.drawable.content_copy),
                    iconDescription = "Copy short link",
                    onClick = {
                        coroutineScope.launch {
                            val urlUrl = "${serverUrl}${urlsRoute}/${if (url.vanity.isNullOrBlank()) url.code else url.vanity}"

                            val clipData = ClipData.newRawUri("URL", urlUrl.toUri()).toClipEntry()

                            clipboardManager.setClipEntry(clipData)

                            Notification.show(
                                context = context,
                                activity = activity,
                            ) {
                                Text(
                                    text = "URL link copied to clipboard"
                                )
                            }
                        }
                    },
                ),
                MoreActionsMenuItem(
                    label = "Copy Destination",
                    icon = painterResource(R.drawable.content_copy),
                    iconDescription = "Copy destination",
                    onClick = {
                        coroutineScope.launch {
                            val clipData = ClipData.newRawUri("URL", url.destination.toUri()).toClipEntry()

                            clipboardManager.setClipEntry(clipData)

                            Notification.show(
                                context = context,
                                activity = activity,
                            ) {
                                Text(
                                    text = "Destination copied to clipboard"
                                )
                            }
                        }
                    },
                ),
                MoreActionsMenuItem(
                    label = "Show QR Code",
                    icon = painterResource(R.drawable.qr_code),
                    iconDescription = "Show QR code",
                    onClick = onShowQRCode,
                ),
                MoreActionsMenuItem(
                    label = "Edit",
                    icon = painterResource(R.drawable.edit),
                    iconDescription = "Edit",
                    onClick = onEdit,
                ),
                MoreActionsMenuItem(
                    label = "Delete",
                    labelColor = MaterialTheme.colorScheme.error,
                    icon = painterResource(R.drawable.delete),
                    iconColor = MaterialTheme.colorScheme.error,
                    iconDescription = "Delete",
                    onClick = onDelete,
                )
            )

            MoreActionsButton(
                items = moreActionsButtonItems,
            )
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            modifier = Modifier.padding(
                top = 4.dp,
                bottom  = 8.dp
            ),
            thickness = 2.dp
        )

        Text(
            text = AnnotatedString.fromHtml("<b>Views:</b> ${url.views}${if (url.maxViews != null && url.maxViews > 0) " / ${url.maxViews}" else ""}"),
        )

        Text(
            text = AnnotatedString.fromHtml("<b>Enabled:</b> ${if (url.enabled) "Yes" else "No"}"),
        )

        Text(
            text = AnnotatedString.fromHtml("<b>Created:</b> ${HumanReadable.timeAgo(Instant.parse(url.createdAt))}"),
        )

        Text(
            text = AnnotatedString.fromHtml("<b>Updated:</b> ${HumanReadable.timeAgo(Instant.parse(url.updatedAt))}"),
        )

        Text(
            text = buildAnnotatedString {
                val rawString = "Destination: [link]${url.destination}[/link]"
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
                            url = url.destination,
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
            }
        )

        if (url.enabled) {
            Text(
                text = buildAnnotatedString {
                    val rawString = "Code: [link]${url.code}[/link]"
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

                        val urlUrl = "${serverUrl}${urlsRoute}/${url.code}"

                        addLink(
                            url = LinkAnnotation.Url(
                                url = urlUrl,
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
                }
            )
        }
    }
}