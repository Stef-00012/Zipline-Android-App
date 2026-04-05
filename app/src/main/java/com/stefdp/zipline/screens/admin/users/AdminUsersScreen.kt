package com.stefdp.zipline.screens.admin.users

import android.content.Context
import androidx.compose.foundation.border
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.google.gson.annotations.SerializedName
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.LocalLoggedUser
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Avatar
import com.stefdp.zipline.components.DeletePromptButtonLayout
import com.stefdp.zipline.components.PromptPopup
import com.stefdp.zipline.components.HeaderButton
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.table.Table
import com.stefdp.zipline.components.table.TableCellData
import com.stefdp.zipline.components.table.TableHeaderData
import com.stefdp.zipline.components.table.TableRowData
import com.stefdp.zipline.components.table.TableScrollbarConfig
import com.stefdp.zipline.network.models.User
import com.stefdp.zipline.network.models.UserRole
import com.stefdp.zipline.network.requests.deleteUser
import com.stefdp.zipline.network.requests.getUsers
import com.stefdp.zipline.screens.FilesScreen
import com.stefdp.zipline.screens.HomeScreen
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.screens.admin.users.components.CreateUserPopup
import com.stefdp.zipline.screens.admin.users.components.EditUserPopup
import com.stefdp.zipline.screens.admin.users.components.LargeUserDisplay
import com.stefdp.zipline.components.IconButton
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.SortOrder
import com.stefdp.zipline.utils.canInteract
import com.stefdp.zipline.utils.shimmerable
import com.stefdp.zipline.utils.verticalLazyScrollbar
import kotlinx.coroutines.launch
import nl.jacobras.humanreadable.HumanReadable
import kotlin.time.Instant

