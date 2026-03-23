package com.stefdp.zipline

import androidx.compose.runtime.Composable
import com.stefdp.zipline.screens.AppScreen
import com.stefdp.zipline.screens.UploadTextScreen

const val IS_DEBUG = true
const val DEBUG_NETWORK = false

val DEBUG_SCREEN: AppScreen = UploadTextScreen//LoadingScreen
val DEBUG_USER_ID = "cmgt5i4ei00ct01n3fou6fd65"

@Composable
fun DebugWrapper(content: @Composable () -> Unit) {
    content()
}