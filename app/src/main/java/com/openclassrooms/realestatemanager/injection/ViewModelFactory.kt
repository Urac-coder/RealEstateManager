package com.openclassrooms.realestatemanager.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.database.repositories.PictureDataRepository
import com.openclassrooms.realestatemanager.database.repositories.PropertyDataRepository
import com.openclassrooms.realestatemanager.view.PropertyViewModel
import java.util.concurrent.Executor


class ViewModelFactory(private val propertyDataSource: PropertyDataRepository, private val pictureDataSource: PictureDataRepository,
                       private val executor: Executor) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PropertyViewModel::class.java)) {
            return PropertyViewModel(propertyDataSource, pictureDataSource, executor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}