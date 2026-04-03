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
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.google.gson.annotations.SerializedName
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.LocalLoggedUser
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
import com.stefdp.zipline.network.models.Url
import com.stefdp.zipline.network.models.requests.GetUrlsQuerySearchField
import com.stefdp.zipline.network.requests.deleteUrl
import com.stefdp.zipline.network.requests.getUrls
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.screens.files.components.IconButton
import com.stefdp.zipline.screens.urls.components.CreateUrlPopup
import com.stefdp.zipline.screens.urls.components.EditUrlPopup
import com.stefdp.zipline.screens.urls.components.EnabledCheckbox
import com.stefdp.zipline.screens.urls.components.LargeUrlDisplay
import com.stefdp.zipline.components.QRCodePopup
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.SecureStorage
import com.stefdp.zipline.utils.SortOrder
import com.stefdp.zipline.utils.camelCaseToHumanReadable
import com.stefdp.zipline.utils.shimmerable
import com.stefdp.zipline.utils.verticalLazyScrollbar
import kotlinx.coroutines.launch
import nl.jacobras.humanreadable.HumanReadable
import kotlin.time.Instant

@Composable
fun UrlsScreen(
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

    val webSettings = LocalWebSettings.current

    val urlsRoute = webSettings?.config?.urls?.route.takeIf { it != "/" } ?: ""

    var urls by remember { mutableStateOf<List<Url>?>(null) }

    var serverUrl by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(true) }

    var compactView by remember { mutableStateOf(false) }

    var sortKey by remember { mutableStateOf(GetUrlsQuerySortBy.CREATED_AT) }
    var sortOrder by remember { mutableStateOf(SortOrder.DESC) }
    val defaultSortOrder = SortOrder.UNSPECIFIED

    var searchKey by remember { mutableStateOf<GetUrlsQuerySearchField?>(null) }
    var searchValue by remember { mutableStateOf(TextFieldValue("")) }

    var createdNewUrlPopupOpen by remember { mutableStateOf(false) }

    var deleteUrl by remember { mutableStateOf<Url?>(null) }
    var editUrl by remember { mutableStateOf<Url?>(null) }

    var qrCodeUrl by remember { mutableStateOf<Url?>(null) }
    var qrCodeText by remember { mutableStateOf("") }

    LaunchedEffect(qrCodeUrl) {
        if (qrCodeUrl == null) {
            qrCodeText = ""
            return@LaunchedEffect
        }

        qrCodeText = "${serverUrl}${urlsRoute}/${if (qrCodeUrl!!.vanity.isNullOrBlank()) qrCodeUrl!!.code else qrCodeUrl!!.vanity}"
    }

    fun sortUrls(urls: List<Url>): List<Url> {
        if (!compactView) return urls.sortedBy { Instant.parse(it.createdAt) }.reversed()

        val ascending = when (sortKey) {
            GetUrlsQuerySortBy.CODE ->
                urls.sortedBy { it.code }

            GetUrlsQuerySortBy.VANITY ->
                urls.sortedBy { it.vanity.orEmpty() }

            GetUrlsQuerySortBy.DESTINATION ->
                urls.sortedBy { it.destination }

            GetUrlsQuerySortBy.VIEWS ->
                urls.sortedBy { it.views }

            GetUrlsQuerySortBy.MAX_VIEWS ->
                urls.sortedBy { it.maxViews ?: Long.MIN_VALUE }

            GetUrlsQuerySortBy.CREATED_AT ->
                urls.sortedBy { Instant.parse(it.createdAt) }

            GetUrlsQuerySortBy.ENABLED ->
                urls.sortedBy { it.enabled }
        }

        return when (sortOrder) {
            SortOrder.ASC -> ascending
            SortOrder.DESC -> ascending.reversed()
            SortOrder.UNSPECIFIED -> urls
        }
    }

    suspend fun updateUrls(search: Boolean = true, sort: Boolean = true) {
        isLoading = true

        if (search) {
            val userUrlsRes = getUrls(
                context = context,
                searchField = searchKey,
                searchQuery = searchValue.text.ifEmpty { null }
            )

            userUrlsRes.onSuccess {
                urls = if (sort) sortUrls(it) else it
            }
        } else {
            val userUrlsRes = getUrls(
                context = context,
            )

            userUrlsRes.onSuccess {
                urls = if (sort) sortUrls(it) else it
            }
        }

        isLoading = false
    }

    LaunchedEffect(Unit) {
        val secureStore = SecureStorage.getInstance(context)

        serverUrl = secureStore.get("serverUrl")
    }

    val coroutineScope = rememberCoroutineScope()

    PromptPopup(
        showPopup = deleteUrl != null,
        onDismissRequest = { deleteUrl = null },
        onCancel = { deleteUrl = null },
        isLoading = isLoading,
        title = "Are you sure?",
        description = "Are you sure you want to delete ${deleteUrl?.code}? This action cannot be undone.",
        onSuccess = {
            coroutineScope.launch {
                if (deleteUrl == null) return@launch

                isLoading = true

                val deleteRes = deleteUrl(
                    context = context,
                    urlId = deleteUrl!!.id
                )

                deleteRes
                    .onSuccess {
                        if (compactView) {
                            updateUrls()
                        } else {
                            updateUrls(
                                search = false,
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
                                    text = "Failed to delete URL: ${it.message}"
                                )
                            }
                        )
                    }

                isLoading = false
                deleteUrl = null
            }
        }
    )

    CreateUrlPopup(
        context = context,
        activity = activity,
        showPopup = createdNewUrlPopupOpen,
        onDismissRequest = { createdNewUrlPopupOpen = false },
        updateUrls = ::updateUrls
    )

    EditUrlPopup(
        context = context,
        activity = activity,
        url = editUrl,
        showPopup = editUrl != null,
        onDismissRequest = { editUrl = null },
        updateUrls = ::updateUrls
    )

    QRCodePopup(
        context = context,
        activity = activity,
        qrCodeText = qrCodeText,
        showPopup = qrCodeUrl != null && serverUrl != null,
        onDismissRequest = { qrCodeUrl = null },
        downloadFileName = "QR_${qrCodeUrl?.id ?: "code"}.png"
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
                        createdNewUrlPopupOpen = true
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

        LaunchedEffect(searchValue) {
            updateUrls()
            lazyColumnListState.animateScrollToItem(0)
        }

        LaunchedEffect(
            sortOrder,
            sortKey,
            compactView,
        ) {
            urls = urls?.let { sortUrls(it) }
        }

        LaunchedEffect(compactView) {
            if (compactView) return@LaunchedEffect

            if (
                (searchKey != null && searchValue.text.isNotBlank()) ||
                (sortKey != GetUrlsQuerySortBy.CREATED_AT && sortOrder != SortOrder.DESC)
            ) {
                updateUrls(
                    search = false,
                    sort = false
                )
            }
        }

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
                        sortOrder = if (sortKey == GetUrlsQuerySortBy.CODE) sortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (sortKey != GetUrlsQuerySortBy.CODE) {
                                sortKey = GetUrlsQuerySortBy.CODE
                                sortOrder = SortOrder.ASC
                            } else if (sortOrder == SortOrder.UNSPECIFIED || sortOrder == SortOrder.DESC) {
                                sortOrder = SortOrder.ASC
                            } else {
                                sortOrder = SortOrder.DESC
                            }
                        },
                        onSearchClick = {
                            searchKey = GetUrlsQuerySearchField.CODE
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
                        sortOrder = if (sortKey == GetUrlsQuerySortBy.VANITY) sortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (sortKey != GetUrlsQuerySortBy.VANITY) {
                                sortKey = GetUrlsQuerySortBy.VANITY
                                sortOrder = SortOrder.ASC
                            } else if (sortOrder == SortOrder.UNSPECIFIED || sortOrder == SortOrder.DESC) {
                                sortOrder = SortOrder.ASC
                            } else {
                                sortOrder = SortOrder.DESC
                            }
                        },
                        onSearchClick = {
                            searchKey = GetUrlsQuerySearchField.VANITY
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
                        sortOrder = if (sortKey == GetUrlsQuerySortBy.DESTINATION) sortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (sortKey != GetUrlsQuerySortBy.DESTINATION) {
                                sortKey = GetUrlsQuerySortBy.DESTINATION
                                sortOrder = SortOrder.ASC
                            } else if (sortOrder == SortOrder.UNSPECIFIED || sortOrder == SortOrder.DESC) {
                                sortOrder = SortOrder.ASC
                            } else {
                                sortOrder = SortOrder.DESC
                            }
                        },
                        onSearchClick = {
                            searchKey = GetUrlsQuerySearchField.DESTINATION
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
                        sortOrder = if (sortKey == GetUrlsQuerySortBy.VIEWS) sortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (sortKey != GetUrlsQuerySortBy.VIEWS) {
                                sortKey = GetUrlsQuerySortBy.VIEWS
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
                                text = "Max Views",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableMaxViewsWidth,
                        name = "max views",
                        sortable = true,
                        sortOrder = if (sortKey == GetUrlsQuerySortBy.MAX_VIEWS) sortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (sortKey != GetUrlsQuerySortBy.MAX_VIEWS) {
                                sortKey = GetUrlsQuerySortBy.MAX_VIEWS
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
                                text = "Created",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableCreatedWidth,
                        name = "created",
                        sortable = true,
                        sortOrder = if (sortKey == GetUrlsQuerySortBy.CREATED_AT) sortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (sortKey != GetUrlsQuerySortBy.CREATED_AT) {
                                sortKey = GetUrlsQuerySortBy.CREATED_AT
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
                                text = "Enabled",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        width = tableEnabledWidth,
                        name = "enabled",
                        sortable = true,
                        sortOrder = if (sortKey == GetUrlsQuerySortBy.ENABLED) sortOrder else defaultSortOrder,
                        onSortChanged = {
                            if (sortKey != GetUrlsQuerySortBy.ENABLED) {
                                sortKey = GetUrlsQuerySortBy.ENABLED
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

                val rows: List<TableRowData> = urls?.map { url ->
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
                                            enabled = serverUrl != null,
                                            onClick = {
                                                val urlUrl = "${serverUrl}${urlsRoute}/${url.code}"

                                                Logger.debug("UrlsScreen", "Opening URL: $urlUrl (serverUrl: $serverUrl, urlsRoute: $urlsRoute, url.code: ${url.code})")

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
                                            enabled = serverUrl != null && !url.vanity.isNullOrBlank(),
                                            onClick = {
                                                val urlUrl = "${serverUrl}${urlsRoute}/${url.vanity}"

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
                                            enabled = serverUrl != null,
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
                                                val urlUrl = "${serverUrl}${urlsRoute}/${if (url.vanity.isNullOrBlank()) url.code else url.vanity}"

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
                                        enabled = !isLoading
                                    )

                                    ActionButtonSpacer()

                                    IconButton(
                                        icon = painterResource(R.drawable.qr_code),
                                        iconContentDescription = "Show QR code",
                                        color = MaterialTheme.colorScheme.primary,
                                        iconColor = MaterialTheme.colorScheme.onPrimary,
                                        onClick = {
                                            qrCodeUrl = url
                                        },
                                        enabled = serverUrl != null && !isLoading
                                    )

                                    ActionButtonSpacer()

                                    IconButton(
                                        icon = painterResource(R.drawable.edit),
                                        iconContentDescription = "Edit URL",
                                        color = MaterialTheme.colorScheme.primary,
                                        iconColor = MaterialTheme.colorScheme.onPrimary,
                                        onClick = { editUrl = url },
                                        enabled = serverUrl != null && !isLoading
                                    )

                                    ActionButtonSpacer()

                                    IconButton(
                                        icon = painterResource(R.drawable.delete),
                                        iconContentDescription = "Delete URL",
                                        color = MaterialTheme.colorScheme.error,
                                        iconColor = MaterialTheme.colorScheme.onError,
                                        onClick = {
                                            deleteUrl = url
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
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (urls == null || isLoading) {
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
                    if (urls!!.size > 0L) {
                        items(urls!!.size) { index ->
                            val url = urls!![index]

                            LargeUrlDisplay(
                                context = context,
                                activity = activity,
                                url = url,
                                serverUrl = serverUrl,
                                urlsRoute = urlsRoute,
                                onDelete = { deleteUrl = url },
                                onEdit = { editUrl = url },
                                onShowQRCode = {
                                    qrCodeUrl = url
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

private enum class GetUrlsQuerySortBy(val value: String) {
    @SerializedName("code")
    CODE("code"),

    @SerializedName("vanity")
    VANITY("vanity"),

    @SerializedName("destination")
    DESTINATION("destination"),

    @SerializedName("views")
    VIEWS("views"),

    @SerializedName("maxViews")
    MAX_VIEWS("maxViews"),

    @SerializedName("createdAt")
    CREATED_AT("createdAt"),

    @SerializedName("enabled")
    ENABLED("enabled");

    override fun toString(): String = value
}