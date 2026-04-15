package com.stefdp.zipline.screens.loading

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.zipline.network.models.PublicServerConfig
import com.stefdp.zipline.network.models.User
import com.stefdp.zipline.network.models.WebSettings
import com.stefdp.zipline.network.models.responses.GetServerVersionResponse
import com.stefdp.zipline.utils.STORAGE_SERVER_URL_KEY
import com.stefdp.zipline.utils.STORAGE_TOKEN_KEY
import com.stefdp.zipline.utils.STORAGE_UNLOCK_WITH_BIOMETRICS_KEY
import com.stefdp.zipline.utils.SecureStorage
import com.stefdp.zipline.utils.getBiometricStatus
import com.stefdp.zipline.utils.minimumZiplineVersion
import io.github.z4kn4fein.semver.toVersionOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoadingUiState(
    val isLogging: Boolean = false
)

class LoadingViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoadingUiState())
    val state: StateFlow<LoadingUiState> = _state.asStateFlow()

    fun startLoading(
        context: Context,
        onError: (String?) -> Unit,
        onSuccess: (Boolean) -> Unit,
        updateServerVersion: suspend () -> Result<GetServerVersionResponse>,
        updateLoggedUser: suspend () -> Result<User>,
        updatePublicSettings: suspend () -> Result<PublicServerConfig>,
        updateWebSettings: suspend () -> Result<WebSettings>,
        updateLoggedUserAvatar: suspend () -> Result<String>,
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLogging = true)
            }

            val secureStore = SecureStorage.getInstance(context)

            val unlockWithBiometrics = secureStore.get(STORAGE_UNLOCK_WITH_BIOMETRICS_KEY)?.toBoolean() ?: false
            val serverUrl = secureStore.get(STORAGE_SERVER_URL_KEY)
            val token = secureStore.get(STORAGE_TOKEN_KEY)

            if (token == null || serverUrl == null) {
                onError(null)

                _state.update {
                    it.copy(isLogging = false)
                }

                return@launch
            }

            val biometricAuthenticationStatus = getBiometricStatus(context)

            val serverVersionRes = updateServerVersion()

            serverVersionRes
                .onSuccess { versionData ->
                    val version = versionData.details.version.toVersionOrNull(strict = false)

                    if (version == null) {
                        onError(null)

                        _state.update {
                            it.copy(isLogging = false)
                        }

                        return@launch
                    } else if (version < minimumZiplineVersion) {
                        onError("You are currently running Zipline v$version. Please update to at least Zipline v${minimumZiplineVersion}.")

                        _state.update {
                            it.copy(isLogging = false)
                        }

                        return@launch
                    }
                }
                .onFailure { error ->
                    onError("Failed to fetch server version, make sure you are running Zipline v$minimumZiplineVersion or greater and have the \"Version Checking\" feature enabled (${error.message})")

                    _state.update {
                        it.copy(isLogging = false)
                    }

                    return@launch
                }

            val newUserStatsRes = updateLoggedUser()

            newUserStatsRes
                .onFailure {
                    onError(null)

                    _state.update {
                        it.copy(isLogging = false)
                    }

                    return@launch
                }
                .onSuccess {
                    updatePublicSettings()
                    updateWebSettings()
                    updateLoggedUserAvatar()

                    onSuccess(unlockWithBiometrics && biometricAuthenticationStatus == BiometricManager.BIOMETRIC_SUCCESS)

                    _state.update {
                        it.copy(isLogging = false)
                    }
                }
        }
    }
}