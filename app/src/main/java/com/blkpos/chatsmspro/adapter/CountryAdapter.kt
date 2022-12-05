package com.blkpos.chatsmspro.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blkpos.chatsmspro.R
import com.blkpos.chatsmspro.model.Country

class CountryAdapter(
    private val countries: ArrayList<Country>,
    private val listener: ViewHolder.IViewHolderClicks,
    val context: Context
) :
    RecyclerView.Adapter<CountryAdapter.ViewHolder?>() {


    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        viewHolder.bind(countries[i], listener, context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cell_country, parent, false))


    }

    override fun getItemCount(): Int {
        return countries.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private var country: Country? = null
        private var listener: IViewHolderClicks? = null
        private var context: Context? = null
        private var descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        fun bind(
            country: Country,
            listener: IViewHolderClicks?,
            context: Context
        ) {
            this.country = country
            this.context = context
            this.listener = listener

            descriptionTextView.text = country.name
            descriptionTextView.setOnClickListener(View.OnClickListener {
                listener?.onClick(country)
            })



        }


        override fun onClick(view: View) {
            listener?.onClick(country)
        }

        interface IViewHolderClicks {
            fun onClick(country: Country?)
        }

//        init {
//            itemView.setOnClickListener(this)
//        }
    }


}