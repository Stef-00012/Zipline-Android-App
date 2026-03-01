package com.stefdp.zipline

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stefdp.zipline.screens.AdminInvitesScreen
import com.stefdp.zipline.screens.AdminSettingsScreen
import com.stefdp.zipline.screens.AdminUsersScreen
import com.stefdp.zipline.screens.BiometricAuthScreen
import com.stefdp.zipline.screens.FilesScreen
import com.stefdp.zipline.screens.FoldersScreen
import com.stefdp.zipline.screens.HomeScreen
import com.stefdp.zipline.screens.LoadingScreen
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.screens.MetricsScreen
import com.stefdp.zipline.screens.SettingsScreen
import com.stefdp.zipline.screens.ShortenURLScreen
import com.stefdp.zipline.screens.UploadFileScreen
import com.stefdp.zipline.screens.UploadTextScreen
import com.stefdp.zipline.ui.theme.ZiplineTheme
import com.stefdp.zipline.utils.NetworkMonitor

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZiplineTheme {
                val activity = this@MainActivity

                val navController = rememberNavController()
                val context = LocalContext.current

//                var userStats by rememberSaveable {
//                    mutableStateOf<UserStats?>(null)
//                }

                val networkMonitor = NetworkMonitor(context)

                val isConnected by networkMonitor.isConnected.collectAsState(initial = true)

                // fetch user data

                LaunchedEffect(isConnected) {
                    if (isConnected) {
                        // fetch user data
                    }
                }

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                CompositionLocalProvider() {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            //  header
                        }
                    ) { innerPadding ->
                        Surface(
                            modifier = Modifier.padding(innerPadding),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            ModalNavigationDrawer(
                                modifier = Modifier.padding(innerPadding),
                                drawerState = drawerState,
                                drawerContent = {
//                                    Sidebar(
//                                        onItemClick = { screen ->
//                                            scope.launch { drawerState.close() }
//                                            navController.navigate(screen)
//                                        }
//                                    )
                                }
                            ) {
                                AppNavigation(
                                    navController = navController,
                                    context = context,
                                    activity = activity
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity
) {
    NavHost(
        navController = navController,
        startDestination = LoadingScreen,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(400))
        },
        exitTransition = {
            fadeOut(tween(300))
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(400))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(400))
        }
    ) {
        composable<LoadingScreen> {
//            LoadingScreen(
//                navController = navController,
//                context = context,
//                activity = activity
//            )
        }

        composable<LoginScreen> {
//            LoginScreen(
//                navController = navController,
//                context = context,
//                activity = activity
//            )
        }

        composable<BiometricAuthScreen> {
//            BiometricAuthScreen(
//                navController = navController,
//                context = context,
//                activity = activity
//            )
        }

        composable<HomeScreen> {
//            HomeScreen(
//                navController = navController,
//                context = context,
//                activity = activity
//            )
        }

        composable<SettingsScreen> {
//            SettingsScreen(
//                navController = navController,
//                context = context,
//                activity = activity
//            )
        }

        composable<MetricsScreen> {
//            MetricsScreen(
//                navController = navController,
//                context = context,
//                activity = activity
//            )
        }

        composable<FilesScreen> {
//            FilesScreen(
//                navController = navController,
//                context = context,
//                activity = activity
//            )
        }

        composable<FoldersScreen> {
//            FoldersScreen(
//                navController = navController,
//                context = context,
//                activity = activity
//            )
        }

        composable<UploadFileScreen> {
//            UploadFileScreen(
//                navController = navController,
//                context = context,
//                activity = activity
//            )
        }

        composable<UploadTextScreen> {
//            UploadTextScreen(
//                navController = navController,
//                context = context,
//                activity = activity
//            )
        }

        composable<ShortenURLScreen> {
//            ShortenURLScreen(
//                navController = navController,
//                context = context,
//                activity = activity
//            )
        }

        composable<AdminSettingsScreen> {
//            AdminSettingsScreen(
//                navController = navController,
//                context = context,
//                activity = activity
//            )
        }

        composable<AdminUsersScreen> {
//            AdminUsersScreen(
//                navController = navController,
//                context = context,
//                activity = activity
//            )
        }

        composable<AdminInvitesScreen> {
//            AdminInvitesScreen(
//                navController = navController,
//                context = context,
//                activity = activity
//            )
        }
    }
}