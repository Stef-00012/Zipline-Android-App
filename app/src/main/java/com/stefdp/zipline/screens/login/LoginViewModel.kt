package com.stefdp.zipline.screens.login

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.zipline.Logger
import com.stefdp.zipline.network.models.PublicServerConfig
import com.stefdp.zipline.network.models.User
import com.stefdp.zipline.network.models.WebSettings
import com.stefdp.zipline.network.models.responses.GetServerVersionResponse
import com.stefdp.zipline.network.requests.LoginResult
import com.stefdp.zipline.network.requests.getPublicConfig
import com.stefdp.zipline.network.requests.getToken
import com.stefdp.zipline.network.requests.login
import com.stefdp.zipline.utils.DomainRegex
import com.stefdp.zipline.utils.STORAGE_DEFAULT_DOMAIN_KEY
import com.stefdp.zipline.utils.STORAGE_SERVER_URL_KEY
import com.stefdp.zipline.utils.STORAGE_TOKEN_KEY
import com.stefdp.zipline.utils.SecureStorage
import com.stefdp.zipline.utils.minimumZiplineVersion
import io.github.z4kn4fein.semver.toVersionOrNull
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class LoginUiState(
    val isLoading: Boolean = false,
    val showNotificationsPopup: Boolean = false,
    val serverUrl: TextFieldValue = TextFieldValue(""),
    val token: TextFieldValue = TextFieldValue(""),
    val username: TextFieldValue = TextFieldValue(""),
    val password: TextFieldValue = TextFieldValue(""),
    val totp: TextFieldValue = TextFieldValue(""),
    val isTokenLogin: Boolean = false,
    val isTotpRequired: Boolean = false,
    val anonymizeDeviceInfo: Boolean = false,
    val supportsRegistration: Boolean = false,
)

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()
    
    fun init(context: Context, serverUrl: String? = null) {
        if (serverUrl != null) {
            setServerUrl(context, TextFieldValue(serverUrl))
        }
    }

    fun setShowNotificationsPopup(show: Boolean) {
        _state.update {
            it.copy(showNotificationsPopup = show)
        }
    }

    fun closeNotificationsPopup() {
        _state.update {
            it.copy(showNotificationsPopup = false)
        }
    }

    fun setServerUrl(context: Context, url: TextFieldValue) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    serverUrl = url,
                    supportsRegistration = false
                )
            }

            if (DomainRegex.matches(url.text)) {
                val publicSettings = getPublicConfig(
                    context = context,
                    serverUrl = url.text.lowercase()
                )

                publicSettings.onSuccess { settings ->
                    _state.update {
                        it.copy(
                            supportsRegistration = settings.features.userRegistration
                        )
                    }
                }
            }
        }
    }

    fun setToken(token: TextFieldValue) {
        _state.update {
            it.copy(token = token)
        }
    }

    fun setUsername(username: TextFieldValue) {
        _state.update {
            it.copy(username = username)
        }
    }

    fun setPassword(password: TextFieldValue) {
        _state.update {
            it.copy(password = password)
        }
    }

    fun setTotp(totp: TextFieldValue) {
        _state.update {
            it.copy(totp = totp)
        }
    }

    fun setAnonymizeDeviceInfo(anonymize: Boolean) {
        _state.update {
            it.copy(anonymizeDeviceInfo = anonymize)
        }
    }

    fun toggleTokenLogin() {
        _state.update {
            it.copy(isTokenLogin = !it.isTokenLogin)
        }
    }

    fun onLogin(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        updateServerVersion: suspend (context: Context) -> Result<GetServerVersionResponse>,
        updateLoggedUser: suspend (context: Context) -> Result<User>,
        updatePublicSettings: suspend (context: Context) -> Result<PublicServerConfig>,
        updateWebSettings: suspend (context: Context) -> Result<WebSettings>,
        updateLoggedUserAvatar: suspend (context: Context) -> Result<String>,
    ) {
        Logger.debug("LoginViewModel", "Starting login process")

        viewModelScope.launch {
            val isValidDomain = DomainRegex.matches(_state.value.serverUrl.text.lowercase())

            if (!isValidDomain) {
                onError("Please enter a valid server URL")

                return@launch
            }

            _state.update {
                it.copy(isLoading = true)
            }

            val secureStore = SecureStorage.getInstance(context)

            secureStore.set(STORAGE_SERVER_URL_KEY, _state.value.serverUrl.text.lowercase())

            if (_state.value.isTokenLogin) {
                secureStore.set(STORAGE_TOKEN_KEY, _state.value.token.text)
            } else {
                val loginRes = login(
                    context = context,
                    username = _state.value.username.text,
                    password = _state.value.password.text,
                    code = _state.value.totp.text.ifEmpty { null },
                    anonymizeDeviceInfo = _state.value.anonymizeDeviceInfo
                )

                loginRes
                    .onSuccess { loginStatus ->
                        Logger.debug("LoginViewModel", "Login successful, status: $loginStatus")

                        if (loginStatus is LoginResult.TotpRequired) {
                            _state.update {
                                it.copy(
                                    isTotpRequired = true,
                                    isLoading = false
                                )
                            }

                            return@launch
                        } else if (loginStatus is LoginResult.Success) {
                            val authCookie = loginStatus.authCookie

                            val tokenRes = getToken(
                                context = context,
                                cookie = authCookie,
                            )

                            tokenRes
                                .onSuccess { tokenData ->
                                    if (tokenData.token == null) {
                                        onError("Failed to retrieve token")

                                        _state.update {
                                            it.copy(isLoading = false)
                                        }

                                        return@launch
                                    }

                                    secureStore.set(STORAGE_TOKEN_KEY, tokenData.token)
                                }
                                .onFailure { error ->
                                    onError("Failed to fetch user token, make sure you are running Zipline v$minimumZiplineVersion or greater (${error.message})")

                                    _state.update {
                                        it.copy(isLoading = false)
                                    }

                                    return@launch
                                }

                        }
                    }
                    .onFailure { error ->
                        onError("Failed to login, make sure you are running Zipline v$minimumZiplineVersion or greater (${error.message})")

                        _state.update {
                            it.copy(isLoading = false)
                        }

                        return@launch
                    }
            }

            val serverVersionRes = updateServerVersion(context)

            serverVersionRes
                .onSuccess { versionData ->
                    val version = versionData.details.version.toVersionOrNull(strict = false)

                    if (version == null) {
                        onError("Failed to fetch server version, make sure you are running Zipline v$minimumZiplineVersion or greater.")

                        _state.update {
                            it.copy(isLoading = false)
                        }

                        return@launch
                    } else if (version < minimumZiplineVersion) {
                        onError("You are currently running Zipline v$version. Please update to at least Zipline v${minimumZiplineVersion}.")

                        _state.update {
                            it.copy(isLoading = false)
                        }

                        return@launch
                    }
                }
                .onFailure { error ->
                    onError("Failed to fetch server version, make sure you are running Zipline v$minimumZiplineVersion or greater and have the \"Version Checking\" feature enabled (${error.message})")

                    _state.update {
                        it.copy(isLoading = false)
                    }

                    return@launch
                }

            val userStatsRes = updateLoggedUser(context)

            userStatsRes
                .onSuccess {
                    val secureStore = SecureStorage.getInstance(context)

                    secureStore.set(STORAGE_DEFAULT_DOMAIN_KEY, _state.value.serverUrl.text)

                    withContext(NonCancellable) {
                        updatePublicSettings(context)
                        updateWebSettings(context)
                        updateLoggedUserAvatar(context)
                    }

                    _state.update {
                        it.copy(isLoading = false)
                    }

                    onSuccess()
                }
                .onFailure { error ->
                    onError("Failed to fetch user, make sure you are running Zipline v$minimumZiplineVersion or greater (${error.message})")

                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
        }
    }
}