package com.stefdp.zipline.screens.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.zipline.network.models.File
import com.stefdp.zipline.network.models.responses.GetStatsResponse
import com.stefdp.zipline.network.requests.getRecentFiles
import com.stefdp.zipline.network.requests.getStats
import com.stefdp.zipline.utils.STORAGE_SERVER_URL_KEY
import com.stefdp.zipline.utils.SecureStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val serverUrl: String? = null,
    val userStats: GetStatsResponse? = null,
    val recentFiles: List<File>? = null,
    val clickedRecentFile: File? = null,
)

class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    fun initData(context: Context) {
        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)

            val serverUrl = secureStore.get(STORAGE_SERVER_URL_KEY)

            _state.update {
                it.copy(serverUrl = serverUrl)
            }
        }
    }

    fun updateData(
        context: Context
    ) {
        viewModelScope.launch {
            val recentFilesRes = getRecentFiles(
                context = context,
                count = 8
            )

            val userStatsRes = getStats(
                context = context,
            )

            _state.update {
                it.copy(
                    recentFiles = recentFilesRes.getOrNull(),
                    userStats = userStatsRes.getOrNull(),
                )
            }
        }
    }

    fun setClickedRecentFile(file: File?) {
        _state.update {
            it.copy(clickedRecentFile = file)
        }
    }
}