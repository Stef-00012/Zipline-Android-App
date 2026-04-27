package com.stefdp.zipline

import android.content.Context
import androidx.lifecycle.ViewModel
import com.stefdp.zipline.network.models.PublicServerConfig
import com.stefdp.zipline.network.models.User
import com.stefdp.zipline.network.models.WebSettings
import com.stefdp.zipline.network.models.responses.GetServerVersionResponse
import com.stefdp.zipline.network.requests.getAvatar
import com.stefdp.zipline.network.requests.getCurrentUser
import com.stefdp.zipline.network.requests.getPublicConfig
import com.stefdp.zipline.network.requests.getServerVersion
import com.stefdp.zipline.network.requests.getWebServerSettings
import com.stefdp.zipline.utils.ZiplineViewState
import com.stefdp.zipline.utils.ZiplineViewStateType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MainUiState(
    val loggedUser: User? = null,
    val loggedUserAvatar: String? = null,
    val publicSettings: PublicServerConfig? = null,
    val webSettings: WebSettings? = null,
    val serverVersion: GetServerVersionResponse? = null,
    val screenViewState: ZiplineViewState = ZiplineViewState(
        adminUsers = ZiplineViewStateType.LARGE,
        adminInvites = ZiplineViewStateType.LARGE,
        files = ZiplineViewStateType.LARGE,
        folders = ZiplineViewStateType.LARGE,
        urls = ZiplineViewStateType.LARGE,
    ),
)

class MainViewModel : ViewModel() {
    private val _state = MutableStateFlow(MainUiState())
    val state: StateFlow<MainUiState> = _state.asStateFlow()

    suspend fun updateLoggedUser(context: Context): Result<User> {
        val tag = "MainActivity[updateLoggedUser]"

        Logger.debug(tag, "Checking if user is already logged in...")

        val currentUserRes = getCurrentUser(
            context = context
        )

        currentUserRes
            .onSuccess { currentUserData ->
                if (currentUserData.user == null) return Result.failure(
                    Exception("User is not logged in")
                )

                Logger.debug(tag, "User is logged in as ${currentUserData.user.username}")

                _state.update {
                    it.copy(loggedUser = currentUserData.user)
                }

                return@updateLoggedUser Result.success(currentUserData.user)
            }
            .onFailure { error ->
                Logger.debug(tag, "User is not logged in")
                Logger.error(tag, "Failed to fetch user stats: ${error.message}")

                _state.update {
                    it.copy(loggedUser = null)
                }

                return@updateLoggedUser Result.failure(error)
            }

        return Result.failure(
            Exception("Something went wrong...")
        )
    }

    suspend fun updateLoggedUserAvatar(context: Context): Result<String> {
        val tag = "MainActivity[updateLoggedUserAvatar]"

        val loggedUserAvatarRes = getAvatar(
            context = context
        )

        loggedUserAvatarRes
            .onSuccess { avatarBase64 ->
                _state.update {
                    it.copy(loggedUserAvatar = avatarBase64)
                }

                return@updateLoggedUserAvatar Result.success(avatarBase64)
            }
            .onFailure { error ->
                Logger.error(tag, "Failed to fetch user avatar: ${error.message}")

                _state.update {
                    it.copy(loggedUserAvatar = null)
                }

                return@updateLoggedUserAvatar Result.failure(error)
            }

        return Result.failure(
            Exception("Something went wrong...")
        )
    }

    suspend fun updatePublicSettings(context: Context): Result<PublicServerConfig> {
        val tag = "MainActivity[updatePublicSettings]"

        val publicConfigRes = getPublicConfig(
            context = context
        )

        publicConfigRes
            .onSuccess { publicConfigData ->
                _state.update {
                    it.copy(publicSettings = publicConfigData)
                }

                return@updatePublicSettings Result.success(publicConfigData)
            }
            .onFailure { error ->
                Logger.error(tag, "Failed to fetch public server config: ${error.message}")

                _state.update {
                    it.copy(publicSettings = null)
                }

                return@updatePublicSettings Result.failure(error)
            }

        return Result.failure(
            Exception("Something went wrong...")
        )
    }

    suspend fun updateWebSettings(context: Context): Result<WebSettings> {
        val tag = "MainActivity[updateWebSettings]"

        val webSettingsRes = getWebServerSettings(
            context = context
        )

        webSettingsRes
            .onSuccess { webSettingsData ->
                _state.update {
                    it.copy(webSettings = webSettingsData)
                }

                return@updateWebSettings Result.success(webSettingsData)
            }
            .onFailure { error ->
                Logger.error(tag, "Failed to fetch web settings: ${error.message}")

                _state.update {
                    it.copy(webSettings = null)
                }

                return@updateWebSettings Result.failure(error)
            }

        return Result.failure(
            Exception("Something went wrong...")
        )
    }

    suspend fun updateServerVersion(context: Context): Result<GetServerVersionResponse> {
        val tag = "MainActivity[updateServerVersion]"

        val serverVersionRes = getServerVersion(
            context = context
        )

        serverVersionRes
            .onSuccess { serverVersionData ->
                _state.update {
                    it.copy(serverVersion = serverVersionData)
                }

                return@updateServerVersion Result.success(serverVersionData)
            }
            .onFailure { error ->
                Logger.error(tag, "Failed to fetch web settings: ${error.message}")

                _state.update {
                    it.copy(serverVersion = null)
                }

                return@updateServerVersion Result.failure(error)
            }

        return Result.failure(
            Exception("Something went wrong...")
        )
    }

    fun updateScreenViewState(context: Context, viewState: ZiplineViewState) {
        _state.update {
            it.copy(screenViewState = viewState)
        }
    }
}