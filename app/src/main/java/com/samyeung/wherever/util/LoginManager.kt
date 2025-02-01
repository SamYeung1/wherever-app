package com.samyeung.wherever.util

import android.content.Context
import android.text.TextUtils
import com.samyeung.wherever.model.Token
import com.samyeung.wherever.fragment.page.ExploreTraceLiveData

class LoginManager {
    companion object {
        private var token: Token? = null
        private var lastLoginTime: Long = -1
        private fun getInstance(): Token? {
            return token
        }

        fun setUpServerTime(lastLoginTime: Long) {
            this.lastLoginTime = lastLoginTime
        }

        fun setUp(context: Context, token: Token):Boolean {
            val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
            this.token = token
            val edit = sharedPreferences.edit()
            edit.putString("refreshToken", token.refreshToken)
            edit.putString("token", token.token)
            return edit.commit()
        }

        fun getRefreshToken(context: Context): String? {
            val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
            return sharedPreferences.getString("refreshToken", "")
        }

        fun getToken(context: Context): String? {
            val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
            return "Bearer " + if (token != null) token!!.token else sharedPreferences.getString("token", null)
        }

        fun getTokenExpiryTime(): Long {
            return if (token != null) token!!.expiryDate else -1
        }


        fun isLogin(context: Context): Boolean {
            val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
            return !TextUtils.isEmpty(sharedPreferences.getString("refreshToken", ""))
        }

        fun getServerTime(): Long {
            return this.lastLoginTime
        }

        fun logout(context: Context) {
            val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
            val edit = sharedPreferences.edit()
            edit.putString("refreshToken", "")
            edit.putString("token", "")
            edit.apply()
            this.token = null
            ExploreTraceLiveData.clear()
        }
    }
}