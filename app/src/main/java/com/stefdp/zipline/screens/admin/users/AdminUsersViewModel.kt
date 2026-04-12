package com.stefdp.zipline.screens.admin.users

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.SerializedName
import com.stefdp.zipline.Logger
import com.stefdp.zipline.network.models.User
import com.stefdp.zipline.network.models.UserQuotaFilesQuota
import com.stefdp.zipline.network.models.UserRole
import com.stefdp.zipline.network.models.requests.UpdateUserBodyQuota
import com.stefdp.zipline.network.requests.getUsers
import com.stefdp.zipline.network.requests.updateUser
import com.stefdp.zipline.network.requests.deleteUser as apiDeleteUser
import com.stefdp.zipline.network.requests.createUser as apiCreateUser
import com.stefdp.zipline.utils.SortOrder
import com.stefdp.zipline.utils.ZiplineViewStateType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

enum class GetUsersQuerySortBy(val value: String) {
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

enum class DeleteUserLevel {
    CONFIRMATION_PROMPT,
    FILE_DELETION_PROMPT
}

data class AdminUsersUiState(
    val isLoading: Boolean = false,
    val users: List<User>? = null,
    val sortKey: GetUsersQuerySortBy = GetUsersQuerySortBy.CREATED_AT,
    var sortOrder: SortOrder = SortOrder.DESC,
    var createNewUserPopupOpen: Boolean = false,
    var deleteUser: User? = null,
    var deleteUserLevel: DeleteUserLevel = DeleteUserLevel.CONFIRMATION_PROMPT,
    val editUser: User? = null,
    val newUserUsername: TextFieldValue = TextFieldValue(""),
    val newUserPassword: TextFieldValue = TextFieldValue(""),
    val newUserAvatar: String? = null,
    val newUserSelectedRole: Set<String> = setOf(UserRole.USER.toString()),
    val editUserUsername: TextFieldValue = TextFieldValue(""),
    val editUserPassword: TextFieldValue = TextFieldValue(""),
    val editUserSelectedRole: Set<String> = setOf(UserRole.USER.toString()),
    val editUserAvatar: String? = null,
    val editUserSelectedFilesQuota: Set<String> = setOf(UserQuotaFilesQuota.NONE.toString()),
    val editUserMaxBytes: TextFieldValue = TextFieldValue(""),
    val editUserMaxFileCount: TextFieldValue = TextFieldValue(""),
    val editUserMaxUrls: TextFieldValue = TextFieldValue("")
)

class AdminUsersViewModel : ViewModel() {
    private val _state = MutableStateFlow(AdminUsersUiState())
    val state: StateFlow<AdminUsersUiState> = _state.asStateFlow()

    fun openCreateNewUserPopup() {
        _state.update {
            it.copy(createNewUserPopupOpen = true)
        }
    }

    fun closeCreateNewUserPopup() {
        _state.update {
            it.copy(createNewUserPopupOpen = false)
        }
    }

    fun setDeleteUser(user: User?) {
        _state.update {
            it.copy(deleteUser = user)
        }
    }

    fun setDeleteUserLevel(level: DeleteUserLevel) {
        _state.update {
            it.copy(deleteUserLevel = level)
        }
    }

    fun setEditUser(user: User?) {
        val currentUserQuota = when (user?.quota?.filesQuota) {
            UserQuotaFilesQuota.BY_BYTES if !user.quota.maxBytes.isNullOrBlank() -> UserQuotaFilesQuota.BY_BYTES
            UserQuotaFilesQuota.BY_FILES if user.quota.maxFiles != null -> UserQuotaFilesQuota.BY_FILES
            else -> UserQuotaFilesQuota.NONE
        }

        _state.update {
            it.copy(
                editUser = user,
                editUserUsername = TextFieldValue(user?.username ?: ""),
                editUserPassword = TextFieldValue(""),
                editUserSelectedRole = setOf((user?.role ?: UserRole.USER).toString()),
                editUserAvatar = user?.avatar,
                editUserSelectedFilesQuota = setOf(currentUserQuota.toString()),
                editUserMaxBytes = TextFieldValue(user?.quota?.maxBytes ?: ""),
                editUserMaxFileCount = TextFieldValue(user?.quota?.maxFiles?.toString() ?: ""),
                editUserMaxUrls = TextFieldValue(user?.quota?.maxUrls?.toString() ?: ""),
            )
        }
    }

