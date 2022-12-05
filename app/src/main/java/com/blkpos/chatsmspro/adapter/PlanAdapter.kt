package com.blkpos.chatsmspro.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blkpos.chatsmspro.R
import com.blkpos.chatsmspro.model.Plan

class PlanAdapter(
    private val groups: ArrayList<Plan>,
    private val listener: ViewHolder.IViewHolderClicks,
    val context: Context
) :
    RecyclerView.Adapter<PlanAdapter.ViewHolder?>() {


    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        viewHolder.bind(groups[i], listener, context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cell_plan, parent, false))


    }

    override fun getItemCount(): Int {
        return groups.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private var plan: Plan? = null
        private var listener: IViewHolderClicks? = null
        private var context: Context? = null
        private var descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        fun bind(
            plan: Plan,
            listener: IViewHolderClicks?,
            context: Context
        ) {
            this.plan = plan
            this.context = context
            this.listener = listener

            descriptionTextView.text = plan.name

            descriptionTextView.setOnClickListener(View.OnClickListener {
                listener?.onClick(plan)
            })

        }


        override fun onClick(view: View) {
            listener?.onClick(plan)
        }

        interface IViewHolderClicks {
            fun onClick(pack: Plan?)
        }

//        init {
//            itemView.setOnClickListener(this)
//        }
    }


}