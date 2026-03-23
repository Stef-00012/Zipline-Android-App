package com.stefdp.zipline.screens.login

import android.content.Context
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.LocalLoggedUser
import com.stefdp.zipline.LocalUpdateLoggedUser
import com.stefdp.zipline.LocalUpdateLoggedUserAvatar
import com.stefdp.zipline.LocalUpdatePublicSettings
import com.stefdp.zipline.LocalUpdateWebSettings
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.User
import com.stefdp.zipline.network.requests.LoginResult
import com.stefdp.zipline.network.requests.getToken
import com.stefdp.zipline.network.requests.login
import com.stefdp.zipline.screens.*
import com.stefdp.zipline.ui.theme.DarkGray
import com.stefdp.zipline.ui.theme.getButtonColors
import com.stefdp.zipline.utils.SecureStorage
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    if (LocalLoggedUser.current is User && currentDestination?.route == LoginScreen::class.qualifiedName) {
        navController.navigate(HomeScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp),
                )
                .padding(16.dp),
        ) {
            Column {
                var serverUrl by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
                var token by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }

                var username by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
                var password by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
                var totp by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }

                var isTokenLogin by rememberSaveable { mutableStateOf(false) }
                var isTotpRequired by rememberSaveable { mutableStateOf(false) }

                var errorMessage by remember { mutableStateOf<String?>(null) }

                var isLoading by remember { mutableStateOf(false) }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp),
                        fontWeight = FontWeight.Bold,
                    )
                }

                Text(
                    text = "Login",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )

                TextInput(
                    modifier = Modifier.fillMaxWidth(),
                    value = serverUrl,
                    onValueChange = { serverUrl = it },
                    placeholder = "https://example.com",
                    label = "Zipline URL",
                    enabled = !isLoading
                )

                if (isTokenLogin) {
                    TextInput(
                        modifier = Modifier.fillMaxWidth(),
                        value = token,
                        onValueChange = { token = it },
                        isPassword = true,
                        placeholder = "MTc5GzAwNDB5Nzk47A==.OGE4ODN2",
                        label = "Token",
                        enabled = !isLoading
                    )
                } else {
                    TextInput(
                        modifier = Modifier.fillMaxWidth(),
                        value = username,
                        onValueChange = { username = it },
                        placeholder = "My Username",
                        label = "Username",
                        enabled = !isLoading
                    )

                    TextInput(
                        modifier = Modifier.fillMaxWidth(),
                        value = password,
                        onValueChange = { password = it },
                        isPassword = true,
                        placeholder = "myCO0lP4ssW0rd!",
                        label = "Password",
                        enabled = !isLoading
                    )

                    if (isTotpRequired) {
                        TextInput(
                            modifier = Modifier.fillMaxWidth(),
                            value = totp,
                            onValueChange = { totp = it },
                            placeholder = "123456",
                            label = "TOTP",
                            enabled = !isLoading
                        )
                    }
                }

                Button(
                    onClick = {
                        isTokenLogin = !isTokenLogin
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 12.dp
                        ),
                    colors = getButtonColors().copy(
                        containerColor = DarkGray,
                        disabledContainerColor = DarkGray.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = "Use ${if (isTokenLogin) "Password" else "Token"} Login",
                    )
                }

                val coroutineScope = rememberCoroutineScope()

                val updateLoggedUser = LocalUpdateLoggedUser.current
                val updateLoggedUserAvatar = LocalUpdateLoggedUserAvatar.current
                val updatePublicSettings = LocalUpdatePublicSettings.current
                val updateWebSettings = LocalUpdateWebSettings.current

                Button(
                    onClick = {
                        coroutineScope.launch {
                            isLoading = true

                            val secureStore = SecureStorage.getInstance(context)

                            secureStore.set("serverUrl", serverUrl.text.lowercase())

                            if (isTokenLogin) {
                                secureStore.set("token", token.text)

                                val userStatsRes = updateLoggedUser()

                                userStatsRes
                                    .onSuccess {
                                        updatePublicSettings()
                                        updateWebSettings()
                                        updateLoggedUserAvatar()

                                        if (currentDestination?.route == LoginScreen::class.qualifiedName) {
                                            navController.navigate(HomeScreen) {
                                                popUpTo(navController.graph.id) { inclusive = true }
                                            }
                                        }

                                        isLoading = false
                                    }
                                    .onFailure { error ->
                                        errorMessage = error.message

                                        isLoading = false
                                    }
                            } else {
                                val loginRes = login(
                                    context = context,
                                    username = username.text,
                                    password = password.text,
                                    code = totp.text.ifEmpty { null },
                                )

                                loginRes
                                    .onSuccess { loginStatus ->
                                        if (loginStatus is LoginResult.TotpRequired) {
                                            isTotpRequired = true
                                            isLoading = false
                                        } else if (loginStatus is LoginResult.Success) {
                                            Log.d("LoginScreen", "Login successful, retrieving token...")
                                            val authCookie = loginStatus.authCookie

                                            val tokenRes = getToken(
                                                context = context,
                                                cookie = authCookie,
                                            )

                                            Log.d("LoginScreen", "Token retrieval result: ${tokenRes.isSuccess}")

                                            tokenRes
                                                .onSuccess { tokenData ->
                                                    if (tokenData.token == null) {
                                                        errorMessage = "Failed to retrieve token"
                                                        isLoading = false

                                                        return@launch
                                                    }

                                                    secureStore.set("token", tokenData.token)

                                                    val userStatsRes = updateLoggedUser()

                                                    userStatsRes
                                                        .onSuccess {
                                                            updatePublicSettings()
                                                            updateWebSettings()
                                                            updateLoggedUserAvatar()

                                                            if (currentDestination?.route == LoginScreen::class.qualifiedName) {
                                                                navController.navigate(HomeScreen) {
                                                                    popUpTo(navController.graph.id) { inclusive = true }
                                                                }
                                                            }

                                                            isLoading = false
                                                        }
                                                        .onFailure { error ->
                                                            errorMessage = error.message

                                                            isLoading = false
                                                        }
                                                }
                                                .onFailure { error ->
                                                    errorMessage = error.message
                                                    isLoading = false
                                                }

                                        }
                                    }
                                    .onFailure { error ->
                                        errorMessage = error.message

                                        isLoading = false
                                    }
                            }
                        }
                    },
                    enabled = !isLoading,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = LocalContentColor.current,
                            )

                            Spacer(
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = "Login",
                            fontWeight = FontWeight.Bold,
                            color = LocalContentColor.current,
                        )
                    }
                }
            }
        }
    }
}