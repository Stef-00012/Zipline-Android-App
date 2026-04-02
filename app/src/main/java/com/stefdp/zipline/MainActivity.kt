package com.stefdp.zipline

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.stefdp.zipline.components.Header
import com.stefdp.zipline.components.Sidebar
import com.stefdp.zipline.network.models.PublicServerConfig
import com.stefdp.zipline.network.models.User
import com.stefdp.zipline.network.models.WebSettings
import com.stefdp.zipline.network.requests.getAvatar
import com.stefdp.zipline.network.requests.getCurrentUser
import com.stefdp.zipline.network.requests.getPublicConfig
import com.stefdp.zipline.network.requests.getWebServerSettings
import com.stefdp.zipline.ui.theme.ZiplineTheme
import com.stefdp.zipline.utils.NetworkMonitor
import kotlinx.coroutines.launch
import com.stefdp.zipline.screens.*
import com.stefdp.zipline.screens.admin.actions.AdminActionsScreen
import com.stefdp.zipline.screens.admin.invites.AdminInvitesScreen
import com.stefdp.zipline.screens.admin.settings.AdminSettingsScreen
import com.stefdp.zipline.screens.admin.users.AdminUsersScreen
import com.stefdp.zipline.screens.biometricauth.BiometricAuthScreen
import com.stefdp.zipline.screens.files.FilesScreen
import com.stefdp.zipline.screens.folders.FoldersScreen
import com.stefdp.zipline.screens.home.HomeScreen
import com.stefdp.zipline.screens.loading.LoadingScreen
import com.stefdp.zipline.screens.login.LoginScreen
import com.stefdp.zipline.screens.metrics.MetricsScreen
import com.stefdp.zipline.screens.settings.SettingsScreen
import com.stefdp.zipline.screens.urls.UrlsScreen
import com.stefdp.zipline.screens.upload.file.UploadFileScreen
import com.stefdp.zipline.screens.upload.text.UploadTextScreen
import kotlinx.coroutines.delay

const val BASE_CORNER_RADIUS = 10

val LocalLoggedUser = compositionLocalOf<User?> { null }
val LocalUpdateLoggedUser = compositionLocalOf<suspend () -> Result<User>> {
    {
        Result.failure(
            Exception("Placeholder")
        )
    }
}

val LocalLoggedUserAvatar = compositionLocalOf<String?> { null }
val LocalUpdateLoggedUserAvatar = compositionLocalOf<suspend () -> Result<String>> {
    {
        Result.failure(
            Exception("Placeholder")
        )
    }
}

val LocalPublicSettings = compositionLocalOf<PublicServerConfig?> { null }
val LocalUpdatePublicSettings = compositionLocalOf<suspend () -> Result<PublicServerConfig>> {
    {
        Result.failure(
            Exception("Placeholder")
        )
    }
}

val LocalWebSettings = compositionLocalOf<WebSettings?> { null }
val LocalUpdateWebSettings = compositionLocalOf<suspend () -> Result<WebSettings>> {
    {
        Result.failure(
            Exception("Placeholder")
        )
    }
}

// TODO: move all inputs & loading from remember to rememberSaveable
// TODO: move from Toast to Notification
// TODO: create a custom function to parse dates like "30d", "2y" etc. in order to do the next line
// TODO: only show dates smaller than "settings.filesMaxExpiration in upload menu
// TODO: move from Log.* to Logger.*

const val APP_VERSION = "2.0.0"

