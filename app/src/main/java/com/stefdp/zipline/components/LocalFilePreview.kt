package com.stefdp.zipline.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.R
import com.stefdp.zipline.utils.FileUploadState
import com.stefdp.zipline.utils.shimmerable

@Composable
fun LocalFilePreview(
    fileState: FileUploadState,
    modifier: Modifier = Modifier,
    onClick: (FileUploadState) -> Unit = {},
    clickEnabled: Boolean = true,
    onImageLoaded: () -> Unit = { },
) {
    var imageLoading by remember { mutableStateOf(true) }
    var imageFailed by remember { mutableStateOf(false) }

    val fileName = fileState.file.displayName
    val fileType = fileState.file.type
    val fileUri = fileState.file.uri

    val isVideo = fileType.startsWith("video/")
    val isEmbeddable = fileType in embeddableMimetypes

    LaunchedEffect(imageLoading) {
        if (!imageLoading) {
            onImageLoaded()
        }
    }

    @Composable
    fun DefaultPreview(
        icon: Painter,
        iconContentDescription: String,
        label: String,
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = icon,
                contentDescription = iconContentDescription,
                modifier = Modifier.size(40.dp)
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = label,
                textAlign = TextAlign.Center
            )
        }
    }

    Box(
        modifier = Modifier
            .then(modifier)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(
                enabled = clickEnabled,
                onClick = { onClick(fileState) },
                role = Role.Image,
                onClickLabel = "Open large file display"
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isEmbeddable) {
            if (imageFailed) {
                DefaultPreview(
                    icon = painterResource(R.drawable.description),
                    iconContentDescription = "$fileType file",
                    label = fileName
                )
            } else {
                if (imageLoading) {
                    DefaultPreview(
                        icon = painterResource(R.drawable.description),
                        iconContentDescription = "$fileType file",
                        label = "${fileName}\nLoading preview..."
                    )
                }

                AsyncImage(
                    model = fileUri,
                    contentDescription = fileName,
                    onSuccess = {
                        imageLoading = false
                    },
                    onError = {
                        imageFailed = true
                    }
                )
            }

            return@Box
        }

        if (isVideo) {
            if (imageFailed) {
                DefaultPreview(
                    icon = painterResource(R.drawable.videocam),
                    iconContentDescription = "Video file",
                    label = fileName
                )
            }  else {
                if (imageLoading) {
                    DefaultPreview(
                        icon = painterResource(R.drawable.videocam),
                        iconContentDescription = "Video file",
                        label = "${fileName}\nLoading preview..."
                    )
                }

                AsyncImage(
                    model = fileUri,
                    contentDescription = fileName,
                    onSuccess = {
                        imageLoading = false
                    },
                    onError = {
                        imageFailed = true
                    }
                )

                if (!imageLoading) {
                    Icon(
                        painter = painterResource(R.drawable.play_arrow),
                        contentDescription = "Play video icon",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            return@Box
        }

        imageLoading = false

        if (fileType == APK_MIMETYPE) {
            DefaultPreview(
                icon = painterResource(R.drawable.apk_document),
                iconContentDescription = "APK file",
                label = fileName
            )

            return@Box
        }

        if (fileType in zipMimetypes) {
            DefaultPreview(
                icon = painterResource(R.drawable.folder_zip),
                iconContentDescription = "Compressed file",
                label = fileName
            )

            return@Box
        }

        DefaultPreview(
            icon = painterResource(R.drawable.description),
            iconContentDescription = "${fileType} file",
            label = fileName
        )
    }
}