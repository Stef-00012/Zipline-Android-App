package com.stefdp.zipline.screens.settings

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.stefdp.zipline.Logger
import com.stefdp.zipline.network.models.Export
import com.stefdp.zipline.network.models.User
import com.stefdp.zipline.network.models.UserSession
import com.stefdp.zipline.network.models.UserViewSettingsAlign
import com.stefdp.zipline.network.models.requests.UpdateCurrentUserBody
import com.stefdp.zipline.network.models.responses.GetServerVersionResponse
import com.stefdp.zipline.network.requests.UpdateCurrentUserResult
import com.stefdp.zipline.network.requests.downloadExport
import com.stefdp.zipline.network.requests.getExports
import com.stefdp.zipline.network.requests.getServerVersion
import com.stefdp.zipline.network.requests.getSessions
import com.stefdp.zipline.network.requests.getTokenWithToken
import com.stefdp.zipline.network.requests.removeCurrentUserAvatar
import com.stefdp.zipline.network.requests.startExport as apiStartExport
import com.stefdp.zipline.network.requests.deleteExport as apiDeleteExport
import com.stefdp.zipline.network.requests.deleteSession as apiDeleteSession
import com.stefdp.zipline.network.requests.logout as apiLogout
import com.stefdp.zipline.network.requests.updateCurrentUser
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.utils.STORAGE_ADMIN_EXPORT_DOWNLOAD_FOLDER_KEY
import com.stefdp.zipline.utils.STORAGE_DEFAULT_DOMAIN_KEY
import com.stefdp.zipline.utils.STORAGE_EXPORT_DOWNLOAD_FOLDER_KEY
import com.stefdp.zipline.utils.STORAGE_FILE_DOWNLOAD_FOLDER_KEY
import com.stefdp.zipline.utils.STORAGE_FOLDER_EXPORT_DOWNLOAD_FOLDER_KEY
import com.stefdp.zipline.utils.STORAGE_SERVER_URL_KEY
import com.stefdp.zipline.utils.STORAGE_TOKEN_KEY
import com.stefdp.zipline.utils.STORAGE_UNLOCK_WITH_BIOMETRICS_KEY
import com.stefdp.zipline.utils.SecureStorage
import com.stefdp.zipline.utils.StorageUtil
import com.stefdp.zipline.utils.getDisplayPath
import com.stefdp.zipline.utils.getServerScheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URI

enum class SettingCategory(
    val value: String,
    val categoryName: String
) {
    USER(
        value = "USER",
        categoryName = "User"
    ),

    AVATAR(
        value = "AVATAR",
        categoryName = "Avatar"
    ),

    VIEWING_FILES(
        value = "VIEWING_FILES",
        categoryName = "Viewing Files"
    ),

    EXPORT_FILES(
        value = "EXPORT_FILES",
        categoryName = "Export Files"
    ),

    APP_SETTINGS(
        value = "APP_SETTINGS",
        categoryName = "App Settings"
    ),

    SESSIONS(
        value = "SESSIONS",
        categoryName = "Sessions"
    );

    override fun toString(): String = value
}

enum class UpdateDownloadFolderType {
    EXPORT,
    SERVER_EXPORT,
    FILE,
    FOLDER_EXPORT
}

data class SettingsUiState(
    val isLoading: Boolean = false,
    val token: String? = null,
    val exports: List<Export>? = null,
    val ziplineVersion: GetServerVersionResponse? = null,
    val hasNotificationPermission: Boolean = false,
    val selectedCategory: Set<String> = setOf(SettingCategory.USER.toString()),
    val settingsUpdateTick: Int = 0,
    val biometricAuthenticationEnabled: Boolean = false,
    val tokenInput: TextFieldValue = TextFieldValue(token ?: ""),
    val usernameInput: TextFieldValue = TextFieldValue(""),
    val passwordInput: TextFieldValue = TextFieldValue(""),
    val newAvatar: String? = null,
    val enableViewRoutes: Boolean = false,
    val showMimetype: Boolean = false,
    val showTags: Boolean = false,
    val showFolder: Boolean = false,
    val viewContent: TextFieldValue = TextFieldValue(""),
    val selectedViewContentAlignment: Set<String> = setOf(UserViewSettingsAlign.LEFT.toString()),
    val enableEmbed: Boolean = false,
    val embedTitle: TextFieldValue = TextFieldValue(""),
    val embedDescription: TextFieldValue = TextFieldValue(""),
    val embedSiteName: TextFieldValue = TextFieldValue(""),
    val embedColor: Color = Color.Black,
    val selectedExportUri: Uri? = null,
    val selectedExportPath: String? = null,
    val updateDownloadFolderType: UpdateDownloadFolderType = UpdateDownloadFolderType.FILE,
    val selectedDefaultDomain: Set<String> = setOf("default"),
    val sessions: List<UserSession>? = emptyList(),
    val currentSession: UserSession? = null,
    val deleteSession: UserSession? = null,
    val deleteAllSessionsPopupOpen: Boolean = false,
)

