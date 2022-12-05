package com.blkpos.chatsmspro.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blkpos.chatsmspro.R
import com.blkpos.chatsmspro.adapter.PlanAdapter
import com.blkpos.chatsmspro.model.Carrier
import com.blkpos.chatsmspro.model.Country
import com.blkpos.chatsmspro.model.Plan
import kotlinx.android.synthetic.main.fragment_carriers.*
import kotlinx.android.synthetic.main.fragment_signup.*

class PlansFragment : BaseFragment() {

    private var plans = ArrayList<Plan>()
    lateinit var carrier: Carrier

    override fun onCreate(savedInstanceState: Bundle?) {

        if (arguments != null) {
            this.carrier =
                PlansFragmentArgs.fromBundle(requireArguments()).carrier
        }

        super.onCreate(savedInstanceState)




    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plans, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupUI(view)

        fetchPlans()

        super.onViewCreated(view, savedInstanceState)
    }

    override fun setupUI(view: View){

        super.setupUI(view)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = PlanAdapter(
            plans,
            object: PlanAdapter.ViewHolder.IViewHolderClicks {
                override fun onClick(plan: Plan?) {

                    requireActivity().runOnUiThread {

                        val result = Bundle().apply {

                            putParcelable("plan", plan)

                        }
                        parentFragmentManager.setFragmentResult("plan", result)

                        findNavController().popBackStack()



                    }

                }

            },
            requireContext()
        )


    }

    private fun fetchPlans(){

        plans.clear()

        restApi.plans(carrier.id).process { plans, throwable ->


            if(plans!=null) this.plans.addAll(plans)

            activity?.runOnUiThread {

                recyclerView.adapter?.notifyDataSetChanged()
                recyclerView.visibility = View.VISIBLE


            }






        }

    }

}