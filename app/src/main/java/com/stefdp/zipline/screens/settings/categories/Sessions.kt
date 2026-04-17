package com.stefdp.zipline.screens.settings.categories

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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
import com.stefdp.zipline.components.IconButton
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.PromptPopup
import com.stefdp.zipline.components.UserAvatar
import com.stefdp.zipline.components.table.Table
import com.stefdp.zipline.components.table.TableCellData
import com.stefdp.zipline.components.table.TableHeader
import com.stefdp.zipline.components.table.TableHeaderData
import com.stefdp.zipline.components.table.TableRowData
import com.stefdp.zipline.network.models.User
import com.stefdp.zipline.network.models.requests.UpdateCurrentUserBody
import com.stefdp.zipline.screens.settings.SettingsUiState
import com.stefdp.zipline.screens.settings.SettingsViewModel
import com.stefdp.zipline.ui.theme.getButtonColors
import com.stefdp.zipline.utils.formatDate
import kotlin.time.Instant

@Composable
internal fun SessionsCategory(
    context: Context,
    activity: FragmentActivity,
    user: User?,
    title: String,
    viewModel: SettingsViewModel,
    state: SettingsUiState
) {
    PromptPopup(
        showPopup = state.deleteAllSessionsPopupOpen,
        title = "Log out of all devices?",
        description = "Are you sure you want to log out of all devices? This will log you out of all devices except the current one.",
        onDismissRequest = { viewModel.closeDeleteAllSessionsPopup() },
        successText = "Log out",
        onSuccess = {
            viewModel.deleteAllSessions(
                context = context,
                onSuccess = {
                    Notification.show(
                        context = context,
                        activity = activity,
                    ) {
                        Text(
                            text = "Successfully logged out of all devices"
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
        },
        onCancel = { viewModel.closeDeleteAllSessionsPopup() },
    )

    PromptPopup(
        showPopup = state.deleteSession != null,
        title = "Log out of device?",
        description = "Are you sure you want to log out of this device?",
        onDismissRequest = { viewModel.setDeleteSession(null) },
        successText = "Log out",
        onSuccess = {
            viewModel.deleteSession(
                context = context,
                onSuccess = {
                    Notification.show(
                        context = context,
                        activity = activity,
                    ) {
                        Text(
                            text = "Successfully logged out of the device"
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
        },
        onCancel = { viewModel.closeDeleteAllSessionsPopup() },
    )

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

            Text(
                text = "You are currently logged into ${user?.sessions?.size ?: 0} other devices"
            )

            Container(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surface),
                scrollable = false
            ) {
                val clientWidth = 150.dp
                val deviceWidth = 150.dp
                val loggedInAtWidth = 200.dp
                val actionsWidth = 80.dp

                val headers: List<TableHeaderData> = listOf(
                    TableHeaderData(
                        name = "client",
                        width = clientWidth,
                    ) {
                        Text(
                            text = "Client",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    TableHeaderData(
                        name = "device",
                        width = deviceWidth,
                    ) {
                        Text(
                            text = "Device",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    TableHeaderData(
                        name = "loggedInAt",
                        width = loggedInAtWidth,
                    ) {
                        Text(
                            text = "Logged in at",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    TableHeaderData(
                        name = "actions",
                        width = actionsWidth,
                    ) {
                        Text(
                            text = "Actions",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )

                val rows: List<TableRowData>? = user?.sessions?.map { session ->
                    TableRowData(
                        cells = listOf(
                            TableCellData(
                                width = clientWidth,
                            ) {
                                Text(
                                    text = session.client
                                )
                            },
                            TableCellData(
                                width = deviceWidth,
                            ) {
                                Text(
                                    text = session.device
                                )
                            },
                            TableCellData(
                                width = loggedInAtWidth,
                            ) {
                                Text(
                                    text = formatDate(session.createdAt)
                                )
                            },
                            TableCellData(
                                width = actionsWidth,
                            ) {
                                IconButton(
                                    icon = painterResource(R.drawable.delete),
                                    iconContentDescription = "Delete Session",
                                    color = MaterialTheme.colorScheme.error,
                                    iconColor = MaterialTheme.colorScheme.onError,
                                    onClick = {
                                        viewModel.setDeleteSession(session)
                                    },
                                    enabled = !state.isLoading
                                )
                            }
                        )
                    )
                }

                Table(
                    headers = headers,
                    rows = rows ?: emptyList(),
                    loading = state.isLoading,
                )
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.openDeleteAllSessionsPopup()
                },
                colors = getButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.error,
                    disabledContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                ),
                enabled = !state.isLoading
            ) {
                Text(
                    text = "Log out of all devices"
                )
            }
        }
    }
}