    fun setNewUserUsername(username: TextFieldValue) {
        _state.update {
            it.copy(newUserUsername = username)
        }
    }

    fun setNewUserPassword(password: TextFieldValue) {
        _state.update {
            it.copy(newUserPassword = password)
        }
    }

    fun setNewUserAvatar(avatar: String?) {
        _state.update {
            it.copy(newUserAvatar = avatar)
        }
    }

    fun setNewUserSelectedRole(role: Set<String>) {
        _state.update {
            it.copy(newUserSelectedRole = role)
        }
    }

    fun setEditUserUsername(username: TextFieldValue) {
        _state.update {
            it.copy(editUserUsername = username)
        }
    }

    fun setEditUserPassword(password: TextFieldValue) {
        _state.update {
            it.copy(editUserPassword = password)
        }
    }

    fun setEditUserAvatar(avatar: String?) {
        _state.update {
            it.copy(editUserAvatar = avatar)
        }
    }

    fun setEditUserSelectedRole(role: Set<String>) {
        _state.update {
            it.copy(editUserSelectedRole = role)
        }
    }

    fun setEditUserSelectedFilesQuota(filesQuota: Set<String>) {
        _state.update {
            it.copy(editUserSelectedFilesQuota = filesQuota)
        }
    }

    fun setEditUserMaxBytes(maxBytes: TextFieldValue) {
        _state.update {
            it.copy(editUserMaxBytes = maxBytes)
        }
    }

    fun setEditUserMaxFileCount(maxFileCount: TextFieldValue) {
        _state.update {
            it.copy(editUserMaxFileCount = maxFileCount)
        }
    }

    fun setEditUserMaxUrls(maxUrls: TextFieldValue) {
        _state.update {
            it.copy(editUserMaxUrls = maxUrls)
        }
    }

    fun updateSort(key: GetUsersQuerySortBy, order: SortOrder) {
        _state.update {
            it.copy(
                sortKey = key,
                sortOrder = order
            )
        }
    }

    fun triggerSort(viewState: ZiplineViewStateType) {
        val currentUsers = _state.value.users ?: return

        _state.update {
            it.copy(users = sortUsersList(currentUsers, viewState))
        }
    }

    private fun sortUsersList(
        users: List<User>,
        viewState: ZiplineViewStateType
    ): List<User> {
        if (viewState == ZiplineViewStateType.LARGE) return users.sortedBy { Instant.parse(it.createdAt) }.reversed()

        val sortKey = _state.value.sortKey
        val sortOrder = _state.value.sortOrder

        val ascending = when (sortKey) {
            GetUsersQuerySortBy.USERNAME -> users.sortedBy { it.username }
            GetUsersQuerySortBy.ROLE -> users.sortedBy { it.role.level }
            GetUsersQuerySortBy.CREATED_AT -> users.sortedBy { Instant.parse(it.createdAt) }
            GetUsersQuerySortBy.UPDATED_AT -> users.sortedBy { Instant.parse(it.updatedAt) }
        }

        return when (sortOrder) {
            SortOrder.ASC -> ascending
            SortOrder.DESC -> ascending.reversed()
            SortOrder.UNSPECIFIED -> users
        }
    }

