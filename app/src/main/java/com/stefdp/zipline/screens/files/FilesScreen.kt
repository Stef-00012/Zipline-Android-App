package com.stefdp.zipline.screens.files

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.stefdp.zipline.components.PromptPopup
import com.stefdp.zipline.components.DownloadFilePasswordPrompt
import com.stefdp.zipline.components.FilePreview
import com.stefdp.zipline.components.HeaderButton
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.Pager
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.components.Select
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.components.Tag
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.components.largefiledisplay.LargeFileDisplay
import com.stefdp.zipline.components.table.Table
import com.stefdp.zipline.components.table.TableCellData
import com.stefdp.zipline.components.table.TableHeaderData
import com.stefdp.zipline.components.table.TableRowData
import com.stefdp.zipline.components.table.TableScrollbarConfig
import com.stefdp.zipline.network.models.UserRole
import com.stefdp.zipline.network.models.requests.GetFilesQuerySearchField
import com.stefdp.zipline.network.models.requests.GetFilesQuerySortBy
import com.stefdp.zipline.screens.HomeScreen
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.screens.UploadFileScreen
import com.stefdp.zipline.components.IconButton
import com.stefdp.zipline.screens.files.components.PendingFile
import com.stefdp.zipline.screens.files.components.tags.TagsPopup
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

const val COMPACT_VIEW_FILE_COUNT = 25L
const val DETAILED_VIEW_FILE_COUNT = 15L

const val VIEW_STATE_KEY = "filesViewState"

internal val validRoles = listOf(
    UserRole.ADMIN,
    UserRole.SUPERADMIN
)

