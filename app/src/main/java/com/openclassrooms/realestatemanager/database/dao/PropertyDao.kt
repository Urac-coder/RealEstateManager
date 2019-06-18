package com.openclassrooms.realestatemanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.openclassrooms.realestatemanager.models.Property

@Dao
public interface PropertyDao {

    @Query("SELECT * FROM Property WHERE id = :propertyId")
    fun getProperty(propertyId: Long): LiveData<Property>

    @Query("SELECT * FROM Property")
    fun getAllProperty(): LiveData<List<Property>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProperty(property: Property)

    @Update
    fun updateProperty(property: Property)

    @Query("DELETE FROM Property WHERE id = :propertyId")
    fun deleteItem(propertyId: Long)
}