package com.openclassrooms.realestatemanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.models.Property

@Dao
public interface PropertyDao {

    @Query("SELECT * FROM Property WHERE id = :propertyId")
    fun getProperty(propertyId: Long): LiveData<Property>

    /*@RawQuery(observedEntities = Property.class)
    fun searchProperty(query: String): LiveData<List<Property>>*/

    @Query("SELECT * FROM Property")
    fun getAllProperty(): LiveData<List<Property>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProperty(property: Property): Long

    @Update
    fun updateProperty(property: Property)

    @Query("DELETE FROM Property WHERE id = :propertyId")
    fun deleteItem(propertyId: Long)
}