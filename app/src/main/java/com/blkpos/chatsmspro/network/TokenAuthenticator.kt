package com.blkpos.chatsmspro.network

import android.content.SharedPreferences
import com.blkpos.chatsmspro.model.response.OauthResponse
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class TokenAuthenticator(
    private val sharedPreferences: SharedPreferences,
    private val baseUrl: String,
    private val clientId: String,
    private val clientSecret: String
) :
    Authenticator {
    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {

        val accessToken = sharedPreferences.getString("accessToken", null)
        val expiresAt = sharedPreferences.getLong("expiresAt", 0)

        if (accessToken != null && expiresAt > System.currentTimeMillis() / 1000) {
            return response.request.newBuilder().header("Authorization", "Bearer $accessToken")
                .build()
        }
        val refreshToken = sharedPreferences.getString("refreshToken", null)
        val oauthResponse: OauthResponse? = refreshToken(refreshToken)
        return if (oauthResponse?.accessToken != null && oauthResponse.refreshToken != null) {
            sharedPreferences.edit().putString("accessToken", oauthResponse.accessToken)
                .putString("refreshToken", oauthResponse.refreshToken)
                .putLong(
                    "expiresAt",
                    System.currentTimeMillis() / 1000 + (oauthResponse.expiresIn ?: 0)
                )
                .apply()
            response.request.newBuilder()
                .header("Authorization", "Bearer " + oauthResponse.accessToken).build()
        } else throw IOException()
    }

    @Throws(IOException::class)
    private fun refreshToken(refreshToken: String?): OauthResponse? {
        val refreshToken = refreshToken ?: return null
        synchronized(refreshToken) {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val restApi = retrofit.create(RestApi::class.java)
            val refreshAction: Call<OauthResponse?>? =
                restApi.refresh("refresh_token", clientId, clientSecret, refreshToken)
            return refreshAction?.execute()?.body()
        }
    }

}
