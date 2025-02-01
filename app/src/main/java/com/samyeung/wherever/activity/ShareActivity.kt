package com.samyeung.wherever.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.samyeung.wherever.util.helper.ExifUtil
import com.samyeung.wherever.util.helper.StorageUtil

class ShareActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val action = intent.action
        val type = intent.type
        if (action == Intent.ACTION_SEND && type != null) {
            if (type.startsWith("image/")) {
                handleImage(intent)
            }
        }
    }


    private fun handleImage(intent: Intent) {
        val image = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        image?.let {
            ExifUtil.Reader(StorageUtil.getRealPathFromURI(applicationContext, it)).getGPSData()?.let {l->
                Log.d("Test", l.toString())
            }
        }
    }
}
