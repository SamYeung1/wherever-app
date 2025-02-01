package com.samyeung.wherever.util.base

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.samyeung.wherever.util.helper.LanguageManager


class App: Application() {
    private val TAG = "App"

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LanguageManager.setLocale(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LanguageManager.setLocale(this)
    }
}