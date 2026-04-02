package com.stefdp.zipline.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.LocalLoggedUser
import com.stefdp.zipline.LocalLoggedUserAvatar
import com.stefdp.zipline.network.models.UserRole
import com.stefdp.zipline.ui.theme.getButtonColors

@Composable
fun UserAvatar(
    enabled: Boolean = true,
    overrideAvatar: String? = null,
    onClick: () -> Unit = {}
) {
    val user = LocalLoggedUser.current

    val username = user?.username ?: "Unknown" //stringResource(R.string.unknown_username),
    val avatar = overrideAvatar ?: LocalLoggedUserAvatar.current

    Button(
        onClick = onClick,
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