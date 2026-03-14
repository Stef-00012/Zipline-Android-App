package com.stefdp.zipline

import androidx.compose.runtime.Composable
import com.stefdp.zipline.screens.AppScreen
import com.stefdp.zipline.screens.LoadingScreen
import com.stefdp.zipline.screens.MetricsScreen

const val IS_DEBUG = true
const val DEBUG_NETWORK = false

val DEBUG_SCREEN: AppScreen = LoadingScreen//MetricsScreen

@Composable
fun DebugWrapper(content: @Composable () -> Unit) {
    content()
}