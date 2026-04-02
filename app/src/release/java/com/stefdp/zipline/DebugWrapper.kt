package com.stefdp.zipline

import androidx.compose.runtime.Composable
import com.stefdp.zipline.screens.AppScreen
import com.stefdp.zipline.screens.LoadingScreen

const val IS_DEBUG = false
const val DEBUG_NETWORK = false

val DEBUG_SCREEN: AppScreen = LoadingScreen
const val DEBUG_USER_ID = ""

@Composable
fun DebugWrapper(content: @Composable () -> Unit) {}

class Logger {
    fun debug(tag: String, message: String, throwable: Throwable? = null) {}

    fun error(tag: String, message: String, throwable: Throwable? = null) {}

    fun info(tag: String, message: String, throwable: Throwable? = null) {}

    fun warn(tag: String, message: String, throwable: Throwable? = null) {}

    fun verbose(tag: String, message: String, throwable: Throwable? = null) {}

    fun wtf(tag: String, message: String, throwable: Throwable? = null) {}
}