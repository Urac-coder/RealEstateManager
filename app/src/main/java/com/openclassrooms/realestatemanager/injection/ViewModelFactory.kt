package com.openclassrooms.realestatemanager.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.database.repositories.PropertyDataRepository
import com.openclassrooms.realestatemanager.view.PropertyViewModel
import java.util.concurrent.Executor


class ViewModelFactory(private val propertyDataSource: PropertyDataRepository, private val executor: Executor) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PropertyViewModel::class.java)) {
            return PropertyViewModel(propertyDataSource, executor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}