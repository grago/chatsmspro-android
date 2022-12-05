package com.blkpos.chatsmspro.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.blkpos.chatsmspro.R
import kotlinx.android.synthetic.main.fragment_permissions.*

class PermissionsFragment: BaseFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_permissions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI(view)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun setupUI(view: View) {
        super.setupUI(view)

        gotoHomeButton.setOnClickListener {
            findNavController().navigate(PermissionsFragmentDirections.actionPermissionsFragmentToHomeFragment())

        }
    }

}