package com.blkpos.chatsmspro.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.blkpos.chatsmspro.R
import com.blkpos.chatsmspro.activity.MainActivity
import com.blkpos.chatsmspro.event.UpdateStatsEvent
import com.blkpos.chatsmspro.helper.DialogHelper
import com.blkpos.chatsmspro.model.Config
import com.blkpos.chatsmspro.model.User
import com.blkpos.chatsmspro.utils.InputUtils
import kotlinx.android.synthetic.main.fragment_withdraw.*
import kotlinx.android.synthetic.main.fragment_withdraw.amountToEarnTextView
import kotlinx.android.synthetic.main.fragment_withdraw.baseLayout
import kotlinx.android.synthetic.main.fragment_withdraw.buttonsLayout
import kotlinx.android.synthetic.main.fragment_withdraw.emailEditText
import kotlinx.android.synthetic.main.fragment_withdraw.loadingProgressBar
import kotlinx.android.synthetic.main.fragment_withdraw.sentCountTextView
import kotlinx.android.synthetic.main.fragment_withdraw.withdrawButton
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class WithdrawFragment : BaseFragment()  {

    lateinit var appConfig: Config

    override fun onCreate(savedInstanceState: Bundle?) {

        if (arguments != null) {
            this.appConfig =
                WithdrawFragmentArgs.fromBundle(requireArguments()).appConfig
        }

        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_withdraw, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI(view)

        fetchStats()
    }

    override fun setupUI(view: View){

        /* hide keyboard on view tap */
        InputUtils.setupTapOutside(baseLayout, requireActivity())

        (requireActivity() as MainActivity).showSupportActionBar()
        (requireActivity() as MainActivity).showMenuButton()

        subtitleTextView.text =  getString(R.string.withdraw_subtitle, appConfig.minimumWithdrawAmount)
        emailEditText.setText(User.currentUser?.lastWithdrawEmail ?: User.currentUser?.email)

        refreshButton.setOnClickListener(onRefreshButtonClicked)
        withdrawButton.setOnClickListener { doWithdraw() }

    }

    private fun fetchStats(){

        refreshButton.visibility = View.GONE
        loading2ProgressBar.visibility = View.VISIBLE

        sentCountTextView.text = null
        amountToEarnTextView.text = null

        restApi.stats().process { statResponse, throwable ->


            if(statResponse!=null) {

                activity?.runOnUiThread {

                    refreshButton.visibility = View.VISIBLE
                    loading2ProgressBar.visibility = View.GONE

                    sentCountTextView.text = statResponse.current.sentCount.toString()
                    amountToEarnTextView.text = statResponse.current.amountToEarn.toString()


                }

            }

        }



    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: UpdateStatsEvent?) {

        requireActivity().runOnUiThread {
            fetchStats()

        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this);
    }


    private fun doWithdraw(){

        enableUI(false)

        InputUtils.hideKeyboard(requireActivity())

        restApi.withdraw(emailEditText.text.toString())?.process { restResponse, throwable ->

            requireActivity().runOnUiThread {

                if ( throwable != null ) {
                    DialogHelper.showDialog(requireContext(), R.string.withdraw_title, throwable.localizedMessage, R.string.ok)
                    enableUI()
                } else {

                    if ( restResponse?.ok == true) {

                        DialogHelper.showDialog(requireContext(), R.string.withdraw_title, "Se ha enviado la solicitud de retiro. Nos pondremos en contacto a la brevedad.", R.string.ok, {
                            activity?.runOnUiThread {
                                findNavController().popBackStack()
                            }
                        })

                    } else {
                        DialogHelper.showDialog(requireContext(), R.string.withdraw_title, restResponse?.getErrorDescription() ?: "Se produjo un error al enviar la solicitud de retiro.", R.string.ok)
                        enableUI()
                    }
                }


            }


        }


    }

    private fun enableUI(enable: Boolean = true){

        buttonsLayout.visibility = if (enable) View.VISIBLE else View.GONE
        loadingProgressBar.visibility = if (enable) View.GONE else View.VISIBLE

    }

    private val onRefreshButtonClicked = View.OnClickListener {

        fetchStats()

    }


}