package com.stefdp.zipline.screens.urls

import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.stefdp.zipline.LocalWebSettings
import com.stefdp.zipline.Logger
import com.stefdp.zipline.R
import com.stefdp.zipline.components.PromptPopup
import com.stefdp.zipline.components.HeaderButton
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.components.table.Table
import com.stefdp.zipline.components.table.TableCellData
import com.stefdp.zipline.components.table.TableHeaderData
import com.stefdp.zipline.components.table.TableRowData
import com.stefdp.zipline.components.table.TableScrollbarConfig
import com.stefdp.zipline.network.models.requests.GetUrlsQuerySearchField
import com.stefdp.zipline.network.requests.deleteUrl
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.components.IconButton
import com.stefdp.zipline.screens.urls.components.CreateUrlPopup
import com.stefdp.zipline.screens.urls.components.EditUrlPopup
import com.stefdp.zipline.components.EnabledCheckbox
import com.stefdp.zipline.screens.urls.components.LargeUrlDisplay
import com.stefdp.zipline.components.QRCodePopup
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.SortOrder
import com.stefdp.zipline.utils.ZiplineViewStateType
import com.stefdp.zipline.utils.camelCaseToHumanReadable
import com.stefdp.zipline.utils.shimmerable
import com.stefdp.zipline.utils.verticalLazyScrollbar
import kotlinx.coroutines.launch
import nl.jacobras.humanreadable.HumanReadable
import kotlin.time.Instant

const val VIEW_STATE_KEY = "urlsViewState"

