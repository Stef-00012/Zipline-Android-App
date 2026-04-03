package com.stefdp.zipline.screens.files

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.navigation.NavHostController
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.LocalLoggedUser
import com.stefdp.zipline.Logger
import com.stefdp.zipline.R
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
import com.stefdp.zipline.network.models.File
import com.stefdp.zipline.network.models.IncompleteFile
import com.stefdp.zipline.network.models.Tag
import com.stefdp.zipline.network.models.UserRole
import com.stefdp.zipline.network.models.requests.GetFilesQuerySearchField
import com.stefdp.zipline.network.models.requests.GetFilesQuerySortBy
import com.stefdp.zipline.network.requests.deleteFile
import com.stefdp.zipline.network.requests.downloadFile
import com.stefdp.zipline.network.requests.getFiles
import com.stefdp.zipline.network.requests.getIncompleteFiles
import com.stefdp.zipline.network.requests.getTags
import com.stefdp.zipline.network.requests.getUser
import com.stefdp.zipline.screens.HomeScreen
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.screens.UploadFileScreen
import com.stefdp.zipline.screens.files.components.IconButton
import com.stefdp.zipline.screens.files.components.PendingFile
import com.stefdp.zipline.screens.files.components.tags.TagsPopup
import com.stefdp.zipline.ui.theme.DarkGray
import com.stefdp.zipline.ui.theme.White
import com.stefdp.zipline.ui.theme.Yellow
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.SecureStorage
import com.stefdp.zipline.utils.SortOrder
import com.stefdp.zipline.utils.StorageUtil
import com.stefdp.zipline.utils.camelCaseToHumanReadable
import com.stefdp.zipline.utils.canInteract
import com.stefdp.zipline.utils.formatBytes
import com.stefdp.zipline.utils.getDisplayPath
import com.stefdp.zipline.utils.shimmerable
import com.stefdp.zipline.utils.verticalLazyScrollbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nl.jacobras.humanreadable.HumanReadable
import kotlin.time.Instant

const val COMPACT_VIEW_FILE_COUNT = 20L
const val DETAILED_VIEW_FILE_COUNT = 15L

