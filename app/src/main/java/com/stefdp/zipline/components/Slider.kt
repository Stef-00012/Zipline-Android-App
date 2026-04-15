package com.stefdp.zipline.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider as NativeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.stefdp.zipline.ui.theme.ZiplineTheme
import com.stefdp.zipline.utils.toAnnotatedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Slider(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    label: CharSequence,
    onValueChangeFinished: (() -> Unit)? = null,
    enabled: Boolean = true,
    steps: Int = 0,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = label.toAnnotatedString(),
            fontWeight = FontWeight.Bold,
            color = if (enabled) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
        )

        NativeSlider(
            value = value,
            onValueChange = onValueChange,
            track = { sliderState ->
                SliderDefaults.Track(
                    sliderState = sliderState,
                    enabled = enabled,
                    drawStopIndicator = null,
                    colors = SliderDefaults.colors().copy(
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        disabledActiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        disabledInactiveTrackColor = MaterialTheme.colorScheme.onBackground.copy(0.3f),
                        inactiveTrackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    )
                )
            },
            enabled = enabled,
            modifier = modifier,
            steps = steps,
            valueRange = valueRange,
            onValueChangeFinished = onValueChangeFinished,
            colors = SliderDefaults.colors().copy(
                disabledThumbColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                thumbColor = MaterialTheme.colorScheme.primary,
            )
        )
    }
}

@Preview
@Composable
fun Preview() {
    ZiplineTheme() {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                Slider(
                    value = 0.1f,
                    onValueChange = { },
                    label = "Enabled",
                    enabled = true
                )

                Slider(
                    value = 0.1f,
                    onValueChange = { },
                    label = "Disabled",
                    enabled = false
                )
            }
        }
    }
}