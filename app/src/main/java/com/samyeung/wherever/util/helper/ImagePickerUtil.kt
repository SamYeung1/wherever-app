package com.samyeung.wherever.util.helper

import android.app.Activity
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.samyeung.wherever.R

object ImagePickerUtil {
    fun openImagePicker(activity: Activity, title: String, single: Boolean = false, requestCode: Int? = null) {
        val picker = ImagePicker
            .create(activity)
            .showCamera(false)
            .folderMode(true)
            .theme(R.style.AppTheme_ImagePickerTheme)
            .toolbarImageTitle(title)
            .toolbarFolderTitle(title)
            .enableLog(false)

        if (single) {
            picker.returnMode(ReturnMode.GALLERY_ONLY)
            picker.single()
        } else {
            picker.multi()
        }
        if (requestCode != null) {
            picker.start(requestCode)
        } else {
            picker.start()
        }

    }
}