package com.blkpos.chatsmspro.di

import android.content.BroadcastReceiver
import com.blkpos.chatsmspro.activity.MainActivity
import com.blkpos.chatsmspro.application.App
import com.blkpos.chatsmspro.fragment.BaseFragment
import com.blkpos.chatsmspro.service.CustomFirebaseMessagingService
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetModule::class, UserModule::class])
interface ApplicationComponent {
    fun inject(fragment: BaseFragment)
    fun inject(activity: MainActivity)
    fun inject(service: CustomFirebaseMessagingService)
    fun inject(receiver: BroadcastReceiver)

}