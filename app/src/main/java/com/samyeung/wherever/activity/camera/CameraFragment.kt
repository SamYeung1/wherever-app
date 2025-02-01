package com.samyeung.wherever.activity.camera

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.Flash
import com.otaliastudios.cameraview.Gesture
import com.otaliastudios.cameraview.GestureAction
import com.samyeung.wherever.R
import com.samyeung.wherever.fragment.BaseFragment
import com.samyeung.wherever.model.Location
import kotlinx.android.synthetic.main.fragment_camera.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CameraFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CameraFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class CameraFragment : BaseFragment() {
    private var location:Location? = null
    private var cameraFlash: Int = 0
    private val FLASH = "flash"
    private lateinit var sharedPreferences: SharedPreferences
    var callback: Callback? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpNavigationBar(Color.BLACK)
        setUpFlash(savedInstanceState)
        arguments?.let {

        }
    }

    private fun setUpFlash(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            cameraFlash = savedInstanceState.getInt(FLASH)
        } else {
            cameraFlash = sharedPreferences.getInt(FLASH, 0)
        }

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        sharedPreferences = context!!.getSharedPreferences("CameraSetting", Context.MODE_PRIVATE)
        if (context is Callback) {
            this.callback = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (cameraFlash) {
            CameraMode.AUTO.id -> {
                camera.flash = Flash.AUTO
                btn_flash.setImageDrawable(ContextCompat.getDrawable(context!!, CameraMode.AUTO.icon))
            }
            CameraMode.FLASH.id -> {
                camera.flash = Flash.ON
                btn_flash.setImageDrawable(ContextCompat.getDrawable(context!!, CameraMode.FLASH.icon))
                cameraFlash = CameraMode.FLASH.id
            }
            CameraMode.NOFLASH.id -> {
                camera.flash = Flash.OFF
                btn_flash.setImageDrawable(ContextCompat.getDrawable(context!!, CameraMode.NOFLASH.icon))
            }
        }
        camera.setLifecycleOwner(this)
        camera.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER)
        camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(jpeg: ByteArray?) {
                callback?.onPictureTaken(jpeg, this@CameraFragment.location)
            }
        })

        btn_capture.setOnClickListener {
            camera.capturePicture()
        }
        btn_flash.setOnClickListener {
            when (camera.flash) {
                Flash.AUTO -> {
                    camera.flash = Flash.ON
                    btn_flash.setImageDrawable(ContextCompat.getDrawable(context!!, CameraMode.FLASH.icon))
                    cameraFlash = CameraMode.FLASH.id
                }
                Flash.ON -> {
                    camera.flash = Flash.OFF
                    btn_flash.setImageDrawable(ContextCompat.getDrawable(context!!, CameraMode.NOFLASH.icon))
                    cameraFlash = CameraMode.NOFLASH.id
                }
                Flash.OFF -> {
                    camera.flash = Flash.AUTO
                    btn_flash.setImageDrawable(ContextCompat.getDrawable(context!!, CameraMode.AUTO.icon))
                    cameraFlash = CameraMode.AUTO.id
                }
            }
        }


    }


    override fun onPause() {
        super.onPause()
        //Save Setting
        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putInt(FLASH, cameraFlash)
        sharedPreferencesEditor.apply()
    }
    fun setLocation(location: Location){
        this.location = location

    }
    interface Callback {
        fun onPictureTaken(byteArray: ByteArray?,location: Location?)
    }

    enum class CameraMode {
        AUTO(0, R.drawable.ic_flash_auto_white_24dp),
        FLASH(1, R.drawable.ic_flash_on_white_24dp),
        NOFLASH(2, R.drawable.ic_flash_off_white_24dp);

        val id: Int
        val icon: Int

        constructor(id: Int, icon: Int) {
            this.id = id
            this.icon = icon
        }
    }
}
