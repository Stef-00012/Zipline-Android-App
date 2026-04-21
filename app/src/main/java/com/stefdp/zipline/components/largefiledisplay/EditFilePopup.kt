package com.stefdp.zipline.components.largefiledisplay

import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.File
import com.stefdp.zipline.network.requests.updateFile
import com.stefdp.zipline.ui.theme.getButtonColors
import kotlinx.coroutines.launch

val intRegex = Regex("^[0-9]*$")

@Composable
internal fun EditFilePopup(
    context: Context,
    activity: FragmentActivity,
    showPopup: Boolean,
    file: File,
    isLoading: Boolean,
    onDismissRequest: () -> Unit,
    updateData: suspend () -> Unit,
    setLoading: (Boolean) -> Unit
)  {
    val coroutineScope = rememberCoroutineScope()

    Popup(
        showPopup = showPopup,
        onDismissRequest = onDismissRequest
    ) {
        Text(
            text = "Editing ${file.originalName ?: file.name}",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
        )

        Spacer()

        var name by rememberSaveable(
            stateSaver = TextFieldValue.Saver
        ) { mutableStateOf(TextFieldValue(file.name)) }

        TextInput(
            modifier = Modifier.fillMaxWidth(),
            label = "Name",
            onValueChange = { name = it },
            value = name,
            placeholder = file.name
        )

        Spacer()

        var maxViews by rememberSaveable(
            stateSaver = TextFieldValue.Saver
        ) {
            val value = if (file.maxViews != null) file.maxViews.toString() else ""

            mutableStateOf(TextFieldValue(value))
        }

        TextInput(
            modifier = Modifier.fillMaxWidth(),
            label = "Max Views",
            onValueChange = {
                if (intRegex.matches(it.text)) maxViews = it
            },
            value = maxViews,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            placeholder = if (file.maxViews != null) file.maxViews.toString() else "Unlimited"
        )

        Spacer()

        var originalName by rememberSaveable(
            stateSaver = TextFieldValue.Saver
        ) { mutableStateOf(TextFieldValue(file.originalName ?: "")) }

        TextInput(
            modifier = Modifier.fillMaxWidth(),
            label = "Original Name",
            onValueChange = { originalName = it },
            value = originalName,
            placeholder = file.originalName ?: "None"
        )

        Spacer()

        var type by rememberSaveable(
            stateSaver = TextFieldValue.Saver
        ) { mutableStateOf(TextFieldValue(file.type)) }

        TextInput(
            modifier = Modifier.fillMaxWidth(),
            label = "Type",
            onValueChange = { type = it },
            value = type,
            placeholder = file.type
        )

        Spacer()

        var password by rememberSaveable(
            stateSaver = TextFieldValue.Saver
        ) { mutableStateOf(TextFieldValue("")) }

        if (file.password == true) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        setLoading(true)

                        val editFileRes = updateFile(
                            context = context,
                            fileId = file.id,
                            password = ""
                        )

                        editFileRes.onSuccess {
                            updateData()
                            onDismissRequest()
                        }
                        editFileRes.onFailure {
                            Notification.show(
                                activity = activity,
                            ) {
                                Text(
                                    text = "Failed to remove password: ${it.message}"
                                )
                            }
                        }

                        setLoading(false)
                    }
                },
                colors = getButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.error,
                    disabledContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(
                    text = "Remove Password"
                )
            }
        } else {
            TextInput(
                modifier = Modifier.fillMaxWidth(),
                label = "Password",
                onValueChange = { password = it },
                value = password,
                isPassword = true,
                placeholder = "mYC00lP4sSw0rD"
            )
        }

        Spacer()

        Button(
            onClick = {
                coroutineScope.launch {
                    setLoading(true)

                    val editFileRes = updateFile(
                        context = context,
                        fileId = file.id,
                        maxViews = if (maxViews.text.isBlank()) null else maxViews.text.toLong(),
                        originalName = originalName.text.ifBlank { null },
                        name = name.text.ifBlank { null },
                        type = type.text.ifBlank { null },
                        password = password.text.ifBlank { null }
                    )

                    editFileRes.onSuccess {
                        updateData()
                        onDismissRequest()
                    }
                    editFileRes.onFailure {
                        Notification.show(
                            activity = activity,
                        ) {
                            Text(
                                text = "Failed to update file: ${it.message}"
                            )
                        }
                    }

                    setLoading(false)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Icon(
                painter = painterResource(R.drawable.save),
                contentDescription = "Save changes"
            )

            Spacer(
                modifier = Modifier.width(5.dp)
            )

            Text(
                text = "Save Changes"
            )
        }
    }
}