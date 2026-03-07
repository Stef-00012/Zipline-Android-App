package com.stefdp.zipline.screens.metrics

import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController

@Composable
fun MetricsScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity
) {
    Text("Metrics Screen")
}