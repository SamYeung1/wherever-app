package com.samyeung.wherever.model

import com.google.gson.annotations.SerializedName
import io.reactivex.annotations.Nullable

data class ResponsePagingResult<T>(
    @SerializedName("code") val resultCode: Int,
    @SerializedName("message") val resultMessage: String,
    @SerializedName("result") val result: PagingModel<T>
)
data class PagingModel<T>(@SerializedName("total") val total: Int,@SerializedName("total_page") val total_page: Int,@SerializedName("data") val data: T)