package com.stefdp.zipline.screens.register

import android.content.Context
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.Logger
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextDivider
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.screens.LoginScreen
import com.stefdp.zipline.screens.RegisterScreen

@Composable
fun RegisterScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    serverUrl: String,
    viewModel: RegisterViewModel = viewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.init(context, serverUrl)
    }

    LaunchedEffect(state.supportsRegistration) {
        if (!state.supportsRegistration) {
            navController.navigate(LoginScreen(serverUrl = state.serverUrl.text)) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
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
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )

                TextInput(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.username,
                    onValueChange = {
                        viewModel.setUsername(it)
                    },
                    placeholder = "My Username",
                    label = "Username",
                    enabled = !state.isLoading
                )

                TextInput(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.password,
                    onValueChange = {
                        viewModel.setPassword(it)
                    },
                    isPassword = true,
                    placeholder = "myCO0lP4ssW0rd!",
                    label = "Password",
                    enabled = !state.isLoading
                )

                Switch(
                    label = "Anonymize Device Info",
                    description = "Anonymize the device info sent to Zipline for device sessions management.",
                    checked = state.anonymizeDeviceInfo,
                    onCheckedChange = {
                        viewModel.setAnonymizeDeviceInfo(it)
                    },
                    enabled = !state.isLoading,
                )

                Switch(
                    label = buildAnnotatedString {
                        val rawString = "I agree to the [link]Terms of Service[/link]."
                        val linkTag = "[link]"
                        val linkEndTag = "[/link]"

                        val startIndex = rawString.indexOf(linkTag)
                        val endIndex = rawString.indexOf(linkEndTag)

                        if (startIndex != -1 && endIndex != -1) {
                            val cleanString = rawString.replace(linkTag, "").replace(linkEndTag, "")

                            append(cleanString)
                            addStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold
                                ),
                                start = 0,
                                end = startIndex - 1,
                            )

                            addLink(
                                url = LinkAnnotation.Url(
                                    url = "${state.serverUrl.text}/auth/tos",
                                    styles = TextLinkStyles(
                                        style = SpanStyle(
                                            color = MaterialTheme.colorScheme.tertiary,
                                            textDecoration = TextDecoration.Underline,
                                            fontWeight = FontWeight.Bold
                                        ),
                                    )
                                ),
                                start = startIndex,
                                end = endIndex - linkTag.length
                            )
                        } else {
                            append(rawString)
                        }
                    },
                    checked = state.agreeTos,
                    onCheckedChange = {
                        viewModel.setAgreeTos(it)
                    },
                    enabled = !state.isLoading,
                )

                Button(
                    onClick = {
                        viewModel.onRegister(
                            context = context,
                            onSuccess = {
                                if (currentDestination?.route?.startsWith(RegisterScreen::class.qualifiedName ?: "") == true) {
                                    navController.navigate(LoginScreen(serverUrl = state.serverUrl.text)) {
                                        popUpTo(navController.graph.id) { inclusive = true }
                                    }
                                }
                            },
                            onError = { error ->
                                Logger.debug("RegisterScreen", "Register error: $error")

                                Notification.show(
                                    activity = activity,
                                    duration = 6000L
                                ) {
                                    Text(
                                        text = error,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                        )
                    },
                    enabled = !state.isLoading && state.agreeTos && state.username.text.isNotBlank() && state.password.text.isNotBlank(),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (state.isLoading) {
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
                            text = "Register",
                            fontWeight = FontWeight.Bold,
                            color = LocalContentColor.current,
                        )
                    }
                }

                Column {
                    TextDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        text = "or"
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            if (currentDestination?.route?.startsWith(RegisterScreen::class.qualifiedName ?: "") == true) {
                                navController.navigate(LoginScreen(serverUrl = state.serverUrl.text)) {
                                    popUpTo(navController.graph.id) { inclusive = true }
                                }
                            }
                        }
                    ) {
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