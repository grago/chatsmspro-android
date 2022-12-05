package com.blkpos.chatsmspro.fragment

import android.app.ActionBar
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.FragmentResultListener
import androidx.navigation.fragment.findNavController
import com.blkpos.chatsmspro.NavGraphDirections
import com.blkpos.chatsmspro.R
import com.blkpos.chatsmspro.activity.MainActivity
import com.blkpos.chatsmspro.extension.localized
import com.blkpos.chatsmspro.helper.DialogHelper
import com.blkpos.chatsmspro.model.Carrier
import com.blkpos.chatsmspro.model.Country
import com.blkpos.chatsmspro.model.Plan
import com.blkpos.chatsmspro.model.User
import com.blkpos.chatsmspro.model.response.RestResponse
import com.blkpos.chatsmspro.utils.InputUtils
import kotlinx.android.synthetic.main.fragment_carriers.*
import kotlinx.android.synthetic.main.fragment_signup.*

class SignUpFragment : BaseFragment() {

    private var country: Country? = null
    private var carrier: Carrier? = null
    private var plan: Plan? = null

    var user: User? = null
    var isProfile = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            this.isProfile = SignUpFragmentArgs.fromBundle(requireArguments()).isProfile
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupUI(view)

        if(!isProfile)
            userStore.removeAccessToken()
        else {

            if(user == null) getUser()

        }


