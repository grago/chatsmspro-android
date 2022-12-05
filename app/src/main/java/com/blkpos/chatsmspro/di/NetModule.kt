package com.blkpos.chatsmspro.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.blkpos.chatsmspro.network.RestApi
import com.blkpos.chatsmspro.network.SimpleCallAdapterFactory
import com.blkpos.chatsmspro.network.TokenAuthenticator
import com.blkpos.chatsmspro.network.TokenInterceptor
import com.google.gson.*
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetModule constructor(
    private val context: Context,
    private val mBaseUrl: String,
    private val clientId: String,
    private val clientSecret: String

) {

    companion object {
        const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'O'"
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(): SharedPreferences {

        return PreferenceManager.getDefaultSharedPreferences(context)
    }


    @Provides
    @Singleton
    fun provideGson(): Gson {
        val gsonBuilder: GsonBuilder = GsonBuilder()
            .addSerializationExclusionStrategy(object : ExclusionStrategy {
                override fun shouldSkipField(f: FieldAttributes): Boolean {
                    return f.getAnnotation(Exclude::class.java) != null
                }

                override fun shouldSkipClass(clazz: Class<*>?): Boolean {
                    return false
                }
            })

            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setDateFormat(DATE_FORMAT)
        return gsonBuilder.create()
    }
//    @Provides
//    @NetworkLibraryScope
//    fun provideOkHttpCache(application: Application): Cache {
//        val cacheSize = 10 * 1024 * 1024 // 10 MiB
//        return Cache(application.cacheDir, cacheSize.toLong())
//    }

    @Provides
    @Singleton
    fun provideOkHttpClient(preferences: SharedPreferences): OkHttpClient {

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

//        val retryInterceptor = Interceptor { chain ->
//            val request: Request = chain.request()
//
//            // try the request
//
//            // try the request
//            var response = chain.proceed(request)
//
//            var tryCount = 0
//            while (!response.isSuccessful && tryCount < 3) {
//                tryCount++
//
//                // retry the request
//                response = chain.proceed(request)
//            }
//
//            // otherwise just pass the original response on
//
//            // otherwise just pass the original response on
//            response
//        }
//.callTimeout(10, TimeUnit.SECONDS)
//        .addInterceptor(retryInterceptor)
        return OkHttpClient.Builder().callTimeout(10, TimeUnit.SECONDS).addInterceptor(interceptor)
//            .addInterceptor(
//                TokenInterceptor(preferences)
//            )
            .authenticator(
            TokenAuthenticator(
                preferences,
                mBaseUrl,
                clientId,
                clientSecret
            )
        )

//        client.setCache(cache);


//
            .build()


    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(mBaseUrl)
            .addCallAdapterFactory(SimpleCallAdapterFactory.create())
            .client(okHttpClient)
            .build()

    }

    @Provides
    @Singleton
    fun provideRestApi(retrofit: Retrofit): RestApi {

        return retrofit.create(RestApi::class.java)

    }

}

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Exclude
