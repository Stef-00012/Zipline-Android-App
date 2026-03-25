package com.stefdp.zipline.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.network.models.File
import com.stefdp.zipline.network.requests.deleteFile
import com.stefdp.zipline.ui.theme.DarkGray
import com.stefdp.zipline.ui.theme.getButtonColors
import kotlinx.coroutines.launch

@Composable
fun DeletePromptPopup(
    showPopup: Boolean,
    isLoading: Boolean,
    onDismissRequest: () -> Unit,
    onDelete: () -> Unit,
    title: String,
    description: String,
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

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 8.dp
                ),
        ) {
            Button(
                onClick = onDismissRequest,
                colors = getButtonColors().copy(
                    containerColor = DarkGray,
                    disabledContainerColor = DarkGray.copy(alpha = 0.5f)
                ),
                enabled = !isLoading
            ) {
                Text("Cancel")
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
                Text("Delete")
            }
        }
    }
}