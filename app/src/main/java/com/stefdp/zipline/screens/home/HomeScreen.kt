package com.stefdp.zipline.screens.home

import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity
) {
    Text("Home Screen")
}