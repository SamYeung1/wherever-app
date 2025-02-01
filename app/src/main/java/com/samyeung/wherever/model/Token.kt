package com.samyeung.wherever.model

import com.google.gson.annotations.SerializedName

data class Token(
    @SerializedName("expiry_date") val expiryDate: Long,
    @SerializedName("type") val type: String,
    @SerializedName("token") val token: String,
    @SerializedName("refreshToken") val refreshToken: String
)