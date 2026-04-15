package com.stefdp.zipline.utils

import android.content.Context
import android.net.Uri
import android.os.storage.StorageManager
import java.util.UUID

object StorageUtil {
    fun canFitFile(context: Context, uri: Uri, fileSize: Long): Boolean {
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager

        return try {
            val uuid = getUuidForUri(storageManager, uri)

            val allocatableBytes = storageManager.getAllocatableBytes(uuid)

            val buffer = 100L * 1024L * 1024L

            allocatableBytes > (fileSize + buffer)
        } catch (e: Exception) {
            true
        }
    }

    fun canFitInternalCache(context: Context, fileSize: Long): Boolean {
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        return try {
            val allocatableBytes = storageManager.getAllocatableBytes(StorageManager.UUID_DEFAULT)
            val buffer = 100L * 1024L * 1024L

            allocatableBytes > (fileSize + buffer)
        } catch (e: Exception) {
            true
        }
    }

    private fun getUuidForUri(storageManager: StorageManager, uri: Uri): UUID {
        val documentId = try {
            val pathSegments = uri.pathSegments

            if (pathSegments.size >= 2) pathSegments[1] else null
        } catch (e: Exception) {
            null
        } ?: return StorageManager.UUID_DEFAULT

        val volumeId = documentId.split(":")[0]

        return when {
            volumeId.equals("primary", ignoreCase = true) -> {
                StorageManager.UUID_DEFAULT
            }
            else -> {
                val volumes = storageManager.storageVolumes

                for (volume in volumes) {
                    val uuidString = volume.uuid

                    if (uuidString != null && uuidString.equals(volumeId, ignoreCase = true)) {
                        return try {
                            UUID.fromString(uuidString)
                        } catch (e: Exception) {
                            StorageManager.UUID_DEFAULT
                        }
                    }
                }
                StorageManager.UUID_DEFAULT
            }
        }
    }
}