package com.openclassrooms.realestatemanager.database.repositories

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.database.dao.PropertyDao
import com.openclassrooms.realestatemanager.models.Property


class PropertyDataRepository(private val propertyDao: PropertyDao) {

    // --- GET ---

    fun getProperty(propertyId: Long): LiveData<Property> {
        return this.propertyDao.getProperty(propertyId)
    }

    fun getAllProperty(): LiveData<List<Property>> {
        return this.propertyDao.getAllProperty()
    }

    // --- INSERT ---

    fun insertProperty(property: Property): Long {
        return propertyDao.insertProperty(property)
    }

    // --- DELETE ---
    fun deleteProperty(propertyId: Long) {
        propertyDao.deleteItem(propertyId)
    }

    // --- UPDATE ---
    fun updateProperty(property: Property) {
        propertyDao.updateProperty(property)
    }

}