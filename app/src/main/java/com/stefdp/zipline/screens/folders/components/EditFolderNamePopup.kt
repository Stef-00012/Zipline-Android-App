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
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.screens.folders.FoldersUiState
import com.stefdp.zipline.screens.folders.FoldersViewModel
import com.stefdp.zipline.utils.ZiplineViewStateType

@Composable
fun EditFolderNamePopup(
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

        TextInput(
            label = "New Folder Name",
            placeholder = "Enter new folder name...",
            value = state.editNameFolderNewName,
            onValueChange = {
                viewModel.setEditNameFolderNewName(it)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading && state.editNameFolderNewName.text.isNotBlank(),
            onClick = {
                viewModel.editFolderName(
                    context = context,
                    viewState = viewState,
                    onSuccess = { oldFolderName, newFolderName ->
                        Notification.show(
                            context = context,
                            activity = activity,
                        ) {
                            Text(
                                text = "$oldFolderName has been updated successfully to $newFolderName"
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