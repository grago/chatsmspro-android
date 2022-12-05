package com.blkpos.chatsmspro.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.telephony.SmsManager
import android.util.Log
import com.blkpos.chatsmspro.application.App
import com.blkpos.chatsmspro.event.UpdateStatsEvent
import com.blkpos.chatsmspro.network.RestApi
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.greenrobot.eventbus.EventBus
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject


class CustomFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var restApi: RestApi

    override fun onCreate() {
        super.onCreate()
        (application as App).appComponent.inject(this)
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        sendRegistrationToServer(s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        showNotification(remoteMessage)
    }

    private fun sendRegistrationToServer(token: String) {

//        sharedPreferences.edit().putString("token", token).commit();
    }

    private fun showNotification(message: RemoteMessage) {
        val data = message.data

        val smsId = data["sms_id"]
        val destinationNumber = data["destination_number"]
        val content = data["content"]

        if(destinationNumber == null || content == null || smsId == null)
            return

        restApi.smsAck(smsId)?.process { restResponse, throwable ->

            if (restResponse?.ok == true) {

                sendSMS(destinationNumber, content, smsId)

            }

        }

    }

    private fun sendSMS(phoneNo: String, msg: String, smsId: String) {
        try {
            val smsManager: SmsManager = SmsManager.getDefault()

            smsManager.sendTextMessage(
                phoneNo, null, msg, PendingIntent.getBroadcast(
                    this, 0,
                    Intent("SMS_SENT"), PendingIntent.FLAG_IMMUTABLE
                ), null
            )

            restApi.smsConfirm(smsId)?.process { restResponse, _ ->

                    if(restResponse?.ok == true) {

                        EventBus.getDefault().post(UpdateStatsEvent())


                    }

                }


//            Handler(Looper.getMainLooper()).postDelayed({
//                deleteSMS(baseContext, msg, phoneNo)
//            }, 2000)
//


        } catch (ex: Exception) {
            ex.printStackTrace()

            restApi.smsError(smsId, ex.localizedMessage)?.process { _, _ ->



            }



        }
    }

    fun deleteSMS(context: Context, message: String, number: String) {
        try {
            Log.d("GGR", "Deleting SMS from inbox")
            val uriSms: Uri = Uri.parse("content://sms/sent")
            val c: Cursor? = context.contentResolver.query(
                uriSms, arrayOf(
                    "_id", "thread_id", "address",
                    "person", "date", "body"
                ), null, null, null
            )
            if (c != null && c.moveToFirst()) {
                do {
                    val id: Long = c.getLong(0)
                    val threadId: Long = c.getLong(1)
                    val address: String = c.getString(2)
                    val body: String = c.getString(5)
                    Log.d("GGR", body)
                    if (message == body) {
//                         && address == number
                        Log.d("GGR", "Deleting SMS with id: $threadId")
//                        context.contentResolver.delete(
//                            Uri.parse("content://sms/$id"), null, null
//                        )
                        context.contentResolver.delete(Uri.parse("content://sms/conversations/${threadId}"), null, null)
                    }
                } while (c.moveToNext())
            }
        } catch (e: java.lang.Exception) {
            Log.d("GGR", "Could not delete SMS from inbox: " + e.message)
        }
    }

    companion object {
        private val c =
            AtomicInteger(0)

        private val iD: Int
            private get() = c.incrementAndGet()
    }
}