@Composable
fun UrlsScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    sharedUrl: String? = null,
    viewModel: UrlsViewModel = viewModel()
) {
    val localLoggedUser = LocalLoggedUser.current

    if (localLoggedUser == null) {
        navController.navigate(LoginScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    val state by viewModel.state.collectAsState()

    val webSettings = LocalWebSettings.current

    val urlsRoute = webSettings?.config?.urls?.route.takeIf { it != "/" } ?: ""

    val screenViewState = LocalScreenViewState.current
    val updateScreenViewState = LocalUpdateScreenViewState.current

    val viewState = screenViewState.urls

    val defaultSortOrder = SortOrder.UNSPECIFIED

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.initData(context, urlsRoute, sharedUrl)
    }

    LaunchedEffect(state.searchValue) {
        viewModel.refreshUrls(context, viewState)
    }

    LaunchedEffect(state.sortOrder, state.sortKey, viewState) {
        viewModel.triggerSort(viewState)
    }

    PromptPopup(
        showPopup = state.deleteUrl != null,
        onDismissRequest = { viewModel.setDeleteUrl(null) },
        onCancel = { viewModel.setDeleteUrl(null) },
        isLoading = state.isLoading,
        title = "Are you sure?",
        description = "Are you sure you want to delete ${state.deleteUrl?.code}? This action cannot be undone.",
        onSuccess = {
            coroutineScope.launch {
                if (state.deleteUrl == null) return@launch

                viewModel.deleteUrl(
                    context = context,
                    urlId = state.deleteUrl!!.id,
                    viewState = viewState,
                    onSuccess = {
                        Notification.show(
                            context = context,
                            activity = activity,
                        ) {
                            Text(
                                text = "URL deleted successfully"
                            )
                        }
                    },
                    onError = { error ->
                        Notification.show(
                            context = context,
                            activity = activity,
                        ) {
                            Text(
                                text = "Failed to delete URL: ${error}"
                            )
                        }
                    }
                )
            }
        }
    )

    CreateUrlPopup(
        context = context,
        activity = activity,
        showPopup = state.isCreatePopupOpen,
        isLoading = state.popupIsLoading,
        onDismissRequest = {
            Notification.show(
                context = context,
                activity = activity,
            ) {
                Text(
                    text = "URL created successfully"
                )
            }

            viewModel.closeCreatePopup()
        },
        createdUrl = state.createdUrlResult,
        onDismissCreatedUrlRequest = {
            viewModel.clearCreatedUrlResult()
        },
        baseUrl = state.createPopupBaseUrl,
        onCreate = { destination, vanity, enabled, maxViews, password, domain ->
            viewModel.createUrl(
                context = context,
                destination = destination,
                vanity = vanity,
                maxViews = maxViews,
                password = password,
                enabled = enabled,
                domain = domain,
                viewState = viewState,
                onSuccess = {
                    viewModel.closeCreatePopup()
                },
                onError = { error ->
                    Notification.show(
                        context = context,
                        activity = activity,
                    ) {
                        Text(
                            text = "Failed to create URL: $error"
                        )
                    }
                }
            )
        }
    )

    EditUrlPopup(
        context = context,
        activity = activity,
        url = state.editUrl,
        showPopup = state.editUrl != null,
        isLoading = state.popupIsLoading,
        onDismissRequest = { viewModel.setEditUrl(null) },
        onEdit = { urlId, destination, vanity, enabled, maxViews, password ->
            viewModel.editUrl(
                context = context,
                urlId = urlId,
                destination = destination,
                vanity = vanity,
                maxViews = maxViews,
                password = password,
                enabled = enabled,
                viewState = viewState,
                onSuccess = {
                    Notification.show(
                        context = context,
                        activity = activity,
                    ) {
                        Text(
                            text = "URL updated successfully"
                        )
                    }

                    viewModel.setEditUrl(null)
                },
                onError = { error ->
                    Notification.show(
                        context = context,
                        activity = activity,
                    ) {
                        Text(
                            text = "Failed to edit URL: $error"
                        )
                    }
                }
            )
        }
    )

    QRCodePopup(
        context = context,
        activity = activity,
        qrCodeText = state.qrCodeText,
        showPopup = state.qrCodeUrl != null && state.serverUrl != null,
        onDismissRequest = { viewModel.setQrCodeUrl(null) },
        downloadFileName = "QR_${state.qrCodeUrl?.id ?: "code"}.png"
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
                text = "URLs",
                maxLines = 1,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderButton(
                    icon = painterResource(R.drawable.add_link),
                    contentDescription = "Create URL",
                    onClick = {
                        viewModel.openCreatePopup()
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
                                urls = newState
                            )
                        )
                    },
                    enabled = !state.isLoading,
                )
            }
        }

        val lazyColumnListState = rememberLazyListState()

