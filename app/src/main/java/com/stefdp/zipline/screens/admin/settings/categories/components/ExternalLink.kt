package com.stefdp.zipline.screens.admin.settings.categories.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.network.models.WebsiteExternalLink
import com.stefdp.zipline.screens.files.components.IconButton
import com.stefdp.zipline.ui.theme.getButtonColors

@Composable
fun ExternalLink(
    url: WebsiteExternalLink,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    isLoading: Boolean,
    isFirst: Boolean,
    isLast: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = AnnotatedString.fromHtml("<b>Name:</b> ${url.name}"),
            )

            Text(
                text = buildAnnotatedString {
                    val rawString = "URL: [link]${url.url}[/link]"

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
                            end = startIndex - 1
                        )

                        addLink(
                            url = LinkAnnotation.Url(
                                url = url.url,
                                styles = TextLinkStyles(
                                    style = SpanStyle(
                                        color = MaterialTheme.colorScheme.primary,
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
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onEdit,
                    enabled = !isLoading
                ) {
                    Icon(
                        painter = painterResource(R.drawable.edit),
                        contentDescription = "Edit"
                    )

                    Spacer(
                        modifier = Modifier.width(4.dp)
                    )

                    Text(
                        text = "Edit"
                    )
                }

                Button(
                    modifier = Modifier.weight(1f),
                    colors = getButtonColors().copy(
                        containerColor = MaterialTheme.colorScheme.error,
                        disabledContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                    ),
                    onClick = onDelete,
                    enabled = !isLoading
                ) {
                    Icon(
                        painter = painterResource(R.drawable.delete),
                        contentDescription = "Delete"
                    )

                    Spacer(
                        modifier = Modifier.width(4.dp)
                    )

                    Text(
                        text = "Delete"
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(0.1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            )
        ) {
            IconButton(
                icon = painterResource(R.drawable.north),
                iconContentDescription = "Move Up",
                color = MaterialTheme.colorScheme.primary,
                iconColor = MaterialTheme.colorScheme.onPrimary,
                onClick = onMoveUp,
                enabled = !isLoading || isFirst
            )

            IconButton(
                icon = painterResource(R.drawable.south),
                iconContentDescription = "Move Down",
                color = MaterialTheme.colorScheme.primary,
                iconColor = MaterialTheme.colorScheme.onPrimary,
                onClick = onMoveDown,
                enabled = !isLoading || isLast
            )
        }
    }
}