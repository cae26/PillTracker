package com.example.pilltracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class LogAdapter(
    private var logs: List<Logs>,
    private val listener: OnItemClickListener,
    private var changeSelectListener: channgeSelectListener

) : RecyclerView.Adapter<LogAdapter.ViewHolder>() {

    interface channgeSelectListener {
        fun changeSelectCheck(int: Int)
        fun changeSelectUncheck(int:Int)
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        val medicineName: TextView = view.findViewById(R.id.medicineName)
        val status: TextView = view.findViewById(R.id.status)
        val dateTaken: TextView = view.findViewById(R.id.dateTaken)
        val additionalNotes: TextView = view.findViewById(R.id.additionalNotes)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
        val clParent : ConstraintLayout = itemView.findViewById(R.id.clParent)
        private val selectedItems: MutableList<Int> = mutableListOf<Int>()

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val log = logs[position]
        val item_id = log.id
        holder.medicineName.text = log.medicineName
        holder.status.text = log.status
        holder.dateTaken.text = log.dateTaken
        holder.additionalNotes.text = log.additionalNotes
        holder.checkBox.isChecked = log.isSelected
//        holder.itemView.setOnClickListener {
//            listener.onItemClick(log)
//        }

        holder.clParent.setOnClickListener {
            if (log.isSelected) {
                holder.checkBox.isChecked = false
                log.isSelected = false
                changeSelectListener.changeSelectUncheck(log.id)
            } else {
            holder.checkBox.isChecked = true
                log.isSelected = true
            changeSelectListener.changeSelectCheck(log.id)
        }}
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