//        LaunchedEffect(viewState) {
//            if (viewState == ZiplineViewStateType.COMPACT) return@LaunchedEffect
//
//            if (
//                (searchKey != null && searchValue.text.isNotBlank()) ||
//                (sortKey != GetUrlsQuerySortBy.CREATED_AT && sortOrder != SortOrder.DESC)
//            ) {
//                updateUrls(
//                    search = false,
//                    sort = false
//                )
//            }
//        }

        if (viewState == ZiplineViewStateType.COMPACT) {
            var uiSearchValue by rememberSaveable(
                stateSaver = TextFieldValue.Saver
            ) { mutableStateOf(state.searchValue) }

            LaunchedEffect(state.searchKey) { uiSearchValue = TextFieldValue("") }

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
                                        viewModel.updateSearchValue(TextFieldValue(""))
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
                val tableCodeWidth = 150.dp
                val tableVanityWidth = 150.dp
                val tableDestinationWidth = 300.dp
                val tableViewsWidth = 120.dp
                val tableMaxViewsWidth = 150.dp
                val tableCreatedWidth = 150.dp
                val tableEnabledWidth = 120.dp
                val tableActionsWidth = 170.dp

                fun onSortChanged(sortKey: GetUrlsQuerySortBy) {
                    if (state.sortKey != sortKey || state.sortOrder == SortOrder.UNSPECIFIED || state.sortOrder == SortOrder.DESC) {
                        viewModel.updateSort(sortKey, SortOrder.ASC)
                    } else {
                        viewModel.updateSort(sortKey, SortOrder.DESC)
                    }
                }

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
                        searchable = true,
                        sortable = true,
                        sortOrder = if (state.sortKey == GetUrlsQuerySortBy.CODE) state.sortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetUrlsQuerySortBy.CODE)
                        },
                        onSearchClick = {
                            viewModel.updateSearchKey(GetUrlsQuerySearchField.CODE)
                        }
                    ),
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Vanity",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableVanityWidth,
                        name = "vanity",
                        searchable = true,
                        sortable = true,
                        sortOrder = if (state.sortKey == GetUrlsQuerySortBy.VANITY) state.sortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetUrlsQuerySortBy.VANITY)
                        },
                        onSearchClick = {
                            viewModel.updateSearchKey(GetUrlsQuerySearchField.VANITY)
                        }
                    ),
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Destination",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableDestinationWidth,
                        name = "destination",
                        searchable = true,
                        sortable = true,
                        sortOrder = if (state.sortKey == GetUrlsQuerySortBy.DESTINATION) state.sortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetUrlsQuerySortBy.DESTINATION)
                        },
                        onSearchClick = {
                            viewModel.updateSearchKey(GetUrlsQuerySearchField.DESTINATION)
                        }
                    ),
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Views",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableViewsWidth,
                        name = "views",
                        sortable = true,
                        sortOrder = if (state.sortKey == GetUrlsQuerySortBy.VIEWS) state.sortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetUrlsQuerySortBy.VIEWS)
                        }
                    ),
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Max Views",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableMaxViewsWidth,
                        name = "max views",
                        sortable = true,
                        sortOrder = if (state.sortKey == GetUrlsQuerySortBy.MAX_VIEWS) state.sortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetUrlsQuerySortBy.MAX_VIEWS)
                        }
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
                        sortOrder = if (state.sortKey == GetUrlsQuerySortBy.CREATED_AT) state.sortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetUrlsQuerySortBy.CREATED_AT)
                        },
                    ),
                    TableHeaderData(
                        content = {
                            Text(
                                text = "Enabled",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableEnabledWidth,
                        name = "enabled",
                        sortable = true,
                        sortOrder = if (state.sortKey == GetUrlsQuerySortBy.ENABLED) state.sortOrder else defaultSortOrder,
                        onSortChanged = {
                            onSortChanged(GetUrlsQuerySortBy.ENABLED)
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

                val rows: List<TableRowData> = state.urls?.map { url ->
                    TableRowData(
                        clickable = true,
                        cells = listOf(
                            TableCellData(
                                content = {
                                    Text(
                                        text = url.code,
                                        color = MaterialTheme.colorScheme.tertiary,
                                        textDecoration = TextDecoration.Underline,
                                        modifier = Modifier.clickable(
                                            enabled = state.serverUrl != null,
                                            onClick = {
                                                val urlUrl = "${state.serverUrl}${urlsRoute}/${url.code}"

                                                val intent = Intent(Intent.ACTION_VIEW, urlUrl.toUri())
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
                                        text = url.vanity ?: "",
                                        color = MaterialTheme.colorScheme.tertiary,
                                        textDecoration = TextDecoration.Underline,
                                        modifier = Modifier.clickable(
                                            enabled = state.serverUrl != null && !url.vanity.isNullOrBlank(),
                                            onClick = {
                                                val urlUrl = "${state.serverUrl}${urlsRoute}/${url.vanity}"

                                                val intent = Intent(Intent.ACTION_VIEW, urlUrl.toUri())
                                                context.startActivity(intent)
                                            }
                                        )
                                    )
                                },
                                width = tableVanityWidth,
                            ),
                            TableCellData(
                                content = {
                                    Text(
                                        text = url.destination,
                                        color = MaterialTheme.colorScheme.tertiary,
                                        textDecoration = TextDecoration.Underline,
                                        modifier = Modifier.clickable(
                                            enabled = state.serverUrl != null,
                                            onClick = {
                                                val intent = Intent(Intent.ACTION_VIEW, url.destination.toUri())
                                                context.startActivity(intent)
                                            }
                                        )
                                    )
                                },
                                width = tableDestinationWidth,
                            ),
                            TableCellData(
                                content = {
                                    Text(
                                        text = url.views.toString(),
                                    )
                                },
                                width = tableViewsWidth,
                            ),
                            TableCellData(
                                content = {
                                    Text(
                                        text = if (url.maxViews != null) url.maxViews.toString() else ""
                                    )
                                },
                                width = tableMaxViewsWidth,
                            ),
                            TableCellData(
                                content = {
                                    Text(
                                        text = HumanReadable.timeAgo(Instant.parse(url.createdAt)),
                                    )
                                },
                                width = tableCreatedWidth,
                            ),
                            TableCellData(
                                content = {
                                    EnabledCheckbox(
                                        enabled = url.enabled
                                    )
                                },
                                width = tableEnabledWidth,
                            ),
                            TableCellData(
                                content = {
                                    @Composable
                                    fun ActionButtonSpacer() {
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }

                                    val clipboardManager = LocalClipboard.current

                                    IconButton(
                                        icon = painterResource(R.drawable.content_copy),
                                        iconContentDescription = "Copy URL",
                                        color = MaterialTheme.colorScheme.primary,
                                        iconColor = MaterialTheme.colorScheme.onPrimary,
                                        onClick = {
                                            coroutineScope.launch {
                                                val urlUrl = "${state.serverUrl}${urlsRoute}/${if (url.vanity.isNullOrBlank()) url.code else url.vanity}"

                                                val clipData = ClipData.newRawUri("URL", urlUrl.toUri()).toClipEntry()

                                                clipboardManager.setClipEntry(clipData)

                                                Notification.show(
                                                    context = context,
                                                    activity = activity,
                                                    content = {
                                                        Text(
                                                            text = "URL copied to clipboard"
                                                        )
                                                    }
                                                )
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
                                            viewModel.setQrCodeUrl(url)
                                        },
                                        enabled = state.serverUrl != null && !state.isLoading
                                    )

                                    ActionButtonSpacer()

                                    IconButton(
                                        icon = painterResource(R.drawable.edit),
                                        iconContentDescription = "Edit URL",
                                        color = MaterialTheme.colorScheme.primary,
                                        iconColor = MaterialTheme.colorScheme.onPrimary,
                                        onClick = { viewModel.setEditUrl(url) },
                                        enabled = state.serverUrl != null && !state.isLoading
                                    )

                                    ActionButtonSpacer()

                                    IconButton(
                                        icon = painterResource(R.drawable.delete),
                                        iconContentDescription = "Delete URL",
                                        color = MaterialTheme.colorScheme.error,
                                        iconColor = MaterialTheme.colorScheme.onError,
                                        onClick = {
                                            viewModel.setDeleteUrl(url)
                                        },
                                        enabled = state.serverUrl != null && !state.isLoading
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
                if (state.urls == null || state.isLoading) {
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
                    if (state.urls!!.size > 0L) {
                        items(state.urls!!.size) { index ->
                            val url = state.urls!![index]

                            LargeUrlDisplay(
                                context = context,
                                activity = activity,
                                url = url,
                                serverUrl = state.serverUrl,
                                urlsRoute = urlsRoute,
                                onDelete = { viewModel.setDeleteUrl(url) },
                                onEdit = { viewModel.setEditUrl(url) },
                                onShowQRCode = { viewModel.setQrCodeUrl(url) },
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
                                        contentDescription = "Link",
                                        modifier = Modifier.size(32.dp)
                                    )

                                    Text(
                                        text = "No URLs found",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                Text(
                                    text = "Shorten a URL to see them here.",
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