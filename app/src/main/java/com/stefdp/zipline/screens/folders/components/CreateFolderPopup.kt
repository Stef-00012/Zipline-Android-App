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
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.BaseFolder
import com.stefdp.zipline.network.requests.createFolder
import kotlinx.coroutines.launch

@Composable
fun CreateFolderPopup(
    context: Context,
    activity: FragmentActivity,
    showPopup: Boolean,
    onDismissRequest: () -> Unit,
    mainFolder: BaseFolder?,
    isLoading: Boolean,
    setIsLoading: (Boolean) -> Unit,
    updateFolders: suspend () -> Unit,
    updateAllFolders: suspend () -> Unit,
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
                text = "Create a ${if (mainFolder == null) "Folder" else "Subfolder"}",
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
                    contentDescription = "Close create folder menu",
                )
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        var folderName by remember { mutableStateOf(TextFieldValue("")) }

        TextInput(
            label = "Name",
            placeholder = "Enter a name...",
            value = folderName,
            onValueChange = { folderName = it },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        var isPublic by remember { mutableStateOf(false) }

        Switch(
            checked = isPublic,
            onCheckedChange = { isPublic = it },
            label = "Public",
            description = "Public folders are visible to everyone.",
            enabled = !isLoading
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        val coroutineScope = rememberCoroutineScope()

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && folderName.text.isNotBlank(),
            onClick = {
                coroutineScope.launch {
                    setIsLoading(true)

                    val createFolderRes = createFolder(
                        context = context,
                        parentId = mainFolder?.id,
                        name = folderName.text,
                        isPublic = isPublic
                    )

                    createFolderRes
                        .onSuccess {
                            updateFolders()
                            updateAllFolders()

                            Notification.show(
                                context = context,
                                activity = activity,
                                content = {
                                    Text(
                                        text = "${it.name} has been created"
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
                                        text = "Failed to create folder: ${it.message}"
                                    )
                                },
                            )
                        }

                    setIsLoading(false)
                }
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.create_new_folder),
                contentDescription = "Move folder",
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = "Create"
            )
        }
    }
}