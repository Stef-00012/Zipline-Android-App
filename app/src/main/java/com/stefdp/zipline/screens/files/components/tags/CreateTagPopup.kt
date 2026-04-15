package com.stefdp.zipline.screens.files.components.tags

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.components.colorpicker.ColorPicker
import com.stefdp.zipline.components.colorpicker.ColorSide
import com.stefdp.zipline.screens.files.FilesUiState
import com.stefdp.zipline.screens.files.FilesViewModel

@Composable
fun CreateTagPopup(
    context: Context,
    activity: FragmentActivity,
    showPopup: Boolean,
    onDismissRequest: () -> Unit,
    viewModel: FilesViewModel,
    state: FilesUiState
) {
    Popup(
        showPopup = showPopup,
        onDismissRequest = onDismissRequest,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Create new Tag",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
            )

            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(
                        onClick = {
                            onDismissRequest()
                        }
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.close),
                    contentDescription = "Close tags menu",
                )
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextInput(
            value = state.createTagName,
            onValueChange = {
                viewModel.setCreateTagName(it)
            },
            modifier = Modifier.fillMaxWidth(),
            label = "Name",
            placeholder = "Enter a name...",
            enabled = !state.tagsLoading
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        ColorPicker(
            color = state.createTagColor,
            onColorChange = {
                viewModel.setCreateTagColor(it)
            },
            label = "Color",
            showAutomaticColorButton = true,
            automaticColorText = state.createTagName.text,
            automaticColorDescription = "Choose a color based on the name",
            colorSide = ColorSide.LEFT,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.tagsLoading
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            enabled = !state.tagsLoading && state.createTagName.text.isNotBlank(),
            onClick = {
                viewModel.createTag(
                    context = context,
                    name = state.createTagName.text,
                    color = state.createTagColor,
                    onSuccess = {
                        Notification.show(
                            context = context,
                            activity = activity,
                        ) {
                            Text(
                                text = "Tag created successfully"
                            )
                        }
                    },
                    onError = { error ->
                        Notification.show(
                            context = context,
                            activity = activity,
                        ) {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.onError
                            )
                        }
                    },
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Create Tag")
        }
    }
}