class SettingsViewModel : ViewModel() {
    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    fun initData(
        context: Context
    ) {
        refreshToken(context)
        refreshExports(context)
        refreshVersion(context)
        refreshBiometricAuthenticationEnabled(context)
        refreshSelectedExportPath(context)
        refreshDefaultDomain(context)
        refreshSessions(context)
    }

    fun openDeleteAllSessionsPopup() {
        _state.update {
            it.copy(deleteAllSessionsPopupOpen = true)
        }
    }

    fun closeDeleteAllSessionsPopup() {
        _state.update {
            it.copy(deleteAllSessionsPopupOpen = false)
        }
    }

    fun setHasNotificationPermission(hasPermission: Boolean) {
        _state.update {
            it.copy(hasNotificationPermission = hasPermission)
        }
    }

    fun setSelectedCategory(category: Set<String>) {
        _state.update {
            it.copy(selectedCategory = category)
        }
    }
    
    fun setUsernameInput(usernameInput: TextFieldValue) {
        _state.update {
            it.copy(usernameInput = usernameInput)
        }
    }
    
    fun setPasswordInput(passwordInput: TextFieldValue) {
        _state.update {
            it.copy(passwordInput = passwordInput)
        }
    }
    
    fun setNewAvatar(newAvatar: String?) {
        _state.update {
            it.copy(newAvatar = newAvatar)
        }
    }
    
    fun setEnableViewRoutes(enable: Boolean) {
        _state.update {
            it.copy(enableViewRoutes = enable)
        }
    }
    
    fun setShowMimetype(show: Boolean) {
        _state.update {
            it.copy(showMimetype = show)
        }
    }
    
    fun setShowTags(show: Boolean) {
        _state.update {
            it.copy(showTags = show)
        }
    }

    fun setShowFolder(show: Boolean) {
        _state.update {
            it.copy(showFolder = show)
        }
    }

    fun setViewContent(content: TextFieldValue) {
        _state.update {
            it.copy(viewContent = content)
        }
    }

    fun setSelectedViewContentAlignment(alignment: Set<String>) {
        _state.update {
            it.copy(selectedViewContentAlignment = alignment)
        }
    }

    fun setEnableEmbed(enable: Boolean) {
        _state.update {
            it.copy(enableEmbed = enable)
        }
    }

    fun setEmbedTitle(title: TextFieldValue) {
        _state.update {
            it.copy(embedTitle = title)
        }
    }

    fun setEmbedDescription(description: TextFieldValue) {
        _state.update {
            it.copy(embedDescription = description)
        }
    }

    fun setEmbedSiteName(siteName: TextFieldValue) {
        _state.update {
            it.copy(embedSiteName = siteName)
        }
    }

    fun setEmbedColor(color: Color) {
        _state.update {
            it.copy(embedColor = color)
        }
    }
    
    fun resetInputs(user: User?) {
        val color = user?.view?.embedColor?.toColorInt()

        _state.update {
            it.copy(
                tokenInput = TextFieldValue(it.token ?: ""),
                usernameInput = TextFieldValue(user?.username ?: ""),
                passwordInput = TextFieldValue(""),
                newAvatar = null,
                enableViewRoutes = user?.view?.enabled ?: false,
                showMimetype = user?.view?.showMimetype ?: false,
                showTags = user?.view?.showTags ?: false,
                showFolder = user?.view?.showFolder ?: false,
                viewContent = TextFieldValue(user?.view?.content ?: ""),
                selectedViewContentAlignment = setOf((user?.view?.align ?: UserViewSettingsAlign.LEFT).toString()),
                enableEmbed = user?.view?.embed ?: false,
                embedTitle = TextFieldValue(user?.view?.embedTitle ?: ""),
                embedDescription = TextFieldValue(user?.view?.embedDescription ?: ""),
                embedSiteName = TextFieldValue(user?.view?.embedSiteName ?: ""),
                embedColor = if (color != null) Color(color) else Color.Black,
            )
        }
    }

