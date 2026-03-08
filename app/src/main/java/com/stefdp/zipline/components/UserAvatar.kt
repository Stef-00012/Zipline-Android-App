package com.stefdp.zipline.components

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.LocalLoggedUser
import com.stefdp.zipline.LocalLoggedUserAvatar
import com.stefdp.zipline.R
import com.stefdp.zipline.screens.SettingsScreen
import com.stefdp.zipline.ui.theme.getButtonColors

@Composable
fun UserAvatar(
    enabled: Boolean,
    navController: NavHostController
) {
    val username = LocalLoggedUser.current?.username ?: "Unknown" //stringResource(R.string.unknown_username),
    val avatar = LocalLoggedUserAvatar.current

    Log.d("UserAvatar", "Username: $username, Avatar: $avatar")

    Button(
        onClick = {
            navController.navigate(SettingsScreen)
        },
        colors = getButtonColors().copy(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        enabled = enabled
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
                    .size(40.dp)
                    .clip(
                        RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                    ),
                alpha = if (enabled) 1f else 0.5f
            )
        } else {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(
                        RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                    )
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                    .padding(5.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.group),
                    contentDescription = "Default Avatar",
                    tint = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                )
            }
        }

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Text(
            text = username,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
        )
    }
}