package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.controllers.fragments.AddPropertyFragment
import com.openclassrooms.realestatemanager.utils.Utils
import org.junit.Assert.*
import org.junit.Test

class AddPropertyFragmentTest {

    @Test
    fun propertyInfoEmptyString_isCorrect() {
        AddPropertyFragment.newInstance()
    }
}