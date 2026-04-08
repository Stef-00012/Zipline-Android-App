package com.stefdp.zipline.components

import android.R
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.ui.theme.ZiplineTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object Notification {
    private var currentDialog: Dialog? = null

    fun show(
        context: Context,
        activity: FragmentActivity,
        duration: Long = 3000L,
        content: @Composable () -> Unit,
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            showInternal(context, activity, content, duration)
        }
    }

    private suspend fun showInternal(context: Context, activity: FragmentActivity, content: @Composable () -> Unit, duration: Long) {
        currentDialog?.dismiss()

        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialog.window?.apply {
            setBackgroundDrawableResource(R.color.transparent)
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

            addFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            )

            attributes.apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                y = (12 * context.resources.displayMetrics.density).toInt()
                width = WindowManager.LayoutParams.WRAP_CONTENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
            }
        }

        val composeView = ComposeView(context).apply {
            setViewTreeLifecycleOwner(activity.window.decorView.findViewTreeLifecycleOwner())
            setViewTreeViewModelStoreOwner(activity.window.decorView.findViewTreeViewModelStoreOwner())
            setViewTreeSavedStateRegistryOwner(activity.window.decorView.findViewTreeSavedStateRegistryOwner())

            setContent {
                NotificationContent(content)
            }
        }

        dialog.setContentView(composeView)

        try {
            if (!activity.isFinishing) {
                dialog.show()

                currentDialog = dialog
            }
        } catch (e: Exception) { return }

        delay(duration)

        if (currentDialog == dialog) {
            dialog.dismiss()
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