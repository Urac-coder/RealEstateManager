package com.openclassrooms.realestatemanager.database.dao

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.models.Property

@Dao
interface PropertyDao {

    @Query("SELECT * FROM Property WHERE id = :propertyId")
    fun getProperty(propertyId: Long): LiveData<Property>

    @Query("SELECT * FROM Property WHERE id = :propertyId")
    fun getPropertyWhitCursor(propertyId: Long): Cursor

    @Query("SELECT * FROM Property WHERE type = :query")
    fun searchProperty(query: String): LiveData<List<Property>>

    @Query("SELECT * FROM Property")
    fun getAllProperty(): LiveData<List<Property>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProperty(property: Property): Long

    @Update
    fun updateProperty(property: Property)

    @Query("DELETE FROM Property WHERE id = :propertyId")
    fun deleteItem(propertyId: Long)
}