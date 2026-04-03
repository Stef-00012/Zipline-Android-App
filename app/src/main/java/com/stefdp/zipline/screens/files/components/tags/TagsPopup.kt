package com.stefdp.zipline.screens.files.components.tags

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.R
import com.stefdp.zipline.components.HeaderButton
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.network.models.Tag
import com.stefdp.zipline.network.requests.deleteTag
import com.stefdp.zipline.screens.files.components.IconButton
import com.stefdp.zipline.ui.theme.ZiplineTheme
import kotlinx.coroutines.launch
import com.stefdp.zipline.components.Tag as TagComponent

@Composable
fun TagsPopup(
    context: Context,
    activity: FragmentActivity,
    showPopup: Boolean,
    onDismissRequest: () -> Unit,
    tags: List<Tag>,
    updateTags: suspend () -> Unit
) {
    var showCreateTagPopup by remember { mutableStateOf(false) }

    var tagToEdit by remember { mutableStateOf<Tag?>(null) }

    val showMainPopup = showPopup && !showCreateTagPopup && tagToEdit == null

    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }

    CreateTagPopup(
        context = context,
        activity = activity,
        showPopup = showCreateTagPopup,
        onDismissRequest = {
            showCreateTagPopup = false
        },
        updateTags = updateTags,
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
                        showCreateTagPopup = true
                    },
                    icon = painterResource(id = R.drawable.add),
                    contentDescription = "Create tag",
                    enabled = !isLoading
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

        if (tags.isNotEmpty()) {
            LazyColumn {
                items(tags.size) {
                    val tag = tags[it]

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
                                    tagToEdit = tag
                                },
                                color = MaterialTheme.colorScheme.primary,
                                iconColor = MaterialTheme.colorScheme.onPrimary,
                                enabled = !isLoading
                            )

                            Spacer(
                                modifier = Modifier.width(4.dp)
                            )

                            IconButton(
                                icon = painterResource(R.drawable.delete),
                                iconContentDescription = "Delete tag",
                                onClick = {
                                    coroutineScope.launch {
                                        isLoading = true

                                        val deleteTagRes = deleteTag(
                                            context = context,
                                            tagId = tag.id
                                        )

                                        val deleteStatus = deleteTagRes.getOrNull()

                                        if (deleteStatus == true) {
                                            Notification.show(
                                                context,
                                                activity = activity,
                                                content = {
                                                    Text(
                                                        text = "Successfully deleted tag"
                                                    )
                                                }
                                            )

                                            updateTags()
                                        } else {
                                            Notification.show(
                                                context = context,
                                                activity = activity,
                                                content = {
                                                    Text(
                                                        text = "Failed to delete tag"
                                                    )
                                                }
                                            )
                                        }

                                        isLoading = false
                                    }
                                },
                                color = MaterialTheme.colorScheme.error,
                                iconColor = MaterialTheme.colorScheme.onError,
                                enabled = !isLoading
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