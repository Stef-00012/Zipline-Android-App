package com.stefdp.zipline.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import com.stefdp.zipline.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.stefdp.zipline.LocalLoggedUser
//import com.stefdp.hackatime.LocalLoggedUser
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

    Surface(
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            IconButton(
                onClick = onMenuClick
            ) {
                Icon(
                    painter = painterResource(R.drawable.menu),
                    contentDescription = "Open Sidebar"
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            val username = LocalLoggedUser.current?.username

            Text(
                text = username ?: "Unknown", //stringResource(R.string.unknown_username),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                val isInSettings = currentDestination?.route == SettingsScreen::class.qualifiedName


            }
        }
    }
}