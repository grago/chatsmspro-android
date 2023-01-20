package com.blkpos.chatsmspro.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.provider.Telephony
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blkpos.chatsmspro.BuildConfig
import com.blkpos.chatsmspro.R
import com.blkpos.chatsmspro.activity.MainActivity
import com.blkpos.chatsmspro.adapter.LedgerEntryAdapter
import com.blkpos.chatsmspro.adapter.StatAdapter
import com.blkpos.chatsmspro.event.UpdateStatsEvent
import com.blkpos.chatsmspro.model.*
import com.blkpos.chatsmspro.receiver.SmsReceiver
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.fragment_carriers.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.recyclerView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class HomeFragment : BaseFragment()  {
    private var stats = ArrayList<LedgerEntry>()
    private var appConfig: Config? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI(view)

        if(!isIgnoringBatteryOptimizations(requireContext())){
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPermissionsFragment())
        } else {

            fetchStats()
            fetchConfig()

            requestSMSPermission()
        }
    }

    override fun setupUI(view: View){

        (requireActivity() as MainActivity).showSupportActionBar()
        (requireActivity() as MainActivity).showMenuButton()

        titleTextView.text =  context?.getString(R.string.home_title, "${User.currentUser?.firstName} ${User.currentUser?.lastName}")

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = LedgerEntryAdapter(
            stats,
            requireContext()
        )

        messageButton.setOnClickListener { doShare() }
        shareButton.setOnClickListener { doShare() }
        recommendButton.setOnClickListener { doShare() }

        refreshButton.setOnClickListener(onRefreshButtonClicked)

        withdrawButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToWithdrawFragment(appConfig!!))
        }

    }

    private fun fetchStats(){

        stats.clear()

        refreshButton.visibility = View.GONE
        loadingProgressBar.visibility = View.VISIBLE

        sentCountTextView.text = null
        amountToEarnTextView.text = null

        restApi.ledgerEntries().process { statResponse, throwable ->


            if(statResponse!=null) {
                this.stats.addAll(statResponse.ledgerEntries)

                activity?.runOnUiThread {

                    recyclerView.adapter?.notifyDataSetChanged()
                    recyclerView.visibility = View.VISIBLE

                    refreshButton.visibility = View.VISIBLE
                    loadingProgressBar.visibility = View.GONE

                    sentCountTextView.text = statResponse.current.sentCount.toString()
                    amountToEarnTextView.text = statResponse.current.amountToEarn.toString()


                }

            }







        }



    }


    private fun requestSMSPermission(){
        val sendSmsPermission = ContextCompat.checkSelfPermission(requireActivity(),
            android.Manifest.permission.SEND_SMS)
        val receiveSmsPermission = ContextCompat.checkSelfPermission(requireActivity(),
            android.Manifest.permission.RECEIVE_SMS)
        val readSmsPermission = ContextCompat.checkSelfPermission(requireActivity(),
            android.Manifest.permission.READ_SMS)

        val modifyPhoneStatePermission = ContextCompat.checkSelfPermission(requireActivity(),
            android.Manifest.permission.MODIFY_PHONE_STATE)

        val permissions = ArrayList<String>()

        if (sendSmsPermission !=  PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.SEND_SMS)

        if (receiveSmsPermission !=  PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.RECEIVE_SMS)

        if (readSmsPermission !=  PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.READ_SMS)

        if (permissions.isNotEmpty()) {

//            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
//                    android.Manifest.permission.SEND_SMS)) {

//                DialogHelper.showDialog(requireContext(), R.string.home_title, getString(R.string.home_title), R.string.ok, {
//
//                }, R.string.ok  )

//            } else {

                requestPermissions(permissions.toTypedArray(), 999)

//            }
        } else {
            // Permission has already been granted

            val filter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
            activity?.registerReceiver(SmsReceiver(), filter)
            sendTokenToServer()
        }



    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 999) {

            for (i in permissions.indices) {
                val permission: String = permissions.get(i)
                val grantResult: Int = grantResults.get(i)
                if (permission == Manifest.permission.SEND_SMS) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        sendTokenToServer()
                    }
                }
            }
        }
    }


    private fun sendTokenToServer() {

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            val token = task.result
            val activity = activity ?: return@OnCompleteListener

            val deviceId = Settings.Secure.getString(
                activity.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            if (token == null) {
                return@OnCompleteListener
            }

            val deviceToken = DeviceToken("android", deviceId, token, BuildConfig.VERSION_NAME)

            restApi.registerToken(deviceToken)
                ?.process { _, _ ->


                }


        })

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


    private fun fetchConfig() {


        restApi.config()?.process { config, _ ->
            appConfig = config
        }

    }

    private fun doShare(){

        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        val shareBody = appConfig?.shareText ?: "Descarga SMSCa\$h https://www.smscash.com"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        context?.startActivity(Intent.createChooser(sharingIntent, "Compartir..."))
    }

    private fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val pwrm = context.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        val name = context.applicationContext.packageName
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return pwrm.isIgnoringBatteryOptimizations(name)
        }
        return true
    }

    private val onRefreshButtonClicked = View.OnClickListener {

        fetchStats()

    }

}