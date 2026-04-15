package com.stefdp.zipline.widgets.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.TextUnit
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.text.FontFamily
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

@Composable
fun Text(
    text: String,
    modifier: GlanceModifier = GlanceModifier,
    color: ColorProvider? = null,
    fontSize: TextUnit? = null,
    fontWeight: FontWeight? = null,
    fontStyle: FontStyle? = null,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null,
    fontFamily: FontFamily? = null,
    maxLines: Int = 1
) {
    Text(
        text = text,
        modifier = modifier,
        style = TextStyle(
            fontFamily = fontFamily ?: FontFamily.SansSerif,
            color = color ?: GlanceTheme.colors.onBackground,
            fontSize = fontSize,
            fontWeight = fontWeight,
            fontStyle = fontStyle,
            textAlign = textAlign,
            textDecoration = textDecoration
        ),
        maxLines = maxLines,
    )
}