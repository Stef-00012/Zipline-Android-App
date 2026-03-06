package com.stefdp.zipline.transferservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import kotlinx.coroutines.CompletableDeferred

object TransferServiceConnection {

    private var service: TransferService? = null
    private var isBound = false
    private var pendingDeferred: CompletableDeferred<TransferService>? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val transferBinder = binder as TransferService.TransferBinder
            service = transferBinder.getService()
            isBound = true
            pendingDeferred?.complete(service!!)
            pendingDeferred = null
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
            isBound = false
        }
    }

    suspend fun getService(context: Context): TransferService {
        service?.let { return it }

        val deferred = CompletableDeferred<TransferService>()
        pendingDeferred = deferred

        val intent = Intent(context, TransferService::class.java)
        context.startForegroundService(intent)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)

        return deferred.await()
    }

    fun unbind(context: Context) {
        if (isBound) {
            context.unbindService(connection)
            isBound = false
            service = null
        }
    }
}