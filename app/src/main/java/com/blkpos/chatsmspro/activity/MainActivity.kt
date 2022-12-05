package com.blkpos.chatsmspro.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import com.blkpos.chatsmspro.BuildConfig
import com.blkpos.chatsmspro.R
import com.blkpos.chatsmspro.application.App
import com.blkpos.chatsmspro.config.Config
import com.blkpos.chatsmspro.model.DeviceToken
import com.blkpos.chatsmspro.network.RestApi
import com.blkpos.chatsmspro.service.ForegroundService
import com.blkpos.chatsmspro.store.UserStore
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var restApi: RestApi

    @Inject
    lateinit var userStore: UserStore

    @Inject
    lateinit var config: Config

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        supportActionBar?.hide()

        (applicationContext as App).appComponent.inject(this)


        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        logoutButton.setOnClickListener {

//            logoutButton.isEnabled = false

            drawerLayout.closeDrawer(GravityCompat.END)

            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                val token = task.result
                val deviceId = Settings.Secure.getString(
                    contentResolver,
                    Settings.Secure.ANDROID_ID
                )
                if (token == null) {
                    return@OnCompleteListener
                }

                val deviceToken = DeviceToken("android", deviceId, token, BuildConfig.VERSION_NAME)

                restApi.unregisterToken(deviceToken)
                    ?.process { _, _ ->

                        userStore.removeAccessToken()

                        runOnUiThread {

                            findNavController(R.id.nav_host_fragment).navigate(R.id.action_to_SignUpFragment)

                        }


                    }


            })



        }

        profileButton.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.END)
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_to_ProfileFragment)
        }

        // Get instance of Power Manager
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager

        // Android M (6) and up only
        // Check if the user has not already whitelisted your app from battery optimizations
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !powerManager.isIgnoringBatteryOptimizations(packageName)) {
            // Display an in-app dialog which will allow the user to exempt your app without leaving it
            startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:$packageName")))
        }

        Intent(this, ForegroundService::class.java).also { intent ->
            startService(intent)
        }

    }

    fun showSupportActionBar(){

        supportActionBar?.show()


    }

    fun showMenuButton(show: Boolean = true){

        menuButton.visibility = if(show) View.VISIBLE else View.GONE


    }

    override fun attachBaseContext(newBase: Context?) {

        if (newBase != null)
            super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
        else
            super.attachBaseContext(newBase);
    }

}