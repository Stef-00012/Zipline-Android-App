package com.stefdp.zipline.screens.admin.settings.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
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
internal fun WebsiteCategory(
    settings: ServerSettings?,
    updateSettings: suspend (PartialServerSettingsSettings) -> List<String>,
    isLoading: Boolean,
    setLoading: (Boolean) -> Unit,
    title: String
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
            Text(
                text = title,
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

            val coroutineScope = rememberCoroutineScope()

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    coroutineScope.launch {
                        setLoading(true)

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