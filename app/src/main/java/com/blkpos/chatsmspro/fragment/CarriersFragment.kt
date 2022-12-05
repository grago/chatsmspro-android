package com.blkpos.chatsmspro.fragment

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blkpos.chatsmspro.R
import com.blkpos.chatsmspro.adapter.CarrierAdapter
import com.blkpos.chatsmspro.model.Carrier
import com.blkpos.chatsmspro.model.Country
import com.blkpos.chatsmspro.utils.InputUtils
import kotlinx.android.synthetic.main.fragment_carriers.*
import kotlinx.android.synthetic.main.fragment_signup.*

class CarriersFragment : BaseFragment() {

    private var carriers = ArrayList<Carrier>()
    lateinit var country: Country

    override fun onCreate(savedInstanceState: Bundle?) {

        if (arguments != null) {
            this.country =
                CarriersFragmentArgs.fromBundle(requireArguments()).country
        }

        super.onCreate(savedInstanceState)




    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_carriers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupUI(view)

        fetchCarriers()

        super.onViewCreated(view, savedInstanceState)
    }

    override fun setupUI(view: View){

        super.setupUI(view)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = CarrierAdapter(
            carriers,
            object: CarrierAdapter.ViewHolder.IViewHolderClicks {
                override fun onClick(carrier: Carrier?) {

                    requireActivity().runOnUiThread {

                        val result = Bundle().apply {

                            putParcelable("carrier", carrier)

                        }
                        parentFragmentManager.setFragmentResult("carrier", result)

                        findNavController().popBackStack()



                    }

                }

            },
            requireContext()
        )


    }

    private fun fetchCarriers(){

        carriers.clear()

        restApi.carriers(country.id).process { carriers, throwable ->


            if(carriers!=null) this.carriers.addAll(carriers)

            activity?.runOnUiThread {

                recyclerView.adapter?.notifyDataSetChanged()
                recyclerView.visibility = View.VISIBLE


            }






        }

    }

}