package com.stefdp.zipline.screens.folders

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.LocalLoggedUser
import com.stefdp.zipline.LocalScreenViewState
import com.stefdp.zipline.LocalUpdateScreenViewState
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.DownloadFilePasswordPrompt
import com.stefdp.zipline.components.EnabledCheckbox
import com.stefdp.zipline.components.FilePreview
import com.stefdp.zipline.components.HeaderButton
import com.stefdp.zipline.components.table.TableCellData
import com.stefdp.zipline.components.table.TableHeaderData
import com.stefdp.zipline.components.table.TableRowData
import com.stefdp.zipline.network.models.BaseFolder
import com.stefdp.zipline.network.models.requests.GetFilesQuerySearchField
import com.stefdp.zipline.components.IconButton
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.Pager
import com.stefdp.zipline.components.PromptPopup
import com.stefdp.zipline.components.Select
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.components.Tag
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.components.largefiledisplay.LargeFileDisplay
import com.stefdp.zipline.components.table.Table
import com.stefdp.zipline.components.table.TableScrollbarConfig
import com.stefdp.zipline.network.models.requests.GetFilesQuerySortBy
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.screens.UploadFileScreen
import com.stefdp.zipline.screens.folders.components.CreateFolderPopup
import com.stefdp.zipline.screens.folders.components.DeleteFolderPopup
import com.stefdp.zipline.screens.folders.components.EditFolderNamePopup
import com.stefdp.zipline.screens.folders.components.LargeFolderDisplay
import com.stefdp.zipline.screens.folders.components.MoveFolderPopup
import com.stefdp.zipline.ui.theme.DarkGray
import com.stefdp.zipline.ui.theme.White
import com.stefdp.zipline.ui.theme.Yellow
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.SortOrder
import com.stefdp.zipline.utils.ZiplineViewStateType
import com.stefdp.zipline.utils.camelCaseToHumanReadable
import com.stefdp.zipline.utils.formatBytes
import com.stefdp.zipline.utils.shimmerable
import com.stefdp.zipline.utils.verticalLazyScrollbar
import kotlinx.coroutines.launch
import nl.jacobras.humanreadable.HumanReadable
import kotlin.time.Instant

const val COMPACT_VIEW_FILE_COUNT = 20L
const val DETAILED_VIEW_FILE_COUNT = 15L

const val VIEW_STATE_KEY = "foldersViewState"

