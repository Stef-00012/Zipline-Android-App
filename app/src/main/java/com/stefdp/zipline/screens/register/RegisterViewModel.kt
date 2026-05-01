package com.stefdp.zipline.screens.register

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.zipline.Logger
import com.stefdp.zipline.network.requests.getPublicConfig
import com.stefdp.zipline.network.requests.register
import com.stefdp.zipline.screens.urls.components.urlRegex
import com.stefdp.zipline.utils.DomainRegex
import com.stefdp.zipline.utils.IPRegex
import com.stefdp.zipline.utils.STORAGE_SERVER_URL_KEY
import com.stefdp.zipline.utils.SecureStorage
import com.stefdp.zipline.utils.minimumZiplineVersion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val isLoading: Boolean = false,
    val supportsRegistration: Boolean = true,
    val serverUrl: TextFieldValue = TextFieldValue(""),
    val username: TextFieldValue = TextFieldValue(""),
    val password: TextFieldValue = TextFieldValue(""),
    val anonymizeDeviceInfo: Boolean = false,
    val agreeTos: Boolean = false,
    val isInsecureUrl: Boolean = false,
    val hasAcknowledgedInsecureUrlWarning: Boolean = false,
)

class RegisterViewModel : ViewModel() {
    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    fun init(context: Context, serverUrl: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    serverUrl = TextFieldValue(serverUrl),
                    isInsecureUrl = serverUrl.lowercase().startsWith("http://"),
                    hasAcknowledgedInsecureUrlWarning = false,
                )
            }

            if (DomainRegex.matches(serverUrl) || IPRegex.matches(serverUrl)) {
                val publicSettings = getPublicConfig(
                    context = context,
                    serverUrl = serverUrl.lowercase()
                )

                publicSettings.onSuccess { settings ->
                    _state.update {
                        it.copy(
                            serverUrl = TextFieldValue(serverUrl),
                            supportsRegistration = settings.features.userRegistration
                        )
                    }
                }
            }
        }
    }

    fun setHasAcknowledgedInsecureUrlWarning(acknowledged: Boolean) {
        _state.update {
            it.copy(hasAcknowledgedInsecureUrlWarning = acknowledged)
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

    fun setAnonymizeDeviceInfo(anonymize: Boolean) {
        _state.update {
            it.copy(anonymizeDeviceInfo = anonymize)
        }
    }

    fun setAgreeTos(agree: Boolean) {
        _state.update {
            it.copy(agreeTos = agree)
        }
    }

    fun onRegister(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        Logger.debug("RegisterViewModel", "Starting register process")

        viewModelScope.launch {
            val isValidDomain = DomainRegex.matches(_state.value.serverUrl.text.lowercase()) || IPRegex.matches(_state.value.serverUrl.text.lowercase())

            if (!isValidDomain) {
                onError("Please enter a valid server URL")

                return@launch
            }

            _state.update {
                it.copy(isLoading = true)
            }

            val secureStore = SecureStorage.getInstance(context)

            secureStore.set(STORAGE_SERVER_URL_KEY, _state.value.serverUrl.text.lowercase())

            val registerRes = register(
                context = context,
                username = _state.value.username.text,
                password = _state.value.password.text,
                anonymizeDeviceInfo = _state.value.anonymizeDeviceInfo
            )

            registerRes
                .onSuccess { response ->
                    if (response.user != null) {
                        onSuccess()

                        return@launch
                    } else {
                        secureStore.del(STORAGE_SERVER_URL_KEY)

                        onError("Failed to register, make sure you are running Zipline v$minimumZiplineVersion or greater")

                        _state.update {
                            it.copy(isLoading = false)
                        }

                        return@launch
                    }
                }
                .onFailure { error ->
                    secureStore.del(STORAGE_SERVER_URL_KEY)

                    onError("Failed to register, make sure you are running Zipline v$minimumZiplineVersion or greater (${error.message})")

                    _state.update {
                        it.copy(isLoading = false)
                    }

                    return@launch
                }
        }
    }
}