package com.stefdp.zipline

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.IntentCompat
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
import com.stefdp.zipline.network.models.ZiplineClient
import com.stefdp.zipline.network.models.responses.GetServerVersionResponse
import com.stefdp.zipline.network.requests.getAvatar
import com.stefdp.zipline.network.requests.getCurrentUser
import com.stefdp.zipline.network.requests.getPublicConfig
import com.stefdp.zipline.network.requests.getServerVersion
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
import com.stefdp.zipline.screens.register.RegisterScreen
import com.stefdp.zipline.screens.settings.SettingsScreen
import com.stefdp.zipline.screens.urls.UrlsScreen
import com.stefdp.zipline.screens.upload.file.UploadFileScreen
import com.stefdp.zipline.screens.upload.text.UploadTextScreen
import com.stefdp.zipline.utils.SecureStorage
import com.stefdp.zipline.utils.ZiplineViewState
import com.stefdp.zipline.utils.ZiplineViewStateType
import kotlinx.coroutines.delay
import com.stefdp.zipline.screens.admin.invites.VIEW_STATE_KEY as ADMIN_INVITES_VIEW_STATE_KEY
import com.stefdp.zipline.screens.admin.users.VIEW_STATE_KEY as ADMIN_USERS_VIEW_STATE_KEY
import com.stefdp.zipline.screens.files.VIEW_STATE_KEY as FILES_VIEW_STATE_KEY
import com.stefdp.zipline.screens.folders.VIEW_STATE_KEY as FOLDERS_VIEW_STATE_KEY
import com.stefdp.zipline.screens.urls.VIEW_STATE_KEY as URLS_VIEW_STATE_KEY

const val BASE_CORNER_RADIUS = 10

val LocalLoggedUser = compositionLocalOf<User?> { null }
val LocalUpdateLoggedUser = compositionLocalOf<suspend (context: Context) -> Result<User>> {
    {
        Result.failure(
            Exception("Placeholder")
        )
    }
}

val LocalLoggedUserAvatar = compositionLocalOf<String?> { null }
val LocalUpdateLoggedUserAvatar = compositionLocalOf<suspend (context: Context) -> Result<String>> {
    {
        Result.failure(
            Exception("Placeholder")
        )
    }
}

val LocalPublicSettings = compositionLocalOf<PublicServerConfig?> { null }
val LocalUpdatePublicSettings = compositionLocalOf<suspend (context: Context) -> Result<PublicServerConfig>> {
    {
        Result.failure(
            Exception("Placeholder")
        )
    }
}

val LocalWebSettings = compositionLocalOf<WebSettings?> { null }
val LocalUpdateWebSettings = compositionLocalOf<suspend (context: Context) -> Result<WebSettings>> {
    {
        Result.failure(
            Exception("Placeholder")
        )
    }
}

val LocalServerVersion = compositionLocalOf<GetServerVersionResponse?> { null }

val LocalUpdateServerVersion = compositionLocalOf<suspend (context: Context) -> Result<GetServerVersionResponse>> {
    {
        Result.failure(
            Exception("Placeholder")
        )
    }
}

val LocalScreenViewState = compositionLocalOf {
    ZiplineViewState(
        adminUsers = ZiplineViewStateType.LARGE,
        adminInvites = ZiplineViewStateType.LARGE,
        files = ZiplineViewStateType.LARGE,
        folders = ZiplineViewStateType.LARGE,
        urls = ZiplineViewStateType.LARGE,
    )
}

val LocalUpdateScreenViewState = compositionLocalOf<(context: Context, viewState: ZiplineViewState) -> Unit> {
    {_, _ -> }
}

class MainActivity : FragmentActivity() {
    private var isAppReady by mutableStateOf(false)

    private val viewModel: MainViewModel by viewModels()

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

        val context = applicationContext

        enableEdgeToEdge()

