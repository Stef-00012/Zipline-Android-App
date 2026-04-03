package com.stefdp.zipline.screens.files.components.tags

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.Logger
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.components.TextInput
import com.stefdp.zipline.components.colorpicker.ColorPicker
import com.stefdp.zipline.components.colorpicker.ColorSide
import com.stefdp.zipline.network.requests.createTag
import com.stefdp.zipline.ui.theme.ZiplineTheme
import com.stefdp.zipline.utils.toHex
import kotlinx.coroutines.launch

@Composable
fun CreateTagPopup(
    context: Context,
    activity: FragmentActivity,
    showPopup: Boolean,
    onDismissRequest: () -> Unit,
    updateTags: suspend () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }

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

        var tagName by remember { mutableStateOf(TextFieldValue("")) }

        TextInput(
            value = tagName,
            onValueChange = { tagName = it },
            modifier = Modifier.fillMaxWidth(),
            label = "Name",
            placeholder = "Enter a name...",
            enabled = !isLoading
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        var tagColor by remember { mutableStateOf(Color.Black) }

        ColorPicker(
            color = tagColor,
            onColorChange = { tagColor = it },
            label = "Color",
            showAutomaticColorButton = true,
            automaticColorText = tagName.text,
            automaticColorDescription = "Choose a color based on the name",
            colorSide = ColorSide.LEFT,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        val coroutineScope = rememberCoroutineScope()

        Button(
            enabled = !isLoading,
            onClick = {
                coroutineScope.launch {
                    isLoading = true

                    val createTagRes = createTag(
                        context = context,
                        name = tagName.text,
                        color = tagColor.toHex()
                    )

                    createTagRes
                        .onSuccess {
                            Notification.show(
                                context,
                                activity = activity,
                                content = {
                                    Text(
                                        text = "Tag created successfully"
                                    )
                                }
                            )

                            updateTags()
                            onDismissRequest()
                        }
                        .onFailure {
                            Logger.error("CreateTagPopup", "Failed to create tag", it)

                            Notification.show(
                                context,
                                activity = activity,
                                content = {
                                    Text(
                                        text = "Failed to create tag"
                                    )
                                }
                            )
                        }

                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Create Tag")
        }
    }
}