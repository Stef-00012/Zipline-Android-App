package com.stefdp.zipline.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.network.requests.verifyFilePassword
import kotlinx.coroutines.launch

@Composable
fun DownloadFilePasswordPrompt(
    context: Context,
    activity: FragmentActivity,
    showPopup: Boolean,
    onDismissRequest: () -> Unit,
    onDownload: (password: String) -> Unit,
    fileId: String
) {
    var password by remember { mutableStateOf(TextFieldValue("")) }

    Popup(
        showPopup = showPopup,
        onDismissRequest = onDismissRequest
    ) {
        Text(
            text = "File Password",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
        )

        TextInput(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            placeholder = "Enter the file password...",
            modifier = Modifier.fillMaxWidth(),
            isPassword = true
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        val coroutineScope = rememberCoroutineScope()

        Button(
            onClick = {
                coroutineScope.launch {
                    val verifyFilePasswordRes = verifyFilePassword(
                        context = context,
                        fileId = fileId,
                        password = password.text
                    )

                    verifyFilePasswordRes
                        .onSuccess {
                            onDownload(password.text)
                        }
                        .onFailure {
                            Notification.show(
                                context = context,
                                activity = activity,
                            ) {
                                Text(
                                    text = "Incorrect password. Please try again."
                                )
                            }
                        }
                }
            },
            enabled = password.text.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Download"
            )
        }
    }
}