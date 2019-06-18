package com.openclassrooms.realestatemanager.view

import android.content.ClipData.Item
import androidx.annotation.Nullable
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.database.repositories.PropertyDataRepository
import com.openclassrooms.realestatemanager.models.Property
import java.util.concurrent.Executor


class PropertyViewModel(private val propertyDataSource: PropertyDataRepository, private val executor: Executor) : ViewModel() {

    // -------------
    // FOR PROPERTY
    // -------------

    fun getProperty(propertyId: Long): LiveData<Property> {
        return propertyDataSource.getProperty(propertyId)
    }

    fun getAllProperty(): LiveData<List<Property>> {
        return propertyDataSource.getAllProperty()
    }

    fun insertProperty(property: Property) {
        executor.execute { propertyDataSource.insertProperty(property) }
    }

    fun deleteProperty(propertId: Long) {
        executor.execute { propertyDataSource.deleteProperty(propertId) }
    }

    fun updateProperty(property: Property) {
        executor.execute { propertyDataSource.updateProperty(property) }
    }
}