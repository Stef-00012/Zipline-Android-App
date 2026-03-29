package com.stefdp.zipline.screens.admin.settings.categories

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Container
import com.stefdp.zipline.components.Switch
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.network.models.PartialServerSettingsSettings
import com.stefdp.zipline.network.models.ServerSettings
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
internal fun OAuthCategory(
    context: Context,
    settings: ServerSettings?,
    updateSettings: suspend (PartialServerSettingsSettings) -> List<String>,
    isLoading: Boolean,
    setLoading: (Boolean) -> Unit,
    title: String,
    settingsUpdateTick: Int
) {
    Container(
        scrollable = false,
        modifier = Modifier.fillMaxWidth()
    ) {
        val scrollState = rememberScrollState()

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .verticalScrollWithScrollbar(
                    scrollState = scrollState,
                )
                .padding(12.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
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

            var bypassLocalLogin by remember(settings?.settings?.oauthBypassLocalLogin, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.oauthBypassLocalLogin ?: false)
            }

            Switch(
                checked = bypassLocalLogin,
                onCheckedChange = { bypassLocalLogin = it },
                label = "Bypass Local Login",
                description = "Skips the local login page and redirects to the OAuth provider, this only works with one provider enabled.",
                enabled = !isLoading
            )

            var loginOnly by remember(settings?.settings?.oauthLoginOnly, settingsUpdateTick) {
                mutableStateOf(settings?.settings?.oauthLoginOnly ?: false)
            }

            Switch(
                checked = loginOnly,
                onCheckedChange = { loginOnly = it },
                label = "Login Only",
                description = "Disables registration and only allows login with OAuth, existing users can link providers for example.",
                enabled = !isLoading
            )

            var discordClientId by remember(settings?.settings?.oauthDiscordClientId, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.oauthDiscordClientId ?: ""))
            }
            var discordClientSecret by remember(settings?.settings?.oauthDiscordClientSecret, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.oauthDiscordClientSecret ?: ""))
            }
            var discordAllowedIds by remember(settings?.settings?.oauthDiscordAllowedIds, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.oauthDiscordAllowedIds?.joinToString(", ") ?: ""))
            }
            var discordDeniedIds by remember(settings?.settings?.oauthDiscordDeniedIds, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.oauthDiscordDeniedIds?.joinToString(", ") ?: ""))
            }
            var discordRedirectUrl by remember(settings?.settings?.oauthDiscordRedirectUri, settingsUpdateTick) {
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

            var googleClientId by remember(settings?.settings?.oauthGoogleClientId, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.oauthGoogleClientId ?: ""))
            }
            var googleClientSecret by remember(settings?.settings?.oauthGoogleClientSecret, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.oauthGoogleClientSecret ?: ""))
            }
            var googleRedirectUrl by remember(settings?.settings?.oauthGoogleRedirectUri, settingsUpdateTick) {
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

            var githubClientId by remember(settings?.settings?.oauthGithubClientId, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.oauthGithubClientId ?: ""))
            }
            var githubClientSecret by remember(settings?.settings?.oauthGithubClientSecret, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.oauthGithubClientSecret ?: ""))
            }
            var githubRedirectUrl by remember(settings?.settings?.oauthGithubRedirectUri, settingsUpdateTick) {
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

            var oidcClientId by remember(settings?.settings?.oauthOidcClientId, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.oauthOidcClientId ?: ""))
            }
            var oidcClientSecret by remember(settings?.settings?.oauthOidcClientSecret, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.oauthOidcClientSecret ?: ""))
            }
            var oidcAuthorizeUrl by remember(settings?.settings?.oauthOidcAuthorizeUrl, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.oauthOidcAuthorizeUrl ?: ""))
            }
            var oidcTokenUrl by remember(settings?.settings?.oauthOidcTokenUrl, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.oauthOidcTokenUrl ?: ""))
            }
            var oidcUserinfoUrl by remember(settings?.settings?.oauthOidcUserinfoUrl, settingsUpdateTick) {
                mutableStateOf(TextFieldValue(settings?.settings?.oauthOidcUserinfoUrl ?: ""))
            }
            var oidcRedirectUrl by remember(settings?.settings?.oauthOidcRedirectUri, settingsUpdateTick) {
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

            val coroutineScope = rememberCoroutineScope()

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    coroutineScope.launch {
                        setLoading(true)

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

                        setLoading(false)
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
}