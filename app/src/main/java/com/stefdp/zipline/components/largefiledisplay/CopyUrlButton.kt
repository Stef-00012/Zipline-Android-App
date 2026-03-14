package com.stefdp.zipline.components.largefiledisplay

import android.content.ClipData
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.R
import com.stefdp.zipline.ui.theme.DarkGray
import com.stefdp.zipline.ui.theme.White
import kotlinx.coroutines.launch

@Composable
internal fun CopyUrlButton(
    standardUrl: String,
    rawUrl: String,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboard.current

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopStart)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { expanded = true },
                enabled = enabled,
                color = DarkGray,
                iconColor = White,
                icon = painterResource(id = R.drawable.content_copy),
                iconContentDescription = "Copy file URL"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Copy URL"
                    )
                },
                onClick = {
                    coroutineScope.launch {
                        val clipData = ClipData.newRawUri("File URL", standardUrl.toUri()).toClipEntry()

                        clipboardManager.setClipEntry(clipData)

                        expanded = false
                    }
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.content_copy),
                        contentDescription = "Copy file URL"
                    )
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Copy Raw URL"
                    )
                },
                onClick = {
                    coroutineScope.launch {
                        val clipData = ClipData.newRawUri("File URL", rawUrl.toUri()).toClipEntry()

                        clipboardManager.setClipEntry(clipData)

                        expanded = false
                    }
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.copy_all),
                        contentDescription = "Copy raw file URL"
                    )
                }
            )
        }
    }
}