package com.samyeung.wherever.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.view.ViewCompat
import com.samyeung.wherever.R
import com.samyeung.wherever.fragment.BaseFragment

open class BaseActivity : ApplicationActivity() {
    protected var mainFragment: BaseFragment? = null

    companion object {
        private val FRAGMENT = "fragment"
        val FRAGMENT_BUNDLE = "fragment_bundle"
        fun start(
            activity: Activity,
            fragment: BaseFragment,
            bundle: Bundle? = null,
            requestCode: Int? = null
        ) {
            val intent = Intent(activity, BaseActivity::class.java)
            intent.putExtra(FRAGMENT, fragment::class.java.name)
            intent.putExtra(FRAGMENT_BUNDLE, if (bundle == null) Bundle() else bundle)
            if (requestCode == null) {
                ActivityCompat.startActivity(activity, intent, null)
            } else {
                ActivityCompat.startActivityForResult(activity, intent, requestCode, null)
            }
        }

        fun startFromURL(activity: Activity, url: Uri, requestCode: Int? = null) {
            val intent = Intent(Intent.ACTION_VIEW, url)
            if (requestCode == null) {
                ActivityCompat.startActivity(activity, intent, null)
            } else {
                ActivityCompat.startActivityForResult(activity, intent, requestCode, null)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.getString(FRAGMENT)?.let {
                val mClass = Class.forName(it)
//                this.mainFragment = mClass.newInstance() as BaseFragment
//                this.mainFragment!!.arguments = extras.getBundle(FRAGMENT_BUNDLE)
//

                setUpFragment(mClass.newInstance() as BaseFragment, savedInstanceState)
            }

        } else {
            this.mainFragment = supportFragmentManager.findFragmentByTag(FRAGMENT) as BaseFragment
            supportFragmentManager.beginTransaction().replace(R.id.content, this.mainFragment!!).commit()
        }

    }

    protected fun setUpFragment(fragment: BaseFragment, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            this.mainFragment = fragment
            if (this.mainFragment!!.arguments == null) {
                this.mainFragment!!.arguments = intent.extras?.getBundle(FRAGMENT_BUNDLE)
            }
            val transaction = supportFragmentManager.beginTransaction()
            this.mainFragment!!.getSharedTransaction()?.let {
                it.toMappedTransaction().forEach { sharedElement ->
                    transaction.addSharedElement(
                        findViewById(sharedElement.id),
                        ViewCompat.getTransitionName(findViewById(sharedElement.id))!!
                    )
                }
            }
            transaction.replace(
                R.id.content, this.mainFragment!!,
                FRAGMENT
            ).commit()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        this.mainFragment!!.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if (this.mainFragment!!.onBackPressed()) {

        } else {
            super.onBackPressed()
        }
    }
}