@Composable
fun FilesScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    userId: String? = null
) {
    val localLoggedUser = LocalLoggedUser.current

    val validRoles = listOf(
        UserRole.ADMIN,
        UserRole.SUPERADMIN
    )

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

    var username by remember { mutableStateOf<String?>(null) }
    var files by remember { mutableStateOf<List<File>?>(null) }

    var incompleteFiles by remember { mutableStateOf<List<IncompleteFile>?>(null) }

    var tags by remember { mutableStateOf<List<Tag>?>(null) }

    var serverUrl by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(true) }
    var tagsLoading by remember { mutableStateOf(true) }

    var manageTagsPopupOpen by remember { mutableStateOf(false) }
    var pendingFilesPopupOpen by remember { mutableStateOf(false) }

    var favoriteFilter by remember { mutableStateOf(false) }
    var compactView by remember { mutableStateOf(false) }

    var currentPage by remember { mutableLongStateOf(1L) }
    var totalPages by remember { mutableLongStateOf(1L) }

    val filesPerPage = if (compactView) COMPACT_VIEW_FILE_COUNT else DETAILED_VIEW_FILE_COUNT

    var sortKey by remember { mutableStateOf(GetFilesQuerySortBy.CREATED_AT) }
    var sortOrder by remember { mutableStateOf(SortOrder.DESC) }
    val defaultSortOrder = SortOrder.UNSPECIFIED
    
    var searchKey by remember { mutableStateOf<GetFilesQuerySearchField?>(null) }
    var searchValue by remember { mutableStateOf(TextFieldValue("")) }

    suspend fun updateFiles() {
        isLoading = true

        if (userId != null && localLoggedUser?.role in validRoles) {
            val userRes = getUser(
                context = context,
                userId = userId,
            )

            userRes.onSuccess {
                username = it.username

                if (!canInteract(localLoggedUser?.role, it.role)) {
                    navController.navigate(HomeScreen) {
                        popUpTo(HomeScreen) { inclusive = true }
                    }

                    return@updateFiles
                }
            }

            val userFilesRes = getFiles(
                context = context,
                userId = userId,
                perPage = filesPerPage,
                page = currentPage,
                filterFavorite = favoriteFilter,
                sortOrder = sortOrder,
                sortBy = sortKey,
                searchField = searchKey,
                searchQuery = searchValue.text.ifEmpty { null }
            )

            userFilesRes.onSuccess {
                files = it.page
                totalPages = it.pages ?: 1L
            }
        } else {
            val userFilesRes = getFiles(
                context = context,
                perPage = filesPerPage,
                page = currentPage,
                filterFavorite = favoriteFilter,
                sortOrder = sortOrder,
                sortBy = sortKey,
                searchField = searchKey,
                searchQuery = searchValue.text.ifEmpty { null }
            )

            userFilesRes.onSuccess {
                files = it.page
                totalPages = it.pages ?: 1L
            }
        }

        isLoading = false
    }

    suspend fun updateIncompleteFiles() {
        val incompleteFilesRes = getIncompleteFiles(
            context = context,
        )

        incompleteFilesRes.onSuccess {
            incompleteFiles = it
        }
    }

    suspend fun updateTags() {
        tagsLoading = true

        val tagsRes = getTags(
            context = context,
        )

        tagsRes.onSuccess {
            tags = it
        }

        tagsLoading = false
    }

    LaunchedEffect(Unit) {
        val secureStore = SecureStorage.getInstance(context)
        serverUrl = secureStore.get("serverUrl")

        updateIncompleteFiles()
        updateTags()
    }

    TagsPopup(
        showPopup = manageTagsPopupOpen,
        onDismissRequest = {
            manageTagsPopupOpen = false
        },
        tags = tags ?: emptyList(),
        updateTags = ::updateTags,
        context = context,
        activity = activity
    )

    Popup(
        showPopup = pendingFilesPopupOpen,
        onDismissRequest = {
            pendingFilesPopupOpen = false
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
                            pendingFilesPopupOpen = false
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

        if (incompleteFiles != null && incompleteFiles!!.isNotEmpty()) {
            var pendingFilesLoading by remember { mutableStateOf(false) }

            val lazyListState = rememberLazyListState()

            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalLazyScrollbar(
                        listState = lazyListState,
                    )
            ) {
                items(incompleteFiles!!.size) {
                    val incompleteFile = incompleteFiles!![it]

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    PendingFile(
                        context = context,
                        activity = activity,
                        file = incompleteFile,
                        updateData = ::updateIncompleteFiles,
                        updateIsLoading = { pendingFilesLoading = it },
                        isLoading = pendingFilesLoading,
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
            val titleText = if (username != null) "$username's Files" else "Files"
            var isTitleOverflowing by remember { mutableStateOf(false) }

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
                                context = context,
                                activity = activity,
                                content = {
                                    Text(titleText)
                                }
                            )
                        }
                    ),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderButton(
                    icon = if (favoriteFilter)
                        painterResource(R.drawable.star_filled)
                    else   painterResource(R.drawable.star),
                    contentDescription = "Favorites filter",
                    enabled = !isLoading,
                    onClick = {
                        favoriteFilter = !favoriteFilter
                    },
                    iconColor = if (favoriteFilter) Yellow else MaterialTheme.colorScheme.primary
                )

                if (userId == null) {
                    Spacer(
                        modifier = Modifier.width(5.dp)

                    )
                    HeaderButton(
                        icon = painterResource(R.drawable.upload_file),
                        contentDescription = "Upload file",
                        onClick = {
                            navController.navigate(UploadFileScreen)
                        },
                        enabled = !isLoading,
                    )
                }

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

                if (userId == null) {
                    Spacer(
                        modifier = Modifier.width(5.dp)
                    )

                    var dropdownExpanded by remember { mutableStateOf(false) }

                    Box(
                        modifier = Modifier.wrapContentSize(Alignment.TopStart)
                    ) {
                        HeaderButton(
                            icon = painterResource(R.drawable.more_horiz),
                            contentDescription = "More actions",
                            onClick = { dropdownExpanded = true },
                            enabled = !isLoading,
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
                                    manageTagsPopupOpen = true
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
                                    pendingFilesPopupOpen = true
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

        var clickedFile by remember { mutableStateOf<File?>(null) }

        LaunchedEffect(
            filesPerPage,
            currentPage,
            favoriteFilter,
            sortOrder,
            sortKey,
            searchValue,
        ) {
            updateFiles()
            lazyColumnListState.animateScrollToItem(0)
            clickedFile = null
        }

        LaunchedEffect(files) {
            if (clickedFile != null) {
                val updatedFile = files?.find { it.id == clickedFile?.id }

                if (updatedFile != null) {
                    clickedFile = updatedFile
                }
            }
        }

        LargeFileDisplay(
            context = context,
            activity = activity,
            file = clickedFile,
            onDismissRequest = { clickedFile = null },
            updateData = ::updateFiles,
            tags = tags,
            onDelete = { clickedFile = null }
        )

        if (compactView) {
            var _searchValue by remember { mutableStateOf(searchValue) }

            LaunchedEffect(searchKey) {
                _searchValue = TextFieldValue("")
            }

            if (searchKey != null) {
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
                            text = "Search by ${camelCaseToHumanReadable(searchKey.toString())}",
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
                                        searchKey = null
                                        searchValue = TextFieldValue("")
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

                    if (searchKey == GetFilesQuerySearchField.TAGS) {
                        var selectedTagIds by remember { mutableStateOf(emptySet<String>()) }

                        Select(
                            label = "Tags",
                            multiple = true,
                            enabled = !tagsLoading && tags?.isNotEmpty() ?: false,
                            options = tags?.map { tag ->
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
                            selectedIds = selectedTagIds,
                            onSelectionChange = { newSelectedIds ->
                                if (newSelectedIds == selectedTagIds) return@Select

                                selectedTagIds = newSelectedIds
                                searchValue = TextFieldValue(
                                    "," + newSelectedIds.joinToString(",")
                                )
                            }
                        )
                    } else {
                        fun onEnter() {
                            searchValue = _searchValue
                        }

                        val focusManager = LocalFocusManager.current

                        TextInput(
                            value = _searchValue,
                            onValueChange = { _searchValue = it },
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

                val headers: List<TableHeaderData> = listOf(
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Name",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableNameWidth,
                        name = "name",
                        searchable = true,
                        sortable = true,
                        sortOrder = if (sortKey == GetFilesQuerySortBy.NAME) sortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (sortKey != GetFilesQuerySortBy.NAME) {
                                sortKey = GetFilesQuerySortBy.NAME
                                sortOrder = SortOrder.ASC
                            } else if (sortOrder == SortOrder.UNSPECIFIED || sortOrder == SortOrder.DESC) {
                                sortOrder = SortOrder.ASC
                            } else {
                                sortOrder = SortOrder.DESC
                            }
                        },
                        onSearchClick = {
                            searchKey = GetFilesQuerySearchField.NAME
                        }
                    ),
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Tags",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableTagsWidth,
                        name = "tags",
                        searchable = true,
                        onSearchClick = {
                            searchKey = GetFilesQuerySearchField.TAGS
                        },
                        searchEnabled = !tagsLoading && tags?.isNotEmpty() ?: false
                    ),
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Type",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableTypeWidth,
                        name = "type",
                        searchable = true,
                        sortable = true,
                        sortOrder = if (sortKey == GetFilesQuerySortBy.TYPE) sortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (sortKey != GetFilesQuerySortBy.TYPE) {
                                sortKey = GetFilesQuerySortBy.TYPE
                                sortOrder = SortOrder.ASC
                            } else if (sortOrder == SortOrder.UNSPECIFIED || sortOrder == SortOrder.DESC) {
                                sortOrder = SortOrder.ASC
                            } else {
                                sortOrder = SortOrder.DESC
                            }
                        },
                        onSearchClick = {
                            searchKey = GetFilesQuerySearchField.TYPE
                        }
                    ),
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Size",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableSizeWidth,
                        name = "size",
                        sortable = true,
                        sortOrder = if (sortKey == GetFilesQuerySortBy.SIZE) sortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (sortKey != GetFilesQuerySortBy.SIZE) {
                                sortKey = GetFilesQuerySortBy.SIZE
                                sortOrder = SortOrder.ASC
                            } else if (sortOrder == SortOrder.UNSPECIFIED || sortOrder == SortOrder.DESC) {
                                sortOrder = SortOrder.ASC
                            } else {
                                sortOrder = SortOrder.DESC
                            }
                        }
                    ),
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Created At",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableCreatedAtWidth,
                        name = "created at",
                        sortable = true,
                        sortOrder = if (sortKey == GetFilesQuerySortBy.CREATED_AT) sortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (sortKey != GetFilesQuerySortBy.CREATED_AT) {
                                sortKey = GetFilesQuerySortBy.CREATED_AT
                                sortOrder = SortOrder.ASC
                            } else if (sortOrder == SortOrder.UNSPECIFIED || sortOrder == SortOrder.DESC) {
                                sortOrder = SortOrder.ASC
                            } else {
                                sortOrder = SortOrder.DESC
                            }
                        }
                    ),
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Favorite",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableFavoriteWidth,
                        name = "favorite",
                        sortable = true,
                        sortOrder = if (sortKey == GetFilesQuerySortBy.FAVORITE) sortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (sortKey != GetFilesQuerySortBy.FAVORITE) {
                                sortKey = GetFilesQuerySortBy.FAVORITE
                                sortOrder = SortOrder.ASC
                            } else if (sortOrder == SortOrder.UNSPECIFIED || sortOrder == SortOrder.DESC) {
                                sortOrder = SortOrder.ASC
                            } else {
                                sortOrder = SortOrder.DESC
                            }
                        }
                    ),
                    TableHeaderData(
                        content = {
                            Text(
                                text = "ID",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableIdWidth,
                        name = "ID",
                        searchable = true,
                        sortable = true,
                        sortOrder = if (sortKey == GetFilesQuerySortBy.ID) sortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (sortKey != GetFilesQuerySortBy.ID) {
                                sortKey = GetFilesQuerySortBy.ID
                                sortOrder = SortOrder.ASC
                            } else if (sortOrder == SortOrder.UNSPECIFIED || sortOrder == SortOrder.DESC) {
                                sortOrder = SortOrder.ASC
                            } else {
                                sortOrder = SortOrder.DESC
                            }
                        },
                        onSearchClick = {
                            searchKey = GetFilesQuerySearchField.ID
                        }
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

                var deleteFile by remember { mutableStateOf<File?>(null) }
                val coroutineScope = rememberCoroutineScope()

                PromptPopup(
                    showPopup = deleteFile != null,
                    onDismissRequest = { deleteFile = null },
                    onCancel = { deleteFile = null },
                    isLoading = isLoading,
                    title = "Are you sure?",
                    description = "Are you sure you want to delete ${deleteFile?.originalName ?: deleteFile?.name}? This action cannot be undone.",
                    onSuccess = {
                        coroutineScope.launch {
                            if (deleteFile == null) return@launch

                            isLoading = true

                            val deleteRes = deleteFile(
                                context = context,
                                fileId = deleteFile!!.id
                            )

                            deleteRes
                                .onSuccess {
                                    updateFiles()
                                }
                                .onFailure {
                                    Notification.show(
                                        context = context,
                                        activity = activity,
                                        content = {
                                            Text(
                                                text = "Failed to delete file: ${it.message}"
                                            )
                                        },
                                    )
                                }

                            deleteFile = null

                            isLoading = false
                        }
                    }
                )

                val rows: List<TableRowData> = files?.map { file ->
                    TableRowData(
                        clickable = true,
                        onClick = { clickedFile = file },
                        cells = listOf(
                            TableCellData(
                                content = {
                                    Text(
                                        text = file.name,
                                    )
                                },
                                width = tableNameWidth,
                            ),
                            TableCellData(
                                content = {
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
                                width = tableTagsWidth,
                            ),
                            TableCellData(
                                content = {
                                    Text(
                                        text = file.type,
                                    )
                                },
                                width = tableTypeWidth,
                            ),
                            TableCellData(
                                content = {
                                    Text(
                                        text = formatBytes(file.size),
                                    )
                                },
                                width = tableSizeWidth,
                            ),
                            TableCellData(
                                content = {
                                    Text(
                                        text = HumanReadable.timeAgo(Instant.parse(file.createdAt)),
                                    )
                                },
                                width = tableCreatedAtWidth,
                            ),
                            TableCellData(
                                content = {
                                    Text(
                                        text = if (file.favorite) "Yes" else "No",
                                        color = if (file.favorite) Yellow else MaterialTheme.colorScheme.onBackground
                                    )
                                },
                                width = tableFavoriteWidth,
                            ),
                            TableCellData(
                                content = {
                                    Text(
                                        text = file.id,
                                    )
                                },
                                width = tableIdWidth,
                            ),
                            TableCellData(
                                content = {
                                    @Composable
                                    fun ActionButtonSpacer() {
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }

                                    IconButton(
                                        icon = painterResource(R.drawable.draft),
                                        iconContentDescription = "More details",
                                        color = MaterialTheme.colorScheme.primary,
                                        iconColor = MaterialTheme.colorScheme.onPrimary,
                                        onClick = { clickedFile = file },
                                        enabled = !isLoading
                                    )

                                    ActionButtonSpacer()

                                    IconButton(
                                        icon = painterResource(R.drawable.open_new),
                                        iconContentDescription = "Open file in browser",
                                        color = MaterialTheme.colorScheme.primary,
                                        iconColor = MaterialTheme.colorScheme.onPrimary,
                                        onClick = {
                                            val fileUrl = "${serverUrl}${file.url}"

                                            val intent = Intent(Intent.ACTION_VIEW, fileUrl.toUri())
                                            context.startActivity(intent)
                                        },
                                        enabled = serverUrl != null && !isLoading
                                    )

                                    ActionButtonSpacer()

                                    val clipboardManager = LocalClipboard.current

                                    IconButton(
                                        icon = painterResource(R.drawable.content_copy),
                                        iconContentDescription = "Copy file link",
                                        color = MaterialTheme.colorScheme.primary,
                                        iconColor = MaterialTheme.colorScheme.onPrimary,
                                        onClick = {
                                            val fileUrl = "${serverUrl}${file.url}"

                                            val clipData = ClipData.newRawUri("File URL", fileUrl.toUri()).toClipEntry()

                                            coroutineScope.launch {
                                                clipboardManager.setClipEntry(clipData)

                                                Notification.show(
                                                    context = context,
                                                    activity = activity,
                                                    content = {
                                                        Text(
                                                            text = "File link copied to clipboard"
                                                        )
                                                    }
                                                )
                                            }
                                        },
                                        enabled = serverUrl != null && !isLoading
                                    )

                                    ActionButtonSpacer()

                                    var selectedUri by remember { mutableStateOf<Uri?>(null) }
                                    var selectedPath by remember { mutableStateOf<String?>(null) }

                                    LaunchedEffect(Unit) {
                                        val secureStore = SecureStorage.getInstance(context)

                                        val fileDownloadFolder = secureStore.get("fileDownloadFolder")

                                        if (fileDownloadFolder != null) {
                                            selectedUri = fileDownloadFolder.toUri()
                                            selectedPath = getDisplayPath(fileDownloadFolder.toUri())
                                        }
                                    }

                                    fun showToast(message: String) {
                                        coroutineScope.launch(Dispatchers.Main) {
                                            Notification.show(
                                                context = context,
                                                activity = activity,
                                                content = {
                                                    Text(message)
                                                }
                                            )
                                        }
                                    }

                                    var downloadFilePassword by remember { mutableStateOf<String?>(null) }
                                    var fileRequiresPassword by remember { mutableStateOf(false) }

                                    fun performDownload() {
                                        val fileFits = StorageUtil.canFitFile(
                                            context = context,
                                            uri = selectedUri!!,
                                            fileSize = file.size
                                        )

                                        if (!fileFits) {
                                            showToast("Not enough space in the selected directory to download the file")

                                            return
                                        }

                                        val fileFitsCache = StorageUtil.canFitInternalCache(
                                            context = context,
                                            fileSize = file.size
                                        )

                                        if (!fileFitsCache) {
                                            showToast("Not enough space in the internal cache to download the file")

                                            return
                                        }

                                        if (file.password == true && downloadFilePassword.isNullOrBlank()) {
                                            fileRequiresPassword = true
                                            return
                                        }

                                        coroutineScope.launch(Dispatchers.IO) {
                                            showToast("Starting download...")

                                            val fileName = file.originalName ?: file.name

                                            val tempFile = java.io.File(context.cacheDir, fileName)
                                            val tempDestinationPath = tempFile.absolutePath

                                            if (tempFile.exists()) tempFile.delete()

                                            val downloadRes = downloadFile(
                                                context = context,
                                                fileId = file.id,
                                                destinationPath = tempDestinationPath,
                                                notificationTitle = "Downloading file",
                                                notificationContent = "Downloading ${file.name}",
                                                password = downloadFilePassword
                                            )

                                            downloadRes
                                                .onSuccess {
                                                    try {
                                                        val docUri =
                                                            DocumentsContract.buildDocumentUriUsingTree(
                                                                selectedUri,
                                                                DocumentsContract.getTreeDocumentId(
                                                                    selectedUri
                                                                )
                                                            )

                                                        val fileUri = DocumentsContract.createDocument(
                                                            context.contentResolver,
                                                            docUri,
                                                            file.type,
                                                            fileName
                                                        )

                                                        if (fileUri != null) {
                                                            context.contentResolver.openOutputStream(fileUri)
                                                                ?.use { out ->
                                                                    tempFile.inputStream().use { inp ->
                                                                        inp.copyTo(out)
                                                                    }
                                                                }

                                                            showToast("File downloaded to ${selectedPath}/$fileName")
                                                        } else {
                                                            showToast("Failed to create file in selected directory")
                                                        }
                                                    } catch (e: Exception) {
                                                        Logger.error(
                                                            "LargeFileDisplay",
                                                            "Failed to copy file to selected directory",
                                                            e
                                                        )

                                                        showToast("Failed to copy file to selected directory: ${e.message}")
                                                    } finally {
                                                        tempFile.delete()
                                                    }
                                                }
                                                .onFailure {
                                                    Logger.error("LargeFileDisplay", "Failed to download file", it)

                                                    showToast("Failed to download file: ${it.message}")
                                                }

                                            fileRequiresPassword = false
                                            downloadFilePassword = null
                                        }
                                    }

                                    DownloadFilePasswordPrompt(
                                        context = context,
                                        activity = activity,
                                        fileId = file.id,
                                        showPopup = fileRequiresPassword && downloadFilePassword == null,
                                        onDismissRequest = {
                                            fileRequiresPassword = false
                                            downloadFilePassword = null
                                        },
                                        onDownload = {
                                            downloadFilePassword = it

                                            performDownload()
                                        }
                                    )

                                    val directoryPicker = rememberLauncherForActivityResult(
                                        contract = ActivityResultContracts.OpenDocumentTree()
                                    ) { uri: Uri? ->
                                        uri?.let {
                                            val secureStore = SecureStorage.getInstance(context)

                                            context.contentResolver.takePersistableUriPermission(
                                                it,
                                                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                            )
                                            selectedUri = it
                                            selectedPath = getDisplayPath(it)

                                            coroutineScope.launch {
                                                secureStore.set("fileDownloadFolder", selectedUri.toString())
                                            }

                                            performDownload()
                                        }
                                    }

                                    IconButton(
                                        icon = painterResource(R.drawable.download),
                                        iconContentDescription = "Download file",
                                        color = DarkGray,
                                        iconColor = White,
                                        onClick = {
                                            if (selectedUri == null) {
                                                directoryPicker.launch(null)

                                                return@IconButton
                                            }

                                            performDownload()
                                        },
                                        enabled = serverUrl != null && !isLoading
                                    )

                                    ActionButtonSpacer()

                                    IconButton(
                                        icon = painterResource(R.drawable.delete),
                                        iconContentDescription = "Open file in browser",
                                        color = MaterialTheme.colorScheme.error,
                                        iconColor = MaterialTheme.colorScheme.onError,
                                        onClick = {
                                            deleteFile = file
                                        },
                                        enabled = serverUrl != null && !isLoading
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
                    )
            ) {
                if (files == null || isLoading) {
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
                    files?.let { files ->
                        if (files.size > 0L) {
                            items(files.size) { index ->
                                FilePreview(
                                    file = files[index],
                                    context = context,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                        .padding(5.dp),
                                    onClick = {
                                        clickedFile = files[index]
                                    },
                                    serverUrl = serverUrl,
                                )
                            }
                        }
                    }
                }
            }
        }

        Pager(
            onFirstPageClick = {
                currentPage = 1L
            },
            onPreviousPageClick = {
                if (currentPage > 1) currentPage--
            },
            onCustomPageInput = {
                page -> currentPage = page.coerceIn(1, totalPages)
            },
            onNextPageClick = {
                if (currentPage < totalPages) currentPage++
            },
            onLastPageClick = {
                currentPage = totalPages
            },
            currentPage = currentPage,
            totalPages = totalPages,
            enabled = !isLoading
        )
    }
}