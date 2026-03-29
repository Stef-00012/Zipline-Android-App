package com.stefdp.zipline.screens.admin.settings

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColor
import androidx.core.graphics.toColorInt
import androidx.core.graphics.toColorLong
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.stefdp.zipline.LocalLoggedUser
import com.stefdp.zipline.LocalUpdatePublicSettings
import com.stefdp.zipline.LocalUpdateWebSettings
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.components.HeaderButton
import com.stefdp.zipline.components.Select
import com.stefdp.zipline.components.SelectOption
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.components.colorpicker.ColorPicker
import com.stefdp.zipline.network.models.FilesFormat
import com.stefdp.zipline.network.models.PartialServerSettingsSettings
import com.stefdp.zipline.network.models.ServerSettings
import com.stefdp.zipline.network.models.ThumbnailFormat
import com.stefdp.zipline.network.models.UserRole
import com.stefdp.zipline.network.models.requests.UploadCompressionType
import com.stefdp.zipline.network.requests.UpdateServerSettingsResult
import com.stefdp.zipline.network.requests.getServerSettings
import com.stefdp.zipline.network.requests.updateServerSettings
import com.stefdp.zipline.screens.HomeScreen
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.utils.NumberRegex
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.compressionFormats
import com.stefdp.zipline.utils.getSettingName
import com.stefdp.zipline.utils.nameFormats
import com.stefdp.zipline.utils.thumbnailFormats
import com.stefdp.zipline.utils.toHex
import com.stefdp.zipline.utils.verticalLazyScrollbar
import com.stefdp.zipline.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun AdminSettingsScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity
) {
    val localLoggedUser = LocalLoggedUser.current

//    if (localLoggedUser == null) {
//        navController.navigate(LoginScreen) {
//            popUpTo(navController.graph.id) { inclusive = true }
//        }
//    }
//
//    localLoggedUser?.role?.level?.let {
//        if (it > UserRole.SUPERADMIN.level) {
//            navController.navigate(HomeScreen) {
//                popUpTo(navController.graph.id) { inclusive = true }
//            }
//        }
//    }

    var isLoading by remember { mutableStateOf(false) }

    var settings by remember { mutableStateOf<ServerSettings?>(null) }

    val coroutineScope = rememberCoroutineScope()

    val updateWebSettings = LocalUpdateWebSettings.current
    val updatePublicSettings = LocalUpdatePublicSettings.current

    suspend fun updateSettings(data: PartialServerSettingsSettings? = null): List<String> {
        isLoading = true

        if (data != null) {
            val updateServerSettingsRes = updateServerSettings(
                context = context,
                data = data
            )

            updateServerSettingsRes
                .onSuccess {
                    if (it is UpdateServerSettingsResult.Error) {
                        return it.error.issues?.map { error -> "${getSettingName(error.path)}: ${error.message}." } ?: listOf(it.error.message ?: it.error.error)
                    } else if (it is UpdateServerSettingsResult.Success) {
                        settings = it.settings

                        updateWebSettings()
                        updatePublicSettings()

                        Toast.makeText(
                            context,
                            "Settings updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .onFailure {
                    return listOf(it.message ?: "Something went wrong...")
                }
        } else {
            val settingsRes = getServerSettings(
                context = context,
            )

            settingsRes.onSuccess {
                settings = it
            }
        }

        isLoading = false
        return emptyList()
    }

    LaunchedEffect(Unit) {
        updateSettings()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 12.dp
                ),
        ) {
            Text(
                text = "Server Settings",
                maxLines = 1,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderButton(
                    icon = painterResource(R.drawable.refresh),
                    contentDescription = "Refresh settings",
                    onClick = {
                        coroutineScope.launch {
                            updateSettings()
                        }
                    },
                    enabled = !isLoading,
                )
            }
        }

        val scrollState = rememberScrollState()

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .verticalScrollWithScrollbar(
                    scrollState = scrollState,
                    scrollbarConfig = ScrollbarConfig(
                        alwaysKeepScrollbar = true
                    )
                )
                .padding(
                    start = 12.dp,
                    end = 12.dp,
                ),
        ) {
            Container(
                scrollable = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Core",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                    )

                    var errors by remember { mutableStateOf<List<String>>(emptyList()) }

                    if (errors.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            errors.forEach {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }

                    var returnHttpsUrls by remember(settings?.settings?.coreReturnHttpsUrls) {
                        mutableStateOf(settings?.settings?.coreReturnHttpsUrls ?: false)
                    }

                    Switch(
                        checked = returnHttpsUrls,
                        onCheckedChange = { returnHttpsUrls = it },
                        label = "Return HTTPS URLs",
                        description = "Return URLs with HTTPS protocol.",
                        enabled = !isLoading
                    )

                    var trustProxies by remember(settings?.settings?.coreTrustProxy) {
                        mutableStateOf(settings?.settings?.coreTrustProxy ?: false)
                    }

                    Switch(
                        checked = trustProxies,
                        onCheckedChange = { trustProxies = it },
                        label = "Trust Proxies",
                        description = "Trust the X-Forwarded-* headers set by proxies. Only enable this if you are behind a trusted proxy (nginx, caddy, etc.). Requires a server restart.",
                        enabled = !isLoading
                    )

                    var defaultDomain by remember(settings?.settings?.coreDefaultDomain) {
                        mutableStateOf(TextFieldValue(settings?.settings?.coreDefaultDomain ?: ""))
                    }

                    TextInput(
                        value = defaultDomain,
                        onValueChange = { defaultDomain = it },
                        label = "Default Domain",
                        description = "The domain to use when generating URLs. This value should not include the protocol.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var temporaryDirectory by remember(settings?.settings?.coreTempDirectory) {
                        mutableStateOf(TextFieldValue(settings?.settings?.coreTempDirectory ?: ""))
                    }

                    TextInput(
                        value = temporaryDirectory,
                        onValueChange = { temporaryDirectory = it },
                        label = "Temporary Directory",
                        description = "The directory to store temporary files. If the path is invalid, certain functions may break. Requires a server restart.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true

                                val data = PartialServerSettingsSettings(
                                    coreReturnHttpsUrls = returnHttpsUrls,
                                    coreTrustProxy = trustProxies,
                                    coreDefaultDomain = defaultDomain.text.takeIf { it.isNotBlank() },
                                    coreTempDirectory = temporaryDirectory.text,
                                )

                                val updateSettingsErrors = updateSettings(data)

                                errors = updateSettingsErrors

                                isLoading = false
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.save),
                            contentDescription = "Save settings"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Save"
                        )
                    }
                }
            }

            Container(
                scrollable = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Chunks",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                    )

                    var errors by remember { mutableStateOf<List<String>>(emptyList()) }

                    if (errors.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            errors.forEach {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }

                    var enableChunks by remember(settings?.settings?.chunksEnabled) {
                        mutableStateOf(settings?.settings?.chunksEnabled ?: false)
                    }

                    Switch(
                        checked = enableChunks,
                        onCheckedChange = { enableChunks = it },
                        label = "Enable Chunks",
                        description = "Enable chunked uploads.",
                        enabled = !isLoading
                    )

                    var maxChunkSize by remember(settings?.settings?.chunksMax) {
                        mutableStateOf(TextFieldValue(settings?.settings?.chunksMax ?: ""))
                    }

                    TextInput(
                        value = maxChunkSize,
                        onValueChange = { maxChunkSize = it },
                        label = "Max Chunk Size",
                        description = "Maximum size of an upload before it is split into chunks.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var chunksSize by remember(settings?.settings?.chunksSize) {
                        mutableStateOf(TextFieldValue(settings?.settings?.chunksSize ?: ""))
                    }

                    TextInput(
                        value = chunksSize,
                        onValueChange = { chunksSize = it },
                        label = "Chunk Size",
                        description = "Size of each chunk.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true

                                val data = PartialServerSettingsSettings(
                                    chunksEnabled = enableChunks,
                                    chunksMax = maxChunkSize.text,
                                    chunksSize = chunksSize.text,
                                )

                                val updateSettingsErrors = updateSettings(data)

                                errors = updateSettingsErrors

                                isLoading = false
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.save),
                            contentDescription = "Save settings"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Save"
                        )
                    }
                }
            }

            Container(
                scrollable = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Tasks",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                        )

                        Text(
                            text = "All options require a restart to take effect.",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }

                    var errors by remember { mutableStateOf<List<String>>(emptyList()) }

                    if (errors.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            errors.forEach {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }

                    var deleteFilesInterval by remember(settings?.settings?.tasksDeleteInterval) {
                        mutableStateOf(TextFieldValue(settings?.settings?.tasksDeleteInterval ?: ""))
                    }

                    TextInput(
                        value = deleteFilesInterval,
                        onValueChange = { deleteFilesInterval = it },
                        label = "Delete Files Interval",
                        description = "How often to check and delete expired files.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var clearInvitesInterval by remember(settings?.settings?.tasksClearInvitesInterval) {
                        mutableStateOf(TextFieldValue(settings?.settings?.tasksClearInvitesInterval ?: ""))
                    }

                    TextInput(
                        value = clearInvitesInterval,
                        onValueChange = { clearInvitesInterval = it },
                        label = "Clear Invites Interval",
                        description = "How often to check and clear expired/used invites.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var maxViewsInterval by remember(settings?.settings?.tasksMaxViewsInterval) {
                        mutableStateOf(TextFieldValue(settings?.settings?.tasksMaxViewsInterval ?: ""))
                    }

                    TextInput(
                        value = maxViewsInterval,
                        onValueChange = { maxViewsInterval = it },
                        label = "Max Views Interval",
                        description = "How often to check and delete files that have reached max views.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var thumbnailsInterval by remember(settings?.settings?.tasksThumbnailsInterval) {
                        mutableStateOf(TextFieldValue(settings?.settings?.tasksThumbnailsInterval ?: ""))
                    }

                    TextInput(
                        value = thumbnailsInterval,
                        onValueChange = { thumbnailsInterval = it },
                        label = "Thumbnails Interval",
                        description = "How often to check and generate thumbnails for video files.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var cleanThumbnailsInterval by remember(settings?.settings?.tasksCleanThumbnailsInterval) {
                        mutableStateOf(TextFieldValue(settings?.settings?.tasksCleanThumbnailsInterval ?: ""))
                    }

                    TextInput(
                        value = cleanThumbnailsInterval,
                        onValueChange = { cleanThumbnailsInterval = it },
                        label = "Clean Thumbnails Interval",
                        description = "How often to check and delete orphaned thumbnails from the filesystem or database.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true

                                val data = PartialServerSettingsSettings(
                                    tasksDeleteInterval = deleteFilesInterval.text,
                                    tasksClearInvitesInterval = clearInvitesInterval.text,
                                    tasksMaxViewsInterval = maxViewsInterval.text,
                                    tasksThumbnailsInterval = thumbnailsInterval.text,
                                    tasksCleanThumbnailsInterval = cleanThumbnailsInterval.text,
                                )

                                val updateSettingsErrors = updateSettings(data)

                                errors = updateSettingsErrors

                                isLoading = false
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.save),
                            contentDescription = "Save settings"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Save"
                        )
                    }
                }
            }

            Container(
                scrollable = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Multi-Factor Authentication",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                    )

                    var errors by remember { mutableStateOf<List<String>>(emptyList()) }

                    if (errors.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            errors.forEach {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }

                    var passkeys by remember(settings?.settings?.mfaPasskeysEnabled) {
                        mutableStateOf(settings?.settings?.mfaPasskeysEnabled ?: false)
                    }

                    Switch(
                        checked = passkeys,
                        onCheckedChange = { passkeys = it },
                        label = "Passkeys",
                        description = "Enable the use of passwordless login with the use of WebAuthn passkeys like your phone, security keys, etc.",
                        enabled = !isLoading
                    )

                    var relyingPartyId by remember(settings?.settings?.mfaPasskeysRpID) {
                        mutableStateOf(TextFieldValue(settings?.settings?.mfaPasskeysRpID ?: ""))
                    }

                    TextInput(
                        value = relyingPartyId,
                        onValueChange = { relyingPartyId = it },
                        label = "Relying Party ID",
                        description = "The Relying Party ID (RP ID) to use for WebAuthn passkeys.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var origin by remember(settings?.settings?.mfaPasskeysOrigin) {
                        mutableStateOf(TextFieldValue(settings?.settings?.mfaPasskeysOrigin ?: ""))
                    }

                    TextInput(
                        value = origin,
                        onValueChange = { origin = it },
                        label = "Origin",
                        description = "The Origin to use for WebAuthn passkeys.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var enableTOTP by remember(settings?.settings?.mfaTotpEnabled) {
                        mutableStateOf(settings?.settings?.mfaTotpEnabled ?: false)
                    }

                    Switch(
                        checked = enableTOTP,
                        onCheckedChange = { enableTOTP = it },
                        label = "Enable TOTP",
                        description = "Enable Time-based One-Time Passwords with the use of an authenticator app.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var issuer by remember(settings?.settings?.mfaTotpIssuer) {
                        mutableStateOf(TextFieldValue(settings?.settings?.mfaTotpIssuer ?: ""))
                    }

                    TextInput(
                        value = issuer,
                        onValueChange = { issuer = it },
                        label = "Issuer",
                        description = "The issuer to use for the TOTP token.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true

                                val data = PartialServerSettingsSettings(
                                    mfaPasskeysEnabled = passkeys,
                                    mfaPasskeysRpID = relyingPartyId.text,
                                    mfaPasskeysOrigin = origin.text,
                                    mfaTotpEnabled = enableTOTP,
                                    mfaTotpIssuer = issuer.text,
                                )

                                val updateSettingsErrors = updateSettings(data)

                                errors = updateSettingsErrors

                                isLoading = false
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.save),
                            contentDescription = "Save settings"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Save"
                        )
                    }
                }
            }

            Container(
                scrollable = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Features",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                    )

                    var errors by remember { mutableStateOf<List<String>>(emptyList()) }

                    if (errors.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            errors.forEach {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }

                    var imageCompression by remember(settings?.settings?.featuresImageCompression) {
                        mutableStateOf(settings?.settings?.featuresImageCompression ?: false)
                    }

                    Switch(
                        checked = imageCompression,
                        onCheckedChange = { imageCompression = it },
                        label = "Image Compression",
                        description = "Allows the ability for users to compress images.",
                        enabled = !isLoading
                    )

                    var robotsTxt by remember(settings?.settings?.featuresRobotsTxt) {
                        mutableStateOf(settings?.settings?.featuresRobotsTxt ?: false)
                    }

                    Switch(
                        checked = robotsTxt,
                        onCheckedChange = { robotsTxt = it },
                        label = "/robots.txt",
                        description = "Enables a /robots.txt to stop search crawlers. Requires a server restart.",
                        enabled = !isLoading
                    )

                    var healthcheck by remember(settings?.settings?.featuresHealthcheck) {
                        mutableStateOf(settings?.settings?.featuresHealthcheck ?: false)
                    }

                    Switch(
                        checked = healthcheck,
                        onCheckedChange = { healthcheck = it },
                        label = "Healthcheck",
                        description = "Enables a healthcheck route for uptime monitoring. Requires a server restart.",
                        enabled = !isLoading
                    )

                    var userRegistration by remember(settings?.settings?.featuresUserRegistration) {
                        mutableStateOf(settings?.settings?.featuresUserRegistration ?: false)
                    }

                    Switch(
                        checked = userRegistration,
                        onCheckedChange = { userRegistration = it },
                        label = "User Registration",
                        description = "Allows users to register an account on the server.",
                        enabled = !isLoading
                    )

                    var oauthRegistration by remember(settings?.settings?.featuresOauthRegistration) {
                        mutableStateOf(settings?.settings?.featuresOauthRegistration ?: false)
                    }

                    Switch(
                        checked = oauthRegistration,
                        onCheckedChange = { oauthRegistration = it },
                        label = "OAuth Registration",
                        description = "Allows users to register an account using OAuth providers.",
                        enabled = !isLoading
                    )

                    var deleteOnMaxViews by remember(settings?.settings?.featuresDeleteOnMaxViews) {
                        mutableStateOf(settings?.settings?.featuresDeleteOnMaxViews ?: false)
                    }

                    Switch(
                        checked = deleteOnMaxViews,
                        onCheckedChange = { deleteOnMaxViews = it },
                        label = "Delete on Max Views",
                        description = "Automatically deletes files/urls after they reach the maximum view count. Requires a server restart.",
                        enabled = !isLoading
                    )

                    var enableMetrics by remember(settings?.settings?.featuresMetricsEnabled) {
                        mutableStateOf(settings?.settings?.featuresMetricsEnabled ?: false)
                    }

                    Switch(
                        checked = enableMetrics,
                        onCheckedChange = { enableMetrics = it },
                        label = "Enable Metrics",
                        description = "Enables metrics for the server. Requires a server restart.",
                        enabled = !isLoading
                    )

                    var adminOnlyMetrics by remember(settings?.settings?.featuresMetricsAdminOnly) {
                        mutableStateOf(settings?.settings?.featuresMetricsAdminOnly ?: false)
                    }

                    Switch(
                        checked = adminOnlyMetrics,
                        onCheckedChange = { adminOnlyMetrics = it },
                        label = "Admin Only Metrics",
                        description = "Requires an administrator to view metrics.",
                        enabled = !isLoading
                    )

                    var showUserSpecificMetrics by remember(settings?.settings?.featuresMetricsShowUserSpecific) {
                        mutableStateOf(settings?.settings?.featuresMetricsShowUserSpecific ?: false)
                    }

                    Switch(
                        checked = showUserSpecificMetrics,
                        onCheckedChange = { showUserSpecificMetrics = it },
                        label = "Show User Specific Metrics",
                        description = "Shows metrics specific to each user, for all users.",
                        enabled = !isLoading
                    )

                    var enableThumbnails by remember(settings?.settings?.featuresThumbnailsEnabled) {
                        mutableStateOf(settings?.settings?.featuresThumbnailsEnabled ?: false)
                    }

                    Switch(
                        checked = enableThumbnails,
                        onCheckedChange = { enableThumbnails = it },
                        label = "Enable Thumbnails",
                        description = "Enables thumbnail generation for images. Requires a server restart.",
                        enabled = !isLoading
                    )

                    var thumbnailsNumberThreads by remember(settings?.settings?.featuresThumbnailsNumberThreads) {
                        mutableStateOf(TextFieldValue(settings?.settings?.featuresThumbnailsNumberThreads?.toString() ?: ""))
                    }

                    TextInput(
                        value = thumbnailsNumberThreads,
                        onValueChange = {
                            if (NumberRegex.matches(it.text)) {
                                thumbnailsNumberThreads = it
                            }
                        },
                        label = "Thumbnails Number of Threads",
                        description = "The number of threads to use for thumbnail generation.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var selectedThumbnailsFormat by remember(settings?.settings?.featuresThumbnailsFormat) {
                        mutableStateOf(setOf((settings?.settings?.featuresThumbnailsFormat ?: ThumbnailFormat.PNG).toString()))
                    }

                    Select(
                        options = thumbnailFormats.map { (id, label) ->
                            SelectOption(
                                id = id.toString(),
                                label = { enabled ->
                                    Text(
                                        text = label,
                                        color = if (enabled)
                                            MaterialTheme.colorScheme.onBackground
                                        else
                                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                    )
                                }
                            )
                        },
                        selectedIds = selectedThumbnailsFormat,
                        onSelectionChange = { selectedThumbnailsFormat = it },
                    )

                    var versionChecking by remember(settings?.settings?.featuresVersionChecking) {
                        mutableStateOf(settings?.settings?.featuresVersionChecking ?: false)
                    }

                    Switch(
                        checked = versionChecking,
                        onCheckedChange = { versionChecking = it },
                        label = "Version Checking",
                        description = "Enable version checking for the server. This will check for updates and display the status on the sidebar to all users.",
                        enabled = !isLoading
                    )

                    var versionAPIUrl by remember(settings?.settings?.featuresVersionAPI) {
                        mutableStateOf(TextFieldValue(settings?.settings?.featuresVersionAPI ?: ""))
                    }

                    TextInput(
                        value = versionAPIUrl,
                        onValueChange = {
                            versionAPIUrl = it
                        },
                        label = "Version API URL",
                        description = buildAnnotatedString {
                            val rawString = "The URL of the version checking server. The default is [link1]https://zipline-version.diced.sh[/link1]. Visit the [link2]GitHub[/link2] to host your own version checking server."

                            val linkUrls = mapOf(
                                "link1" to "https://zipline-version.diced.sh",
                                "link2" to "https://github.com/diced/zipline-version-worker"
                            )

                            val regex = Regex("""\[(link\d+)](.*?)\[/\1]""")

                            var currentIndex = 0
                            val matches = regex.findAll(rawString)

                            for (match in matches) {
                                append(rawString.substring(currentIndex, match.range.first))

                                val tag = match.groupValues[1]
                                val visibleText = match.groupValues[2]
                                val url = linkUrls[tag]

                                val startIndex = this.length

                                append(visibleText)

                                if (url != null) {
                                    addLink(
                                        url = LinkAnnotation.Url(
                                            url = url,
                                            styles = TextLinkStyles(
                                                style = SpanStyle(
                                                    color = MaterialTheme.colorScheme.primary,
                                                    textDecoration = TextDecoration.Underline
                                                )
                                            )
                                        ),
                                        start = startIndex,
                                        end = this.length
                                    )
                                }

                                currentIndex = match.range.last + 1
                            }

                            if (currentIndex < rawString.length) {
                                append(rawString.substring(currentIndex))
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true

                                val thumbnailsFormat = thumbnailFormats.firstOrNull {
                                    it.first.toString() == selectedThumbnailsFormat.firstOrNull()
                                }?.first

                                val data = PartialServerSettingsSettings(
                                    featuresImageCompression = imageCompression,
                                    featuresRobotsTxt = robotsTxt,
                                    featuresHealthcheck = healthcheck,
                                    featuresUserRegistration = userRegistration,
                                    featuresOauthRegistration = oauthRegistration,
                                    featuresDeleteOnMaxViews = deleteOnMaxViews,
                                    featuresMetricsEnabled = enableMetrics,
                                    featuresMetricsAdminOnly = adminOnlyMetrics,
                                    featuresMetricsShowUserSpecific = showUserSpecificMetrics,
                                    featuresThumbnailsEnabled = enableThumbnails,
                                    featuresThumbnailsNumberThreads = thumbnailsNumberThreads.text.toLongOrNull(),
                                    featuresThumbnailsFormat = thumbnailsFormat,
                                    featuresVersionChecking = versionChecking,
                                    featuresVersionAPI = versionAPIUrl.text,
                                )

                                val updateSettingsErrors = updateSettings(data)

                                errors = updateSettingsErrors

                                isLoading = false
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.save),
                            contentDescription = "Save settings"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Save"
                        )
                    }
                }
            }

            Container(
                scrollable = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Files",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                    )

                    var errors by remember { mutableStateOf<List<String>>(emptyList()) }

                    if (errors.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            errors.forEach {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }

                    var route by remember(settings?.settings?.filesRoute) {
                        mutableStateOf(TextFieldValue(settings?.settings?.filesRoute ?: "/"))
                    }

                    TextInput(
                        value = route,
                        onValueChange = {
                            if (it.text.isBlank()) {
                                route = TextFieldValue("/")
                            }

                            if (it.text.startsWith("/")) {
                                route = it
                            }
                        },
                        label = "Route",
                        description = "The route to use for file uploads. Requires a server restart.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var length by remember(settings?.settings?.filesLength) {
                        mutableStateOf(TextFieldValue((settings?.settings?.filesLength ?: "").toString()))
                    }

                    TextInput(
                        value = length,
                        onValueChange = {
                            if (NumberRegex.matches(it.text)) {
                                length = it
                            }
                        },
                        label = "Length",
                        description = "The length of the file name (for randomly generated names).",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var assumeMimetypes by remember(settings?.settings?.filesAssumeMimetypes) {
                        mutableStateOf(settings?.settings?.filesAssumeMimetypes ?: false)
                    }

                    Switch(
                        checked = assumeMimetypes,
                        onCheckedChange = { assumeMimetypes = it },
                        label = "Assume Mimetypes",
                        description = "Assume the mimetype of a file for its extension.",
                        enabled = !isLoading
                    )

                    var removeGPSMetadata by remember(settings?.settings?.filesRemoveGpsMetadata) {
                        mutableStateOf(settings?.settings?.filesRemoveGpsMetadata ?: false)
                    }

                    Switch(
                        checked = removeGPSMetadata,
                        onCheckedChange = { removeGPSMetadata = it },
                        label = "Remove GPS Metadata",
                        description = "Remove GPS metadata from files.",
                        enabled = !isLoading
                    )

                    var selectedDefaultFormat by remember(settings?.settings?.filesDefaultFormat) {
                        mutableStateOf(setOf((settings?.settings?.filesDefaultFormat ?: FilesFormat.RANDOM).toString()))
                    }

                    Select(
                        label = "Default Format",
                        description = "The default format to use for file names.",
                        options = nameFormats.map { (id, label) ->
                            SelectOption(
                                id = id.toString(),
                                label = { enabled ->
                                    Text(
                                        text = label,
                                        color = if (enabled)
                                            MaterialTheme.colorScheme.onBackground
                                        else
                                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                    )
                                }
                            )
                        },
                        onSelectionChange = { selectedDefaultFormat = it },
                        selectedIds = selectedDefaultFormat,
                        enabled = !isLoading
                    )

                    var disabledExtensions by remember(settings?.settings?.filesDisabledExtensions) {
                        mutableStateOf(TextFieldValue(settings?.settings?.filesDisabledExtensions?.joinToString(", ") ?: ""))
                    }

                    TextInput(
                        value = disabledExtensions,
                        onValueChange = {
                            disabledExtensions = it
                        },
                        label = "Disabled Extensions",
                        description = "Extensions to disable, separated by commas.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var maxFileSize by remember(settings?.settings?.filesMaxFileSize) {
                        mutableStateOf(TextFieldValue(settings?.settings?.filesMaxFileSize ?: ""))
                    }

                    TextInput(
                        value = maxFileSize,
                        onValueChange = {
                            maxFileSize = it
                        },
                        label = "Max File Size",
                        description = "The maximum file size allowed.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var defaultDateFormat by remember(settings?.settings?.filesDefaultDateFormat) {
                        mutableStateOf(TextFieldValue(settings?.settings?.filesDefaultDateFormat ?: ""))
                    }

                    TextInput(
                        value = defaultDateFormat,
                        onValueChange = {
                            defaultDateFormat = it
                        },
                        label = "Default Date Format",
                        description = "The default date format to use.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var defaultExpiration by remember(settings?.settings?.filesDefaultExpiration) {
                        mutableStateOf(TextFieldValue(settings?.settings?.filesDefaultExpiration ?: ""))
                    }

                    TextInput(
                        value = defaultExpiration,
                        onValueChange = {
                            defaultExpiration = it
                        },
                        label = "Default Expiration",
                        description = "The default expiration time for files.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var maxExpiration by remember(settings?.settings?.filesMaxExpiration) {
                        mutableStateOf(TextFieldValue(settings?.settings?.filesMaxExpiration ?: ""))
                    }

                    TextInput(
                        value = maxExpiration,
                        onValueChange = {
                            maxExpiration = it
                        },
                        label = "Max Expiration",
                        description = "The maximum expiration time allowed for files.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var randomWordsAdjectivesNumber by remember(settings?.settings?.filesRandomWordsNumAdjectives) {
                        mutableStateOf(TextFieldValue((settings?.settings?.filesRandomWordsNumAdjectives ?: "").toString()))
                    }

                    TextInput(
                        value = randomWordsAdjectivesNumber,
                        onValueChange = {
                            if (NumberRegex.matches(it.text)) {
                                randomWordsAdjectivesNumber = it
                            }
                        },
                        label = "Random Words Num Adjectives",
                        description = "The number of adjectives to use for the random-words/gfycat format.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var randomWordsSeparator by remember(settings?.settings?.filesRandomWordsSeparator) {
                        mutableStateOf(TextFieldValue(settings?.settings?.filesRandomWordsSeparator ?: ""))
                    }

                    TextInput(
                        value = randomWordsSeparator,
                        onValueChange = {
                            randomWordsSeparator = it
                        },
                        label = "Random Words Separator",
                        description = "The separator to use for the random-words/gfycat format.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var selectedDefaultCompressionFormat by remember(settings?.settings?.filesDefaultCompressionFormat) {
                        mutableStateOf(setOf((settings?.settings?.filesDefaultCompressionFormat ?: UploadCompressionType.PNG).toString()))
                    }

                    Select(
                        label = "Default Compression Format",
                        description = "The default image compression format to use when only a compression percent is specified.",
                        options = compressionFormats.map { (id, label) ->
                            SelectOption(
                                id = id.toString(),
                                label = { enabled ->
                                    Text(
                                        text = label,
                                        color = if (enabled)
                                            MaterialTheme.colorScheme.onBackground
                                        else
                                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                    )
                                }
                            )
                        },
                        onSelectionChange = { selectedDefaultCompressionFormat = it },
                        selectedIds = selectedDefaultCompressionFormat,
                        enabled = !isLoading
                    )

                    var maxFilesPerUpload by remember(settings?.settings?.filesMaxFilesPerUpload) {
                        mutableStateOf(TextFieldValue((settings?.settings?.filesMaxFilesPerUpload ?: "").toString()))
                    }

                    TextInput(
                        value = maxFilesPerUpload,
                        onValueChange = {
                            if (NumberRegex.matches(it.text)) {
                                maxFilesPerUpload = it
                            }
                        },
                        label = "Max Files Per Upload",
                        description = "The maximum number of files allowed per upload. Requires a server restart.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true

                                val defaultFormat = nameFormats.firstOrNull {
                                    it.first.toString() == selectedDefaultFormat.firstOrNull()
                                }?.first

                                val defaultCompressionFormat = compressionFormats.firstOrNull {
                                    it.first.toString() == selectedDefaultCompressionFormat.firstOrNull()
                                }?.first

                                val data = PartialServerSettingsSettings(
                                    filesRoute = route.text,
                                    filesLength = length.text.toLongOrNull(),
                                    filesAssumeMimetypes = assumeMimetypes,
                                    filesRemoveGpsMetadata = removeGPSMetadata,
                                    filesDefaultFormat = defaultFormat,
                                    filesDisabledExtensions = disabledExtensions.text
                                        .split(", ", ",")
                                        .map { it.trim() }
                                        .filter { it.isNotEmpty() },
                                    filesMaxFileSize = maxFileSize.text,
                                    filesDefaultDateFormat = defaultDateFormat.text,
                                    filesDefaultExpiration = defaultExpiration.text,
                                    filesMaxExpiration = maxExpiration.text,
                                    filesRandomWordsNumAdjectives = randomWordsAdjectivesNumber.text.toLongOrNull(),
                                    filesRandomWordsSeparator = randomWordsSeparator.text,
                                    filesDefaultCompressionFormat = defaultCompressionFormat,
                                    filesMaxFilesPerUpload = maxFilesPerUpload.text.toLongOrNull(),
                                )

                                val updateSettingsErrors = updateSettings(data)

                                errors = updateSettingsErrors

                                isLoading = false
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.save),
                            contentDescription = "Save settings"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Save"
                        )
                    }
                }
            }

            Container(
                scrollable = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "URL Shortener",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                    )

                    var errors by remember { mutableStateOf<List<String>>(emptyList()) }

                    if (errors.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            errors.forEach {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }

                    var route by remember(settings?.settings?.urlsRoute) {
                        mutableStateOf(TextFieldValue(settings?.settings?.urlsRoute ?: ""))
                    }

                    TextInput(
                        value = route,
                        onValueChange = {
                            if (it.text.isBlank()) {
                                route = TextFieldValue("/")
                            }

                            if (it.text.startsWith("/")) {
                                route = it
                            }
                        },
                        label = "Route",
                        description = "The route to use for short URLs. Requires a server restart.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var length by remember(settings?.settings?.urlsLength) {
                        mutableStateOf(TextFieldValue((settings?.settings?.urlsLength ?: "").toString()))
                    }

                    TextInput(
                        value = length,
                        onValueChange = {
                            if (NumberRegex.matches(it.text)) {
                                length = it
                            }
                        },
                        label = "Length",
                        description = "The length of the short URL (for randomly generated names).",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true

                                val data = PartialServerSettingsSettings(
                                    urlsRoute = route.text,
                                    urlsLength = length.text.toLongOrNull(),
                                )

                                val updateSettingsErrors = updateSettings(data)

                                errors = updateSettingsErrors

                                isLoading = false
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.save),
                            contentDescription = "Save settings"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Save"
                        )
                    }
                }
            }

            Container(
                scrollable = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Invites",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                    )

                    var errors by remember { mutableStateOf<List<String>>(emptyList()) }

                    if (errors.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            errors.forEach {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }

                    var invitesEnabled by remember(settings?.settings?.invitesEnabled) {
                        mutableStateOf(settings?.settings?.invitesEnabled ?: false)
                    }

                    Switch(
                        checked = invitesEnabled,
                        onCheckedChange = { invitesEnabled = it },
                        label = "Invites Enabled",
                        description = "Enable the use of invite links to register new users.",
                        enabled = !isLoading
                    )

                    var length by remember(settings?.settings?.invitesLength) {
                        mutableStateOf(TextFieldValue((settings?.settings?.invitesLength ?: "").toString()))
                    }

                    TextInput(
                        value = length,
                        onValueChange = {
                            if (NumberRegex.matches(it.text)) {
                                length = it
                            }
                        },
                        label = "Length",
                        description = "The length of the invite code.",
                        enabled = !isLoading && settings?.settings?.invitesEnabled == true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true

                                val data = PartialServerSettingsSettings(
                                    invitesEnabled = invitesEnabled,
                                    invitesLength = length.text.toLongOrNull(),
                                )

                                val updateSettingsErrors = updateSettings(data)

                                errors = updateSettingsErrors

                                isLoading = false
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.save),
                            contentDescription = "Save settings"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Save"
                        )
                    }
                }
            }

            Container(
                scrollable = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Ratelimit",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                        )

                        Text(
                            text = "All options require a restart to take effect.",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }

                    var errors by remember { mutableStateOf<List<String>>(emptyList()) }

                    if (errors.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            errors.forEach {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }

                    var enableRatelimit by remember(settings?.settings?.ratelimitEnabled) {
                        mutableStateOf(settings?.settings?.ratelimitEnabled ?: false)
                    }

                    Switch(
                        checked = enableRatelimit,
                        onCheckedChange = { enableRatelimit = it },
                        label = "Enable Ratelimit",
                        description = "Enable ratelimiting for the server.",
                        enabled = !isLoading
                    )

                    var adminBypass by remember(settings?.settings?.ratelimitAdminBypass) {
                        mutableStateOf(settings?.settings?.ratelimitAdminBypass ?: false)
                    }

                    Switch(
                        checked = adminBypass,
                        onCheckedChange = { adminBypass = it },
                        label = "Admin Bypass",
                        description = "Allow admins to bypass the ratelimit.",
                        enabled = !isLoading
                    )

                    var maxRequests by remember(settings?.settings?.ratelimitMax) {
                        mutableStateOf(TextFieldValue((settings?.settings?.ratelimitMax ?: "").toString()))
                    }

                    TextInput(
                        value = maxRequests,
                        onValueChange = {
                            if (NumberRegex.matches(it.text)) {
                                maxRequests = it
                            }
                        },
                        label = "Max Requests",
                        description = "The maximum number of requests allowed within the window. If no window is set, this is the maximum number of requests until it reaches the limit.",
                        enabled = !isLoading && settings?.settings?.invitesEnabled == true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var window by remember(settings?.settings?.ratelimitWindow) {
                        mutableStateOf(TextFieldValue((settings?.settings?.ratelimitWindow ?: "").toString()))
                    }

                    TextInput(
                        value = window,
                        onValueChange = {
                            if (NumberRegex.matches(it.text)) {
                                window = it
                            }
                        },
                        label = "Window",
                        description = "The window in seconds to allow the max requests.",
                        enabled = !isLoading && settings?.settings?.invitesEnabled == true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var allowList by remember(settings?.settings?.ratelimitAllowList) {
                        mutableStateOf(TextFieldValue(settings?.settings?.ratelimitAllowList?.joinToString(", ") ?: ""))
                    }

                    TextInput(
                        value = allowList,
                        onValueChange = {
                            allowList = it
                        },
                        label = "Allow List",
                        description = "A comma-separated list of IP addresses to bypass the ratelimit.",
                        enabled = !isLoading && settings?.settings?.invitesEnabled == true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true

                                val data = PartialServerSettingsSettings(
                                    ratelimitEnabled = enableRatelimit,
                                    ratelimitAdminBypass = adminBypass,
                                    ratelimitMax = maxRequests.text.toLongOrNull(),
                                    ratelimitWindow = window.text.toLongOrNull(),
                                    ratelimitAllowList = allowList.text
                                        .split(", ", ",")
                                        .map { it.trim() }
                                        .filter { it.isNotEmpty() },
                                )

                                val updateSettingsErrors = updateSettings(data)

                                errors = updateSettingsErrors

                                isLoading = false
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.save),
                            contentDescription = "Save settings"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Save"
                        )
                    }
                }
            }

            Container(
                scrollable = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Website",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                    )

                    var errors by remember { mutableStateOf<List<String>>(emptyList()) }

                    if (errors.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            errors.forEach {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }

                    var title by remember(settings?.settings?.websiteTitle) {
                        mutableStateOf(TextFieldValue(settings?.settings?.websiteTitle ?: ""))
                    }

                    TextInput(
                        value = title,
                        onValueChange = { title = it },
                        label = "Title",
                        description = "The title of the website in browser tabs and at the top.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var titleLogo by remember(settings?.settings?.websiteTitleLogo) {
                        mutableStateOf(TextFieldValue(settings?.settings?.websiteTitleLogo ?: ""))
                    }

                    TextInput(
                        value = titleLogo,
                        onValueChange = { titleLogo = it },
                        label = "Title Logo",
                        description = "The URL to use for the title logo. This is placed to the left of the title.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // TODO: external urls

                    var loginBackground by remember(settings?.settings?.websiteLoginBackground) {
                        mutableStateOf(TextFieldValue(settings?.settings?.websiteLoginBackground ?: ""))
                    }

                    TextInput(
                        value = loginBackground,
                        onValueChange = { loginBackground = it },
                        label = "Login Background",
                        description = "The URL to use for the login background.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var loginBackgroundBlur by remember(settings?.settings?.websiteLoginBackgroundBlur) {
                        mutableStateOf(settings?.settings?.websiteLoginBackgroundBlur ?: false)
                    }

                    Switch(
                        checked = loginBackgroundBlur,
                        onCheckedChange = { loginBackgroundBlur = it },
                        label = "Login Background Blur",
                        description = "Whether to blur the login background.",
                        enabled = !isLoading
                    )

                    var defaultAvatar by remember(settings?.settings?.websiteDefaultAvatar) {
                        mutableStateOf(TextFieldValue(settings?.settings?.websiteDefaultAvatar ?: ""))
                    }

                    TextInput(
                        value = defaultAvatar,
                        onValueChange = { defaultAvatar = it },
                        label = "Default Avatar",
                        description = "The path to use for the default avatar. This must be a path to an image, not a URL.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var termsOfService by remember(settings?.settings?.websiteTos) {
                        mutableStateOf(TextFieldValue(settings?.settings?.websiteTos ?: ""))
                    }

                    TextInput(
                        value = termsOfService,
                        onValueChange = { termsOfService = it },
                        label = "Terms of Service",
                        description = "Path to a Markdown (.md) file to use for the terms of service.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var defaultTheme by remember(settings?.settings?.websiteThemeDefault) {
                        mutableStateOf(TextFieldValue(settings?.settings?.websiteThemeDefault ?: ""))
                    }

                    TextInput(
                        value = defaultTheme,
                        onValueChange = { defaultTheme = it },
                        label = "Default Theme",
                        description = "The default theme to use for the website.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var darkTheme by remember(settings?.settings?.websiteThemeDark) {
                        mutableStateOf(TextFieldValue(settings?.settings?.websiteThemeDark ?: ""))
                    }

                    TextInput(
                        value = darkTheme,
                        onValueChange = { darkTheme = it },
                        label = "Dark Theme",
                        description = "The dark theme to use for the website when the default theme is \"system\".",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var lightTheme by remember(settings?.settings?.websiteThemeLight) {
                        mutableStateOf(TextFieldValue(settings?.settings?.websiteThemeLight ?: ""))
                    }

                    TextInput(
                        value = lightTheme,
                        onValueChange = { lightTheme = it },
                        label = "Light Theme",
                        description = "The light theme to use for the website when the default theme is \"system\".",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true

                                val data = PartialServerSettingsSettings(
                                    websiteTitle = title.text,
                                    websiteTitleLogo = titleLogo.text.takeIf { it.isNotBlank() },
                                    websiteLoginBackground = loginBackground.text.takeIf { it.isNotBlank() },
                                    websiteLoginBackgroundBlur = loginBackgroundBlur,
                                    websiteDefaultAvatar = defaultAvatar.text.takeIf { it.isNotBlank() },
                                    websiteTos = termsOfService.text.takeIf { it.isNotBlank() },
                                    websiteThemeDefault = defaultTheme.text,
                                    websiteThemeDark = darkTheme.text,
                                    websiteThemeLight = lightTheme.text,
                                )

                                val updateSettingsErrors = updateSettings(data)

                                errors = updateSettingsErrors

                                isLoading = false
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.save),
                            contentDescription = "Save settings"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Save"
                        )
                    }
                }
            }

            Container(
                scrollable = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "PWA",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                        )

                        Text(
                            text = "Refresh the website after enabling PWA to see any changes.",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }

                    var errors by remember { mutableStateOf<List<String>>(emptyList()) }

                    if (errors.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            errors.forEach {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }

                    var pwaEnabled by remember(settings?.settings?.pwaEnabled) {
                        mutableStateOf(settings?.settings?.pwaEnabled ?: false)
                    }

                    Switch(
                        checked = pwaEnabled,
                        onCheckedChange = { pwaEnabled = it },
                        label = "PWA Enabled",
                        description = "Allow users to install the Zipline PWA on their devices.",
                        enabled = !isLoading
                    )

                    var title by remember(settings?.settings?.pwaTitle) {
                        mutableStateOf(TextFieldValue(settings?.settings?.pwaTitle ?: ""))
                    }

                    TextInput(
                        value = title,
                        onValueChange = {
                            title = it
                        },
                        label = "Title",
                        description = "The title for the PWA.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var shortName by remember(settings?.settings?.pwaShortName) {
                        mutableStateOf(TextFieldValue(settings?.settings?.pwaShortName ?: ""))
                    }

                    TextInput(
                        value = shortName,
                        onValueChange = {
                            shortName = it
                        },
                        label = "Short Name",
                        description = "The short name for the PWA.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var description by remember(settings?.settings?.pwaDescription) {
                        mutableStateOf(TextFieldValue(settings?.settings?.pwaDescription ?: ""))
                    }

                    TextInput(
                        value = description,
                        onValueChange = {
                            description = it
                        },
                        label = "Description",
                        description = "he description for the PWA.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var themeColor by remember(settings?.settings?.pwaThemeColor) {
                        val color = settings?.settings?.pwaThemeColor?.toColorInt()

                        mutableStateOf(if (color != null) Color(color) else Color.Black)
                    }

                    ColorPicker(
                        color = themeColor,
                        onColorChange = { themeColor = it },
                        label = "Theme Color",
                        description = "The theme color for the PWA.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var backgroundColor by remember(settings?.settings?.pwaBackgroundColor) {
                        val color = settings?.settings?.pwaBackgroundColor?.toColorInt()

                        mutableStateOf(if (color != null) Color(color) else Color.Black)
                    }

                    ColorPicker(
                        color = backgroundColor,
                        onColorChange = { backgroundColor = it },
                        label = "Background Color",
                        description = "The background color for the PWA.",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true

                                val data = PartialServerSettingsSettings(
                                    pwaEnabled = pwaEnabled,
                                    pwaTitle = title.text.takeIf { it.isNotBlank() },
                                    pwaShortName = shortName.text.takeIf { it.isNotBlank() },
                                    pwaDescription = description.text.takeIf { it.isNotBlank() },
                                    pwaThemeColor = themeColor.toHex(),
                                    pwaBackgroundColor = backgroundColor.toHex(),
                                )

                                val updateSettingsErrors = updateSettings(data)

                                errors = updateSettingsErrors

                                isLoading = false
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.save),
                            contentDescription = "Save settings"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Save"
                        )
                    }
                }
            }

            Container(
                scrollable = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "OAuth",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                        )

                        Text(
                            text = "For OAuth to work, the \"OAuth Registration\" setting must be enabled in the Features section. If you have issues, try restarting Zipline after saving.",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }

                    var errors by remember { mutableStateOf<List<String>>(emptyList()) }

                    if (errors.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            errors.forEach {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }

                    var bypassLocalLogin by remember(settings?.settings?.oauthBypassLocalLogin) {
                        mutableStateOf(settings?.settings?.oauthBypassLocalLogin ?: false)
                    }

                    Switch(
                        checked = bypassLocalLogin,
                        onCheckedChange = { bypassLocalLogin = it },
                        label = "Bypass Local Login",
                        description = "Skips the local login page and redirects to the OAuth provider, this only works with one provider enabled.",
                        enabled = !isLoading
                    )

                    var loginOnly by remember(settings?.settings?.oauthLoginOnly) {
                        mutableStateOf(settings?.settings?.oauthLoginOnly ?: false)
                    }

                    Switch(
                        checked = loginOnly,
                        onCheckedChange = { loginOnly = it },
                        label = "Login Only",
                        description = "Disables registration and only allows login with OAuth, existing users can link providers for example.",
                        enabled = !isLoading
                    )

                    var discordClientId by remember(settings?.settings?.oauthDiscordClientId) {
                        mutableStateOf(TextFieldValue(settings?.settings?.oauthDiscordClientId ?: ""))
                    }
                    var discordClientSecret by remember(settings?.settings?.oauthDiscordClientSecret) {
                        mutableStateOf(TextFieldValue(settings?.settings?.oauthDiscordClientSecret ?: ""))
                    }
                    var discordAllowedIds by remember(settings?.settings?.oauthDiscordAllowedIds) {
                        mutableStateOf(TextFieldValue(settings?.settings?.oauthDiscordAllowedIds?.joinToString(", ") ?: ""))
                    }
                    var discordDeniedIds by remember(settings?.settings?.oauthDiscordDeniedIds) {
                        mutableStateOf(TextFieldValue(settings?.settings?.oauthDiscordDeniedIds?.joinToString(", ") ?: ""))
                    }
                    var discordRedirectUrl by remember(settings?.settings?.oauthDiscordRedirectUri) {
                        mutableStateOf(TextFieldValue(settings?.settings?.oauthDiscordRedirectUri ?: ""))
                    }

                    Container(
                        scrollable = false,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "Discord",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable(
                                        onClick = {
                                            val url = "https://discord.com/developers/applications"

                                            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                            context.startActivity(intent)
                                        }
                                )
                            )

                            TextInput(
                                value = discordClientId,
                                onValueChange = {
                                    discordClientId = it
                                },
                                label = "Discord Client ID",
                                enabled = !isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )

                            TextInput(
                                value = discordClientSecret,
                                onValueChange = {
                                    discordClientSecret = it
                                },
                                label = "Discord Client Secret",
                                enabled = !isLoading,
                                isPassword = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            TextInput(
                                value = discordAllowedIds,
                                onValueChange = {
                                    discordAllowedIds = it
                                },
                                label = "Discord Allowed IDs",
                                description = "A comma-separated list of Discord user IDs that are allowed to log in. Leave empty to disable allow list.",
                                enabled = !isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )

                            TextInput(
                                value = discordDeniedIds,
                                onValueChange = {
                                    discordDeniedIds = it
                                },
                                label = "Discord Denied IDs",
                                description = "A comma-separated list of Discord user IDs that are denied from logging in. Leave empty to disable deny list.",
                                enabled = !isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )

                            TextInput(
                                value = discordRedirectUrl,
                                onValueChange = {
                                    discordRedirectUrl = it
                                },
                                label = "Discord Redirect URL",
                                description = "The redirect URL to use instead of the host when logging in. This is not required if the URL generated by Zipline works as intended.",
                                enabled = !isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    var googleClientId by remember(settings?.settings?.oauthGoogleClientId) {
                        mutableStateOf(TextFieldValue(settings?.settings?.oauthGoogleClientId ?: ""))
                    }
                    var googleClientSecret by remember(settings?.settings?.oauthGoogleClientSecret) {
                        mutableStateOf(TextFieldValue(settings?.settings?.oauthGoogleClientSecret ?: ""))
                    }
                    var googleRedirectUrl by remember(settings?.settings?.oauthGoogleRedirectUri) {
                        mutableStateOf(TextFieldValue(settings?.settings?.oauthGoogleRedirectUri ?: ""))
                    }

                    Container(
                        scrollable = false,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "Google",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable(
                                        onClick = {
                                            val url = "https://console.developers.google.com/"

                                            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                            context.startActivity(intent)
                                        }
                                    )
                            )

                            TextInput(
                                value = googleClientId,
                                onValueChange = {
                                    googleClientId = it
                                },
                                label = "Google Client ID",
                                enabled = !isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )

                            TextInput(
                                value = googleClientSecret,
                                onValueChange = {
                                    googleClientSecret = it
                                },
                                label = "Google Client Secret",
                                enabled = !isLoading,
                                isPassword = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            TextInput(
                                value = googleRedirectUrl,
                                onValueChange = {
                                    googleRedirectUrl = it
                                },
                                label = "Google Redirect URL",
                                description = "The redirect URL to use instead of the host when logging in. This is not required if the URL generated by Zipline works as intended.",
                                enabled = !isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    var githubClientId by remember(settings?.settings?.oauthGithubClientId) {
                        mutableStateOf(TextFieldValue(settings?.settings?.oauthGithubClientId ?: ""))
                    }
                    var githubClientSecret by remember(settings?.settings?.oauthGithubClientSecret) {
                        mutableStateOf(TextFieldValue(settings?.settings?.oauthGithubClientSecret ?: ""))
                    }
                    var githubRedirectUrl by remember(settings?.settings?.oauthGithubRedirectUri) {
                        mutableStateOf(TextFieldValue(settings?.settings?.oauthGithubRedirectUri ?: ""))
                    }

                    Container(
                        scrollable = false,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "GitHub",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable(
                                        onClick = {
                                            val url = "https://github.com/settings/developers"

                                            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                            context.startActivity(intent)
                                        }
                                )
                            )

                            TextInput(
                                value = githubClientId,
                                onValueChange = {
                                    githubClientId = it
                                },
                                label = "GitHub Client ID",
                                enabled = !isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )

                            TextInput(
                                value = githubClientSecret,
                                onValueChange = {
                                    githubClientSecret = it
                                },
                                label = "GitHub Client Secret",
                                enabled = !isLoading,
                                isPassword = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            TextInput(
                                value = githubRedirectUrl,
                                onValueChange = {
                                    githubRedirectUrl = it
                                },
                                label = "GitHub Redirect URL",
                                description = "The redirect URL to use instead of the host when logging in. This is not required if the URL generated by Zipline works as intended.",
                                enabled = !isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    var oidcClientId by remember(settings?.settings?.oauthOidcClientId) {
                        mutableStateOf(TextFieldValue(settings?.settings?.oauthOidcClientId ?: ""))
                    }
                    var oidcClientSecret by remember(settings?.settings?.oauthOidcClientSecret) {
                        mutableStateOf(TextFieldValue(settings?.settings?.oauthOidcClientSecret ?: ""))
                    }
                    var oidcAuthorizeUrl by remember(settings?.settings?.oauthOidcAuthorizeUrl) {
                        mutableStateOf(TextFieldValue(settings?.settings?.oauthOidcAuthorizeUrl ?: ""))
                    }
                    var oidcTokenUrl by remember(settings?.settings?.oauthOidcTokenUrl) {
                        mutableStateOf(TextFieldValue(settings?.settings?.oauthOidcTokenUrl ?: ""))
                    }
                    var oidcUserinfoUrl by remember(settings?.settings?.oauthOidcUserinfoUrl) {
                        mutableStateOf(TextFieldValue(settings?.settings?.oauthOidcUserinfoUrl ?: ""))
                    }
                    var oidcRedirectUrl by remember(settings?.settings?.oauthOidcRedirectUri) {
                        mutableStateOf(TextFieldValue(settings?.settings?.oauthOidcRedirectUri ?: ""))
                    }

                    Container(
                        scrollable = false,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "OpenID Connect",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                ),
                            )

                            TextInput(
                                value = oidcClientId,
                                onValueChange = {
                                    oidcClientId = it
                                },
                                label = "OIDC Client ID",
                                enabled = !isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )

                            TextInput(
                                value = oidcClientSecret,
                                onValueChange = {
                                    oidcClientSecret = it
                                },
                                label = "OIDC Client Secret",
                                enabled = !isLoading,
                                isPassword = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            TextInput(
                                value = oidcAuthorizeUrl,
                                onValueChange = {
                                    oidcAuthorizeUrl = it
                                },
                                label = "OIDC Authorize URL",
                                enabled = !isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )

                            TextInput(
                                value = oidcTokenUrl,
                                onValueChange = {
                                    oidcTokenUrl = it
                                },
                                label = "OIDC Token URL",
                                enabled = !isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )

                            TextInput(
                                value = oidcUserinfoUrl,
                                onValueChange = {
                                    oidcUserinfoUrl = it
                                },
                                label = "OIDC Userinfo URL",
                                enabled = !isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )

                            TextInput(
                                value = oidcRedirectUrl,
                                onValueChange = {
                                    oidcRedirectUrl = it
                                },
                                label = "OIDC Redirect URL",
                                description = "The redirect URL to use instead of the host when logging in. This is not required if the URL generated by Zipline works as intended.",
                                enabled = !isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true

                                val data = PartialServerSettingsSettings(
                                    oauthBypassLocalLogin = bypassLocalLogin,
                                    oauthLoginOnly = loginOnly,
                                    oauthDiscordClientId = discordClientId.text.takeIf { it.isNotBlank() },
                                    oauthDiscordClientSecret = discordClientSecret.text.takeIf { it.isNotBlank() },
                                    oauthDiscordAllowedIds = discordAllowedIds.text
                                        .split(", ", ",")
                                        .map { it.trim() }
                                        .filter { it.isNotBlank() },
                                    oauthDiscordDeniedIds = discordDeniedIds.text
                                        .split(", ", ",")
                                        .map { it.trim() }
                                        .filter { it.isNotBlank() }
                                    ,
                                    oauthDiscordRedirectUri = discordRedirectUrl.text.takeIf { it.isNotBlank() },
                                    oauthGoogleClientId = googleClientId.text.takeIf { it.isNotBlank() },
                                    oauthGoogleClientSecret = googleClientSecret.text.takeIf { it.isNotBlank() },
                                    oauthGoogleRedirectUri = googleRedirectUrl.text.takeIf { it.isNotBlank() },
                                    oauthGithubClientId = githubClientId.text.takeIf { it.isNotBlank() },
                                    oauthGithubClientSecret = githubClientSecret.text.takeIf { it.isNotBlank() },
                                    oauthGithubRedirectUri = githubRedirectUrl.text.takeIf { it.isNotBlank() },
                                    oauthOidcClientId = oidcClientId.text.takeIf { it.isNotBlank() },
                                    oauthOidcClientSecret = oidcClientSecret.text.takeIf { it.isNotBlank() },
                                    oauthOidcAuthorizeUrl = oidcAuthorizeUrl.text.takeIf { it.isNotBlank() },
                                    oauthOidcTokenUrl = oidcTokenUrl.text.takeIf { it.isNotBlank() },
                                    oauthOidcUserinfoUrl = oidcUserinfoUrl.text.takeIf { it.isNotBlank() },
                                    oauthOidcRedirectUri = oidcRedirectUrl.text.takeIf { it.isNotBlank() },
                                )

                                val updateSettingsErrors = updateSettings(data)

                                errors = updateSettingsErrors

                                isLoading = false
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.save),
                            contentDescription = "Save settings"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Save"
                        )
                    }
                }
            }

            /*
                TODO:
                - HTTP Webhooks
                - Domains
                - Discord Webhook
                    - On Upload
                    - On Shorten
            */
        }
    }
}