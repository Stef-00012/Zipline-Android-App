package com.stefdp.zipline.screens.urls

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.SerializedName
import com.stefdp.zipline.network.models.Url
import com.stefdp.zipline.network.models.requests.GetUrlsQuerySearchField
import com.stefdp.zipline.network.requests.createUrl
import com.stefdp.zipline.network.requests.deleteUrl
import com.stefdp.zipline.network.requests.getUrls
import com.stefdp.zipline.network.requests.updateUrl
import com.stefdp.zipline.utils.STORAGE_SERVER_URL_KEY
import com.stefdp.zipline.utils.SecureStorage
import com.stefdp.zipline.utils.SortOrder
import com.stefdp.zipline.utils.ZiplineViewStateType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Instant

enum class GetUrlsQuerySortBy(val value: String) {
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

data class UrlsUiState(
    val isLoading: Boolean = true,
    val urls: List<Url>? = null,
    val serverUrl: String? = null,
    val urlsRoute: String = "",
    val sortKey: GetUrlsQuerySortBy = GetUrlsQuerySortBy.CREATED_AT,
    val sortOrder: SortOrder = SortOrder.DESC,
    val searchKey: GetUrlsQuerySearchField? = null,
    val searchValue: TextFieldValue = TextFieldValue(""),
    val isCreatePopupOpen: Boolean = false,
    val createPopupBaseUrl: String? = null,
    val deleteUrl: Url? = null,
    val editUrl: Url? = null,
    val qrCodeUrl: Url? = null,
    val qrCodeText: String = "",
    val popupIsLoading: Boolean = false,
    val popupErrorMessage: String? = null,
    val createdUrlResult: String? = null,
    val createUrlDestination: TextFieldValue = TextFieldValue(""),
    val createUrlVanity: TextFieldValue = TextFieldValue(""),
    val createUrlMaxViews: TextFieldValue = TextFieldValue(""),
    val createUrlSelectedOverrideDomain: Set<String> = setOf("default"),
    val createUrlEnabled: Boolean = true,
    val createUrlPassword: TextFieldValue = TextFieldValue(""),
    val editUrlDestination: TextFieldValue = TextFieldValue(""),
    val editUrlVanity: TextFieldValue = TextFieldValue(""),
    val editUrlMaxViews: TextFieldValue = TextFieldValue(""),
    val editUrlEnabled: Boolean = true,
    val editUrlPassword: TextFieldValue = TextFieldValue("")
)

class UrlsViewModel : ViewModel() {
    private val _state = MutableStateFlow(UrlsUiState())
    val state: StateFlow<UrlsUiState> = _state.asStateFlow()

    fun initData(
        context: Context,
        urlsRoute: String,
        sharedUrl: String?
    ) {
        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)
            val serverUrl = secureStore.get(STORAGE_SERVER_URL_KEY)

