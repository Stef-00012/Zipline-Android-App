package com.stefdp.zipline.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.ui.theme.DarkGray
import com.stefdp.zipline.ui.theme.getButtonColors

@Composable
fun DeletePromptPopup(
    showPopup: Boolean,
    isLoading: Boolean,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    onDismissRequest: () -> Unit,
    title: String,
    description: String,
    cancelText: String = "Cancel",
    deleteText: String = "Delete",
    buttonLayout: DeletePromptButtonLayout = DeletePromptButtonLayout.HORIZONTAL
) {
    Popup(
        showPopup = showPopup,
        onDismissRequest = onDismissRequest
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
        )

        Text(
            text = description
        )

        if (buttonLayout == DeletePromptButtonLayout.HORIZONTAL) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 8.dp
                    ),
            ) {
                Button(
                    onClick = onCancel,
                    colors = getButtonColors().copy(
                        containerColor = DarkGray,
                        disabledContainerColor = DarkGray.copy(alpha = 0.5f)
                    ),
                    enabled = !isLoading
                ) {
                    Text(cancelText)
                }

                Button(
                    onClick = {
                        onDelete()
                    },
                    colors = getButtonColors().copy(
                        containerColor = MaterialTheme.colorScheme.error,
                        disabledContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                    ),
                    enabled = !isLoading
                ) {
                    Text(deleteText)
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 8.dp
                    ),
            ) {
                Button(
                    onClick = onCancel,
                    colors = getButtonColors().copy(
                        containerColor = DarkGray,
                        disabledContainerColor = DarkGray.copy(alpha = 0.5f)
                    ),
                    enabled = !isLoading
                ) {
                    Text(cancelText)
                }

                Button(
                    onClick = {
                        onDelete()
                    },
                    colors = getButtonColors().copy(
                        containerColor = MaterialTheme.colorScheme.error,
                        disabledContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                    ),
                    enabled = !isLoading
                ) {
                    Text(deleteText)
                }
            }
        }


    }
}

enum class DeletePromptButtonLayout {
    HORIZONTAL,
    VERTICAL
}