        setContent {
            ZiplineTheme {
                val activity = this@MainActivity

                val navController = rememberNavController()

                val state by viewModel.state.collectAsState()

                val networkMonitor = NetworkMonitor(context)

                val isConnected by networkMonitor.isConnected.collectAsState(initial = true)

                val coroutineScope = rememberCoroutineScope()

                LaunchedEffect(isConnected) {
                    if (isConnected) {
                        viewModel.updateLoggedUser(context)
                        viewModel.updateLoggedUserAvatar(context)
                        viewModel.updateWebSettings(context)
                        viewModel.updatePublicSettings(context)
                    }

                    coroutineScope.launch {
                        val secureStore = SecureStorage.getInstance(context)

                        val adminInvitesViewState = secureStore.get(ADMIN_INVITES_VIEW_STATE_KEY) ?: ZiplineViewStateType.LARGE.name
                        val adminUsersViewState = secureStore.get(ADMIN_USERS_VIEW_STATE_KEY) ?: ZiplineViewStateType.LARGE.name
                        val filesViewState = secureStore.get(FILES_VIEW_STATE_KEY) ?: ZiplineViewStateType.LARGE.name
                        val foldersViewState = secureStore.get(FOLDERS_VIEW_STATE_KEY) ?: ZiplineViewStateType.LARGE.name
                        val urlsViewState = secureStore.get(URLS_VIEW_STATE_KEY) ?: ZiplineViewStateType.LARGE.name

                        viewModel.updateScreenViewState(
                            context = context,
                            viewState = ZiplineViewState(
                                adminInvites = if (adminInvitesViewState in ZiplineViewStateType) ZiplineViewStateType.valueOf(adminInvitesViewState) else ZiplineViewStateType.LARGE,
                                adminUsers = if (adminUsersViewState in ZiplineViewStateType) ZiplineViewStateType.valueOf(adminUsersViewState) else ZiplineViewStateType.LARGE,
                                files = if (filesViewState in ZiplineViewStateType) ZiplineViewStateType.valueOf(filesViewState) else ZiplineViewStateType.LARGE,
                                folders = if (foldersViewState in ZiplineViewStateType) ZiplineViewStateType.valueOf(foldersViewState) else ZiplineViewStateType.LARGE,
                                urls = if (urlsViewState in ZiplineViewStateType) ZiplineViewStateType.valueOf(urlsViewState) else ZiplineViewStateType.LARGE,
                            )
                        )
                    }
                }

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

                CompositionLocalProvider(
                    LocalLoggedUser provides state.loggedUser,
                    LocalUpdateLoggedUser provides viewModel::updateLoggedUser,
                    LocalLoggedUserAvatar provides state.loggedUserAvatar,
                    LocalUpdateLoggedUserAvatar provides viewModel::updateLoggedUserAvatar,
                    LocalPublicSettings provides state.publicSettings,
                    LocalUpdatePublicSettings provides viewModel::updatePublicSettings,
                    LocalWebSettings provides state.webSettings,
                    LocalUpdateWebSettings provides viewModel::updateWebSettings,
                    LocalServerVersion provides state.serverVersion,
                    LocalUpdateServerVersion provides viewModel::updateServerVersion,
                    LocalScreenViewState provides state.screenViewState,
                    LocalUpdateScreenViewState provides viewModel::updateScreenViewState
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    val invalidRoutes = listOf(
                        LoginScreen::class.qualifiedName,
                        RegisterScreen::class.qualifiedName,
                        LoadingScreen::class.qualifiedName,
                        BiometricAuthScreen::class.qualifiedName
                    )

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            val isInvalid = invalidRoutes.any { routeName ->
                                currentDestination?.route?.startsWith(routeName ?: "") == true
                            }

                            if (!isInvalid) {
                                Header(
                                    activity = activity,
                                    context = context,
                                    navController = navController,
                                    onMenuClick = {
                                        coroutineScope.launch {
                                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                        }
                                    }
                                )
                            }
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
                                            coroutineScope.launch { drawerState.close() }
                                            navController.navigate(screen)
                                        },
                                        navController = navController,
                                        closeSidebar = {
                                            coroutineScope.launch { drawerState.close() }
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

                if (!isConnected) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        Surface(
                            modifier = Modifier.padding(innerPadding),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.wifi_off),
                                    contentDescription = "No internet connection",
                                    modifier = Modifier.size(50.dp)
                                )

                                Spacer(
                                    modifier = Modifier.height(20.dp)
                                )

                                Text(
                                    text = "No Internet Connection",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    )
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

        composable<LoginScreen> { backStackEntry ->
            val loginScreen = backStackEntry.toRoute<LoginScreen>()

            LoginScreen(
                navController = navController,
                context = context,
                activity = activity,
                serverUrl = loginScreen.serverUrl,
            )
        }

        composable<RegisterScreen> { backStackEntry ->
            val registerScreen = backStackEntry.toRoute<RegisterScreen>()

            RegisterScreen(
                navController = navController,
                context = context,
                activity = activity,
                serverUrl = registerScreen.serverUrl,
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

        composable<SettingsScreen> { backStackEntry ->
            val settingsScreen = backStackEntry.toRoute<SettingsScreen>()

            SettingsScreen(
                navController = navController,
                context = context,
                activity = activity,
                update = settingsScreen.update,
                updateSwitchCategory = settingsScreen.updateSwitchCategory
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

        composable<UploadFileScreen> { backStackEntry ->
            val uploadFileScreen = backStackEntry.toRoute<UploadFileScreen>()

            UploadFileScreen(
                navController = navController,
                context = context,
                activity = activity,
                sharedFiles = uploadFileScreen.files
            )
        }

        composable<UploadTextScreen> { backStackEntry ->
            val uploadTextScreen = backStackEntry.toRoute<UploadTextScreen>()

            UploadTextScreen(
                navController = navController,
                context = context,
                activity = activity,
                sharedText = uploadTextScreen.text
            )
        }

        composable<UrlsScreen> { backStackEntry ->
            val urlsScreen = backStackEntry.toRoute<UrlsScreen>()

            UrlsScreen(
                navController = navController,
                context = context,
                activity = activity,
                sharedUrl = urlsScreen.url
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

fun handleSharedIntent(intent: Intent, navController: NavHostController) {
    val action = intent.action ?: return

    if (action == Intent.ACTION_SEND || action == Intent.ACTION_SEND_MULTIPLE) {
        val text = intent.getStringExtra(Intent.EXTRA_TEXT)

        if (text != null && (text.startsWith(
                prefix = "http://",
                ignoreCase = true
            ) || text.startsWith(
                prefix = "https://",
                ignoreCase = true
            ))
        ) {
            navController.navigate(UrlsScreen(url = text)) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }

            return
        }

        val uris = if (action == Intent.ACTION_SEND) {
            IntentCompat.getParcelableExtra(
                intent,
                Intent.EXTRA_STREAM,
                android.net.Uri::class.java
            )?.let { listOf(it) }
        } else {
            IntentCompat.getParcelableArrayListExtra(
                intent,
                Intent.EXTRA_STREAM,
                android.net.Uri::class.java
            )
        }

        if (!uris.isNullOrEmpty()) {
            val uriStrings = uris.map { it.toString() }

            navController.navigate(UploadFileScreen(files = uriStrings)) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
            return
        }

        if (text != null) {
            navController.navigate(UploadTextScreen(text = text)) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }
}

fun isShareIntent(intent: Intent): Boolean {
    val action = intent.action ?: return false

    return action == Intent.ACTION_SEND || action == Intent.ACTION_SEND_MULTIPLE
}