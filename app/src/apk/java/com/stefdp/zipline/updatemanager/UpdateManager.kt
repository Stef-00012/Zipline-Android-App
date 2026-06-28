package com.stefdp.zipline.updatemanager

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.BuildConfig
import com.stefdp.zipline.network.requests.getLatestRelease
import io.github.z4kn4fein.semver.toVersionOrNull

class UpdateManager(
    private val activity: FragmentActivity,
    private val context: Context,
    private val updateLauncher: ActivityResultLauncher<IntentSenderRequest>,
) : BaseUpdateManager {
    companion object {
        const val RELEASE_FILE_NAME = "app-fdroid-release-signed.apk"
    }

    override fun getInstaller(): Installer {
        return getInstaller(activity)
    }

    override suspend fun checkForUpdates(
        openStore: Boolean
    ): Boolean {
        val latestReleaseRes = getLatestRelease(
            context = context,
            username = "Stef",
            repo = "Zipline-Android-App"
        )

        if (latestReleaseRes.isSuccess) {
            val currentVersion = BuildConfig.VERSION_NAME.toVersionOrNull() ?: return false

            val data = latestReleaseRes.getOrNull() ?: return false

            data.assets.find { it.name == RELEASE_FILE_NAME } ?: return false

            val releaseVersion = data.tagName.trimStart('v').toVersionOrNull() ?: return false

            return releaseVersion > currentVersion
        }

        return false
    }

    override suspend fun update() {
        val installer = getInstaller()

        if (installer != Installer.APK) {
            val url = "market://details?id=${BuildConfig.APPLICATION_ID}"

            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            activity.startActivity(intent)

            return
        } else {
            val latestReleaseRes = getLatestRelease(
                context = context,
                username = "Stef",
                repo = "Zipline-Android-App"
            )

            latestReleaseRes.onSuccess { data ->
                val asset = data.assets.find { it.name == RELEASE_FILE_NAME } ?: return@onSuccess

                val url = asset.browserDownloadUrl

                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                activity.startActivity(intent)
            }
        }
    }
}