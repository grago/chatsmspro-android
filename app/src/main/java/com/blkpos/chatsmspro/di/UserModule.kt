package com.blkpos.chatsmspro.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.blkpos.chatsmspro.config.Config
import com.blkpos.chatsmspro.store.UserStore
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UserModule constructor(private val context: Context) {

    @Provides
    @Singleton
    fun provideUserStore(preferences: SharedPreferences): UserStore {

        return UserStore(preferences)
    }

    @Provides
    @Singleton
    fun provideConfig(): Config {

        return Config(context)
    }


}
