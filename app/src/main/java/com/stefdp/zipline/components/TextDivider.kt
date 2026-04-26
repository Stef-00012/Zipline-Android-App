package com.stefdp.zipline.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.ui.theme.Gray

@Preview
@Composable
fun TextDivider(
    modifier: Modifier = Modifier,
    text: String = "hello"
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f)
        )

        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp),
            color = Gray
        )

        HorizontalDivider(
            modifier = Modifier.weight(1f)
        )
    }
}