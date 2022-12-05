package com.blkpos.chatsmspro.application

import android.app.Activity
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.blkpos.chatsmspro.config.Config
import com.blkpos.chatsmspro.di.ApplicationComponent
import com.blkpos.chatsmspro.di.DaggerApplicationComponent
import com.blkpos.chatsmspro.di.NetModule
import com.blkpos.chatsmspro.di.UserModule
import com.blkpos.chatsmspro.service.CustomFirebaseMessagingService
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import java.util.*


class App: Application(), Application.ActivityLifecycleCallbacks {

    companion object{
        const val PROPERTIES_FILE_NAME = "Config.properties"
        const val BASE_URL = "BaseUrl"
        const val BASE_URL_PICTURES = "ImagesUrl"
        const val CLIENT_ID = "ClientId"
        const val CLIENT_SECRET = "ClientSecret"
    }


    public lateinit var appComponent: ApplicationComponent

//    public lateinit var appComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/Lato-Regular.ttf")
//                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )
        val baseProperties: Properties = Config(applicationContext).getProperties(PROPERTIES_FILE_NAME)

        appComponent = DaggerApplicationComponent
            .builder()
            .userModule(UserModule(this))
            .netModule(NetModule(this,baseProperties.getProperty(BASE_URL), baseProperties.getProperty(CLIENT_ID) , baseProperties.getProperty(CLIENT_SECRET)))
            .build()


    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {

    }

    override fun onActivityStarted(p0: Activity) {

    }

    override fun onActivityResumed(p0: Activity) {

    }

    override fun onActivityPaused(p0: Activity) {

    }

    override fun onActivityStopped(p0: Activity) {

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }


    override fun onActivityDestroyed(p0: Activity) {
        val restartService = Intent(
            applicationContext,
            CustomFirebaseMessagingService::class.java
        )
        val pendingIntent = PendingIntent.getService(
            applicationContext,
            1,
            restartService,
            PendingIntent.FLAG_ONE_SHOT
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager[AlarmManager.ELAPSED_REALTIME, 5000] = pendingIntent
    }

}