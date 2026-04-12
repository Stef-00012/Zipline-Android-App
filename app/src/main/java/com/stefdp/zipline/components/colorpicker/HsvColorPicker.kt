package com.stefdp.zipline.components.colorpicker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.ui.theme.DarkGray
import com.stefdp.zipline.utils.drawCheckerboard
import com.stefdp.zipline.utils.toHex

@Composable
fun HsvColorPicker(
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    onDone: (Color) -> Unit,
    withAlpha: Boolean = false
) {
    val initialHsv = remember {
        val hsv = FloatArray(3)

        android.graphics.Color.colorToHSV(initialColor.toArgb(), hsv)

        hsv
    }

    var hue by rememberSaveable { mutableFloatStateOf(initialHsv[0]) }
    var saturation by rememberSaveable { mutableFloatStateOf(initialHsv[1]) }
    var value by rememberSaveable { mutableFloatStateOf(initialHsv[2]) }
    var alpha by rememberSaveable { mutableFloatStateOf(initialColor.alpha) }

    val currentColor = remember(hue, saturation, value, alpha) {
        Color.hsv(hue, saturation, value, alpha)
    }

    var hexText by rememberSaveable(currentColor) { mutableStateOf(currentColor.toHex()) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 1.dp,
                    color = DarkGray,
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier.matchParentSize()
                ) {
                    drawCheckerboard(10f)
                }

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(initialColor)
                )

                Text(
                    text = initialColor.toHex(),
                    color = if (initialColor.luminance() > 0.5f) Color.Black else Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier.matchParentSize()
                ) {
                    drawCheckerboard(10f)
                }

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(currentColor)
                )

                Text(
                    text = hexText,
                    color = if (currentColor.luminance() > 0.5f && alpha > 0.5f) Color.Black else Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        change.consume()

                        saturation = (change.position.x / size.width).coerceIn(0f, 1f)
                        value = 1f - (change.position.y / size.height).coerceIn(0f, 1f)

                        onColorSelected(Color.hsv(hue, saturation, value, alpha))
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        saturation = (offset.x / size.width).coerceIn(0f, 1f)
                        value = 1f - (offset.y / size.height).coerceIn(0f, 1f)

                        onColorSelected(Color.hsv(hue, saturation, value, alpha))
                    }
                }
        ) {
            val hueColor = Color.hsv(hue, 1f, 1f)
            drawRect(
                color = hueColor
            )

            drawRect(
                brush = Brush.horizontalGradient(colors = listOf(Color.White, Color.Transparent))
            )

            drawRect(
                brush = Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black))
            )

            val pointerX = saturation * size.width
            val pointerY = (1f - value) * size.height

            drawCircle(
                color = Color.White,
                radius = 12.dp.toPx(),
                center = Offset(pointerX, pointerY),
                style = Stroke(width = 2.dp.toPx())
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        val rainbowColors = listOf(
            Color.Red, Color.Yellow, Color.Green, Color.Cyan,
            Color.Blue, Color.Magenta, Color.Red
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        change.consume()

                        hue = ((change.position.x / size.width) * 360f).coerceIn(0f, 360f)

                        onColorSelected(Color.hsv(hue, saturation, value, alpha))
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        hue = ((offset.x / size.width) * 360f).coerceIn(0f, 360f)

                        onColorSelected(Color.hsv(hue, saturation, value, alpha))
                    }
                }
        ) {
            drawRect(brush = Brush.horizontalGradient(colors = rainbowColors))

            val pointerX = (hue / 360f) * size.width

            drawCircle(
                color = Color.White,
                radius = 10.dp.toPx(),
                center = Offset(pointerX, size.height / 2),
                style = Stroke(width = 2.dp.toPx())
            )

            drawCircle(
                color = Color.hsv(hue, 1f, 1f),
                radius = 8.dp.toPx(),
                center = Offset(pointerX, size.height / 2)
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        if (withAlpha) {
            val solidColor = Color.hsv(hue, saturation, value, 1f)

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            change.consume()

                            alpha = (change.position.x / size.width).coerceIn(0f, 1f)

                            onColorSelected(Color.hsv(hue, saturation, value, alpha))
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            alpha = (offset.x / size.width).coerceIn(0f, 1f)

                            onColorSelected(Color.hsv(hue, saturation, value, alpha))
                        }
                    }
            ) {
                drawCheckerboard(squareSize = 12f)

                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, solidColor)
                    )
                )

                val pointerX = alpha * size.width

                drawCircle(
                    color = Color.White,
                    radius = 10.dp.toPx(),
                    center = Offset(pointerX, size.height / 2),
                    style = Stroke(width = 2.dp.toPx())
                )

                drawCircle(
                    color = currentColor,
                    radius = 8.dp.toPx(),
                    center = Offset(pointerX, size.height / 2)
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }

        Button(
            onClick = { onDone(currentColor) },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Done")
        }
    }
}