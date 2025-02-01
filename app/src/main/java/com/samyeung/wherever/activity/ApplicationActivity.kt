package com.samyeung.wherever.activity

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.samyeung.wherever.util.helper.LanguageManager


abstract class ApplicationActivity: AppCompatActivity() {
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LanguageManager.setLocale(newBase!!))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}