@Composable
fun AdminUsersScreen(
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

    var users by remember { mutableStateOf<List<User>?>(null) }

    var isLoading by remember { mutableStateOf(true) }

    var compactView by remember { mutableStateOf(false) }

    var sortKey by remember { mutableStateOf(GetUsersQuerySortBy.CREATED_AT) }
    var sortOrder by remember { mutableStateOf(SortOrder.DESC) }
    val defaultSortOrder = SortOrder.UNSPECIFIED

    var createdNewUserPopupOpen by remember { mutableStateOf(false) }

    var deleteUser by remember { mutableStateOf<User?>(null) }
    var deleteUserLevel by remember { mutableIntStateOf(0) }
    var editUser by remember { mutableStateOf<User?>(null) }

    fun sortUsers(users: List<User>): List<User> {
        if (!compactView) return users.sortedBy { Instant.parse(it.createdAt) }.reversed()

        val ascending = when (sortKey) {
            GetUsersQuerySortBy.USERNAME ->
                users.sortedBy { it.username }

            GetUsersQuerySortBy.ROLE ->
                users.sortedBy { it.role.level }

            GetUsersQuerySortBy.CREATED_AT ->
                users.sortedBy { Instant.parse(it.createdAt) }

            GetUsersQuerySortBy.UPDATED_AT ->
                users.sortedBy { Instant.parse(it.updatedAt) }
        }

        return when (sortOrder) {
            SortOrder.ASC -> ascending
            SortOrder.DESC -> ascending.reversed()
            SortOrder.UNSPECIFIED -> users
        }
    }

    suspend fun updateUsers(sort: Boolean = true) {
        isLoading = true

        val usersRes = getUsers(
            context = context,
            excludeSelf = true
        )

        usersRes.onSuccess {
            users = if (sort) sortUsers(it) else it
        }

        isLoading = false
    }

    LaunchedEffect(Unit) {
        updateUsers()
    }

    val coroutineScope = rememberCoroutineScope()

    if (deleteUserLevel == 0) {
        PromptPopup(
            showPopup = deleteUser != null,
            onDismissRequest = { deleteUser = null },
            onCancel = { deleteUser = null },
            isLoading = isLoading,
            title = "Delete ${deleteUser?.username}?",
            description = "Are you sure you want to delete ${deleteUser?.username}? This action cannot be undone.",
            onSuccess = {
                coroutineScope.launch {
                    if (deleteUser == null) return@launch

                    deleteUserLevel = 1
                }
            }
        )
    } else {
        PromptPopup(
            showPopup = deleteUser != null,
            isLoading = isLoading,
            title = "Delete ${deleteUser?.username}'s data?",
            description = "Would you like to delete ${deleteUser?.username}'s files and urls? This action cannot be undone.",
            buttonLayout = DeletePromptButtonLayout.VERTICAL,
            onDismissRequest = {
                deleteUserLevel = 0
                deleteUser = null
            },
            onSuccess = {
                coroutineScope.launch {
                    if (deleteUser == null) return@launch

                    isLoading = true

                    val deleteRes = deleteUser(
                        context = context,
                        userId = deleteUser!!.id,
                        deleteUserFilesAndUrls = true
                    )

                    deleteRes
                        .onSuccess {
                            if (compactView) {
                                updateUsers()
                            } else {
                                updateUsers(
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
                                        text = "Failed to delete user: ${it.message}"
                                    )
                                },
                            )
                        }

                    isLoading = false
                    deleteUser = null
                    deleteUserLevel = 0
                }
            },
            onCancel = {
                coroutineScope.launch {
                    if (deleteUser == null) return@launch

                    isLoading = true

                    val deleteRes = deleteUser(
                        context = context,
                        userId = deleteUser!!.id,
                    )

                    deleteRes
                        .onSuccess {
                            if (compactView) {
                                updateUsers()
                            } else {
                                updateUsers(
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
                                        text = "Failed to delete user: ${it.message}"
                                    )
                                }
                            )
                        }

                    isLoading = false
                    deleteUser = null
                    deleteUserLevel = 0
                }
            },
            cancelText = "No, keep everything & only delete user",
            successText = "Yes, delete everything"
        )
    }

    CreateUserPopup(
        context = context,
        activity = activity,
        currentUser = localLoggedUser,
        showPopup = createdNewUserPopupOpen,
        onDismissRequest = { createdNewUserPopupOpen = false },
        updateUsers = ::updateUsers
    )

    EditUserPopup(
        context = context,
        activity = activity,
        currentUser = localLoggedUser,
        user = editUser,
        showPopup = editUser != null,
        onDismissRequest = { editUser = null },
        updateUsers = ::updateUsers
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
                text = "Users",
                maxLines = 1,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderButton(
                    icon = painterResource(R.drawable.person_add),
                    contentDescription = "Create User",
                    onClick = {
                        createdNewUserPopupOpen = true
                    },
                    enabled = !isLoading,
                )

                Spacer(
                    modifier = Modifier.width(5.dp)
                )

                HeaderButton(
                    icon = if (compactView)
                        painterResource(R.drawable.view_agenda)
                    else  painterResource(R.drawable.view_module),
                    contentDescription = if (compactView) "Switch to detailed view" else "Switch to compact view",
                    onClick = {
                        compactView = !compactView
                    },
                    enabled = !isLoading,
                )
            }
        }

        val lazyColumnListState = rememberLazyListState()

        LaunchedEffect(
            sortOrder,
            sortKey,
            compactView,
        ) {
            users = users?.let { sortUsers(it) }
        }

        LaunchedEffect(compactView) {
            if (compactView) return@LaunchedEffect

            if (sortKey != GetUsersQuerySortBy.CREATED_AT && sortOrder != SortOrder.DESC) {
                updateUsers(
                    sort = false
                )
            }
        }

        if (compactView) {
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
                val tableAvatarWidth = 75.dp
                val tableUsernameWidth = 150.dp
                val tableRoleWidth = 130.dp
                val tableCreatedWidth = 150.dp
                val tableLastUpdatedWidth = 170.dp
                val tableActionsWidth = 140.dp

                val headers: List<TableHeaderData> = listOf(
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Avatar",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableAvatarWidth,
                        name = "avatar",
                    ),
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Username",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableUsernameWidth,
                        name = "username",
                        sortable = true,
                        sortOrder = if (sortKey == GetUsersQuerySortBy.USERNAME) sortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (sortKey != GetUsersQuerySortBy.USERNAME) {
                                sortKey = GetUsersQuerySortBy.USERNAME
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
                                text = "Role",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableRoleWidth,
                        name = "role",
                        sortable = true,
                        sortOrder = if (sortKey == GetUsersQuerySortBy.ROLE) sortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (sortKey != GetUsersQuerySortBy.ROLE) {
                                sortKey = GetUsersQuerySortBy.ROLE
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
                        sortOrder = if (sortKey == GetUsersQuerySortBy.CREATED_AT) sortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (sortKey != GetUsersQuerySortBy.CREATED_AT) {
                                sortKey = GetUsersQuerySortBy.CREATED_AT
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
                                text = "Last Updated",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableLastUpdatedWidth,
                        name = "created",
                        sortable = true,
                        sortOrder = if (sortKey == GetUsersQuerySortBy.UPDATED_AT) sortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (sortKey != GetUsersQuerySortBy.UPDATED_AT) {
                                sortKey = GetUsersQuerySortBy.UPDATED_AT
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

                val rows: List<TableRowData> = users?.map { user ->
                    val canInteractWithUser = canInteract(localLoggedUser?.role, user.role)

                    TableRowData(
                        clickable = true,
                        cells = listOf(
                            TableCellData(
                                content = {
                                    Avatar(
                                        avatar = user.avatar,
                                        isAdmin = user.role.level <= UserRole.ADMIN.level,
                                    )
                                },
                                width = tableAvatarWidth,
                            ),
                            TableCellData(
                                content = {
                                    Text(
                                        text = user.username,
                                    )
                                },
                                width = tableUsernameWidth,
                            ),
                            TableCellData(
                                content = {
                                    Text(
                                        text = user.role.roleName,
                                    )
                                },
                                width = tableRoleWidth,
                            ),
                            TableCellData(
                                content = {
                                    Text(
                                        text = HumanReadable.timeAgo(Instant.parse(user.createdAt)),
                                    )
                                },
                                width = tableCreatedWidth,
                            ),
                            TableCellData(
                                content = {
                                    Text(
                                        text = HumanReadable.timeAgo(Instant.parse(user.updatedAt)),
                                    )
                                },
                                width = tableLastUpdatedWidth,
                            ),
                            TableCellData(
                                content = {
                                    @Composable
                                    fun ActionButtonSpacer() {
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }

                                    IconButton(
                                        icon = painterResource(R.drawable.folder_open),
                                        iconContentDescription = "View user's files",
                                        color = MaterialTheme.colorScheme.primary,
                                        iconColor = MaterialTheme.colorScheme.onPrimary,
                                        onClick = {
                                            navController.navigate(FilesScreen(user.id))
                                        },
                                        enabled = !isLoading && canInteractWithUser
                                    )

                                    ActionButtonSpacer()

                                    IconButton(
                                        icon = painterResource(R.drawable.edit),
                                        iconContentDescription = "Edit User",
                                        color = MaterialTheme.colorScheme.primary,
                                        iconColor = MaterialTheme.colorScheme.onPrimary,
                                        onClick = { editUser = user },
                                        enabled = !isLoading && canInteractWithUser
                                    )

                                    ActionButtonSpacer()

                                    IconButton(
                                        icon = painterResource(R.drawable.delete),
                                        iconContentDescription = "Delete URL",
                                        color = MaterialTheme.colorScheme.error,
                                        iconColor = MaterialTheme.colorScheme.onError,
                                        onClick = {
                                            deleteUser = user
                                        },
                                        enabled = !isLoading && canInteractWithUser
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
                if (users == null || isLoading) {
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
                    if (users!!.size > 0L) {
                        items(users!!.size) { index ->
                            val user = users!![index]
                            val canInteractWithUser = canInteract(localLoggedUser?.role, user.role)

                            LargeUserDisplay(
                                user = user,
                                canInteractWithUser = canInteractWithUser,
                                onDelete = { deleteUser = user },
                                onEdit = { editUser = user },
                                onOpenFiles = {
                                    navController.navigate(FilesScreen(user.id))
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
                                        painter = painterResource(R.drawable.file_copy_off),
                                        contentDescription = "User",
                                        modifier = Modifier.size(32.dp)
                                    )

                                    Text(
                                        text = "No users found",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                Text(
                                    text = "Create a user to see them here.",
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

private enum class GetUsersQuerySortBy(val value: String) {
    @SerializedName("username")
    USERNAME("username"),

    @SerializedName("role")
    ROLE("role"),

    @SerializedName("createdAt")
    CREATED_AT("createdAt"),

    @SerializedName("updatedAt")
    UPDATED_AT("updatedAt");

    override fun toString(): String = value
}