package com.stefdp.zipline.screens.admin.invites

import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.LocalLoggedUser
import com.stefdp.zipline.LocalScreenViewState
import com.stefdp.zipline.LocalUpdateScreenViewState
import com.stefdp.zipline.Logger
import com.stefdp.zipline.R
import com.stefdp.zipline.components.PromptPopup
import com.stefdp.zipline.components.HeaderButton
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.table.Table
import com.stefdp.zipline.components.table.TableCellData
import com.stefdp.zipline.components.table.TableHeaderData
import com.stefdp.zipline.components.table.TableRowData
import com.stefdp.zipline.components.table.TableScrollbarConfig
import com.stefdp.zipline.network.models.UserRole
import com.stefdp.zipline.screens.HomeScreen
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.screens.admin.invites.components.CreateInvitePopup
import com.stefdp.zipline.screens.admin.invites.components.LargeInviteDisplay
import com.stefdp.zipline.components.IconButton
import com.stefdp.zipline.components.QRCodePopup
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.SortOrder
import com.stefdp.zipline.utils.ZiplineViewStateType
import com.stefdp.zipline.utils.shimmerable
import com.stefdp.zipline.utils.verticalLazyScrollbar
import kotlinx.coroutines.launch
import nl.jacobras.humanreadable.HumanReadable
import kotlin.time.Instant

const val VIEW_STATE_KEY = "adminInvitesViewState"

