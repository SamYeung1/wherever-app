package com.samyeung.wherever.util.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.v7.graphics.Palette
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.*
import com.samyeung.wherever.util.helper.palette.PaletteBitmap
import java.io.ByteArrayOutputStream
import java.io.File

object ImageUtil {
    fun loadImage(
        context: Context,
        imageView: ImageView,
        url: String?,
        requestOptions: RequestOptions,
        listener: RequestListener<Drawable>? = null
    ) {
        Glide.with(imageView)
            .load(url)
            .thumbnail(0.5f)
            .apply(requestOptions)
            .addListener(listener)
            .into(DrawableImageViewTarget(imageView))
    }

    fun loadImageWithPalette(
        context: Context,
        imageView: ImageView,
        url: String?,
        requestOptions: RequestOptions,
        palette:(palette:Palette?)->Unit
    ) {
        Glide.with(imageView)
            .`as`(PaletteBitmap::class.java)
            .load(url)
            .thumbnail(0.5f)

            .apply(requestOptions)
            .into(object : ImageViewTarget<PaletteBitmap>(imageView) {
                override fun setResource(resource: PaletteBitmap?) {
                    resource?.let {
                        imageView.setImageBitmap(it.bitmap)
                        palette(it.palette)
                    }

                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    imageView.setImageDrawable(errorDrawable)
                    palette(null)
                }

            })
    }

    fun loadImage(context: Context, imageView: ImageView, @DrawableRes id: Int, requestOptions: RequestOptions) {
        Glide.with(imageView)
            .load(id)
            .thumbnail(0.5f)
            .apply(requestOptions)
            .into(DrawableImageViewTarget(imageView))
    }

    fun createCenterCropRequestOption(): RequestOptions {
        return RequestOptions
            .centerCropTransform()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    }

    fun createMakerIconRequestOption(): RequestOptions {
        return RequestOptions
            .centerCropTransform()
            .override(150)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    }

    fun createCircleCropRequestOption(): RequestOptions {
        return RequestOptions
            .circleCropTransform()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    }

    fun compress(context: Context,resource:Bitmap): File {
        val outputStream = ByteArrayOutputStream()
        resource.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val compressedFile = File(context.cacheDir, "${System.nanoTime().toString().format("%x")}.jpg")
        compressedFile.writeBytes(outputStream.toByteArray())
        return compressedFile
    }
}