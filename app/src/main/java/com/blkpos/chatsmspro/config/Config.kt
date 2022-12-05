package com.blkpos.chatsmspro.config

import android.content.Context
import android.util.Log
import java.io.IOException
import java.util.*

class Config(private val context: Context) {
    private val properties: Properties = Properties()
    fun getProperties(FileName: String?): Properties {
        try {
            /**
             * getAssets() Return an AssetManager instance for your
             * application's package. AssetManager Provides access to an
             * application's raw asset files;
             */
            val assetManager = context.assets
            /**
             * Open an asset using ACCESS_STREAMING mode. This
             */
            val inputStream = assetManager.open(FileName!!)
            /**
             * Loads properties from the specified InputStream,
             */
            properties.load(inputStream)
        } catch (e: IOException) { // TODO Auto-generated catch block
            Log.e("AssetsPropertyReader", e.toString())
        }
        return properties
    }


    fun baseUrl(): String{
        return getProperties("Config.properties").getProperty("BaseUrl")
    }

}