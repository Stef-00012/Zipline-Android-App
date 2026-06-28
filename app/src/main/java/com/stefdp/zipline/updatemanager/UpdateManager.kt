package com.stefdp.zipline.updatemanager

import android.os.Build
import androidx.fragment.app.FragmentActivity

interface BaseUpdateManager {
    fun getInstaller(): Installer

    suspend fun checkForUpdates(
        openStore: Boolean = false
    ): Boolean

    suspend fun update()
}

enum class Installer {
    APK,
    FDROID,
    PLAY_STORE
}

internal fun getInstaller(activity: FragmentActivity): Installer {
    val packageManager = activity.packageManager

    val installer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        packageManager.getInstallSourceInfo(activity.packageName).installingPackageName
    } else {
        @Suppress("DEPRECATION")
        packageManager.getInstallerPackageName(activity.packageName)
    }

    return when(installer) {
        "com.android.vending" -> Installer.PLAY_STORE
        "org.fdroid.fdroid" -> Installer.FDROID
        else -> Installer.APK
    }
}