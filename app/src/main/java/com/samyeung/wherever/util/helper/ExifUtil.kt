package com.samyeung.wherever.util.helper

import android.location.Location
import android.support.media.ExifInterface
import android.util.Log
import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.Metadata
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import com.drew.metadata.exif.GpsDirectory


object ExifUtil {
    class Reader(filePath: String) {
        private var exif: Metadata = ImageMetadataReader.readMetadata(File(filePath))
        fun getGPSData(): com.samyeung.wherever.model.Location? {
            for (directory in exif.getDirectories()) {
                for (tag in directory.getTags()) {
                    Log.d("Test","${directory.getName()},${tag.getTagName()}, ${tag.getDescription()}")
                }
                if (directory.hasErrors()) {
                    for (error in directory.getErrors()) {
                        System.err.format("ERROR: %s", error)
                    }
                }
            }
            val directory = exif.getFirstDirectoryOfType(GpsDirectory::class.java)

            return if (directory?.geoLocation != null) com.samyeung.wherever.model.Location(
                directory.geoLocation.latitude,
                directory.geoLocation.longitude
            ) else null
        }

    }

    class Builder(filePath: String) {
        private var exif: ExifInterface = ExifInterface(filePath)
        fun putData(tag: String, data: String): Builder {
            this.exif.setAttribute(tag, data)
            return this
        }

        fun putGpsData(lat: Double, lon: Double): Builder {
            putData(ExifInterface.TAG_GPS_LATITUDE, convertGpsToDMS(lat))
            putData(ExifInterface.TAG_GPS_LATITUDE_REF, if (lat < 0) "S" else "N")
            putData(ExifInterface.TAG_GPS_LONGITUDE, convertGpsToDMS(lon))
            putData(ExifInterface.TAG_GPS_LONGITUDE_REF, if (lon < 0) "W" else "E")
            return this
        }

        fun putDateTime(dateTime: Long): Builder {
            putData(ExifInterface.TAG_DATETIME, SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(Date(dateTime)))
            putData(ExifInterface.TAG_GPS_DATESTAMP, SimpleDateFormat("yyyy:MM:dd").format(Date(dateTime)))
            putData(ExifInterface.TAG_GPS_TIMESTAMP, SimpleDateFormat("HH:mm:ss").format(Date(dateTime)))
            return this
        }

        fun build(): ExifInterface {
            return this.exif
        }

        private fun convertGpsToDMS(latOrLon: Double): String {
            val degMinSec = Location.convert(latOrLon, Location.FORMAT_SECONDS).split(":")

            val degrees = degMinSec[0].toInt().absoluteValue
            val seconds = (degMinSec[2].toDouble() * 10000).roundToInt()
            return "$degrees/1,${degMinSec[1]}/1,$seconds/10000"
        }
    }
}