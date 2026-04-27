package com.stefdp.zipline

import android.util.Log
import androidx.compose.runtime.Composable
import com.stefdp.zipline.screens.AdminInvitesScreen
import com.stefdp.zipline.screens.AppScreen
import com.stefdp.zipline.screens.FoldersScreen
import com.stefdp.zipline.screens.LoadingScreen
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.screens.SettingsScreen

const val IS_DEBUG = true
const val DEBUG_NETWORK = false

val DEBUG_SCREEN: AppScreen = LoadingScreen//LoadingScreen
const val DEBUG_USER_ID = "cmgt5i4ei00ct01n3fou6fd65"

@Composable
fun DebugWrapper(content: @Composable () -> Unit) {
    content()
}

class Logger {
    companion object {
        fun debug(tag: String, message: String, throwable: Throwable? = null) {
            Log.d(tag, message, throwable)
        }

        fun error(tag: String, message: String, throwable: Throwable? = null) {
            Log.e(tag, message, throwable)
        }

        fun info(tag: String, message: String, throwable: Throwable? = null) {
            Log.i(tag, message, throwable)
        }

        fun warn(tag: String, message: String, throwable: Throwable? = null) {
            Log.w(tag, message, throwable)
        }

        fun verbose(tag: String, message: String, throwable: Throwable? = null) {
            Log.v(tag, message, throwable)
        }

        fun wtf(tag: String, message: String, throwable: Throwable? = null) {
            Log.wtf(tag, message, throwable)
        }
    }
}