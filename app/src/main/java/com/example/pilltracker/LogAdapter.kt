package com.example.pilltracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LogAdapter(
    private var logs: List<Logs>,
    private val listener: LogFragment
) : RecyclerView.Adapter<LogAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox =  view.findViewById(R.id.checkBox)
        val medicineName: TextView = view.findViewById(R.id.medicineName)
        val status: TextView = view.findViewById(R.id.status)
        val dateTaken: TextView = view.findViewById(R.id.dateTaken)
        val additionalNotes: TextView = view.findViewById(R.id.additionalNotes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val log = logs[position]
        holder.medicineName.text = log.medicineName
        holder.status.text = log.status
        holder.dateTaken.text = log.dateTaken
        holder.additionalNotes.text = log.additionalNotes


        holder.itemView.setOnClickListener {
            // Handle item click/tick event here
            holder.checkBox.isChecked = !holder.checkBox.isChecked
        }
    }

    override fun getItemCount(): Int {
        return logs.size
    }


    fun updatelogs(logs: List<Logs>) {
        this.logs = logs
        notifyDataSetChanged()
    }


    interface OnItemClickListener {
        fun onItemClick(log: Logs)
    }
    fun selectAllItems(selected: Boolean) {
        logs.forEach { it.isSelected = selected }
        notifyDataSetChanged()
    }


}


