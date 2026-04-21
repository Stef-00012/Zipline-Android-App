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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.components.Select
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.network.models.requests.DeleteFolderChildrenAction
import com.stefdp.zipline.screens.folders.FoldersUiState
import com.stefdp.zipline.screens.folders.FoldersViewModel
import com.stefdp.zipline.ui.theme.getButtonColors
import com.stefdp.zipline.utils.ZiplineViewStateType
import com.stefdp.zipline.utils.getFolderPath
import com.stefdp.zipline.utils.isChildOf

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
    filesPerPage: Long,
    onDismissRequest: () -> Unit,
    viewState: ZiplineViewStateType,
    viewModel: FoldersViewModel,
    state: FoldersUiState,
) {
    Popup(
        showPopup = showPopup,
        onDismissRequest = onDismissRequest,
    ) {
        val deleteFolderName = state.deleteFolder?.name ?: ""

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

        val folderChildrenCount = state.deleteFolder?.count?.children ?: 0
        val folderFileCount = state.deleteFolder?.count?.files ?: 0

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
                selectedIds = state.selectedDeleteFolderType,
                onSelectionChange = { selectedIds ->
                    viewModel.setSelectedDeleteFolderType(selectedIds)
                },
                enabled = !state.isLoading
            )

            when (DeleteFolderChildrenAction.valueOf(state.selectedDeleteFolderType.first())) {
                DeleteFolderChildrenAction.MOVE_TO_FOLDER -> {
                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Select(
                        label = "Target Folder",
                        selectedIds = state.selectedDeleteNewFolder,
                        options = state.allFolders
                            .filter { !isChildOf(
                                folder = it,
                                folders = state.allFolders,
                                targetParentId = state.deleteFolder?.id ?: ""
                            ) }
                            .map { folder ->
                                SelectOption(
                                    id = folder.id,
                                    label = {
                                        Text(
                                            text = getFolderPath(folder, state.allFolders)
                                        )
                                    }
                                )
                            },
                        onSelectionChange = { selectedIds ->
                            viewModel.setSelectedDeleteNewFolder(selectedIds)
                        },
                        enabled = !state.isLoading
                    )
                }

                DeleteFolderChildrenAction.CASCADE_DELETE -> {
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Warning: This will permanently delete all contents within this folder (subfolders will be deleted, and files will be unlinked from their folders).",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {}
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading && (DeleteFolderChildrenAction.valueOf(state.selectedDeleteFolderType.first()) != DeleteFolderChildrenAction.MOVE_TO_FOLDER ||
                    (DeleteFolderChildrenAction.valueOf(state.selectedDeleteFolderType.first()) == DeleteFolderChildrenAction.MOVE_TO_FOLDER && state.selectedDeleteNewFolder.first() != "none")),
            colors = getButtonColors().copy(
                containerColor = MaterialTheme.colorScheme.error,
                disabledContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
            ),
            onClick = {
                viewModel.deleteFolder(
                    context = context,
                    filesPerPage = filesPerPage,
                    viewState = viewState,
                    onError = { error ->
                        Notification.show(
                            activity = activity,
                        ) {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    onSuccess = {
                        Notification.show(
                            activity = activity,
                        ) {
                            Text(
                                text = "$deleteFolderName has been deleted"
                            )
                        }
                    }
                )
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