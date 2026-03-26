package com.stefdp.zipline.components.header

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
import androidx.compose.material3.LocalContentColor
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
import com.stefdp.zipline.components.Avatar
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.network.models.UserRole
import com.stefdp.zipline.screens.SettingsScreen
import com.stefdp.zipline.ui.theme.getButtonColors

@Composable
fun UserAvatar(
    enabled: Boolean,
    navController: NavHostController
) {
    val user = LocalLoggedUser.current

    val username = user?.username ?: "Unknown" //stringResource(R.string.unknown_username),
    val avatar = LocalLoggedUserAvatar.current

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
        Avatar(
            avatar = avatar,
            enabled = enabled,
            isAdmin = user != null && user.role.level <= UserRole.ADMIN.level,
        )

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Text(
            text = username,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = if (enabled)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            ),
        )
    }
}