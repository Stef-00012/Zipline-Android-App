package com.stefdp.zipline

import androidx.compose.runtime.Composable

const val IS_DEBUG = true
const val DEBUG_NETWORK = false

@Composable
fun DebugWrapper(content: @Composable () -> Unit) {
    content()
}