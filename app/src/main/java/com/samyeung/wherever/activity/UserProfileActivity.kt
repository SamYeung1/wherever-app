package com.samyeung.wherever.activity

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import com.samyeung.wherever.fragment.main.ProfileFragment

class UserProfileActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpFragment(ProfileFragment(),savedInstanceState)

    }
    companion object {
        private val URL: String = "wherever://view_profile"
        fun start(activity: Activity, userId: String, resultCode: Int? = null) {
            startFromURL(activity,
                parseURL(userId),resultCode)
        }

        fun parseURL(userId: String): Uri {
            return Uri.parse("$URL?user_id=${userId}")
        }
    }
}