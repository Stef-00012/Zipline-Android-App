package com.stefdp.zipline.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.R

private const val SIZE = 50

@Composable
fun Pager(
    onFirstPageClick: () -> Unit,
    onPreviousPageClick: () -> Unit,
    onCustomPageInput: (Long) -> Unit,
    onNextPageClick: () -> Unit,
    onLastPageClick: () -> Unit,
    currentPage: Long,
    totalPages: Long,
    enabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val firstPageButtonEnabled = enabled && currentPage > 1

        IconButton(
            onClick = onFirstPageClick,
            enabled = firstPageButtonEnabled,
            modifier = Modifier
                .size(SIZE.dp)
                .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                .background(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                )
                .border(
                    width = 2.dp,
                    color = if (firstPageButtonEnabled) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                ),
            shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.first_page),
                contentDescription = "Go to first page"
            )
        }

        Spacer(
            modifier = Modifier.width(5.dp)
        )

        val previousPageButtonEnabled = enabled && currentPage > 1

        IconButton(
            onClick = onPreviousPageClick,
            enabled = previousPageButtonEnabled,
            modifier = Modifier
                .size(SIZE.dp)
                .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                .background(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                )
                .border(
                    width = 2.dp,
                    color = if (previousPageButtonEnabled) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                ),
            shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.chevron_left),
                contentDescription = "Go to previous page"
            )
        }

        Spacer(
            modifier = Modifier.width(5.dp)
        )

        val customInputEnabled = enabled && totalPages > 1

        var pageValue by remember { mutableLongStateOf(currentPage) }
        var _pageValue by remember(currentPage) { mutableStateOf(TextFieldValue("$currentPage")) }

        LaunchedEffect(pageValue) {
            onCustomPageInput(pageValue)
        }

        val numberRegex = Regex("""^[0-9]+$""")

        val focusManager = LocalFocusManager.current

        fun onEnter() {
            if (numberRegex.matches(_pageValue.text)) {
                pageValue = _pageValue.text.toLong().coerceIn(1, totalPages)
            }
        }

        TextInput(
            value = _pageValue,
            onValueChange = {
                _pageValue = if (it.text.isEmpty()) it
                else if (!numberRegex.matches(it.text)) return@TextInput
                else if (it.text.toLong() > totalPages) TextFieldValue("$totalPages")
                else if (it.text.toLong() < 1) TextFieldValue("1")
                else it
            },
            keyboardActions = KeyboardActions(
                onDone = {
                    onEnter()
                    focusManager.clearFocus()
                },
            ),
            enabled = customInputEnabled,
            containerModifier = Modifier
                .weight(1f)
                .height(SIZE.dp),
            modifier = Modifier
                .onPreviewKeyEvent { keyEvent ->
                    if (keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyUp) {
                        onEnter()
                        focusManager.clearFocus()

                        true
                    } else {
                        false
                    }
                }
        )

        Spacer(
            modifier = Modifier.width(5.dp)
        )

        val nextPageButtonEnabled = enabled && currentPage < totalPages

        IconButton(
            onClick = onNextPageClick,
            enabled = nextPageButtonEnabled,
            modifier = Modifier
                .size(SIZE.dp)
                .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                .background(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                )
                .border(
                    width = 2.dp,
                    color = if (nextPageButtonEnabled) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                ),
            shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.chevron_right),
                contentDescription = "Go to next page"
            )
        }

        Spacer(
            modifier = Modifier.width(5.dp)
        )

        val lastPageButtonEnabled = enabled && currentPage < totalPages

        IconButton(
            onClick = onLastPageClick,
            enabled = lastPageButtonEnabled,
            modifier = Modifier
                .size(SIZE.dp)
                .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                .background(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                )
                .border(
                    width = 2.dp,
                    color = if (lastPageButtonEnabled) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                ),
            shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.last_page),
                contentDescription = "Go to last page"
            )
        }
    }
}