            _state.update {
                it.copy(
                    serverUrl = serverUrl,
                    urlsRoute = urlsRoute,
                    createPopupBaseUrl = sharedUrl,
                    isCreatePopupOpen = sharedUrl != null,
                    createUrlDestination = TextFieldValue(sharedUrl ?: "")
                )
            }
        }
    }

    fun updateSearchKey(key: GetUrlsQuerySearchField?) {
        _state.update {
            it.copy(
                searchKey = key,
                searchValue = TextFieldValue("")
            )
        }
    }

    fun updateSearchValue(value: TextFieldValue) {
        _state.update {
            it.copy(searchValue = value)
        }
    }

    fun updateSort(key: GetUrlsQuerySortBy, order: SortOrder) {
        _state.update {
            it.copy(
                sortKey = key,
                sortOrder = order
            )
        }
    }

    fun openCreatePopup() {
        _state.update {
            it.copy(
                isCreatePopupOpen = true,
                popupErrorMessage = null
            )
        }
    }

    fun closeCreatePopup() {
        _state.update {
            it.copy(
                isCreatePopupOpen = false,
                createPopupBaseUrl = null,
                popupErrorMessage = null,
                createdUrlResult = null
            )
        }
    }

    fun clearCreatedUrlResult() {
        _state.update {
            it.copy(createdUrlResult = null)
        }
    }

    fun setCreateUrlDestination(value: TextFieldValue) {
        _state.update {
            it.copy(createUrlDestination = value)
        }
    }

    fun setCreateUrlVanity(value: TextFieldValue) {
        _state.update {
            it.copy(createUrlVanity = value)
        }
    }

    fun setCreateUrlMaxViews(value: TextFieldValue) {
        _state.update {
            it.copy(createUrlMaxViews = value)
        }
    }

    fun setCreateUrlSelectedOverrideDomain(overrideDomain: Set<String>) {
        _state.update {
            it.copy(createUrlSelectedOverrideDomain = overrideDomain)
        }
    }

    fun setCreateUrlEnabled(enabled: Boolean) {
        _state.update {
            it.copy(createUrlEnabled = enabled)
        }
    }

    fun setCreateUrlPassword(value: TextFieldValue) {
        _state.update {
            it.copy(createUrlPassword = value)
        }
    }

    fun setEditUrl(url: Url?) {
        _state.update {
            it.copy(
                editUrl = url,
                popupErrorMessage = null,
                editUrlDestination = TextFieldValue(url?.destination ?: ""),
                editUrlVanity = TextFieldValue(url?.vanity ?: ""),
                editUrlMaxViews = TextFieldValue(
                    if (url?.maxViews != null)
                        (url.maxViews.takeIf { maxViews -> maxViews > 0L } ?: "").toString()
                    else ""
                ),
                editUrlEnabled = url?.enabled ?: true,
                editUrlPassword = TextFieldValue("")
            )
        }
    }

    fun setEditUrlDestination(value: TextFieldValue) {
        _state.update {
            it.copy(editUrlDestination = value)
        }
    }

    fun setEditUrlVanity(value: TextFieldValue) {
        _state.update {
            it.copy(editUrlVanity = value)
        }
    }

    fun setEditUrlMaxViews(value: TextFieldValue) {
        _state.update {
            it.copy(editUrlMaxViews = value)
        }
    }

    fun setEditUrlEnabled(enabled: Boolean) {
        _state.update {
            it.copy(editUrlEnabled = enabled)
        }
    }

    fun setEditUrlPassword(value: TextFieldValue) {
        _state.update {
            it.copy(editUrlPassword = value)
        }
    }

    fun setDeleteUrl(url: Url?) {
        _state.update {
            it.copy(deleteUrl = url)
        }
    }

    fun setQrCodeUrl(url: Url?) {
        if (url == null) {
            _state.update {
                it.copy(
                    qrCodeUrl = null,
                    qrCodeText = ""
                )
            }
        } else {
            val serverUrl = _state.value.serverUrl
            val urlsRoute = _state.value.urlsRoute

            val qrText = "${serverUrl}${urlsRoute}/${if (url.vanity.isNullOrBlank()) url.code else url.vanity}"

            _state.update {
                it.copy(
                    qrCodeUrl = url,
                    qrCodeText = qrText
                )
            }
        }
    }

    fun refreshUrls(
        context: Context,
        viewState: ZiplineViewStateType,
        search: Boolean = true,
        sort: Boolean = true
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val searchKey = _state.value.searchKey
            val searchValue = _state.value.searchValue.text.ifEmpty { null }

            val res = if (search) {
                getUrls(
                    context = context,
                    searchField = searchKey,
                    searchQuery = searchValue
                )
            } else {
                getUrls(context = context)
            }

            res.onSuccess { fetchedUrls ->
                val finalUrls = if (sort) sortUrlsList(fetchedUrls, viewState) else fetchedUrls

                _state.update {
                    it.copy(
                        urls = finalUrls,
                        isLoading = false
                    )
                }
            }.onFailure {
                _state.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    fun triggerSort(viewState: ZiplineViewStateType) {
        val currentUrls = _state.value.urls ?: return

        _state.update {
            it.copy(urls = sortUrlsList(currentUrls, viewState))
        }
    }

    private fun sortUrlsList(
        urls: List<Url>,
        viewState: ZiplineViewStateType
    ): List<Url> {
        if (viewState == ZiplineViewStateType.LARGE) return urls.sortedBy { Instant.parse(it.createdAt) }.reversed()

        val sortKey = _state.value.sortKey
        val sortOrder = _state.value.sortOrder

        val ascending = when (sortKey) {
            GetUrlsQuerySortBy.CODE -> urls.sortedBy { it.code }
            GetUrlsQuerySortBy.VANITY -> urls.sortedBy { it.vanity.orEmpty() }
            GetUrlsQuerySortBy.DESTINATION -> urls.sortedBy { it.destination }
            GetUrlsQuerySortBy.VIEWS -> urls.sortedBy { it.views }
            GetUrlsQuerySortBy.MAX_VIEWS -> urls.sortedBy { it.maxViews ?: Long.MIN_VALUE }
            GetUrlsQuerySortBy.CREATED_AT -> urls.sortedBy { Instant.parse(it.createdAt) }
            GetUrlsQuerySortBy.ENABLED -> urls.sortedBy { it.enabled }
        }

        return when (sortOrder) {
            SortOrder.ASC -> ascending
            SortOrder.DESC -> ascending.reversed()
            SortOrder.UNSPECIFIED -> urls
        }
    }

    fun deleteUrl(
        context: Context,
        urlId: String,
        viewState: ZiplineViewStateType,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val deleteRes = deleteUrl(context = context, urlId = urlId)

            deleteRes.onSuccess {
                if (viewState == ZiplineViewStateType.COMPACT) {
                    refreshUrls(context, viewState)
                } else {
                    refreshUrls(context, viewState, search = false, sort = false)
                }

                _state.update {
                    it.copy(deleteUrl = null)
                }

                onSuccess()
            }.onFailure { error ->
                _state.update {
                    it.copy(isLoading = false, deleteUrl = null)
                }

                onError(error.message ?: "Failed to delete URL")
            }
        }
    }

    fun createUrl(
        context: Context,
        viewState: ZiplineViewStateType,
        destination: String,
        vanity: String?,
        maxViews: Long?,
        enabled: Boolean,
        domain: String?,
        password: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    popupIsLoading = true,
                    popupErrorMessage = null
                )
            }
            val res = createUrl(context, destination, vanity, enabled, maxViews, password, domain)

            res.onSuccess { createdUrl ->
                _state.update {
                    it.copy(
                        popupIsLoading = false,
                        createdUrlResult = createdUrl.url
                    )
                }

                refreshUrls(context, viewState)

                onSuccess()
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        popupIsLoading = false,
                        popupErrorMessage = error.message ?: "Something went wrong..."
                    )
                }

                onError(error.message ?: "Failed to create URL")
            }
        }
    }

    fun editUrl(
        context: Context,
        viewState: ZiplineViewStateType,
        urlId: String,
        destination: String,
        vanity: String?,
        maxViews: Long?,
        enabled: Boolean,
        password: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    popupIsLoading = true,
                    popupErrorMessage = null
                )
            }

            val res = updateUrl(context, urlId, destination, vanity, enabled, maxViews, password)

            res.onSuccess { _ ->
                _state.update {
                    it.copy(
                        popupIsLoading = false,
                        editUrl = null
                    )
                }

                refreshUrls(context, viewState)

                onSuccess()
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        popupIsLoading = false,
                        popupErrorMessage = error.message ?: "Something went wrong..."
                    )
                }
                onError(error.message ?: "Failed to update URL")
            }
        }
    }
}