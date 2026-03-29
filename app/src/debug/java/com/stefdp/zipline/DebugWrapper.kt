package com.stefdp.zipline

import androidx.compose.runtime.Composable
import com.stefdp.zipline.screens.AdminSettingsScreen
import com.stefdp.zipline.screens.AppScreen

const val IS_DEBUG = true
const val DEBUG_NETWORK = false

val DEBUG_SCREEN: AppScreen = AdminSettingsScreen//LoadingScreen
val DEBUG_USER_ID = "cmgt5i4ei00ct01n3fou6fd65"

@Composable
fun DebugWrapper(content: @Composable () -> Unit) {
    content()
}