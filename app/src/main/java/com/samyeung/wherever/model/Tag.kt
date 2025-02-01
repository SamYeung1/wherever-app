package com.samyeung.wherever.model

import com.google.gson.annotations.SerializedName

data class Tag(@SerializedName("id") val id:String,@SerializedName("title")  val title:String)