package com.blkpos.chatsmspro.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blkpos.chatsmspro.R
import com.blkpos.chatsmspro.adapter.CountryAdapter
import com.blkpos.chatsmspro.model.Country
import kotlinx.android.synthetic.main.fragment_carriers.*

class CountriesFragment : BaseFragment() {

    private var countries = ArrayList<Country>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_countries, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupUI(view)

        fetchCountries()

        super.onViewCreated(view, savedInstanceState)
    }

    override fun setupUI(view: View){

        super.setupUI(view)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = CountryAdapter(
            countries,
            object: CountryAdapter.ViewHolder.IViewHolderClicks {
                override fun onClick(country: Country?) {

                    requireActivity().runOnUiThread {

                        val result = Bundle().apply {

                            putParcelable("country", country)

                        }
                        parentFragmentManager.setFragmentResult("country", result)

                        findNavController().popBackStack()



                    }

                }


            },
            requireContext()
        )


    }

    private fun fetchCountries(){

        countries.clear()

        restApi.countries().process { countries, throwable ->


            if(countries!=null) this.countries.addAll(countries)

            activity?.runOnUiThread {

                recyclerView.adapter?.notifyDataSetChanged()
                recyclerView.visibility = View.VISIBLE


            }






        }

    }

}