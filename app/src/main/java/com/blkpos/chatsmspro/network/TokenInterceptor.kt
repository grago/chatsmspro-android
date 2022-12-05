package com.blkpos.chatsmspro.network

import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(
    private val sharedPreferences: SharedPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        var accessToken = sharedPreferences.getString("accessToken", null)

        val expiresAt = sharedPreferences.getLong("expiresAt", 0)
        if (accessToken != null && expiresAt < System.currentTimeMillis() / 1000) {
            accessToken = null
        }


        return if (accessToken == null) {
            chain.proceed(chain.request())
        } else {
            val authenticatedRequest = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
            chain.proceed(authenticatedRequest)
        }
    }
}