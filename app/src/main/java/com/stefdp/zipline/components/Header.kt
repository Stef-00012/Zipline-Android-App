package com.stefdp.zipline.components

import androidx.compose.foundation.layout.Box
import com.stefdp.zipline.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.stefdp.zipline.screens.BiometricAuthScreen
import com.stefdp.zipline.screens.LoadingScreen
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.screens.SettingsScreen

@Composable
fun Header(
    navController: NavHostController,
    onMenuClick: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val invalidRoutes = listOf(
        LoginScreen::class.qualifiedName,
        LoadingScreen::class.qualifiedName,
        BiometricAuthScreen::class.qualifiedName
    )

    if (currentDestination?.route in invalidRoutes) return

    val outlineColor = MaterialTheme.colorScheme.outline

    Surface(
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = outlineColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 5.dp.toPx()
                )
            },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .statusBarsPadding()
        ) {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.menu),
                    contentDescription = "Open Sidebar",
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                val isInSettings = currentDestination?.route == SettingsScreen::class.qualifiedName

                UserAvatar(
                    enabled = !isInSettings,
                    onClick = {
                        navController.navigate(SettingsScreen)
                    },
                )
            }
        }
    }
}