package com.blkpos.chatsmspro.network

import com.blkpos.chatsmspro.model.*
import com.blkpos.chatsmspro.model.response.OauthResponse
import com.blkpos.chatsmspro.model.response.RestResponse
import com.blkpos.chatsmspro.model.response.StatResponse
import retrofit2.Call
import retrofit2.http.*

interface RestApi {

    @POST("oauth/v2/token")
    @FormUrlEncoded
    @Headers("Cache-Control: no-cache")
    fun login(
        @Field("grant_type") grantType: String?,
        @Field("client_id") clientId: String?,
        @Field("client_secret") clientSecret: String?,
        @Field("username") username: String?,
        @Field("password") password: String?
    ): Simple<OauthResponse?>?
    //
    @POST("oauth/v2/token")
    @FormUrlEncoded
    @Headers("Cache-Control: no-cache")
    fun refresh(
        @Field("grant_type") grantType: String?,
        @Field("client_id") clientId: String?,
        @Field("client_secret") clientSecret: String?,
        @Field("refresh_token") refreshToken: String?
    ): Call<OauthResponse?>?
    //
    @GET("oauth/v2/token")
    @Headers("Cache-Control: no-cache")
    fun facebookLogin(
        @Query("grant_type") grantType: String?,
        @Query("client_id") clientId: String?,
        @Query("client_secret") clientSecret: String?,
        @Query("facebook_access_token") facebookAccessToken: String?
    ): Simple<OauthResponse?>?

    @GET("oauth/v2/token")
    @Headers("Cache-Control: no-cache")
    fun verificationCodeLogin(
        @Query("grant_type") grantType: String?,
        @Query("client_id") clientId: String?,
        @Query("client_secret") clientSecret: String?,
        @Query("verification_code") verificationCode: String
    ): Simple<OauthResponse?>?


    @GET("api/user/user.json")
    fun user(): Simple<User?>?

    @POST("api/registration.json")
    fun register(@Body user: User?): Simple<RestResponse?>

    @POST("api/user/user.json")
    fun editProfile(@Body user: User?): Simple<RestResponse?>?
//
//    @GET("api/reset_password.json")
//    fun resetPassword(@Query("username") username: String): Simple<RestResponse?>?
//

    @GET("api/countries.json")
    fun countries(): Simple<ArrayList<Country>?>

    @GET("api/carriers.json")
    fun carriers(@Query("country_id") countryId: Int): Simple<ArrayList<Carrier>?>

    @GET("api/plans.json")
    fun plans(@Query("carrier_id") carrierId: Int): Simple<ArrayList<Plan>?>

    @GET("api/user/stats.json")
    fun stats(): Simple<StatResponse?>

    @POST("api/user/register_token.json")
    fun registerToken(@Body deviceToken: DeviceToken?): Simple<RestResponse?>?

    @POST("api/user/unregister_token.json")
    fun unregisterToken(@Body deviceToken: DeviceToken?): Simple<RestResponse?>?

    @GET("api/sms_ack.json")
    fun smsAck(@Query("sms_id") smsId: String): Simple<RestResponse?>?

    @GET("api/sms_confirm.json")
    fun smsConfirm(@Query("sms_id") smsId: String): Simple<RestResponse?>?

    @GET("api/user/config.json")
    fun config(): Simple<Config?>?

    @POST("api/user/withdraw.json")
    fun withdraw(@Query("email") coins: String): Simple<RestResponse>

    @PUT("api/sms_check.json")
    fun smsCheck(@Query("verification_code") verificationCode: String, @Query("body") body: String, @Query("from") from: String): Simple<RestResponse>

    @GET("api/sms_error.json")
    fun smsError(@Query("sms_id") smsId: String, @Query("error") error: String): Simple<RestResponse?>?

    @GET("api/get_verification_code.json")
    fun getVerificationCode(@Query("method") method: String, @Query("country_id") countryId: Int, @Query("mobile_number") mobileNumber: String): Simple<RestResponse?>?

}