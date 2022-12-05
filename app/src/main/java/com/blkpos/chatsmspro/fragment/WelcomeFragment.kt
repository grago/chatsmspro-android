package com.blkpos.chatsmspro.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.blkpos.chatsmspro.R
import com.blkpos.chatsmspro.activity.MainActivity
import com.blkpos.chatsmspro.helper.DialogHelper
import com.blkpos.chatsmspro.model.User

class WelcomeFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI(view)

        checkUserLoggedIn()

    }


    private fun checkUserLoggedIn() {

//        userStore.removeAccessToken()

        if (userStore.isUserLoggedIn()) {

            val userCall = restApi.user()

            userCall!!.process { user, _ ->

                requireActivity().runOnUiThread {
                    if (user != null) {

                        User.currentUser = user
                        gotoHome()


                    } else {

                        displayError()


                    }
                }

            }

        } else {

            displayError()
        }

    }

    private fun displayError(){

        if(!userStore.isUserLoggedIn()){
            gotoSignUp()
            return
        }

        requireActivity().runOnUiThread {
            DialogHelper.showDialog( requireContext(),  R.string.welcome_error_title,  getString(R.string.welcome_error),  R.string.welcome_retry,  {
                    checkUserLoggedIn()
                },  R.string.welcome_logout,  {
                    requireActivity().runOnUiThread {
                        gotoSignUp()
                    }

                })
        }
    }

    private fun gotoHome(){

        requireActivity().runOnUiThread {
            findNavController().navigate(R.id.action_WelcomeFragment_to_HomeFragment)
        }

    }

    private fun gotoSignUp(){


        requireActivity().runOnUiThread {
            findNavController().navigate(R.id.action_WelcomeFragment_to_SignUpFragment)
        }

    }



}
