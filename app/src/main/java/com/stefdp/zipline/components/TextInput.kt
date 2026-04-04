package com.stefdp.zipline.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.R
import com.stefdp.zipline.ui.theme.ZiplineTheme
import com.stefdp.zipline.ui.theme.getOutlinedTextFieldColors
import com.stefdp.zipline.utils.toAnnotatedString
import kotlinx.coroutines.launch

@Composable
fun TextInput(
    modifier: Modifier = Modifier,
    containerModifier: Modifier = Modifier,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    label: CharSequence? = null,
    description: CharSequence? = null,
    placeholder: CharSequence?= null,
    enabled: Boolean = true,
    isPassword: Boolean = false,
    singleLine: Boolean = true,
    onPasswordToggle: suspend (
        currentlyVisible: Boolean
    ) -> Boolean = { true },
    trailingIcon: Painter? = null,
    trailingIconColor: Color = LocalContentColor.current,
    onTrailingIconPress: () -> Unit = {},
    trailingIconContentDescription: String? = null,
    leadingIcon: Painter? = null,
    leadingIconColor: Color = LocalContentColor.current,
    onLeadingIconPress: () -> Unit = {},
    leadingIconContentDescription: String? = null,
    colors: TextFieldColors = getOutlinedTextFieldColors(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    readOnly: Boolean = false,
) {
    val coroutineScope = rememberCoroutineScope()

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    fun handlePasswordToggle() {
        coroutineScope.launch {
            val proceed = onPasswordToggle(passwordVisible)

            if (proceed) {
                passwordVisible = !passwordVisible
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = containerModifier
    ) {
        OutlinedTextField(
            value = value,
            keyboardActions = keyboardActions,
            onValueChange = onValueChange,
            textStyle = LocalTextStyle.current.copy(
                color = if (enabled)
                    MaterialTheme.colorScheme.onBackground
                else
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            ),
            modifier = modifier,
            readOnly = readOnly,
            singleLine = singleLine,
            label = if (label != null) {
                {
                    Text(
                        text = label.toAnnotatedString(),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = if (enabled)
                            MaterialTheme.colorScheme.onBackground
                        else
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            } else null,
            placeholder = if (placeholder != null) {
                {
                    Text(
                        text = placeholder.toAnnotatedString(),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            } else null,
            enabled = enabled,
            colors = colors,
            shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp),
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            trailingIcon = if (isPassword || trailingIcon != null) {
                {
                    IconButton(
                        modifier = Modifier.padding(end = 4.dp),
                        onClick = if (isPassword) {
                            ::handlePasswordToggle
                        } else onTrailingIconPress
                    ) {
                        Icon(
                            painter = if (isPassword) {
                                if (passwordVisible) {
                                    painterResource(R.drawable.visibility_off)
                                } else {
                                    painterResource(R.drawable.visibility)
                                }
                            } else {
                                /*
                                    This is safe because this button only appears if the
                                    (isPassword || sideButtonIcon != null) condition passes,
                                    and this "else" is only reached if "isPassword" is false,
                                    which means "sideButtonIcon" must be non-null
                                */
                                trailingIcon as Painter
                            },
                            tint = trailingIconColor,
                            contentDescription = if (isPassword) {
                                if (passwordVisible)
                                    "Hide Password"
                                else "Show Password"
                            } else trailingIconContentDescription ?: "Unknown",
                            modifier = Modifier.requiredSize(28.dp)
                        )
                    }
                }
            } else null,
            leadingIcon = if (leadingIcon != null) {
                {
                    IconButton(
                        modifier = Modifier.padding(end = 4.dp),
                        onClick = onLeadingIconPress
                    ) {
                        Icon(
                            painter = leadingIcon,
                            tint = leadingIconColor,
                            contentDescription = leadingIconContentDescription ?: "Unknown",
                            modifier = Modifier.requiredSize(28.dp)
                        )
                    }
                }
            } else null
        )

        if (description != null) {
            Text(
                text = description.toAnnotatedString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                ),
                modifier = Modifier.padding(
                    horizontal = 8.dp
                )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun TextInputPreview() {
    ZiplineTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = Color.Transparent
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    TextInput(
                        value = TextFieldValue(""),
                        onValueChange = { },
                        label = "Label",
                        isPassword = true,
                        placeholder = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    TextInput(
                        value = TextFieldValue(""),
                        onValueChange = { },
                        enabled = false,
                        label = "Label",
                        isPassword = true,
                        placeholder = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    TextInput(
                        value = TextFieldValue("aaa"),
                        onValueChange = { },
                        enabled = false,
                        label = "Label",
                        isPassword = true,
                        placeholder = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    TextInput(
                        value = TextFieldValue("aaa"),
                        onValueChange = { },
                        enabled = false,
                        label = "Label",
                        isPassword = false,
                        placeholder = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"
                    )
                }
            }

        }
    }
}