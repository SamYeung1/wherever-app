package com.samyeung.wherever.model

import com.google.gson.annotations.SerializedName
import io.reactivex.annotations.Nullable

data class ResponseResult<T>(
    @SerializedName("time") val time: Long,
    @SerializedName("code") val resultCode: Int,
    @SerializedName("message") val resultMessage: String,
    @SerializedName("result") val result: T
)