package com.stefdp.zipline.screens.settings.categories

import android.content.ClipData
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.LocalUpdateLoggedUserAvatar
import com.stefdp.zipline.R
import com.stefdp.zipline.components.AvatarInput
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.components.UserAvatar
import com.stefdp.zipline.network.models.User
import com.stefdp.zipline.network.models.requests.UpdateCurrentUserBody
import com.stefdp.zipline.network.requests.UpdateCurrentUserResult
import com.stefdp.zipline.network.requests.removeCurrentUserAvatar
import com.stefdp.zipline.ui.theme.getButtonColors
import com.stefdp.zipline.utils.shimmerable
import kotlinx.coroutines.launch

@Composable
internal fun AvatarCategory(
    context: Context,
    activity: FragmentActivity,
    updateUser: suspend (UpdateCurrentUserBody?) -> List<String>,
    isLoading: Boolean,
    setLoading: (Boolean) -> Unit,
    title: String,
) {
    val localUpdateLoggedUserAvatar = LocalUpdateLoggedUserAvatar.current

    Container(
        scrollable = false,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
            )

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

            var newAvatar by remember { mutableStateOf<String?>(null) }

            AvatarInput(
                context = context,
                activity = activity,
                onAvatarChange = { newAvatar = it },
                modifier = Modifier.fillMaxWidth(),
                label = "Upload new avatar...",
                enabled = !isLoading,
            )

            Container(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(12.dp),
                ) {
                    Text(
                        text = "Preview of new avatar"
                    )

                    UserAvatar(
                        overrideAvatar = newAvatar,
                    )
                }
            }

            val coroutineScope = rememberCoroutineScope()

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (newAvatar != null) {
                    Button(
                        modifier = Modifier.weight(0.3f),
                        onClick = {
                            coroutineScope.launch {
                                newAvatar = null
                            }
                        },
                        border = BorderStroke(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.error,
                        ),
                        colors = getButtonColors().copy(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                        ),
                        enabled = !isLoading
                    ) {
                        Text(
                            text = "Cancel"
                        )
                    }
                }

                Button(
                    modifier = Modifier.weight(0.7f),
                    onClick = {
                        coroutineScope.launch {
                            setLoading(true)

                            val removeAvatarRes = removeCurrentUserAvatar(context)

                            removeAvatarRes
                                .onSuccess {
                                    if (it is UpdateCurrentUserResult.Error) {
                                        errors = it.error.issues?.map { error -> "${error.instancePath}: ${error.message}." } ?: listOf(it.error.message ?: it.error.error)
                                    } else if (it is UpdateCurrentUserResult.Success) {
                                        localUpdateLoggedUserAvatar()

                                        Notification.show(
                                            context = context,
                                            activity = activity,
                                            content = {
                                                Text(
                                                    text = "Avatar removed successfully"
                                                )
                                            }
                                        )

                                        updateUser(null)
                                    }
                                }
                                .onFailure {
                                    errors = listOf(it.message ?: "Something went wrong...")
                                }

                            localUpdateLoggedUserAvatar()

                            setLoading(false)
                        }
                    },
                    enabled = !isLoading,
                    colors = getButtonColors().copy(
                        containerColor = MaterialTheme.colorScheme.error,
                        disabledContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.close),
                        contentDescription = "Remove avatar"
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Remove Avatar"
                    )
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    coroutineScope.launch {
                        setLoading(true)

                        val data = UpdateCurrentUserBody(
                            avatar = newAvatar
                        )

                        errors = updateUser(data)

                        setLoading(false)
                    }
                },
                enabled = !isLoading && newAvatar != null
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