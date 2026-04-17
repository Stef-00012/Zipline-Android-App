package com.stefdp.zipline.screens.admin.invites

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.SerializedName
import com.stefdp.zipline.Logger
import com.stefdp.zipline.network.models.Invite
import com.stefdp.zipline.network.requests.getInvites
import com.stefdp.zipline.utils.STORAGE_DEFAULT_DOMAIN_KEY
import com.stefdp.zipline.network.requests.createInvite as apiCreateInvite
import com.stefdp.zipline.network.requests.deleteInvite as apiDeleteInvite
import com.stefdp.zipline.utils.STORAGE_SERVER_URL_KEY
import com.stefdp.zipline.utils.SecureStorage
import com.stefdp.zipline.utils.SortOrder
import com.stefdp.zipline.utils.ZiplineViewStateType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

enum class GetInvitesQuerySortBy(val value: String) {
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

data class AdminInvitesUiState(
    val isLoading: Boolean = false,
    val invites: List<Invite>? = null,
    val serverUrl: String? = null,
    val defaultDomain: String? = null,
    val sortKey: GetInvitesQuerySortBy = GetInvitesQuerySortBy.CREATED_AT,
    val sortOrder: SortOrder = SortOrder.DESC,
    val createNewInvitePopupOpen: Boolean = false,
    val deleteInvite: Invite? = null,
    val qrCodeInvite: Invite? = null,
    val qrCodeText: String = "",
    val selectedExpiresAt: Set<String> = setOf("never"),
    val maxUses: TextFieldValue = TextFieldValue("")
)

class AdminInvitesViewModel : ViewModel() {
    private val _state = MutableStateFlow(AdminInvitesUiState())
    val state: StateFlow<AdminInvitesUiState> = _state.asStateFlow()

    fun initData(context: Context) {
        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)

            val serverUrl = secureStore.get(STORAGE_SERVER_URL_KEY)
            val defaultDomain = secureStore.get(STORAGE_DEFAULT_DOMAIN_KEY)

            _state.update {
                it.copy(
                    serverUrl = serverUrl,
                    defaultDomain = defaultDomain
                )
            }
        }
    }

    fun openCreateNewInvitePopup() {
        _state.update {
            it.copy(createNewInvitePopupOpen = true)
        }
    }

    fun closeCreateNewInvitePopup() {
        _state.update {
            it.copy(createNewInvitePopupOpen = false)
        }
    }

    fun setDeleteInvite(invite: Invite?) {
        _state.update {
            it.copy(deleteInvite = invite)
        }
    }

    fun setQrCodeInvite(invite: Invite?) {
        Logger.debug("setQrCodeInvite", "Setting QR code invite: $invite")

        if (invite == null) {
            _state.update {
                it.copy(
                    qrCodeInvite = null,
                    qrCodeText = ""
                )
            }
        } else {
            val serverUrl = _state.value.serverUrl

            val qrText = "${serverUrl}/invite/${invite.code}"

            _state.update {
                it.copy(
                    qrCodeInvite = invite,
                    qrCodeText = qrText
                )
            }
        }
    }

    fun setSelectedExpiresAt(expiresAt: Set<String>) {
        _state.update {
            it.copy(selectedExpiresAt = expiresAt)
        }
    }

    fun setMaxUses(maxUses: TextFieldValue) {
        _state.update {
            it.copy(maxUses = maxUses)
        }
    }

    fun updateSort(key: GetInvitesQuerySortBy, order: SortOrder) {
        _state.update {
            it.copy(
                sortKey = key,
                sortOrder = order
            )
        }
    }

    fun refreshInvites(
        context: Context,
        sort: Boolean = true,
        viewState: ZiplineViewStateType
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val usersRes = getInvites(
                context = context,
            )

            usersRes.onSuccess { invites ->
                _state.update {
                    it.copy(
                        invites = if (sort) sortInvitesList(
                            invites,
                            viewState
                        ) else invites,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun deleteInvite(
        context: Context,
        viewState: ZiplineViewStateType,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            if (_state.value.deleteInvite == null) return@launch

            _state.update {
                it.copy(isLoading = true)
            }

            val deleteRes = apiDeleteInvite(
                context = context,
                codeOrId = _state.value.deleteInvite!!.id
            )

            deleteRes
                .onSuccess {
                    if (viewState == ZiplineViewStateType.COMPACT) {
                        refreshInvites(
                            context = context,
                            viewState = viewState
                        )
                    } else {
                        refreshInvites(
                            context = context,
                            sort = false,
                            viewState = viewState
                        )
                    }

                    _state.update {
                        it.copy(
                            deleteInvite = null,
                            isLoading = false
                        )
                    }
                }
                .onFailure {
                    onError("Failed to delete invite: ${it.message}")

                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
        }
    }

    fun createInvite(
        context: Context,
        viewState: ZiplineViewStateType,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val createInviteRes = apiCreateInvite(
                context = context,
                expiresAt = _state.value.selectedExpiresAt.firstOrNull() ?: "never",
                maxUses = _state.value.maxUses.text.toLongOrNull(),
            )

            createInviteRes
                .onSuccess {
                    onSuccess()

                    refreshInvites(
                        context = context,
                        viewState = viewState
                    )

                    closeCreateNewInvitePopup()
                }
                .onFailure { error ->
                    Logger.error("CreateInvitePopup", "Failed to create invite", error)

                    val errorMessage = error.message ?: "Something went wrong..."

                    onError("Failed to create invite: $errorMessage")

                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
        }
    }

    fun triggerSort(viewState: ZiplineViewStateType) {
        val currentInvites = _state.value.invites ?: return

        _state.update {
            it.copy(invites = sortInvitesList(currentInvites, viewState))
        }
    }

    private fun sortInvitesList(
        invites: List<Invite>,
        viewState: ZiplineViewStateType
    ): List<Invite> {
        if (viewState == ZiplineViewStateType.LARGE) return invites.sortedBy { Instant.parse(it.createdAt) }.reversed()

        val sortKey = _state.value.sortKey
        val sortOrder = _state.value.sortOrder

        val ascending = when (sortKey) {
            GetInvitesQuerySortBy.CODE -> invites.sortedBy { it.code }
            GetInvitesQuerySortBy.CREATED_BY -> invites.sortedBy { it.inviter.username }
            GetInvitesQuerySortBy.CREATED_AT -> invites.sortedBy { Instant.parse(it.createdAt) }
            GetInvitesQuerySortBy.UPDATED_AT -> invites.sortedBy { Instant.parse(it.updatedAt) }
            GetInvitesQuerySortBy.EXPIRES_AT -> invites.sortedBy { it.expiresAt?.let { expiresAt -> Instant.parse(expiresAt) } }
            GetInvitesQuerySortBy.MAX_USES -> invites.sortedBy { it.maxUses }
            GetInvitesQuerySortBy.USES -> invites.sortedBy { it.uses }
        }

        return when (sortOrder) {
            SortOrder.ASC -> ascending
            SortOrder.DESC -> ascending.reversed()
            SortOrder.UNSPECIFIED -> invites
        }
    }
}