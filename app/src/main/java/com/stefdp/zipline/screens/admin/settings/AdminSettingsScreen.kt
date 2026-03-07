package com.stefdp.zipline.screens.admin.settings

import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController

@Composable
fun AdminSettingsScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity
) {
    Text("Admin Settings Screen")
}