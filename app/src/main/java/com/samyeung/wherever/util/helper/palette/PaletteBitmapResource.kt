package com.samyeung.wherever.util.helper.palette

import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.util.Util


class PaletteBitmapResource(private val paletteBitmap: PaletteBitmap, private val bitmapPool: BitmapPool) :
    Resource<PaletteBitmap> {
    override fun getResourceClass(): Class<PaletteBitmap> {
        return PaletteBitmap::class.java
    }

    override fun get(): PaletteBitmap = paletteBitmap

    override fun getSize(): Int = Util.getBitmapByteSize(paletteBitmap.bitmap)
    override fun recycle() {
        try{
            bitmapPool.put(paletteBitmap.bitmap)
            paletteBitmap.bitmap.recycle()
        }catch (e:Exception){
            e.printStackTrace()
        }


    }
}