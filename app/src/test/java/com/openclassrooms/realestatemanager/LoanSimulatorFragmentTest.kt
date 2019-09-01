package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.models.Property
import junit.framework.Assert.assertEquals
import kotlinx.android.synthetic.main.fragment_loan_simulator_display_input.*
import kotlinx.android.synthetic.main.fragment_loan_simulator_display_result.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LoanSimulatorFragmentTest {

    //DATA
    var fragment_loan_simulator_editText_price: Int = 200000
    var fragment_loan_simulator_contribution_value: Int = 20000
    var fragment_loan_simulator_editText_rate: Float = 1.3F
    var fragment_loan_simulator_editText_duration: Int = 20

    // ---------------------
    // FUNCTION TO TEST
    // ---------------------

    private fun calculationLoan(): Int{
        var price: Int = fragment_loan_simulator_editText_price
        var contribution: Int = fragment_loan_simulator_contribution_value

        return  price - contribution
    }

    private fun calculationMonthlyPayments(loan: Int): Int{
        var price: Int = loan
        var rate: Float = fragment_loan_simulator_editText_rate
        var duration: Int = fragment_loan_simulator_editText_duration

        return (price * rate/(duration * 12)).toInt()
    }

    private fun calculationInterest(loan: Int): Int{
        var rate: Float = fragment_loan_simulator_editText_rate
        var price: Int = loan

        return ((rate - 1) * price).toInt()
    }

    private fun displayProgress(value: Int): Int{
        return  value * 1000
    }

    // ---------------------
    // TEST
    // ---------------------

    @Test
    fun calculationLoan_isCorrect(){
        var result: Int = calculationLoan()

        assertEquals(180000, result)
    }

    @Test
    fun calculationMonthlyPayments_isCorrect(){
        var result: Int = calculationMonthlyPayments(180000)

        assertEquals(974, result)
    }

    @Test
    fun calculationInterest_isCorrect(){
        var result: Int = calculationInterest(180000)

        assertEquals(53999, result)
    }

    @Test
    fun displayProgress_isCorrect(){
        var result: Int = displayProgress(20)

        assertEquals(20000, result)
    }
}