package com.stefdp.zipline.components

import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.R

@Composable
fun Avatar(
    avatar: String?,
    size: Dp = 40.dp,
    enabled: Boolean = true,
    isAdmin: Boolean = false
) {
    if (avatar != null) {
        val avatarBase64 = avatar.substringAfter("base64,")
        val imageBitmap = Base64
            .decode(avatarBase64, Base64.DEFAULT)
            .decodeToImageBitmap()

        Image(
            bitmap = imageBitmap,
            contentDescription = "User Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .clip(
                    RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                ),
            alpha = if (enabled) 1f else 0.5f
        )
    } else {
        Box(
            modifier = Modifier
                .size(size)
                .clip(
                    RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                )
                .background(
                    if (isAdmin) MaterialTheme.colorScheme.error.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant
                )
                .padding(5.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.group),
                contentDescription = "Default Avatar",
                tint = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = 0.5f
                ),
            )
        }
    }
}