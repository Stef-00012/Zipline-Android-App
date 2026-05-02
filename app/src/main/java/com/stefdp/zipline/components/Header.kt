package com.stefdp.zipline.components

import android.app.Activity.RESULT_OK
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import com.stefdp.zipline.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.stefdp.zipline.screens.BiometricAuthScreen
import com.stefdp.zipline.screens.LoadingScreen
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.screens.SettingsScreen
import com.stefdp.zipline.updatemanager.UpdateManager
import kotlinx.coroutines.launch

@Composable
fun Header(
    activity: FragmentActivity,
    context: Context,
    navController: NavHostController,
    onMenuClick: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val outlineColor = MaterialTheme.colorScheme.outline

    var isUpdateAvailable by rememberSaveable { mutableStateOf(false) }

    val updateLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode != RESULT_OK) {
            Notification.show(
                activity
            ) {
                Text(
                    text = "Update failed or was cancelled"
                )
            }
        }
    }

    val updateManager = UpdateManager(
        activity = activity,
        context = context,
        updateLauncher = updateLauncher
    )

//    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val update = updateManager.checkForUpdates()

        isUpdateAvailable = update

        if (update) {
            Notification.show(
                activity
            ) {
                Text("Update Available")

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        navController.navigate(SettingsScreen(
                            update = true,
                            updateSwitchCategory = true
                        ))
                    }
                ) {
                    Text("Update")
                }
            }
        }
    }

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
                val isInSettings = currentDestination?.route?.startsWith(SettingsScreen::class.qualifiedName ?: "") == true

                UserAvatar(
                    enabled = !isInSettings,
                    onClick = {
                        navController.navigate(SettingsScreen(
                            update = isUpdateAvailable
                        ))
                    },
                )
            }
        }
    }
}