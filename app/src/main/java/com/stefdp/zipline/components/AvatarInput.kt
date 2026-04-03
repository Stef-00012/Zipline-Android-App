package com.stefdp.zipline.components

import android.content.Context
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.R
import com.stefdp.zipline.ui.theme.getOutlinedTextFieldColors
import com.stefdp.zipline.utils.getFileInfo
import com.stefdp.zipline.utils.toAnnotatedString

const val MAX_AVATAR_FILE_SIZE = 10L * 1024L * 1024L // 5 MB

@Composable
fun AvatarInput(
    context: Context,
    activity: FragmentActivity,
    modifier: Modifier = Modifier,
    onAvatarChange: (avatar: String?) -> Unit,
    enabled: Boolean = true,
    label: CharSequence? = null,
    placeholder: CharSequence? = null,
    colors: TextFieldColors = getOutlinedTextFieldColors(!enabled),
    includeSideButton: Boolean = true,
) {
    var filename by remember { mutableStateOf(TextFieldValue("")) }
    var avatarBase64 by remember { mutableStateOf<String?>(null) }
    var mimetype by remember { mutableStateOf<String>("image/png") }

    LaunchedEffect(avatarBase64) {
        onAvatarChange(avatarBase64?.let { "data:${mimetype};base64,$it" })
    }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            getFileInfo(context, uri)?.let { (name, size, mimeType) ->
                mimetype = mimeType
                filename = TextFieldValue(name)

                if (size >= MAX_AVATAR_FILE_SIZE) {
                    Notification.show(
                        context = context,
                        activity = activity,
                        content = {
                            Text(
                                text = "File is too large: $name"
                            )
                        }
                    )

                    return@rememberLauncherForActivityResult
                }

                val contentResolver = context.contentResolver

                avatarBase64 = contentResolver.openInputStream(uri)?.use { inputStream ->
                    val bytes = inputStream.readBytes()

                    Base64.encodeToString(bytes, Base64.NO_WRAP)
                } ?: avatarBase64
            }
        } else {
            filename = TextFieldValue("")
            avatarBase64 = null
        }
    }

    OutlinedTextField(
        value = filename,
        onValueChange = {},
        enabled = false,
        readOnly = true,
        singleLine = true,
        colors = colors.copy(
            disabledTextColor = MaterialTheme.colorScheme.onBackground,
        ),
        shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                onClick = {
                    filePicker.launch(arrayOf("image/*"))
                }
            ),
        trailingIcon = if (includeSideButton) {
            {
                Icon(
                    painter = painterResource(R.drawable.hide_image),
                    contentDescription = "Delete selected avatar",
                    tint = if (filename.text.isNotBlank())
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    modifier = Modifier
                        .clickable(
                            enabled = filename.text.isNotBlank() && avatarBase64 != null,
                            onClick = {
                                filename = TextFieldValue("")
                                avatarBase64 = null
                            }
                        )
                )
            }
        } else null,
        label = if (label != null) {
            {
                Text(
                    text = label.toAnnotatedString(),
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
                    text = placeholder.toAnnotatedString(),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        } else null,
    )
}