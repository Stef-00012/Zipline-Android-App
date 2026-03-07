package com.stefdp.zipline.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.ui.theme.ZiplineTheme
import com.stefdp.zipline.ui.theme.getSwitchColors
import com.stefdp.zipline.utils.toAnnotatedString

@Composable

fun Switch(
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
    enabled: Boolean = true,
    label: CharSequence,
    description: CharSequence? = null,
) {
    Row(
        modifier = modifier
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = getSwitchColors()
        )

        Column(
            modifier = Modifier.padding(start = 10.dp)
        ) {
            Text(
                text = label.toAnnotatedString(),
                fontWeight = FontWeight.Bold,
                color = if (enabled) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )

            if (description != null) {
                Text(
                    text = description.toAnnotatedString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (enabled) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL or Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun SwitchPreview() {
    ZiplineTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                Column() {
                    Switch(
                        checked = true,
                        onCheckedChange = {},
                        enabled = true,
                        label = "test label",
                        description = "a very long description to test how it goes on a new line hello hello hello hello hello hello hello"
                    )

                    Switch(
                        checked = false,
                        onCheckedChange = {},
                        enabled = true,
                        label = "test label",
                        description = "a very long description to test how it goes on a new line hello hello hello hello hello hello hello"
                    )

                    Switch(
                        checked = true,
                        onCheckedChange = {},
                        enabled = false,
                        label = "test label",
                        description = "a very long description to test how it goes on a new line hello hello hello hello hello hello hello"
                    )

                    Switch(
                        checked = false,
                        onCheckedChange = {},
                        enabled = false,
                        label = "test label",
                        description = "a very long description to test how it goes on a new line hello hello hello hello hello hello hello"
                    )
                }
            }

        }
    }
}