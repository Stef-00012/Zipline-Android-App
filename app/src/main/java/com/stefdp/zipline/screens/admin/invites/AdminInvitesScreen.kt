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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavHostController
import com.google.gson.annotations.SerializedName
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.LocalLoggedUser
import com.stefdp.zipline.LocalScreenViewState
import com.stefdp.zipline.LocalUpdateScreenViewState
import com.stefdp.zipline.R
import com.stefdp.zipline.components.PromptPopup
import com.stefdp.zipline.components.HeaderButton
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.table.Table
import com.stefdp.zipline.components.table.TableCellData
import com.stefdp.zipline.components.table.TableHeaderData
import com.stefdp.zipline.components.table.TableRowData
import com.stefdp.zipline.components.table.TableScrollbarConfig
import com.stefdp.zipline.network.models.Invite
import com.stefdp.zipline.network.models.UserRole
import com.stefdp.zipline.network.requests.deleteInvite
import com.stefdp.zipline.network.requests.getInvites
import com.stefdp.zipline.screens.HomeScreen
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.screens.admin.invites.components.CreateInvitePopup
import com.stefdp.zipline.screens.admin.invites.components.LargeInviteDisplay
import com.stefdp.zipline.components.IconButton
import com.stefdp.zipline.components.QRCodePopup
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.SecureStorage
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

    var invites by remember { mutableStateOf<List<Invite>?>(null) }

    var serverUrl by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(true) }

    val screenViewState = LocalScreenViewState.current
    val updateScreenViewState = LocalUpdateScreenViewState.current

    val viewState = screenViewState.adminInvites

    var sortKey by remember { mutableStateOf(GetInvitesQuerySortBy.CREATED_AT) }
    var sortOrder by remember { mutableStateOf(SortOrder.DESC) }
    val defaultSortOrder = SortOrder.UNSPECIFIED

    var createdNewInvitePopupOpen by remember { mutableStateOf(false) }

    var deleteInvite by remember { mutableStateOf<Invite?>(null) }

    var qrCodeInvite by remember { mutableStateOf<Invite?>(null) }
    var qrCodeText by remember { mutableStateOf("") }

    LaunchedEffect(qrCodeInvite) {
        if (qrCodeInvite == null) {
            qrCodeText = ""
            return@LaunchedEffect
        }

        qrCodeText = "${serverUrl}/invite/${qrCodeInvite!!.code}"
    }

    fun sortInvites(invites: List<Invite>): List<Invite> {
        if (viewState == ZiplineViewStateType.LARGE) return invites.sortedBy { Instant.parse(it.createdAt) }.reversed()

        val ascending = when (sortKey) {
            GetInvitesQuerySortBy.CODE ->
                invites.sortedBy { it.code }

            GetInvitesQuerySortBy.CREATED_BY ->
                invites.sortedBy { it.inviter.username }

            GetInvitesQuerySortBy.CREATED_AT ->
                invites.sortedBy { Instant.parse(it.createdAt) }

            GetInvitesQuerySortBy.UPDATED_AT ->
                invites.sortedBy { Instant.parse(it.updatedAt) }

            GetInvitesQuerySortBy.EXPIRES_AT ->
                invites.sortedBy { it.expiresAt?.let { expiresAt -> Instant.parse(expiresAt) } }

            GetInvitesQuerySortBy.MAX_USES ->
                invites.sortedBy { it.maxUses }

            GetInvitesQuerySortBy.USES ->
                invites.sortedBy { it.uses }
        }

        return when (sortOrder) {
            SortOrder.ASC -> ascending
            SortOrder.DESC -> ascending.reversed()
            SortOrder.UNSPECIFIED -> invites
        }
    }

    suspend fun updateInvites(sort: Boolean = true) {
        isLoading = true

        val usersRes = getInvites(
            context = context,
        )

        usersRes.onSuccess {
            invites = if (sort) sortInvites(it) else it
        }

        isLoading = false
    }

    LaunchedEffect(Unit) {
        val secureStore = SecureStorage.getInstance(context)

        serverUrl = secureStore.get("serverUrl")
    }

    LaunchedEffect(Unit) {
        updateInvites()
    }

    val coroutineScope = rememberCoroutineScope()

    PromptPopup(
        showPopup = deleteInvite != null,
        onDismissRequest = { deleteInvite = null },
        onCancel = { deleteInvite = null },
        isLoading = isLoading,
        title = "Are you sure?",
        description = "Are you sure you want to delete invite ${deleteInvite?.code}? This action cannot be undone.",
        onSuccess = {
            coroutineScope.launch {
                if (deleteInvite == null) return@launch

                coroutineScope.launch {
                    if (deleteInvite == null) return@launch

                    isLoading = true

                    val deleteRes = deleteInvite(
                        context = context,
                        codeOrId = deleteInvite!!.id
                    )

                    deleteRes
                        .onSuccess {
                            if (viewState == ZiplineViewStateType.COMPACT) {
                                updateInvites()
                            } else {
                                updateInvites(
                                    sort = false
                                )
                            }
                        }
                        .onFailure {
                            Notification.show(
                                context = context,
                                activity = activity,
                                content = {
                                    Text(
                                        text = "Failed to delete invite: ${it.message}"
                                    )
                                },
                            )
                        }

                    isLoading = false
                    deleteInvite = null
                }
            }
        }
    )

    CreateInvitePopup(
        context = context,
        activity = activity,
        showPopup = createdNewInvitePopupOpen,
        onDismissRequest = { createdNewInvitePopupOpen = false },
        updateInvites = ::updateInvites
    )

    QRCodePopup(
        context = context,
        activity = activity,
        qrCodeText = qrCodeText,
        showPopup = qrCodeInvite != null && serverUrl != null,
        onDismissRequest = { qrCodeInvite = null },
        downloadFileName = "QR_${qrCodeInvite?.id ?: "code"}.png"
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
                        createdNewInvitePopupOpen = true
                    },
                    enabled = !isLoading,
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
                    enabled = !isLoading,
                )
            }
        }

        val clipboardManager = LocalClipboard.current

        val lazyColumnListState = rememberLazyListState()

        LaunchedEffect(
            sortOrder,
            sortKey,
            viewState,
        ) {
            invites = invites?.let { sortInvites(it) }
        }

        LaunchedEffect(viewState) {
            if (viewState == ZiplineViewStateType.COMPACT) return@LaunchedEffect

            if (sortKey != GetInvitesQuerySortBy.CREATED_AT && sortOrder != SortOrder.DESC) {
                updateInvites(
                    sort = false
                )
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

                val headers: List<TableHeaderData> = listOf(
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Code",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableCodeWidth,
                        name = "code",
                        sortable = true,
                        sortOrder = if (sortKey == GetInvitesQuerySortBy.CODE) sortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (sortKey != GetInvitesQuerySortBy.CODE) {
                                sortKey = GetInvitesQuerySortBy.CODE
                                sortOrder = SortOrder.ASC
                            } else if (sortOrder == SortOrder.UNSPECIFIED || sortOrder == SortOrder.DESC) {
                                sortOrder = SortOrder.ASC
                            } else {
                                sortOrder = SortOrder.DESC
                            }
                        },
                    ),
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Created by",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableCreatedByWidth,
                        name = "createdBy",
                        sortable = true,
                        sortOrder = if (sortKey == GetInvitesQuerySortBy.CREATED_BY) sortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (sortKey != GetInvitesQuerySortBy.CREATED_BY) {
                                sortKey = GetInvitesQuerySortBy.CREATED_BY
                                sortOrder = SortOrder.ASC
                            } else if (sortOrder == SortOrder.UNSPECIFIED || sortOrder == SortOrder.DESC) {
                                sortOrder = SortOrder.ASC
                            } else {
                                sortOrder = SortOrder.DESC
                            }
                        },
                    ),
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Created",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableCreatedWidth,
                        name = "created",
                        sortable = true,
                        sortOrder = if (sortKey == GetInvitesQuerySortBy.CREATED_AT) sortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (sortKey != GetInvitesQuerySortBy.CREATED_AT) {
                                sortKey = GetInvitesQuerySortBy.CREATED_AT
                                sortOrder = SortOrder.ASC
                            } else if (sortOrder == SortOrder.UNSPECIFIED || sortOrder == SortOrder.DESC) {
                                sortOrder = SortOrder.ASC
                            } else {
                                sortOrder = SortOrder.DESC
                            }
                        },
                    ),
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Last update at",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableLastUpdatedWidth,
                        name = "updatedAt",
                        sortable = true,
                        sortOrder = if (sortKey == GetInvitesQuerySortBy.UPDATED_AT) sortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (sortKey != GetInvitesQuerySortBy.UPDATED_AT) {
                                sortKey = GetInvitesQuerySortBy.UPDATED_AT
                                sortOrder = SortOrder.ASC
                            } else if (sortOrder == SortOrder.UNSPECIFIED || sortOrder == SortOrder.DESC) {
                                sortOrder = SortOrder.ASC
                            } else {
                                sortOrder = SortOrder.DESC
                            }
                        },
                    ),
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Expires",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableExpiresWidth,
                        name = "expires",
                        sortable = true,
                        sortOrder = if (sortKey == GetInvitesQuerySortBy.EXPIRES_AT) sortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (sortKey != GetInvitesQuerySortBy.EXPIRES_AT) {
                                sortKey = GetInvitesQuerySortBy.EXPIRES_AT
                                sortOrder = SortOrder.ASC
                            } else if (sortOrder == SortOrder.UNSPECIFIED || sortOrder == SortOrder.DESC) {
                                sortOrder = SortOrder.ASC
                            } else {
                                sortOrder = SortOrder.DESC
                            }
                        },
                    ),
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Max uses",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableMaxUsesWidth,
                        name = "maxUses",
                        sortable = true,
                        sortOrder = if (sortKey == GetInvitesQuerySortBy.MAX_USES) sortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (sortKey != GetInvitesQuerySortBy.MAX_USES) {
                                sortKey = GetInvitesQuerySortBy.MAX_USES
                                sortOrder = SortOrder.ASC
                            } else if (sortOrder == SortOrder.UNSPECIFIED || sortOrder == SortOrder.DESC) {
                                sortOrder = SortOrder.ASC
                            } else {
                                sortOrder = SortOrder.DESC
                            }
                        },
                    ),
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Uses",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableUsesWidth,
                        name = "uses",
                        sortable = true,
                        sortOrder = if (sortKey == GetInvitesQuerySortBy.USES) sortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (sortKey != GetInvitesQuerySortBy.USES) {
                                sortKey = GetInvitesQuerySortBy.USES
                                sortOrder = SortOrder.ASC
                            } else if (sortOrder == SortOrder.UNSPECIFIED || sortOrder == SortOrder.DESC) {
                                sortOrder = SortOrder.ASC
                            } else {
                                sortOrder = SortOrder.DESC
                            }
                        },
                    ),
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Actions",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableActionsWidth,
                        name = "actions"
                    ),
                )

                val rows: List<TableRowData> = invites?.map { invite ->
                    TableRowData(
                        clickable = true,
                        cells = listOf(
                            TableCellData(
                                content = {
                                    Text(
                                        text = invite.code,
                                        color = MaterialTheme.colorScheme.tertiary,
                                        textDecoration = TextDecoration.Underline,
                                        modifier = Modifier.clickable(
                                            enabled = serverUrl != null,
                                            onClick = {
                                                val url = "$serverUrl/invite/${invite.code}"

                                                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                                context.startActivity(intent)
                                            }
                                        )
                                    )
                                },
                                width = tableCodeWidth,
                            ),
                            TableCellData(
                                content = {
                                    Text(
                                        text = invite.inviter.username,
                                    )
                                },
                                width = tableCreatedByWidth,
                            ),
                            TableCellData(
                                content = {
                                    Text(
                                        text = HumanReadable.timeAgo(Instant.parse(invite.createdAt)),
                                    )
                                },
                                width = tableCreatedWidth,
                            ),
                            TableCellData(
                                content = {
                                    Text(
                                        text = HumanReadable.timeAgo(Instant.parse(invite.updatedAt)),
                                    )
                                },
                                width = tableLastUpdatedWidth,
                            ),
                            TableCellData(
                                content = {
                                    Text(
                                        text = invite.expiresAt?.let {
                                            HumanReadable.timeAgo(Instant.parse(it))
                                        } ?: "Never",
                                    )
                                },
                                width = tableExpiresWidth,
                            ),
                            TableCellData(
                                content = {
                                    Text(
                                        text = (invite.maxUses ?: "Unlimited").toString(),
                                    )
                                },
                                width = tableMaxUsesWidth,
                            ),
                            TableCellData(
                                content = {
                                    Text(
                                        text = invite.uses.toString(),
                                    )
                                },
                                width = tableUsesWidth,
                            ),
                            TableCellData(
                                content = {
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
                                                val url = "$serverUrl/invite/${invite.code}"

                                                val clipData = ClipData.newRawUri("Invite URL", url.toUri()).toClipEntry()

                                                clipboardManager.setClipEntry(clipData)

                                                Notification.show(
                                                    context = context,
                                                    activity = activity,
                                                    content = {
                                                        Text(
                                                            text = "Invite link copied to clipboard"
                                                        )
                                                    },
                                                )
                                            }
                                        },
                                        enabled = !isLoading
                                    )

                                    ActionButtonSpacer()

                                    IconButton(
                                        icon = painterResource(R.drawable.qr_code),
                                        iconContentDescription = "Show QR code",
                                        color = MaterialTheme.colorScheme.primary,
                                        iconColor = MaterialTheme.colorScheme.onPrimary,
                                        onClick = { qrCodeInvite = invite },
                                        enabled = !isLoading
                                    )

                                    ActionButtonSpacer()

                                    IconButton(
                                        icon = painterResource(R.drawable.delete),
                                        iconContentDescription = "Delete invite",
                                        color = MaterialTheme.colorScheme.error,
                                        iconColor = MaterialTheme.colorScheme.onError,
                                        onClick = {
                                            deleteInvite = invite
                                        },
                                        enabled = !isLoading
                                    )
                                },
                                width = tableActionsWidth,
                            )
                        )
                    )
                } ?: emptyList()

                Table(
                    modifier = Modifier.fillMaxSize(),
                    headers = headers,
                    rows = rows,
                    loading = isLoading,
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
                if (invites == null || isLoading) {
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
                    if (invites!!.size > 0L) {
                        items(invites!!.size) { index ->
                            val invite = invites!![index]

                            LargeInviteDisplay(
                                context = context,
                                activity = activity,
                                invite = invite,
                                serverUrl = serverUrl,
                                onDelete = { deleteInvite = invite },
                                onShowQRCode = { qrCodeInvite = invite }
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

private enum class GetInvitesQuerySortBy(val value: String) {
    @SerializedName("code")
    CODE("code"),

    @SerializedName("createdBy")
    CREATED_BY("createdBy"),

    @SerializedName("createdAt")
    CREATED_AT("createdAt"),

    @SerializedName("updatedAt")
    UPDATED_AT("updatedAt"),

    @SerializedName("expiresAt")
    EXPIRES_AT("expiresAt"),

    @SerializedName("maxUses")
    MAX_USES("maxUses"),

    @SerializedName("uses")
    USES("uses");

    override fun toString(): String = value
}