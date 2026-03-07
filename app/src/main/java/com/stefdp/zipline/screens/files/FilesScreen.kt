package com.stefdp.zipline.screens.files

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController

@Composable
fun FilesScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    userId: String? = null
) {
    Column {
        Text("Files Screen")
        Text("userId: $userId")
    }
}