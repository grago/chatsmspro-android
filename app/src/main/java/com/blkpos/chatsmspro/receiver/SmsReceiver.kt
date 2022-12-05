package com.blkpos.chatsmspro.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import com.blkpos.chatsmspro.application.App
import com.blkpos.chatsmspro.config.Config
import com.blkpos.chatsmspro.di.Exclude
import com.blkpos.chatsmspro.di.NetModule
import com.blkpos.chatsmspro.event.UpdateStatsEvent
import com.blkpos.chatsmspro.network.RestApi
import com.blkpos.chatsmspro.network.SimpleCallAdapterFactory
import com.blkpos.chatsmspro.network.TokenAuthenticator
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import dagger.android.AndroidInjection
import dagger.android.DaggerBroadcastReceiver
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SmsReceiver : BroadcastReceiver() {

//    private var mLastTimeReceived = System.currentTimeMillis()

    override fun onReceive(context: Context?, intent: Intent?) {

//        val currentTimeMillis = System.currentTimeMillis()
//        if (currentTimeMillis - mLastTimeReceived > 200) {
//            mLastTimeReceived = currentTimeMillis

            val pdus: Array<*>
            val msgs: Array<SmsMessage?>
            var msgFrom: String?
            var msgText: String?
            val strBuilder = StringBuilder()
            intent?.extras?.let {
                try {
                    pdus = it.get("pdus") as Array<*>
                    msgs = arrayOfNulls(pdus.size)
                    for (i in msgs.indices) {
                        msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                        strBuilder.append(msgs[i]?.messageBody)
                    }

                    msgText = strBuilder.toString()
                    msgFrom = msgs[0]?.originatingAddress

                    if (!msgFrom.isNullOrBlank() && !msgText.isNullOrBlank()) {
                        //
                        // Do some thing here
                        //
                        val regex = Regex("S-([0-9a-zA-Z]{10})")
                        if ( regex.containsMatchIn(msgText!!) ){

                            val verificationCode = regex.find(msgText!!)?.value ?: return


                            val baseProperties: Properties = Config(context?.applicationContext!!).getProperties(
                                App.PROPERTIES_FILE_NAME
                            )

                            val interceptor = HttpLoggingInterceptor()
                            interceptor.level = HttpLoggingInterceptor.Level.BODY


                            val okHttpClient = OkHttpClient.Builder().callTimeout(10, TimeUnit.SECONDS).addInterceptor(interceptor).build()

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
                                .setDateFormat(NetModule.DATE_FORMAT)
                            val gson = gsonBuilder.create()

                            val retrofit = Retrofit.Builder()
                                .addConverterFactory(GsonConverterFactory.create(gson))
                                .baseUrl(baseProperties.getProperty("BaseUrl"))
                                .addCallAdapterFactory(SimpleCallAdapterFactory.create())
                                .client(okHttpClient)
                                .build()


                            val restApi = retrofit.create(RestApi::class.java)


                            restApi.smsCheck(verificationCode, msgText!!, msgFrom!!)?.process { restResponse, _ ->


                            }


                        }
                    }
                } catch (e: Exception) {
                }
            }
        }
//    }
}