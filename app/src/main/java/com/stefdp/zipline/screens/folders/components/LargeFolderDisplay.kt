package com.stefdp.zipline.screens.folders.components

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
import com.stefdp.zipline.network.models.BaseFolder
import kotlinx.coroutines.launch
import nl.jacobras.humanreadable.HumanReadable
import kotlin.time.Instant

@Composable
fun LargeFolderDisplay(
    context: Context,
    activity: FragmentActivity,
    folder: BaseFolder,
    serverUrl: String?,
    onOpen: () -> Unit,
    onMove: () -> Unit,
    onExportZip: () -> Unit,
    onTogglePublic: () -> Unit,
    onToggleAnonymousUploads: () -> Unit,
    onEditName: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                enabled = true,
                onClick = onOpen
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = folder.name,
                color = if (folder.public)
                    MaterialTheme.colorScheme.tertiary
                else MaterialTheme.colorScheme.onSurface,
                textDecoration = if (folder.public)
                    TextDecoration.Underline
                else null,
                modifier = Modifier.clickable(
                    enabled = serverUrl != null && folder.public,
                    onClick = {
                        val urlUrl = "${serverUrl}/folder/${folder.id}"

                        val intent = Intent(Intent.ACTION_VIEW, urlUrl.toUri())
                        context.startActivity(intent)
                    }
                )
            )

            val clipboardManager = LocalClipboard.current
            val coroutineScope = rememberCoroutineScope()

            val moreActionsButtonItems = listOf(
                MoreActionsMenuItem(
                    label = "Open Folder",
                    icon = painterResource(R.drawable.folder_open),
                    iconDescription = "Open folder",
                    onClick = onOpen,
                ),
                MoreActionsMenuItem(
                    label = "Move Folder",
                    icon = painterResource(R.drawable.folder_copy),
                    iconDescription = "Move folder",
                    onClick = onMove,
                ),
                MoreActionsMenuItem(
                    label = "Export as ZIP",
                    icon = painterResource(R.drawable.folder_zip),
                    iconDescription = "Export as ZIP",
                    onClick = onExportZip,
                ),
                MoreActionsMenuItem(
                    label = if (folder.public) "Make Private" else "Make Public",
                    icon = painterResource(
                        if (folder.public) R.drawable.lock
                        else R.drawable.lock_open
                    ),
                    iconDescription = if (folder.public) "Make private" else "Make public",
                    onClick = onTogglePublic,
                ),
                MoreActionsMenuItem(
                    label = if (folder.allowUploads) "Disallow Anonymous Uploads" else "Allow Anonymous Uploads",
                    icon = painterResource(
                        if (folder.allowUploads) R.drawable.share_off
                        else R.drawable.share
                    ),
                    iconDescription = if (folder.allowUploads) "Disallow anonymous uploads" else "Allow anonymous uploads",
                    onClick = onToggleAnonymousUploads,
                ),
                MoreActionsMenuItem(
                    label = "Edit Name",
                    icon = painterResource(R.drawable.edit),
                    iconDescription = "Edit name",
                    onClick = onEditName,
                ),
                MoreActionsMenuItem(
                    label = "Copy URL",
                    icon = painterResource(R.drawable.content_copy),
                    iconDescription = "Copy URL",
                    enabled = folder.public && serverUrl != null,
                    onClick = {
                        coroutineScope.launch {
                            val urlUrl = "${serverUrl}/folder/${folder.id}"

                            val clipData = ClipData.newPlainText("Folder URL", urlUrl).toClipEntry()

                            clipboardManager.setClipEntry(clipData)

                            Notification.show(
                                context = context,
                                activity = activity,
                            ) {
                                Text(
                                    text = "Folder URL link copied to clipboard"
                                )
                            }
                        }
                    },
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
            text = AnnotatedString.fromHtml("<b>Created:</b> ${HumanReadable.timeAgo(Instant.parse(folder.createdAt))}"),
        )

        Text(
            text = AnnotatedString.fromHtml("<b>Updated:</b> ${HumanReadable.timeAgo(Instant.parse(folder.updatedAt))}"),
        )

        Text(
            text = AnnotatedString.fromHtml("<b>Public:</b> ${if (folder.public) "Yes" else "No"}"),
        )

        Text(
            text = AnnotatedString.fromHtml("<b>Files:</b> ${folder.count?.files ?: 0}"),
        )

        if (folder.count?.children != null && folder.count.children > 0) {
            Text(
                text = AnnotatedString.fromHtml("<b>Subfolders:</b> ${folder.count.children}"),
            )
        }
    }
}