package com.stefdp.zipline.screens.settings.categories

import android.content.ClipData
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.User
import com.stefdp.zipline.network.models.requests.UpdateCurrentUserBody
import com.stefdp.zipline.utils.shimmerable
import kotlinx.coroutines.launch

@Composable
internal fun UserCategory(
    context: Context,
    user: User?,
    token: String?,
    updateUser: suspend (UpdateCurrentUserBody) -> List<String>,
    isLoading: Boolean,
    setLoading: (Boolean) -> Unit,
    title: String,
    settingsUpdateTick: Int
) {
    Container(
        scrollable = false,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(12.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )

                Text(
                    text = user?.id ?: "--------------------------------------",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    modifier = Modifier.shimmerable(
                        enabled = user?.id == null,
                        color = MaterialTheme.colorScheme.surface,
                    )
                )
            }

            var errors by remember { mutableStateOf<List<String>>(emptyList()) }

            if (errors.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    errors.forEach {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }

            var isTokenVisible by remember { mutableStateOf(false) }

            var tokenInput by remember(token, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(token ?: ""))
            }

            val clipboardManager = LocalClipboard.current

            val coroutineScope = rememberCoroutineScope()

            TextInput(
                value = if (isTokenVisible) tokenInput else TextFieldValue("[Click to Reveal]"),
                onValueChange = {},
                readOnly = true,
                label = "Token",
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        onClick = { isTokenVisible = true },
                        enabled = !isLoading && !isTokenVisible && token != null
                    ),
                sideButtonIcon = painterResource(R.drawable.content_copy),
                onSideButtonPress = {
                    coroutineScope.launch {
                        val clipData = ClipData.newPlainText("User Token", tokenInput.text).toClipEntry()

                        clipboardManager.setClipEntry(clipData)

                        Toast.makeText(
                            context,
                            "Token copied to clipboard",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )

            var username by remember(user?.username, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(user?.username ?: ""))
            }

            TextInput(
                value = username,
                onValueChange = { username = it },
                label = "Username",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            var password by remember {
                mutableStateOf(TextFieldValue(""))
            }

            TextInput(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                description = "Leave blank to keep the same password.",
                isPassword = true,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    coroutineScope.launch {
                        setLoading(true)

                        val data = UpdateCurrentUserBody(
                            password = password.text.takeIf { it.isNotEmpty() },
                            username = username.text.takeIf { it.isNotEmpty() },
                        )



                        val updateUserErrors = updateUser(data)

                        errors = updateUserErrors

                        setLoading(false)
                    }
                },
                enabled = !isLoading
            ) {
                Icon(
                    painter = painterResource(R.drawable.save),
                    contentDescription = "Save"
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Save"
                )
            }
        }
    }
}