    fun setSelectedExportUri(
        context: Context,
        exportUri: Uri?
    ) {
        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)
            secureStore.set(STORAGE_EXPORT_DOWNLOAD_FOLDER_KEY, exportUri.toString())

            _state.update {
                it.copy(
                    selectedExportUri = exportUri,
                    selectedExportPath = exportUri?.let { uri -> getDisplayPath(uri) }
                )
            }
        }
    }

    fun setUpdateDownloadFolderType(type: UpdateDownloadFolderType) {
        _state.update {
            it.copy(updateDownloadFolderType = type)
        }
    }

    fun setSelectedDefaultDomain(context: Context, domain: Set<String>) {
        viewModelScope.launch {
            _state.update {
                it.copy(selectedDefaultDomain = domain)
            }

            val secureStore = SecureStorage.getInstance(context)

            val serverUrl = secureStore.get(STORAGE_SERVER_URL_KEY)

            val selectedDomain = domain.firstOrNull() ?: "default"

            secureStore.set(
                STORAGE_DEFAULT_DOMAIN_KEY,
                if (selectedDomain == "default" && serverUrl != null) {
                    serverUrl
                } else {
                    val scheme = getServerScheme(serverUrl)

                    "$scheme://$selectedDomain"
                }
            )
        }
    }

    fun setDeleteSession(session: UserSession?) {
        _state.update {
            it.copy(deleteSession = session)
        }
    }

    fun updateDownloadFolder(
        context: Context,
        uri: Uri,
    ) {
        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)

            val key = when (_state.value.updateDownloadFolderType) {
                UpdateDownloadFolderType.EXPORT -> STORAGE_EXPORT_DOWNLOAD_FOLDER_KEY
                UpdateDownloadFolderType.SERVER_EXPORT -> STORAGE_ADMIN_EXPORT_DOWNLOAD_FOLDER_KEY
                UpdateDownloadFolderType.FILE -> STORAGE_FILE_DOWNLOAD_FOLDER_KEY
                UpdateDownloadFolderType.FOLDER_EXPORT -> STORAGE_FOLDER_EXPORT_DOWNLOAD_FOLDER_KEY
            }

            secureStore.set(key, uri.toString())
        }
    }

    fun updateUser(
        context: Context,
        data: UpdateCurrentUserBody? = null,
        updateAvatar: Boolean = false,
        onError: (List<String>) -> Unit = {},
        onSuccess: () -> Unit = {},
        localUpdateLoggedUser: suspend () -> Result<User>,
        localUpdateLoggedUserAvatar: suspend () -> Result<String> = { Result.success("") }
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            if (data != null) {
                val updateServerSettingsRes = updateCurrentUser(
                    context = context,
                    data = data
                )

                updateServerSettingsRes
                    .onSuccess { response ->
                        if (response is UpdateCurrentUserResult.Error) {
                            val errors = response.error.issues?.map { error -> "${error.instancePath}: ${error.message}." } ?: listOf(response.error.message ?: response.error.error)

                            onError(errors)

                            _state.update {
                                it.copy(isLoading = false)
                            }
                        } else if (response is UpdateCurrentUserResult.Success) {
                            localUpdateLoggedUser()

                            if (updateAvatar) {
                                localUpdateLoggedUserAvatar()
                            }

                            onSuccess()

                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    settingsUpdateTick = it.settingsUpdateTick + 1
                                )
                            }
                        }
                    }
                    .onFailure { error ->
                        val errors = listOf(error.message ?: "Something went wrong...")

                        onError(errors)

                        _state.update {
                            it.copy(isLoading = false)
                        }
                    }
            } else {
                localUpdateLoggedUser()

                onSuccess()

                _state.update {
                    it.copy(
                        isLoading = false,
                        settingsUpdateTick = it.settingsUpdateTick + 1
                    )
                }
            }
        }
    }

    fun logout(
        context: Context,
        navController: NavHostController,
        localUpdateLoggedUser: suspend () -> Result<User>,
    ) {
        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)

            secureStore.del(STORAGE_TOKEN_KEY)
            secureStore.del(STORAGE_SERVER_URL_KEY)

            apiLogout(context)

            localUpdateLoggedUser()

            navController.navigate(LoginScreen) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    fun updateUserAvatar(
        localUpdateLoggedUserAvatar: suspend () -> Result<String>
    ) {
        viewModelScope.launch {
            localUpdateLoggedUserAvatar()
        }
    }

    fun removeUserAvatar(
        context: Context,
        onError: (List<String>) -> Unit,
        onSuccess: () -> Unit,
        localUpdateLoggedUser: suspend () -> Result<User>,
        localUpdateLoggedUserAvatar: suspend () -> Result<String>
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val removeAvatarRes = removeCurrentUserAvatar(context)

            removeAvatarRes
                .onSuccess { response ->
                    if (response is UpdateCurrentUserResult.Error) {
                        val errors = response.error.issues?.map { error -> "${error.instancePath}: ${error.message}." } ?: listOf(response.error.message ?: response.error.error)

                        onError(errors)

                        _state.update {
                            it.copy(isLoading = false)
                        }
                    } else if (response is UpdateCurrentUserResult.Success) {
                        localUpdateLoggedUserAvatar()

                        onSuccess()

                        updateUser(
                            context = context,
                            localUpdateLoggedUser = localUpdateLoggedUser
                        )

                        localUpdateLoggedUserAvatar()

                        _state.update {
                            it.copy(isLoading = false)
                        }
                    }
                }
                .onFailure { error ->
                    val errors = listOf(error.message ?: "Something went wrong...")

                    onError(errors)

                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
        }
    }

    fun startExport(
        context: Context,
        onError: (List<String>) -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val startExportRes = apiStartExport(context)

            startExportRes
                .onSuccess {
                    refreshExports(context)

                    onSuccess()
                }
                .onFailure { error ->
                    onError(listOf(error.message ?: "Something went wrong..."))

                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
        }
    }

    fun deleteExport(
        context: Context,
        exportId: String,
        onError: (List<String>) -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val deleteExportRes = apiDeleteExport(
                context = context,
                exportId = exportId
            )

            deleteExportRes
                .onSuccess { response ->
                    if (response.deleted) {
                        refreshExports(context)

                        onSuccess()
                    } else {
                        onError(
                            listOf("Failed to delete export.")
                        )

                        _state.update {
                            it.copy(isLoading = false)
                        }
                    }
                }
                .onFailure { error ->
                    val errors = listOf(error.message ?: "Something went wrong...")

                    onError(errors)

                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
        }
    }

    fun refreshBiometricAuthenticationEnabled(context: Context) {
        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)

            _state.update {
                it.copy(
                    biometricAuthenticationEnabled = secureStore.get(STORAGE_UNLOCK_WITH_BIOMETRICS_KEY).toBoolean()
                )
            }
        }
    }

    fun setBiometricAuthenticationEnabled(context: Context, enabled: Boolean) {
        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)
            secureStore.set(STORAGE_UNLOCK_WITH_BIOMETRICS_KEY, enabled.toString())

            _state.update {
                it.copy(
                    biometricAuthenticationEnabled = enabled
                )
            }
        }
    }

    fun refreshSessions(context: Context) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val sessionsRes = getSessions(context)

            Logger.debug("SettingsViewModel", "Fetched sessions success: ${sessionsRes.isSuccess}")

            sessionsRes
                .onSuccess { sessionsResponse ->
                    _state.update {
                        it.copy(
                            sessions = sessionsResponse.other,
                            currentSession = sessionsResponse.current
                        )
                    }
                }

            _state.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun refreshDefaultDomain(context: Context) {
        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)
            val defaultDomain = secureStore.get(STORAGE_DEFAULT_DOMAIN_KEY)

            val domain = defaultDomain?.let { URI(it).host ?: it } ?: "default"

            _state.update {
                it.copy(
                    selectedDefaultDomain = setOf(domain)
                )
            }
        }
    }

    fun refreshSelectedExportPath(context: Context) {
        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)
            val exportDownloadFolder = secureStore.get(STORAGE_EXPORT_DOWNLOAD_FOLDER_KEY)

            if (exportDownloadFolder != null) {
                val uri = exportDownloadFolder.toUri()

                _state.update {
                    it.copy(
                        selectedExportUri = uri,
                        selectedExportPath = getDisplayPath(uri)
                    )
                }
            }
        }
    }

    fun refreshToken(context: Context) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val tokenRes = getTokenWithToken(context)

            tokenRes
                .onSuccess { token ->
                    _state.update {
                        it.copy(
                            token = token.token,
                            tokenInput = TextFieldValue(token.token ?: ""),
                            isLoading = false
                        )
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
        }
    }

    fun refreshExports(context: Context) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val exportsRes = getExports(context)

            exportsRes
                .onSuccess { exports ->
                    _state.update {
                        it.copy(
                            exports = exports,
                            isLoading = false
                        )
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
        }
    }

    fun refreshVersion(context: Context) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val versionRes = getServerVersion(context)

            versionRes
                .onSuccess { version ->
                    _state.update {
                        it.copy(
                            ziplineVersion = version,
                            isLoading = false
                        )
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
        }
    }

    fun performExportDownload(
        context: Context,
        export: Export,
        exportUri: Uri,
        sendNotification: (content: @Composable () -> Unit) -> Unit,
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val exportFits = StorageUtil.canFitFile(
                context = context,
                uri = exportUri,
                fileSize = export.size.toLong()
            )

            if (!exportFits) {
                sendNotification {
                    Text(
                        text = "Not enough space in the selected directory to download the export"
                    )
                }

                _state.update {
                    it.copy(isLoading = false)
                }

                return@launch
            }

            val exportFitsCache = StorageUtil.canFitInternalCache(
                context = context,
                fileSize = export.size.toLong()
            )

            if (!exportFitsCache) {
                sendNotification {
                    Text(
                        text = "Not enough space in the internal cache to download the export"
                    )
                }

                _state.update {
                    it.copy(isLoading = false)
                }

                return@launch
            }

            Logger.debug("ExportFiles", "Starting download of export ${export.id} with size ${export.size} bytes to ${_state.value.selectedExportPath}")

            withContext(Dispatchers.IO) {
                sendNotification {
                    Text(
                        text = "Starting download..."
                    )
                }

                val fileName = "export_${export.id}_${System.currentTimeMillis()}.zip"

                val tempFile = java.io.File(context.cacheDir, fileName)
                val tempDestinationPath = tempFile.absolutePath

                if (tempFile.exists()) tempFile.delete()

                val downloadRes = downloadExport(
                    context = context,
                    exportId = export.id,
                    destinationPath = tempDestinationPath,
                    notificationTitle = "Downloading export",
                    notificationContent = "Downloading export ${export.id}",
                )

                downloadRes
                    .onSuccess {
                        try {
                            val docUri =
                                DocumentsContract.buildDocumentUriUsingTree(
                                    exportUri,
                                    DocumentsContract.getTreeDocumentId(
                                        exportUri
                                    )
                                )

                            val fileUri = DocumentsContract.createDocument(
                                context.contentResolver,
                                docUri,
                                "application/zip",
                                fileName
                            )

                            if (fileUri != null) {
                                context.contentResolver.openOutputStream(fileUri)
                                    ?.use { out ->
                                        tempFile.inputStream().use { inp ->
                                            inp.copyTo(out)
                                        }
                                    }

                                sendNotification {
                                    Text(
                                        text = "Export downloaded to ${_state.value.selectedExportPath}/$fileName"
                                    )
                                }
                            } else {
                                sendNotification {
                                    Text(
                                        text = "Failed to create file in selected directory"
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            Logger.error(
                                "ExportFiles",
                                "Failed to copy file to selected directory",
                                e
                            )

                            sendNotification {
                                Text(
                                    text = "Failed to copy file to selected directory: ${e.message}"
                                )
                            }
                        } finally {
                            tempFile.delete()
                        }
                    }
                    .onFailure {
                        Logger.error("ExportFiles", "Failed to download export", it)

                        sendNotification {
                             Text(
                                 text = "Failed to download export: ${it.message}"
                             )
                        }
                    }
            }

            _state.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun deleteSession(
        context: Context,
        onError: (String) -> Unit,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            if (_state.value.deleteSession == null) return@launch

            _state.update {
                it.copy(isLoading = true)
            }

            val deleteRes = apiDeleteSession(
                context = context,
                sessionId = _state.value.deleteSession!!.id
            )

            deleteRes
                .onSuccess { sessionsRes ->
                    _state.update {
                        it.copy(sessions = sessionsRes.other)
                    }

                    refreshSessions(context)

                    onSuccess()
                }
                .onFailure { error ->
                    onError("Failed to log out of session: ${error.message}")
                }

            _state.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun deleteAllSessions(
        context: Context,
        onError: (String) -> Unit,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val deleteRes = apiDeleteSession(
                context = context,
                all = true,
                sessionId = ""
            )

            deleteRes
                .onSuccess { sessionsRes ->
                    _state.update {
                        it.copy(sessions = sessionsRes.other)
                    }

                    refreshSessions(context)

                    onSuccess()
                }
                .onFailure { error ->
                    onError("Failed to delete all sessions: ${error.message}")
                }

            _state.update {
                it.copy(isLoading = false)
            }
        }
    }
}