        super.onViewCreated(view, savedInstanceState)
    }

    override fun setupUI(view: View){

        super.setupUI(view)

        (requireActivity() as MainActivity).showSupportActionBar()

        InputUtils.setupTapOutside(baseLayout, requireActivity())

        firstNameEditText.hint = firstNameEditText.hint.toString().uppercase()
        lastNameEditText.hint = lastNameEditText.hint.toString().uppercase()
        emailAddressEditText.hint = emailAddressEditText.hint.toString().uppercase()
//        passwordEditText.hint = passwordEditText.hint.toString().uppercase()
        phoneNumberAddressEditText.hint = phoneNumberAddressEditText.hint.toString().uppercase()
        countryEditText.hint = countryEditText.hint.toString().uppercase()
        carrierEditText.hint = carrierEditText.hint.toString().uppercase()
        planEditText.hint = planEditText.hint.toString().uppercase()
        smsAvailableEditText.hint = smsAvailableEditText.hint.toString().uppercase()

        planButton.setOnClickListener(onPlanButtonClicked)
        carrierButton.setOnClickListener(onCarrierButtonClicked)
        countryButton.setOnClickListener(onCountryButtonClicked)

        signupButton.setOnClickListener(onSignUpButtonClicked)
        loginButton.setOnClickListener(onLoginButtonClicked)

        smsAvailableEditText.setOnEditorActionListener EditorAction@{ v, actionId, event ->

            if(actionId == EditorInfo.IME_ACTION_UNSPECIFIED && event.action == KeyEvent.ACTION_DOWN ) {
                doSignUp()
                return@EditorAction true
            }

            return@EditorAction false


        }

        // hide menu button
        (requireActivity() as MainActivity).showMenuButton(false)

        parentFragmentManager.setFragmentResultListener("country", viewLifecycleOwner, onCountriesFragmentResult)
        parentFragmentManager.setFragmentResultListener("carrier", viewLifecycleOwner, onCarriersFragmentResult)
        parentFragmentManager.setFragmentResultListener("plan", viewLifecycleOwner, onPlansFragmentResult)

        if(isProfile) {
            signupButton.text = getString(R.string.signup_save)
            loginButton.visibility = View.GONE


            val params  = signupButton.layoutParams as LinearLayout.LayoutParams
            params.weight = 2f
            params.width = ActionBar.LayoutParams.MATCH_PARENT
            signupButton.layoutParams = params

        }

    }

    private val onCountryButtonClicked = View.OnClickListener {



        findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToCountriesFragment())


    }

    private val onCarrierButtonClicked = View.OnClickListener {


        val country = country ?: return@OnClickListener

        findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToCarriersFragment(country))



    }

    private val onPlanButtonClicked = View.OnClickListener {


        val carrier = carrier ?: return@OnClickListener

        findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToPlansFragment(carrier))



    }


    private val onSignUpButtonClicked: View.OnClickListener = View.OnClickListener {


        doSignUp()



    }

    private val onLoginButtonClicked: View.OnClickListener = View.OnClickListener {


        findNavController().navigate(R.id.action_SignUpFragment_to_LoginFragment)



    }

    private fun doSignUp(){

        InputUtils.hideKeyboard(requireActivity())

        enableUI(false)

//        nestedScrollView.post { nestedScrollView.fullScroll(View.FOCUS_DOWN) }

        if (user == null)
            user = User()

//        user?.plainPassword = passwordEditText.text.toString()
        user?.firstName = firstNameEditText.text.toString()
        user?.lastName = lastNameEditText.text.toString()
        user?.email = emailAddressEditText.text.toString()
        user?.mobileNumber = phoneNumberAddressEditText.text.toString()
        user?.mobilePlan = plan
        user?.smsAmount = smsAvailableEditText.text.toString().toIntOrNull() ?: 0
        user?.dailySmsAmount = dailySmsAvailableEditText.text.toString().toIntOrNull() ?: 0
        user?.country = country
        user?.carrier = carrier

        if(isProfile)
            restApi.editProfile(user)?.process(responseHandler)
        else
            restApi.register(user)?.process(responseHandler)
    }


    private val responseHandler: (RestResponse?, Throwable?) -> Unit = responseHandler@{ restResponse, throwable ->

        if (restResponse != null){

            if ( restResponse.ok){

                if(isProfile){
                    User.currentUser = user
                    activity?.runOnUiThread {
                        findNavController().popBackStack()
                    }
                } else {


                    val mobileNumber = user?.mobileNumber ?: return@responseHandler
                    val country = user?.country ?: return@responseHandler

                    activity?.runOnUiThread {

                        findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToVerificationFragment(country, mobileNumber))

                    }



//                    val clientId = config.getProperties("Config.properties").getProperty("ClientId")
//                    val clientSecret =
//                        config.getProperties("Config.properties").getProperty("ClientSecret")
//
//                    if (userStore.isUserLoggedIn()) {
//
//                        User.currentUser = user
//
//                        //                    gotoHome()
//
//                    } else {
//
//                        restApi.login(
//                            "password",
//                            clientId,
//                            clientSecret,
//                            user?.email,
//                            user?.plainPassword
//                        )?.process { oauthResponse, throwable ->
//
//
//                            if (oauthResponse != null && oauthResponse.error == null && oauthResponse.accessToken != null && oauthResponse.refreshToken != null) {
//
//                                userStore.setAccessToken(
//                                    oauthResponse.accessToken,
//                                    oauthResponse.refreshToken,
//                                    oauthResponse.expiresIn
//                                )
//
//                                restApi.user()
//                                    ?.process { user, throwable ->
//
//                                        User.currentUser = user
//
//                                        requireActivity().runOnUiThread {
//                                            gotoHome()
//                                        }
//
//
//                                    }
//
//                            }
//
//                        }
//                    }

                }

            } else {

                val errors = restResponse.errors?.map {
                    it.message
                }?.fold("") { acc, s ->

                    acc + s?.localized(requireContext()) + "\n"

                }

                requireActivity().runOnUiThread {
                    DialogHelper.showDialog(
                        requireContext(),
                        R.string.signup,
                        "${getString(R.string.signup_please_verify_the_following)}\n\n$errors",
                        R.string.ok,
                        {
                            enableUI(true)
                        }

                    )
                }
            }


        } else {

            requireActivity().runOnUiThread {
                enableUI(true)
                Toast.makeText(
                    requireContext(),
                    throwable?.localizedMessage.toString(),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

        }


    }


    private fun enableUI(enable: Boolean = true){

        activity?.runOnUiThread{

            loadingProgressBar.visibility = if(enable) View.GONE else View.VISIBLE
            buttonsLayout.visibility = if(enable) View.VISIBLE else View.GONE

        }



    }

    private val onCountriesFragmentResult = FragmentResultListener OnFragmentResult@{ requestKey, result ->


        country = result.getParcelable("country")
        carrier = null

        countryEditText.setText(country?.name)
        carrierEditText.setText(null)


    }

    private val onCarriersFragmentResult = FragmentResultListener OnFragmentResult@{ requestKey, result ->

        carrier = result.getParcelable("carrier")

        carrierEditText.setText(carrier?.name)



    }

    private val onPlansFragmentResult = FragmentResultListener OnFragmentResult@{ requestKey, result ->

        plan = result.getParcelable("plan")

        planEditText.setText(plan?.name)



    }


    private fun gotoHome(){

        findNavController().navigate(R.id.action_SignUpFragment_to_HomeFragment)

    }

    private fun getUser(){

        enableUI(false)

        restApi.user()?.process { user, throwable ->

            if(user != null ){

                this.user = user

                requireActivity().runOnUiThread {

                    firstNameEditText.setText(user.firstName)
                    lastNameEditText.setText(user.lastName)
                    emailAddressEditText.setText(user.email)
                    phoneNumberAddressEditText.setText(user?.mobileNumber)
                    smsAvailableEditText.setText(user?.smsAmount.toString())
                    dailySmsAvailableEditText.setText(user?.dailySmsAmount.toString())
                    country = user?.country
                    fetchCountries(country) {
                        val it = it ?: return@fetchCountries
                        countryEditText.setText(it)
                        country?.name = it
                    }

                    carrier = user?.carrier
                    fetchCarriers(country, carrier) {
                        carrierEditText.setText(it)
                    }

                    plan = user?.mobilePlan
                    fetchPlans(carrier, plan) {
                        planEditText.setText(it)
                    }

//                    user?.email = emailAddressEditText.text.toString()
//                    user?.mobileNumber = phoneNumberAddressEditText.text.toString()
//                    user?.mobilePlan = mobilePlanEditText.text.toString()
//                    user?.smsAmount = smsAvailableEditText.text.toString().toIntOrNull() ?: 0
//                    user?.country = country
//                    user?.carrier = carrier

                    enableUI(true)

                }



            } else {

                findNavController().popBackStack()

            }

        }

    }

    private fun fetchCountries(country: Country?, closure: (String?) -> Unit ){

        restApi.countries().process { countries, throwable ->



            activity?.runOnUiThread {

                closure(countries?.firstOrNull {
                    it.id == country?.id
                }?.name)



            }






        }

    }


    private fun fetchCarriers(country: Country?, carrier: Carrier?, closure: (String?) -> Unit ){

        val countryId = country?.id ?: return
        restApi.carriers(countryId).process { carriers, throwable ->



            activity?.runOnUiThread {

                closure(carriers?.firstOrNull {
                    it.id == carrier?.id
                }?.name)



            }






        }

    }

    private fun fetchPlans(carrier: Carrier?, plan: Plan?, closure: (String?) -> Unit ){

        val carrierId = carrier?.id ?: return
        restApi.plans(carrierId).process { plans, throwable ->

            activity?.runOnUiThread {

                closure(plans?.firstOrNull { it.id == plan?.id }?.name)



            }






        }

    }

}