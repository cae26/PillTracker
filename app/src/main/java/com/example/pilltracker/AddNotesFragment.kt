package com.example.pilltracker

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class AddNotesFragment : Fragment() {

    private lateinit var addNotes: EditText
    private lateinit var submitBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Submit button
        val submitBtn = view.findViewById<Button>(R.id.submitBtn)
        // declarations for edit texts
        addNotes= view.findViewById(R.id.addNotes)

        submitBtn.setOnClickListener {
           //TODO chagee the data base

        }


}

    companion object {
        fun newInstance(log: Logs): Fragment {
            val fragment = AddNotesFragment()
            return fragment
        }
    }


}
