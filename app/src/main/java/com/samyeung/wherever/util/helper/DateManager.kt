package com.samyeung.wherever.util.helper

import android.content.Context
import android.text.format.DateUtils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by Sam Yeung on 6/17/2017.
 */

object DateManager {
     val TIME_BY_DEVICE = 0
    val DATE_BY_DEVICE = 1
    val toDay: Long
        get() {
            val date = Date()
            return date.time
        }
    fun createTimeDiff(context: Context,time:Long):String{
        val diff = toDay - time
        val diffS = (diff / 1000 %60).toLong()
        val diffM = (diff / (60 * 1000) % 60).toLong()
        val diffH = (diff / (60 * 60 * 1000) % 24).toLong()
        val diffD = (diff / (24 * 60 * 60 * 1000)).toLong()
        //return "${DateManager.createSimpleFormat(DateManager.DATE_BY_DEVICE, context).format(Date(time))} ${DateManager.createSimpleFormat(DateManager.TIME_BY_DEVICE, context).format(Date(time))}"
        //return "${diffS} ${diffM} ${diffH} ${diffD} \n${DateManager.createSimpleFormat(DateManager.DATE_BY_DEVICE, context).format(Date(time))} ${DateManager.createSimpleFormat(DateManager.TIME_BY_DEVICE, context).format(Date(time))}"
        return DateUtils.getRelativeTimeSpanString(time, toDay,DateUtils.SECOND_IN_MILLIS).toString()
    }

    fun createSimpleFormat(type:Int,context: Context): DateFormat {
        return when (type) {
            DATE_BY_DEVICE -> SimpleDateFormat("MMM d, YYYY")
            TIME_BY_DEVICE -> android.text.format.DateFormat.getTimeFormat(context)
            else -> DateFormat.getDateInstance()
        }
    }
    fun createTime(context: Context,date: Date):String{
        return "${createSimpleFormat(DATE_BY_DEVICE, context).format(date)} ${DateManager.createSimpleFormat(TIME_BY_DEVICE, context).format(date)}"
    }
    fun createTime(context: Context,date: Long):String{
        return "${createSimpleFormat(DATE_BY_DEVICE, context).format(date)} ${DateManager.createSimpleFormat(TIME_BY_DEVICE, context).format(date)}"
    }

}
