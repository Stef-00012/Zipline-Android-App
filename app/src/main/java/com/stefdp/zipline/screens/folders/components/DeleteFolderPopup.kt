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
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.Logger
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.components.Select
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.network.models.BaseFolder
import com.stefdp.zipline.network.models.requests.DeleteFolderChildrenAction
import com.stefdp.zipline.network.requests.deleteFolder
import com.stefdp.zipline.ui.theme.getButtonColors
import com.stefdp.zipline.utils.getFolderPath
import com.stefdp.zipline.utils.isChildOf
import kotlinx.coroutines.launch

private val deleteFolderTypeSelectOptions = listOf(
    DeleteFolderChildrenAction.MOVE_TO_ROOT,
    DeleteFolderChildrenAction.MOVE_TO_FOLDER,
    DeleteFolderChildrenAction.CASCADE_DELETE
)

@Composable
fun DeleteFolderPopup(
    context: Context,
    activity: FragmentActivity,
    showPopup: Boolean,
    onDismissRequest: () -> Unit,
    folder: BaseFolder?,
    isLoading: Boolean,
    setIsLoading: (Boolean) -> Unit,
    allFolders: List<BaseFolder>,
    updateFolders: suspend () -> Unit,
    updateAllFolders: suspend () -> Unit,
) {
    var selectedDeleteFolderType by remember { mutableStateOf(setOf(DeleteFolderChildrenAction.MOVE_TO_ROOT.toString())) }
    var selectedNewFolder by remember { mutableStateOf(setOf("none")) }

    Popup(
        showPopup = showPopup,
        onDismissRequest = onDismissRequest,
    ) {
        val deleteFolderName = folder?.name ?: ""

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Delete \"$deleteFolderName\"?",
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
                    contentDescription = "Close delete menu",
                )
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "This action cannot be undone.",
            color = MaterialTheme.colorScheme.error
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        val folderChildrenCount = folder?.count?.children ?: 0
        val folderFileCount = folder?.count?.files ?: 0

        if (folderChildrenCount > 0 || folderFileCount > 0) {
            Text(
                text = "This folder contains ${if (folderFileCount > 0) "$folderFileCount file(s)" else ""}${if (folderChildrenCount > 0) "${if (folderFileCount > 0) " and " else ""} $folderChildrenCount subfolder(s)" else ""}. What would you like to do with them?"
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Select(
                options = deleteFolderTypeSelectOptions.map { option ->
                    SelectOption(
                        id = option.toString(),
                        label = {
                            Text(
                                text = option.displayName,
                                color = if (option == DeleteFolderChildrenAction.CASCADE_DELETE)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                },
                selectedIds = selectedDeleteFolderType,
                onSelectionChange = { selectedIds ->
                    selectedDeleteFolderType = selectedIds
                },
                enabled = !isLoading
            )

            val deleteFolderType = DeleteFolderChildrenAction.valueOf(selectedDeleteFolderType.first())

            if (deleteFolderType == DeleteFolderChildrenAction.MOVE_TO_FOLDER) {
                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Select(
                    label = "Target Folder",
                    selectedIds = selectedNewFolder,
                    options = allFolders
                        .filter { !isChildOf(
                            folder = it,
                            folders = allFolders,
                            targetParentId = folder?.id ?: ""
                        ) }
                        .map { folder ->
                            SelectOption(
                                id = folder.id,
                                label = {
                                    Text(
                                        text = getFolderPath(folder, allFolders)
                                    )
                                }
                            )
                        },
                    onSelectionChange = { selectedIds ->
                        selectedNewFolder = selectedIds
                    },
                    enabled = !isLoading
                )
            } else if (deleteFolderType == DeleteFolderChildrenAction.CASCADE_DELETE) {
                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Warning: This will permanently delete all contents within this folder (subfolders will be deleted, and files will be unlinked from their folders).",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        val coroutineScope = rememberCoroutineScope()

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && (DeleteFolderChildrenAction.valueOf(selectedDeleteFolderType.first()) != DeleteFolderChildrenAction.MOVE_TO_FOLDER ||
                    (DeleteFolderChildrenAction.valueOf(selectedDeleteFolderType.first()) == DeleteFolderChildrenAction.MOVE_TO_FOLDER && selectedNewFolder.first() != "none")),
            colors = getButtonColors().copy(
                containerColor = MaterialTheme.colorScheme.error,
                disabledContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
            ),
            onClick = {
                coroutineScope.launch {
                    if (folder == null) return@launch

                    setIsLoading(true)

                    val deleteRes = deleteFolder(
                        context = context,
                        folderId = folder.id,
                        childrenAction = DeleteFolderChildrenAction.valueOf(selectedDeleteFolderType.first()),
                        targetFolderId = selectedNewFolder.firstOrNull()
                    )

                    deleteRes
                        .onSuccess {
                            updateFolders()
                            updateAllFolders()

                            Notification.show(
                                context = context,
                                activity = activity,
                                content = {
                                    Text(
                                        text = "$deleteFolderName has been deleted"
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
                                        text = "Failed to delete folder: ${it.message}"
                                    )
                                },
                            )
                        }

                    setIsLoading(false)
                }
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.delete),
                contentDescription = "Delete folder",
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = "Delete Folder"
            )
        }
    }
}