@Composable
fun FilesScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    userId: String? = null,
    viewModel: FilesViewModel = viewModel()
) {
    val localLoggedUser = LocalLoggedUser.current

    if (localLoggedUser == null) {
        navController.navigate(LoginScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    if (userId != null && localLoggedUser?.role !in validRoles) {
        navController.navigate(HomeScreen) {
            popUpTo(HomeScreen) { inclusive = true }
        }
    }

    val state by viewModel.state.collectAsState()

    val screenViewState = LocalScreenViewState.current
    val updateScreenViewState = LocalUpdateScreenViewState.current

    val viewState = screenViewState.files

    val filesPerPage = if (viewState == ZiplineViewStateType.COMPACT) COMPACT_VIEW_FILE_COUNT else DETAILED_VIEW_FILE_COUNT

    val defaultSortOrder = SortOrder.UNSPECIFIED

    suspend fun updateFiles() {
        viewModel.refreshFiles(
            context = context,
            filesPerPage = filesPerPage,
            userId = userId,
            currentUserRole = localLoggedUser?.role,
            onUnauthorized = {
                navController.navigate(HomeScreen) {
                    popUpTo(HomeScreen) { inclusive = true }
                }
            }
        )
    }

    suspend fun updateIncompleteFiles() {
        viewModel.refreshIncompleteFiles(context)
    }

    suspend fun updateTags() {
        viewModel.refreshTags(context)
    }

    LaunchedEffect(Unit) {
        viewModel.initData(context)

        updateIncompleteFiles()
        updateTags()
    }

    TagsPopup(
        showPopup = state.manageTagsPopupOpen,
        onDismissRequest = {
            viewModel.closeManageTagsPopup()
        },
        context = context,
        activity = activity,
        viewModel = viewModel,
        state = state
    )

    Popup(
        showPopup = state.pendingFilesPopupOpen,
        onDismissRequest = {
            viewModel.closePendingFilesPopup()
        },
        scrollable = false
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Pending Files",
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
                            viewModel.closePendingFilesPopup()
                        }
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.close),
                    contentDescription = "Close pending files",
                )
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        if (state.incompleteFiles != null && state.incompleteFiles!!.isNotEmpty()) {
            val lazyListState = rememberLazyListState()

            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalLazyScrollbar(
                        listState = lazyListState,
                    )
            ) {
                items(state.incompleteFiles!!.size) { incompleteFileIndex ->
                    val incompleteFile = state.incompleteFiles!![incompleteFileIndex]

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    PendingFile(
                        context = context,
                        activity = activity,
                        file = incompleteFile,
                        viewModel = viewModel,
                        state = state
                    )
                }
            }
        } else {
            Text(
                text = "No pending files.",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                ),
            )
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
            val titleText = if (state.username != null) "${state.username}'s Files" else "Files"
            var isTitleOverflowing by rememberSaveable { mutableStateOf(false) }

            Text(
                text = titleText,
                maxLines = 1,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { textLayoutResult ->
                    isTitleOverflowing = textLayoutResult.hasVisualOverflow
                },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                    .combinedClickable(
                        role = Role.Button,
                        enabled = isTitleOverflowing,
                        onClick = {},
                        onLongClick = {
                            Notification.show(
                                activity = activity,
                            ) {
                                Text(titleText)
                            }
                        }
                    ),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderButton(
                    icon = if (state.favoriteFilter)
                        painterResource(R.drawable.star_filled)
                    else   painterResource(R.drawable.star),
                    contentDescription = "Favorites filter",
                    enabled = !state.isLoading,
                    onClick = {
                        viewModel.toggleFavoriteFilter()
                    },
                    iconColor = if (state.favoriteFilter) Yellow else MaterialTheme.colorScheme.primary
                )

                if (userId == null) {
                    Spacer(
                        modifier = Modifier.width(5.dp)

                    )
                    HeaderButton(
                        icon = painterResource(R.drawable.upload_file),
                        contentDescription = "Upload file",
                        onClick = {
                            navController.navigate(UploadFileScreen())
                        },
                        enabled = !state.isLoading,
                    )
                }

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
                                files = newState
                            )
                        )
                    },
                    enabled = !state.isLoading,
                )

                if (userId == null) {
                    Spacer(
                        modifier = Modifier.width(5.dp)
                    )

                    var dropdownExpanded by rememberSaveable { mutableStateOf(false) }

                    Box(
                        modifier = Modifier.wrapContentSize(Alignment.TopStart)
                    ) {
                        HeaderButton(
                            icon = painterResource(R.drawable.more_horiz),
                            contentDescription = "More actions",
                            onClick = { dropdownExpanded = true },
                            enabled = !state.isLoading,
                        )

                        DropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false },
                            containerColor = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text("Manage Tags")
                                },
                                onClick = {
                                    viewModel.openManageTagsPopup()
                                    dropdownExpanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.sell),
                                        contentDescription = "Manage tags"
                                    )
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text("View Pending Files")
                                },
                                onClick = {
                                    viewModel.openPendingFilesPopup()
                                    dropdownExpanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.pending),
                                        contentDescription = "View pending files"
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        val lazyColumnListState = rememberLazyListState()

        LaunchedEffect(
            state.currentPage,
            state.favoriteFilter,
            state.sortOrder,
            state.sortKey,
            state.searchValue,
        ) {
            updateFiles()
            lazyColumnListState.animateScrollToItem(0)
            viewModel.setClickedFile(null)
        }

        LaunchedEffect(state.files) {
            if (state.clickedFile != null) {
                val updatedFile = state.files?.find { it.id == state.clickedFile?.id }

                if (updatedFile != null) {
                    viewModel.setClickedFile(updatedFile)
                }
            }
        }

        LargeFileDisplay(
            context = context,
            activity = activity,
            file = state.clickedFile,
            onDismissRequest = {
                viewModel.setClickedFile(null)
            },
            updateData = ::updateFiles,
            tags = state.tags,
            onDelete = {
                viewModel.setClickedFile(null)
            }
        )

        if (viewState == ZiplineViewStateType.COMPACT) {
            var uiSearchValue by rememberSaveable(
                stateSaver = TextFieldValue.Saver
            ) { mutableStateOf(state.searchValue) }

            LaunchedEffect(state.searchKey) {
                uiSearchValue = TextFieldValue("")
            }

            if (state.searchKey != null) {
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
                            text = "Search by ${camelCaseToHumanReadable(state.searchKey.toString())}",
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
                                        viewModel.updateSearchKey(null)
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

                    if (state.searchKey == GetFilesQuerySearchField.TAGS) {
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
                            viewModel.updateSearchValue(uiSearchValue)
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
                    .weight(1f)
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
                    if (state.sortKey != sortKey || state.sortOrder == SortOrder.UNSPECIFIED || state.sortOrder == SortOrder.DESC) {
                        viewModel.updateSort(sortKey, SortOrder.ASC)
                    } else {
                        viewModel.updateSort(sortKey, SortOrder.DESC)
                    }
                }

                val headers: List<TableHeaderData> = listOf(
                    TableHeaderData(
                        width = tableNameWidth,
                        name = "name",
                        searchable = true,
                        sortable = true,
                        sortOrder = if (state.sortKey == GetFilesQuerySortBy.NAME) state.sortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetFilesQuerySortBy.NAME)
                        },
                        onSearchClick = {
                            viewModel.updateSearchKey(GetFilesQuerySearchField.NAME)
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
                            viewModel.updateSearchKey(GetFilesQuerySearchField.TAGS)
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
                        sortOrder = if (state.sortKey == GetFilesQuerySortBy.TYPE) state.sortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetFilesQuerySortBy.TYPE)
                        },
                        onSearchClick = {
                            viewModel.updateSearchKey(GetFilesQuerySearchField.TYPE)
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
                        sortOrder = if (state.sortKey == GetFilesQuerySortBy.SIZE) state.sortOrder else defaultSortOrder,
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
                        sortOrder = if (state.sortKey == GetFilesQuerySortBy.CREATED_AT) state.sortOrder else defaultSortOrder,
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
                        sortOrder = if (state.sortKey == GetFilesQuerySortBy.FAVORITE) state.sortOrder else defaultSortOrder,
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
                        sortOrder = if (state.sortKey == GetFilesQuerySortBy.ID) state.sortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetFilesQuerySortBy.ID)
                        },
                        onSearchClick = {
                            viewModel.updateSearchKey(GetFilesQuerySearchField.ID)
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

                val coroutineScope = rememberCoroutineScope()

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

                val rows: List<TableRowData> = state.files?.map { file ->
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
                                    },
                                    onDownload = {
                                        viewModel.setDownloadFilePassword(it)

                                        viewModel.performDownload(
                                            context = context,
                                            file = file,
                                            uri = state.selectedUri!!,
                                            password = it,
                                            sendNotification = { content ->
                                                Notification.show(
                                                    activity = activity,
                                                    content = content
                                                )
                                            }
                                        )
                                    }
                                )

                                val directoryPicker = rememberLauncherForActivityResult(
                                    contract = ActivityResultContracts.OpenDocumentTree()
                                ) { uri: Uri? ->
                                    uri?.let {
                                        context.contentResolver.takePersistableUriPermission(
                                            it,
                                            Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                        )

                                        viewModel.setSelectedUri(context, it)

                                        viewModel.performDownload(
                                            context = context,
                                            file = file,
                                            uri = uri,
                                            password = state.downloadFilePassword,
                                            sendNotification = { content ->
                                                Notification.show(
                                                    activity = activity,
                                                    content = content
                                                )
                                            }
                                        )
                                    }
                                }

                                IconButton(
                                    icon = painterResource(R.drawable.download),
                                    iconContentDescription = "Download file",
                                    color = DarkGray,
                                    iconColor = White,
                                    onClick = {
                                        if (state.selectedUri == null) {
                                            directoryPicker.launch(null)

                                            return@IconButton
                                        }

                                        viewModel.performDownload(
                                            context = context,
                                            file = file,
                                            uri = state.selectedUri!!,
                                            password = state.downloadFilePassword,
                                            sendNotification = { content ->
                                                Notification.show(
                                                    activity = activity,
                                                    content = content
                                                )
                                            }
                                        )
                                    },
                                    enabled = !state.isLoading
                                )

                                ActionButtonSpacer()

                                IconButton(
                                    icon = painterResource(R.drawable.delete),
                                    iconContentDescription = "Delete File",
                                    color = MaterialTheme.colorScheme.error,
                                    iconColor = MaterialTheme.colorScheme.onError,
                                    onClick = {
                                        viewModel.setDeleteFile(file)
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
                    )
            ) {
                if (state.files == null || state.isLoading) {
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
                    state.files?.let { files ->
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
            onFirstPageClick = {
                viewModel.setCurrentPage(1L)
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
            currentPage = state.currentPage,
            totalPages = state.totalPages,
            enabled = !state.isLoading
        )
    }
}