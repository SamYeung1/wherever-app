package com.samyeung.wherever.cst

import com.samyeung.wherever.BuildConfig


object APIMapping {
    val BASE_URL = BuildConfig.API_PATH
    val API_KEY = "8f5fa23d-67b5-4c75-9fea-d6f144e319e8"
    val CODE_NETWORK_ERROR = -2000

    val CODE_SUCCESS = 2000
    val CODE_AUTH_ERROR = 4001
    val CODE_AUTH_EXPIRED = 4002
    val CODE_ERROR_NOT_FOUND = 9003
    val CODE_EMAIL_USED = 1001
    val CODE_PERMISSION_ERROR = 4003
    val CODE_PASSWORD_FORMAT = 4004
    val CODE_EMAIL_VERIFICATION_ERROR = 4005
    val CODE_ACCOUNT_BLACK_LIST_ERROR = 4006
}