package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.models.Property
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DisplayPropertyFragmentTest {

    // ---------------------
    // FUNCTION TO TEST
    // ---------------------

    private fun getUrlMap(property: Property): String{
        var baseUrl: String = "https://maps.googleapis.com/maps/api/staticmap?cente="
        var formattedAddress: String = property.address.replace(" ", "+")
        formattedAddress = formattedAddress.replaceFirst("+", "")
        return baseUrl + formattedAddress + "," + property.city + "&size=500x300" + "&markers=" + formattedAddress + "," + property.city + "&key=" + BuildConfig.ApiKey
    }

    // ---------------------
    // TEST
    // ---------------------

    @Test
    fun getUrlMap(){
        var propertyTest = Property(0, "type", 0, 0, 0, 0, "description", "picture", "address",
                                "city", 0, "pointOfInterest", false, "entryDate", "saleDate",
                        "realEstateAgent", 0)

        var result: String = getUrlMap(propertyTest)

        assertEquals("https://maps.googleapis.com/maps/api/staticmap?cente=address,city&size=500x300&markers=address,city&key=AIzaSyDaQQmTUPEcCwfBwxHaASYD1iHPKhUhxNk", result)
    }
}