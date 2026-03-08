package com.stefdp.zipline.components

import android.content.Context
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
    modifier: Modifier = Modifier
) {
    var imageLoading by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
            .then(modifier)
            .shimmerable(
                enabled = imageLoading,
                color = MaterialTheme.colorScheme.surfaceVariant,
                keepBackground = true
            ),
        contentAlignment = Alignment.Center
    ) {
        if (file.password == true) {
            LaunchedEffect(Unit) {
                imageLoading = false
            }

            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.lock),
                    contentDescription = "Password protected file",
                    modifier = Modifier.size(40.dp)
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = "Click to view protected ${file.name}",
                    textAlign = TextAlign.Center
                )
            }

            return@Box
        }

        var serverUrl by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            val secureStore = SecureStorage.getInstance(context)

            serverUrl = secureStore.get("serverUrl")
        }

        if (file.type in embeddableMimetypes && serverUrl != null) {
            AsyncImage(
                model = "$serverUrl/raw${file.url}",
                contentDescription = file.originalName ?: file.name,
                onSuccess = {
                    imageLoading = false
                },
            )

            return@Box
        }

        if (file.type.startsWith("video/")) {
            if (file.thumbnail?.path != null && serverUrl != null) {
                AsyncImage(
                    model = "$serverUrl/raw/${file.thumbnail.path}",
                    contentDescription = file.originalName ?: file.name,
                    onSuccess = {
                        imageLoading = false
                    },
                )

                Icon(
                    painter = painterResource(R.drawable.play_arrow),
                    contentDescription = "Play video icon",
                    modifier = Modifier.size(40.dp)
                )
            } else {
                LaunchedEffect(Unit) {
                    imageLoading = false
                }

                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.videocam),
                        contentDescription = "Video file",
                        modifier = Modifier.size(40.dp)
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = "Click to play video ${file.name}",
                        textAlign = TextAlign.Center
                    )
                }
            }

            return@Box
        }

        LaunchedEffect(Unit) {
            imageLoading = false
        }

        if (file.type == APK_MIMETYPE) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.apk_document),
                    contentDescription = "APK file",
                    modifier = Modifier.size(40.dp)
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = "Click to view file ${file.name}",
                    textAlign = TextAlign.Center
                )
            }

            return@Box
        }

        if (file.type in zipMimetypes) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.folder_zip),
                    contentDescription = "Compressed file",
                    modifier = Modifier.size(40.dp)
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = "Click to view file ${file.name}",
                    textAlign = TextAlign.Center
                )
            }

            return@Box
        }

        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.description),
                contentDescription = "${file.type} file",
                modifier = Modifier.size(40.dp)
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "Click to view file ${file.name}",
                textAlign = TextAlign.Center
            )
        }
    }
}