package com.openclassrooms.realestatemanager.controllers.fragments

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.Picture
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.view.PropertyViewModel
import com.openclassrooms.realestatemanager.view.adapter.AddPropertyPictureAdapter
import kotlinx.android.synthetic.main.fragment_loan_simulator.*
import kotlinx.android.synthetic.main.fragment_loan_simulator_display_result.*

class LoanSimulatorFragment : Fragment() {

    companion object {
        fun newInstance(): LoanSimulatorFragment {
            return LoanSimulatorFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment_loan_simulator_seekbar_contribution.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {
                // Display the current progress of SeekBar
                fragment_loan_simulator_contribution_value.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something
            }
        })

        //fragment_loan_simulator_seekbar_contribution.progress = 100
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_loan_simulator, container, false)
    }
}