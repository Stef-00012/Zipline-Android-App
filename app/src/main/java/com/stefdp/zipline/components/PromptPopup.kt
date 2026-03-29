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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.ui.theme.DarkGray
import com.stefdp.zipline.ui.theme.getButtonColors
import com.stefdp.zipline.utils.toAnnotatedString

@Composable
fun PromptPopup(
    showPopup: Boolean,
    isLoading: Boolean = false,
    onCancel: () -> Unit,
    onSuccess: () -> Unit,
    onDismissRequest: () -> Unit,
    title: CharSequence,
    description: CharSequence,
    cancelText: CharSequence = "Cancel",
    successText: CharSequence = "Delete",
    cancelColor: Color = DarkGray,
    successColor: Color = MaterialTheme.colorScheme.error,
    buttonLayout: DeletePromptButtonLayout = DeletePromptButtonLayout.HORIZONTAL,
    content: @Composable () -> Unit = { }
) {
    Popup(
        showPopup = showPopup,
        onDismissRequest = onDismissRequest
    ) {
        Text(
            text = title.toAnnotatedString(),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
        )

        Text(
            text = description.toAnnotatedString()
        )

        content()

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
                        containerColor = cancelColor,
                        disabledContainerColor = cancelColor.copy(alpha = 0.5f)
                    ),
                    enabled = !isLoading
                ) {
                    Text(cancelText.toAnnotatedString())
                }

                Button(
                    onClick = {
                        onSuccess()
                    },
                    colors = getButtonColors().copy(
                        containerColor = successColor,
                        disabledContainerColor = successColor.copy(alpha = 0.5f)
                    ),
                    enabled = !isLoading
                ) {
                    Text(successText.toAnnotatedString())
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
                        containerColor = cancelColor,
                        disabledContainerColor = cancelColor.copy(alpha = 0.5f)
                    ),
                    enabled = !isLoading
                ) {
                    Text(cancelText.toAnnotatedString())
                }

                Button(
                    onClick = {
                        onSuccess()
                    },
                    colors = getButtonColors().copy(
                        containerColor = successColor,
                        disabledContainerColor = successColor.copy(alpha = 0.5f)
                    ),
                    enabled = !isLoading
                ) {
                    Text(successText.toAnnotatedString())
                }
            }
        }
    }
}

enum class DeletePromptButtonLayout {
    HORIZONTAL,
    VERTICAL
}