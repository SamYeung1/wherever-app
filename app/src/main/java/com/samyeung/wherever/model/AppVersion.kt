package com.samyeung.wherever.model

import com.google.gson.annotations.SerializedName

data class AppVersion(
    @SerializedName("is_latest_version") val isLatestVersion:Boolean,
    @SerializedName("is_force_update") val isForceUpdate:Boolean,
    @SerializedName("url") val url:String
)