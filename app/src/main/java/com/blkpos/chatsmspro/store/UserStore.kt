package com.blkpos.chatsmspro.store

import android.content.SharedPreferences

public class UserStore(private val sharedPreferences: SharedPreferences) {


    fun removeAccessToken() {
        sharedPreferences.edit().remove("accessToken").remove("refreshToken")
            .remove("expiresAt").apply()
    }

    fun setAccessToken(
        accessToken: String?,
        refreshToken: String?,
        expiresIn: Long
    ) {
        sharedPreferences.edit().putString("accessToken", accessToken)
            .putString("refreshToken", refreshToken)
            .putLong("expiresAt", System.currentTimeMillis() / 1000 + expiresIn)
            .apply()
    }

    fun isUserLoggedIn(): Boolean {
        val accessToken = sharedPreferences.getString("accessToken", null)
        return accessToken != null
    }


}