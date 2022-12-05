package com.blkpos.chatsmspro.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blkpos.chatsmspro.R
import com.blkpos.chatsmspro.model.Carrier

class CarrierAdapter(
    private val groups: ArrayList<Carrier>,
    private val listener: ViewHolder.IViewHolderClicks,
    val context: Context
) :
    RecyclerView.Adapter<CarrierAdapter.ViewHolder?>() {


    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        viewHolder.bind(groups[i], listener, context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cell_carrier, parent, false))


    }

    override fun getItemCount(): Int {
        return groups.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private var carrier: Carrier? = null
        private var listener: IViewHolderClicks? = null
        private var context: Context? = null
        private var descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        fun bind(
            carrier: Carrier,
            listener: IViewHolderClicks?,
            context: Context
        ) {
            this.carrier = carrier
            this.context = context
            this.listener = listener

            descriptionTextView.text = carrier.name

            descriptionTextView.setOnClickListener(View.OnClickListener {
                listener?.onClick(carrier)
            })

        }


        override fun onClick(view: View) {
            listener?.onClick(carrier)
        }

        interface IViewHolderClicks {
            fun onClick(pack: Carrier?)
        }

//        init {
//            itemView.setOnClickListener(this)
//        }
    }


}