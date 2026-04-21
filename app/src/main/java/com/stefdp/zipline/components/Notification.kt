package com.stefdp.zipline.components

import android.app.Dialog
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.Logger
import com.stefdp.zipline.ui.theme.ZiplineTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object Notification {
    private var currentDialog: Dialog? = null

    fun show(
        activity: FragmentActivity,
        duration: Long = 3000L,
        content: @Composable () -> Unit,
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            showInternal(activity, content, duration)
        }
    }

    private suspend fun showInternal(
        activity: FragmentActivity,
        content: @Composable () -> Unit,
        duration: Long
    ) {
        currentDialog?.dismiss()

        if (activity.isFinishing || activity.isDestroyed) return

        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

            addFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            )

            attributes.apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                y = (12 * activity.resources.displayMetrics.density).toInt()
                width = WindowManager.LayoutParams.WRAP_CONTENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
            }
        }

        val composeView = ComposeView(activity).apply {
            setViewTreeLifecycleOwner(activity)
            setViewTreeViewModelStoreOwner(activity)
            setViewTreeSavedStateRegistryOwner(activity)

            setContent {
                NotificationContent(content)
            }
        }

        dialog.setContentView(composeView)

        try {
            if (!activity.isFinishing && !activity.isDestroyed) {
                dialog.show()
                currentDialog = dialog
            }
        } catch (e: WindowManager.BadTokenException) {
            return Logger.error("Notification", "Failed to show notification", e)
        }

        delay(duration)

        if (currentDialog == dialog) {
            try {
                dialog.dismiss()
            } catch (_: Exception) {}
        }
    }

    @Composable
    private fun NotificationContent(content: @Composable () -> Unit) {
        ZiplineTheme {
            Surface(
                shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 4.dp,
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(
                            horizontal = 20.dp,
                            vertical = 12.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    content()
                }
            }
        }
    }
}