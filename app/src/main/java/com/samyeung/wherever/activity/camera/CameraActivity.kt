package com.samyeung.wherever.activity.camera

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.WindowManager
import com.samyeung.wherever.R
import com.samyeung.wherever.activity.BaseActivity
import com.samyeung.wherever.util.helper.*
import com.samyeung.wherever.model.Location
import com.samyeung.wherever.fragment.editor.EditorFragment
import com.samyeung.wherever.fragment.editor.page.base.DescriptionEditorFragment
import com.samyeung.wherever.fragment.editor.page.base.LocationEditorFragment
import com.samyeung.wherever.fragment.page.MessagePageFragment


class CameraActivity : BaseActivity(), MessagePageFragment.Listener, CameraFragment.Callback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (PermissionUtil.checkPermissionForCamera(this)) {
            setUpFragment(
                MessagePageFragment.newInstance(
                    resources.getString(R.string.title_permission_camera),
                    "",
                    resources.getString(R.string.enable),
                    R.drawable.ic_camera_alt_black_24dp
                ), savedInstanceState
            )
        } else {

            setUpFragment(CameraFragment(), savedInstanceState)


        }
        LocationUtil.LocationHolder.get().observe(this, Observer { location ->
            location?.let {
                (this.mainFragment as? CameraFragment)?.setLocation(it)
            }

        })
    }

    override fun onButtonClicked() {
        PermissionUtil.showRequestForCamera(this, 100)
    }

    override fun onPictureTaken(byteArray: ByteArray?, location: com.samyeung.wherever.model.Location?) {
        val file = StorageUtil.saveImage(applicationContext, byteArray!!, location)
        val intent = Intent()
        intent.putExtra(EXTRA_TYPE, TYPE_IMAGE)
        intent.putExtra(EXTRA_DATA_IMAGE, Uri.fromFile(file))
        if (location != null) {
            intent.putExtra(EXTRA_DATA_LOCATION, location)
        } else {
            intent.putExtra(EXTRA_DATA_LOCATION, LocationUtil.LocationHolder.get().value!!)
        }

        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 100) {
            if ((grantResults.all { it == PackageManager.PERMISSION_GRANTED })) {
                setUpFragment(CameraFragment(), null)
            }
        }
    }

    companion object {
        private val TYPE_IMAGE = "image"
        private val EXTRA_TYPE = "type"
        private val EXTRA_DATA_IMAGE = "EXTRA_DATA_IMAGE"
        private val EXTRA_DATA_LOCATION = "EXTRA_DATA_LOCATION"

        fun start(activity: Activity, requestCode: Int, bundle: Bundle? = null) {
            val intent = Intent(activity, CameraActivity::class.java)
            intent.putExtra(FRAGMENT_BUNDLE, bundle)
            ActivityCompat.startActivityForResult(activity, intent, requestCode, null)
        }

        fun handleData(activity: Activity, resultCode: Int, data: Intent?) {
            if (resultCode == Activity.RESULT_OK) {
                EditorFragment.start(
                    activity,
                    DescriptionEditorFragment.Data(data!!.extras.getParcelable<Uri>(EXTRA_DATA_IMAGE).path),
                    LocationEditorFragment.Data(
                        data!!.extras.getParcelable<Uri>(EXTRA_DATA_IMAGE).path,
                        data!!.extras.getParcelable<Location>(EXTRA_DATA_LOCATION)
                    )
                )
            }
        }
    }
}