package com.stefdp.zipline.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.ui.theme.ZiplineTheme

@Composable
fun Popup(
    modifier: Modifier = Modifier,
    showPopup: Boolean,
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(
        dismissOnBackPress = true,
        dismissOnClickOutside = true,
        usePlatformDefaultWidth = false
    ),
    scrollable: Boolean = true,
    content: @Composable (ColumnScope.() -> Unit),
) {
    if (showPopup) {
        val windowInfo = LocalWindowInfo.current
        val density = LocalDensity.current

        val maxHeight = with(density) {
            (windowInfo.containerSize.height * 0.75f).toDp()
        }

        Dialog(
            onDismissRequest = onDismissRequest,
            properties = properties,
        ) {
            val scrollState = rememberScrollState()

            val mainModifier = modifier
                .fillMaxWidth(0.95f)
                .heightIn(
                    max = maxHeight,
                )
                .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))

            Card(
                modifier = if (scrollable) mainModifier.verticalScroll(scrollState) else mainModifier,
                shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(6.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun PopupPreview() {
    ZiplineTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                Popup(
                    showPopup = true,
                    onDismissRequest = {},
                ) {
                    Text("aaa")
                }
            }
        }
    }
}