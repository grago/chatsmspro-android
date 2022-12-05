package com.blkpos.chatsmspro.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.blkpos.chatsmspro.application.App
import com.blkpos.chatsmspro.config.Config
import com.blkpos.chatsmspro.network.RestApi
import com.blkpos.chatsmspro.store.UserStore
import javax.inject.Inject

open class BaseFragment: Fragment() {

    @Inject
    lateinit var restApi: RestApi

    @Inject
    lateinit var config: Config

    @Inject
    lateinit var userStore: UserStore


    override fun onCreate(savedInstanceState: Bundle?) {

        (activity?.applicationContext as App).appComponent.inject(this)

        super.onCreate(savedInstanceState)
    }


    open fun setupUI(view: View){


    }



}