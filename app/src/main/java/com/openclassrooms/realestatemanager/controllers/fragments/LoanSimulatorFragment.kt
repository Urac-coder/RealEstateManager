package com.openclassrooms.realestatemanager.controllers.fragments

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.Picture
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.utils.toast
import com.openclassrooms.realestatemanager.view.PropertyViewModel
import com.openclassrooms.realestatemanager.view.adapter.AddPropertyPictureAdapter
import kotlinx.android.synthetic.main.fragment_loan_simulator.*
import kotlinx.android.synthetic.main.fragment_loan_simulator_display_result.*
import kotlinx.android.synthetic.main.fragment_loan_simulator_display_input.*
import kotlinx.android.synthetic.main.fragment_main_item.view.*
import java.text.DecimalFormat

class LoanSimulatorFragment : Fragment() {

    private var simulationDone: Boolean = false
    val decimalFormat = DecimalFormat("#,###,###")


    companion object {
        fun newInstance(): LoanSimulatorFragment {
            return LoanSimulatorFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_loan_simulator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment_loan_simulator_seekbar_contribution.isEnabled = false
        fragment_loan_simulator_result.visibility = View.GONE
        fragment_loan_simulator_btn.text = "Calculer"

        fragment_loan_simulator_editText_price.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (!fragment_loan_simulator_editText_price.text.isEmpty() && fragment_loan_simulator_editText_price.text.toString().toInt() > 1000){
                    fragment_loan_simulator_seekbar_contribution.isEnabled = true
                    initSeekBar(fragment_loan_simulator_editText_price.text.toString().toInt())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        fragment_loan_simulator_seekbar_contribution.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {
                displayProgress(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something
            }
        })

        fragment_loan_simulator_btn.setOnClickListener {
            if (!simulationDone){
                if (!fragment_loan_simulator_editText_price.text.isEmpty() && !fragment_loan_simulator_editText_rate.text.isEmpty() &&
                        !fragment_loan_simulator_editText_duration.text.isEmpty()){

                    manageView("doSimulation")
                    calculationAndDisplayResult()
                    simulationDone = true
            } else {
                context!!.toast("Vous devez selectionner tous les champs")
            }

        } else{
                manageView("newSimulation")
                simulationDone = false
            }

        }
    }

    // ---------------------
    // UTILS
    // ---------------------

    private fun calculationLoan(): Int{
        var price: Int = fragment_loan_simulator_editText_price.text.toString().toInt()
        var contribution: Int = fragment_loan_simulator_contribution_value.text.toString().toInt()

        return  price - contribution
    }

    private fun calculationMonthlyPayments(loan: Int): Int{
        var price: Int = loan
        var rate: Float = fragment_loan_simulator_editText_rate.text.toString().toFloat()
        var duration: Int = fragment_loan_simulator_editText_duration.text.toString().toInt()

        return (price * rate/(duration * 12)).toInt()
    }

    private fun calculationInterest(loan: Int): Int{
        var rate: Float = fragment_loan_simulator_editText_rate.text.toString().toFloat()
        var price: Int = loan

        return ((rate - 1) * price).toInt()
    }

    private fun calculationAndDisplayResult(){
        fragment_loan_simulator_loanValue.text = decimalFormat.format(calculationLoan()).toString()
        fragment_loan_simulator_monthlyFees.text = decimalFormat.format(calculationMonthlyPayments(calculationLoan())).toString()
        fragment_loan_simulator_interest.text = decimalFormat.format(calculationInterest(calculationLoan())).toString()
    }

    private fun initSeekBar(value: Int){
            var max: Int = value/1000
            fragment_loan_simulator_seekbar_contribution.max = max
    }

    private fun displayProgress(value: Int){
        var displayProgress: Int = value * 1000
        fragment_loan_simulator_contribution_value.text = displayProgress.toString()
    }

    private fun manageView(action: String){
        when(action){
            "doSimulation" -> {
                    fragment_loan_simulator_btn.visibility = View.GONE
                    fragment_loan_simulator_result.visibility = View.VISIBLE
                    fragment_loan_simulator_btn.visibility = View.VISIBLE
                    fragment_loan_simulator_btn.text = "Nouvelle simulation"
            }

            "newSimulation" -> {
                fragment_loan_simulator_result.visibility = View.GONE
                fragment_loan_simulator_btn.text = "Calculer"
                fragment_loan_simulator_editText_price.text.clear()
                fragment_loan_simulator_editText_rate.text.clear()
                fragment_loan_simulator_editText_duration.text.clear()
                fragment_loan_simulator_seekbar_contribution.progress = 0
                fragment_loan_simulator_seekbar_contribution.isEnabled = false
            }
        }
    }
}