@Composable
fun AdminInvitesScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: AdminInvitesViewModel = viewModel()
) {
    val localLoggedUser = LocalLoggedUser.current

    if (localLoggedUser == null) {
        navController.navigate(LoginScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    localLoggedUser?.role?.level?.let {
        if (it > UserRole.ADMIN.level) {
            navController.navigate(HomeScreen) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    val state by viewModel.state.collectAsState()

    val screenViewState = LocalScreenViewState.current
    val updateScreenViewState = LocalUpdateScreenViewState.current

    val viewState = screenViewState.adminInvites

    val defaultSortOrder = SortOrder.UNSPECIFIED

    LaunchedEffect(Unit) {
        viewModel.initData(context)

        viewModel.refreshInvites(
            context = context,
            viewState = viewState
        )
    }

    val coroutineScope = rememberCoroutineScope()

    PromptPopup(
        showPopup = state.deleteInvite != null,
        onDismissRequest = {
            viewModel.setDeleteInvite(null)
        },
        onCancel = {
            viewModel.setDeleteInvite(null)
        },
        isLoading = state.isLoading,
        title = "Are you sure?",
        description = "Are you sure you want to delete invite ${state.deleteInvite?.code}? This action cannot be undone.",
        onSuccess = {
            viewModel.deleteInvite(
                context = context,
                viewState = viewState,
                onError = { error ->
                    coroutineScope.launch {
                        Notification.show(
                            context = context,
                            activity = activity,
                        ) {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.onError
                            )
                        }
                    }
                },
            )
        }
    )

    CreateInvitePopup(
        context = context,
        activity = activity,
        showPopup = state.createNewInvitePopupOpen,
        onDismissRequest = {
            viewModel.closeCreateNewInvitePopup()
        },
        viewState = viewState,
        state = state,
        viewModel = viewModel
    )

    LaunchedEffect(
        state.qrCodeInvite,
        state.serverUrl
    ) {
        Logger.debug("AdminInvitesScreen", "QR code invite or server URL changed, qrCodeInvite: ${state.qrCodeInvite}, serverUrl: ${state.serverUrl}")
    }

    QRCodePopup(
        context = context,
        activity = activity,
        qrCodeText = state.qrCodeText,
        showPopup = state.qrCodeInvite != null && state.serverUrl != null,
        onDismissRequest = {
            viewModel.setQrCodeInvite(null)
        },
        downloadFileName = "QR_${state.qrCodeInvite?.id ?: "code"}.png"
    )

    Column(
        modifier = Modifier.padding(
            start = 12.dp,
            end = 12.dp,
            top = 12.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Invites",
                maxLines = 1,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderButton(
                    icon = painterResource(R.drawable.add),
                    contentDescription = "Create Invite",
                    onClick = {
                        viewModel.openCreateNewInvitePopup()
                    },
                    enabled = !state.isLoading,
                )

                Spacer(
                    modifier = Modifier.width(5.dp)
                )

                HeaderButton(
                    icon = if (viewState == ZiplineViewStateType.COMPACT)
                        painterResource(R.drawable.view_agenda)
                    else  painterResource(R.drawable.view_module),
                    contentDescription = if (viewState == ZiplineViewStateType.COMPACT) "Switch to detailed view" else "Switch to compact view",
                    onClick = {
                        val newState = if (viewState == ZiplineViewStateType.COMPACT) ZiplineViewStateType.LARGE else ZiplineViewStateType.COMPACT

                        updateScreenViewState(
                            screenViewState.copy(
                                adminInvites = newState
                            )
                        )
                    },
                    enabled = !state.isLoading,
                )
            }
        }

        val clipboardManager = LocalClipboard.current

        val lazyColumnListState = rememberLazyListState()

        LaunchedEffect(
            state.sortOrder,
            state.sortKey,
            viewState,
        ) {
            viewModel.triggerSort(viewState)
        }

        LaunchedEffect(viewState) {
            if (viewState == ZiplineViewStateType.COMPACT) return@LaunchedEffect

            if (state.sortKey != GetInvitesQuerySortBy.CREATED_AT && state.sortOrder != SortOrder.DESC) {
                viewModel.triggerSort(viewState)
            }
        }

        if (viewState == ZiplineViewStateType.COMPACT) {
            Column(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .weight(1f)
                    .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                    )
            ) {
                val tableCodeWidth = 100.dp
                val tableCreatedByWidth = 150.dp
                val tableCreatedWidth = 150.dp
                val tableLastUpdatedWidth = 170.dp
                val tableExpiresWidth = 130.dp
                val tableMaxUsesWidth = 130.dp
                val tableUsesWidth = 130.dp
                val tableActionsWidth = 140.dp

                fun onSortChanged(sortKey: GetInvitesQuerySortBy) {
                    if (state.sortKey != sortKey || state.sortOrder == SortOrder.UNSPECIFIED || state.sortOrder == SortOrder.DESC) {
                        viewModel.updateSort(sortKey, SortOrder.ASC)
                    } else {
                        viewModel.updateSort(sortKey, SortOrder.DESC)
                    }
                }

                val headers: List<TableHeaderData> = listOf(
                    TableHeaderData(
                        width = tableCodeWidth,
                        name = "code",
                        sortable = true,
                        sortOrder = if (state.sortKey == GetInvitesQuerySortBy.CODE) state.sortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetInvitesQuerySortBy.CODE)
                        },
                    ) {
                        Text(
                            text = "Code",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    TableHeaderData(
                        width = tableCreatedByWidth,
                        name = "createdBy",
                        sortable = true,
                        sortOrder = if (state.sortKey == GetInvitesQuerySortBy.CREATED_BY) state.sortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetInvitesQuerySortBy.CREATED_BY)
                        },
                    ) {
                        Text(
                            text = "Created by",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    TableHeaderData(
                        width = tableCreatedWidth,
                        name = "created",
                        sortable = true,
                        sortOrder = if (state.sortKey == GetInvitesQuerySortBy.CREATED_AT) state.sortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetInvitesQuerySortBy.CREATED_AT)
                        },
                    ) {
                        Text(
                            text = "Created",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    TableHeaderData(
                        width = tableLastUpdatedWidth,
                        name = "updatedAt",
                        sortable = true,
                        sortOrder = if (state.sortKey == GetInvitesQuerySortBy.UPDATED_AT) state.sortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetInvitesQuerySortBy.UPDATED_AT)
                        },
                    ) {
                        Text(
                            text = "Last update at",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    TableHeaderData(
                        width = tableExpiresWidth,
                        name = "expires",
                        sortable = true,
                        sortOrder = if (state.sortKey == GetInvitesQuerySortBy.EXPIRES_AT) state.sortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetInvitesQuerySortBy.EXPIRES_AT)
                        },
                    ) {
                        Text(
                            text = "Expires",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    TableHeaderData(
                        width = tableMaxUsesWidth,
                        name = "maxUses",
                        sortable = true,
                        sortOrder = if (state.sortKey == GetInvitesQuerySortBy.MAX_USES) state.sortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetInvitesQuerySortBy.MAX_USES)
                        },
                    ) {
                        Text(
                            text = "Max uses",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    TableHeaderData(
                        width = tableUsesWidth,
                        name = "uses",
                        sortable = true,
                        sortOrder = if (state.sortKey == GetInvitesQuerySortBy.USES) state.sortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetInvitesQuerySortBy.USES)
                        },
                    ) {
                        Text(
                            text = "Uses",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    TableHeaderData(
                        width = tableActionsWidth,
                        name = "actions"
                    ) {
                        Text(
                            text = "Actions",
                            fontWeight = FontWeight.Bold
                        )
                    },
                )

                val rows: List<TableRowData> = state.invites?.map { invite ->
                    TableRowData(
                        clickable = true,
                        cells = listOf(
                            TableCellData(
                                width = tableCodeWidth,
                            ) {
                                Text(
                                    text = invite.code,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    textDecoration = TextDecoration.Underline,
                                    modifier = Modifier.clickable(
                                        enabled = state.serverUrl != null,
                                        onClick = {
                                            val url = "${state.serverUrl}/invite/${invite.code}"

                                            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                            context.startActivity(intent)
                                        }
                                    )
                                )
                            },
                            TableCellData(
                                width = tableCreatedByWidth,
                            ) {
                                Text(
                                    text = invite.inviter.username,
                                )
                            },
                            TableCellData(
                                width = tableCreatedWidth,
                            ) {
                                Text(
                                    text = HumanReadable.timeAgo(Instant.parse(invite.createdAt)),
                                )
                            },
                            TableCellData(
                                width = tableLastUpdatedWidth,
                            ) {
                                Text(
                                    text = HumanReadable.timeAgo(Instant.parse(invite.updatedAt)),
                                )
                            },
                            TableCellData(
                                width = tableExpiresWidth,
                            ) {
                                Text(
                                    text = invite.expiresAt?.let {
                                        HumanReadable.timeAgo(Instant.parse(it))
                                    } ?: "Never",
                                )
                            },
                            TableCellData(
                                width = tableMaxUsesWidth,
                            ) {
                                Text(
                                    text = (invite.maxUses ?: "Unlimited").toString(),
                                )
                            },
                            TableCellData(
                                width = tableUsesWidth,
                            ) {
                                Text(
                                    text = invite.uses.toString(),
                                )
                            },
                            TableCellData(
                                width = tableActionsWidth,
                            ) {
                                @Composable
                                fun ActionButtonSpacer() {
                                    Spacer(modifier = Modifier.width(8.dp))
                                }

                                IconButton(
                                    icon = painterResource(R.drawable.content_copy),
                                    iconContentDescription = "Copy invite URL",
                                    color = MaterialTheme.colorScheme.primary,
                                    iconColor = MaterialTheme.colorScheme.onPrimary,
                                    onClick = {
                                        coroutineScope.launch {
                                            val url = "${state.serverUrl}/invite/${invite.code}"

                                            val clipData = ClipData.newPlainText("Invite URL", url).toClipEntry()

                                            clipboardManager.setClipEntry(clipData)

                                            Notification.show(
                                                context = context,
                                                activity = activity,
                                            ) {
                                                Text(
                                                    text = "Invite link copied to clipboard"
                                                )
                                            }
                                        }
                                    },
                                    enabled = !state.isLoading
                                )

                                ActionButtonSpacer()

                                IconButton(
                                    icon = painterResource(R.drawable.qr_code),
                                    iconContentDescription = "Show QR code",
                                    color = MaterialTheme.colorScheme.primary,
                                    iconColor = MaterialTheme.colorScheme.onPrimary,
                                    onClick = {
                                        viewModel.setQrCodeInvite(invite)
                                    },
                                    enabled = !state.isLoading
                                )

                                ActionButtonSpacer()

                                IconButton(
                                    icon = painterResource(R.drawable.delete),
                                    iconContentDescription = "Delete invite",
                                    color = MaterialTheme.colorScheme.error,
                                    iconColor = MaterialTheme.colorScheme.onError,
                                    onClick = {
                                        viewModel.setDeleteInvite(invite)
                                    },
                                    enabled = !state.isLoading
                                )
                            }
                        )
                    )
                } ?: emptyList()

                Table(
                    modifier = Modifier.fillMaxSize(),
                    headers = headers,
                    rows = rows,
                    loading = state.isLoading,
                    scrollbarConfig = TableScrollbarConfig(
                        vertical = ScrollbarConfig(
                            alwaysKeepScrollbar = true
                        ),
                        horizontal = ScrollbarConfig(
                            alwaysKeepScrollbar = true
                        )
                    )
                )
            }
        } else {
            LazyColumn(
                state = lazyColumnListState,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
                    .verticalLazyScrollbar(
                        listState = lazyColumnListState,
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (state.invites == null || state.isLoading) {
                    items(5) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                                .fillMaxWidth()
                                .height(200.dp)
                                .shimmerable(
                                    enabled = true,
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    keepBackground = true
                                ),
                            contentAlignment = Alignment.Center
                        ) {}
                    }
                } else {
                    if (state.invites!!.size > 0L) {
                        items(state.invites!!.size) { index ->
                            val invite = state.invites!![index]

                            LargeInviteDisplay(
                                context = context,
                                activity = activity,
                                invite = invite,
                                serverUrl = state.serverUrl,
                                onDelete = {
                                    viewModel.setDeleteInvite(invite)
                                },
                                onShowQRCode = {
                                    viewModel.setQrCodeInvite(invite)
                                }
                            )
                        }
                    } else {
                        item {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.link_2),
                                        contentDescription = "Invite",
                                        modifier = Modifier.size(32.dp)
                                    )

                                    Text(
                                        text = "No invites found",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                Text(
                                    text = "Create an invite to see them here.",
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}