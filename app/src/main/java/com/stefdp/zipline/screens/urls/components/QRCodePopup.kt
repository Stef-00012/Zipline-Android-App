package com.stefdp.zipline.screens.urls.components

import android.content.ClipData
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.stefdp.zipline.R
import com.stefdp.zipline.components.Button
import com.stefdp.zipline.components.Popup
import com.stefdp.zipline.utils.getDisplayPath
import com.stefdp.zipline.utils.rememberQrBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun QRCodePopup(
    context: Context,
    qrCodeText: String,
    showPopup: Boolean,
    onDismissRequest: () -> Unit,
    downloadFileName: String,
) {
    if (qrCodeText.isBlank()) return

    val qrCode = rememberQrBitmap(
        content = qrCodeText,
        size = 200.dp,
        padding = 0.5.dp
    )

    Popup(
        showPopup = showPopup,
        onDismissRequest = onDismissRequest
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "QR Code",
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
                    contentDescription = "Close QR code menu",
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = BitmapPainter(qrCode.asImageBitmap()),
                contentDescription = "QR Code"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val coroutineScope = rememberCoroutineScope()
            val clipboardManager = LocalClipboard.current

            Button(
                onClick = {
                    coroutineScope.launch {
                        val file = File(context.cacheDir, "shared_qr.png")

                        file.outputStream().use { out ->
                            qrCode.compress(Bitmap.CompressFormat.PNG, 100, out)
                        }

                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

                        val clipData = ClipData.newUri(context.contentResolver, "QR Code", uri).toClipEntry()

                        clipboardManager.setClipEntry(clipData)

                        Toast.makeText(
                            context,
                            "QR code copied to clipboard",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.content_copy),
                    contentDescription = "Copy QR code image",
                )

                Spacer(
                    modifier = Modifier.width(4.dp)
                )

                Text("Copy Image")
            }

            Button(
                onClick = {
                    val values = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, downloadFileName)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }

                    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

                    uri?.let {
                        context.contentResolver.openOutputStream(it).use { out ->
                            if (out != null) {
                                qrCode.compress(Bitmap.CompressFormat.PNG, 100, out)

                                Toast.makeText(
                                    context,
                                    "QR code saved in Pictures/$downloadFileName",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.download),
                    contentDescription = "Download QR code image",
                )

                Spacer(
                    modifier = Modifier.width(4.dp)
                )

                Text("Download")
            }
        }
    }
}