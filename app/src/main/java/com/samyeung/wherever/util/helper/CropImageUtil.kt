package com.samyeung.wherever.util.helper

import android.app.Activity
import android.net.Uri
import com.theartofdev.edmodo.cropper.CropImage

object CropImageUtil {
    fun openCrop(activity: Activity, imageUri: Uri, requestCode: Int, isSquare: Boolean = false) {
        val cropImage = CropImage.activity(imageUri)
        if (isSquare) {
            //cropImage.setRequestedSize(500,500)
            cropImage.setAspectRatio(500, 500)
        }
        activity.startActivityForResult(cropImage.getIntent(activity), requestCode)
    }
}