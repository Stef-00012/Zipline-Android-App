package com.stefdp.zipline.screens.files.components.tags

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.R
import com.stefdp.zipline.components.HeaderButton
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.components.IconButton
import com.stefdp.zipline.screens.files.FilesUiState
import com.stefdp.zipline.screens.files.FilesViewModel
import com.stefdp.zipline.components.Tag as TagComponent

@Composable
fun TagsPopup(
    context: Context,
    activity: FragmentActivity,
    showPopup: Boolean,
    onDismissRequest: () -> Unit,
    viewModel: FilesViewModel,
    state: FilesUiState
) {
    val showMainPopup = showPopup && !state.showCreateTagPopup && state.tagToEdit == null

    CreateTagPopup(
        context = context,
        activity = activity,
        showPopup = state.showCreateTagPopup,
        onDismissRequest = {
            viewModel.closeCreateTagPopup()
        },
        viewModel = viewModel,
        state = state
    )

    EditTagPopup(
        context = context,
        activity = activity,
        showPopup = state.tagToEdit != null,
        onDismissRequest = {
            viewModel.setTagToEdit(null)
        },
        viewModel = viewModel,
        state = state
    )

    Popup(
        showPopup = showMainPopup,
        onDismissRequest = onDismissRequest,
        scrollable = false
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row {
                Text(
                    text = "Tags",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                HeaderButton(
                    onClick = {
                        viewModel.openCreateTagPopup()
                    },
                    icon = painterResource(id = R.drawable.add),
                    contentDescription = "Create tag",
                    enabled = !state.tagsLoading
                )
            }

            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(
                        onClick = {
                            onDismissRequest()
                        }
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.close),
                    contentDescription = "Close tags menu",
                )
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        if (!state.tags.isNullOrEmpty()) {
            LazyColumn {
                items(state.tags.size) {
                    val tag = state.tags[it]

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 4.dp
                            )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .widthIn(
                                        max = 200.dp
                                    )
                            ) {
                                TagComponent(
                                    tag = tag,
                                )
                            }

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Text(
                                text = "${tag.files?.size ?: 0} file${if (tag.files?.size != 1) "s" else ""}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            )
                        }

                        Row {
                            IconButton(
                                icon = painterResource(R.drawable.edit),
                                iconContentDescription = "Edit tag",
                                onClick = {
                                    viewModel.setTagToEdit(tag)
                                },
                                color = MaterialTheme.colorScheme.primary,
                                iconColor = MaterialTheme.colorScheme.onPrimary,
                                enabled = !state.tagsLoading
                            )

                            Spacer(
                                modifier = Modifier.width(4.dp)
                            )

                            IconButton(
                                icon = painterResource(R.drawable.delete),
                                iconContentDescription = "Delete tag",
                                onClick = {
                                    viewModel.deleteTag(
                                        context = context,
                                        tagId = tag.id,
                                        onSuccess = {
                                            Notification.show(
                                                activity = activity,
                                            ) {
                                                Text(
                                                    text = "Successfully deleted tag"
                                                )
                                            }
                                        },
                                        onError = { error ->
                                            Notification.show(
                                                activity = activity,
                                            ) {
                                                Text(
                                                    text = error,
                                                    color = MaterialTheme.colorScheme.onError
                                                )
                                            }
                                        }
                                    )
                                },
                                color = MaterialTheme.colorScheme.error,
                                iconColor = MaterialTheme.colorScheme.onError,
                                enabled = !state.tagsLoading
                            )
                        }
                    }
                }
            }
        } else {
            Text(
                text = "No tags. Create one by clicking the plus icon.",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                ),
            )
        }
    }
}