    fun refreshUsers(
        context: Context,
        sort: Boolean = true,
        viewState: ZiplineViewStateType,
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val usersRes = getUsers(
                context = context,
                excludeSelf = true
            )

            usersRes.onSuccess { users ->
                _state.update {
                    it.copy(
                        users = if (sort) sortUsersList(users, viewState) else users,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun deleteUser(
        context: Context,
        deleteUserFilesAndUrls: Boolean,
        viewState: ZiplineViewStateType,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (_state.value.deleteUser == null) return@launch

            val deleteRes = apiDeleteUser(
                context = context,
                userId = _state.value.deleteUser!!.id,
                deleteUserFilesAndUrls = deleteUserFilesAndUrls
            )

            deleteRes
                .onSuccess {
                    if (viewState == ZiplineViewStateType.COMPACT) {
                        refreshUsers(
                            context = context,
                            viewState = viewState
                        )
                    } else {
                        refreshUsers(
                            context = context,
                            sort = false,
                            viewState = viewState
                        )
                    }

                    onSuccess()

                    _state.update {
                        it.copy(
                            deleteUser = null,
                            deleteUserLevel = DeleteUserLevel.CONFIRMATION_PROMPT,
                            isLoading = false
                        )
                    }
                }
                .onFailure {
                    onError("Failed to delete user: ${it.message}")

                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
        }
    }

    fun createUser(
        context: Context,
        viewState: ZiplineViewStateType,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val userRole = UserRole.valueOf(_state.value.newUserSelectedRole.firstOrNull() ?: UserRole.USER.toString())

            val createUserRes = apiCreateUser(
                context = context,
                username = _state.value.newUserUsername.text,
                password = _state.value.newUserPassword.text,
                role = userRole,
                avatar = _state.value.newUserAvatar
            )

            createUserRes
                .onSuccess {
                    refreshUsers(
                        context = context,
                        viewState = viewState
                    )

                    closeCreateNewUserPopup()

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.error("CreateUrlPopup", "Failed to create user", error)

                    val errorMessage = error.message ?: "Something went wrong..."

                    onError("Failed to create user: $errorMessage")

                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
        }
    }

    fun editUser(
        context: Context,
        viewState: ZiplineViewStateType,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (_state.value.editUser == null) return@launch

            _state.update {
                it.copy(isLoading = true)
            }

            val userRole = UserRole.valueOf(_state.value.editUserSelectedRole.firstOrNull() ?: UserRole.USER.toString())

            val userQuota = UserQuotaFilesQuota.valueOf(_state.value.editUserSelectedFilesQuota.firstOrNull() ?: UserQuotaFilesQuota.NONE.toString())

            val userQuotaBody = when (userQuota) {
                UserQuotaFilesQuota.BY_BYTES -> UpdateUserBodyQuota(
                    filesType = userQuota,
                    maxBytes = _state.value.editUserMaxBytes.text.ifBlank { null },
                    maxFiles = null,
                    maxUrls = if (_state.value.editUserMaxUrls.text.isBlank())
                        null
                    else _state.value.editUserMaxUrls.text.toLong()
                )

                UserQuotaFilesQuota.BY_FILES -> UpdateUserBodyQuota(
                    filesType = userQuota,
                    maxBytes = null,
                    maxFiles = if (_state.value.editUserMaxFileCount.text.isBlank())
                        null
                    else _state.value.editUserMaxFileCount.text.toLong(),
                    maxUrls = if (_state.value.editUserMaxUrls.text.isBlank())
                        null
                    else _state.value.editUserMaxUrls.text.toLong()
                )

                UserQuotaFilesQuota.NONE -> UpdateUserBodyQuota(
                    filesType = userQuota,
                    maxBytes = null,
                    maxFiles = null,
                    maxUrls = if (_state.value.editUserMaxUrls.text.isBlank())
                        null
                    else _state.value.editUserMaxUrls.text.toLong()
                )
            }

            val editUserRes = updateUser(
                context = context,
                userId = _state.value.editUser!!.id,
                username = _state.value.editUserUsername.text.ifBlank { null },
                password = _state.value.editUserPassword.text.ifBlank { null },
                avatar = _state.value.editUserAvatar,
                role = userRole,
                quota = userQuotaBody

            )

            editUserRes
                .onSuccess {
                    refreshUsers(
                        context = context,
                        viewState = viewState
                    )

                    onSuccess()

                    _state.update {
                        it.copy(
                            isLoading = false,
                            editUser = null
                        )
                    }
                }
                .onFailure { error ->
                    Logger.error("EditUrlPopup", "Failed to edit user", error)

                    val errorMessage = error.message ?: "Something went wrong..."

                    onError("Failed to edit user: $errorMessage")

                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
        }
    }
}