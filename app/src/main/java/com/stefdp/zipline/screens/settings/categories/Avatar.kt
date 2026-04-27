package com.stefdp.zipline.screens.settings.categories

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.LocalLoggedUserAvatar
import com.stefdp.zipline.LocalUpdateLoggedUser
import com.stefdp.zipline.LocalUpdateLoggedUserAvatar
import com.stefdp.zipline.R
import com.stefdp.zipline.components.AvatarInput
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.UserAvatar
import com.stefdp.zipline.network.models.requests.UpdateCurrentUserBody
import com.stefdp.zipline.screens.settings.SettingsUiState
import com.stefdp.zipline.screens.settings.SettingsViewModel
import com.stefdp.zipline.ui.theme.getButtonColors
import com.stefdp.zipline.utils.verticalScrollWithScrollbar

@Composable
internal fun AvatarCategory(
    context: Context,
    activity: FragmentActivity,
    updateUser: (
        UpdateCurrentUserBody?,
    ) -> Unit,
    title: String,
    viewModel: SettingsViewModel,
    state: SettingsUiState
) {
    val localLoggedUserAvatar = LocalLoggedUserAvatar.current
    val localUpdateLoggedUserAvatar = LocalUpdateLoggedUserAvatar.current
    val localUpdateLoggedUser = LocalUpdateLoggedUser.current

    Container(
        scrollable = false,
        modifier = Modifier.fillMaxWidth()
    ) {
        val scrollState = rememberScrollState()

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .verticalScrollWithScrollbar(
                    scrollState = scrollState,
                )
                .padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
            )

            AvatarInput(
                context = context,
                activity = activity,
                onAvatarChange = {
                    viewModel.setNewAvatar(it)
                },
                modifier = Modifier.fillMaxWidth(),
                label = "Upload new avatar...",
                enabled = !state.isLoading,
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
                        overrideAvatar = state.newAvatar,
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.newAvatar != null) {
                    Button(
                        modifier = Modifier.weight(0.3f),
                        onClick = {
                            viewModel.setNewAvatar(null)
                        },
                        border = BorderStroke(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.error,
                        ),
                        colors = getButtonColors().copy(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                        ),
                        enabled = !state.isLoading
                    ) {
                        Text(
                            text = "Cancel"
                        )
                    }
                }

                if (localLoggedUserAvatar != null) {
                    Button(
                        modifier = Modifier.weight(0.7f),
                        onClick = {
                            viewModel.removeUserAvatar(
                                context = context,
                                localUpdateLoggedUser = localUpdateLoggedUser,
                                localUpdateLoggedUserAvatar = localUpdateLoggedUserAvatar,
                                onError = { errors ->
                                    Notification.show(
                                        activity = activity,
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text("Failed to remove user avatar:")

                                            errors.forEach {
                                                Text(
                                                    text = it,
                                                    color = MaterialTheme.colorScheme.error,
                                                )
                                            }
                                        }
                                    }
                                },
                                onSuccess = {
                                    Notification.show(
                                        activity = activity,
                                    ) {
                                        Text("User avatar removed successfully")
                                    }
                                }
                            )
                        },
                        enabled = !state.isLoading,
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
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val data = UpdateCurrentUserBody(
                        avatar = state.newAvatar
                    )

                    updateUser(data)

                    viewModel.updateUserAvatar(
                        context = context,
                        localUpdateLoggedUserAvatar = localUpdateLoggedUserAvatar,
                    )
                },
                enabled = !state.isLoading && state.newAvatar != null
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