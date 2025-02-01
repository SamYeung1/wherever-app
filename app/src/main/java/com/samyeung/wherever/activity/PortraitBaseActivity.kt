package com.samyeung.wherever.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import com.samyeung.wherever.fragment.BaseFragment

open class PortraitBaseActivity: BaseActivity() {
    companion object {
        private val FRAGMENT = "fragment"
        private val FRAGMENT_BUNDLE = "fragment_bundle"
        fun start(activity: Activity, fragment: BaseFragment, bundle: Bundle? = null, requestCode: Int? = null){
            val intent = Intent(activity, PortraitBaseActivity::class.java)
            intent.putExtra(FRAGMENT,fragment::class.java.name)
            intent.putExtra(FRAGMENT_BUNDLE,if(bundle == null) Bundle() else bundle)
            if(requestCode == null){
                ActivityCompat.startActivity(activity,intent,null)
            }else{
                ActivityCompat.startActivityForResult(activity,intent,requestCode,null)
            }
        }
    }

}