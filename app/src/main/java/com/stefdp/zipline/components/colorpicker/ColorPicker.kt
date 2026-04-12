package com.stefdp.zipline.components.colorpicker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.ui.theme.getOutlinedTextFieldColors
import androidx.core.graphics.toColorInt
import com.stefdp.zipline.utils.toHex
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.utils.Saver
import com.stefdp.zipline.utils.colorHash
import com.stefdp.zipline.utils.drawCheckerboard
import com.stefdp.zipline.utils.toAnnotatedString

val ColorRegex = Regex("^#([A-Fa-f0-9]{6})$")
val AlphaColorRegex = Regex("^#([A-Fa-f0-9]{8})")
val GeneralHexRegex = Regex("^#([A-Fa-f0-9])*$")

@Composable
fun ColorPicker(
    modifier: Modifier = Modifier,
    containerModifier: Modifier = Modifier,
    color: Color,
    onColorChange: (Color) -> Unit,
    enabled: Boolean = true,
    label: CharSequence? = null,
    placeholder: CharSequence? = null,
    description: CharSequence? = null,
    colors: TextFieldColors = getOutlinedTextFieldColors(),
    colorSide: ColorSide = ColorSide.RIGHT,
    showAutomaticColorButton: Boolean = false,
    automaticColorText: String? = null,
    automaticColorDescription: String? = null,
    alpha: Boolean = false,
) {
    val colorRegex = if (alpha) AlphaColorRegex else ColorRegex
    val maxLength = if (alpha) 9 else 7

    var value by rememberSaveable(
        color,
        stateSaver = TextFieldValue.Saver
    ) {
        mutableStateOf(TextFieldValue(color.toHex(alpha)))
    }
    val originalValue by rememberSaveable(
        stateSaver = TextFieldValue.Saver
    ) {
        mutableStateOf(value)
    }
    var showPicker by rememberSaveable { mutableStateOf(false) }

    var currentColor by rememberSaveable(
        value,
        stateSaver = Color.Saver
    ) {
        mutableStateOf(
            if (colorRegex.matches(value.text)) {
                Color(value.text.toColorInt())
            } else {
                color
            }
        )
    }
    var pickerColor by rememberSaveable(
        stateSaver = Color.Saver
    ) {
        mutableStateOf(currentColor)
    }

    LaunchedEffect(value.text) {
        if (colorRegex.matches(value.text)) {
            val newColor = Color(value.text.toColorInt())

            currentColor = newColor
            pickerColor = newColor
        }
    }

    val focusManager = LocalFocusManager.current

    fun onDone() {
        if (!colorRegex.matches(value.text)) {
            value = originalValue
        }

        val newColor = Color(value.text.toColorInt())

        onColorChange(newColor)
    }

    @Composable
    fun SideButton(side: ColorSide) {
        if (colorSide == side) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .clickable(
                        enabled = enabled,
                        onClick = { showPicker = true }
                    )
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawCheckerboard(squareSize = 8f)
                }
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(pickerColor)
                        .clip(CircleShape)
                )
            }
        } else if (showAutomaticColorButton) {
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(
                        onClick = {
                            pickerColor = colorHash(automaticColorText ?: "")
                            value = TextFieldValue(pickerColor.toHex(alpha))
                            onColorChange(pickerColor)
                        }
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.format_paint),
                    contentDescription = automaticColorDescription,
                )
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = containerModifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                if (it.text.isBlank()) {
                    value = TextFieldValue("#${if (alpha) "00" else ""}000000")
                }

                if (!GeneralHexRegex.matches(it.text)) return@OutlinedTextField

                if (it.text.length <= maxLength && it.text.startsWith("#")) {
                    value = it
                }
            },
            textStyle = LocalTextStyle.current.copy(
                color = if (enabled)
                    MaterialTheme.colorScheme.onBackground
                else
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            ),
            singleLine = true,
            keyboardActions = KeyboardActions(
                onDone = { onDone() }
            ),
            label = if (label != null) {
                {
                    Text(
                        text = label.toString(),
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
                        text = placeholder.toString(),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            } else null,
            colors = colors,
            shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp),
            enabled = enabled,
            leadingIcon = if (colorSide == ColorSide.RIGHT && !showAutomaticColorButton)
                null
            else ({
                SideButton(ColorSide.LEFT)
            }),
            trailingIcon = if (colorSide == ColorSide.LEFT && !showAutomaticColorButton)
                null
            else ({
                SideButton(ColorSide.RIGHT)
            }),
            modifier = modifier
                .onPreviewKeyEvent { keyEvent ->
                    if (keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyUp) {
                        onDone()
                        focusManager.clearFocus()

                        true
                    } else {
                        false
                    }
                }
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

    Popup(
        showPopup = showPicker,
        onDismissRequest = { showPicker = false },
    ) {
        HsvColorPicker(
            initialColor = color,
            withAlpha = alpha,
            onColorSelected = { selectedColor ->
                currentColor = selectedColor
            },
            onDone = { finalColor ->
                onColorChange(finalColor)
                pickerColor = finalColor
                value = TextFieldValue(finalColor.toHex(alpha))
                showPicker = false
            }
        )
    }
}

enum class ColorSide {
    LEFT, RIGHT
}
