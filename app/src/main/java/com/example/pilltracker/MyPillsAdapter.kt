package com.example.pilltracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyPillsAdapter(
    private var myPills: List<MyPills>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<MyPillsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameOfMedicine: TextView = view.findViewById(R.id.tv_medicine_name)
        val dose: TextView = view.findViewById(R.id.tv_dose)
        val timeToTakeMed: TextView = view.findViewById(R.id.tv_time_to_take_med)
        val remainingMedicine: TextView = view.findViewById(R.id.tv_remaining_medicine)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mypill, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val medicine = myPills[position]
        holder.nameOfMedicine.text = medicine.nameOfMedicine
        holder.dose.text = medicine.dose
        holder.timeToTakeMed.text = medicine.timeToTakeMed
        holder.remainingMedicine.text = medicine.remainingMedicine

        // Set up the click listener
        holder.itemView.setOnClickListener {
            listener.onItemClick(medicine)
        }
    }

    override fun getItemCount(): Int {
        return myPills.size
    }

    // Update the list of medicines and notify the adapter
    fun updateMyPills(newMedicines: List<MyPills>) {
        myPills = newMedicines
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(medicine: MyPills)
    }
}
