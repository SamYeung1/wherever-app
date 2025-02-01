package com.samyeung.wherever.util.helper

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import com.samyeung.wherever.model.Location
import java.io.File
import android.net.Uri
import java.io.FileOutputStream
import java.io.InputStream


object StorageUtil {
    fun saveImage(context: Context, data: ByteArray, location: Location? = null): File {
        val file = File(
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)}${File.separator}${System.nanoTime().toString().format(
                "%x"
            )}.jpg"
        )
        file.writeBytes(data)
        location?.let {
            ExifUtil.Builder(file.absolutePath)
                .putGpsData(it.lat, it.lng)
                .putDateTime(DateManager.toDay)
                .build()
                .saveAttributes()
        }
        //insertImage to content Provider
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DATA, file.absolutePath)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        return file
    }

    fun deleteImage(context: Context,path: String): Boolean {
        context.contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "= ?",
            arrayOf(path))
        return File(path).delete()
    }

    fun getRealPathFromURI(context: Context, uri: Uri): String {
        var input: InputStream? = null
        var path:String? = null
        if(uri.authority !=null){
            input = context.contentResolver.openInputStream(uri)
            val file = createTempFileFrom(context,input)
            path = file!!.absolutePath
        }
        return path!!
        /*
        var cursor: Cursor? = null
        try {
            val id = DocumentsContract.getDocumentId(uri).split(":")[1]
            val sel = MediaStore.Images.Media._ID + "=?"
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, sel, arrayOf(id), null)
            val columnIndex = cursor!!.getColumnIndexOrThrow(proj[0])
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        } finally {
            cursor?.close()
        }
        */
    }

    private fun createTempFileFrom(context: Context, inputStream: InputStream): File? {
        var tfile: File? = null
        try {
            var read: Int = 0
            val buffer = ByteArray(8 * 1024)
            tfile = File(context.externalCacheDir, "temp_${System.currentTimeMillis()}.jpg")
            val out = FileOutputStream(tfile)
            while ({ read = inputStream.read(buffer); read }() != -1) {
                out.write(buffer, 0, read)
            }
            out.flush()
        }catch (e:Exception){
            e.printStackTrace()
        }
        return tfile
    }
}