class MainActivity : FragmentActivity() {
    private var isAppReady by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            !isAppReady
        }

        lifecycleScope.launch {
            delay(100L)
            isAppReady = true
        }

        enableEdgeToEdge()
        setContent {
            ZiplineTheme {
                val activity = this@MainActivity

                val navController = rememberNavController()
                val context = LocalContext.current

                var loggedUser by rememberSaveable {
                   mutableStateOf<User?>(null)
                }

                var loggedUserAvatar by rememberSaveable {
                    mutableStateOf<String?>(null)
                }

                var publicSettings by rememberSaveable {
                    mutableStateOf<PublicServerConfig?>(null)
                }

                var webSettings by rememberSaveable {
                    mutableStateOf<WebSettings?>(null)
                }

                val networkMonitor = NetworkMonitor(context)

                val isConnected by networkMonitor.isConnected.collectAsState(initial = true)

                suspend fun updateLoggedUser(): Result<User> {
                    val tag = "MainActivity[updateLoggedUser]"

                    Log.d(tag, "Checking if user is already logged in...")

                    val currentUserRes = getCurrentUser(
                        context = context
                    )

                    currentUserRes
                        .onSuccess { currentUserData ->
                            if (currentUserData.user == null) return Result.failure(
                                Exception("User is not logged in")
                            )

                            Log.d(tag, "User is logged in as ${currentUserData.user.username}")

                            loggedUser = currentUserData.user

                            return@updateLoggedUser Result.success(currentUserData.user)
                        }
                        .onFailure { error ->
                            Log.d(tag, "User is not logged in")
                            Log.e(tag, "Failed to fetch user stats: ${error.message}")

                            loggedUser = null

                            return@updateLoggedUser Result.failure(error)
                        }

                    return Result.failure(
                        Exception("Something went wrong...")
                    )
                }

                suspend fun updateLoggedUserAvatar(): Result<String> {
                    val tag = "MainActivity[updateLoggedUserAvatar]"

                    val loggedUserAvatarRes = getAvatar(
                        context = context
                    )

                    loggedUserAvatarRes
                        .onSuccess { avatarBase64 ->
                            loggedUserAvatar = avatarBase64

                            return@updateLoggedUserAvatar Result.success(avatarBase64)
                        }
                        .onFailure { error ->
                            Log.e(tag, "Failed to fetch user avatar: ${error.message}")

                            loggedUserAvatar = null

                            return@updateLoggedUserAvatar Result.failure(error)
                        }

                    return Result.failure(
                        Exception("Something went wrong...")
                    )
                }

                suspend fun updatePublicSettings(): Result<PublicServerConfig> {
                    val tag = "MainActivity[updatePublicSettings]"

                    val publicConfigRes = getPublicConfig(
                        context = context
                    )

                    publicConfigRes
                        .onSuccess { publicConfigData ->
                            publicSettings = publicConfigData

                            return@updatePublicSettings Result.success(publicConfigData)
                        }
                        .onFailure { error ->
                            Log.e(tag, "Failed to fetch public server config: ${error.message}")

                            publicSettings = null

                            return@updatePublicSettings Result.failure(error)
                        }

                    return Result.failure(
                        Exception("Something went wrong...")
                    )
                }

                suspend fun updateWebSettings(): Result<WebSettings> {
                    val tag = "MainActivity[updateWebSettings]"

                    val webSettingsRes = getWebServerSettings(
                        context = context
                    )

                    webSettingsRes
                        .onSuccess { webSettingsData ->
                            webSettings = webSettingsData

                            return@updateWebSettings Result.success(webSettingsData)
                        }
                        .onFailure { error ->
                            Log.e(tag, "Failed to fetch web settings: ${error.message}")

                            webSettings = null

                            return@updateWebSettings Result.failure(error)
                        }

                    return Result.failure(
                        Exception("Something went wrong...")
                    )
                }

                LaunchedEffect(isConnected) {
                    if (isConnected) {
                        updateLoggedUser()
                        updateLoggedUserAvatar()
                        updateWebSettings()
                        updatePublicSettings()
                    }
                }

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                CompositionLocalProvider(
                    LocalLoggedUser provides loggedUser,
                    LocalUpdateLoggedUser provides ::updateLoggedUser,
                    LocalLoggedUserAvatar provides loggedUserAvatar,
                    LocalUpdateLoggedUserAvatar provides ::updateLoggedUserAvatar,
                    LocalPublicSettings provides publicSettings,
                    LocalUpdatePublicSettings provides ::updatePublicSettings,
                    LocalWebSettings provides webSettings,
                    LocalUpdateWebSettings provides ::updateWebSettings,
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    val invalidRoutes = listOf(
                        LoginScreen::class.qualifiedName,
                        LoadingScreen::class.qualifiedName,
                        BiometricAuthScreen::class.qualifiedName
                    )

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            Header(
                                navController = navController,
                                onMenuClick = {
                                    if (currentDestination?.route in invalidRoutes) {
                                        return@Header
                                    }

                                    scope.launch {
                                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                    }
                                }
                            )
                        }
                    ) { innerPadding ->
                        Surface(
                            color = MaterialTheme.colorScheme.background
                        ) {
                            ModalNavigationDrawer(
                                modifier = Modifier.padding(innerPadding),
                                drawerState = drawerState,
                                drawerContent = {
                                    Sidebar(
                                        onItemClick = { screen ->
                                            scope.launch { drawerState.close() }
                                            navController.navigate(screen)
                                        },
                                        navController = navController,
                                        closeSidebar = {
                                            scope.launch { drawerState.close() }
                                        }
                                    )
                                },
                                gesturesEnabled = currentDestination?.route !in invalidRoutes
                            ) {
                                AppNavigation(
                                    navController = navController,
                                    context = context,
                                    activity = activity,
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
    activity: FragmentActivity,
) {
    NavHost(
        navController = navController,
        startDestination = if (IS_DEBUG) DEBUG_SCREEN else LoadingScreen,
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
            LoadingScreen(
                navController = navController,
                context = context,
                activity = activity
            )
        }

        composable<LoginScreen> {
            LoginScreen(
                navController = navController,
                context = context,
                activity = activity
            )
        }

        composable<BiometricAuthScreen> {
            BiometricAuthScreen(
                navController = navController,
                context = context,
                activity = activity
            )
        }

        composable<HomeScreen> {
            HomeScreen(
                navController = navController,
                context = context,
                activity = activity
            )
        }

        composable<SettingsScreen> {
            SettingsScreen(
                navController = navController,
                context = context,
                activity = activity
            )
        }

        composable<MetricsScreen> {
            MetricsScreen(
                navController = navController,
                context = context,
                activity = activity
            )
        }

        composable<FilesScreen> { backStackEntry ->
            val filesScreen = backStackEntry.toRoute<FilesScreen>()

            FilesScreen(
                navController = navController,
                context = context,
                activity = activity,
                userId = filesScreen.userId
            )
        }

        composable<FoldersScreen> {
            FoldersScreen(
                navController = navController,
                context = context,
                activity = activity
            )
        }

        composable<UploadFileScreen> {
            UploadFileScreen(
                navController = navController,
                context = context,
                activity = activity
            )
        }

        composable<UploadTextScreen> {
            UploadTextScreen(
                navController = navController,
                context = context,
                activity = activity
            )
        }

        composable<UrlsScreen> {
            UrlsScreen(
                navController = navController,
                context = context,
                activity = activity
            )
        }

        composable<AdminSettingsScreen> {
            AdminSettingsScreen(
                navController = navController,
                context = context,
                activity = activity
            )
        }

        composable<AdminUsersScreen> {
            AdminUsersScreen(
                navController = navController,
                context = context,
                activity = activity
            )
        }

        composable<AdminActionsScreen> {
            AdminActionsScreen(
                navController = navController,
                context = context,
                activity = activity,
            )
        }

        composable<AdminInvitesScreen> {
            AdminInvitesScreen(
                navController = navController,
                context = context,
                activity = activity
            )
        }
    }
}