@Composable
fun FoldersScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: FoldersViewModel = viewModel()
) {
    val localLoggedUser = LocalLoggedUser.current

    if (localLoggedUser == null) {
        navController.navigate(LoginScreen()) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    val state by viewModel.state.collectAsState()

    val screenViewState = LocalScreenViewState.current
    val updateScreenViewState = LocalUpdateScreenViewState.current

    val viewState = screenViewState.folders

    val filesPerPage = if (viewState == ZiplineViewStateType.COMPACT) COMPACT_VIEW_FILE_COUNT else DETAILED_VIEW_FILE_COUNT

    val defaultSortOrder = SortOrder.UNSPECIFIED

    LaunchedEffect(Unit) {
        viewModel.initData(context)

        viewModel.refreshFolders(context, viewState)
        viewModel.refreshFiles(context, filesPerPage)
        viewModel.refreshAllFolders(context)
        viewModel.refreshTags(context)
    }

    LaunchedEffect(
        state.folderSortKey,
        state.folderSortOrder,
        viewState
    ) {
        if (viewState == ZiplineViewStateType.COMPACT) {
            viewModel.triggerFolderSort(viewState)
        }
    }

    LaunchedEffect(state.mainFolder) {
        viewModel.refreshFolders(context, viewState)
        viewModel.refreshFiles(context, filesPerPage)
    }

    fun toggleAnonymousUploads(folder: BaseFolder) {
        viewModel.toggleFolderAnonymousUploads(
            context = context,
            folder = folder,
            viewState = viewState,
            onError = { error ->
                Notification.show(
                    activity = activity,
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            onSuccess = { folder ->
                Notification.show(
                    activity = activity,
                ) {
                    Text(
                        text = "${folder.name} will ${if (folder.allowUploads) "now allow" else "no longer allow"} anonymous uploads"
                    )
                }
            }
        )
    }

    fun togglePublic(folder: BaseFolder) {
        viewModel.toggleFolderPublic(
            context = context,
            folder = folder,
            viewState = viewState,
            onError = { error ->
                Notification.show(
                    activity = activity,
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            onSuccess = { folder ->
                Notification.show(
                    activity = activity,
                ) {
                    Text(
                        text = "${folder.name} is now ${if (folder.public) "public" else "private"}"
                    )
                }
            }
        )
    }

    val coroutineScope = rememberCoroutineScope()

    val directoryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )

            if (state.downloadFolderType == DownloadFolderType.FOLDER) {
                viewModel.setDownloadFolderExportUri(context, it)

                state.folderToExport?.let { folder ->
                    viewModel.performFolderExport(
                        context = context,
                        folder = folder,
                        folderExportUri = it,
                        sendNotification = { content ->
                            Notification.show(
                                activity = activity,
                                content = content
                            )
                        }
                    )
                }
            } else {
                viewModel.setDownloadFileUri(context, it)

                state.fileToDownload?.let { file ->
                    viewModel.performFileDownload(
                        context = context,
                        file = file,
                        fileDownloadUri = it,
                        sendNotification = { content ->
                            Notification.show(
                                activity = activity,
                                content = content
                            )
                         }
                    )
                }
            }
        }
    }

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
                text = "Folders",
                maxLines = 1,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderButton(
                    icon = painterResource(R.drawable.create_new_folder),
                    contentDescription = "Create Folder",
                    onClick = {
                        viewModel.openCreateNewFolderPopup()
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
                            context,
                            screenViewState.copy(
                                folders = newState
                            )
                        )
                    },
                    enabled = !state.isLoading,
                )
            }
        }

        if (state.foldersPath.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.home),
                    contentDescription = "Root folder",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            onClick = {
                                viewModel.setMainFolder(null)
                                viewModel.setFolderPath(emptyList())
                            }
                        )
                )

                state.foldersPath.forEachIndexed { index, folder ->
                    Text("/")

                    Text(
                        text = folder.name,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.clickable(
                            onClick = {
                                viewModel.setMainFolder(folder)
                                viewModel.setFolderPath(state.foldersPath.subList(0, index + 1))
                            }
                        )
                    )
                }
            }
        }

        DeleteFolderPopup(
            context = context,
            activity = activity,
            filesPerPage = filesPerPage,
            showPopup = state.deleteFolder != null,
            onDismissRequest = {
                viewModel.setDeleteFolder(null)
            },
            viewModel = viewModel,
            state = state,
            viewState = viewState
        )

        CreateFolderPopup(
            context = context,
            activity = activity,
            showPopup = state.createNewFolderPopupOpen,
            onDismissRequest = {
                viewModel.closeCreateNewFolderPopup()
            },
            viewModel = viewModel,
            state = state,
            viewState = viewState
        )

        EditFolderNamePopup(
            context = context,
            activity = activity,
            showPopup = state.editNameFolder != null,
            onDismissRequest = {
                viewModel.setEditNameFolder(null)
            },
            viewModel = viewModel,
            state = state,
            viewState = viewState
        )

        MoveFolderPopup(
            context = context,
            activity = activity,
            showPopup = state.moveFolder != null,
            onDismissRequest = {
                viewModel.setMoveFolder(null)
            },
            viewModel = viewModel,
            state = state,
            viewState = viewState
        )

        val foldersLazyColumnListState = rememberLazyListState()

        if (viewState == ZiplineViewStateType.COMPACT) {
            Column(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .weight(0.35f)
                    .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                    )
            ) {
                val tableNameWidth = 350.dp
                val tablePublicWidth = 120.dp
                val tableUploadsWidth = 130.dp
                val tableCreatedWidth = 170.dp
                val tableLastUpdatedAtWidth = 170.dp
                val tableActionsWidth = 130.dp

                fun onSortChanged(sortKey: GetFoldersQuerySortBy) {
                    if (state.folderSortKey != sortKey || state.folderSortOrder == SortOrder.UNSPECIFIED || state.folderSortOrder == SortOrder.DESC) {
                        viewModel.updateFoldersSort(sortKey, SortOrder.ASC)
                    } else {
                        viewModel.updateFoldersSort(sortKey, SortOrder.DESC)
                    }
                }

                val headers: List<TableHeaderData> = listOf(
                    TableHeaderData(
                        width = tableNameWidth,
                        name = "name",
                        sortable = true,
                        sortOrder = if (state.folderSortKey == GetFoldersQuerySortBy.NAME) state.folderSortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetFoldersQuerySortBy.NAME)
                        },
                    ) {
                        Text(
                            text = "Name",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    TableHeaderData(
                        width = tablePublicWidth,
                        name = "public",
                        sortable = true,
                        sortOrder = if (state.folderSortKey == GetFoldersQuerySortBy.PUBLIC) state.folderSortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetFoldersQuerySortBy.PUBLIC)
                        },
                    ) {
                        Text(
                            text = "Public",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    TableHeaderData(
                        width = tableUploadsWidth,
                        name = "uploads",
                        sortable = true,
                        sortOrder = if (state.folderSortKey == GetFoldersQuerySortBy.UPLOADS) state.folderSortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetFoldersQuerySortBy.UPLOADS)
                        },
                    ) {
                        Text(
                            text = "Uploads?",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    TableHeaderData(
                        width = tableCreatedWidth,
                        name = "created at",
                        sortable = true,
                        sortOrder = if (state.folderSortKey == GetFoldersQuerySortBy.CREATED_AT) state.folderSortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetFoldersQuerySortBy.CREATED_AT)
                        },
                    ) {
                        Text(
                            text = "Created",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    TableHeaderData(
                        width = tableLastUpdatedAtWidth,
                        name = "lastUpdatedAt",
                        sortable = true,
                        sortOrder = if (state.folderSortKey == GetFoldersQuerySortBy.UPDATED_AT) state.folderSortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetFoldersQuerySortBy.UPDATED_AT)
                        },
                    ) {
                        Text(
                            text = "Last update at",
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

                val rows: List<TableRowData> = state.folders?.map { folder ->
                    TableRowData(
                        clickable = true,
                        onClick = {
                            viewModel.setMainFolder(folder)
                            viewModel.setFolderPath(state.foldersPath + folder)
                        },
                        cells = listOf(
                            TableCellData(
                                width = tableNameWidth,
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = folder.name,
                                    )

                                    val folderChildrenCount = folder.count?.children ?: 0

                                    if (folderChildrenCount > 0) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "$folderChildrenCount SUBFOLDER${if (folderChildrenCount > 1) "S" else ""}",
                                            )
                                        }
                                    }
                                }
                            },
                            TableCellData(
                                width = tablePublicWidth,
                            ) {
                                EnabledCheckbox(
                                    enabled = folder.public
                                )
                            },
                            TableCellData(
                                width = tableUploadsWidth,
                            ) {
                                EnabledCheckbox(
                                    enabled = folder.allowUploads
                                )
                            },
                            TableCellData(
                                width = tableCreatedWidth,
                            ) {
                                Text(
                                    text = HumanReadable.timeAgo(Instant.parse(folder.createdAt)),
                                )
                            },
                            TableCellData(
                                width = tableLastUpdatedAtWidth,
                            ) {
                                Text(
                                    text = HumanReadable.timeAgo(Instant.parse(folder.updatedAt)),
                                )
                            },
                            TableCellData(
                                width = tableActionsWidth,
                            ) {
                                @Composable
                                fun ActionButtonSpacer() {
                                    Spacer(modifier = Modifier.width(8.dp))
                                }

                                var expanded by rememberSaveable { mutableStateOf(false) }

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                                ) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "Open Folder",
                                                color = if (state.isLoading)
                                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                                else
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        },
                                        enabled = !state.isLoading,
                                        onClick = {
                                            viewModel.setMainFolder(folder)
                                            viewModel.setFolderPath(state.foldersPath + folder)

                                            expanded = false
                                        },
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.folder_open),
                                                contentDescription = "Open folder",
                                                tint = if (state.isLoading)
                                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                                else
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    )

                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "Move Folder",
                                                color = if (!state.isLoading)
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                else
                                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                            )
                                        },
                                        enabled = !state.isLoading,
                                        onClick = {
                                            viewModel.setMoveFolder(folder)
                                            expanded = false
                                        },
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.folder_copy),
                                                contentDescription = "Move folder",
                                                tint = if (!state.isLoading)
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                else
                                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                            )
                                        }
                                    )

                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "Export as ZIP",
                                                color = if (!state.isLoading)
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                else
                                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                            )
                                        },
                                        enabled = !state.isLoading,
                                        onClick = {
                                            viewModel.setFolderToExport(folder)

                                            if (state.selectedFolderExportUri == null) {
                                                viewModel.setDownloadFolderType(DownloadFolderType.FOLDER)

                                                directoryPicker.launch(null)

                                                return@DropdownMenuItem
                                            }

                                            viewModel.performFolderExport(
                                                context = context,
                                                folder = folder,
                                                folderExportUri = state.selectedFolderExportUri!!,
                                                sendNotification = { content ->
                                                    Notification.show(
                                                        activity = activity,
                                                        content = content
                                                    )
                                                }
                                            )

                                            expanded = false
                                        },
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.folder_zip),
                                                contentDescription = "Export as ZIP",
                                                tint = if (!state.isLoading)
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                else
                                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                            )
                                        }
                                    )

                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = if (folder.public) "Make Private" else "Make Public",
                                                color = if (!state.isLoading)
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                else
                                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                            )
                                        },
                                        enabled = !state.isLoading,
                                        onClick = {
                                            togglePublic(folder)

                                            expanded = false
                                        },
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(
                                                    if (folder.public)
                                                        R.drawable.lock
                                                    else
                                                        R.drawable.lock_open
                                                ),
                                                contentDescription = if (folder.public) "Make private" else "Make public",
                                                tint = if (!state.isLoading)
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                else
                                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                            )
                                        }
                                    )

                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = if (folder.allowUploads) "Disallow Anonymous Uploads" else "Allow Anonymous Uploads",
                                                color = if (!state.isLoading)
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                else
                                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                            )
                                        },
                                        enabled = !state.isLoading,
                                        onClick = {
                                            toggleAnonymousUploads(folder)

                                            expanded = false
                                        },
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(
                                                    if (folder.allowUploads)
                                                        R.drawable.share_off
                                                    else
                                                        R.drawable.share
                                                ),
                                                contentDescription = if (folder.allowUploads) "Disallow anonymous uploads" else "Allow anonymous uploads",
                                                tint = if (!state.isLoading)
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                else
                                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                            )
                                        }
                                    )

                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "Edit Name",
                                                color = if (!state.isLoading)
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                else
                                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                            )
                                        },
                                        enabled = !state.isLoading,
                                        onClick = {
                                            viewModel.setEditNameFolder(folder)
                                            expanded = false
                                        },
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.edit),
                                                contentDescription = "Edit name",
                                                tint = if (!state.isLoading)
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                else
                                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                            )
                                        }
                                    )

                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "Delete",
                                                color = if (!state.isLoading)
                                                    MaterialTheme.colorScheme.error
                                                else
                                                    MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                                            )
                                        },
                                        enabled = !state.isLoading,
                                        onClick = {
                                            viewModel.setDeleteFolder(folder)
                                            expanded = false
                                        },
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.delete),
                                                contentDescription = "Delete",
                                                tint = if (!state.isLoading)
                                                    MaterialTheme.colorScheme.error
                                                else
                                                    MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                                            )
                                        }
                                    )
                                }

                                IconButton(
                                    icon = painterResource(R.drawable.more_horiz),
                                    iconContentDescription = "More actions",
                                    color = MaterialTheme.colorScheme.primary,
                                    iconColor = MaterialTheme.colorScheme.onPrimary,
                                    onClick = { expanded = true },
                                    enabled = !state.isLoading
                                )

                                ActionButtonSpacer()

                                val clipboardManager = LocalClipboard.current

                                IconButton(
                                    icon = painterResource(R.drawable.content_copy),
                                    iconContentDescription = "Copy folder link",
                                    color = MaterialTheme.colorScheme.primary,
                                    iconColor = MaterialTheme.colorScheme.onPrimary,
                                    onClick = {
                                        val fileUrl = "${state.defaultDomain}/folder/${folder.id}"

                                        val clipData = ClipData.newPlainText("Folder URL", fileUrl).toClipEntry()

                                        coroutineScope.launch {
                                            clipboardManager.setClipEntry(clipData)

                                            Notification.show(
                                                activity = activity,
                                            ) {
                                                Text(
                                                    text = "Folder link copied to clipboard"
                                                )
                                            }
                                        }
                                    },
                                    enabled = state.serverUrl != null && !state.isLoading && folder.public
                                )

                                ActionButtonSpacer()

                                IconButton(
                                    icon = painterResource(R.drawable.delete),
                                    iconContentDescription = "Delete folder",
                                    color = MaterialTheme.colorScheme.error,
                                    iconColor = MaterialTheme.colorScheme.onError,
                                    onClick = {
                                        viewModel.setDeleteFolder(folder)
                                    },
                                    enabled = state.serverUrl != null && !state.isLoading
                                )
                            }
                        )
                    )
                } ?: emptyList()

                Table(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.35f),
                    headers = headers,
                    rows = rows,
                    loading = state.folders == null,
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
            val foldersModifier = Modifier.fillMaxWidth()
            val foldersHeightModifier = if (state.folders?.isEmpty() == true && state.mainFolder != null)
                foldersModifier.height(0.dp)
            else
                foldersModifier.weight(0.35f)

            LazyColumn(
                state = foldersLazyColumnListState,
                modifier = foldersHeightModifier
                    .padding(vertical = 8.dp)
                    .verticalLazyScrollbar(
                        listState = foldersLazyColumnListState,
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (state.folders == null || state.isLoading) {
                    items(5) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                                .fillMaxWidth()
                                .height(170.dp)
                                .shimmerable(
                                    enabled = true,
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    keepBackground = true
                                ),
                            contentAlignment = Alignment.Center
                        ) {}
                    }
                } else {
                    state.folders?.let { folders ->
                        if (folders.size > 0L) {
                            items(folders.size) { index ->
                                val folder = folders[index]

                                LargeFolderDisplay(
                                    context = context,
                                    activity = activity,
                                    folder = folder,
                                    serverUrl = state.defaultDomain,
                                    onOpen = {
                                        viewModel.setMainFolder(folder)
                                        viewModel.setFolderPath(state.foldersPath + folder)
                                    },
                                    onMove = {
                                        viewModel.setMoveFolder(folder)
                                    },
                                    onExportZip = {
                                        viewModel.setFolderToExport(folder)

                                        if (state.selectedFolderExportUri == null) {
                                            viewModel.setDownloadFolderType(DownloadFolderType.FOLDER)

                                            directoryPicker.launch(null)

                                            return@LargeFolderDisplay
                                        }

                                        viewModel.performFolderExport(
                                            context = context,
                                            folder = folder,
                                            folderExportUri = state.selectedFolderExportUri!!,
                                            sendNotification = { content ->
                                                Notification.show(
                                                    activity = activity,
                                                    content = content
                                                )
                                            }
                                        )
                                    },
                                    onTogglePublic = {
                                        togglePublic(folder)
                                    },
                                    onToggleAnonymousUploads = {
                                        toggleAnonymousUploads(folder)
                                    },
                                    onEditName = {
                                        viewModel.setEditNameFolder(folder)
                                    },
                                    onDelete = {
                                        viewModel.setDeleteFolder(folder)
                                    },
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
                                            painter = painterResource(R.drawable.folder),
                                            contentDescription = "Folder",
                                            modifier = Modifier.size(32.dp)
                                        )

                                        Text(
                                            text = "No folders found",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }

                                    Text(
                                        text = "Create a folder to see it here.",
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (state.mainFolder != null) {
            val filesLazyColumnListState = rememberLazyListState()

            LaunchedEffect(
                state.currentPage,
                state.fileSortOrder,
                state.fileSortKey,
                state.fileSearchValue,
            ) {
                viewModel.refreshFiles(context, filesPerPage)
                filesLazyColumnListState.animateScrollToItem(0)
                viewModel.setClickedFile(null)
            }

            LaunchedEffect(state.folderFiles) {
                if (state.clickedFile != null) {
                    val updatedFile = state.folderFiles?.find { it.id == state.clickedFile?.id }

                    if (updatedFile != null) {
                        viewModel.setClickedFile(updatedFile)
                    }
                }
            }

            fun updateData() {
                viewModel.refreshFolders(context, viewState)
            }

            LargeFileDisplay(
                context = context,
                activity = activity,
                file = state.clickedFile,
                updateData = ::updateData,
                onDismissRequest = {
                    viewModel.setClickedFile(null)
                },
                tags = state.tags,
            )

            if (viewState == ZiplineViewStateType.COMPACT) {
                var uiSearchValue by rememberSaveable(
                    stateSaver = TextFieldValue.Saver
                ) { mutableStateOf(state.fileSearchValue) }

                LaunchedEffect(state.fileSearchKey) {
                    uiSearchValue = TextFieldValue("")
                }

                if (state.fileSearchKey != null) {
                    Column(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Search by ${camelCaseToHumanReadable(state.fileSearchKey.toString())}",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                            )

                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable(
                                        onClick = {
                                            viewModel.updateFileSearchKey(null)
                                        }
                                    )
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.close),
                                    contentDescription = "Close search",
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        if (state.fileSearchKey == GetFilesQuerySearchField.TAGS) {
                            Select(
                                label = "Tags",
                                multiple = true,
                                enabled = !state.tagsLoading && state.tags?.isNotEmpty() ?: false,
                                options = state.tags?.map { tag ->
                                    SelectOption(
                                        id = tag.id,
                                        label = { enabled ->
                                            Tag(
                                                tag = tag,
                                                enabled = enabled
                                            )
                                        },
                                    )
                                } ?: emptyList(),
                                selectedIds = state.selectedTagIds,
                                onSelectionChange = { newSelectedIds ->
                                    if (newSelectedIds == state.selectedTagIds) return@Select

                                    viewModel.setSelectedTagIds(newSelectedIds)
                                }
                            )
                        } else {
                            fun onEnter() {
                                viewModel.updateFileSearchValue(uiSearchValue)
                            }

                            val focusManager = LocalFocusManager.current

                            TextInput(
                                value = uiSearchValue,
                                onValueChange = { uiSearchValue = it },
                                label = "Search",
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        onEnter()
                                        focusManager.clearFocus()
                                    }
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onPreviewKeyEvent { keyEvent ->
                                        if (keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyUp) {
                                            onEnter()
                                            focusManager.clearFocus()

                                            true
                                        } else {
                                            false
                                        }
                                    }
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                        .weight(0.65f)
                        .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                        )
                ) {
                    val tableNameWidth = 300.dp
                    val tableTagsWidth = 200.dp
                    val tableTypeWidth = 250.dp
                    val tableSizeWidth = 150.dp
                    val tableCreatedAtWidth = 170.dp
                    val tableFavoriteWidth = 130.dp
                    val tableIdWidth = 300.dp
                    val tableActionsWidth = 220.dp

                    fun onSortChanged(sortKey: GetFilesQuerySortBy) {
                        if (state.fileSortKey != sortKey || state.fileSortOrder == SortOrder.UNSPECIFIED || state.fileSortOrder == SortOrder.DESC) {
                            viewModel.updateFilesSort(sortKey, SortOrder.ASC)
                        } else {
                            viewModel.updateFilesSort(sortKey, SortOrder.DESC)
                        }
                    }

                    val headers: List<TableHeaderData> = listOf(
                        TableHeaderData(
                            width = tableNameWidth,
                            name = "name",
                            searchable = true,
                            sortable = true,
                            sortOrder = if (state.fileSortKey == GetFilesQuerySortBy.NAME) state.fileSortOrder else defaultSortOrder,
                            onSortChanged = {
                                onSortChanged(GetFilesQuerySortBy.NAME)
                            },
                            onSearchClick = {
                                viewModel.updateFileSearchKey(GetFilesQuerySearchField.NAME)
                            }
                        ) {
                            Text(
                                text = "Name",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        TableHeaderData(
                            width = tableTagsWidth,
                            name = "tags",
                            searchable = true,
                            onSearchClick = {
                                viewModel.updateFileSearchKey(GetFilesQuerySearchField.TAGS)
                            },
                            searchEnabled = !state.tagsLoading && state.tags?.isNotEmpty() ?: false
                        ) {
                            Text(
                                text = "Tags",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        TableHeaderData(
                            width = tableTypeWidth,
                            name = "type",
                            searchable = true,
                            sortable = true,
                            sortOrder = if (state.fileSortKey == GetFilesQuerySortBy.TYPE) state.fileSortOrder else defaultSortOrder,
                            onSortChanged = {
                                onSortChanged(GetFilesQuerySortBy.TYPE)
                            },
                            onSearchClick = {
                                viewModel.updateFileSearchKey(GetFilesQuerySearchField.TYPE)
                            }
                        ) {
                            Text(
                                text = "Type",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        TableHeaderData(
                            width = tableSizeWidth,
                            name = "size",
                            sortable = true,
                            sortOrder = if (state.fileSortKey == GetFilesQuerySortBy.SIZE) state.fileSortOrder else defaultSortOrder,
                            onSortChanged = {
                                onSortChanged(GetFilesQuerySortBy.SIZE)
                            }
                        ) {
                            Text(
                                text = "Size",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        TableHeaderData(
                            width = tableCreatedAtWidth,
                            name = "created at",
                            sortable = true,
                            sortOrder = if (state.fileSortKey == GetFilesQuerySortBy.CREATED_AT) state.fileSortOrder else defaultSortOrder,
                            onSortChanged = {
                                onSortChanged(GetFilesQuerySortBy.CREATED_AT)
                            }
                        ) {
                            Text(
                                text = "Created At",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        TableHeaderData(
                            width = tableFavoriteWidth,
                            name = "favorite",
                            sortable = true,
                            sortOrder = if (state.fileSortKey == GetFilesQuerySortBy.FAVORITE) state.fileSortOrder else defaultSortOrder,
                            onSortChanged = {
                                onSortChanged(GetFilesQuerySortBy.FAVORITE)
                            }
                        ) {
                            Text(
                                text = "Favorite",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        TableHeaderData(
                            width = tableIdWidth,
                            name = "ID",
                            searchable = true,
                            sortable = true,
                            sortOrder = if (state.fileSortKey == GetFilesQuerySortBy.ID) state.fileSortOrder else defaultSortOrder,
                            onSortChanged = {
                                onSortChanged(GetFilesQuerySortBy.ID)
                            },
                            onSearchClick = {
                                viewModel.updateFileSearchKey(GetFilesQuerySearchField.ID)
                            }
                        ) {
                            Text(
                                text = "ID",
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

                    PromptPopup(
                        showPopup = state.deleteFile != null,
                        onDismissRequest = {
                            viewModel.setDeleteFile(null)
                        },
                        onCancel = {
                            viewModel.setDeleteFile(null)
                        },
                        isLoading = state.isLoading,
                        title = "Are you sure?",
                        description = "Are you sure you want to delete ${state.deleteFile?.originalName ?: state.deleteFile?.name}? This action cannot be undone.",
                        onSuccess = {
                            viewModel.deleteFile(
                                context = context,
                                filesPerPage = filesPerPage,
                                onSuccess = {
                                    Notification.show(
                                        activity = activity,
                                    ) {
                                        Text(
                                            text = "File deleted successfully"
                                        )
                                    }
                                },
                                onError = { error ->
                                    Notification.show(
                                        activity = activity,
                                    ) {
                                        Text(
                                            text = error,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            )
                        }
                    )

                    val rows: List<TableRowData> = state.folderFiles?.map { file ->
                        TableRowData(
                            clickable = true,
                            onClick = {
                                viewModel.setClickedFile(file)
                            },
                            cells = listOf(
                                TableCellData(
                                    width = tableNameWidth,
                                ) {
                                    Text(
                                        text = file.name,
                                    )
                                },
                                TableCellData(
                                    width = tableTagsWidth,
                                ) {
                                    FlowRow(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        file.tags?.forEach { tag ->
                                            Tag(tag = tag)
                                        }
                                    }
                                },
                                TableCellData(
                                    width = tableTypeWidth,
                                ) {
                                    Text(
                                        text = file.type,
                                    )
                                },
                                TableCellData(
                                    width = tableSizeWidth,
                                ) {
                                    Text(
                                        text = formatBytes(file.size),
                                    )
                                },
                                TableCellData(
                                    width = tableCreatedAtWidth,
                                ) {
                                    Text(
                                        text = HumanReadable.timeAgo(Instant.parse(file.createdAt)),
                                    )
                                },
                                TableCellData(
                                    width = tableFavoriteWidth,
                                ) {
                                    Text(
                                        text = if (file.favorite) "Yes" else "No",
                                        color = if (file.favorite) Yellow else MaterialTheme.colorScheme.onBackground
                                    )
                                },
                                TableCellData(
                                    width = tableIdWidth,
                                ) {
                                    Text(
                                        text = file.id,
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
                                        icon = painterResource(R.drawable.draft),
                                        iconContentDescription = "More details",
                                        color = MaterialTheme.colorScheme.primary,
                                        iconColor = MaterialTheme.colorScheme.onPrimary,
                                        onClick = {
                                            viewModel.setClickedFile(file)
                                        },
                                        enabled = !state.isLoading
                                    )

                                    ActionButtonSpacer()

                                    IconButton(
                                        icon = painterResource(R.drawable.open_new),
                                        iconContentDescription = "Open file in browser",
                                        color = MaterialTheme.colorScheme.primary,
                                        iconColor = MaterialTheme.colorScheme.onPrimary,
                                        onClick = {
                                            val fileUrl = "${state.serverUrl}${file.url}"

                                            val intent = Intent(Intent.ACTION_VIEW, fileUrl.toUri())
                                            activity.startActivity(intent)
                                        },
                                        enabled = state.serverUrl != null && !state.isLoading
                                    )

                                    ActionButtonSpacer()

                                    val clipboardManager = LocalClipboard.current

                                    IconButton(
                                        icon = painterResource(R.drawable.content_copy),
                                        iconContentDescription = "Copy file link",
                                        color = MaterialTheme.colorScheme.primary,
                                        iconColor = MaterialTheme.colorScheme.onPrimary,
                                        onClick = {
                                            val fileUrl = "${state.defaultDomain}${file.url}"

                                            val clipData = ClipData.newPlainText("File URL", fileUrl).toClipEntry()

                                            coroutineScope.launch {
                                                clipboardManager.setClipEntry(clipData)

                                                Notification.show(
                                                    activity = activity,
                                                ) {
                                                    Text(
                                                        text = "File link copied to clipboard"
                                                    )
                                                }
                                            }
                                        },
                                        enabled = state.serverUrl != null && !state.isLoading
                                    )

                                    ActionButtonSpacer()

                                    DownloadFilePasswordPrompt(
                                        context = context,
                                        activity = activity,
                                        fileId = file.id,
                                        showPopup = state.fileRequiresPassword && state.downloadFilePassword == null,
                                        onDismissRequest = {
                                            viewModel.setFileRequiresPassword(false)
                                            viewModel.setDownloadFilePassword(null)
                                        },
                                        onDownload = {
                                            if (state.selectedFileDownloadUri == null) return@DownloadFilePasswordPrompt

                                            viewModel.setDownloadFilePassword(it)

                                            state.fileToDownload?.let { file ->
                                                viewModel.performFileDownload(
                                                    context = context,
                                                    file = file,
                                                    fileDownloadUri = state.selectedFileDownloadUri!!,
                                                    sendNotification = { content ->
                                                        Notification.show(
                                                            activity = activity,
                                                            content = content
                                                        )
                                                    }
                                                )
                                            }
                                        }
                                    )

                                    IconButton(
                                        icon = painterResource(R.drawable.download),
                                        iconContentDescription = "Download file",
                                        color = DarkGray,
                                        iconColor = White,
                                        onClick = {
                                            viewModel.setFileToDownload(file)

                                            if (state.selectedFileDownloadUri == null) {
                                                viewModel.setDownloadFolderType(DownloadFolderType.FILE)

                                                directoryPicker.launch(null)

                                                return@IconButton
                                            }

                                            viewModel.performFileDownload(
                                                context = context,
                                                file = file,
                                                fileDownloadUri = state.selectedFileDownloadUri!!,
                                                sendNotification = { content ->
                                                    Notification.show(
                                                        activity = activity,
                                                        content = content
                                                    )
                                                }
                                            )
                                        },
                                        enabled = state.serverUrl != null && !state.isLoading
                                    )

                                    ActionButtonSpacer()

                                    IconButton(
                                        icon = painterResource(R.drawable.delete),
                                        iconContentDescription = "Open file in browser",
                                        color = MaterialTheme.colorScheme.error,
                                        iconColor = MaterialTheme.colorScheme.onError,
                                        onClick = {
                                            viewModel.setDeleteFile(file)
                                        },
                                        enabled = state.serverUrl != null && !state.isLoading
                                    )
                                }
                            )
                        )
                    } ?: emptyList()

                    Table(
                        modifier = Modifier.fillMaxSize(),
                        headers = headers,
                        rows = rows,
                        loading = state.folderFiles == null,
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
                    state = filesLazyColumnListState,
                    modifier = Modifier
                        .weight(0.65f)
                        .padding(vertical = 8.dp)
                        .verticalLazyScrollbar(
                            listState = filesLazyColumnListState,
                        )
                ) {
                    if (state.folderFiles == null || state.isLoading) {
                        items(5) {
                            Box(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                                    .fillMaxWidth()
                                    .height(250.dp)
                                    .shimmerable(
                                        enabled = true,
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        keepBackground = true
                                    ),
                                contentAlignment = Alignment.Center
                            ) {}
                        }
                    } else {
                        state.folderFiles?.let { files ->
                            if (files.size > 0L) {
                                items(files.size) { index ->
                                    val file = files[index]

                                    FilePreview(
                                        file = file,
                                        context = context,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(250.dp)
                                            .padding(5.dp),
                                        onClick = {
                                            viewModel.setClickedFile(file)
                                        },
                                        serverUrl = state.serverUrl,
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
                                                text = "No files found",
                                                style = MaterialTheme.typography.titleLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                navController.navigate(UploadFileScreen())
                                            },
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.upload),
                                                contentDescription = "Upload file",
                                            )

                                            Spacer(
                                                modifier = Modifier.width(8.dp)
                                            )

                                            Text(
                                                text = "Upload a file",
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Pager(
                currentPage = state.currentPage,
                totalPages = state.totalPages,
                onFirstPageClick = {
                    viewModel.setCurrentPage(1)
                },
                onPreviousPageClick = {
                    if (state.currentPage > 1) viewModel.setCurrentPage(state.currentPage - 1)
                },
                onCustomPageInput = { newPage ->
                    viewModel.setCurrentPage(newPage.coerceIn(1, state.totalPages))
                },
                onNextPageClick = {
                    if (state.currentPage < state.totalPages) viewModel.setCurrentPage(state.currentPage + 1)
                },
                onLastPageClick = {
                    viewModel.setCurrentPage(state.totalPages)
                },
                enabled = !state.isLoading && state.totalPages > 1
            )
        }
    }
}