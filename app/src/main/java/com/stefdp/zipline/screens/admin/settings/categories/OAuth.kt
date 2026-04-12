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
import com.stefdp.zipline.network.models.OauthSettings
import com.stefdp.zipline.network.models.ServerSettings
import com.stefdp.zipline.screens.admin.settings.AdminSettingsUiState
import com.stefdp.zipline.screens.admin.settings.AdminSettingsViewModel
import com.stefdp.zipline.utils.ScrollbarConfig
import com.stefdp.zipline.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
internal fun OAuthCategory(
    context: Context,
    updateSettings: (OauthSettings) -> Unit,
    title: String,
    viewModel: AdminSettingsViewModel,
    state: AdminSettingsUiState,
) {
    Container(
        scrollable = false,
        modifier = Modifier.fillMaxWidth()
    ) {
        val scrollState = rememberScrollState()

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
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

            Switch(
                checked = state.oauthBypassLocalLogin,
                onCheckedChange = {
                    viewModel.setOauthBypassLocalLogin(it)
                },
                label = "Bypass Local Login",
                description = "Skips the local login page and redirects to the OAuth provider, this only works with one provider enabled.",
                enabled = !state.isLoading
            )

            Switch(
                checked = state.oauthLoginOnly,
                onCheckedChange = {
                    viewModel.setOauthLoginOnly(it)
                },
                label = "Login Only",
                description = "Disables registration and only allows login with OAuth, existing users can link providers for example.",
                enabled = !state.isLoading
            )

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
                            color = MaterialTheme.colorScheme.tertiary,
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
                        value = state.oauthDiscordClientId,
                        onValueChange = {
                            viewModel.setOauthDiscordClientId(it)
                        },
                        label = "Discord Client ID",
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextInput(
                        value = state.oauthDiscordClientSecret,
                        onValueChange = {
                            viewModel.setOauthDiscordClientSecret(it)
                        },
                        label = "Discord Client Secret",
                        enabled = !state.isLoading,
                        isPassword = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextInput(
                        value = state.oauthDiscordAllowedIds,
                        onValueChange = {
                            viewModel.setOauthDiscordAllowedIds(it)
                        },
                        label = "Discord Allowed IDs",
                        description = "A comma-separated list of Discord user IDs that are allowed to log in. Leave empty to disable allow list.",
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextInput(
                        value = state.oauthDiscordDeniedIds,
                        onValueChange = {
                            viewModel.setOauthDiscordDeniedIds(it)
                        },
                        label = "Discord Denied IDs",
                        description = "A comma-separated list of Discord user IDs that are denied from logging in. Leave empty to disable deny list.",
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextInput(
                        value = state.oauthDiscordRedirectUri,
                        onValueChange = {
                            viewModel.setOauthDiscordRedirectUri(it)
                        },
                        label = "Discord Redirect URL",
                        description = "The redirect URL to use instead of the host when logging in. This is not required if the URL generated by Zipline works as intended.",
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
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
                        text = "Google",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary,
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
                        value = state.oauthGoogleClientId,
                        onValueChange = {
                            viewModel.setOauthGoogleClientId(it)
                        },
                        label = "Google Client ID",
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextInput(
                        value = state.oauthGoogleClientSecret,
                        onValueChange = {
                            viewModel.setOauthGoogleClientSecret(it)
                        },
                        label = "Google Client Secret",
                        enabled = !state.isLoading,
                        isPassword = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextInput(
                        value = state.oauthGoogleRedirectUri,
                        onValueChange = {
                            viewModel.setOauthGoogleRedirectUri(it)
                        },
                        label = "Google Redirect URL",
                        description = "The redirect URL to use instead of the host when logging in. This is not required if the URL generated by Zipline works as intended.",
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
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
                        text = "GitHub",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary,
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
                        value = state.oauthGithubClientId,
                        onValueChange = {
                            viewModel.setOauthGithubClientId(it)
                        },
                        label = "GitHub Client ID",
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextInput(
                        value = state.oauthGithubClientSecret,
                        onValueChange = {
                            viewModel.setOauthGithubClientSecret(it)
                        },
                        label = "GitHub Client Secret",
                        enabled = !state.isLoading,
                        isPassword = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextInput(
                        value = state.oauthGithubRedirectUri,
                        onValueChange = {
                            viewModel.setOauthGithubRedirectUri(it)
                        },
                        label = "GitHub Redirect URL",
                        description = "The redirect URL to use instead of the host when logging in. This is not required if the URL generated by Zipline works as intended.",
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
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
                        text = "OpenID Connect",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                    )

                    TextInput(
                        value = state.oauthOidcClientId,
                        onValueChange = {
                            viewModel.setOauthOidcClientId(it)
                        },
                        label = "OIDC Client ID",
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextInput(
                        value = state.oauthOidcClientSecret,
                        onValueChange = {
                            viewModel.setOauthOidcClientSecret(it)
                        },
                        label = "OIDC Client Secret",
                        enabled = !state.isLoading,
                        isPassword = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextInput(
                        value = state.oauthOidcAuthorizeUrl,
                        onValueChange = {
                            viewModel.setOauthOidcAuthorizeUrl(it)
                        },
                        label = "OIDC Authorize URL",
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextInput(
                        value = state.oauthOidcTokenUrl,
                        onValueChange = {
                            viewModel.setOauthOidcTokenUrl(it)
                        },
                        label = "OIDC Token URL",
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextInput(
                        value = state.oauthOidcUserinfoUrl,
                        onValueChange = {
                            viewModel.setOauthOidcUserinfoUrl(it)
                        },
                        label = "OIDC Userinfo URL",
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextInput(
                        value = state.oauthOidcRedirectUri,
                        onValueChange = {
                            viewModel.setOauthOidcRedirectUri(it)
                        },
                        label = "OIDC Redirect URL",
                        description = "The redirect URL to use instead of the host when logging in. This is not required if the URL generated by Zipline works as intended.",
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val data = OauthSettings(
                        oauthBypassLocalLogin = state.oauthBypassLocalLogin,
                        oauthLoginOnly = state.oauthLoginOnly,
                        oauthDiscordClientId = state.oauthDiscordClientId.text.takeIf { it.isNotBlank() },
                        oauthDiscordClientSecret = state.oauthDiscordClientSecret.text.takeIf { it.isNotBlank() },
                        oauthDiscordAllowedIds = state.oauthDiscordAllowedIds.text
                            .split(", ", ",")
                            .map { it.trim() }
                            .filter { it.isNotBlank() },
                        oauthDiscordDeniedIds = state.oauthDiscordDeniedIds.text
                            .split(", ", ",")
                            .map { it.trim() }
                            .filter { it.isNotBlank() }
                        ,
                        oauthDiscordRedirectUri = state.oauthDiscordRedirectUri.text.takeIf { it.isNotBlank() },
                        oauthGoogleClientId = state.oauthGoogleClientId.text.takeIf { it.isNotBlank() },
                        oauthGoogleClientSecret = state.oauthGoogleClientSecret.text.takeIf { it.isNotBlank() },
                        oauthGoogleRedirectUri = state.oauthGoogleRedirectUri.text.takeIf { it.isNotBlank() },
                        oauthGithubClientId = state.oauthGithubClientId.text.takeIf { it.isNotBlank() },
                        oauthGithubClientSecret = state.oauthGithubClientSecret.text.takeIf { it.isNotBlank() },
                        oauthGithubRedirectUri = state.oauthGithubRedirectUri.text.takeIf { it.isNotBlank() },
                        oauthOidcClientId = state.oauthOidcClientId.text.takeIf { it.isNotBlank() },
                        oauthOidcClientSecret = state.oauthOidcClientSecret.text.takeIf { it.isNotBlank() },
                        oauthOidcAuthorizeUrl = state.oauthOidcAuthorizeUrl.text.takeIf { it.isNotBlank() },
                        oauthOidcTokenUrl = state.oauthOidcTokenUrl.text.takeIf { it.isNotBlank() },
                        oauthOidcUserinfoUrl = state.oauthOidcUserinfoUrl.text.takeIf { it.isNotBlank() },
                        oauthOidcRedirectUri = state.oauthOidcRedirectUri.text.takeIf { it.isNotBlank() },
                    )

                    updateSettings(data)
                },
                enabled = !state.isLoading
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