package com.openclassrooms.realestatemanager

import android.app.DatePickerDialog
import com.openclassrooms.realestatemanager.models.Picture
import com.openclassrooms.realestatemanager.models.Property
import junit.framework.Assert
import junit.framework.Assert.assertEquals
import kotlinx.android.synthetic.main.fragment_search_property.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.text.SimpleDateFormat
import java.util.*


@RunWith(JUnit4::class)
class SearchPropertyFragmentTest {

    //DATA
    private lateinit var propertyListSecondFilter: List<Property>
    private lateinit var propertyListThirdFilter: List<Property>

    // ---------------------
    // FUNCTION TO TEST
    // ---------------------

    private fun compareDate(date1: String, date2: String): Boolean{
        var formatter =  SimpleDateFormat("dd/MM/yyyy")

        var date1: Date = formatter.parse(date1)
        var date2: Date = formatter.parse(date2)

        val compare: Int = date1.compareTo(date2)
        return compare < 0
    }

    private fun formatedAvailable(value: String): Boolean{
        return value == "A vendre" //return true if  value == "A  vendre"
    }

    private fun filterProperty(propertyList: List<Property>): List<Property>{
        var listToReturn: List<Property>
        var propertyListFirstFilter = propertyList.filter {
            it.price in 50000..200000 && it.surface >= 50 && it.surface <= 200 && it.nbRooms >= 5 && it.bedrooms >= 2 && it.nbOfPicture >= 4 && it.statusAvailable &&
                    compareDate("25/06/2011", it.entryDate)
        }

            propertyListSecondFilter = propertyListFirstFilter.filter { it.city  == "Longfossé" }
            listToReturn = propertyListSecondFilter

            propertyListThirdFilter = listToReturn.filter { it.pointOfInterest == "ecole" }
            listToReturn = propertyListThirdFilter

        return listToReturn
    }

    // ---------------------
    // TEST
    // ---------------------

    @Test
    fun compareDate_isCorrect(){
        var result: Boolean = compareDate("12/01/1937", "25/09/1937")

        assertEquals(true, result)
    }

    @Test
    fun formatedAvailable(){
        var result1: Boolean = formatedAvailable("A vendre")
        var result2: Boolean = formatedAvailable("Vendu")

        assertEquals(true, result1)
        assertEquals(false, result2)
    }

    @Test
    fun filterProperty_isCorrect(){
        var property1 = Property(1, "type", 300000, 0, 0, 0, "description", "picture", "address",
                "city", 0, "pointOfInterest", false, "entryDate", "saleDate",
                "realEstateAgent", 0)

        var property2 = Property(2, "type", 150000, 50, 5, 2, "description", "picture", "address",
                "Longfossé", 0, "ecole", true, "25/06/2019", "saleDate",
                "realEstateAgent", 4)

        var propertyList = listOf(property1, property2)
        var propertyListTest: List<Property>

        propertyListTest = filterProperty(propertyList)

        assertEquals(1, propertyListTest.size)
        assertEquals(2, propertyListTest[0].id)
    }
}