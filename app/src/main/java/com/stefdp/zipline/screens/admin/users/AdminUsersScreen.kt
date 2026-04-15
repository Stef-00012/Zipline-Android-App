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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.LocalLoggedUser
import com.stefdp.zipline.LocalScreenViewState
import com.stefdp.zipline.LocalUpdateScreenViewState
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
import com.stefdp.zipline.network.models.UserRole
import com.stefdp.zipline.screens.FilesScreen
import com.stefdp.zipline.screens.HomeScreen
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.screens.admin.users.components.CreateUserPopup
import com.stefdp.zipline.screens.admin.users.components.EditUserPopup
import com.stefdp.zipline.screens.admin.users.components.LargeUserDisplay
import com.stefdp.zipline.components.IconButton
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.SortOrder
import com.stefdp.zipline.utils.ZiplineViewStateType
import com.stefdp.zipline.utils.canInteract
import com.stefdp.zipline.utils.shimmerable
import com.stefdp.zipline.utils.verticalLazyScrollbar
import nl.jacobras.humanreadable.HumanReadable
import kotlin.time.Instant

const val VIEW_STATE_KEY = "adminUsersViewState"

@Composable
fun AdminUsersScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: AdminUsersViewModel = viewModel()
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

    val viewState = screenViewState.adminUsers

    val defaultSortOrder = SortOrder.UNSPECIFIED

    LaunchedEffect(Unit) {
        viewModel.refreshUsers(
            context = context,
            viewState = viewState
        )
    }

    if (state.deleteUserLevel == DeleteUserLevel.CONFIRMATION_PROMPT) {
        PromptPopup(
            showPopup = state.deleteUser != null,
            onDismissRequest = {
                viewModel.setDeleteUser(null)
            },
            onCancel = {
                viewModel.setDeleteUser(null)
            },
            isLoading = state.isLoading,
            title = "Delete ${state.deleteUser?.username}?",
            description = "Are you sure you want to delete ${state.deleteUser?.username}? This action cannot be undone.",
            onSuccess = {
                if (state.deleteUser == null) return@PromptPopup

                viewModel.setDeleteUserLevel(DeleteUserLevel.FILE_DELETION_PROMPT)
            }
        )
    } else {
        PromptPopup(
            showPopup = state.deleteUser != null,
            isLoading = state.isLoading,
            title = "Delete ${state.deleteUser?.username}'s data?",
            description = "Would you like to delete ${state.deleteUser?.username}'s files and urls? This action cannot be undone.",
            buttonLayout = DeletePromptButtonLayout.VERTICAL,
            onDismissRequest = {
                viewModel.setDeleteUserLevel(DeleteUserLevel.CONFIRMATION_PROMPT)
                viewModel.setDeleteUser(null)
            },
            onSuccess = {
                viewModel.deleteUser(
                    context = context,
                    viewState = viewState,
                    deleteUserFilesAndUrls = true,
                    onSuccess = {
                        Notification.show(
                            context = context,
                            activity = activity,
                        ) {
                            Text(
                                text = "User deleted successfully"
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
            onCancel = {
                viewModel.deleteUser(
                    context = context,
                    viewState = viewState,
                    deleteUserFilesAndUrls = false,
                    onSuccess = {
                        Notification.show(
                            context = context,
                            activity = activity,
                        ) {
                            Text(
                                text = "User deleted successfully"
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
            cancelText = "No, keep everything & only delete user",
            successText = "Yes, delete everything"
        )
    }

    CreateUserPopup(
        context = context,
        activity = activity,
        currentUser = localLoggedUser,
        showPopup = state.createNewUserPopupOpen,
        onDismissRequest = {
            viewModel.closeCreateNewUserPopup()
        },
        viewModel = viewModel,
        viewState = viewState,
        state = state,
    )

    EditUserPopup(
        context = context,
        activity = activity,
        currentUser = localLoggedUser,
        showPopup = state.editUser != null,
        onDismissRequest = {
            viewModel.setEditUser(null)
        },
        viewModel = viewModel,
        viewState = viewState,
        state = state,
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
                        viewModel.openCreateNewUserPopup()
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
                                adminUsers = newState
                            )
                        )
                    },
                    enabled = !state.isLoading,
                )
            }
        }

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

            if (state.sortKey != GetUsersQuerySortBy.CREATED_AT && state.sortOrder != SortOrder.DESC) {
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
                val tableAvatarWidth = 75.dp
                val tableUsernameWidth = 150.dp
                val tableRoleWidth = 130.dp
                val tableCreatedWidth = 150.dp
                val tableLastUpdatedWidth = 170.dp
                val tableActionsWidth = 140.dp

                fun onSortChanged(sortKey: GetUsersQuerySortBy) {
                    if (state.sortKey != sortKey || state.sortOrder == SortOrder.UNSPECIFIED || state.sortOrder == SortOrder.DESC) {
                        viewModel.updateSort(sortKey, SortOrder.ASC)
                    } else {
                        viewModel.updateSort(sortKey, SortOrder.DESC)
                    }
                }

                val headers: List<TableHeaderData> = listOf(
                    TableHeaderData(
                        width = tableAvatarWidth,
                        name = "avatar",
                    ) {
                        Text(
                            text = "Avatar",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    TableHeaderData(
                        width = tableUsernameWidth,
                        name = "username",
                        sortable = true,
                        sortOrder = if (state.sortKey == GetUsersQuerySortBy.USERNAME) state.sortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetUsersQuerySortBy.USERNAME)
                        },
                    ) {
                        Text(
                            text = "Username",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    TableHeaderData(
                        width = tableRoleWidth,
                        name = "role",
                        sortable = true,
                        sortOrder = if (state.sortKey == GetUsersQuerySortBy.ROLE) state.sortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetUsersQuerySortBy.ROLE)
                        },
                    ) {
                        Text(
                            text = "Role",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    TableHeaderData(
                        width = tableCreatedWidth,
                        name = "created",
                        sortable = true,
                        sortOrder = if (state.sortKey == GetUsersQuerySortBy.CREATED_AT) state.sortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetUsersQuerySortBy.CREATED_AT)
                        },
                    ) {
                        Text(
                            text = "Created",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    TableHeaderData(
                        width = tableLastUpdatedWidth,
                        name = "created",
                        sortable = true,
                        sortOrder = if (state.sortKey == GetUsersQuerySortBy.UPDATED_AT) state.sortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetUsersQuerySortBy.UPDATED_AT)
                        },
                    ) {
                        Text(
                            text = "Last Updated",
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

                val rows: List<TableRowData> = state.users?.map { user ->
                    val canInteractWithUser = canInteract(localLoggedUser?.role, user.role)

                    TableRowData(
                        clickable = true,
                        cells = listOf(
                            TableCellData(
                                width = tableAvatarWidth,
                            ) {
                                Avatar(
                                    avatar = user.avatar,
                                    isAdmin = user.role.level <= UserRole.ADMIN.level,
                                )
                            },
                            TableCellData(
                                width = tableUsernameWidth,
                            ) {
                                Text(
                                    text = user.username,
                                )
                            },
                            TableCellData(
                                width = tableRoleWidth,
                            ) {
                                Text(
                                    text = user.role.roleName,
                                )
                            },
                            TableCellData(
                                width = tableCreatedWidth,
                            ) {
                                Text(
                                    text = HumanReadable.timeAgo(Instant.parse(user.createdAt)),
                                )
                            },
                            TableCellData(
                                width = tableLastUpdatedWidth,
                            ) {
                                Text(
                                    text = HumanReadable.timeAgo(Instant.parse(user.updatedAt)),
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
                                    icon = painterResource(R.drawable.folder_open),
                                    iconContentDescription = "View user's files",
                                    color = MaterialTheme.colorScheme.primary,
                                    iconColor = MaterialTheme.colorScheme.onPrimary,
                                    onClick = {
                                        navController.navigate(FilesScreen(user.id))
                                    },
                                    enabled = !state.isLoading && canInteractWithUser
                                )

                                ActionButtonSpacer()

                                IconButton(
                                    icon = painterResource(R.drawable.edit),
                                    iconContentDescription = "Edit User",
                                    color = MaterialTheme.colorScheme.primary,
                                    iconColor = MaterialTheme.colorScheme.onPrimary,
                                    onClick = {
                                        viewModel.setEditUser(user)
                                    },
                                    enabled = !state.isLoading && canInteractWithUser
                                )

                                ActionButtonSpacer()

                                IconButton(
                                    icon = painterResource(R.drawable.delete),
                                    iconContentDescription = "Delete URL",
                                    color = MaterialTheme.colorScheme.error,
                                    iconColor = MaterialTheme.colorScheme.onError,
                                    onClick = {
                                        viewModel.setDeleteUser(user)
                                    },
                                    enabled = !state.isLoading && canInteractWithUser
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
                if (state.users == null || state.isLoading) {
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
                    if (state.users!!.size > 0L) {
                        items(state.users!!.size) { index ->
                            val user = state.users!![index]
                            val canInteractWithUser = canInteract(localLoggedUser?.role, user.role)

                            LargeUserDisplay(
                                user = user,
                                canInteractWithUser = canInteractWithUser,
                                onDelete = {
                                    viewModel.setDeleteUser(user)
                                },
                                onEdit = {
                                    viewModel.setEditUser(user)
                                },
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