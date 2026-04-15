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
import com.stefdp.zipline.screens.folders.FoldersUiState
import com.stefdp.zipline.screens.folders.FoldersViewModel
import com.stefdp.zipline.utils.ZiplineViewStateType
import com.stefdp.zipline.utils.getFolderPath
import com.stefdp.zipline.utils.isChildOf

@Composable
fun MoveFolderPopup(
    context: Context,
    activity: FragmentActivity,
    showPopup: Boolean,
    onDismissRequest: () -> Unit,
    viewModel: FoldersViewModel,
    state: FoldersUiState,
    viewState: ZiplineViewStateType
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
                text = "Move \"${state.moveFolder?.name}\"",
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
            ) + state.allFolders
                .filter { !isChildOf(
                    folder = it,
                    folders = state.allFolders,
                    targetParentId = state.moveFolder?.id ?: ""
                ) }
                .map { folder ->
                    SelectOption(
                        id = folder.id,
                        label = { enabled ->
                            Text(
                                text = getFolderPath(folder, state.allFolders),
                                color = if (enabled)
                                    MaterialTheme.colorScheme.onBackground
                                else
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    )
                },
            onSelectionChange = {
                viewModel.setMoveFolderDestination(it)
            },
            selectedIds = state.moveFolderDestination,
            enabled = !state.isLoading
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
            onClick = {
                viewModel.moveFolder(
                    context = context,
                    viewState = viewState,
                    onSuccess = { folderName ->
                        Notification.show(
                            context = context,
                            activity = activity,
                        ) {
                            Text(
                                text = "$folderName has been moved"
                            )
                        }
                    },
                    onError = { error ->
                        Notification.show(
                            context = context,
                            activity = activity,
                        ) {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
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