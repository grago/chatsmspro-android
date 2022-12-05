package com.blkpos.chatsmspro.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blkpos.chatsmspro.R
import com.blkpos.chatsmspro.model.Carrier
import com.blkpos.chatsmspro.model.Stat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StatAdapter(
    private val stats: ArrayList<Stat>,
    val context: Context
) :
    RecyclerView.Adapter<StatAdapter.ViewHolder?>() {


    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        viewHolder.bind(stats[i], context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cell_stat, parent, false))


    }

    override fun getItemCount(): Int {
        return stats.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var stat: Stat? = null
        private var context: Context? = null
        private var dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private var availableTextView: TextView = itemView.findViewById(R.id.availableTextView)
        fun bind(
            stat: Stat,
            context: Context
        ) {
            this.stat = stat
            this.context = context

            val cal: Calendar = Calendar.getInstance()
            cal.set(stat.year, stat.month-1, 1)
            dateTextView.text = "${cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("es", "AR"))} - ${stat.year}"
            availableTextView.text = "${stat.sentCount} SMS // $${stat.amountEarned}"

        }



//        init {
//            itemView.setOnClickListener(this)
//        }
    }


}