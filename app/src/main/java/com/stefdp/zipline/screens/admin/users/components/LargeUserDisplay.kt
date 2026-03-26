package com.stefdp.zipline.screens.admin.users.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Avatar
import com.stefdp.zipline.components.LargeDisplayIconButton
import com.stefdp.zipline.components.MoreActionsButton
import com.stefdp.zipline.components.MoreActionsMenuItem
import com.stefdp.zipline.network.models.User
import com.stefdp.zipline.network.models.UserRole
import nl.jacobras.humanreadable.HumanReadable
import kotlin.time.Instant

@Composable
fun LargeUserDisplay(
    user: User,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onOpenFiles: () -> Unit,
    canInteractWithUser: Boolean,
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Avatar(
                    avatar = user.avatar,
                    size = 50.dp,
                    isAdmin = user.role.level <= UserRole.ADMIN.level
                )

                Text(
                    text = user.username,
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LargeDisplayIconButton(
                    icon = painterResource(R.drawable.folder_open),
                    iconContentDescription = "Open files",
                    onClick = onOpenFiles,
                    enabled = canInteractWithUser
                )

                val moreActionsButtonItems = listOf(
                    MoreActionsMenuItem(
                        label = "Edit",
                        icon = painterResource(R.drawable.edit),
                        iconDescription = "Edit",
                        onClick = onEdit,
                        enabled = canInteractWithUser
                    ),
                    MoreActionsMenuItem(
                        label = "Delete",
                        labelColor = MaterialTheme.colorScheme.error,
                        icon = painterResource(R.drawable.delete),
                        iconColor = MaterialTheme.colorScheme.error,
                        iconDescription = "Delete",
                        onClick = onDelete,
                        enabled = canInteractWithUser
                    )
                )

                MoreActionsButton(
                    items = moreActionsButtonItems,
                )
            }
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
            text = AnnotatedString.fromHtml("<b>ID:</b> ${user.id}"),
        )

        Text(
            text = AnnotatedString.fromHtml("<b>Role:</b> ${user.role.roleName}"),
        )

        Text(
            text = AnnotatedString.fromHtml("<b>Created:</b> ${HumanReadable.timeAgo(Instant.parse(user.createdAt))}"),
        )

        Text(
            text = AnnotatedString.fromHtml("<b>Updated:</b> ${HumanReadable.timeAgo(Instant.parse(user.updatedAt))}"),
        )
    }
}