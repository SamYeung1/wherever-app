package com.samyeung.wherever.util.helper.palette

import android.content.Context
import android.support.v7.graphics.Palette
import android.graphics.Bitmap
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder


class PaletteBitmapTranscoder(context: Context,val bitmapPool:BitmapPool) : ResourceTranscoder<Bitmap, PaletteBitmap> {

    val id: String
        get() = PaletteBitmapTranscoder::class.java.name

    override fun transcode(toTranscode: Resource<Bitmap>, options: Options): Resource<PaletteBitmap>? {
        val bitmap = toTranscode.get()
        val palette = Palette.Builder(bitmap).generate()
        val result = PaletteBitmap(bitmap, palette)
        return PaletteBitmapResource(result, bitmapPool)
    }
}