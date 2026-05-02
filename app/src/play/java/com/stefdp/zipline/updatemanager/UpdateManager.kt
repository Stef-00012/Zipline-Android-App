package com.stefdp.zipline.updatemanager

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.fragment.app.FragmentActivity
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.tasks.await

class UpdateManager(
    private val activity: FragmentActivity,
    private val context: Context,
    private val updateLauncher: ActivityResultLauncher<IntentSenderRequest>
) : BaseUpdateManager {
    override fun getInstaller(): Installer {
        return getInstaller(activity)
    }

    override suspend fun checkForUpdates(): Boolean {
        return try {
            val appUpdateManager = AppUpdateManagerFactory.create(context)
            val appUpdateInfo = appUpdateManager.appUpdateInfo.await()

            appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun update() {
        val appUpdateManager = AppUpdateManagerFactory.create(context)
        val appUpdateInfo = appUpdateManager.appUpdateInfo.await()

        if (appUpdateInfo.updateAvailability() != UpdateAvailability.UPDATE_AVAILABLE || !appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) return

        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            updateLauncher,
            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
        )
    }
}