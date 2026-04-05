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
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.BaseFolder
import com.stefdp.zipline.network.requests.updateFolder
import com.stefdp.zipline.ui.theme.getButtonColors
import kotlinx.coroutines.launch

@Composable
fun EditFolderNamePopup(
    context: Context,
    activity: FragmentActivity,
    showPopup: Boolean,
    onDismissRequest: () -> Unit,
    folder: BaseFolder?,
    isLoading: Boolean,
    setIsLoading: (Boolean) -> Unit,
    updateFolders: suspend () -> Unit,
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
                text = "Edit Folder Name",
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
                    contentDescription = "Close edit folder name menu",
                )
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        var newFolderName by remember { mutableStateOf(TextFieldValue(folder?.name ?: "")) }

        TextInput(
            label = "New Folder Name",
            placeholder = "Enter new folder name...",
            value = newFolderName,
            onValueChange = { newFolderName = it },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        val coroutineScope = rememberCoroutineScope()

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && newFolderName.text.isNotBlank(),
            onClick = {
                coroutineScope.launch {
                    if (folder == null) return@launch

                    setIsLoading(true)

                    val updateFolderRes = updateFolder(
                        context = context,
                        folderId = folder.id,
                        name = newFolderName.text
                    )

                    updateFolderRes
                        .onSuccess {
                            updateFolders()

                            Notification.show(
                                context = context,
                                activity = activity,
                                content = {
                                    Text(
                                        text = "${folder.name} has been updated successfully to ${it.name}"
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
                                        text = "Failed to rename folder: ${it.message}"
                                    )
                                },
                            )
                        }

                    setIsLoading(false)
                }
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.edit),
                contentDescription = "Edit name",
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = "Edit Name"
            )
        }
    }
}