package com.samyeung.wherever.backgroundservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class UploadTraceService : Service() {
    private var mBinder:LocalBinder = LocalBinder()
    inner class LocalBinder : Binder() {
        fun getService(): UploadTraceService = this@UploadTraceService
    }

    override fun onBind(intent: Intent): IBinder = mBinder
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    fun upload(){

    }
}
