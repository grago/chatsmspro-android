package com.blkpos.chatsmspro.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.blkpos.chatsmspro.R
import com.blkpos.chatsmspro.model.Country
import com.blkpos.chatsmspro.model.User
import kotlinx.android.synthetic.main.fragment_verification.*

class VerificationFragment : BaseFragment() {

    private lateinit var phoneNumber: String
    private lateinit var country: Country

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            this.phoneNumber = VerificationFragmentArgs.fromBundle(requireArguments()).phoneNumber
            this.country = VerificationFragmentArgs.fromBundle(requireArguments()).country
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupUI(view)

        super.onViewCreated(view, savedInstanceState)
    }

    override fun setupUI(view: View){

        super.setupUI(view)

        subtitleTextView.text = getString(R.string.verification_subtitle, "+${country.code}${phoneNumber}")

        whatsappButton.setOnClickListener {
            sendVerificationCode("wpp")
        }

        smsButton.setOnClickListener {
            sendVerificationCode("sms")
        }


        code1EditText.addTextChangedListener {
            if(code1EditText.text.length == 1)
                code2EditText.requestFocus()
        }

        code2EditText.addTextChangedListener {
            if(code2EditText.text.length == 1)
                code3EditText.requestFocus()
        }

        code3EditText.addTextChangedListener {
            if(code3EditText.text.length == 1)
                code4EditText.requestFocus()
        }

        code4EditText.addTextChangedListener {
            if(code4EditText.text.length == 1)
                code5EditText.requestFocus()
        }

        code5EditText.addTextChangedListener {
            if(code5EditText.text.length == 1)
                code6EditText.requestFocus()

        }


        sendButton.setOnClickListener(onSendButtonClicked)

        resendButton.setOnClickListener {

            step2Layout.visibility = View.GONE
            step1Layout.visibility = View.VISIBLE
            enableUI(true)

        }

        code1EditText.requestFocus()

    }

    private fun enableUI(enable: Boolean){

        whatsappButton.isEnabled = enable
        smsButton.isEnabled = enable
        loadingProgressBar.visibility = if(enable) View.GONE else View.VISIBLE

    }

    private fun enableUI2(enable: Boolean){

        sendButton.visibility = if(enable) View.VISIBLE else View.GONE
        loading2ProgressBar.visibility = if(enable) View.GONE else View.VISIBLE

    }

    private fun sendVerificationCode(method: String){

        enableUI(false)

        restApi.getVerificationCode(method, country.id, phoneNumber)?.process { response, throwable ->

            activity?.runOnUiThread {
                enableUI(true)
            }

            if(response?.ok == true) {


                activity?.runOnUiThread {
                    code1EditText.text = null
                    code2EditText.text = null
                    code3EditText.text = null
                    code4EditText.text = null
                    code5EditText.text = null
                    code6EditText.text = null
                    step1Layout.visibility = View.GONE
                    step2Layout.visibility = View.VISIBLE
                }


            } else {


                activity?.runOnUiThread {
                    Toast.makeText(context, R.string.verification_subtitle, Toast.LENGTH_SHORT).show()
                }

            }




        }

    }

    private val onSendButtonClicked = View.OnClickListener{

        enableUI2(false)

        val clientId = config.getProperties("Config.properties").getProperty("ClientId")
        val clientSecret = config.getProperties("Config.properties").getProperty("ClientSecret")

        val verificationCode = "${code1EditText.text}${code2EditText.text}${code3EditText.text}${code4EditText.text}${code5EditText.text}${code6EditText.text}"

        restApi.verificationCodeLogin("https://api.smscashapp.com/verification_code_access_token", clientId, clientSecret, verificationCode)?.process { oauthResponse, throwable ->

            requireActivity().runOnUiThread {

                if (oauthResponse != null && oauthResponse.error == null && oauthResponse.accessToken != null && oauthResponse.refreshToken != null) {

                    userStore.setAccessToken(
                        oauthResponse.accessToken,
                        oauthResponse.refreshToken,
                        oauthResponse.expiresIn
                    )

                    restApi.user()?.process { user, throwable ->

                        User.currentUser = user

                        activity?.runOnUiThread {
                            gotoHome()
                        }


                    }

                } else {


                    activity?.runOnUiThread {

                        enableUI2(true)
                        Toast.makeText(requireContext(), R.string.verification_invalid, Toast.LENGTH_SHORT)
                            .show()

                    }

                }

            }

        }


    }

    private fun gotoHome(){


        findNavController().navigate(VerificationFragmentDirections.actionVerificationFragmentToHomeFragment())

    }


}