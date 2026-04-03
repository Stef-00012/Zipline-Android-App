package com.stefdp.zipline.screens.files.components

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Notification
import com.stefdp.zipline.network.models.IncompleteFile
import com.stefdp.zipline.network.models.IncompleteFileMetadata
import com.stefdp.zipline.network.models.IncompleteFileMetadataFile
import com.stefdp.zipline.network.models.IncompleteFileStatus
import com.stefdp.zipline.network.requests.deleteIncompleteFiles
import com.stefdp.zipline.ui.theme.DarkGray
import com.stefdp.zipline.ui.theme.DarkGreen
import com.stefdp.zipline.ui.theme.DarkRed
import com.stefdp.zipline.ui.theme.DarkYellow
import com.stefdp.zipline.ui.theme.Gray
import com.stefdp.zipline.ui.theme.LightGreen
import com.stefdp.zipline.ui.theme.LightRed
import com.stefdp.zipline.ui.theme.LightYellow
import com.stefdp.zipline.ui.theme.ZiplineTheme
import com.stefdp.zipline.ui.theme.getButtonColors
import kotlinx.coroutines.launch

@Composable
fun PendingFile(
    context: Context,
    activity: FragmentActivity,
    file: IncompleteFile,
    isLoading: Boolean,
    updateIsLoading: (Boolean) -> Unit,
    updateData: suspend () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = file.metadata.file.filename,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
            )

            FileStatus(status = file.status)
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = file.metadata.file.type,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                ),
            )

            Text(
                text = "${file.chunksComplete}/${file.chunksTotal} processed",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }

        Text(
            text = file.id,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        val coroutineScope = rememberCoroutineScope()

        Button(
            onClick = {
                coroutineScope.launch {
                    updateIsLoading(true)

                    val deleteIncompleteFileRes = deleteIncompleteFiles(
                        context = context,
                        ids = listOf(file.id)
                    )

                    deleteIncompleteFileRes
                        .onSuccess {
                            Notification.show(
                                context,
                                activity = activity,
                                content = {
                                    Text(
                                        text = "Pending File deleted"
                                    )
                                },
                            )

                            updateData()
                        }
                        .onFailure {
                            Notification.show(
                                context,
                                activity = activity,
                                content = {
                                    Text(
                                        text = "Failed to delete pending file"
                                    )
                                },
                            )
                        }

                    updateIsLoading(false)
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
            colors = getButtonColors().copy(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                disabledContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.delete),
                contentDescription = "Clear pending file",
            )

            Text(
                text = "Clear"
            )
        }
    }
}

@Composable
private fun FileStatus(status: IncompleteFileStatus) {
    val backgroundColor = when (status) {
        IncompleteFileStatus.PENDING -> Gray
        IncompleteFileStatus.PROCESSING -> LightYellow
        IncompleteFileStatus.COMPLETE -> LightGreen
        IncompleteFileStatus.FAILED -> LightRed
    }

    val color = when (status) {
        IncompleteFileStatus.PENDING -> DarkGray
        IncompleteFileStatus.PROCESSING -> DarkYellow
        IncompleteFileStatus.COMPLETE -> DarkGreen
        IncompleteFileStatus.FAILED -> DarkRed
    }


    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(
                vertical = 4.dp,
                horizontal = 8.dp
            )
    ) {
        Text(
            text = status.toString(),
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}