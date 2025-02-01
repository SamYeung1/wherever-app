package com.samyeung.wherever

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import com.samyeung.wherever.activity.ApplicationActivity
import com.samyeung.wherever.fragment.BaseFragment
import com.samyeung.wherever.util.helper.FragmentUtil
import com.samyeung.wherever.util.LoginManager
import com.samyeung.wherever.fragment.SplashFragment
import com.samyeung.wherever.fragment.auth.LoginFragment
import com.samyeung.wherever.fragment.main.PermissionFragment

class MainActivity : ApplicationActivity() ,SplashFragment.Callback{
    companion object {
        val FRG_TAG_MAIN = "main_fragment"
        fun start(activity: Activity){
            val intent = Intent(activity,MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            ActivityCompat.startActivity(activity,intent,null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadFragment(savedInstanceState)
    }

    private fun loadFragment(savedInstanceState: Bundle?) {
            setFragment(savedInstanceState, SplashFragment.newInstance())
    }

    private fun setFragment(savedInstanceState: Bundle?, fragment: BaseFragment) {
        FragmentUtil.setFragment(savedInstanceState, this, R.id.content, fragment, FRG_TAG_MAIN,allowingStateLoss = true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        FragmentUtil.getFragmentFromFragmentManagerByTag(this, FRG_TAG_MAIN)!!.onActivityResult(
            requestCode,
            resultCode,
            data
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        FragmentUtil.getFragmentFromFragmentManagerByTag(this, FRG_TAG_MAIN)!!.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
    }


    override fun onSuccess() {
        if (LoginManager.isLogin(applicationContext)) {
            setFragment(null, PermissionFragment.newInstance())
        }else{
            setFragment(null, LoginFragment.newInstance())
        }
    }
    override fun onError() {

    }
}
