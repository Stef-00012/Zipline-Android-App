package com.stefdp.zipline.components.largefiledisplay

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
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.network.models.File
import com.stefdp.zipline.network.requests.deleteFile
import com.stefdp.zipline.ui.theme.DarkGray
import com.stefdp.zipline.ui.theme.getButtonColors
import kotlinx.coroutines.launch

@Composable
internal fun DeleteFilePromptPopup(
    context: Context,
    showPopup: Boolean,
    file: File,
    isLoading: Boolean,
    onDismissRequest: () -> Unit,
    updateData: suspend () -> Unit,
    setLoading: (Boolean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Popup(
        showPopup = showPopup,
        onDismissRequest = onDismissRequest
    ) {
        Text(
            text = "Are you sure?",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
        )

        Text(
            text = "Are you sure you want to delete ${file.originalName ?: file.name}? This action cannot be undone."
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
                    coroutineScope.launch {
                        setLoading(true)

                        val deleteRes = deleteFile(
                            context = context,
                            fileId = file.id
                        )

                        deleteRes
                            .onSuccess {
                                updateData()
                                onDismissRequest()
                            }
                            .onFailure {
                                Toast.makeText(
                                    context,
                                    "Failed to delete file: ${it.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        setLoading(false)
                        onDismissRequest()
                    }
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