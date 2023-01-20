package com.blkpos.chatsmspro.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blkpos.chatsmspro.R
import com.blkpos.chatsmspro.model.LedgerEntry
import java.text.SimpleDateFormat
import java.util.*

class LedgerEntryAdapter(
    private val entries: ArrayList<LedgerEntry>,
    val context: Context
) :
    RecyclerView.Adapter<LedgerEntryAdapter.ViewHolder?>() {


    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        viewHolder.bind(entries[i], context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cell_stat, parent, false))


    }

    override fun getItemCount(): Int {
        return entries.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var entry: LedgerEntry? = null
        private var context: Context? = null
        private var dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private var availableTextView: TextView = itemView.findViewById(R.id.availableTextView)
        fun bind(
            entry: LedgerEntry,
            context: Context
        ) {
            this.entry = entry
            this.context = context


            if(entry.stat != null) {
                val cal: Calendar = Calendar.getInstance()
                cal.set(entry.stat.year, entry.stat.month - 1, 1)
                dateTextView.text = "${
                    cal.getDisplayName(
                        Calendar.MONTH,
                        Calendar.LONG,
                        Locale("es", "AR")
                    )
                } - ${entry.stat.year}"
                availableTextView.text = "${entry.stat.sentCount} SMS // $${entry.stat.amountEarned}"
            } else if(entry.withdrawRequest != null) {
                if(entry.withdrawRequest.fulfilledAt == null) {
                    dateTextView.text = context.getString(R.string.home_withdraw_request_pending)
                } else {
                    val df = SimpleDateFormat("dd/MM/yyyy")
                    dateTextView.text = context.getString(R.string.home_withdraw_request_fulfilled, df.format(entry.withdrawRequest.fulfilledAt))
                }

                availableTextView.text = "$${entry.withdrawRequest.amount}"
            }


        }



//        init {
//            itemView.setOnClickListener(this)
//        }
    }


}