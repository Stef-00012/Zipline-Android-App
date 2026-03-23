package com.stefdp.zipline.components

import android.content.Context
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
import com.stefdp.zipline.network.models.File
import com.stefdp.zipline.utils.SecureStorage
import com.stefdp.zipline.utils.shimmerable

val embeddableMimetypes = listOf(
    "image/webp",
    "image/apng",
    "image/png",
    "image/avif",
    "image/heic",
    "image/jpeg",
    "image/gif",
    "image/x-icon",
    "image/svg+xml"
)

val zipMimetypes = listOf(
    "application/zip",
    "application/x-zip-compressed",
    "application/x-zip",
    "application/octet-stream"
)

const val APK_MIMETYPE = "application/vnd.android.package-archive"

@Composable
fun FilePreview(
    file: File,
    context: Context,
    modifier: Modifier = Modifier,
    onClick: (File) -> Unit = {},
    clickEnabled: Boolean = true,
    previewVideos: Boolean = false,
    onImageLoaded: () -> Unit = { },
) {
    var imageLoading by remember { mutableStateOf(true) }
    var imageFailed by remember { mutableStateOf(false) }

    val isVideo = file.type.startsWith("video/")
    val isEmbeddable = file.type in embeddableMimetypes

    var serverUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val secureStore = SecureStorage.getInstance(context)

        serverUrl = secureStore.get("serverUrl")
    }

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

    val isShimmerEnabled = isVideo && previewVideos && imageLoading

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
            .then(modifier)
            .shimmerable(
                enabled = isShimmerEnabled,
                color = MaterialTheme.colorScheme.surfaceVariant,
                keepBackground = true
            )
            .clickable(
                enabled = clickEnabled && !isShimmerEnabled,
                onClick = { onClick(file) },
                role = Role.Image,
                onClickLabel = "Open large file display"
            ),
        contentAlignment = Alignment.Center
    ) {
        if (file.password == true) {
            imageLoading = false

            DefaultPreview(
                icon = painterResource(R.drawable.lock),
                iconContentDescription = "Password protected file",
                label = "Password protected file ${file.name}"
            )

            return@Box
        }

        if (isEmbeddable && serverUrl != null) {
            if (imageFailed) {
                DefaultPreview(
                    icon = painterResource(R.drawable.description),
                    iconContentDescription = "${file.type} file",
                    label = "Click to view file ${file.name}"
                )
            } else {
                if (imageLoading) {
                    DefaultPreview(
                        icon = painterResource(R.drawable.description),
                        iconContentDescription = "${file.type} file",
                        label = "Click to view file ${file.name}\nLoading preview..."
                    )
                }

                AsyncImage(
                    model = "$serverUrl/raw/${file.name}",
                    contentDescription = file.originalName ?: file.name,
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
            if (imageFailed || serverUrl == null) {
                DefaultPreview(
                    icon = painterResource(R.drawable.videocam),
                    iconContentDescription = "Video file",
                    label = "Click to play video ${file.name}"
                )
            } else if (previewVideos) {
                VideoPlayer(
                    context = context,
                    videoUrl = "$serverUrl/raw/${file.name}",
                    onError = {
                        imageFailed = true
                        imageLoading = false
                    },
                    onSuccess = {
                        imageLoading = false
                    }
                )
            } else {
                if (imageLoading) {
                    DefaultPreview(
                        icon = painterResource(R.drawable.videocam),
                        iconContentDescription = "Video file",
                        label = "Click to play video ${file.name}\nLoading preview..."
                    )
                }

                if (file.thumbnail?.path != null) {
                    AsyncImage(
                        model = "$serverUrl/raw/${file.thumbnail.path}",
                        contentDescription = file.originalName ?: file.name,
                        onSuccess = {
                            imageLoading = false
                        },
                        onError = {
                            imageFailed = true
                        }
                    )
                } else {
                    AsyncImage(
                        model = "$serverUrl/raw/${file.name}",
                        contentDescription = file.originalName ?: file.name,
                        onSuccess = {
                            imageLoading = false
                        },
                        onError = {
                            imageFailed = true
                        }
                    )
                }

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

        if (file.type == APK_MIMETYPE) {
            DefaultPreview(
                icon = painterResource(R.drawable.apk_document),
                iconContentDescription = "APK file",
                label = "Click to view file ${file.name}"
            )

            return@Box
        }

        if (file.type in zipMimetypes) {
            DefaultPreview(
                icon = painterResource(R.drawable.folder_zip),
                iconContentDescription = "Compressed file",
                label = "Click to view file ${file.name}"
            )

            return@Box
        }

        DefaultPreview(
            icon = painterResource(R.drawable.description),
            iconContentDescription = "${file.type} file",
            label = "Click to view file ${file.name}"
        )
    }
}