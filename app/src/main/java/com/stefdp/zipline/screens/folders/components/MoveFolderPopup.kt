package com.stefdp.zipline.screens.folders.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.components.Select
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.BaseFolder
import com.stefdp.zipline.network.requests.moveFolder
import com.stefdp.zipline.network.requests.updateFolder
import com.stefdp.zipline.ui.theme.getButtonColors
import com.stefdp.zipline.utils.getFolderPath
import kotlinx.coroutines.launch

@Composable
fun MoveFolderPopup(
    context: Context,
    activity: FragmentActivity,
    showPopup: Boolean,
    onDismissRequest: () -> Unit,
    folder: BaseFolder?,
    isLoading: Boolean,
    setIsLoading: (Boolean) -> Unit,
    updateFolders: suspend () -> Unit,
    allFolders: List<BaseFolder>,
) {
    Popup(
        showPopup = showPopup,
        onDismissRequest = onDismissRequest,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Move \"${folder?.name}\"",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
            )

            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(
                        onClick = onDismissRequest
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.close),
                    contentDescription = "Close move folder menu",
                )
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Select a destination folder for this folder.",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        var destination by remember { mutableStateOf(setOf("default")) }

        Select(
            label = "Destination",
            description = "Add this file to a folder. Use the \"/ (Root)\" option to not add the file to a folder. This value is not saved to your browser, and is cleared after uploading.",
            options = listOf(
                SelectOption(
                    id = "default",
                    label = {
                        Text("/ (Root)")
                    }
                )
            ) + allFolders.map { folder ->
                SelectOption(
                    id = folder.id,
                    label = { enabled ->
                        Text(
                            text = getFolderPath(folder, allFolders),
                            color = if (enabled)
                                MaterialTheme.colorScheme.onBackground
                            else
                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                )
            },
            onSelectionChange = { destination = it },
            selectedIds = destination,
            enabled = !isLoading
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        val coroutineScope = rememberCoroutineScope()

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            onClick = {
                coroutineScope.launch {
                    if (folder == null) return@launch

                    setIsLoading(true)

                    val newParentId = destination.firstOrNull()

                    val moveFolderRes = moveFolder(
                        context = context,
                        folderId = folder.id,
                        newParentId = if (newParentId == "default") null else newParentId
                    )

                    moveFolderRes
                        .onSuccess {
                            updateFolders()

                            Notification.show(
                                context = context,
                                activity = activity,
                                content = {
                                    Text(
                                        text = "${folder.name} has been moved"
                                    )
                                },
                            )

                            onDismissRequest()
                        }
                        .onFailure {
                            Notification.show(
                                context = context,
                                activity = activity,
                                content = {
                                    Text(
                                        text = "Failed to move folder: ${it.message}"
                                    )
                                },
                            )
                        }

                    setIsLoading(false)
                }
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.folder_copy),
                contentDescription = "Move folder",
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = "Move Folder"
            )
        }
    }
}