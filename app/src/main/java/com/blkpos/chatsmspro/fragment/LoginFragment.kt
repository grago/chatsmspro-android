package com.blkpos.chatsmspro.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentResultListener
import androidx.navigation.fragment.findNavController
import com.blkpos.chatsmspro.R
import com.blkpos.chatsmspro.activity.MainActivity
import com.blkpos.chatsmspro.model.Country
import com.blkpos.chatsmspro.utils.InputUtils
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseFragment() {

    private var country: Country? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI(view)
    }

    override fun setupUI(view: View){

        loginButton.setOnClickListener(onLoginButtonClicked)
        signupButton.setOnClickListener(onSignUpButtonClicked)

        /* hide keyboard on view tap */
        InputUtils.setupTapOutside(baseLayout, requireActivity())

        // hide menu button
        (requireActivity() as MainActivity).showMenuButton(false)

        countryButton.setOnClickListener(onCountryButtonClicked)

        parentFragmentManager.setFragmentResultListener("country", viewLifecycleOwner, onCountriesFragmentResult)

    }

    private val onCountryButtonClicked = View.OnClickListener {


        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToCountriesFragment())


    }

    private val onLoginButtonClicked: View.OnClickListener = View.OnClickListener {


        InputUtils.hideKeyboard(requireActivity())

        val country = country ?:  return@OnClickListener

        enableUI(false)

        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToVerificationFragment(country, mobileNumberEditText.text.toString()))

//        val clientId = config.getProperties("Config.properties").getProperty("ClientId")
//        val clientSecret = config.getProperties("Config.properties").getProperty("ClientSecret")
//
//        restApi.login("password", clientId, clientSecret, emailEditText.text.toString(), passwordEditText.text.toString())?.process { oauthResponse, throwable ->
//
//            requireActivity().runOnUiThread {
//
//                if (oauthResponse != null && oauthResponse.error == null && oauthResponse.accessToken != null && oauthResponse.refreshToken != null) {
//
//                    userStore.setAccessToken(
//                        oauthResponse.accessToken,
//                        oauthResponse.refreshToken,
//                        oauthResponse.expiresIn
//                    )
//
//                    restApi.user()?.process { user, throwable ->
//
//                        User.currentUser = user
//
//                        requireActivity().runOnUiThread {
//                            gotoHome()
//                        }
//
//
//                    }
//
//                } else {
//
//
//                    enableUI(true)
//                    Toast.makeText(requireContext(), R.string.login_login_error, Toast.LENGTH_SHORT)
//                        .show()
//
//                }
//
//            }
//
//        }

    }

    private val onSignUpButtonClicked: View.OnClickListener = View.OnClickListener {


        findNavController().navigate(R.id.action_LoginFragment_to_SignUpFragment)



    }


    private fun enableUI(enable: Boolean){

        buttonsLayout.visibility = if (enable) View.VISIBLE else View.GONE
        loadingProgressBar.visibility = if (enable) View.GONE else View.VISIBLE

    }


    private fun gotoHome(){


        findNavController().navigate(R.id.action_LoginFragment_to_HomeFragment)

    }

    private val onCountriesFragmentResult = FragmentResultListener OnFragmentResult@{ requestKey, result ->


        country = result.getParcelable("country")

        countryEditText.setText(country?.name)


    }

}