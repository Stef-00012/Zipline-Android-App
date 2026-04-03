package com.stefdp.zipline.screens.admin.invites.components

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import com.stefdp.zipline.network.models.Invite
import com.stefdp.zipline.network.models.Url
import kotlinx.coroutines.launch
import nl.jacobras.humanreadable.HumanReadable
import kotlin.time.Instant

@Composable
fun LargeInviteDisplay(
    context: Context,
    activity: FragmentActivity,
    invite: Invite,
    serverUrl: String?,
    onShowQRCode: () -> Unit,
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
                text = invite.code,
                color = MaterialTheme.colorScheme.tertiary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(
                    enabled = serverUrl != null,
                    onClick = {
                        val inviteUrl = "${serverUrl}/invite/${invite.code}"

                        val intent = Intent(Intent.ACTION_VIEW, inviteUrl.toUri())
                        context.startActivity(intent)
                    }
                )
            )

            val clipboardManager = LocalClipboard.current
            val coroutineScope = rememberCoroutineScope()

            val moreActionsButtonItems = listOf(
                MoreActionsMenuItem(
                    label = "Copy URL",
                    icon = painterResource(R.drawable.content_copy),
                    iconDescription = "Copy invite URL",
                    onClick = {
                        coroutineScope.launch {
                            val inviteUrl = "${serverUrl}/invite/${invite.code}"

                            val clipData = ClipData.newRawUri("Invite URL", inviteUrl.toUri()).toClipEntry()

                            clipboardManager.setClipEntry(clipData)

                            Notification.show(
                                context = context,
                                activity = activity,
                                content = {
                                    Text(
                                        text = "Invite URL copied to clipboard"
                                    )
                                }
                            )
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
            text = AnnotatedString.fromHtml("<b>Created by:</b> ${invite.inviter.username}"),
        )

        Text(
            text = AnnotatedString.fromHtml("<b>Created:</b> ${HumanReadable.timeAgo(Instant.parse(invite.createdAt))}"),
        )

        Text(
            text = AnnotatedString.fromHtml("<b>Uses:</b> ${invite.uses}${if (invite.maxUses != null) " / ${invite.maxUses}" else ""}"),
        )
    }
}