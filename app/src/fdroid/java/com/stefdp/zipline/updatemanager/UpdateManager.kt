package com.stefdp.zipline.updatemanager

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.BuildConfig
import com.stefdp.zipline.Logger

class UpdateManager(
    private val activity: FragmentActivity,
    private val context: Context,
    private val updateLauncher: ActivityResultLauncher<IntentSenderRequest>,
) : BaseUpdateManager {
    override fun getInstaller(): Installer {
        return getInstaller(activity)
    }

    override suspend fun checkForUpdates(
        openStore: Boolean
    ): Boolean {
        if (openStore) {
            val url = "market://details?id=${BuildConfig.APPLICATION_ID}"

            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            activity.startActivity(intent)
        }

        return false
    }

    override suspend fun update() {}
}